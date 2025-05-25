package report_order.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import report_order.demo.model.request.InventoryRequest;
import report_order.demo.model.response.InventoryResponse;
import report_order.demo.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @PostMapping
    public InventoryResponse save(@RequestBody @Valid InventoryRequest request) {
        return inventoryService.save(request);
    }

    @GetMapping
    public Page<InventoryResponse> list(Pageable pageable, @RequestParam(required = false) Long itemId) {
        return inventoryService.list(pageable, itemId);
    }

    @GetMapping("/{id}")
    public InventoryResponse get(@PathVariable Long id) {
        return inventoryService.get(id);
    }

    @PutMapping("/{id}")
    public InventoryResponse update(@PathVariable Long id, @RequestBody @Valid InventoryRequest request) {
        return inventoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        inventoryService.delete(id);
    }
}