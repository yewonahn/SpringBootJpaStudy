package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
// import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// 데이터 변경, 데이터 관련 로직들은 기본적으로 꼭 트랜잭션이 있어야 함 -> 트랜잭션 안에서 실행되어야 함
@Transactional(readOnly = true)
// (1)
// -> public 메서드들은 transaction 에 걸려 들어감
// javax 가 제공하는, spring 이 제공하는 transaction 이 있는데, 이미 spring 사용하는 중이니까 spring 이 제공하는거 사용 추천
// (+ 사용할 수 있는 기능 더 많음)
// (2)
// 읽기 전용 (조회) 인 곳에서 (readOnly = true) 사용하면
// jpa 가 조회하는 곳에서는 트랜잭션이 성능 더 최적화
// (읽기 전용 아닌 곳에서 사용 x)
// 여기에서는 조회가 더 많으니까 (readOnly = true) 를 디폴트로 두기
// -> public 인 곳에 자동으로 @Transactional(readOnly = true) 적용되고, 따로 @Transactional 해둔 곳엔 그렇게 적용
// 원래 (readOnly = False) 이게 디폴트
// @AllArgsConstructor -> 이거 쓰면 생성자 주입도 안써줘도 됨
@RequiredArgsConstructor    // final 있는 필드만 가지고 생성자를 만들어줌. @AllArgsConstructor 보다 이거 권장
public class MemberService {

    // 스프링 빈에 등록되어있는 MemberRepository 를 injection 해줌 -> 필드 injection
    // 더 이상 변경할 일 없으니까 final 써주는거 권장
    private final MemberRepository memberRepository;

    // (방법1) setter Injection
    /*
    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
     */

    // (방법2) 생성자 Injection <- 권장 방식은 이거
    // 최신 버전의 스프링에서는,
    // @Autowired 써주지 않아도, 생성자가 하나만 있는 경우엔 스프링에서 알아서 생성자에 autowired 를 해줌
    /*
    @Autowired
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }
     */


    @Transactional
    // 회원 가입
    public Long join(Member member) {
        // 중복 회원 검증
        validateDuplicateMember(member);

        memberRepository.save(member);
        return member.getId();
    }

    // 실무에서는 멀티 스레드 상황을 고려해서 (ex) 동시에 같은 이름을 가진 member 저장하는 요청 들어오는
    // db에 member 의 name 을 unique 제약 조건으로 두는 걸 권장
    private void validateDuplicateMember(Member member) {
        // Exception
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    // 회원 전체 조회
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }
}
