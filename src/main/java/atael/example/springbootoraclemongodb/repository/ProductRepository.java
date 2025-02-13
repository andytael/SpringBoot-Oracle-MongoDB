package atael.example.springbootoraclemongodb.repository;

import atael.example.springbootoraclemongodb.dto.ProductStats;
import atael.example.springbootoraclemongodb.model.Product;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {

  /**
   * Returns products by category
   * @param category Category name
   * @return List of Products
   */
  List<Product> findByCategory(String category);

  /**
   * Custom query with projection: Returns name and price for products with price > ?0
   * SQL: select p.data.name, p.data.price from products p where p.data.price > :price
   * @param price Price
   * @return A List of products withe price higher than param price
   */
  @Query(value = "{ 'price': { '$gt': ?0 } }", fields = "{ 'name': 1, 'price': 1, '_id': 0 }")
  List<Product> findNameAndPriceByPriceGreaterThan(Double price);

  /**
   * Aggregation method: Group by category and compute average price
   * SQL: select p.data.category, avg(p.data.price) from products p group by p.data.category;
   * @return A List of categories with its average price
   */
  @Aggregation(pipeline = {
      "{ '$group': { '_id': '$category', 'averagePrice': { '$avg': '$price' } } }",
      "{ '$project': { '_id': 0, 'category': '$_id', 'averagePrice': 1 } }"
  })
  List<ProductStats> aggregateAveragePriceByCategory();
}
