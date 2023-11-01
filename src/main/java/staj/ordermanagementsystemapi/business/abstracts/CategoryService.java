package staj.ordermanagementsystemapi.business.abstracts;

import java.util.Date;
import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.CategoryDto;

public interface CategoryService {
    List<CategoryDto> getAllCategories();
    CategoryDto getCategoryById(Integer id);
    CategoryDto saveCategory(CategoryDto categoryDTO);
    CategoryDto updateCategory(Integer id, String updatedName, String updatedDetails, Date updatedTimestamp);
    void deleteCategory(Integer id);
}
