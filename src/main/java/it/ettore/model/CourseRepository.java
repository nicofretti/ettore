package it.ettore.model;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findById(long id);

    Optional<List<Course>> findByProfessor(User user);
}
