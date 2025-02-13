package atael.example.springbootoraclemongodb.controller;

import atael.example.springbootoraclemongodb.dto.ProductStats;
import atael.example.springbootoraclemongodb.model.Product;
import atael.example.springbootoraclemongodb.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api/v1/product")
@Slf4j
public class ProductController {

  private final ProductRepository productRepository;

  public ProductController(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  /**
   * Get all products with paging and sorting
   * Example: GET /api/v1/product?page=0&size=5&sortBy=price&order=desc
   * @param page Which page, default 0
   * @param size Page size, default 10
   * @param sortBy Which column to sort by, default name
   * @param order Sort order, default ascending
   * @return Page of products
   *
   * curl http://localhost:8080/api/v1/product?page=0&size=40&sortBY=name&order=asc
   */
  @GetMapping
  public ResponseEntity<Page<Product>> getAllProducts(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "name") String sortBy,
      @RequestParam(defaultValue = "asc") String order) {

    Sort sort = order.equalsIgnoreCase("asc")
        ? Sort.by(sortBy).ascending()
        : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    Page<Product> productPage = productRepository.findAll(pageable);
    return ResponseEntity.ok(productPage);
  }

  /**
   * Get a single product by ID
   * @param id The ID of the product
   * @return Returns the product data
   *
   * curl http://localhost:8080/api/v1/product/1
   */
  @GetMapping("/{id}")
  public ResponseEntity<Product> getProductById(@PathVariable String id) {
    Optional<Product> productOpt = productRepository.findById(id);
    return productOpt.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Get products by category (derived method)
   * @param category Category name
   * @return List of Products if they exists
   *
   * curl http://localhost:8080/api/v1/product/category/Electronics
   */
  @GetMapping("/category/{category}")
  public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
    List<Product> products = productRepository.findByCategory(category);
    if (products.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(products);
  }

  /**
   * Projection query: Get only name and price for products with price > given value
   * @param price The value of price
   * @return Returns a list of Products if they exists
   *
   * curl http://localhost:8080/api/v1/product/priceGreaterThan/50
   */
  @GetMapping("/priceGreaterThan/{price}")
  public ResponseEntity<List<Product>> getNameAndPriceByPriceGreaterThan(@PathVariable Double price) {
    List<Product> products = productRepository.findNameAndPriceByPriceGreaterThan(price);
    if (products.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(products);
  }

  /**
   * Get average price by category
   * @return Returns a List of categories with average price
   *
   * curl http://localhost:8080/api/v1/product/averagePriceByCategory
   */
  @GetMapping("/averagePriceByCategory")
  public ResponseEntity<List<ProductStats>> getAveragePriceByCategory() {
    List<ProductStats> productStats = productRepository.aggregateAveragePriceByCategory();
    if (productStats.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(productStats);
  }

  /**
   * Create a Product
   * @param product Product JSON object
   * @return Returns URI of the created product
   *
   * curl -X POST -H "Content-Type: application/json" \
   * -d '{ "productId":"1", "name":  "Test product", "category": "Mechanical", "price": 4 }' \
   * http://localhost:8080/api/v1/product
   */
  @PostMapping
  public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    Product saved = productRepository.save(product);
    URI location = fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(saved.getProductId())
        .toUri();
    return ResponseEntity.created(location).build();
  }

  /**
   * Update a product by ID
   * @param id Product ID
   * @param productDetails Details of the product that needs updates
   * @return The URI of the updated product
   *
   * curl -v -X PUT -H "Content-Type: application/json" -d \
   * '{ "name":  "Kalle", "category": "Mechanical", "price": 40 }' \
   * http://localhost:8080/api/v1/product/1
   */
  @PutMapping("/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable String id,
                                               @RequestBody Product productDetails) {
    Optional<Product> productOpt = productRepository.findById(id);
    if (productOpt.isPresent()) {
      Product product = productOpt.get();
      product.setName(productDetails.getName());
      product.setPrice(productDetails.getPrice());
      product.setCategory(productDetails.getCategory());
      Product updated = productRepository.save(product);
      URI location = fromCurrentRequest()
          .buildAndExpand(updated.getProductId())
          .toUri();
      return ResponseEntity.created(location).build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * Delete a product by ID
   * @param id The Product ID to be deleted
   * @return Httpstatus no content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
    productRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

}
