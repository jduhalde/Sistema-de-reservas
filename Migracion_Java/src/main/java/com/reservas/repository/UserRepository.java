package com.reservas.repository;

import com.reservas.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Spring Data JPA entenderá este nombre de método y creará automáticamente
    // la consulta para buscar un usuario por su dirección de email.
    Optional<User> findByEmail(String email);

}
