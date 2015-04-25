package repo;

import models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/3/31.
 */
public interface ReminderRepository extends JpaRepository<Reminder, Long>{
    @Query("SELECT rem FROM Reminder rem WHERE rem.startTime <= ?1 AND rem.endTime > ?1 AND rem.remindTime = ?2")
    List<Reminder>getCurrentReminders(Date cur, String time);
    @Query("SELECT rem FROM Reminder rem WHERE rem.doctor.id=?1")
    List<Reminder>getRemindersOfDcotor(Long doctorID);
    @Query("SELECT rem FROM Reminder rem WHERE rem.id=?1 AND rem.doctor.id=?2")
    Reminder getReminderOfDoctor(Long reminderID, Long doctorID);
}
