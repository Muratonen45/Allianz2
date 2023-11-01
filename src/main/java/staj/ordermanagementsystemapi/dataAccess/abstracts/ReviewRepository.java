package staj.ordermanagementsystemapi.dataAccess.abstracts;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Product;
import staj.ordermanagementsystemapi.entities.concretes.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer>{

	
	    List<Review> findByProduct(Product product);
	    List<Review> findByCustomer(Customer customer);
}
