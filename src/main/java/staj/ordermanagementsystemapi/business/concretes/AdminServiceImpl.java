package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.AdminService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.AdminRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.entities.concretes.Admin;
import staj.ordermanagementsystemapi.entities.concretes.Customer;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,CustomerRepository customerRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.customerRepository= customerRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Admin> getAllAdmins() {
        return null;
    }
    
    
    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Admin getAdminById(Integer id) {
        return null;
    }

    @Override
    public Admin saveAdmin(Admin admin) {
        try {
            String hashedPassword = passwordEncoder.encode(admin.getPassword());
            admin.setPassword(hashedPassword);
            return adminRepository.save(admin);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save admin: " + e.getMessage());
        }
    }

    @Override
    public Admin updateAdmin(Integer id, Admin adminDTO ) {
        return null;
    }

    @Override
    public void deleteAdmin(Integer id) {
        Admin existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("admin", "id", id));
        adminRepository.deleteById(id);
    }
}
