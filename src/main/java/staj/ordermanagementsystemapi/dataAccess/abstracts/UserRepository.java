package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;

import staj.ordermanagementsystemapi.entities.concretes.User;

public interface UserRepository extends JpaRepository<User, Integer> {

	
	User findByUsername(String username);
}
