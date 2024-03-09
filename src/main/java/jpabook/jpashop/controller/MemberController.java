package jpabook.jpashop.controller;

import jakarta.validation.Valid;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("members/new")
    public String createForm(Model model) {
        // controller 에서 view 로 넘어갈 때 이 데이터를 담아서 넘김
        // validation 같은 걸 해주기 때문에 비어있어도 memberForm 가지고 감
        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";
    }

    @PostMapping("members/new")
    // @Valid
    // : 사용하면 javax.validation 쓴다는 걸 스프링이 인지해서
    // MemberForm 에 작성한 @NotEmpty() 를 validation 할 수 있음
    // BindingResult
    // : 원래는 form 에서 에러가 발생하면, controller 에 코드 안넘어가고 튕기는데,
    // validate 한 거 뒤에 BindingResult 이게 있으면, 오류가 여기에 담겨서 아래 코드가 실행 됨
    public String create(@Valid MemberForm form, BindingResult result) {

        if (result.hasErrors()) {
            // 어떤 에러 있는지 여기에 뿌릴 수 있음
            return "members/createMemberForm";
        }

        Address address = new Address(form.getCity(), form.getStreet(), form.getZipcode());

        Member member = new Member();
        member.setName(form.getName());
        member.setAddress(address);

        memberService.join(member);

        // 첫 번째 페이지로 넘어감
        return "redirect:/";
    }

    @GetMapping("members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);

        return "members/memberList";
    }


}
