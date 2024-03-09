package jpabook.jpashop.controller;

import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    @GetMapping(value = "items/new")
    public String createForm(Model model) {
        model.addAttribute("form", new BookForm());
        return "items/createItemForm";
    }

    @PostMapping(value = "items/new")
    public String create(BookForm form) {
        Book book = new Book();
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
        return "redirect:/items";
    }

    // 상품 목록
    @GetMapping(value = "items")
    public String list(Model model) {
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    // 상품 수정 폼
    @GetMapping(value = "items/{itemId}/edit")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model
            model) {

        Book item = (Book) itemService.findItem(itemId);
        BookForm form = new BookForm();

        form.setId(item.getId());
        form.setName(item.getName());
        form.setPrice(item.getPrice());
        form.setStockQuantity(item.getStockQuantity());
        form.setAuthor(item.getAuthor());
        form.setIsbn(item.getIsbn());

        model.addAttribute("form", form);
        return "items/updateItemForm";
    }

    // 상품 수정
    @PostMapping(value = "items/{itemId}/edit")
    public String updateItem(@PathVariable Long itemId, @ModelAttribute("form") BookForm form) {

        /*

        1. 이런식으로,
        컨트롤러에서 어설프게 엔티티를 변경하지 않을 것

        Book book = new Book();
        book.setId(form.getId());
        // 식별자가 이미 존재 -> db에 이미 들어갔다가 온 객체 -> **준영속 객체**
        book.setName(form.getName());
        book.setPrice(form.getPrice());
        book.setStockQuantity(form.getStockQuantity());
        book.setAuthor(form.getAuthor());
        book.setIsbn(form.getIsbn());

        itemService.saveItem(book);
         */
        // -> itemRepository.save(item) -> else { em.merge(item); }
        // : **merge** 실행 됨
        // merge 는 ItemService 에 작성한 updateItem 내용과 같은 역할을 해줌
        // BUT, merge 사용하지 말고 **변경 감지** 사용

        // 2. 이렇게 작성 (위에처럼 어설프게 엔티티를 파라미터로 쓰지 말고)
        // 정확하게 필요한 데이터만 받기 -> 유지보수를 위해 훨씬 좋음
        // +) 변경해야할 데이터가 많아서 번거로운 경우,
        // 서비스 계층에 dto 만들고, itemService.updateItem 파라미터로 dto 받도록
        itemService.updateItem(itemId, form.getName(), form.getPrice(), form.getStockQuantity());

        return "redirect:/items";
    }
}

