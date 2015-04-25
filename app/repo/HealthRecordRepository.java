package repo;

import models.HealthRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/4/16.
 */
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long>{
    @Query("SELECT rec FROM HealthRecord rec WHERE rec.user.id=?1 AND rec.category=?2 AND rec.subCategory=?3 ORDER BY rec.createTime")
    List<HealthRecord> getSpcificRecordOfUser(Long uid, String category, String subCategory);
    @Query("SELECT rec FROM HealthRecord rec WHERE rec.user.id=?1 AND rec.category='宝宝' AND rec.subCategory='奶量' AND rec.recordDate=?2")
    HealthRecord getBabyMilkRecordOfUser(Long uid, Date time);
    @Query("SELECT rec FROM HealthRecord rec WHERE rec.user.id=?1 AND rec.category=?2 AND rec.subCategory='体温' AND rec.recordDate=?3")
    HealthRecord getTempRecordOfUser(Long uid, String category,  Date time);
    @Query("SELECT rec FROM HealthRecord rec WHERE rec.user.id=?1 AND rec.category=?2 AND rec.subCategory=?3 AND rec.cycle=?4")
    HealthRecord getOtherRecordOfUser(Long uid, String category, String subCategory, int cycle);
}
