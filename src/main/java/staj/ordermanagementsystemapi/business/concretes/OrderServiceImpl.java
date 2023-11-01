package staj.ordermanagementsystemapi.business.concretes;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.OrderService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.OrderRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ProductRepository;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Order;
import staj.ordermanagementsystemapi.entities.concretes.Product;
import staj.ordermanagementsystemapi.entities.dto.OrderDto;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ModelMapper modelMapper, CustomerRepository customerRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    @Override
    public List<OrderDto> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto getOrderById(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        return modelMapper.map(order, OrderDto.class);
    }

    @Override
    public List<OrderDto> getOrdersOfCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer", "id", customerId));
        List<Order> orders = orderRepository.findByCustomer(customer);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderDto saveOrder(OrderDto orderDTO) {
        Order order = modelMapper.map(orderDTO, Order.class);
        validateOrder(order);
        try {
            withdrawCost(order);
            Order savedOrder = orderRepository.save(order);
            return modelMapper.map(savedOrder, OrderDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save the order: " + e.getMessage());
        }
    }

    public void validateOrder(Order order) {
        // Validate objects
        int customerId = order.getCustomer().getId();
        int productId = order.getProduct().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer", "id", customerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "id", productId));

        // Validate balance
        double balance = customer.getWalletBalance();
        double price = product.getPrice();
        int quantity = order.getQuantity();
        if (balance < price*quantity) {
            throw new IllegalArgumentException("Insufficient wallet balance: " + balance + " required: " + price*quantity);
        }
    }

    public void withdrawCost(Order order) {
        Customer customer = order.getCustomer();
        Product product = order.getProduct();
        double balance = customer.getWalletBalance();
        double totalPrice = product.getPrice() + order.getQuantity();

        try {
            customer.setWalletBalance(balance - totalPrice);
            customerRepository.save(customer);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to withdraw cost from customer:" + e.getMessage());
        }
    }

    @Override
    public OrderDto updateOrder(Integer id, Date deliveryDate, String status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));

        try {
            // Update the fields
            order.setDeliveryDate(deliveryDate);
            order.setStatus(status);

            orderRepository.save(order);
            return modelMapper.map(order, OrderDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update the order: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(Integer id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", id));
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to delete the order: " + e.getMessage());
        }
    }
}