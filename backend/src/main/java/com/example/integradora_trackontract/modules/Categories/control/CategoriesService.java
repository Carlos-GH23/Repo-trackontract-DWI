package com.example.integradora_trackontract.modules.Categories.control;
import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesDTO;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesRepository;
import com.example.integradora_trackontract.utils.Message;
import com.example.integradora_trackontract.utils.TypesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class CategoriesService {
    private static final Logger logger = LoggerFactory.getLogger(CategoriesService.class);

    private final CategoriesRepository categoriesRepository;

    @Autowired
    public CategoriesService(CategoriesRepository categoriesRepository) {
        this.categoriesRepository= categoriesRepository;
    }

    //Busqueda de categorias inactivas
    @Transactional(readOnly = true)
    public List<Categories> findAllByStatusIsFalse() {
        logger.info("Buscando categorias con estado inactivo");
        return categoriesRepository.findAllByStatusIsFalse();
    }


    //Busqueda de categorias
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAll() {
        List<Categories> categories = categoriesRepository.findAll();
        logger.info("La búsqueda ha sido realizada correctamente");
        if(categories.isEmpty()) {
            return new ResponseEntity<>(new Message("No hay categorias registradas", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Listado de categorias obtenido correctamente");

        return new ResponseEntity<>(new Message(categories,"Listado de categorias", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Guardar Categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> save(CategoriesDTO dto) {
        Optional<Categories> existingCategory = categoriesRepository.findByName(dto.getName());
        if(existingCategory.isPresent()) {
            return new ResponseEntity<>(new Message("La categoria ya existe", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName().length() >= 100) {
            return new ResponseEntity<>(new Message("El nombre de la categoria excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getName() == null || dto.getName().isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre de la categoria no puede ser nulo o vacío", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getDescription() == null || dto.getDescription().isEmpty()) {
            return new ResponseEntity<>(new Message("La descripción de la categoria no puede ser nula o vacía", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if(dto.getDescription().length() >= 255) {
            return new ResponseEntity<>(new Message("La descripción de la categoria excede los 255 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Categories category = new Categories(dto.getName(), dto.getDescription(), true);
        category.setStatus(true);
        category = categoriesRepository.saveAndFlush(category);
        CategoriesDTO saveDTO = new CategoriesDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.isStatus()
        );
        if(category == null) {
            return new ResponseEntity<>(new Message("La categoria no se registró", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("El registro ha sido realizado correctamente");
        return new ResponseEntity<>(new Message(saveDTO,"La categoria se registró correctamente", TypesResponse.SUCCESS), HttpStatus.CREATED);


    }

    //Actualizar Categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> update(CategoriesDTO dto) {
        Optional<Categories> categoriesOptional = categoriesRepository.findById(dto.getId());
        if (!categoriesOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Categoria no encontrada", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        if (dto.getName().length() >= 100) {
            return new ResponseEntity<>(new Message("El nombre de la categoria excede los 100 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        if (dto.getDescription().length() >= 255) {
            return new ResponseEntity<>(new Message("La descripción de la categoria excede los 255 caracteres", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Categories category = categoriesOptional.get();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category = categoriesRepository.saveAndFlush(category);
        if (category == null) {
            return new ResponseEntity<>(new Message("La categoria no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Categoria actualizado correctamente");
        return new ResponseEntity<>(new Message(category, "Categoria actualizado correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Desactivar/Activar Categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> changeStatus(CategoriesDTO dto) {
        Optional<Categories> categoriesOptional = categoriesRepository.findById(dto.getId());
        if(!categoriesOptional.isPresent()){
            return new ResponseEntity<>(new Message("Categoria no encontrada", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Categories categories = categoriesOptional.get();
        categories.setStatus(!categories.isStatus());
        categories = categoriesRepository.saveAndFlush(categories);
        if(categories == null){
            return new ResponseEntity<>(new Message("El status de la categoria no se actualizó", TypesResponse.ERROR), HttpStatus.BAD_REQUEST);
        }
        logger.info("Categoria actualizada correctamente");
        return new ResponseEntity<>(new Message(categories, "El status de la categoria fue cambiado exitosamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    // Eliminar Categoria
    @Transactional(rollbackFor = {SQLException.class})
    public ResponseEntity<Message> delete(Long id) {
        Optional<Categories> categoriesOptional = categoriesRepository.findById(id);
        if(!categoriesOptional.isPresent()){
            return new ResponseEntity<>(new Message("Categoria no encontrada", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        Categories categories = categoriesOptional.get();
        categoriesRepository.delete(categories);
        logger.info("Categoria eliminada correctamente");
        return new ResponseEntity<>(new Message("Categoria eliminada correctamente", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Categoria por nombre (PENDIENTE DE REVISION)
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findByName(String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>(new Message("El nombre no debe ser nulo", TypesResponse.WARNING), HttpStatus.BAD_REQUEST);
        }
        Optional<Categories> categoriesOptional = categoriesRepository.findByName(name);
        if (!categoriesOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Categoria no encontrada", TypesResponse.WARNING), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de categoria por nombre realizada correctamente");
        return new ResponseEntity<>(new Message(categoriesOptional.get(), "Categoria encontrada", TypesResponse.SUCCESS), HttpStatus.OK);
    }


    //Busqueda de Categoria por ID
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findById(Long id) {
        Optional<Categories> categoriesOptional = categoriesRepository.findById(id);
        if (!categoriesOptional.isPresent()) {
            return new ResponseEntity<>(new Message("Categoria no encontrada", TypesResponse.ERROR), HttpStatus.NOT_FOUND);
        }
        logger.info("Busqueda de categoria por ID realizada correctamente");
        return new ResponseEntity<>(new Message(categoriesOptional.get(), "Categoria encontrada", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Busqueda de categorias activas
    @Transactional(readOnly = true)
    public ResponseEntity<Message> findAllByStatusIsTrue() {
        List<Categories> categories = categoriesRepository.findAllByStatusIsTrue();
        if (categories.isEmpty()) {
            return new ResponseEntity<>(new Message(categories, "No hay categorias activas", TypesResponse.WARNING), HttpStatus.OK);
        }
        logger.info("Busqueda de categorias activas realizada correctamente");
        return new ResponseEntity<>(new Message(categories, "Categorias activas encontradas", TypesResponse.SUCCESS), HttpStatus.OK);
    }

    //Scheduled para revisar cuantas categorias estan activas e inactivas
    @Scheduled(cron = "0 0 1 * * *")
    public void printCategoriesStatus() {
        List<Categories> activeCategories = categoriesRepository.findAllByStatusIsTrue();
        List<Categories> inactiveCategories = categoriesRepository.findAllByStatusIsFalse();
        logger.info("Categorias activas: {}", activeCategories.size());
        logger.info("Categorias inactivas: {}", inactiveCategories.size());
    }

}
