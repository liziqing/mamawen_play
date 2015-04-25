package repo;

import models.DoctorCommodityOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Created by Ronald on 2015/4/11.
 */
public interface OrderRepository  extends JpaRepository<DoctorCommodityOrder, Long>{
    @Query("SELECT o FROM DoctorCommodityOrder o WHERE o.doctor.id = ?1 ORDER BY o.createTime DESC")
    Page<DoctorCommodityOrder> getOrdersOfDoctor(Long doctorID, Pageable p);
}
