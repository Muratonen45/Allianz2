package staj.ordermanagementsystemapi.business.abstracts;

import java.util.Date;
import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.OrderDto;

public interface OrderService {
    List<OrderDto> getAllOrders();
    OrderDto getOrderById(Integer id);
    List <OrderDto> getOrdersOfCustomer(Integer customerId);
    OrderDto saveOrder(OrderDto orderDto);
    OrderDto updateOrder(Integer id, Date deliveryDate, String updatedStatus);
    void deleteOrder(Integer id);
}
