package report_order.demo.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"order\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_no", unique = true, nullable = false)
    private String orderNo;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    private Integer qty;

    private Integer price;
}

