package report_order.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import report_order.demo.model.request.ItemRequest;
import report_order.demo.model.response.ItemResponse;
import report_order.demo.service.ItemService;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public Page<ItemResponse> list(Pageable pageable) {
        return itemService.listItems(pageable);
    }

    @PostMapping
    public ItemResponse save(@RequestBody @Valid ItemRequest request) {
        return itemService.save(request);
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @RequestBody @Valid ItemRequest request) {
        return itemService.edit(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
