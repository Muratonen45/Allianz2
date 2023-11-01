package staj.ordermanagementsystemapi.business.concretes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.LoginService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.AdminRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ManagerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.StaffRepository;
import staj.ordermanagementsystemapi.entities.concretes.Admin;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Manager;
import staj.ordermanagementsystemapi.entities.concretes.Staff;

@Service
public class LoginServiceImpl implements LoginService{

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ManagerRepository managerRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public String login(String username, String password) {
        Admin admin = adminRepository.findByUsername(username);
        if (admin != null) {
            if (passwordEncoder.matches(password, admin.getPassword())) {
                return "admin";
            }
        }

        Manager manager = managerRepository.findByUsername(username);
        if (manager != null) {
            if (passwordEncoder.matches(password, manager.getPassword())) {
                return "manager";
            }
        }

        Staff staff = staffRepository.findByMail(username);
        if (staff != null) {
            if (passwordEncoder.matches(password, staff.getPassword())) {
                return "staff";
            }
        }

        Customer customer = customerRepository.findByMail(username);
        if (customer != null) {
            if (passwordEncoder.matches(password, customer.getPassword())) {
                return "customer";
            }
        }

        if (admin == null && manager == null && staff == null && customer == null) {
            throw new ResourceNotFoundException("User", "username or mail", username);
        }
        throw new IllegalArgumentException("Invalid username or password.");
    }
}
