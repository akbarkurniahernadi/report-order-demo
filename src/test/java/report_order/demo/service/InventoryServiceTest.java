package report_order.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.entity.Inventory;
import report_order.demo.model.entity.Item;
import report_order.demo.model.request.InventoryRequest;
import report_order.demo.model.response.InventoryResponse;
import report_order.demo.repository.InventoryRepository;
import report_order.demo.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveTopUp() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(10);
        request.setType("T"); // Top-up

        Item item = Item.builder().id(1L).name("Item A").price(1000).build();
        Inventory savedInventory = Inventory.builder().id(1L).item(item).qty(10).type("T").build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        InventoryResponse result = inventoryService.save(request);

        assertEquals(1L, result.getId());
        assertEquals("Item A", result.getItemName());
        assertEquals(10, result.getQty());
        assertEquals("T", result.getType());

        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testSaveWithdrawWithEnoughStock() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(5);
        request.setType("W"); // Withdrawal

        Item item = Item.builder().id(1L).name("Item A").price(1000).build();
        Inventory savedInventory = Inventory.builder().id(2L).item(item).qty(5).type("W").build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(10);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(savedInventory);

        InventoryResponse result = inventoryService.save(request);

        assertEquals(2L, result.getId());
        assertEquals("W", result.getType());

        verify(inventoryRepository).calculateRemainingStock(1L);
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testSaveWithdrawWithInsufficientStock() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(15);
        request.setType("W");

        Item item = Item.builder().id(1L).name("Item A").price(1000).build();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(10);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.save(request));

        assertEquals("Insufficient stock for withdrawal", ex.getMessage());
    }

    @Test
    void testUpdateWithStockCheckSuccess() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(7);
        request.setType("W");

        Item item = Item.builder().id(1L).name("Item A").build();
        Inventory existing = Inventory.builder().id(1L).item(item).qty(5).type("W").build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(6); // current stock excluding this withdrawal
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryResponse result = inventoryService.update(1L, request);

        assertEquals(7, result.getQty());
        assertEquals("W", result.getType());
    }

    @Test
    void testUpdateWithInsufficientStock() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(20);
        request.setType("W");

        Item item = Item.builder().id(1L).name("Item A").build();
        Inventory existing = Inventory.builder().id(1L).item(item).qty(5).type("W").build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(10);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.update(1L, request));

        assertEquals("Insufficient stock for withdrawal", ex.getMessage());
    }

    @Test
    void testListWithItemId() {
        Pageable pageable = PageRequest.of(0, 10);
        Long itemId = 1L;

        Item item = Item.builder().id(1L).name("Item A").build();
        Inventory inventory = Inventory.builder().id(1L).item(item).qty(5).type("T").build();

        Page<Inventory> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findByItemId(itemId, pageable)).thenReturn(page);

        Page<InventoryResponse> result = inventoryService.list(pageable, itemId);

        assertEquals(1, result.getTotalElements());
        assertEquals("Item A", result.getContent().get(0).getItemName());
    }

    @Test
    void testListAllWithoutItemId() {
        Pageable pageable = PageRequest.of(0, 10);

        Item item = Item.builder().id(1L).name("Item A").build();
        Inventory inventory = Inventory.builder().id(1L).item(item).qty(5).type("T").build();

        Page<Inventory> page = new PageImpl<>(List.of(inventory));
        when(inventoryRepository.findAll(pageable)).thenReturn(page);

        Page<InventoryResponse> result = inventoryService.list(pageable, null);

        assertEquals(1, result.getTotalElements());
        assertEquals("Item A", result.getContent().get(0).getItemName());
    }

    @Test
    void testGetInventoryFound() {
        Item item = Item.builder().id(1L).name("Item A").build();
        Inventory inventory = Inventory.builder().id(1L).item(item).qty(5).type("T").build();

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));

        InventoryResponse response = inventoryService.get(1L);

        assertEquals(1L, response.getId());
        assertEquals("Item A", response.getItemName());
    }

    @Test
    void testGetInventoryNotFound() {
        when(inventoryRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> inventoryService.get(1L));

        assertEquals("Inventory not found", ex.getMessage());
    }

    @Test
    void testDelete() {
        Long id = 1L;

        doNothing().when(inventoryRepository).deleteById(id);

        inventoryService.delete(id);

        verify(inventoryRepository).deleteById(id);
    }
}
