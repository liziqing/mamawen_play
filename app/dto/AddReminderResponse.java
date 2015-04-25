package dto;

/**
 * Created by Ronald on 2015/3/31.
 */
public class AddReminderResponse extends GeneralResponse{
    public ReminderInfo reminder;

    public AddReminderResponse(ReminderInfo reminder) {
        this.reminder = reminder;
    }
}
