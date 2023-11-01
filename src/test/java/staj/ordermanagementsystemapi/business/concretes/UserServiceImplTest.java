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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import staj.ordermanagementsystemapi.business.abstracts.ManagerService;
import staj.ordermanagementsystemapi.business.abstracts.UserService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ManagerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.UserRepository;
import staj.ordermanagementsystemapi.entities.concretes.Manager;
import staj.ordermanagementsystemapi.entities.concretes.User;

 class UserServiceImplTest {

	
	 private UserService userService;
	    private UserRepository userRepository;
	    private ModelMapper modelMapper;
	    private BCryptPasswordEncoder passwordEncoder;

	    @BeforeEach
	    public void setUp() {
	        userRepository = mock(UserRepository.class);
	        modelMapper = new ModelMapper();
	        passwordEncoder = new BCryptPasswordEncoder();
	        userService = new UserServiceImpl(userRepository, modelMapper, passwordEncoder);
	    }
	    
	    @Test
	    public void UserService_GetAll_ReturnsAllUsers() {
	        // Arrange
	        List<User> users = new ArrayList<>();
	        users.add(new User(1, "User1", "password1", new Date(),"Role1"));
	        users.add(new User(2, "User2", "password2", new Date(),"Role2"));
	        when(userRepository.findAll()).thenReturn(users);

	        // Act
	        List<User> result = userService.getAllUsers();

	        // Assert
	        assertNotNull(result);
	        assertEquals(users.size(), result.size());
	    }

	    @Test
	    public void UserService_GetById_ValidUserId_ReturnsUser() {
	        // Arrange
	        int userId = 1;
	        User user = new User(userId, "User1", "password1", new Date(),"Role1");
	        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

	        // Act
	        User result = userService.getUserById(userId);

	        // Assert
	        assertNotNull(result);
	        assertEquals(user.getId(), result.getId());
	        assertEquals(user.getUsername(), result.getUsername());
	        assertEquals(user.getPassword(), result.getPassword());
	        assertEquals(user.getRole(), result.getRole());
	    }

	    @Test
	    public void UserService_GetById_UserNotFoundThrowsResourceNotFoundException() {
	        // Arrange
	        int userId = 1;
	        when(userRepository.findById(userId)).thenReturn(Optional.empty());

	        // Act & Assert
	        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));
	    }
	    
	    
	    
	    
	    @Test
	    public void UserService_SaveUser_ValidUser_ReturnsSavedUser() {
	        // Arrange
	        User user = new User(1, "User1", "password1", new Date(),"Role1");
	        when(userRepository.save(user)).thenReturn(user);

	        // Act
	        User result = userService.saveUser(user);

	        // Assert
	        assertNotNull(result);
	        assertEquals(user.getId(), result.getId());
	        assertEquals(user.getUsername(), result.getUsername());
	        assertEquals(user.getPassword(), result.getPassword());
	        assertEquals(user.getRole(), result.getRole());
	    }

	    @Test
	    public void UserService_SaveUser_DuplicateUsernameThrowsIllegalArgumentException() {
	        // Arrange
	        User user = new User(1, "User1", "password1", new Date(),"Role1");
	        when(userRepository.save(any())).thenThrow(DataIntegrityViolationException.class);

	        // Act & Assert
	        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(user));
	    }


	    @Test
	    public void UserService_UpdateUser_UserNotFoundThrowsResourceNotFoundException() {
	        // Arrange
	        int userId = 1;
	        String updatedUsername = "UpdatedUser";
	        String updatedPassword = "updatedPassword";
	        String updatedRole = "updatedRole";
	        when(userRepository.findById(userId)).thenReturn(Optional.empty());

	        // Act & Assert
	        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userId, updatedUsername, updatedPassword,updatedRole));
	    }

	    @Test
	    public void UserService_Delete_ValidUserId_DeletesUser() {
	        // Arrange
	        int userId = 1;
	        User existingUser = new User(userId, "User1", "password1", new Date(),"Role1");
	        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

	        // Act
	        assertDoesNotThrow(() -> userService.deleteUser(userId));

	        // Assert
	        verify(userRepository, times(1)).deleteById(userId);
	    }

	    @Test
	    public void UserService_Delete_UserNotFoundThrowsResourceNotFoundException() {
	        // Arrange
	        int userId = 1;
	        when(userRepository.findById(userId)).thenReturn(Optional.empty());

	        // Act & Assert
	        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));
	    }
	}

	    
	    
	    
	    
	    
	    
	    
	    

