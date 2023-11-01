package staj.ordermanagementsystemapi.api.controller;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import staj.ordermanagementsystemapi.core.utilities.mappers.JWTGenerator;
import staj.ordermanagementsystemapi.dataAccess.abstracts.RoleRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.UserEntityRepository;
import staj.ordermanagementsystemapi.entities.concretes.Role;
import staj.ordermanagementsystemapi.entities.concretes.UserEntity;
import staj.ordermanagementsystemapi.entities.dto.AuthResponseDTO;
import staj.ordermanagementsystemapi.entities.dto.LoginDto;
import staj.ordermanagementsystemapi.entities.dto.RegisterDto;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

	 private AuthenticationManager authenticationManager;
	    private UserEntityRepository userEntityRepository;
	    private RoleRepository roleRepository;
	    private PasswordEncoder passwordEncoder;
	    private JWTGenerator jwtGenerator;
	    
	    
	    
	    @Autowired
	    public AuthController(AuthenticationManager authenticationManager, 
	    		UserEntityRepository userEntityRepository,
	                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, 
	                          JWTGenerator jwtGenerator) {
	        this.authenticationManager = authenticationManager;
	        this.userEntityRepository = userEntityRepository;
	        this.roleRepository = roleRepository;
	        this.passwordEncoder = passwordEncoder;
	        this.jwtGenerator = jwtGenerator;
	       
	    }
	    
	    
	    
	    
	    
	    @PostMapping("login")
	    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDto loginDto){
	        Authentication authentication = authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(
	                loginDto.getUsername(),
	                loginDto.getPassword()));
	        SecurityContextHolder.getContext().setAuthentication(authentication);
	        String token = jwtGenerator.generateToken(authentication);
	        return new ResponseEntity<>(new AuthResponseDTO(token), HttpStatus.OK);
	    }
	    
	    
	    
	    @PostMapping("register")
	    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto) {
	        if (userEntityRepository.existsByUsername(registerDto.getUsername())) {
	            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
	        }

	        UserEntity user = new UserEntity();
	        user.setUsername(registerDto.getUsername());
	        user.setPassword(passwordEncoder.encode((registerDto.getPassword())));

	        Optional<Role> role = roleRepository.findByName("User");
	        if (role.isPresent()) {
	            user.setRoles(Collections.singletonList(role.get()));
	            userEntityRepository.save(user);
	            return new ResponseEntity<>("User registered success!", HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>("Role not found!", HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	    
	
}
