package staj.ordermanagementsystemapi.business.abstracts;

import java.util.Date;
import java.util.List;

import staj.ordermanagementsystemapi.entities.dto.ProductDto;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Integer id);
    ProductDto saveProduct(ProductDto productDto);
    ProductDto updateProduct(Integer id,
    		                 String updatedName,
                             Double updatedPrice, 
                             String updatedThumbnail,
                             String updatedDetail, 
                             Integer updatedCategoryId,
                             Integer newQuantity, 
                             Date newDate);
    void deleteProduct(Integer id);
}
