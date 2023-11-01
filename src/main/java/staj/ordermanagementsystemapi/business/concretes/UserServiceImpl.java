package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.UserService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.UserRepository;
import staj.ordermanagementsystemapi.entities.concretes.User;

@Service
public class UserServiceImpl implements UserService {

	
	private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository UserRepository, ModelMapper modelMapper, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = UserRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }
	
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User getUserById(Integer id) {
		return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", id));
	}

	@Override
	public User saveUser(User user) {
		try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            return userRepository.save(user);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to save the user:" + e);
        }
	}

	@Override
	public User updateUser(Integer id, String updatedUsername, String updatedPassword,String updatedRole) {
		User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        try {
            User updatedUser = new User(id, updatedUsername, updatedPassword, user.getTimestamp(),updatedRole);
            userRepository.save(updatedUser);
            return updatedUser;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to update the user: " + e);
        }
	}

	@Override
	public void deleteUser(Integer id) {
		  User user = userRepository.findById(id)
	                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
	        try {
	            userRepository.deleteById(id);
	        } catch (Exception e) {
	            throw new IllegalArgumentException("Failed to delete the user: " + e);
	        }

		
	}

}
