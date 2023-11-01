package staj.ordermanagementsystemapi.entities.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDto {
    private Integer id;
    private String description;
    private Integer star;
    private CustomerDto customer;
    private ProductDto product;
    private Date timestamp;
}
