package com.example.integradora_trackontract.modules.Clients.control;

import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Clients.model.ClientsDTO;
import com.example.integradora_trackontract.modules.Clients.model.ClientsRepository;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.Roles.model.RolesRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class ClientsService {

    private static final Logger logger = LoggerFactory.getLogger(ClientsService.class);
    private final ClientsRepository clientsRepository;
    private final UserRepository userRepository;
    private final RolesRepository rolesRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository, UserRepository userRepository, 
                        RolesRepository rolesRepository, PasswordEncoder passwordEncoder) {
        this.clientsRepository = clientsRepository;
        this.userRepository = userRepository;
        this.rolesRepository = rolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //Busqueda de clientes inactivos
    @Transactional(readOnly = true)
    public List<Clients> findAllByStatusIsFalse(Boolean status) {
        logger.info("Buscando clientes con estado inactivo");
        return clientsRepository.findAllByStatusIsFalse();
    }

    //Busqueda de clientes
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Clients> clients = clientsRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        if(clients.isEmpty()) {
            return new ResponseEntity<>(new Message(clients, "No hay clientes registradas", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Listado de clientes obtenido correctamente");

        return new ResponseEntity<>(new Message(clients,"Listado de clientes", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(ClientsDTO dto) {
        Optional<Clients> existingClients = clientsRepository.findByName(dto.getName());
        if(existingClients.isPresent()) {
            return new ResponseEntity<>(new Message("El cliente ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName().length() > 50){
            return new ResponseEntity<>(new Message("El nombre del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getBusiness_name().length() > 50){
            return new ResponseEntity<>(new Message("El nombre del negocio del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getRepresentative_name().length() > 100){
            return new ResponseEntity<>(new Message("El nombre del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getRepresentative_surnames().length() >100){
            return new ResponseEntity<>(new Message("Los apellidos del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getEmail().length() >100){
            return new ResponseEntity<>(new Message("El email del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getPhone().length() > 15){
            return new ResponseEntity<>(new Message("El teléfono del cliente excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }if(dto.getStatus() == null) {
            return new ResponseEntity<>(new Message("El estado del cliente no puede ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        
        // Crear el cliente
        Clients clients = new Clients(dto.getName(), dto.getBusiness_name(), dto.getRepresentative_name(),
                dto.getRepresentative_surnames(), dto.getEmail(), dto.getPhone(), true);
        clients.setStatus(true);
        clients = clientsRepository.saveAndFlush(clients);
        
        if(clients == null) {
            return new ResponseEntity<>(new Message("El cliente no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        
        // Crear automáticamente el usuario para el cliente
        try {
            logger.info("Iniciando creación automática de usuario para cliente: {}", dto.getEmail());
            
            // Buscar el rol CLIENT
            Optional<Roles> clientRole = rolesRepository.findByName("CLIENT");
            if (!clientRole.isPresent()) {
                logger.warn("Rol CLIENT no encontrado, no se pudo crear el usuario automáticamente");
            } else {
                logger.info("Rol CLIENT encontrado: {} (ID: {})", clientRole.get().getName(), clientRole.get().getId());
                
                // Generar contraseña: nombre123
                String password = dto.getRepresentative_name() + "123";
                String encodedPassword = passwordEncoder.encode(password);
                logger.info("Contraseña generada para usuario: {} -> Encriptada: {}", password, encodedPassword);
                
                // Crear el usuario
                User user = new User();
                user.setName(dto.getRepresentative_name());
                user.setLastName(dto.getRepresentative_surnames());
                user.setEmail(dto.getEmail());
                user.setPhoneNumber(dto.getPhone());
                user.setPassword(encodedPassword);
                user.setStatus(true);
                user.setCreated_at(LocalDateTime.now());
                user.setUpdated_at(LocalDateTime.now());
                user.setLogin_attempts(0);
                user.setRol_id(clientRole.get());
                
                logger.info("Usuario creado en memoria: {} con rol: {}", user.getEmail(), user.getRol_id().getName());
                
                User savedUser = userRepository.saveAndFlush(user);
                logger.info("Usuario guardado en BD con ID: {} y email: {}", savedUser.getId(), savedUser.getEmail());
                
                // Verificar que se guardó correctamente
                Optional<User> verifyUser = userRepository.findByEmail(dto.getEmail());
                if (verifyUser.isPresent()) {
                    User verifiedUser = verifyUser.get();
                    logger.info("Usuario verificado en BD - ID: {}, Email: {}, Rol: {}, Status: {}", 
                        verifiedUser.getId(), verifiedUser.getEmail(), 
                        verifiedUser.getRol_id().getName(), verifiedUser.isStatus());
                } else {
                    logger.error("ERROR: Usuario no se pudo verificar después de guardar");
                }
                
                logger.info("Usuario creado automáticamente para el cliente: {}", dto.getEmail());
            }
        } catch (Exception e) {
            logger.error("Error al crear usuario automáticamente para el cliente: {}", e.getMessage(), e);
            // No fallar la creación del cliente por un error en la creación del usuario
        }
        
        ClientsDTO saveDTO = new ClientsDTO(
                clients.getId(),
                clients.getName(),
                clients.getBusiness_name(),
                clients.getRepresentative_name(),
                clients.getRepresentative_surnames(),
                clients.getEmail(),
                clients.getPhone(),
                clients.isStatus()
        );
        
        logger.info("El registro ha sido realizado correctamente");
        return new ResponseEntity<>(new Message(saveDTO,"El cliente se registró correctamente", TypesResponse.SUCCESS), HttpStatus.CREATED);
    }

    //Actualizar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(ClientsDTO dto) {
        Optional<Clients> clientsOptional = clientsRepository.findById(dto.getId());
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getBusiness_name().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del negocio del cliente excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_name().length() > 100) {
            return new ResponseEntity<>(new Message("El nombre del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_surnames().length() > 100) {
            return new ResponseEntity<>(new Message("Los apellidos del representante del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El email del cliente excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhone().length() > 15) {
            return new ResponseEntity<>(new Message("El teléfono del cliente excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getStatus() == null) {
            return new ResponseEntity<>(new Message("El estado del cliente no puede ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Clients clients = clientsOptional.get();
        clients.setName(dto.getName());
        clients.setBusiness_name(dto.getBusiness_name());
        clients.setRepresentative_name(dto.getRepresentative_name());
        clients.setRepresentative_surnames(dto.getRepresentative_surnames());
        clients.setEmail(dto.getEmail());
        clients.setPhone(dto.getPhone());
        clients.setStatus(dto.getStatus());
        clients = clientsRepository.saveAndFlush(clients);
        if (clients == null) {
            return new ResponseEntity<>(new Message("El cliente no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Cliente actualizado correctamente");
        return new ResponseEntity<>(new Message(clients, "Cliente actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Desactivar/Activar Clientes
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(ClientsDTO dto) {
        Optional<Clients> clientsOptional = clientsRepository.findById(dto.getId());
        if(!clientsOptional.isPresent()){
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Clients clients = clientsOptional.get();
        clients.setStatus(!clients.isStatus());
        clients = clientsRepository.saveAndFlush(clients);
        if(clients == null){
            return new ResponseEntity<>(new Message("El status del cliente no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Cliente actualizado correctamente");
        return new ResponseEntity<>(new Message(clients, "El status del cliente fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Eliminar Cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> delete(Long id) {
        Optional<Clients> clientsOptional = clientsRepository.findById(id);
        if(!clientsOptional.isPresent()){
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Clients clients = clientsOptional.get();
        if (clients.getContracts() != null && !clients.getContracts().isEmpty()) {
            return new ResponseEntity<>(new Message("El cliente no se puede eliminar porque tiene contratos asociados", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        clientsRepository.delete(clients);
        logger.info("Cliente eliminado correctamente");
        return new ResponseEntity<>(new Message("Cliente eliminado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Clientes por nombre (PENDIENTE DE REVISION)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre no debe ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Clients> clientsOptional = clientsRepository.findByName(name);
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de cliente por nombre realizada correctamente");
        return new ResponseEntity<>(new Message(clientsOptional.get(), "Cliente encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Cliente por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById(Long id) {
        Optional<Clients> clientsOptional = clientsRepository.findById(id);
        if (!clientsOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de cliente por ID realizada correctamente");
        return new ResponseEntity<>(new Message(clientsOptional.get(), "Cliente encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Busqueda de clientes activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue() {
        List<Clients> clients = clientsRepository.findAllByStatusIsTrue();
        if (clients.isEmpty()) {
            return new ResponseEntity<>(new Message(clients, "No hay clientes activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Busqueda de clientes activos realizada correctamente");
        return new ResponseEntity<>(new Message(clients, "Clientes activos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Scheduled para revisar cuantas clientes estan activas e inactivas
    @Scheduled(cron = "0 0 0 * * ?")
    public void countClientsStatus() {
        int activeCount = clientsRepository.countByStatusIsTrue();
        int inactiveCount = clientsRepository.countByStatusIsFalse();
        logger.info("Clientes activos: {}, Clientes inactivos: {}", activeCount, inactiveCount);
    }

    // Obtener perfil del cliente por email
    @Transactional(readOnly = true)
    public ResponseEntity<Message> getProfileByEmail(String email) {
        Optional<Clients> clientOptional = clientsRepository.findByEmail(email);
        if (!clientOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        
        Clients client = clientOptional.get();
        logger.info("Perfil del cliente obtenido correctamente para email: {}", email);
        return new ResponseEntity<>(new Message(client, "Perfil del cliente obtenido", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Actualizar perfil del cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updateProfile(String email, ClientsDTO dto) {
        Optional<Clients> clientOptional = clientsRepository.findByEmail(email);
        if (!clientOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }

        Clients client = clientOptional.get();

        // Validaciones básicas
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getBusiness_name() == null || dto.getBusiness_name().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del negocio no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_name() == null || dto.getRepresentative_name().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del representante no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getRepresentative_surnames() == null || dto.getRepresentative_surnames().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("Los apellidos del representante no pueden ser vacíos", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            return new ResponseEntity<>(new Message("El teléfono no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // Actualizar campos
        client.setName(dto.getName());
        client.setBusiness_name(dto.getBusiness_name());
        client.setRepresentative_name(dto.getRepresentative_name());
        client.setRepresentative_surnames(dto.getRepresentative_surnames());
        client.setPhone(dto.getPhone());
        client.setUpdated_at(LocalDateTime.now());

        client = clientsRepository.saveAndFlush(client);
        if (client == null) {
            return new ResponseEntity<>(new Message("El perfil no se pudo actualizar", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        logger.info("Perfil del cliente actualizado correctamente");
        return new ResponseEntity<>(new Message(client, "Perfil actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Actualizar contraseña del cliente
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> updatePassword(String email, String newPassword, String confirmPassword) {
        // Primero buscar el cliente por email
        Optional<Clients> clientOptional = clientsRepository.findByEmail(email);
        if (!clientOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Cliente no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }

        // Luego buscar el usuario asociado al cliente
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Validaciones básicas
        if (!newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>(new Message("La confirmación no coincide", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (newPassword.length() < 8) {
            return new ResponseEntity<>(new Message("La nueva contraseña debe tener al menos 8 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // Actualizar contraseña
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdated_at(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        logger.info("Contraseña del cliente actualizada correctamente");
        return new ResponseEntity<>(new Message("Contraseña actualizada exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

}
