package com.example.inovation.controller;


import com.example.inovation.controller.form.MemberLoginForm;
import com.example.inovation.domain.User;
import com.example.inovation.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import com.example.inovation.controller.form.MemberForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") //CORS ERROR 해결

public class UserController {

    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> createMember(@RequestBody @Valid MemberForm form, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("400");
        }
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(form.getPassword());
        user.setName(form.getName());
        userService.join(user);
        return ResponseEntity.ok("200");
    }

    // 회원탈퇴
    /*@DeleteMapping("/{memberId}")
    public ResponseEntity<String> deleteMember(@PathVariable("memberId") Long memberId) {
        boolean isDeleted = userService.deleteMember(memberId);
        if (isDeleted) {
            return ResponseEntity.ok("User deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to delete member.");
        }
    }*/

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<String> loginMember(@RequestBody MemberLoginForm form, HttpSession session) {
        User user = userService.login(form.getEmail(), form.getPassword());
        if(user != null) {
            session.setAttribute("user", user);
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
