package staj.ordermanagementsystemapi.core.utilities.mappers;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.dataAccess.abstracts.UserEntityRepository;
import staj.ordermanagementsystemapi.entities.concretes.Role;
import staj.ordermanagementsystemapi.entities.concretes.UserEntity;


@Service
public class CustomUserDetailsService implements UserDetailsService  {

	
	private UserEntityRepository userEntityRepository;

    @Autowired
    public CustomUserDetailsService(UserEntityRepository userEntityRepository) {
        this.userEntityRepository = userEntityRepository;
    }
	
	
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userEntityRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        Collection<GrantedAuthority> authorities = user.getAuthorities();

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
 
}


