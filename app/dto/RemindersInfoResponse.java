package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/5.
 */
public class RemindersInfoResponse extends GeneralResponse{
    public List<ReminderInfo> reminders;

    public RemindersInfoResponse(List<ReminderInfo> reminders) {
        this.reminders = reminders;
    }
}
