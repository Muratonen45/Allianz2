package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.CustomerService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDto getCustomerById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return modelMapper.map(customer, CustomerDto.class);
    }

    @Override
    public CustomerDto saveCustomer(CustomerDto customerDTO) {
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        try {
            String hashedPassword = passwordEncoder.encode(customer.getPassword());
            customer.setPassword(hashedPassword);
            Customer savedCustomer = customerRepository.save(customer);
            return modelMapper.map(savedCustomer, CustomerDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save the customer:" + e.getMessage());
        }
    }

    @Override
    public CustomerDto updateCustomer(Integer id, String updatedName, String updatedLocation,
                                      String updatedPhone, String updatedMail, String updatedBirthDate,
                                      String updatedPassword, Double updatedWalletBalance) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        try {
            Customer updatedCustomer = new Customer(id, updatedName, updatedLocation,
                    updatedPhone, updatedMail, updatedBirthDate, updatedPassword,
                    updatedWalletBalance, customer.getTimestamp());

            customerRepository.save(updatedCustomer);
            return modelMapper.map(updatedCustomer, CustomerDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update the customer:" + e.getMessage());
        }
    }

    @Override
    public void deleteCustomer(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        try {
            customerRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to delete the customer:" + e.getMessage());
        }
    }
}