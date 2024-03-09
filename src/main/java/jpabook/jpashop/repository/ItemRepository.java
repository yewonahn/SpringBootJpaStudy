package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        // item 은 저장할 때, 처음에 id 가 없음
        // 따라서 id 가 없다는 건, 완전히 새로 저장함을 의미
        if(item.getId() == null) {
            // 이 아이템을 신규로 등록
            em.persist(item);
        } else {
            // item 이 있다는 건, 이미 db 에 등록된 걸 db 에서 가져온 거
            // 따라서 여기에서의 save 는 update 와 유사
            // (merge 는 update 와 유사함)
            em.merge(item);
            // Item merge = em.merge(item);
        }
    }

    // 단 건 조회를 할 땐 find 이용 가능
    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    // 여러 개 찾을 땐, jpql 이용
    public List<Item> findAll() {
        return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
