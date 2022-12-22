package it.ettore.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public User() {
    }

    @Override
    public String toString() {
        return String.format("User{id=%d}", id);
    }

    // Id getter
    public long getId() {
        return id;
    }
}
