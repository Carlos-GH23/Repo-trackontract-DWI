package com.example.integradora_trackontract.modules.User.control;

import com.example.integradora_trackontract.auth.service.AuthService;
import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Clients.model.Clients;
import com.example.integradora_trackontract.modules.Contracts.control.ContractsService;
import com.example.integradora_trackontract.modules.Contracts.model.Contracts;
import com.example.integradora_trackontract.modules.Contracts.model.ContractsDTO; /*Me marca error en esta importacion*/
import com.example.integradora_trackontract.modules.Roles.model.Roles;
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

    @Autowired
    public UserService(UserRepository userRepository,PasswordEncoder passwordEncoder,AuthService authService ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
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
            return new ResponseEntity<>(new Message("No hay usuarios registrados", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        logger.info("Listado de usuarios obtenido correctamente");

        return new ResponseEntity<>(new Message(users, "Listado de usuarios", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Usuarios
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(UserDTO dto) {
        Optional<User> existingUser = userRepository.findByName(dto.getName());
        if (existingUser.isPresent()) {
            return new ResponseEntity<>(new Message("El usuario ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
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

        Roles role = new Roles();
        role.setId(2L);
        User user = new User(dto.getName(), dto.getLast_name(), dto.getEmail(), dto.getPhoneNumber(), hashedPassword, true, LocalDateTime.now(), LocalDateTime.now(), 0, null, null, null, null, role);
        user = userRepository.saveAndFlush(user);
        if (user == null) {
            return new ResponseEntity<>(new Message("El usuario no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
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
    public ResponseEntity<Message> findAllByStatusIsTrue () {
        List<User> users = userRepository.findAllByStatusIsTrue();
        if (users.isEmpty()) {
            return new ResponseEntity<>(new Message("No hay usuarios activos", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de usuarios activos realizada correctamente");
        return new ResponseEntity<>(new Message(users, "Usuarios activos encontradas", TypesResponse.SUCCESS), HttpStatus.OK);
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
}
