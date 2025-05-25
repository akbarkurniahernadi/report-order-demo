package report_order.demo.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InventoryRequest {
    @NotNull
    private Long itemId;

    @NotNull
    private Integer qty;

    @Pattern(regexp = "[TW]", message = "Type must be 'T' or 'W'")
    private String type;
}
