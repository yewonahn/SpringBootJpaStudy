package jpabook.jpashop.service;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    // @Transactional 디폴트가 현재는 (readOnly = true) 로 설정해뒀으므로,
    // save 에서는 (readonly = false) 가 되도록, 이렇게 따로 적어줘야함 (여기서 적어준 게 우선권을 가짐)
    @Transactional
    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public List<Item> findItems() {
        return itemRepository.findAll();
    }

    public Item findItem(Long itemId) {
        return itemRepository.findOne(itemId);
    }

    // 현재 이 ItemRepository 는 위임만 하는 역할 수행
    // 경우에 따라서는, 이렇게 위임만 하는 경우 굳이 꼭 만들어야하나 고민해보자
    // 이런 경우엔, controller 에서 repository 에 바로 접근해서 써도 된다고 생각

    // [준영속 컨텍스트를 수정하는 방법1]
    // 변경 감지 기능 사용
    @Transactional
    // (Long itemId, Book param) -> (Long itemId, String name, int price, int stockQuantity)
    public void updateItem(Long itemId, String name, int price, int stockQuantity) {
        // 2. 변경 후
        Item findItem = itemRepository.findOne(itemId);
        findItem.setPrice(price);
        findItem.setName(name);
        findItem.setStockQuantity(stockQuantity);

        /*
        1. 변경 전, (Long itemId, Book param) 일 때

        // findItem 은 영속 상태
        Item findItem = itemRepository.findOne(itemId);

        // 따라서 값을 세팅한 다음에,
        findItem.setPrice(param.getPrice());
        findItem.setName(param.getName());
        findItem.setStockQuantity(param.getStockQuantity());
        // @Transactional 에 의해 트랜잭션이 커밋됨
        // 커밋 되고 나면, JPA 가 flush 날림 (영속성 컨텍스트에 있는것 중에 변경사항이 있는것들 찾아냄)
        // -> findItem 은 기존에 있었고, 값 변경됐으니까 변경 감지해서 update 처리해 줌
         */
    }
}
