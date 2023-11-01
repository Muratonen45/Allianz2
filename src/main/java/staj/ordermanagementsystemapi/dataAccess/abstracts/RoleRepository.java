package staj.ordermanagementsystemapi.dataAccess.abstracts;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import staj.ordermanagementsystemapi.entities.concretes.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
