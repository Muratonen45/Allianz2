package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.ManagerService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ManagerRepository;
import staj.ordermanagementsystemapi.entities.concretes.Manager;

@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ManagerServiceImpl(ManagerRepository managerRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.managerRepository = managerRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Manager> getAllManagers() {
        return managerRepository.findAll();
    }

    @Override
    public Manager getManagerById(Integer id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", id));
    }

    @Override
    public Manager saveManager(Manager manager) {
        try {
            String hashedPassword = passwordEncoder.encode(manager.getPassword());
            manager.setPassword(hashedPassword);
            return managerRepository.save(manager);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save the manager:" + e);
        }
    }

    @Override
    public Manager updateManager(Integer id, String updatedUsername, String updatedPassword) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", id));

        try {
            Manager updatedManager = new Manager(id, updatedUsername, updatedPassword, manager.getTimestamp());
            managerRepository.save(updatedManager);
            return updatedManager;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update the manager: " + e);
        }
    }

    @Override
    public void deleteManager(Integer id) {
        Manager manager = managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Manager", "id", id));
        try {
            managerRepository.deleteById(id);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to delete the manager: " + e);
        }
    }
}
