package com.example.inovation.controller;


import com.example.inovation.domain.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import com.example.inovation.controller.form.MemberForm;
import com.example.inovation.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /*회원가입*/
    @GetMapping("/members/new")
    public String getCreatePage(Model model){
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    public String create(@Valid MemberForm form, BindingResult result){
        if (result.hasErrors()) {
            return "members/createMemberForm";
        }
        Member member = new Member();
        member.setEmail(form.getEmail());
        member.setPassword(form.getPassword());
        member.setName(form.getName());
        memberService.join(member);
        return "redirect:/";
    }
    /*회원가입 끝*/


    /*로그인*/
    @GetMapping("/members/login")
    public String getLoginpage() {

        return "members/loginMemberForm";
    }

    @PostMapping("/members/login")
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session, Model model) {

        Member member = memberService.login(email, password);

        if(member != null) {

            session.setAttribute("member", member); // 세션에 로그인 정보 저장

            return "redirect:/";
        }
        else {

            model.addAttribute("error", "이메일 또는 비밀번호가 틀렸습니다.");

            return "members/loginMemberForm";
        }
    }
    /*로그인 끝*/

    /*로그아웃*/
    @GetMapping("/members/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 무효화
        return "redirect:/";
    }
    /*로그인*/
}
