package atael.example.springbootoraclemongodb.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
public class Product {
  @Id
  private String productId;
  private String name;
  private String category;
  private Double price;
}
