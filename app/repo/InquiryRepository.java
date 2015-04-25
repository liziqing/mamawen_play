package repo;

import models.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.LockModeType;
import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/3/3.
 */
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    //@Query("SELECT * FROM inquriy WHERE createTime >= :stime AND createTime < :etime ORDER BY createTime")
    List<Inquiry> findByLevelAndReceptedFalseAndCreateTimeBetween(int level, Date start, Date end);

    @Query(value = "SELECT * FROM inquiry WHERE level = 1 AND (createTime BETWEEN ?1 AND ?2) AND recepted = false ORDER BY createTime ", nativeQuery = true)
    //@Query(value = "SELECT i FROM inquiry i WHERE i.level = 1 AND (i.createTime BETWEEN ?2 AND ?3) AND i.recepted = false ORDER BY i.createTime")
    List<Inquiry> getUnreceptLevel1Inquiry(Date start, Date end);
    @Query(value = "SELECT * FROM inquiry WHERE level = 2 AND (updateTime BETWEEN ?1 AND ?2) AND recepted = false ORDER BY createTime", nativeQuery = true)
        //@Query(value = "SELECT i FROM inquiry i WHERE i.level = 1 AND (i.createTime BETWEEN ?2 AND ?3) AND i.recepted = false ORDER BY i.createTime")
    List<Inquiry> getUnreceptLevel2Inquiry(Date start, Date end);

    @Query("SELECT DISTINCT inq FROM Inquiry inq " +
            "WHERE inq.level = ?1 AND inq.recepted=false AND inq.depart.name=?2 AND (inq.assigned = 0 OR inq.assigned = ?3) " +
            "ORDER BY inq.assigned desc ,inq.updateTime ")
    Page<Inquiry> getLevelNUnrecptedInquiry(int level,String department, Long doctorID, Pageable p);
    @Query("SELECT DISTINCT inq FROM Inquiry inq " +
            "WHERE inq.level <> ?1 AND inq.recepted=false AND inq.depart.name=?2 AND (inq.assigned = 0 OR inq.assigned = ?3) " +
            "ORDER BY inq.assigned desc , inq.level desc, inq.updateTime")
    Page<Inquiry> getOtherLevelNUnrecptedInquiry(int level,String department, Long doctorID, Pageable p);
    @Query(value = "(SELECT * FROM inquiry WHERE level =?1 AND recepted = false AND depart_id = ?2 AND  (assigned = 0 OR assigned = ?3) ORDER BY assigned DESC, updateTime) " +
            "UNION " +
            "(SELECT * FROM inquiry WHERE level <> ?1 AND recepted=false AND depart_id = ?2 AND (assigned = 0 OR assigned = ?3) ORDER BY assigned desc, level desc, updateTime) LIMIT ?5 OFFSET ?4", nativeQuery = true)
    List<Inquiry> getAllAvailibleInquiry(int level, Long depart, Long doctorID, int page, int limit);
    @Transactional
    @Modifying
    @Query("UPDATE Inquiry inq SET inq.assigned = 0, inq.updateTime = ?2, inq.level = 2 WHERE inq.level = 1 AND inq.updateTime <= ?1 AND inq.recepted = false")
    void updateOldLeve1Inquiry(Long highLimit, Long current);
    @Transactional
    @Modifying
    @Query("UPDATE Inquiry inq SET inq.assigned = 0, inq.updateTime = ?2, inq.level = 3 WHERE inq.level = 2 AND inq.updateTime <= ?1 AND inq.recepted = false")
    void updateOldLeve2Inquiry(Long highLimit, Long current);
    @Transactional
    @Modifying
    @Query("UPDATE Inquiry inq SET inq.assigned = 0, inq.updateTime = ?2, inq.level = 3 WHERE inq.level = 3 AND inq.updateTime <= ?1 AND inq.recepted = false")
    void updateOldLeve3Inquiry(Long highLimit, Long current);
    @Query("SELECT inq FROM Inquiry inq WHERE inq.user.id = ?1 AND inq.finished = true")
    List<Inquiry> getFinishedInquiryOfUser(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT inq FROM Inquiry inq WHERE inq.id = ?1")
    Inquiry findOneWithLock(Long id);
}
