package it.ettore.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

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
    @Column(unique = true)
    private String email;
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

    public static String hashPsw(String psw) {
        try {
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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null; //Todo return null in case it fails or we can return un-hashed password?
    }

    static void assertEmail(String email) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
        if (!Pattern.compile(regexPattern).matcher(email).matches()) {
            throw new IllegalArgumentException("Email invalida");
        }
    }

    public void setEmail(String email) {
        assertEmail(email);
        this.email = email;
    }

    public void setPswHash(String pswHash) {
        this.pswHash = hashPsw(pswHash);
    }

    @Override
    public String toString() {
        return String.format("User{id=%d,email=%s,role=%s}", id, email, role.toString());
    }
}
