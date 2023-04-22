package com.example.inovation.repository;

import com.example.inovation.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByEmail(String email);
    List<Member> findByName(String name);

    //@Query("SELECT m FROM Member m WHERE m.email = ?1 AND m.password = ?2")
    Member findByEmailAndPassword(String email, String password);

}
