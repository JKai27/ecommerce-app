package shopeazy.com.ecommerceapp.service.serviceImplementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import shopeazy.com.ecommerceapp.model.document.DatabaseSequence;

@Service
@RequiredArgsConstructor
public class SequenceGeneratorService {
    private final MongoOperations mongoOperations;

    public long generateSequence(String sequenceName) {
        DatabaseSequence counter = mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(sequenceName)),
                new Update().inc("sequence", 1),
                FindAndModifyOptions.options().returnNew(true).upsert(true),
                DatabaseSequence.class
        );
        return counter != null ? counter.getSequence() : 1;
    }

    public void resetSequence(String seqName) {
        mongoOperations.findAndModify(
                Query.query(Criteria.where("_id").is(seqName)),
                new Update().set("sequence", 0),
                FindAndModifyOptions.options().upsert(true).returnNew(true),
                DatabaseSequence.class
        );
    }
}
