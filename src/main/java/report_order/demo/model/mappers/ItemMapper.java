package report_order.demo.model.mappers;

import report_order.demo.model.entity.Item;
import report_order.demo.model.response.ItemResponse;

public class ItemMapper {
    public static ItemResponse toResponse(Item item, Integer stock) {
        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .remainingStock(stock)
                .build();
    }
}
