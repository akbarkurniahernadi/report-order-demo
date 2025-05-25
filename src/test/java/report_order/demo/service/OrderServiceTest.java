package report_order.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.entity.*;
import report_order.demo.model.request.OrderRequest;
import report_order.demo.model.response.OrderResponse;
import report_order.demo.repository.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    private Item item;
    private OrderEntity orderEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        item = Item.builder()
                .id(1L)
                .name("Item A")
                .price(1000)
                .build();

        orderEntity = OrderEntity.builder()
                .id(1L)
                .item(item)
                .qty(2)
                .price(2000)
                .orderNo("ORD-001")
                .build();
    }

    @Test
    void testSaveOrder_Success() {
        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQty(2);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(10);
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = orderService.save(request);

        assertNotNull(response);
        assertEquals(2, response.getQty());
        assertEquals(2000, response.getPrice());
        verify(orderRepository, times(1)).save(any(OrderEntity.class));
    }

    @Test
    void testSaveOrder_InsufficientStock() {
        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQty(20);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(5);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.save(request));
        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void testGetOrder_Success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));

        OrderResponse response = orderService.get(1L);

        assertNotNull(response);
        assertEquals(2, response.getQty());
        assertEquals("Item A", response.getItemName());
    }

    @Test
    void testGetOrder_NotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.get(1L));
        assertEquals("Order not found", ex.getMessage());
    }

    @Test
    void testDeleteOrder() {
        orderService.delete(1L);
        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    void testListOrders() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderEntity> orderList = List.of(orderEntity);
        Page<OrderEntity> page = new PageImpl<>(orderList);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<OrderResponse> responsePage = orderService.list(pageable, null);
        assertEquals(1, responsePage.getTotalElements());
    }

    @Test
    void testUpdateOrder_Success_SameItem() {
        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQty(3);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(1); // currentStock = 1
        when(orderRepository.save(any())).thenReturn(orderEntity);

        Inventory existingInventory = Inventory.builder()
                .id(10L)
                .item(item)
                .qty(2)
                .type("W")
                .build();

        when(inventoryRepository.findFirstByItemIdAndTypeAndQty(1L, "W", 2))
                .thenReturn(Optional.of(existingInventory));

        OrderResponse response = orderService.update(1L, request);
        assertNotNull(response);
        assertEquals(3, response.getQty());
    }


    @Test
    void testUpdateOrder_InsufficientStock_SameItem() {
        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQty(20);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(1); // +2 = 3 < 20

        RuntimeException ex = assertThrows(RuntimeException.class, () -> orderService.update(1L, request));
        assertEquals("Insufficient stock", ex.getMessage());
    }

    @Test
    void testUpdateOrder_Success_DifferentItem() {
        Item newItem = Item.builder()
                .id(2L)
                .name("Item B")
                .price(1500)
                .build();

        OrderRequest request = new OrderRequest();
        request.setItemId(2L);
        request.setQty(1);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(itemRepository.findById(2L)).thenReturn(Optional.of(newItem));
        when(inventoryRepository.calculateRemainingStock(2L)).thenReturn(5);
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Inventory existingInventory = Inventory.builder()
                .id(10L)
                .item(item)
                .qty(2)
                .type("W")
                .build();

        when(inventoryRepository.findFirstByItemIdAndTypeAndQty(1L, "W", 2))
                .thenReturn(Optional.of(existingInventory));

        OrderResponse response = orderService.update(1L, request);

        assertNotNull(response);
        assertEquals(1, response.getQty());
        assertEquals(1500, response.getPrice());
        assertEquals(2L, response.getItemId());

        verify(inventoryRepository).save(any(Inventory.class));
    }

}
