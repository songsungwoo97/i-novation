package com.example.inovation.controller;


import com.example.inovation.domain.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import com.example.inovation.controller.form.MemberForm;
import com.example.inovation.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> createMember(@RequestBody @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("회원가입 실패");
        }
        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        memberService.join(member);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 회원탈퇴
    /*@DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        boolean isDeleted = memberService.deleteMember(memberId);
        if (isDeleted) {
            return ResponseEntity.ok("Member deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete member.");
        }
    }*/

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestParam("email") String email, @RequestParam("password") String password) {
        Member member = memberService.login(email, password);
        if(member != null) {
            return ResponseEntity.ok("로그인 성공");
        }
        else {
            return ResponseEntity.badRequest().body("로그인 실패");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logoutMember(HttpSession session) {
        session.invalidate(); // Invalidate session to perform logout
        // Return a ResponseEntity with appropriate HTTP status code and success message
        return ResponseEntity.ok("로그아웃 성공");
    }


}
