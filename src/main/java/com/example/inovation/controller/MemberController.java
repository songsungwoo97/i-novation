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

    // Endpoint for creating a new member
    @PostMapping("/signup")
    public ResponseEntity<String> createMember(@RequestBody @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            // Handle validation errors and return appropriate response
            // For example, you can return a ResponseEntity with appropriate HTTP status code and error message
            return ResponseEntity.badRequest().body("Validation failed. Please check your input.");
        }
        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        memberService.join(member);
        // Return a ResponseEntity with appropriate HTTP status code and success message
        return ResponseEntity.ok("Member created successfully.");
    }

    // Endpoint for logging in
    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestParam("email") String email, @RequestParam("password") String password) {
        Member member = memberService.login(email, password);
        if(member != null) {
            // Perform login logic and return appropriate response
            // For example, you can return a ResponseEntity with appropriate HTTP status code and success message
            return ResponseEntity.ok("Logged in successfully.");
        }
        else {
            // Return a ResponseEntity with appropriate HTTP status code and error message
            return ResponseEntity.badRequest().body("Email or password is incorrect.");
        }
    }

    // Endpoint for logging out
    @PostMapping("/logout")
    public ResponseEntity<String> logoutMember(HttpSession session) {
        session.invalidate(); // Invalidate session to perform logout
        // Return a ResponseEntity with appropriate HTTP status code and success message
        return ResponseEntity.ok("Logged out successfully.");
    }
}
