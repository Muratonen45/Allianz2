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

import staj.ordermanagementsystemapi.business.abstracts.OrderService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;
import staj.ordermanagementsystemapi.entities.dto.OrderDto;
import staj.ordermanagementsystemapi.entities.dto.ProductDto;

public class OrderControllerTest {

    private OrderController orderController;
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        orderService = mock(OrderService.class);
        orderController = new OrderController(orderService);
    }

    @Test
    void getAllOrders_ReturnsAllOrdersSuccessfully() {
        // Arrange
        List<OrderDto> expectedOrders = new ArrayList<>();
        expectedOrders.add(new OrderDto(1, new CustomerDto(), new ProductDto(), 5, new Date(), null, "pending"));
        expectedOrders.add(new OrderDto(2, new CustomerDto(), new ProductDto(), 3, new Date(), null, "delivered"));
        when(orderService.getAllOrders()).thenReturn(expectedOrders);

        // Act
        ResponseEntity<List<OrderDto>> responseEntity = orderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedOrders.size(), responseEntity.getBody().size());
    }

    @Test
    void getOrderById_ValidOrderId_ReturnsOrderSuccessfully() {
        // Arrange
        int orderId = 1;
        OrderDto expectedOrder = new OrderDto(1, new CustomerDto(), new ProductDto(), 5, new Date(), null, "pending");
        when(orderService.getOrderById(orderId)).thenReturn(expectedOrder);

        // Act
        ResponseEntity<OrderDto> responseEntity = orderController.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedOrder.getId(), responseEntity.getBody().getId());
        assertEquals(expectedOrder.getStatus(), responseEntity.getBody().getStatus());
    }

    @Test
    void getOrderById_OrderNotFound_ReturnsNotFound() {
        // Arrange
        int orderId = 1;
        when(orderService.getOrderById(orderId)).thenReturn(null);

        // Act
        ResponseEntity<OrderDto> responseEntity = orderController.getOrderById(orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void getOrdersOfCustomer_ReturnsOrdersSuccessfully() {
        // Arrange
        List<OrderDto> expectedOrders = new ArrayList<>();
        CustomerDto customer = new CustomerDto(1, "name", "loc", "phone", "mail", "birth", "pass", 500.00, new Date());
        expectedOrders.add(new OrderDto(1, customer, new ProductDto(), 5, new Date(), null, "pending"));
        expectedOrders.add(new OrderDto(2, customer, new ProductDto(), 3, new Date(), new Date(), "delivered"));
        when(orderService.getOrdersOfCustomer(1)).thenReturn(expectedOrders);

        // Act
        ResponseEntity<List<OrderDto>> responseEntity = orderController.getOrderOfCustomer(1);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedOrders.size(), responseEntity.getBody().size());
    }

    @Test
    void addOrder_ValidOrder_ReturnsCreatedOrder() {
        // Arrange
        OrderDto newOrder = new OrderDto(null, new CustomerDto(), new ProductDto(), 5, new Date(), null, "pending");
        OrderDto expectedSavedOrder = new OrderDto(1, new CustomerDto(), new ProductDto(), 5, new Date(), null, "pending");
        when(orderService.saveOrder(newOrder)).thenReturn(expectedSavedOrder);

        // Act
        ResponseEntity<OrderDto> responseEntity = orderController.addOrder(newOrder);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedOrder.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedOrder.getStatus(), responseEntity.getBody().getStatus());
    }

    @Test
    void updateOrder_ValidOrder_ReturnsUpdatedOrder() {
        // Arrange
        int orderId = 1;
        Date updatedDeliveryDate = new Date();
        String updatedStatus = "delivered";
        OrderDto updatedOrder = new OrderDto(orderId, new CustomerDto(), new ProductDto(), 5, new Date(), updatedDeliveryDate, updatedStatus);
        when(orderService.updateOrder(orderId, updatedDeliveryDate, updatedStatus))
                .thenReturn(updatedOrder);

        // Act
        ResponseEntity<OrderDto> responseEntity = orderController.updateOrder(orderId, updatedDeliveryDate, updatedStatus);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedOrder.getId(), responseEntity.getBody().getId());
        assertEquals(updatedOrder.getStatus(), responseEntity.getBody().getStatus());
    }

    @Test
    void updateOrder_OrderNotFound_ReturnsNotFound() {
        // Arrange
        int orderId = 1;
        Date updatedDeliveryDate = new Date();
        String updatedStatus = "delivered";
        when(orderService.updateOrder(orderId, updatedDeliveryDate, updatedStatus))
                .thenThrow(new ResourceNotFoundException("Order", "id", orderId));

        // Act
        ResponseEntity<OrderDto> responseEntity = orderController.updateOrder(orderId, updatedDeliveryDate, updatedStatus);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void deleteOrder_ValidOrderId_ReturnsNoContent() {
        // Arrange
        int orderId = 1;

        // Act
        ResponseEntity<Void> responseEntity = orderController.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteOrder_OrderNotFound_ReturnsNotFound() {
        // Arrange
        int orderId = 1;
        doThrow(new ResourceNotFoundException("Order", "id", orderId)).when(orderService).deleteOrder(orderId);

        // Act
        ResponseEntity<Void> responseEntity = orderController.deleteOrder(orderId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
