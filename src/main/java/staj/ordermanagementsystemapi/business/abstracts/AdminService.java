package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;

import staj.ordermanagementsystemapi.entities.concretes.Admin;
import staj.ordermanagementsystemapi.entities.concretes.Customer;

public interface AdminService {
    List<Admin> getAllAdmins();
    List<Customer> getAllCustomers();
    Admin getAdminById(Integer id);
    Admin saveAdmin(Admin adminDto);
    Admin updateAdmin(Integer id, Admin adminDto);
    void deleteAdmin(Integer id);
}
