package staj.ordermanagementsystemapi.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import staj.ordermanagementsystemapi.business.abstracts.StaffService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.StaffDto;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    @Autowired
    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping("/get")
    public ResponseEntity<List<StaffDto>> getAllStaff() {
        List<StaffDto> staffList = staffService.getAllStaff();
        try{
            return new ResponseEntity<>(staffList, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<StaffDto> getStaffById(@PathVariable Integer id) {
        StaffDto staff = staffService.getStaffById(id);
        if (staff != null) {
            return new ResponseEntity<>(staff, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/add")
    public ResponseEntity<StaffDto> addStaff(@RequestBody StaffDto staffDTO) {
        StaffDto savedStaff = staffService.saveStaff(staffDTO);
        return new ResponseEntity<>(savedStaff, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StaffDto> updateStaff(
            @PathVariable Integer id,
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String mail,
            @RequestParam String password,
            @RequestParam String role
    ) {
        try {
            StaffDto updatedStaff = staffService.updateStaff(id, name, phone, mail, password, role);
            return new ResponseEntity<>(updatedStaff, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Integer id) {
        try {
            staffService.deleteStaff(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}