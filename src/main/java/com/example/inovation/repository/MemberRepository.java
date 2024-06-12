package com.example.inovation.repository;

import com.example.inovation.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<User, Long> {
    List<User> findByEmail(String email);
    List<User> findByName(String name);

    //@Query("SELECT m FROM User m WHERE m.email = ?1 AND m.password = ?2")
    User findByEmailAndPassword(String email, String password);

}
