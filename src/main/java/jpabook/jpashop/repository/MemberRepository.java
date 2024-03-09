package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

// @Repository 사용하면 컴포넌트 스캔에 의해 자동으로 스프링 빈에 등록돼서 관리 됨
@Repository
@RequiredArgsConstructor
public class MemberRepository {

    // JPA 를 사용하므로, JPA 가 제공하는 표준 어노테이션인 @PersistenceContext 사용
    // -> 스프링이 EntityManger 를 만들어서 여기에 injection 해줌
    // @PersistenceContext
    private final EntityManager em;

    // repository 도, service 처럼, 이렇게 생성자 injection 할 수 있음
    // But, spring boot 의 springDataJpa 라이브러리를 사용하면,
    // 위에 EntityManager 에서 @PersistenceContext 를 @AutoWired 로 바꿀 수 있음
    // 따라서, service 로직과 마찬가지로 @RequiredArgsConstructor 사용 가능 -> 생성자 안써줘도 됨
    /*
    public MemberRepository() {
        this.em = em;
    }
     */

    // JPA 가 저장해줌
    public void save(Member member) {
        // (주의) persist 가 작동된다고 바로 db에 반영되는거 아님
        // persist 하면 영속성 컨텍스트에 member 객체를 넣음
        // db 트랜잭션이 commit 되는 시점에 플러쉬 flush 가 되면서 db에 반영 (-> db에 insert 쿼리 날리는 것)
        em.persist(member);
    }

    public Member findOne(Long id) {
        // find(타입, pk)
        return em.find(Member.class, id);
    }


    // <둘의 차이> from 의 대상
    // SQL : 테이블을 대상으로 쿼리
    // JPQL : 엔티티 객체를 대상으로 쿼리
    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public List<Member> findByName(String name) {
        // 파라미터 바인딩해서 이름으로 특정 회원 찾기
        return em.createQuery("select m from Member m where m.name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();
    }
}
