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

import staj.ordermanagementsystemapi.business.abstracts.OrderService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.OrderRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ProductRepository;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Order;
import staj.ordermanagementsystemapi.entities.concretes.Product;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;
import staj.ordermanagementsystemapi.entities.dto.OrderDto;
import staj.ordermanagementsystemapi.entities.dto.ProductDto;

class OrderServiceImplTest {

    private OrderService orderService;
    private OrderRepository orderRepository;
    private ModelMapper modelMapper;
    private CustomerRepository customerRepository;
    private ProductRepository productRepository;

    @BeforeEach
    public void setUp() {
        orderRepository = mock(OrderRepository.class);
        modelMapper = new ModelMapper();
        customerRepository = mock(CustomerRepository.class);
        productRepository = mock(ProductRepository.class);

        orderService = new OrderServiceImpl(orderRepository, modelMapper, customerRepository, productRepository);
    }

    @Test
    public void OrderService_GetAll_ReturnsAllOrders() {
        // Arrange
        List<Order> orders = new ArrayList<>();
        orders.add(new Order(1, new Customer(), new Product(), 2, new Date(), null, "Pending"));
        orders.add(new Order(2, new Customer(), new Product(), 1, new Date(), null, "Delivered"));
        when(orderRepository.findAll()).thenReturn(orders);

        // Act
        List<OrderDto> orderDtoList = orderService.getAllOrders();

        // Assert
        assertNotNull(orderDtoList);
        assertEquals(orders.size(), orderDtoList.size());
    }

    @Test
    public void OrderService_GetById_ValidOrderReturnsOrder() {
        // Arrange
        int orderId = 1;
        Order order = new Order(orderId, new Customer(), new Product(), 2, new Date(), null, "Pending");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderDto orderDto = orderService.getOrderById(orderId);

        // Assert
        assertNotNull(orderDto);
        assertEquals(order.getId(), orderDto.getId());
        assertEquals(order.getQuantity(), orderDto.getQuantity());
    }

    @Test
    public void OrderService_GetById_OrderNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    public void OrderService_GetOrdersOfCustomer_ReturnsOrders() {
        // Arrange
        List<Order> ordersOfCustomer = new ArrayList<>();
        Integer customerId = 1;
        Customer customer = new Customer(customerId, "name", "loc", "phone", "mail", "birth", "pass", 500.00, new Date());
        ordersOfCustomer.add(new Order(1, customer, new Product(), 2, new Date(), null, "Pending"));
        ordersOfCustomer.add(new Order(2, customer, new Product(), 1, new Date(), null, "Delivered"));

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomer(customer)).thenReturn(ordersOfCustomer);

        // Act
        List<OrderDto> orderDtoList = orderService.getOrdersOfCustomer(customerId);

        // Assert
        assertNotNull(orderDtoList);
        assertEquals(ordersOfCustomer.size(), orderDtoList.size());
    }

    @Test
    public void OrderService_SaveOrder_ValidOrderDTO_ReturnsSavedOrderDTO() {
        // Arrange
        OrderDto orderDto = new OrderDto();
        orderDto.setQuantity(2);

        // Create and set Customer and Product
        CustomerDto customerDto = new CustomerDto();
        customerDto.setId(1); // Ensure this id exists in the customerRepository mock
        customerDto.setWalletBalance(100.0); // Balance enough for the order
        orderDto.setCustomer(customerDto);

        ProductDto productDto = new ProductDto();
        productDto.setId(1); // Ensure this id exists in the productRepository mock
        productDto.setPrice(20.0); // Price per product
        orderDto.setProduct(productDto);

        Order order = modelMapper.map(orderDto, Order.class);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // Mock repository findById methods
        Customer customer = modelMapper.map(customerDto, Customer.class);
        Product product = modelMapper.map(productDto, Product.class);
        when(customerRepository.findById(1)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        // Act
        OrderDto savedOrderDto = orderService.saveOrder(orderDto);

        // Assert
        assertNotNull(savedOrderDto);
        assertEquals(order.getQuantity(), savedOrderDto.getQuantity());
    }


    @Test
    public void OrderService_UpdateOrder_ValidOrderIdAndData_ReturnsUpdatedOrderDTO() {
        // Arrange
        int orderId = 1;
        Date newDeliveryDate = new Date();
        String newStatus = "Delivered";
        Order order = new Order(orderId, new Customer(), new Product(), 2, new Date(), null, "Pending");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        OrderDto updatedOrderDto = orderService.updateOrder(orderId, newDeliveryDate, newStatus);

        // Assert
        assertNotNull(updatedOrderDto);
        assertEquals(order.getId(), updatedOrderDto.getId());
        assertEquals(newDeliveryDate, updatedOrderDto.getDeliveryDate());
        assertEquals(newStatus, updatedOrderDto.getStatus());
    }

    @Test
    public void OrderService_UpdateOrder_OrderNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int orderId = 1;
        Date newDeliveryDate = new Date();
        String newStatus = "Delivered";
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrder(orderId, newDeliveryDate, newStatus));
    }

    @Test
    public void OrderService_Delete_ValidOrderId_DeletesOrder() {
        // Arrange
        int orderId = 1;
        Order order = new Order(orderId, new Customer(), new Product(), 2, new Date(), null, "Pending");
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // Act
        assertDoesNotThrow(() -> orderService.deleteOrder(orderId));

        // Assert
        verify(orderRepository, times(1)).deleteById(orderId);
    }

    @Test
    public void OrderService_Delete_OrderNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int orderId = 1;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> orderService.deleteOrder(orderId));
    }
}
