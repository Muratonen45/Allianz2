package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer>{

	Customer findByMail(String username);
}
