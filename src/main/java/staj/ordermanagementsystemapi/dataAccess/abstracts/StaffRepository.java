package staj.ordermanagementsystemapi.dataAccess.abstracts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import staj.ordermanagementsystemapi.entities.concretes.Staff;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {

	Staff findByMail(String username);
}
