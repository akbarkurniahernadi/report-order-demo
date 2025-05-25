package report_order.demo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import report_order.demo.model.request.OrderRequest;
import report_order.demo.model.response.OrderResponse;
import report_order.demo.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSave() {
        OrderRequest request = new OrderRequest();
        request.setItemId(1L);
        request.setQty(3);

        OrderResponse response = OrderResponse.builder()
                .id(1L)
                .orderNo("ORD-001")
                .itemId(1L)
                .itemName("Item A")
                .qty(3)
                .price(3000)
                .build();

        when(orderService.save(request)).thenReturn(response);

        OrderResponse result = orderController.save(request);

        assertEquals(response, result);
        verify(orderService, times(1)).save(request);
    }

    @Test
    void testList() {
        Pageable pageable = PageRequest.of(0, 10);
        Long itemId = 1L;

        List<OrderResponse> content = List.of(
                OrderResponse.builder().id(1L).orderNo("ORD-001").itemId(itemId).itemName("Item A").qty(2).price(2000).build(),
                OrderResponse.builder().id(2L).orderNo("ORD-002").itemId(itemId).itemName("Item A").qty(5).price(5000).build()
        );
        Page<OrderResponse> page = new PageImpl<>(content, pageable, content.size());

        when(orderService.list(pageable, itemId)).thenReturn(page);

        Page<OrderResponse> result = orderController.list(pageable, itemId);

        assertEquals(2, result.getContent().size());
        verify(orderService, times(1)).list(pageable, itemId);
    }

    @Test
    void testGet() {
        Long id = 1L;

        OrderResponse response = OrderResponse.builder()
                .id(id)
                .orderNo("ORD-001")
                .itemId(1L)
                .itemName("Item A")
                .qty(2)
                .price(2000)
                .build();

        when(orderService.get(id)).thenReturn(response);

        OrderResponse result = orderController.get(id);

        assertEquals(response, result);
        verify(orderService, times(1)).get(id);
    }

    @Test
    void testDelete() {
        Long id = 1L;

        doNothing().when(orderService).delete(id);

        orderController.delete(id);

        verify(orderService, times(1)).delete(id);
    }
}
