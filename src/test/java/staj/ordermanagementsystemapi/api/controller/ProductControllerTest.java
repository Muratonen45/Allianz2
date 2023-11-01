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

import staj.ordermanagementsystemapi.business.abstracts.ProductService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.CategoryDto;
import staj.ordermanagementsystemapi.entities.dto.ProductDto;

public class ProductControllerTest {

    private ProductController productController;
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        productService = mock(ProductService.class);
        productController = new ProductController(productService);
    }

    @Test
    void getAllProducts_ReturnsAllProductsSuccessfully() {
        // Arrange
        List<ProductDto> expectedProducts = new ArrayList<>();
        expectedProducts.add(new ProductDto(1, "Product 1", 100.0, "thumbnail1", "detail1", new CategoryDto(), 100, new Date()));
        expectedProducts.add(new ProductDto(2, "Product 2", 200.0, "thumbnail2", "detail2", new CategoryDto(), 200, new Date()));
        when(productService.getAllProducts()).thenReturn(expectedProducts);

        // Act
        ResponseEntity<List<ProductDto>> responseEntity = productController.getAllProducts();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedProducts.size(), responseEntity.getBody().size());
    }

    @Test
    void getProductById_ValidProductId_ReturnsProductSuccessfully() {
        // Arrange
        int productId = 1;
        ProductDto expectedProduct = new ProductDto(productId, "Test Product", 100.0, "testthumbnail", "testdetail", new CategoryDto(), 50, new Date());
        when(productService.getProductById(productId)).thenReturn(expectedProduct);

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedProduct.getId(), responseEntity.getBody().getId());
        assertEquals(expectedProduct.getName(), responseEntity.getBody().getName());
    }

    @Test
    void getProductById_ProductNotFound_ReturnsNotFound() {
        // Arrange
        int productId = 1;
        when(productService.getProductById(productId)).thenReturn(null);

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.getProductById(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void addProduct_ValidProduct_ReturnsCreatedProduct() {
        // Arrange
        ProductDto newProduct = new ProductDto(null, "New Product", 500.0, "newthumbnail", "newdetail", new CategoryDto(), 10, new Date());
        ProductDto expectedSavedProduct = new ProductDto(1, "New Product", 500.0, "newthumbnail", "newdetail", new CategoryDto(), 10, new Date());
        when(productService.saveProduct(newProduct)).thenReturn(expectedSavedProduct);

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.addProduct(newProduct);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedProduct.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedProduct.getName(), responseEntity.getBody().getName());
    }

    @Test
    void addProduct_DuplicateProductName_ReturnsBadRequest() {
        // Arrange
        ProductDto newProduct = new ProductDto(null, "Duplicate Product", 500.0, "newthumbnail", "newdetail", new CategoryDto(), 10, new Date());
        when(productService.saveProduct(newProduct)).thenThrow(new IllegalArgumentException("Product name must be unique."));

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.addProduct(newProduct);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateProduct_ValidProduct_ReturnsUpdatedProduct() {
        // Arrange
        int productId = 1;
        ProductDto updatedProduct = new ProductDto(productId, "Updated Product", 800.0, "updatedthumbnail", "updateddetail", new CategoryDto(), 20, new Date());
        when(productService.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp()))
                .thenReturn(updatedProduct);

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedProduct.getId(), responseEntity.getBody().getId());
        assertEquals(updatedProduct.getName(), responseEntity.getBody().getName());
        assertEquals(updatedProduct.getPrice(), responseEntity.getBody().getPrice());
    }

    @Test
    void updateProduct_ProductNotFound_ReturnsNotFound() {
        // Arrange
        int productId = 1;
        ProductDto updatedProduct = new ProductDto(productId, "Updated Product", 800.0, "updatedthumbnail", "updateddetail", new CategoryDto(), 20, new Date());
        when(productService.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp()))
                .thenThrow(new ResourceNotFoundException("Product", "id", productId));

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void updateProduct_DuplicateProductName_ReturnsBadRequest() {
        // Arrange
        int productId = 1;
        ProductDto updatedProduct = new ProductDto(productId, "Updated Product", 800.0, "updatedthumbnail", "updateddetail", new CategoryDto(), 20, new Date());
        when(productService.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp()))
                .thenThrow(new IllegalArgumentException("Product name must be unique."));

        // Act
        ResponseEntity<ProductDto> responseEntity = productController.updateProduct(productId, updatedProduct.getName(), updatedProduct.getPrice(), updatedProduct.getThumbnail(), updatedProduct.getDetails(), updatedProduct.getCategory().getId(), updatedProduct.getQuantity(), updatedProduct.getTimestamp());

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void deleteProduct_ValidProductId_ReturnsNoContent() {
        // Arrange
        int productId = 1;

        // Act
        ResponseEntity<Void> responseEntity = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteProduct_ProductNotFound_ReturnsNotFound() {
        // Arrange
        int productId = 1;
        doThrow(new ResourceNotFoundException("Product", "id", productId)).when(productService).deleteProduct(productId);

        // Act
        ResponseEntity<Void> responseEntity = productController.deleteProduct(productId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
