package dto;

/**
 * Created by Ronald on 2015/4/8.
 */
public class ReminderUpdateResponse extends GeneralResponse{

    public ReminderInfo reminder;

    public ReminderUpdateResponse(ReminderInfo reminder) {
        this.reminder = reminder;
    }
}
