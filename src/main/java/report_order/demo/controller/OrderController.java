package report_order.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import report_order.demo.model.request.OrderRequest;
import report_order.demo.model.response.OrderResponse;
import report_order.demo.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse save(@RequestBody @Valid OrderRequest request) {
        return orderService.save(request);
    }

    @GetMapping
    public Page<OrderResponse> list(Pageable pageable, @RequestParam(required = false) Long itemId) {
        return orderService.list(pageable, itemId);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return orderService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }
}
