package staj.ordermanagementsystemapi.business.concretes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import staj.ordermanagementsystemapi.business.abstracts.CustomerService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;
class CustomerServiceImplTest {

    private CustomerService customerService;
    private CustomerRepository customerRepository;
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        customerRepository = mock(CustomerRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = new BCryptPasswordEncoder();
        customerService = new CustomerServiceImpl(customerRepository, modelMapper, passwordEncoder);
    }

    // CustomerService Tests
    @Test
    public void CustomerService_GetAll_ReturnsAllCustomers() {
        // Arrange
        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer(1, "Customer 1", "Location 1", "1234567890", "customer1@customer.com", "1996-04-18", "password1", 100.0, new Date()));
        customers.add(new Customer(2, "Customer 2", "Location 2", "0987654321", "customer2@customer.com", "2002-06-21", "password2", 200.0, new Date()));
        when(customerRepository.findAll()).thenReturn(customers);

        // Act
        List<CustomerDto> customerDtoList = customerService.getAllCustomers();

        // Assert
        assertNotNull(customerDtoList);
        assertEquals(customers.size(), customerDtoList.size());
    }

    @Test
    public void CustomerService_GetCustomerById_ValidCustomerReturnsCustomer() {
        // Arrange
        int customerId = 1;
        Customer customer = new Customer(1, "Test Customer", "Test Location", "1234567890", "test@example.com", "1996-04-18", "test123", 100.0, new Date());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerDto customerDto = customerService.getCustomerById(customerId);

        // Assert
        assertNotNull(customerDto);
        assertEquals(customer.getId(), customerDto.getId());
        assertEquals(customer.getName(), customerDto.getName());
    }

    @Test
    public void CustomerService_GetCustomerById_CustomerNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int customerId = 1;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.getCustomerById(customerId));
    }

    @Test
    public void CustomerService_SaveCustomer_ValidCustomerDTO_ReturnsSavedCustomerDTO() {
        // Arrange
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Test Customer");
        customerDto.setLocation("Test Location");
        customerDto.setPhone("1234567890");
        customerDto.setMail("test@example.com");
        customerDto.setBirthDate("1996-04-18");
        customerDto.setPassword("test123");
        customerDto.setWalletBalance(100.0);

        Customer customer = modelMapper.map(customerDto, Customer.class);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // Act
        CustomerDto savedCustomerDto = customerService.saveCustomer(customerDto);

        // Assert
        assertNotNull(savedCustomerDto);
        assertEquals(customer.getId(), savedCustomerDto.getId());
        assertEquals(customer.getName(), savedCustomerDto.getName());
    }

    @Test
    public void CustomerService_UpdateCustomer_ValidCustomerIdAndData_ReturnsUpdatedCustomerDTO() {
        // Arrange
        int customerId = 1;
        String updatedName = "Updated Customer";
        String updatedLocation = "Updated Location";
        String updatedPhone = "0987654321";
        String updatedMail = "updated@example.com";
        String updatedBirthDate = "2002-06-21";
        String updatedPassword = "updated123";
        Double updatedWalletBalance = 200.0;

        Customer customer = new Customer(customerId, "Test Customer", "Test Location", "1234567890", "test@example.com", "1996-04-18", "test123", 100.0, new Date());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        // Act
        CustomerDto updatedCustomerDto = customerService.updateCustomer(customerId, updatedName, updatedLocation, updatedPhone, updatedMail, updatedBirthDate, updatedPassword, updatedWalletBalance);

        // Assert
        assertNotNull(updatedCustomerDto);
        assertEquals(customerId, updatedCustomerDto.getId());
        assertEquals(updatedName, updatedCustomerDto.getName());
        assertEquals(updatedLocation, updatedCustomerDto.getLocation());
        assertEquals(updatedPhone, updatedCustomerDto.getPhone());
        assertEquals(updatedMail, updatedCustomerDto.getMail());
        assertEquals(updatedBirthDate, updatedCustomerDto.getBirthDate());
        assertEquals(updatedPassword, updatedCustomerDto.getPassword());
        assertEquals(updatedWalletBalance, updatedCustomerDto.getWalletBalance());
    }

    @Test
    public void CustomerService_UpdateCustomer_CustomerNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int customerId = 1;
        String updatedName = "Updated Customer";
        String updatedLocation = "Updated Location";
        String updatedPhone = "0987654321";
        String updatedMail = "updated@example.com";
        String updatedBirthDate = "2002-06-21";
        String updatedPassword = "updated123";
        Double updatedWalletBalance = 200.0;

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.updateCustomer(customerId, updatedName, updatedLocation, updatedPhone, updatedMail, updatedBirthDate, updatedPassword, updatedWalletBalance));
    }

    @Test
    public void CustomerService_Delete_ValidCustomerId_DeletesCustomer() {
        // Arrange
        int customerId = 1;
        Customer existingCustomer = new Customer(customerId, "Test Customer", "Test Location",
        		"1234567890", "test@example.com", "1996-04-18", "test123", 100.0, new Date());
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        // Act
        assertDoesNotThrow(() -> customerService.deleteCustomer(customerId));

        // Assert
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    public void CustomerService_Delete_CustomerNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int customerId = 1;
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(customerId));
    }
}
