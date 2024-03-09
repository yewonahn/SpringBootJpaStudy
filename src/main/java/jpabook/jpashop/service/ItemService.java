package jpabook.jpashop.service;

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
}
