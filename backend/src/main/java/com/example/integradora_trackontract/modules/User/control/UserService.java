package com.example.integradora_trackontract.modules.User.control;

import com.example.integradora_trackontract.auth.service.AuthService;
import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Contracts.control.ContractsService;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsDTO; /*Me marca error en esta importacion*/
import com.example.integradora_trackontract.modules.Roles.model.Roles;
import com.example.integradora_trackontract.modules.Roles.model.RolesRepository;
import com.example.integradora_trackontract.modules.User.model.User;
import com.example.integradora_trackontract.modules.User.model.UserDTO;
import com.example.integradora_trackontract.modules.User.model.UserProfileDTO;
import com.example.integradora_trackontract.modules.User.model.UserRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final RolesRepository rolesRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService, RolesRepository rolesRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.rolesRepository = rolesRepository;
    }

    //Busqueda de usuarios inactivos
    @Transactional(readOnly = true)
    public List<User> findAllByStatusIsFalse(Boolean status) {
        logger.info("Buscando usuarios con estado inactivo");
        return userRepository.findAllByStatusIsFalse();
    }

    //Busqueda de usuarios
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<User> users = userRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message(users, "No hay usuarios registrados", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Listado de usuarios obtenido correctamente");

        return new ResponseEntity<>(new Message(users, "Listado de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Usuarios
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(UserDTO dto) {
        logger.info("Iniciando proceso de creación de usuario - Email: {}", dto.getEmail());
        logger.info("Datos recibidos - Nombre: {}, Apellido: {}, Email: {}, Teléfono: {}, Status: {}", 
            dto.getName(), dto.getLast_name(), dto.getEmail(), dto.getPhoneNumber(), dto.getStatus());
        
        Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
        if (existingUser.isPresent()) {
            logger.warn("Intento de crear usuario con email ya existente: {}", dto.getEmail());
            return new ResponseEntity<>(new Message("El email ya está registrado", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del usuario excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getLast_name() == null || dto.getLast_name().isEmpty()) {
            return new ResponseEntity<>(new Message("El apellido del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getLast_name().length() > 50) {
            return new ResponseEntity<>(new Message("El apellido del usuario excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            return new ResponseEntity<>(new Message("El correo electrónico del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El correo electrónico del usuario excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty()) {
            return new ResponseEntity<>(new Message("El número de teléfono del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber().length() > 15) {
            return new ResponseEntity<>(new Message("El número de teléfono del usuario excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            return new ResponseEntity<>(new Message("La contraseña del usuario no puede ser nula o vacía", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPassword().length() > 255) {
            return new ResponseEntity<>(new Message("La contraseña del usuario excede los 255 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        logger.info("Contraseña encriptada generada correctamente");

        // Buscar el rol ABOGADO
        logger.info("Buscando rol ABOGADO en la base de datos...");
        Optional<Roles> abogadoRole = rolesRepository.findByName("ABOGADO");
        if (!abogadoRole.isPresent()) {
            logger.error("Rol ABOGADO no encontrado en la base de datos");
            return new ResponseEntity<>(new Message("Error interno: Rol ABOGADO no encontrado", TypesResponse.ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.info("Rol ABOGADO encontrado - ID: {}, Nombre: {}", abogadoRole.get().getId(), abogadoRole.get().getName());

        logger.info("Creando objeto User en memoria...");
        User user = new User();
        user.setName(dto.getName());
        user.setLastName(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setPassword(hashedPassword);
        user.setStatus(true);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        user.setLogin_attempts(0);
        user.setRol_id(abogadoRole.get());
        
        logger.info("Usuario creado en memoria - Email: {}, Nombre: {}, Apellido: {}, Teléfono: {}, Rol: {}, Status: {}", 
            user.getEmail(), user.getName(), user.getLastName(), user.getPhoneNumber(), 
            user.getRol_id().getName(), user.isStatus());
        
        logger.info("Guardando usuario en la base de datos...");
        user = userRepository.saveAndFlush(user);
        if (user == null) {
            logger.error("Error: Usuario no se registró - resultado null");
            return new ResponseEntity<>(new Message("El usuario no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Usuario guardado exitosamente en BD - ID: {}, Email: {}, Nombre: {}, Rol: {}", 
            user.getId(), user.getEmail(), user.getName(), user.getRol_id().getName());
        
        // Verificar que se guardó correctamente
        logger.info("Verificando que el usuario se guardó correctamente...");
        Optional<User> verifyUser = userRepository.findByEmail(dto.getEmail());
        if (verifyUser.isPresent()) {
            User verifiedUser = verifyUser.get();
            logger.info("Usuario verificado exitosamente en BD - ID: {}, Email: {}, Rol: {}, Status: {}", 
                verifiedUser.getId(), verifiedUser.getEmail(), 
                verifiedUser.getRol_id().getName(), verifiedUser.isStatus());
        } else {
            logger.error("ERROR: Usuario no se pudo verificar después de guardar - Email: {}", dto.getEmail());
        }
        
        logger.info("El registro ha sido realizado correctamente");
        return new ResponseEntity<>(new Message(user, "El usuario se registró correctamente", TypesResponse.SUCCESS), HttpStatus.CREATED);
    }

    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(UserDTO dto) {
        Optional<User> userOptional = userRepository.findById(dto.getId());
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getName().length() > 50) {
            return new ResponseEntity<>(new Message("El nombre del usuario excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getLast_name() == null || dto.getLast_name().isEmpty()) {
            return new ResponseEntity<>(new Message("El apellido del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getLast_name().length() > 50) {
            return new ResponseEntity<>(new Message("El apellido del usuario excede los 50 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            return new ResponseEntity<>(new Message("El correo electrónico del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail().length() > 100) {
            return new ResponseEntity<>(new Message("El correo electrónico del usuario excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty()) {
            return new ResponseEntity<>(new Message("El número de teléfono del usuario no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber().length() > 15) {
            return new ResponseEntity<>(new Message("El número de teléfono del usuario excede los 15 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        if (dto.getStatus() == null) {
            return new ResponseEntity<>(new Message("El estado del usuario no puede ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            String hashedPassword = passwordEncoder.encode(dto.getPassword());
            user.setPassword(hashedPassword);
        }

        user.setName(dto.getName());
        user.setLastName(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setStatus(dto.getStatus());
        user.setUpdated_at(LocalDateTime.now());

        user = userRepository.saveAndFlush(user);

        if (user == null) {
            return new ResponseEntity<>(new Message("El usuario no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }

        logger.info("Usuario actualizado correctamente");
        return new ResponseEntity<>(new Message(user, "Usuario actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Desactivar/Activar Usuarios
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus (UserDTO dto){
        Optional<User> userOptional = userRepository.findById(dto.getId());
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
        user.setStatus(!user.isStatus());
        user = userRepository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("El status del usuario no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Usuario actualizado correctamente");
        return new ResponseEntity<>(new Message(user, "El status del usuario fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Eliminar Usuarios
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> delete (Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();
//        if (user.isStatus()) {
//            return new ResponseEntity<>(new Message("No se puede eliminar un usuario activo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
//        }
        userRepository.delete(user);
        logger.info("Usuario eliminado correctamente");
        return new ResponseEntity<>(new Message("Usuario eliminado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Busqueda de Usuario por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById (Long id){
        Optional<User> userOptional = userRepository.findById(id);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de usuario por ID realizada correctamente");
        return new ResponseEntity<>(new Message(userOptional.get(), "Usuario encontrado", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Busqueda de usuarios activos
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue() {
        List<User> users = userRepository.findAllByStatusIsTrue();
        logger.info("Buscando usuarios con estado activo");
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message(users, "No hay usuarios activos", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Busqueda de usuarios activos realizada correctamente");
        return new ResponseEntity<>(new Message(users, "Usuarios activos encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Buscar usuarios por rol específico (ej: ABOGADO)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByRole(String roleName) {
        List<User> users = userRepository.findAllByRoleName(roleName);
        logger.info("Buscando usuarios con rol: {}", roleName);
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message(users, "No hay usuarios con rol " + roleName, TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Usuarios con rol {} encontrados correctamente", roleName);
        return new ResponseEntity<>(new Message(users, "Usuarios con rol " + roleName + " encontrados", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    public UserProfileDTO getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
        return new UserProfileDTO(
                user.getId(),
                user.getName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.isStatus(),
                user.getRol_id().getName()
        );
    }

    //Scheduled para revisar cuantos usuarios estan activas e inactivas
    @Scheduled(cron = "0 0 0 * * ?") // Cada día a medianoche
    public void checkUserStatus() {
        List<User> activeUsers = userRepository.findAllByStatusIsTrue();
        List<User> inactiveUsers = userRepository.findAllByStatusIsFalse();
        logger.info("Usuarios activos: {}, Usuarios inactivos: {}", activeUsers.size(), inactiveUsers.size());
    }

    @Transactional
    public ResponseEntity<Message> updateMyProfile(String email, UserDTO dto) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Usuario no encontrado", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }

        User user = userOptional.get();

        // Validaciones básicas
        if (dto.getName() == null || dto.getName().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getLast_name() == null || dto.getLast_name().isEmpty()) {
            return new ResponseEntity<>(new Message("El apellido no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            return new ResponseEntity<>(new Message("El correo no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getPhoneNumber() == null || dto.getPhoneNumber().isEmpty()) {
            return new ResponseEntity<>(new Message("El teléfono no puede ser vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }

        // Actualizar campos
        user.setName(dto.getName());
        user.setLastName(dto.getLast_name());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        //No actualizar el status
        user.setStatus(dto.getStatus());
        user.setUpdated_at(LocalDateTime.now());

        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(new Message("Perfil actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Cambio de contraseña
    public ResponseEntity<Message> changeMyPassword(String email,
                                                    com.example.integradora_trackontract.modules.User.model.ChangePasswordRequest req) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        // Validaciones
        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La contraseña actual no es correcta", TypesResponse.WARNING),
                    HttpStatus.UNAUTHORIZED);
        }
        if (!req.getNewPassword().equals(req.getConfirmNewPassword())) {
            return new ResponseEntity<>(new Message("La confirmación no coincide", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST);
        }
        if (req.getNewPassword().length() < 8) {
            return new ResponseEntity<>(new Message("La nueva contraseña debe tener al menos 8 caracteres", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST);
        }
        if (passwordEncoder.matches(req.getNewPassword(), user.getPassword())) {
            return new ResponseEntity<>(new Message("La nueva contraseña no puede ser igual a la actual", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST);
        }

        // Actualizar
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        user.setUpdated_at(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        // Seguridad extra: invalidar tokens activos
        authService.revokeAllUserTokens(user);

        return new ResponseEntity<>(new Message("Contraseña actualizada. Vuelve a iniciar sesión.", TypesResponse.SUCCESS),
                HttpStatus.OK);
    }

    // Actualizar contraseña sin verificar la actual (para admin o reset)
    public ResponseEntity<Message> updatePasswordWithoutCurrent(String email, String newPassword, String confirmPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        // Validaciones básicas
        if (!newPassword.equals(confirmPassword)) {
            return new ResponseEntity<>(new Message("La confirmación no coincide", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST);
        }
        if (newPassword.length() < 8) {
            return new ResponseEntity<>(new Message("La nueva contraseña debe tener al menos 8 caracteres", TypesResponse.WARNING),
                    HttpStatus.BAD_REQUEST);
        }

        // Actualizar
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdated_at(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        return new ResponseEntity<>(new Message("Contraseña actualizada exitosamente", TypesResponse.SUCCESS),
                HttpStatus.OK);
    }
}
