package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import staj.ordermanagementsystemapi.entities.concretes.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
