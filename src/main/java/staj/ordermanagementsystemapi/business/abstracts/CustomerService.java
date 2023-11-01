package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.CustomerDto;

public interface CustomerService {
    List<CustomerDto> getAllCustomers();
    CustomerDto getCustomerById(Integer id);
    CustomerDto saveCustomer(CustomerDto customerDto);
    CustomerDto updateCustomer(Integer id,
    		                   String updatedName,
                               String updatedLocation, 
                               String updatedPhone,
                               String updatedMail, 
                               String updatedBirthDate,
                               String updatedPassword, 
                               Double updatedWalletBalance);
    void deleteCustomer(Integer id);
}
