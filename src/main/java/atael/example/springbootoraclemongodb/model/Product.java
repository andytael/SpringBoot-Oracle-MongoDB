package atael.example.springbootoraclemongodb.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
@Data
public class Product {

  // In MongoDB, each document stored in a standard collection requires a unique _id field that acts as a primary key. 
  // If an inserted document omits the _id field, the MongoDB driver automatically generates an ObjectId for the _id field.
  
  @Id
  private String productId; // Mapped to _id in the document
  private String name;
  private String category;
  private Double price;
}
