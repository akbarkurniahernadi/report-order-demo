package report_order.demo.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import report_order.demo.model.entity.Inventory;
import report_order.demo.model.entity.Item;
import report_order.demo.model.entity.OrderEntity;
import report_order.demo.model.mappers.OrderMapper;
import report_order.demo.model.request.OrderRequest;
import report_order.demo.model.response.OrderResponse;
import report_order.demo.repository.InventoryRepository;
import report_order.demo.repository.ItemRepository;
import report_order.demo.repository.OrderRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final InventoryRepository inventoryRepository;

    public OrderResponse save(OrderRequest request) {
        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Integer stock = inventoryRepository.calculateRemainingStock(item.getId());
        if (stock < request.getQty()) {
            throw new RuntimeException("Insufficient stock");
        }

        OrderEntity order = orderRepository.save(OrderEntity.builder()
                .item(item)
                .qty(request.getQty())
                .price(item.getPrice() * request.getQty())
                .orderNo(UUID.randomUUID().toString())
                .build());

        inventoryRepository.save(Inventory.builder()
                .item(item)
                .qty(request.getQty())
                .type("W")
                .build());

        return OrderMapper.toResponse(order);
    }

    public Page<OrderResponse> list(Pageable pageable, Long itemId) {
        Page<OrderEntity> orders = (itemId == null) ?
                orderRepository.findAll(pageable) :
                orderRepository.findByItemId(itemId, pageable);
        return orders.map(OrderMapper::toResponse);
    }

    public OrderResponse get(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        return OrderMapper.toResponse(order);
    }

    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public OrderResponse update(Long id, OrderRequest request) {
        OrderEntity order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Item oldItem = order.getItem();
        int oldQty = order.getQty();

        Item newItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!oldItem.getId().equals(newItem.getId())) {
            // If item changed, restore stock of old item, check stock for new item
            int newStock = inventoryRepository.calculateRemainingStock(newItem.getId());
            if (newStock < request.getQty()) {
                throw new RuntimeException("Insufficient stock for new item");
            }
        } else {
            // If item same, adjust stock by returning oldQty
            int stock = inventoryRepository.calculateRemainingStock(newItem.getId()) + oldQty;
            if (stock < request.getQty()) {
                throw new RuntimeException("Insufficient stock");
            }
        }

        // Update order
        order.setItem(newItem);
        order.setQty(request.getQty());
        order.setPrice(newItem.getPrice() * request.getQty());
        OrderEntity updatedOrder = orderRepository.save(order);

        // Update inventory withdrawal
        Inventory withdrawal = inventoryRepository
                .findFirstByItemIdAndTypeAndQty(oldItem.getId(), "W", oldQty)
                .orElseThrow(() -> new RuntimeException("Related inventory withdrawal not found"));

        withdrawal.setItem(newItem);
        withdrawal.setQty(request.getQty());
        inventoryRepository.save(withdrawal);

        return OrderMapper.toResponse(updatedOrder);
    }
}