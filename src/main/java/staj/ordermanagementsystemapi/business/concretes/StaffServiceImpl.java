package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.StaffService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.StaffRepository;
import staj.ordermanagementsystemapi.entities.concretes.Staff;
import staj.ordermanagementsystemapi.entities.dto.StaffDto;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<StaffDto> getAllStaff() {
        List<Staff> staffList = staffRepository.findAll();
        return staffList.stream()
                .map(staff -> modelMapper.map(staff, StaffDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public StaffDto getStaffById(Integer id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Staff", "id", id));
        return modelMapper.map(staff, StaffDto.class);
    }

    @Override
    public StaffDto saveStaff(StaffDto staffDto) {
        Staff staff = modelMapper.map(staffDto, Staff.class);
        String hashedPassword = passwordEncoder.encode(staff.getPassword());
        staff.setPassword(hashedPassword);
        Staff savedStaff = staffRepository.save(staff);
        return modelMapper.map(savedStaff, StaffDto.class);
    }

    @Override
    public StaffDto updateStaff(Integer id, String updatedName, String updatedPhone, String updatedMail, String updatedPassword, String updatedRole) {
        Staff existingStaff = staffRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Staff", "id", id));

        existingStaff.setName(updatedName);
        existingStaff.setPhone(updatedPhone);
        existingStaff.setMail(updatedMail);
        existingStaff.setPassword(updatedPassword);
        existingStaff.setRole(updatedRole);

        Staff updatedStaff = staffRepository.save(existingStaff);
        return modelMapper.map(updatedStaff, StaffDto.class);
    }

    @Override
    public void deleteStaff(Integer id) {
        Staff existingStaff = staffRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Staff", "id", id));
        staffRepository.deleteById(id);
    }
}