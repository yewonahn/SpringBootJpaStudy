package jpabook.jpashop.service;

import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import jpabook.jpashop.repository.MemberRepository;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // 주문
    public Long order(Long memberId, Long itemId, int count) {

        // 식별자만 넘겨받고,
        // 핵심 비즈니스 로직 서비스에서, entity 를 찾는 것 부터 직접
        // 트랜잭션 안에서 entity 조회해야 영속성 컨텍스트로 진행됨 (status 바꾸기 가능 등등..)

        // 엔티티 조회
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // 배송 정보 생성
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        delivery.setStatus(DeliveryStatus.READY);

        // 주문 상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // 이 방식 외의 다른 방식으로의 생성 (new OrderItem()) 을 막아야 함
        // -> orderItem 에 로직 추가 -> 근데 이것도 롬복 사용으로 줄이기 가능
        // @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용

        // 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);
        // 주문 상품 생성과 마찬가지 이유로
        // @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용

        // 주문 저장
        orderRepository.save(order);
        // 여기에서 생성한 delivery, orderItem 도 저장해줘야 하는거 아닌가?
        // -> Order 엔티티에서, delivery, orderItems 에 걸어둔 cascade = CascadeType.All 옵션 때문에,
        // -> order 를 persist 하면, (order 안에 cascade 조건 걸어둔) delivery, orderItems 도 강제로 persist 날려줌

        return order.getId();
    }

    // 취소
    @Transactional
    public void cancelOrder(Long orderId) {
        // 주문 엔티티 조회
        Order order = orderRepository.findOne(orderId);
        // 주문 취소
        order.cancel();

        // 1. 엔티티에서 cancel 비즈니스 로직을 설계해놨기 때문에 이렇게 간결하게 설계 가능
        // 2. JPA 를 사용했기 때문에
        // -> db sql 을 직접 다루는 jdbc 템플릿, mybatis 등을 사용 or 내가 직접 sql 을 날리는 경우,
        // 데이터를 변경한 경우, 바깥에서 update 쿼리를 직접 짜서 repository 에 날려야 함
        // (ex) cancel 한 경우, item 의 재고가 바뀌니까 재고를 + 하는 sql 을 직접 짜서 올려야 함
        // 서비스 계층에서 비즈니스 로직을 다 쓸 수 밖에 없었음

        // BUT, JPA 를 사용하면,
        // 엔티티 안에 있는 데이터들만 바꿔주면, JPA 가 알아서 바뀐 변경 포인트들을 찾아서 db 에 update 쿼리가 날라감
    }

    // 검색
    public List<Order> findOrders(OrderSearch orderSearch) {
        return orderRepository.findAllByString(orderSearch);
    }
}
