package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    // 연관관계의 주인이 아님을 표시
    // Order 테이블에 있는 member 필드에 의해 매핑된 것 임을 나타냄 (읽기 전용)
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();
}
