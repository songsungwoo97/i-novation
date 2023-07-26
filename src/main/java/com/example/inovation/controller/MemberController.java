package com.example.inovation.controller;


import com.example.inovation.controller.form.MemberLoginForm;
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
//@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결
public class MemberController {

    private final MemberService memberService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> createMember(@RequestBody @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("400");
        }
        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        memberService.join(member);
        return ResponseEntity.ok("200");
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
    public ResponseEntity<String> loginMember(@RequestBody MemberLoginForm form, HttpSession session) {
        Member member = memberService.login(form.getEmail(), form.getPassword());
        if(member != null) {
            session.setAttribute("member", member);
            return ResponseEntity.ok("200");
        }
        else {
            return ResponseEntity.badRequest().body("400");
        }
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logoutMember(HttpSession session) {
        session.invalidate(); // Invalidate session to perform logout
        // Return a ResponseEntity with appropriate HTTP status code and success message
        return ResponseEntity.ok("200");
    }


}
