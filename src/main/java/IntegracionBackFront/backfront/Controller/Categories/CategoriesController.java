package IntegracionBackFront.backfront.Controller.Categories;

import IntegracionBackFront.backfront.Exceptions.Category.ExceptionCategoryNotFound;
import IntegracionBackFront.backfront.Models.DTO.Categories.CategoryDTO;
import IntegracionBackFront.backfront.Services.Categories.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/category")
@CrossOrigin(origins = "*")
public class CategoriesController {

    //Inyectar la clase service
    @Autowired
    private CategoryService service;

    @GetMapping("/getDataCategories")
    private ResponseEntity<Page<CategoryDTO>> getData(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        //Parte 1: Se evalua cuantos registros desea por página el usuario
        //teniendo como máximo 50 registros por página
        if (size <= 0 || size > 50){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "El tamaño de la página debe estar entre 1 y 50"
            ));
            return ResponseEntity.ok(null);
        }

        //Esta parte se ejecuta si en dado caso no se ejecuta la primera
        //Parte 2: Invocando al método getAllCategories contenido en el service y guardamos los datos
        //en el objeto category
        //Si no hay datos category = null de lo contrario no será nulo
        Page<CategoryDTO> category = service.getAllCategories(page, size);
        if (category == null){
            ResponseEntity.badRequest().body(Map.of(
                    "status", "No hay categorias registradas"
            ));
        }
        return ResponseEntity.ok(category);
    }

    @PostMapping("/InsertCategory")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO created = service.insert(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/updatedCategory/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id,
                                                      @Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            CategoryDTO updated = service.update(id, categoryDTO);
            return ResponseEntity.ok(updated);
        } catch (ExceptionCategoryNotFound e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/DeleteCategory/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        boolean deleted = service.delete(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
