package report_order.demo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.entity.Item;
import report_order.demo.model.request.ItemRequest;
import report_order.demo.model.response.ItemResponse;
import report_order.demo.repository.InventoryRepository;
import report_order.demo.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private ItemService itemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetItemSuccess() {
        Item item = Item.builder().id(1L).name("Test Item").price(1000).build();
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(20);

        ItemResponse result = itemService.getItem(1L);

        assertEquals("Test Item", result.getName());
        assertEquals(1000, result.getPrice());
        assertEquals(20, result.getRemainingStock());
    }

    @Test
    void testGetItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.getItem(1L));

        assertEquals("Item not found", ex.getMessage());
    }

    @Test
    void testListItems() {
        Item item1 = Item.builder().id(1L).name("Item 1").price(100).build();
        Item item2 = Item.builder().id(2L).name("Item 2").price(200).build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Item> itemPage = new PageImpl<>(List.of(item1, item2));

        when(itemRepository.findAll(pageable)).thenReturn(itemPage);
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(5);
        when(inventoryRepository.calculateRemainingStock(2L)).thenReturn(8);

        Page<ItemResponse> result = itemService.listItems(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("Item 1", result.getContent().get(0).getName());
        assertEquals(5, result.getContent().get(0).getRemainingStock());
    }

    @Test
    void testSaveItem() {
        ItemRequest request = new ItemRequest();
        request.setName("New Item");
        request.setPrice(999);

        Item saved = Item.builder().id(1L).name("New Item").price(999).build();

        when(itemRepository.save(any(Item.class))).thenReturn(saved);

        ItemResponse result = itemService.save(request);

        assertEquals("New Item", result.getName());
        assertEquals(0, result.getRemainingStock());
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void testEditItemSuccess() {
        Item existing = Item.builder().id(1L).name("Old").price(100).build();
        ItemRequest request = new ItemRequest();
        request.setName("Updated");
        request.setPrice(200);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(inventoryRepository.calculateRemainingStock(1L)).thenReturn(7);

        ItemResponse result = itemService.edit(1L, request);

        assertEquals("Updated", result.getName());
        assertEquals(200, result.getPrice());
        assertEquals(7, result.getRemainingStock());
    }

    @Test
    void testEditItemNotFound() {
        ItemRequest request = new ItemRequest();
        request.setName("Updated");
        request.setPrice(200);

        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> itemService.edit(1L, request));

        assertEquals("Item not found", ex.getMessage());
    }

    @Test
    void testDeleteItem() {
        Long id = 1L;
        doNothing().when(itemRepository).deleteById(id);

        itemService.delete(id);

        verify(itemRepository).deleteById(id);
    }
}
