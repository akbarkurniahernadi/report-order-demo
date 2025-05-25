package report_order.demo.model.mappers;

import report_order.demo.model.entity.Inventory;
import report_order.demo.model.response.InventoryResponse;

public class InventoryMapper {
    public static InventoryResponse toResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .itemId(inventory.getItem().getId())
                .itemName(inventory.getItem().getName())
                .qty(inventory.getQty())
                .type(inventory.getType())
                .build();
    }
}
