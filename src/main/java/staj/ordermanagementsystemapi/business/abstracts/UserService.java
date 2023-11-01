package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;
import staj.ordermanagementsystemapi.entities.concretes.User;

public interface UserService {

	List<User> getAllUsers();
    User getUserById(Integer id);
    User saveUser(User UserDto);
    User updateUser(Integer id, String updatedUsername, String updatedPassword,String updatedRole);
    void deleteUser(Integer id);
}

