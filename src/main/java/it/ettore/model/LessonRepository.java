package it.ettore.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Optional<Lesson> findById(long id);

    Iterable<Lesson> findByCourse(Course course);
}
