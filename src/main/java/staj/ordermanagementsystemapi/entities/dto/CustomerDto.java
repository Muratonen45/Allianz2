package staj.ordermanagementsystemapi.entities.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    private Integer id;
    private String name;
    private String location;
    private String phone;
    private String mail;
    private String birthDate;
    private String password;
    private Double walletBalance;
    private Date timestamp;
}