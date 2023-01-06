package it.ettore.model;

import javax.persistence.*;

@Entity
public class Course {
    public static enum Category {
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

    @ManyToOne
    @JoinColumn(name="professor_id", nullable = false)
    private User professor;

    public Course() {

    }

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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getStartingYear() {
        return startingYear;
    }

    public Category getCategory() {
        return category;
    }

    public User getProfessor() {
        return professor;
    }

    @Override
    public String toString() {
        return String.format("Course{id=%d}", id);
    }
}
