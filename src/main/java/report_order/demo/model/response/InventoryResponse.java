package report_order.demo.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private Integer qty;
    private String type;
}
