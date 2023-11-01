package staj.ordermanagementsystemapi.business.concretes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;

import staj.ordermanagementsystemapi.business.abstracts.CategoryService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CategoryRepository;
import staj.ordermanagementsystemapi.entities.concretes.Category;
import staj.ordermanagementsystemapi.entities.dto.CategoryDto;

class CategoryServiceImplTest {

    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        modelMapper = new ModelMapper();
        categoryService = new CategoryServiceImpl(categoryRepository, modelMapper);
    }

    @Test
    public void CategoryService_GetAll_ReturnsAllCategories() {
        // Arrange
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "Category 1", "Detail 1",new Date()));
        categories.add(new Category(2, "Category 2", "Detail 2", new Date()));
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<CategoryDto> categoryDTOList = categoryService.getAllCategories();

        // Assert
        assertNotNull(categoryDTOList);
        assertEquals(categories.size(), categoryDTOList.size());
    }

    @Test
    public void CategoryService_GetById_ValidCategoryReturnsCategory() {
        // Arrange
        int categoryId = 1;
        Category category = new Category(1, "Test Category", "Test Detail", new Date());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // Act
        CategoryDto categoryDto = categoryService.getCategoryById(categoryId);

        // Assert
        assertNotNull(categoryDto);
        assertEquals(category.getId(), categoryDto.getId());
        assertEquals(category.getName(), categoryDto.getName());
    }

    @Test
    public void CategoryService_GetById_CategoryNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(categoryId));
    }

    @Test
    public void CategoryService_SaveCategory_ValidCategoryDTO_ReturnsSavedCategoryDTO() {
        // Arrange
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Test Category");
        Category category = modelMapper.map(categoryDto, Category.class);
        when(categoryRepository.save(category)).thenReturn(category);

        // Act
        CategoryDto savedCategoryDto = categoryService.saveCategory(categoryDto);

        // Assert
        assertNotNull(savedCategoryDto);
        assertEquals(category.getId(), savedCategoryDto.getId());
        assertEquals(category.getName(), savedCategoryDto.getName());
    }

    @Test
    public void CategoryService_SaveCategory_DuplicateCategoryNameThrowsIllegalArgumentException() {
        // Arrange
        CategoryDto categoryDTO = new CategoryDto();
        categoryDTO.setName("Test Category");

        when(categoryRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.saveCategory(categoryDTO));
    }

    @Test
    public void CategoryService_UpdateCategory_ValidCategoryIdAndDTO_ReturnsUpdatedCategoryDTO() {
        // Arrange
        int categoryId = 1;
        String updatedName = "Updated Name";
        String updatedDetails = "Updated Details";
        Date updatedDate = new Date();
        CategoryDto categoryDto = new CategoryDto(categoryId, updatedName, updatedDetails, updatedDate);
        Category existingCategory = new Category(categoryId, "Test Category", "Test Details", new Date());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any())).thenReturn(existingCategory);

        // Act
        CategoryDto updatedCategoryDTO = categoryService.updateCategory(categoryId, updatedName, updatedDetails, updatedDate);

        // Assert
        assertNotNull(updatedCategoryDTO);
        assertEquals(categoryId, updatedCategoryDTO.getId());
        assertEquals(categoryDto.getName(), updatedCategoryDTO.getName());
    }

    @Test
    public void CategoryService_UpdateCategory_CategoryNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int categoryId = 1;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryId, "a", "a", new Date()));
    }

    @Test
    public void CategoryService_UpdateCategory_DuplicateCategoryNameThrowsIllegalArgumentException() {
        // Arrange
        int categoryId = 1;
        String updatedName = "Updated Name";
        String updatedDetails = "Updated Details";
        Date updatedDate = new Date();
        CategoryDto categoryDto = new CategoryDto(categoryId, updatedName, updatedDetails, updatedDate);
        Category existingCategory = new Category(categoryId, "Test Category", "Test Details", new Date());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.updateCategory(categoryId, updatedName, updatedDetails, updatedDate));
    }

    @Test
    public void CategoryService_Delete_ValidCategoryId_DeletesCategory() {
        // Arrange
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "Test Category", "Test Details", new Date());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        // Act
        assertDoesNotThrow(() -> categoryService.deleteCategory(categoryId));

        // Assert
        verify(categoryRepository, times(1)).deleteById(categoryId);
    }

    @Test
    public void CategoryService_Delete_CategoryNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int categoryId = 1;
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
    }

    @Test
    public void CategoryService_Delete_AssociatedCategoryThrowsIllegalArgumentException() {
        // Arrange
        int categoryId = 1;
        Category existingCategory = new Category(categoryId, "Test Category", "Test Details", new Date());
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existingCategory));
        doThrow(DataIntegrityViolationException.class).when(categoryRepository).deleteById(categoryId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> categoryService.deleteCategory(categoryId));
    }
}
