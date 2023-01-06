package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    public enum Role {
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

    @OneToMany(mappedBy="professor", cascade = CascadeType.ALL)
    private List<Course> coursesTaught = new ArrayList<>();

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

    @Override
    public String toString() {
        return String.format("User{id=%d,email=%s,role=%s}", id, email, role.toString());
    }
}
