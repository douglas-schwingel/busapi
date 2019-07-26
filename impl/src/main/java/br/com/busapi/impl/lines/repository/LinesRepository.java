package br.com.busapi.impl.lines.repository;

import br.com.busapi.impl.lines.models.Line;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LinesRepository extends MongoRepository<Line, String> {

}
