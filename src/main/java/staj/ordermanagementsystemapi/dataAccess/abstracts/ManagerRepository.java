package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Manager;

@Repository
public interface ManagerRepository extends JpaRepository<Manager, Integer>{

	 Manager findByUsername(String username);
}
