package report_order.demo.model.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderRequest {
    @NotNull
    private Long itemId;

    @NotNull
    private Integer qty;
}
