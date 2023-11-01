package staj.ordermanagementsystemapi.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import staj.ordermanagementsystemapi.business.abstracts.UserService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.entities.concretes.User;

public class UserControllerTest {

	 private UserController userController;
	    private UserService userService;

	    @BeforeEach
	    public void setUp() {
	        userService = mock(UserService.class);
	        userController = new UserController(userService);
	    }
	    
	     
	    
	    
	    
	    @Test
	    void getAllUsers_ReturnsAllUsersSuccessfully() {
	        // Arrange
	        List<User> expectedUsers = new ArrayList<>();
	        expectedUsers.add(new User(1, "User 1", "password1",null,"Role1"));
	        expectedUsers.add(new User(2, "User 2", "password2",null,"Role2"));
	        when(userService.getAllUsers()).thenReturn(expectedUsers);

	        // Act
	        ResponseEntity<List<User>> responseEntity = userController.getAllUsers();

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(expectedUsers.size(), responseEntity.getBody().size());
	    }

	    @Test
	    void getUserById_ValidUserId_ReturnsUserSuccessfully() {
	        // Arrange
	        int userId = 1;
	        User expectedUser = new User(userId, "Test User", "testpassword", null,"testRole");
	        when(userService.getUserById(userId)).thenReturn(expectedUser);

	        // Act
	        ResponseEntity<User> responseEntity = userController.getUserById(userId);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(expectedUser.getId(), responseEntity.getBody().getId());
	        assertEquals(expectedUser.getUsername(), responseEntity.getBody().getUsername());
	    }

	    @Test
	    void getUserById_UserNotFound_ReturnsNotFound() {
	        // Arrange
	        int userId = 1;
	        when(userService.getUserById(userId)).thenReturn(null);

	        // Act
	        ResponseEntity<User> responseEntity = userController.getUserById(userId);

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	        assertNull(responseEntity.getBody());
	    }
	    
	      
	    
	    @Test
	    void addUser_ValidUser_ReturnsCreatedUser() {
	        // Arrange
	        User newUser = new User(null, "New User", "newpassword", null,"new Role");
	        User expectedSavedUser = new User(1, "New User", "newpassword", null,"new Role");
	        when(userService.saveUser(newUser)).thenReturn(expectedSavedUser);

	        // Act
	        ResponseEntity<User> responseEntity = userController.addUser(newUser);

	        // Assert
	        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(expectedSavedUser.getId(), responseEntity.getBody().getId());
	        assertEquals(expectedSavedUser.getUsername(), responseEntity.getBody().getUsername());
	    }

	    @Test
	    void updateUser_ValidUser_ReturnsUpdatedUser() {
	        // Arrange
	        int userId = 1;
	        User updateUser = new User(userId, "Updated User", "updatedpassword", null,"updatedRole");
	        when(userService.updateUser(userId, updateUser.getUsername(), updateUser.getPassword(),updateUser.getRole()))
	                .thenReturn(updateUser);

	        // Act
	        ResponseEntity<User> responseEntity = userController.updateUser(userId, updateUser.getUsername(), updateUser.getPassword(),updateUser.getRole());

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	        assertNotNull(responseEntity.getBody());
	        assertEquals(updateUser.getId(), responseEntity.getBody().getId());
	        assertEquals(updateUser.getUsername(), responseEntity.getBody().getUsername());
	        assertEquals(updateUser.getPassword(), responseEntity.getBody().getPassword());
	        assertEquals(updateUser.getRole(), responseEntity.getBody().getRole());
	    }

	    @Test
	    void updateUser_UserNotFound_ReturnsNotFound() {
	        // Arrange
	        int userId = 1;
	        User updatedUser = new User(userId, "Updated User", "updatedpassword", null,"updatedRole");
	        when(userService.updateUser(userId, updatedUser.getUsername(), updatedUser.getPassword(), updatedUser.getRole()))
	                .thenThrow(new ResourceNotFoundException("user", "id", userId));

	        // Act
	        ResponseEntity<User> responseEntity = userController.updateUser(userId, updatedUser.getUsername(), updatedUser.getPassword(), updatedUser.getRole());

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	        assertNull(responseEntity.getBody());
	    }

	    @Test
	    void deleteUser_ValidUserId_ReturnsNoContent() {
	        // Arrange
	        int userId = 1;

	        // Act
	        ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

	        // Assert
	        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
	    }

	    @Test
	    void deleteUser_UserNotFound_ReturnsNotFound() {
	        // Arrange
	        int userId = 1;
	        doThrow(new ResourceNotFoundException("User", "id", userId)).when(userService).deleteUser(userId);

	        // Act
	        ResponseEntity<Void> responseEntity = userController.deleteUser(userId);

	        // Assert
	        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	    }
	}


