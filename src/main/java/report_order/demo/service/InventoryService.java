package report_order.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import report_order.demo.model.entity.Inventory;
import report_order.demo.model.entity.Item;
import report_order.demo.model.mappers.InventoryMapper;
import report_order.demo.model.request.InventoryRequest;
import report_order.demo.model.response.InventoryResponse;
import report_order.demo.repository.InventoryRepository;
import report_order.demo.repository.ItemRepository;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ItemRepository itemRepository;

    public InventoryResponse save(InventoryRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (request.getType().equals("W")) {
            Integer currentStock = inventoryRepository.calculateRemainingStock(item.getId());
            if (currentStock < request.getQty()) {
                throw new RuntimeException("Insufficient stock for withdrawal");
            }
        }

        Inventory inventory = inventoryRepository.save(Inventory.builder()
                .item(item)
                .qty(request.getQty())
                .type(request.getType())
                .build());

        return InventoryMapper.toResponse(inventory);
    }

    public Page<InventoryResponse> list(Pageable pageable, Long itemId) {
        Page<Inventory> inventories = (itemId == null) ?
                inventoryRepository.findAll(pageable) :
                inventoryRepository.findByItemId(itemId, pageable);
        return inventories.map(InventoryMapper::toResponse);
    }

    public InventoryResponse get(Long id) {
        Inventory inventory = inventoryRepository.findById(id).orElseThrow(() -> new RuntimeException("Inventory not found"));
        return InventoryMapper.toResponse(inventory);
    }

    public void delete(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Transactional
    public InventoryResponse update(Long id, InventoryRequest request) {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        // If the type is withdrawal, check if the stock is sufficient for the requested quantity change
        if (request.getType().equals("W")) {
            Integer currentStock = inventoryRepository.calculateRemainingStock(item.getId());
            // Calculate stock by adding back the old quantity first before subtracting the new quantity
            int adjustedStock = currentStock + inventory.getQty(); // add old qty back because it will be updated
            if (adjustedStock < request.getQty()) {
                throw new RuntimeException("Insufficient stock for withdrawal");
            }
        }

        inventory.setItem(item);
        inventory.setQty(request.getQty());
        inventory.setType(request.getType());

        Inventory updatedInventory = inventoryRepository.save(inventory);

        return InventoryMapper.toResponse(updatedInventory);
    }


}

