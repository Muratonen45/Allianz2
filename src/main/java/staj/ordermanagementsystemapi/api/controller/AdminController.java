package staj.ordermanagementsystemapi.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import staj.ordermanagementsystemapi.business.abstracts.AdminService;
import staj.ordermanagementsystemapi.business.abstracts.CustomerService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.concretes.Admin;
import staj.ordermanagementsystemapi.entities.dto.CustomerDto;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CustomerService customerService;

    @Autowired
    public AdminController(AdminService adminService,CustomerService customerService) {
        this.adminService = adminService;
        this.customerService=customerService;
    }
    
    @GetMapping("/get")
    public List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }
    

    @PostMapping("/add")
    public ResponseEntity<Admin> addAdmin(@RequestBody Admin admin) {
        try {
            Admin savedAdmin = adminService.saveAdmin(admin);
            return new ResponseEntity<>(savedAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Integer id) {
        try {
            adminService.deleteAdmin(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
