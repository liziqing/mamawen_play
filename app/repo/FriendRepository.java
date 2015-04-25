package repo;

import models.FriendItem;
import models.FriendsHealthRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;


/**
 * Created by Ronald on 2015/4/10.
 */
public interface FriendRepository extends JpaRepository<FriendItem, Long>{
    @Query("SELECT friend FROM FriendItem friend WHERE friend.doctor.id = ?1 AND friend.status=1 ORDER BY friend.id")
    Page<FriendItem> getFriendsOfDoctor(Long doctorID, Pageable p);
    @Query("SELECT friend FROM FriendItem friend WHERE friend.doctor.id = ?1 AND friend.user.id = ?2")
    FriendItem getFriendOfDoctor(Long doctorID, Long userID);
    @Query("SELECT NEW models.FriendsHealthRecord(friend, record) FROM FriendItem item JOIN item.user friend LEFT JOIN friend.records record WHERE  record.createTime >= (SELECT MAX(r.updateTime) FROM HealthRecord r WHERE r.user.id=friend.id) AND item.doctor.id = ?1 ORDER BY item.createTime")
    List<FriendsHealthRecord> getFriendsHealthRecord(Long id);
}
