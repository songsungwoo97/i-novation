package com.example.inovation.domain.user.repository;

import com.example.inovation.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
    boolean existsByEmail(String email);

    //@Query("SELECT m FROM User m WHERE m.email = ?1 AND m.password = ?2")
    User findByEmailAndPassword(String email, String password);

}
