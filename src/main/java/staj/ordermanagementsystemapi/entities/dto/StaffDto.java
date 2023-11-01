package staj.ordermanagementsystemapi.entities.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StaffDto {
    private Integer id;
    private String name;
    private String phone;
    private String mail;
    private String password;
    private String role;
    private Date timestamp;
}
