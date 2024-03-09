package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.aspectj.bridge.MessageUtil.fail;

@RunWith(SpringRunner.class)    // Junit 실행할 때 spring 이랑 같이 엮어서 실행하기 위해
@SpringBootTest // 스프링부트를 띄운 상태로 테스트 하기 위해서. 이거 없으면 @Autowired 다 실패
@Transactional  // 스프링에서 test 코드에 transactional 있으면 transaction commit 을 안하고 rollback 을 해버림
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test
    // @Rollback(false) <- rollback 안하고 눈으로 확인하고 싶으면 이거 이용. 이거 해주면 insert 문 나감
    public void 회원가입() throws Exception {

        // given
        Member member = new Member();
        member.setName("kim");

        // when
        Long savedId = memberService.join(member);
        // 이거 해주면, 영속성 컨텍스트에 member 가 들어가게 되는거임

        // then
        // 저장한거랑 조회한거랑 같은지 다른지 -> true 나옴
        Assertions.assertEquals(member, memberRepository.findOne(savedId));
        // 같은 transaction 안에서 저장하고 조회하면, 영속성 컨텍스트가 똑같음
        // 같은 영속성 컨텍스트 안에서는 id 값(식별자가)이 같으면 같은 entity 로 식별
        // 1차 캐시로 불리는 곳에서 같은 영속성 컨텍스트에서 관리되고 있는 똑같은 게 있기 때문에
        // 기존에 관리하던 게 나오는 거임
        // 따라서 확인해보면, insert 쿼리만 쓰이고 select 쿼리는 안쓰임
    }

    @Test(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        // given
        Member member1 = new Member();
        member1.setName("kim");

        Member member2 = new Member();
        member2.setName("kim");

        // when
        memberService.join(member1);
        memberService.join(member2);    // (expected = IllegalStateException.class) 쓰면서 다시 여기로 위치

        // (expected = IllegalStateException.class) 이거 써주면서, 아래 코드 작성 안해도 되게 됨
        /*
        try {
            memberService.join(member2);
        } catch (IllegalStateException e) {
            return;
        }
         */

        // then
        fail("예외가 발생해야 합니다.");
    }
}
