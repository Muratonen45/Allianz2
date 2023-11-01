package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;

import staj.ordermanagementsystemapi.entities.concretes.Manager;

public interface ManagerService {
    List<Manager> getAllManagers();
    Manager getManagerById(Integer id);
    Manager saveManager(Manager managerDto);
    Manager updateManager(Integer id, String updatedUsername, String updatedPassword);
    void deleteManager(Integer id);
}
