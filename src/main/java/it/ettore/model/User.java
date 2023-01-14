package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A register user of Ettore is either a professor or a student. A professor teaches many courses and a student is
 * subscribed to many course. A student is only taught by one professor but is subscribed to by many students.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(nullable = false)
    private String pswHash;
    @Column(nullable = false)
    private Role role;

    // For the professor
    @OneToMany(mappedBy = "professor")
    private List<Course> coursesTaught = new ArrayList<>();

    // For the student
    @ManyToMany(mappedBy = "studentsRequesting")
    private List<Course> coursesRequesting = new ArrayList<>();
    @ManyToMany(mappedBy = "studentsJoined")
    private List<Course> coursesJoined = new ArrayList<>();

    public User(String firstName, String lastName, String email, String psw, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        assertEmail(email);
        this.email = email;
        assertPassword(psw);
        this.pswHash = hashPsw(psw);
        this.role = role;
    }

    @SneakyThrows
    public static String hashPsw(String psw) {
        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] salt = new byte[16];
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        salt = "kokarkokarkokar1".getBytes();
        md.update(salt);
        byte[] hashedPsw = md.digest(psw.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashedPsw) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    static void assertEmail(String email) {
        String regexPattern = "^[\\w.]+@[\\w.]+$";
        if (!Pattern.compile(regexPattern).matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    static void assertPassword(String psw) {
        if (psw.length() < 8) {
            throw new IllegalArgumentException("Password is too short");
        }
    }

    public void setEmail(String email) {
        assertEmail(email);
        this.email = email;
    }

    public void setPassword(String psw) {
        assertPassword(psw);
        this.pswHash = hashPsw(psw);
    }

    public String fullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return String.format("User{id=%d,email=%s,role=%s}", id, email, role.toString());
    }

    public enum Role {
        PROFESSOR, STUDENT,
    }
}
