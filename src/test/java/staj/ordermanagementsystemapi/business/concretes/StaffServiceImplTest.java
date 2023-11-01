package staj.ordermanagementsystemapi.business.concretes;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import staj.ordermanagementsystemapi.business.abstracts.StaffService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.StaffRepository;
import staj.ordermanagementsystemapi.entities.concretes.Staff;
import staj.ordermanagementsystemapi.entities.dto.StaffDto;

class StaffServiceImplTest {

    private StaffService staffService;
    private StaffRepository staffRepository;
    private ModelMapper modelMapper;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        staffRepository = mock(StaffRepository.class);
        modelMapper = new ModelMapper();
        passwordEncoder = new BCryptPasswordEncoder();
        staffService = new StaffServiceImpl(staffRepository, modelMapper, passwordEncoder);
    }

    @Test
    public void StaffService_GetAll_ReturnsAllStaff() {
        // Arrange
        List<Staff> staffList = new ArrayList<>();
        staffList.add(new Staff(1, "Staff 1", "1234567890", "staff1@staff.com", "password", "ROLE_ADMIN", new Date()));
        staffList.add(new Staff(2, "Staff 2", "0987654321", "staff2@staff.com", "password", "ROLE_USER", new Date()));
        when(staffRepository.findAll()).thenReturn(staffList);

        // Act
        List<StaffDto> staffDtoList = staffService.getAllStaff();

        // Assert
        assertNotNull(staffDtoList);
        assertEquals(staffList.size(), staffDtoList.size());
    }

    @Test
    public void StaffService_GetById_ValidStaffReturnsStaff() {
        // Arrange
        int staffId = 1;
        Staff staff = new Staff(staffId, "Test Staff", "1234567890", "teststaff@staff.com", "password", "ROLE_ADMIN", new Date());
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(staff));

        // Act
        StaffDto staffDto = staffService.getStaffById(staffId);

        // Assert
        assertNotNull(staffDto);
        assertEquals(staff.getId(), staffDto.getId());
        assertEquals(staff.getName(), staffDto.getName());
        assertEquals(staff.getMail(), staffDto.getMail());
    }

    @Test
    public void StaffService_GetById_StaffNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int staffId = 1;
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> staffService.getStaffById(staffId));
    }

    @Test
    public void StaffService_SaveStaff_ValidStaffDTO_ReturnsSavedStaffDTO() {
        // Arrange
        StaffDto staffDto = new StaffDto();
        staffDto.setName("Test Staff");
        staffDto.setMail("teststaff@example.com");
        staffDto.setPassword("testpassword");

        Staff staff = new Staff();
        staff.setName(staffDto.getName());
        staff.setMail(staffDto.getMail());
        staff.setPassword(staffDto.getPassword());

        when(staffRepository.save(any(Staff.class))).thenReturn(staff);

        // Act
        StaffDto savedStaffDto = staffService.saveStaff(staffDto);

        // Assert
        assertNotNull(savedStaffDto);
        assertEquals(staff.getName(), savedStaffDto.getName());
        assertEquals(staff.getMail(), savedStaffDto.getMail());
    }


    @Test
    public void StaffService_UpdateStaff_ValidStaffIdAndData_ReturnsUpdatedStaffDTO() {
        // Arrange
        int staffId = 1;
        String updatedName = "Updated Staff";
        String updatedEmail = "updatedstaff@example.com";
        StaffDto staffDto = new StaffDto(staffId, updatedName, "1234567890", updatedEmail, "password", "ROLE_ADMIN", new Date());
        Staff existingStaff = new Staff(staffId, "Test Staff", "1234567890", "teststaff@staff.com", "password", "ROLE_ADMIN", new Date());
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
        when(staffRepository.save(any())).thenReturn(existingStaff);

        // Act
        StaffDto updatedStaffDto = staffService.updateStaff(staffId, updatedName, staffDto.getPhone(), updatedEmail, staffDto.getPassword(), staffDto.getRole());

        // Assert
        assertNotNull(updatedStaffDto);
        assertEquals(staffId, updatedStaffDto.getId());
        assertEquals(updatedName, updatedStaffDto.getName());
        assertEquals(updatedEmail, updatedStaffDto.getMail());
    }

    @Test
    public void StaffService_UpdateStaff_StaffNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int staffId = 1;
        String updatedName = "Updated Staff";
        String updatedEmail = "updatedstaff@staff.com";
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> staffService.updateStaff(staffId, updatedName, "1234567890", updatedEmail, "password", "ROLE_ADMIN"));
    }

    @Test
    public void StaffService_Delete_ValidStaffId_DeletesStaff() {
        // Arrange
        int staffId = 1;
        Staff existingStaff = new Staff(staffId, "Test Staff", "1234567890", "teststaff@staff.com", "password", "ROLE_ADMIN", new Date());
        when(staffRepository.findById(staffId)).thenReturn(Optional.of(existingStaff));
        // Act
        assertDoesNotThrow(() -> staffService.deleteStaff(staffId));

        // Assert
        verify(staffRepository, times(1)).deleteById(staffId);
    }

    @Test
    public void StaffService_Delete_StaffNotFoundThrowsResourceNotFoundException() {
        // Arrange
        int staffId = 1;
        when(staffRepository.findById(staffId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> staffService.deleteStaff(staffId));
    }
}
