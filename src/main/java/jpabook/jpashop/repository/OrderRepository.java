package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    // 검색 기능
     public List<Order> findAll(OrderSearch orderSearch) {

        /*
        [동적 쿼리 적용하기 전 jpql]
        객체니까 참조하는 스타일로 join
        return em.createQuery("select o from Order o join o.member m" +
                        " where o.status = :status " +
                        "  and m.name like :name", Order.class)
                .setParameter("status", orderSearch.getOrderStatus())   // 파라미터 바인딩
                .setParameter("name", orderSearch.getMemberName())
                .setMaxResults(1000)    // 최대 1000개까지만 join
                .getResultList();
        // setFirstResult(100) 사용하면 페이징도 가능 (100부터 시작)
         */

         // 동적쿼리로 만들기
         // [방법1] language  = JPAQL
         // 결론 : 매우 복잡. 알 필요없음
         String jpql = "select o from Order o join o.member m";
         boolean isFirstCondition = true;

         //주문 상태 검색
         if (orderSearch.getOrderStatus() != null) {
             if (isFirstCondition) {
                 jpql += " where";
                 isFirstCondition = false;
             }
             else {
                 jpql += " and";
             }
             jpql += " o.status = :status";
         }

         // 회원 이름 검색
         if (StringUtils.hasText(orderSearch.getMemberName())) {
             if (isFirstCondition) {
                 jpql += " where";
                 isFirstCondition = false;
             }
             else {
                 jpql += " and";
             }
             jpql += " m.name like :name";
         }

         TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                 .setMaxResults(1000); //최대 1000건
         // 파라미터 바인딩도 동적으로 해야 함
         if (orderSearch.getOrderStatus() != null) {
             query = query.setParameter("status", orderSearch.getOrderStatus());
         }
         if (StringUtils.hasText(orderSearch.getMemberName())) {
         }
         query = query.setParameter("name", orderSearch.getMemberName());
         return query.getResultList();

         // JPQL 을 문자로 생성하는건 굉장히 복잡하고 번거롭다. 실수로 인한 버그 발생 가능성도 있음
     }

     // [방법2] JPQL 을 자바코드로 작성할 수 있도록 JPA 가 제공하는 표준
     // JPA Criteria 가 도와줌
    // 결론 : 매우 복잡. 알 필요없음
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Order, Member> m = o.join("member", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        // 주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        // 회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getMemberName()
                            + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000); //최대 1000

        return query.getResultList();
    }
    // 결론 : [방법1] [방법2] 모두 실무에서 안 씀

    // ** -> Querydsl 을 사용하자
}
