package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "startingYear"})})
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String name;
    private String description;
    @Column(nullable = false)
    private int startingYear;
    @Column(nullable = false)
    private Category category;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "professor_id")
    private User professor;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "course_request", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> studentsRequesting;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "course_join", joinColumns = @JoinColumn(name = "course_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> studentsJoined;

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

    public String getIcon() {
        switch (category) {
            case Maths:
                return "fa fa-calculator";
            case Science:
                return "fa fa-flask";
            case History:
                return "fa fa-book";
            case Geography:
                return "fa fa-globe";
            case Art:
                return "fa fa-palette";
            case Music:
                return "fa fa-music";
            case Languages:
                return "fa-language";
            default:
                return "fa fa-question";
        }
    }

    @Override
    public String toString() {
        return String.format("Course{id=%d,name=%s}", id, name);
    }

    public void requestJoin(User student) {
        if (studentsJoined != null && studentsJoined.contains(student)) {
            throw new IllegalStateException("This student has already joined, no need to request");
        }

        if (studentsRequesting == null) {
            studentsRequesting = new ArrayList<>();
        } else if (studentsRequesting.contains(student)) {
            throw new IllegalStateException("This student has already requested to join");
        }

        studentsRequesting.add(student);
    }

    public void acceptStudent(User student) {
        if (studentsRequesting == null || !studentsRequesting.contains(student)) {
            throw new IllegalStateException("This student has no pending request to join this course");
        }

        if (studentsJoined == null) {
            studentsJoined = new ArrayList<>();
        } else if (studentsJoined.contains(student)) {
            throw new IllegalStateException("This student has already been accepted to join the course");
        }

        studentsRequesting.remove(student);
        studentsJoined.add(student);
    }

    public void rejectStudent(User student) {
        if (studentsRequesting == null || !studentsRequesting.contains(student)) {
            throw new IllegalStateException("This student has no pending request to join this course");
        }

        if (studentsJoined == null) {
            studentsJoined = new ArrayList<>();
        } else if (studentsJoined.contains(student)) {
            throw new IllegalStateException("This student has already been accepted to join the course");
        }

        studentsRequesting.remove(student);
    }

    public void removeStudent(User student) {
        if (studentsJoined == null || !studentsJoined.contains(student)) {
            throw new IllegalStateException("This student hasn't joined the course");
        }

        studentsJoined.remove(student);
    }

    public enum Category {
        Maths,
        Science,
        History,
        Geography,
        Art,
        Music,
        Languages;

        public static Category fromString(String s) {
            switch (s) {
                case "Maths":
                    return Maths;
                case "Science":
                    return Science;
                case "History":
                    return History;
                case "Geography":
                    return Geography;
                case "Art":
                    return Art;
                case "Music":
                    return Music;
                case "Languages":
                    return Languages;
                default:
                    return null;
            }
        }

        public String toString() {
            switch (this) {
                case Maths:
                    return "Maths";
                case Science:
                    return "Science";
                case History:
                    return "History";
                case Geography:
                    return "Geography";
                case Art:
                    return "Art";
                case Music:
                    return "Music";
                case Languages:
                    return "Languages";
                default:
                    return null;
            }
        }
    }
}
