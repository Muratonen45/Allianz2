package staj.ordermanagementsystemapi.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Order;
import staj.ordermanagementsystemapi.entities.concretes.Product;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>{

	 List<Order> findByCustomer(Customer customer);
	    boolean existsByCustomerAndProduct(Customer customer, Product product);
}
