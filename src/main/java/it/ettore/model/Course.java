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
public class Course {
    public enum Category {
        Maths, Science, History, Geography, Art, Music, Languages,
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

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Lesson> lessons = new ArrayList<>();

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
}
