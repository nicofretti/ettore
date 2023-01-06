package it.ettore.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {
    public static enum Role {
        PROFESSOR,
        STUDENT,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;
    private String email;
    // TODO Should this be a bytes array instead?
    private String pswHash;
    private Role role;

    @OneToMany(mappedBy="professor")
    private List<Course> coursesTaught;

    public User() {

    }

    public User(String firstName, String lastName, String email, String psw, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        assertEmail(email);
        this.email = email;
        this.pswHash = hashPsw(psw);
        this.role = role;
    }

    static String hashPsw(String psw) {
        // TODO Actual implementation
        return "some hash";
    }

    static void assertEmail(String email) {
        // TODO check regex
        if (false) {
            throw new IllegalArgumentException("bad email");
        }
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPswHash() {
        return pswHash;
    }

    public Role getRole() {
        return role;
    }

    public List<Course> getCoursesTaught() {
        return coursesTaught;
    }

    @Override
    public String toString() {
        switch (role) {
            case PROFESSOR:
                return String.format("User{id=%d,email=%s,role=Professor,num_courses=%d}", id, email, coursesTaught.size());
            case STUDENT:
                return String.format("User{id=%d,email=%s,role=Student}", id, email);
            default:
                throw new IllegalStateException();
        }
    }
}
