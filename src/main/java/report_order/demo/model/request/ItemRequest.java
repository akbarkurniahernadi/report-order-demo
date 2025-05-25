package report_order.demo.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ItemRequest {
    @NotBlank
    private String name;

    @NotNull
    private Integer price;
}
