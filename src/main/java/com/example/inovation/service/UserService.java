package com.example.inovation.service;

import com.example.inovation.domain.User;
import com.example.inovation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class UserService {

    private final MemberRepository memberRepository;


    /*회원 가입*/
    @Transactional
    public Long join(User user) {

        validateDuplicateMember(user);
        memberRepository.save(user);

        return user.getId();
    }

    /*로그인*/

    public User login(String email, String password) {
        User user = memberRepository.findByEmailAndPassword(email, password);
        if(user == null) return null;

        return user;
    }


    /*중복 확인*/
    private void validateDuplicateMember(User user) {
        List<User> findUsers = memberRepository.findByEmail(user.getEmail());
        if (!findUsers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
}
