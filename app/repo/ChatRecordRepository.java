package repo;

import models.ChatRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Created by Ronald on 2015/3/24.
 */
public interface ChatRecordRepository extends JpaRepository<ChatRecord, Long>{
    @Query("SELECT chat FROM ChatRecord chat WHERE chat.senderID IN ?1 AND chat.receiverID IN ?1 ORDER BY chat.createTime DESC")
    List<ChatRecord> getChatRecordsOfPair(List<Long>ids);
}
