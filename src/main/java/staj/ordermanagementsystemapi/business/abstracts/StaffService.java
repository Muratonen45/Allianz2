package staj.ordermanagementsystemapi.business.abstracts;

import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.StaffDto;

public interface StaffService {

	    List<StaffDto> getAllStaff();
	    StaffDto getStaffById(Integer id);
	    StaffDto saveStaff(StaffDto staffDTO);
	    public StaffDto updateStaff(Integer id, 
	    		                    String updatedName, 
	    		                    String updatedPhone, 
	    		                    String updatedMail, 
	    		                    String updatedPassword,
	    		                    String updatedRole);
	    void deleteStaff(Integer id);
}
