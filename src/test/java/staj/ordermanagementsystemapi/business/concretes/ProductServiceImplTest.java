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
import staj.ordermanagementsystemapi.business.abstracts.ProductService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CategoryRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ProductRepository;
import staj.ordermanagementsystemapi.entities.concretes.Category;
import staj.ordermanagementsystemapi.entities.concretes.Product;
import staj.ordermanagementsystemapi.entities.dto.CategoryDto;
import staj.ordermanagementsystemapi.entities.dto.ProductDto;

class ProductServiceImplTest {

    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private ModelMapper categoryModelMapper;

    private ProductService productService;
    private ProductRepository productRepository;
    private ModelMapper productModelMapper;

    @BeforeEach
    public void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryModelMapper = new ModelMapper();
        categoryService = new CategoryServiceImpl(categoryRepository, categoryModelMapper);

        productRepository = mock(ProductRepository.class);
        productModelMapper = new ModelMapper();
        productService = new ProductServiceImpl(productRepository, categoryRepository, productModelMapper);
    }

    // ProductService Tests
    @Test
    public void ProductService_GetAll_ReturnsAllProducts() {
        // Arrange
        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Product 1", 10.0, "thumbnail1", "Detail 1", new Category(), 100, new Date()));
        products.add(new Product(2, "Product 2", 20.0, "thumbnail2", "Detail 2", new Category(), 50, new Date()));
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<ProductDto> productDtoList = productService.getAllProducts();

        // Assert
        assertNotNull(productDtoList);
        assertEquals(products.size(), productDtoList.size());
    }

    @Test
    public void ProductService_GetById_ValidProductReturnsProduct() {
        // Arrange
        int productId = 1;
        Product product = new Product(1, "Test Product", 10.0, "thumbnail", "Test Detail", new Category(), 100, new Date());
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        ProductDto productDto = productService.getProductById(productId);

        // Assert
        assertNotNull(productDto);
        assertEquals(product.getId(), productDto.getId());
        assertEquals(product.getName(), productDto.getName());
    }

    @Test
    public void ProductService_GetById_ProductNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(productId));
    }

    @Test
    public void ProductService_SaveProduct_ValidProductDTO_ReturnsSavedProductDTO() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setPrice(10.0);
        Product product = productModelMapper.map(productDto, Product.class);
        when(productRepository.save(product)).thenReturn(product);

        // Act
        ProductDto savedProductDto = productService.saveProduct(productDto);

        // Assert
        assertNotNull(savedProductDto);
        assertEquals(product.getId(), savedProductDto.getId());
        assertEquals(product.getName(), savedProductDto.getName());
    }

    @Test
    public void ProductService_SaveProduct_DuplicateProductNameThrowsIllegalArgumentException() {
        // Arrange
        ProductDto productDto = new ProductDto();
        productDto.setName("Test Product");
        productDto.setPrice(10.0);

        when(productRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.saveProduct(productDto));
    }

    @Test
    public void ProductService_UpdateProduct_ValidProductIdAndData_ReturnsUpdatedProductDTO() {
        // Arrange
        int productId = 1;
        String updatedName = "Updated Product";
        Double updatedPrice = 15.0;
        String updatedThumbnail = "updated_thumbnail.jpg";
        String updatedDetail = "Updated Product Details";
        int categoryId = 1; // Category ID

        // Creating Category and CategoryDTO
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Category 1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Category 1");

        Integer newQuantity = 50;
        Date newDate = new Date();

        // Creating ProductDTO
        ProductDto productDto = new ProductDto(productId, updatedName, updatedPrice, updatedThumbnail, updatedDetail, categoryDto, newQuantity, newDate);
        Product existingProduct = new Product(productId, "Test Product", 10.0, "thumbnail", "Test Details", category, 100, new Date());
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category)); // Mock category fetch

        // Act
        ProductDto updatedProductDTO = productService.updateProduct(productId, updatedName, updatedPrice, updatedThumbnail, updatedDetail, categoryId, newQuantity, newDate);

        // Assert
        assertNotNull(updatedProductDTO);
        assertEquals(productId, updatedProductDTO.getId());
        assertEquals(updatedName, updatedProductDTO.getName());
        assertEquals(updatedPrice, updatedProductDTO.getPrice());
        assertEquals(updatedThumbnail, updatedProductDTO.getThumbnail());
        assertEquals(updatedDetail, updatedProductDTO.getDetails());
        assertEquals(categoryDto.getId(), updatedProductDTO.getCategory().getId());
        assertEquals(categoryDto.getName(), updatedProductDTO.getCategory().getName());
        assertEquals(newQuantity, updatedProductDTO.getQuantity());
    }

    @Test
    public void ProductService_UpdateProduct_ProductNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int productId = 1;
        String updatedName = "Updated Product";
        Double updatedPrice = 15.0;
        String updatedThumbnail = "updated_thumbnail.jpg";
        String updatedDetail = "Updated Product Details";
        int categoryId = 1; // Category ID

        // Creating Category and CategoryDTO
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Category 1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Category 1");

        Integer newQuantity = 50;
        Date newDate = new Date();

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, updatedName, updatedPrice, updatedThumbnail, updatedDetail, categoryId, newQuantity, newDate));
    }

    @Test
    public void ProductService_UpdateProduct_CategoryNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int productId = 1;
        String updatedName = "Updated Product";
        Double updatedPrice = 15.0;
        String updatedThumbnail = "updated_thumbnail.jpg";
        String updatedDetail = "Updated Product Details";
        int categoryId = 1; // Category ID

        // Creating Category and CategoryDTO
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Category 1");
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Category 1");

        Integer newQuantity = 50;
        Date newDate = new Date();

        // Mocking the product fetch
        Product existingProduct = new Product(productId, "Test Product", 10.0, "thumbnail", "Test Details", category, 100, new Date());
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

        // Mocking the category fetch with an empty Optional to simulate category not found
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, updatedName, updatedPrice, updatedThumbnail, updatedDetail, categoryId, newQuantity, newDate));
    }

    @Test
    public void ProductService_Delete_ValidProductId_DeletesProduct() {
        // Arrange
        int productId = 1;
        Product existingProduct = new Product(productId, "Test Product", 10.0, "thumbnail", "Test Details", new Category(), 100, new Date());
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        // Act
        assertDoesNotThrow(() -> productService.deleteProduct(productId));

        // Assert
        verify(productRepository, times(1)).deleteById(productId);
    }

    @Test
    public void ProductService_Delete_ProductNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int productId = 1;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));
    }

    @Test
    public void ProductService_Delete_AssociatedProductThrowsIllegalArgumentException() {
        // Arrange
        int productId = 1;
        Product existingProduct = new Product(productId, "Test Product", 10.0, "thumbnail", "Test Details", new Category(), 100, new Date());
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(productId);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.deleteProduct(productId));
    }
}
