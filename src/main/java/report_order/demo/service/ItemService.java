package report_order.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import report_order.demo.model.entity.Item;
import report_order.demo.model.mappers.ItemMapper;
import report_order.demo.model.request.ItemRequest;
import report_order.demo.model.response.ItemResponse;
import report_order.demo.repository.InventoryRepository;
import report_order.demo.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public ItemResponse getItem(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        Integer stock = inventoryRepository.calculateRemainingStock(item.getId());
        return ItemMapper.toResponse(item, stock);
    }

    public Page<ItemResponse> listItems(Pageable pageable) {
        return itemRepository.findAll(pageable).map(item ->
                ItemMapper.toResponse(item, inventoryRepository.calculateRemainingStock(item.getId())));
    }

    public ItemResponse save(ItemRequest request) {
        Item item = itemRepository.save(Item.builder()
                .name(request.getName())
                .price(request.getPrice())
                .build());
        return ItemMapper.toResponse(item, 0);
    }

    public ItemResponse edit(Long id, ItemRequest request) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new RuntimeException("Item not found"));
        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item = itemRepository.save(item);
        return ItemMapper.toResponse(item, inventoryRepository.calculateRemainingStock(id));
    }

    public void delete(Long id) {
        itemRepository.deleteById(id);
    }
}

