package staj.ordermanagementsystemapi.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import staj.ordermanagementsystemapi.business.abstracts.StaffService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.dto.StaffDto;

public class StaffControllerTest {

    private StaffController staffController;
    private StaffService staffService;

    @BeforeEach
    public void setUp() {
        staffService = mock(StaffService.class);
        staffController = new StaffController(staffService);
    }

    @Test
    void getAllStaff_ReturnsAllStaffSuccessfully() {
        // Arrange
        List<StaffDto> expectedStaffList = new ArrayList<>();
        expectedStaffList.add(new StaffDto(1, "murat onen", "123456789", "murat@murat.com", "password", "admin", new Date()));
        expectedStaffList.add(new StaffDto(2, "huseyin onen", "987654321", "huseyin@huseyin.com", "password", "staff", new Date()));
        when(staffService.getAllStaff()).thenReturn(expectedStaffList);

        // Act
        ResponseEntity<List<StaffDto>> responseEntity = staffController.getAllStaff();

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedStaffList.size(), responseEntity.getBody().size());
    }

    @Test
    void getStaffById_ValidStaffId_ReturnsStaffSuccessfully() {
        // Arrange
        int staffId = 1;
        StaffDto expectedStaff = new StaffDto(staffId, "murat onen", "123456789", "murat@murat.com", "password", "admin", new Date());
        when(staffService.getStaffById(staffId)).thenReturn(expectedStaff);

        // Act
        ResponseEntity<StaffDto> responseEntity = staffController.getStaffById(staffId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedStaff.getId(), responseEntity.getBody().getId());
        assertEquals(expectedStaff.getName(), responseEntity.getBody().getName());
    }

    @Test
    void getStaffById_StaffNotFound_ReturnsNotFound() {
        // Arrange
        int staffId = 1;
        when(staffService.getStaffById(staffId)).thenReturn(null);

        // Act
        ResponseEntity<StaffDto> responseEntity = staffController.getStaffById(staffId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void addStaff_ValidStaff_ReturnsCreatedStaff() {
        // Arrange
        StaffDto newStaff = new StaffDto(null, "murat onen", "123456789", "murat@murat.com", "password", "admin", null);
        StaffDto expectedSavedStaff = new StaffDto(1, "murat onen", "123456789", "murat@murat.com", "password", "admin", new Date());
        when(staffService.saveStaff(newStaff)).thenReturn(expectedSavedStaff);

        // Act
        ResponseEntity<StaffDto> responseEntity = staffController.addStaff(newStaff);

        // Assert
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(expectedSavedStaff.getId(), responseEntity.getBody().getId());
        assertEquals(expectedSavedStaff.getName(), responseEntity.getBody().getName());
    }

    @Test
    void updateStaff_ValidStaff_ReturnsUpdatedStaff() {
        // Arrange
        int staffId = 1;
        String updatedName = "huseyin onen";
        String updatedPhone = "987654321";
        String updatedMail = "huseyinonen@onen.com";
        String updatedPassword = "newpassword";
        String updatedRole = "staff";
        StaffDto updatedStaff = new StaffDto(staffId, updatedName, updatedPhone, updatedMail, updatedPassword, updatedRole, new Date());
        when(staffService.updateStaff(staffId, updatedName, updatedPhone, updatedMail, updatedPassword, updatedRole))
                .thenReturn(updatedStaff);

        // Act
        ResponseEntity<StaffDto> responseEntity = staffController.updateStaff(staffId, updatedName, updatedPhone, updatedMail, updatedPassword, updatedRole);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(updatedStaff.getId(), responseEntity.getBody().getId());
        assertEquals(updatedStaff.getName(), responseEntity.getBody().getName());
    }

    @Test
    void updateStaff_StaffNotFound_ReturnsNotFound() {
        // Arrange
        int staffId = 1;
        String updatedName = "huseyin onen";
        String updatedPhone = "987654321";
        String updatedMail = "huseyinonen@onen.com";
        String updatedPassword = "newpassword";
        String updatedRole = "staff";
        when(staffService.updateStaff(staffId, updatedName, updatedPhone, updatedMail, updatedPassword, updatedRole))
                .thenThrow(new ResourceNotFoundException("Staff", "id", staffId));

        // Act
        ResponseEntity<StaffDto> responseEntity = staffController.updateStaff(staffId, updatedName, updatedPhone, updatedMail, updatedPassword, updatedRole);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertNull(responseEntity.getBody());
    }

    @Test
    void deleteStaff_ValidStaffId_ReturnsNoContent() {
        // Arrange
        int staffId = 1;

        // Act
        ResponseEntity<Void> responseEntity = staffController.deleteStaff(staffId);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    }

    @Test
    void deleteStaff_StaffNotFound_ReturnsNotFound() {
        // Arrange
        int staffId = 1;
        doThrow(new ResourceNotFoundException("Staff", "id", staffId)).when(staffService).deleteStaff(staffId);

        // Act
        ResponseEntity<Void> responseEntity = staffController.deleteStaff(staffId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }
}
