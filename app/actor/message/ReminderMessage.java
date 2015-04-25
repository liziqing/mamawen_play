package actor.message;

import models.Doctor;
import models.User;

/**
 * Created by Ronald on 2015/4/2.
 */
public class ReminderMessage {
    public User user;
    public Doctor doctor;
    public String title;
    public String content;
    public String patientName;
    public Boolean remindMe = true;
}
