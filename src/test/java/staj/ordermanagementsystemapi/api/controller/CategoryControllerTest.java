package staj.ordermanagementsystemapi.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import staj.ordermanagementsystemapi.business.abstracts.CategoryService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.CategoryDto;

public class CategoryControllerTest {

    private CategoryController categoryController;
    private CategoryService categoryService;

    @BeforeEach
    public void setUp() {
        categoryService = mock(CategoryService.class);
        categoryController = new CategoryController(categoryService);
    }

    @Test
    void getAllCategories_ReturnsAllCategoriesSuccessfully() {
        // Arrange
        List<CategoryDto> expectedCategories = new ArrayList<>();
        expectedCategories.add(new CategoryDto(1, "Category 1", "Detail 1", new Date()));
        expectedCategories.add(new CategoryDto(2, "Category 2", "Detail 2", new Date()));
        when(categoryService.getAllCategories()).thenReturn(expectedCategories);

        // Act
        ResponseEntity<List<CategoryDto>> responseEntity = categoryController.getAllCategories();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedCategories.size(), responseEntity.getBody().size());
    }

    @Test
    void getCategoryById_ValidCategoryId_ReturnsCategorySuccessfully() {
        // Arrange
        int categoryId = 1;
        CategoryDto expectedCategory = new CategoryDto(categoryId, "Test Category", "Test Details", new Date());
        when(categoryService.getCategoryById(categoryId)).thenReturn(expectedCategory);

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.getCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedCategory.getId(), responseEntity.getBody().getId());
        assertEquals(expectedCategory.getName(), responseEntity.getBody().getName());
    }

    @Test
    void getCategoryById_CategoryNotFound_ReturnsNotFound() {
        // Arrange
        int categoryId = 1;
        when(categoryService.getCategoryById(categoryId)).thenReturn(null);

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.getCategoryById(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void addCategory_ValidCategory_ReturnsCreatedCategory() {
        // Arrange
        CategoryDto newCategory = new CategoryDto(null, "New Category", "New Details", new Date());
        CategoryDto expectedSavedCategory = new CategoryDto(1, "New Category", "New Details", new Date());
        when(categoryService.saveCategory(newCategory)).thenReturn(expectedSavedCategory);

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.addCategory(newCategory);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedCategory.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedCategory.getName(), responseEntity.getBody().getName());
    }

    @Test
    void addCategory_DuplicateCategoryName_ReturnsBadRequest() {
        // Arrange
        CategoryDto newCategory = new CategoryDto(null, "Duplicate Category", "New Details", new Date());
        when(categoryService.saveCategory(newCategory)).thenThrow(new IllegalArgumentException("Category name must be unique."));

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.addCategory(newCategory);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateCategory_ValidCategory_ReturnsUpdatedCategory() {
        // Arrange
        int categoryId = 1;
        CategoryDto updatedCategory = new CategoryDto(categoryId, "Updated Category", "Updated Details", new Date());
        when(categoryService.updateCategory(categoryId, updatedCategory.getName(),
        		updatedCategory.getDetails(), updatedCategory.getTimestamp()))
                .thenReturn(updatedCategory);

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.updateCategory(categoryId, updatedCategory.getName(),
        		updatedCategory.getDetails(), updatedCategory.getTimestamp());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedCategory.getId(), responseEntity.getBody().getId());
        assertEquals(updatedCategory.getName(), responseEntity.getBody().getName());
        assertEquals(updatedCategory.getDetails(), responseEntity.getBody().getDetails());
    }

    @Test
    void updateCategory_CategoryNotFound_ReturnsNotFound() {
        // Arrange
        int categoryId = 1;
        CategoryDto updatedCategory = new CategoryDto(categoryId, "Updated Category", "Updated Details", new Date());
        when(categoryService.updateCategory(categoryId, updatedCategory.getName(), updatedCategory.getDetails(), 
        		updatedCategory.getTimestamp()))
                .thenThrow(new ResourceNotFoundException("Category", "id", categoryId));

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.updateCategory(categoryId,
        		updatedCategory.getName(), updatedCategory.getDetails(), updatedCategory.getTimestamp());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void updateCategory_DuplicateCategoryName_ReturnsBadRequest() {
        // Arrange
        int categoryId = 1;
        CategoryDto updatedCategory = new CategoryDto(categoryId, "Updated Category", "Updated Details", new Date());
        when(categoryService.updateCategory(categoryId, updatedCategory.getName(), updatedCategory.getDetails(),
        		updatedCategory.getTimestamp()))
                .thenThrow(new IllegalArgumentException("Category name must be unique."));

        // Act
        ResponseEntity<CategoryDto> responseEntity = categoryController.updateCategory(categoryId,
        		updatedCategory.getName(), updatedCategory.getDetails(), updatedCategory.getTimestamp());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void deleteCategory_ValidCategoryId_ReturnsNoContent() {
        // Arrange
        int categoryId = 1;

        // Act
        ResponseEntity<Void> responseEntity = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteCategory_CategoryNotFound_ReturnsNotFound() {
        // Arrange
        int categoryId = 1;
        doThrow(new ResourceNotFoundException("Category", "id", categoryId)).when(categoryService).deleteCategory(categoryId);

        // Act
        ResponseEntity<Void> responseEntity = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteCategory_AssociatedCategory_ReturnsBadRequest() {
        // Arrange
        int categoryId = 1;
        doThrow(new IllegalArgumentException("Category with id " + categoryId + " is associated with existing products and cannot be deleted."))
                .when(categoryService).deleteCategory(categoryId);

        // Act
        ResponseEntity<Void> responseEntity = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
}
