package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer>{

	Admin findByUsername(String username);
}
