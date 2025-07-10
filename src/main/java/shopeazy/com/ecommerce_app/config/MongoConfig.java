package shopeazy.com.ecommerce_app.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

@Configuration
public class MongoConfig {

    private final MongoClient mongoClient;

    public MongoConfig(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Bean
    public GridFSBucket gridFSBucket() {
        MongoDatabase database = mongoClient.getDatabase("ecommerce-app");
        return GridFSBuckets.create(database);
    }

    @Bean
    public GridFsTemplate gridFsTemplate(MongoTemplate mongoTemplate) {
        return new GridFsTemplate(mongoTemplate.getMongoDatabaseFactory(), mongoTemplate.getConverter());
    }
}