package jpabook.jpashop.domain;

import jakarta.persistence.*;
import jpabook.jpashop.domain.item.Item;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int orderPrice;    // 주문 가격
    private int count; // 주문 수량

    // ** 아래의 생성 메서드 외에, new OrderItem() 를 활용하는 식의 생성 방법을 통한 생성을 막기 위한 로직 추가
    // JPA 는 protected 까지 기본 생성자를 만들 수 있게 허용해줌
    // JPA 쓰면서 protected 해둔다는 건 -> 쓰지 말라는 걸 의미
    /*
    protected OrderItem() {
    }
    // 이거 쓰면 에러 뜸 -> 제약 가능
    // 이걸 롬복으로 줄일 수 있음 -> @NoArgsConstructor(access = AccessLevel.PROTECTED)
     */
    // **

    // == 생성 메서드 ==
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        // 할인 등이 적용되면 item 원가와 orderPrice 가 달라질 수 있으므로 따로 작성해줘야 함
        orderItem.setItem(item);
        orderItem.setOrderPrice(orderPrice);
        orderItem.setCount(count);

        // 주문 수량 만큼, 상품 수량에서 빼주기
        item.removeStock(count);
        return orderItem;
    }

    // == 비즈니스 로직 ==
    public void cancel() {
        // 주문을 취소한 경우에,
        // 해당 주문에 포함됐었던 item 의 재고를 주문 수량만큼 다시 늘려줘야 함
        getItem().addStock(count);
    }

    // == 조회 로직 ==
    public int getTotalPrice() {
        return getOrderPrice() * getCount();
    }
}
