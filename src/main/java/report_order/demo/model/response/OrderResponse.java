package report_order.demo.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderNo;
    private Long itemId;
    private String itemName;
    private Integer qty;
    private Integer price;
}
