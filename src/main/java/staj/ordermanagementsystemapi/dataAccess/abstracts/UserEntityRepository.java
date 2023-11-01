package staj.ordermanagementsystemapi.dataAccess.abstracts;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import staj.ordermanagementsystemapi.entities.concretes.UserEntity;

public interface UserEntityRepository extends JpaRepository<UserEntity, Integer> {
	
    Optional<UserEntity> findByUsername(String username);
    Boolean existsByUsername(String username);
}
