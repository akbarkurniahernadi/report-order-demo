package report_order.demo.model.mappers;

import report_order.demo.model.entity.OrderEntity;
import report_order.demo.model.response.OrderResponse;

public class OrderMapper {
    public static OrderResponse toResponse(OrderEntity order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNo(order.getOrderNo())
                .itemId(order.getItem().getId())
                .itemName(order.getItem().getName())
                .qty(order.getQty())
                .price(order.getPrice())
                .build();
    }
}
