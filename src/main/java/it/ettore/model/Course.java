package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Course {
    public enum Category {
        Maths,
        Science,
        History,
        Geography,
        Art,
        Music,
        Languages,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;
    private String description;
    private int startingYear;
    private Category category;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "professor_id", nullable = false)
    private User professor;

    public Course(String name, String description, int startingYear, Category category, User professor) {
        this.name = name;
        this.description = description;
        this.startingYear = startingYear;
        this.category = category;
        this.professor = professor;
    }

    public String formatPeriod() {
        return String.format("(%d/%d)", startingYear, startingYear + 1);
    }

    @Override
    public String toString() {
        return String.format("Course{id=%d,name=%s}", id, name);
    }
}
