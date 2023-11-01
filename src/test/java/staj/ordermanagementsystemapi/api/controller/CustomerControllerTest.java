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

import staj.ordermanagementsystemapi.business.abstracts.CustomerService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;

public class CustomerControllerTest {

    private CustomerController customerController;
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        customerService = mock(CustomerService.class);
        customerController = new CustomerController(customerService);
    }

    @Test
    void getAllCustomers_ReturnsAllCustomersSuccessfully() {
        // Arrange
        List<CustomerDto> expectedCustomers = new ArrayList<>();
        expectedCustomers.add(new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date()));
        expectedCustomers.add(new CustomerDto(2, "huseyin onen", "huseyin@huseyin.com", "67891", "huseyin's location", "18-04-1996", "password456", 50.0, new Date()));
        when(customerService.getAllCustomers()).thenReturn(expectedCustomers);

        // Act
        ResponseEntity<List<CustomerDto>> responseEntity = customerController.getAllCustomers();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedCustomers.size(), responseEntity.getBody().size());
    }

    @Test
    void getCustomerById_ValidCustomerId_ReturnsCustomerSuccessfully() {
        // Arrange
        int customerId = 1;
        CustomerDto expectedCustomer = new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date());
        when(customerService.getCustomerById(customerId)).thenReturn(expectedCustomer);

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.getCustomerById(customerId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedCustomer.getId(), responseEntity.getBody().getId());
        assertEquals(expectedCustomer.getName(), responseEntity.getBody().getName());
    }

    @Test
    void getCustomerById_CustomerNotFound_ReturnsNotFound() {
        // Arrange
        int customerId = 1;
        when(customerService.getCustomerById(customerId)).thenReturn(null);

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.getCustomerById(customerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void addCustomer_ValidCustomer_ReturnsCreatedCustomer() {
        // Arrange
        CustomerDto newCustomer = new CustomerDto(null, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, null);
        CustomerDto expectedSavedCustomer = new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date());
        when(customerService.saveCustomer(newCustomer)).thenReturn(expectedSavedCustomer);

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.addCustomer(newCustomer);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedCustomer.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedCustomer.getName(), responseEntity.getBody().getName());
    }

    @Test
    void addCustomer_DuplicateCustomerEmail_ReturnsBadRequest() {
        // Arrange
        CustomerDto newCustomer = new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date());
        when(customerService.saveCustomer(newCustomer)).thenThrow(new IllegalArgumentException("Customer email must be unique."));

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.addCustomer(newCustomer);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    @Test
    void updateCustomer_ValidCustomer_ReturnsUpdatedCustomer() {
        // Arrange
        int customerId = 1;
        CustomerDto updatedCustomer = new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date());
        when(customerService.updateCustomer(customerId, updatedCustomer.getName(), updatedCustomer.getLocation(),
                updatedCustomer.getPhone(), updatedCustomer.getMail(), updatedCustomer.getBirthDate(),
                updatedCustomer.getPassword(), updatedCustomer.getWalletBalance())).thenReturn(updatedCustomer);

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.updateCustomer(customerId, updatedCustomer.getName(),
                updatedCustomer.getLocation(), updatedCustomer.getPhone(), updatedCustomer.getMail(),
                updatedCustomer.getBirthDate(), updatedCustomer.getPassword(), updatedCustomer.getWalletBalance());

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedCustomer.getId(), responseEntity.getBody().getId());
        assertEquals(updatedCustomer.getName(), responseEntity.getBody().getName());
        assertEquals(updatedCustomer.getMail(), responseEntity.getBody().getMail());
    }

    @Test
    void updateCustomer_CustomerNotFound_ReturnsNotFound() {
        // Arrange
        int customerId = 1;
        CustomerDto updatedCustomer = new CustomerDto(1, "murat onen", "murat@murat.com", "12345", "murat's location", "21-06-2002", "password123", 100.0, new Date());
        when(customerService.updateCustomer(customerId, updatedCustomer.getName(), updatedCustomer.getLocation(),
                updatedCustomer.getPhone(), updatedCustomer.getMail(), updatedCustomer.getBirthDate(),
                updatedCustomer.getPassword(), updatedCustomer.getWalletBalance()))
                .thenThrow(new ResourceNotFoundException("Customer", "id", customerId));

        // Act
        ResponseEntity<CustomerDto> responseEntity = customerController.updateCustomer(customerId, updatedCustomer.getName(),
                updatedCustomer.getLocation(), updatedCustomer.getPhone(), updatedCustomer.getMail(),
                updatedCustomer.getBirthDate(), updatedCustomer.getPassword(), updatedCustomer.getWalletBalance());

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void deleteCustomer_ValidCustomerId_ReturnsNoContent() {
        // Arrange
        int customerId = 1;

        // Act
        ResponseEntity<Void> responseEntity = customerController.deleteCustomer(customerId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteCustomer_CustomerNotFound_ReturnsNotFound() {
        // Arrange
        int customerId = 1;
        doThrow(new ResourceNotFoundException("Customer", "id", customerId)).when(customerService).deleteCustomer(customerId);

        // Act
        ResponseEntity<Void> responseEntity = customerController.deleteCustomer(customerId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
