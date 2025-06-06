package report_order.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import report_order.demo.model.entity.Item;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
