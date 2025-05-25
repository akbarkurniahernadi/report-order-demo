package report_order.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import report_order.demo.model.entity.OrderEntity;


@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Page<OrderEntity> findByItemId(Long itemId, Pageable pageable);
}
