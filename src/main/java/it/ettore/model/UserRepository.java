package it.ettore.model;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findById(long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByPswHash(String pswHash);
}
