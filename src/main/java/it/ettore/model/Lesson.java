package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String title;
    private String description;
    @Column(nullable = false)
    private String content;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public Lesson(String title, String description, String content, Course course) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.course = course;
    }

    @Override
    public String toString() {
        return String.format("Lesson{id=%d,title=%s}", id, title);
    }

}
