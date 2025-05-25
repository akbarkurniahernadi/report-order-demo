package report_order.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.request.InventoryRequest;
import report_order.demo.model.response.InventoryResponse;
import report_order.demo.service.InventoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSave() {
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(5);
        request.setType("T");

        InventoryResponse response = InventoryResponse.builder()
                .id(1L)
                .itemId(1L)
                .itemName("Item A")
                .qty(5)
                .type("T")
                .build();

        when(inventoryService.save(request)).thenReturn(response);

        InventoryResponse result = inventoryController.save(request);

        assertEquals(response, result);
        verify(inventoryService, times(1)).save(request);
    }

    @Test
    public void testList() {
        Pageable pageable = PageRequest.of(0, 10);
        Long itemId = 1L;

        List<InventoryResponse> content = List.of(
                InventoryResponse.builder().id(1L).itemId(itemId).itemName("Item A").qty(10).type("T").build(),
                InventoryResponse.builder().id(2L).itemId(itemId).itemName("Item A").qty(5).type("W").build()
        );
        Page<InventoryResponse> page = new PageImpl<>(content, pageable, content.size());

        when(inventoryService.list(pageable, itemId)).thenReturn(page);

        Page<InventoryResponse> result = inventoryController.list(pageable, itemId);

        assertEquals(2, result.getContent().size());
        verify(inventoryService, times(1)).list(pageable, itemId);
    }

    @Test
    public void testGet() {
        Long id = 1L;
        InventoryResponse response = InventoryResponse.builder()
                .id(id)
                .itemId(1L)
                .itemName("Item A")
                .qty(10)
                .type("T")
                .build();

        when(inventoryService.get(id)).thenReturn(response);

        InventoryResponse result = inventoryController.get(id);

        assertEquals(response, result);
        verify(inventoryService, times(1)).get(id);
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        InventoryRequest request = new InventoryRequest();
        request.setItemId(1L);
        request.setQty(20);
        request.setType("T");

        InventoryResponse response = InventoryResponse.builder()
                .id(id)
                .itemId(1L)
                .itemName("Item A")
                .qty(20)
                .type("T")
                .build();

        when(inventoryService.update(id, request)).thenReturn(response);

        InventoryResponse result = inventoryController.update(id, request);

        assertEquals(response, result);
        verify(inventoryService, times(1)).update(id, request);
    }

    @Test
    public void testDelete() {
        Long id = 1L;

        doNothing().when(inventoryService).delete(id);

        inventoryController.delete(id);

        verify(inventoryService, times(1)).delete(id);
    }
}
