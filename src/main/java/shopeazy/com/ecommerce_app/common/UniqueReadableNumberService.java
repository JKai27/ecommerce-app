package shopeazy.com.ecommerce_app.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerce_app.seller.model.Counter;

@Service
@RequiredArgsConstructor
public class UniqueReadableNumberService {
    private final MongoTemplate mongoTemplate;

    public int getNextSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Counter counter = mongoTemplate.findOne(query, Counter.class);
        if (counter == null) {
            counter = new Counter();
            counter.setId(seqName);
            counter.setSeq(1);
            mongoTemplate.save(counter);
        }
        Update update = new Update().inc("seq", 1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true).upsert(true);

        counter = mongoTemplate.findAndModify(query,update,options,Counter.class);

        return counter != null ? counter.getSeq() : 1;
    }
    public void resetSequence(String seqName) {
        Query query = new Query(Criteria.where("_id").is(seqName));
        Update update = new Update().set("seq", 0);  // or 1, depending on your starting point
        mongoTemplate.upsert(query, update, Counter.class);
    }
}
