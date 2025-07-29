package com.example.integradora_trackontract.modules.Categories.control;
import com.example.integradora_trackontract.modules.Categories.model.Categories;
import com.example.integradora_trackontract.modules.Categories.model.CategoriesDTO;
import com.example.integradora_trackontract.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesController {

    private final CategoriesService categoriesService;

    @Autowired
    public CategoriesController(CategoriesService categoriesService) {
        this.categoriesService = categoriesService;
    }

    @GetMapping("/all")
    public ResponseEntity<Message> getAllCategories() {
        return categoriesService.findAll();
    }

    @PostMapping("/save")
    public ResponseEntity<Message> saveCategories(@Validated(CategoriesDTO.Register.class) @RequestBody CategoriesDTO dto) {
        return categoriesService.save(dto);
    }

    @PutMapping("/update")
    public ResponseEntity<Message> updateCategories(@Validated(CategoriesDTO.Modify.class) @RequestBody CategoriesDTO dto) {
        return categoriesService.update(dto);
    }

    @PutMapping("/change-status")
    public ResponseEntity<Message> changeStatus(@Validated(CategoriesDTO.ChangeStatus.class) @RequestBody CategoriesDTO dto) {
        return categoriesService.changeStatus(dto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Message> deleteById(@PathVariable Long id) {
        return categoriesService.delete(id);
    }

    @GetMapping("/all/status/false")
    public List<Categories> getAllCategoriesByInactiveStatus() {
        return categoriesService.findAllByStatusIsFalse(true);
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Message> getCategoriesByName(@PathVariable String name) {
        return categoriesService.findByName(name);
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<Message> getCategoriesById(@PathVariable Long id) {
        return categoriesService.findById(id);
    }

    @GetMapping("/all/status/true")
    public ResponseEntity<Message> getAllCategoriesByActiveStatus() {
        return categoriesService.findAllByStatusIsTrue();
    }

}
