package br.com.busapi.impl.lines.repository;

import br.com.busapi.impl.lines.models.Line;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinesRepository extends MongoRepository<Line, String> {

    Line findById(Integer id);

    List<Line> findAllByCoordinatesNear(Point point, Distance distance);

    List<Line> findAllByNameContains(String name);

    Line findByCode(String code);
}
