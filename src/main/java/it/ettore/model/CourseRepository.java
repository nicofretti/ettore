package it.ettore.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    Optional<Course> findById(long id);

    Iterable<Course> findByProfessor(User user);

}
