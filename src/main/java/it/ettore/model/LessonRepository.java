package it.ettore.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Iterator;
import java.util.Optional;

public interface LessonRepository extends CrudRepository<Lesson, Long> {
    Optional<Lesson> findById(long id);

    Iterator<Lesson> findByCourse(Course course);
}
