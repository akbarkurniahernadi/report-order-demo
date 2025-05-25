package report_order.demo.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private Integer price;
    private Integer remainingStock;
}
