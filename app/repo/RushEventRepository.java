package repo;

import models.RushEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/4/13.
 */
public interface RushEventRepository extends JpaRepository<RushEvent, Long> {
    @Query("SELECT evt FROM RushEvent evt WHERE evt.startTime >= ?1 AND evt.startTime < ?2 ORDER BY evt.startTime")
    List<RushEvent> getRushEventInRange(Date start, Date end);
}
