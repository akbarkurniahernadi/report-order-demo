package report_order.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import report_order.demo.model.entity.Inventory;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    @Query("SELECT COALESCE(SUM(CASE WHEN i.type = 'T' THEN i.qty ELSE -i.qty END), 0) FROM Inventory i WHERE i.item.id = :itemId")
    Integer calculateRemainingStock(@Param("itemId") Long itemId);

    Page<Inventory> findByItemId(Long itemId, Pageable pageable);


    Optional<Inventory> findFirstByItemIdAndTypeAndQty(Long id, String w, Integer qty);
}
