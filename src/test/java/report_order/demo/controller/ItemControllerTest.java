package report_order.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.request.ItemRequest;
import report_order.demo.model.response.ItemResponse;
import report_order.demo.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGet() {
        Long id = 1L;
        ItemResponse mockResponse = ItemResponse.builder()
                .id(id)
                .name("Keyboard")
                .price(150000)
                .remainingStock(20)
                .build();

        when(itemService.getItem(id)).thenReturn(mockResponse);

        ItemResponse result = itemController.get(id);

        assertEquals(mockResponse, result);
        verify(itemService, times(1)).getItem(id);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ItemResponse> items = List.of(
                ItemResponse.builder().id(1L).name("Item 1").price(1000).remainingStock(10).build(),
                ItemResponse.builder().id(2L).name("Item 2").price(2000).remainingStock(20).build()
        );
        Page<ItemResponse> page = new PageImpl<>(items, pageable, items.size());

        when(itemService.listItems(pageable)).thenReturn(page);

        Page<ItemResponse> result = itemController.list(pageable);

        assertEquals(2, result.getContent().size());
        verify(itemService, times(1)).listItems(pageable);
    }

    @Test
    public void testSave() {
        ItemRequest request = new ItemRequest();
        request.setName("Mouse");
        request.setPrice(50000);

        ItemResponse saved = ItemResponse.builder()
                .id(1L)
                .name("Mouse")
                .price(50000)
                .remainingStock(0)
                .build();

        when(itemService.save(request)).thenReturn(saved);

        ItemResponse result = itemController.save(request);

        assertEquals(saved, result);
        verify(itemService, times(1)).save(request);
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        ItemRequest request = new ItemRequest();
        request.setName("Updated Mouse");
        request.setPrice(60000);

        ItemResponse updated = ItemResponse.builder()
                .id(id)
                .name("Updated Mouse")
                .price(60000)
                .remainingStock(5)
                .build();

        when(itemService.edit(id, request)).thenReturn(updated);

        ItemResponse result = itemController.update(id, request);

        assertEquals(updated, result);
        verify(itemService, times(1)).edit(id, request);
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        doNothing().when(itemService).delete(id);

        itemController.delete(id);

        verify(itemService, times(1)).delete(id);
    }
}
