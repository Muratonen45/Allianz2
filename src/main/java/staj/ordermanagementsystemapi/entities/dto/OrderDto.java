package staj.ordermanagementsystemapi.entities.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {
    private Integer id;
    private CustomerDto customer;
    private ProductDto product;
    private Integer quantity;
    private Date orderDate;
    private Date deliveryDate;
    private String status;
}