package staj.ordermanagementsystemapi.business.concretes;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import staj.ordermanagementsystemapi.business.abstracts.ReviewService;
import staj.ordermanagementsystemapi.core.exception.ResourceNotFoundException;
import staj.ordermanagementsystemapi.dataAccess.abstracts.CustomerRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.OrderRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ProductRepository;
import staj.ordermanagementsystemapi.dataAccess.abstracts.ReviewRepository;
import staj.ordermanagementsystemapi.entities.concretes.Customer;
import staj.ordermanagementsystemapi.entities.concretes.Product;
import staj.ordermanagementsystemapi.entities.concretes.Review;
import staj.ordermanagementsystemapi.entities.dto.ReviewDto;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewServiceImpl(ProductRepository productRepository, CustomerRepository customerRepository, OrderRepository orderRepository, ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDto getReviewById(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("review", "id", id));
        return modelMapper.map(review, ReviewDto.class);
    }

    @Override
    public ReviewDto saveReview(ReviewDto reviewDTO) {
        Review review = modelMapper.map(reviewDTO, Review.class);
        int customerId = review.getCustomer().getId();
        int productId = review.getProduct().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer", "id", customerId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "id", productId));
        if (orderRepository.existsByCustomerAndProduct(customer, product)){
            Review savedReview = reviewRepository.save(review);
            return modelMapper.map(savedReview, ReviewDto.class);
        } else {
            throw new ResourceNotFoundException("order", "customer id & product id", customerId + " & " + productId);
        }
    }

    @Override
    public ReviewDto updateReview(Integer id, Integer updatedStar, String updatedDescription) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("review", "id", id));

        review.setStar(updatedStar);
        review.setDescription(updatedDescription);

        Review updatedReview = reviewRepository.save(review);
        return modelMapper.map(updatedReview, ReviewDto.class);
    }

    @Override
    public void deleteReview(Integer id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("review", "id", id));
        reviewRepository.deleteById(id);
    }

    @Override
    public List<ReviewDto> getReviewsOfProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "id", productId));
        List<Review> reviews = reviewRepository.findByProduct(product);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public double getAverageReviewOfProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("product", "id", productId));
        List<Review> reviews = reviewRepository.findByProduct(product);
        return reviews.stream()
                .mapToInt(Review::getStar)
                .average()
                .orElse(0.0);
    }

    @Override
    public List<ReviewDto> getReviewsOfCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("customer", "id", customerId));
        List<Review> reviews = reviewRepository.findByCustomer(customer);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewDto.class))
                .collect(Collectors.toList());
    }
}
