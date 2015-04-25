package models;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Ronald on 2015/3/31.
 */
@Entity
@Table(name = "reminder",
        indexes = {@Index(name = "reminder_index_start", columnList = "startTime"),
       @Index(name="reminder_index_start", columnList = "endTime"),
        @Index(name = "reminder_index_time", columnList = "remindTime")})
public class Reminder {
    @Id
    @GeneratedValue
    public Long id;
    public String title;
    public String patientName;
    public String content;

    public Date startTime;
    public Date endTime;
    public Boolean remindMe = false;
    public String remindTime;

    @ManyToOne(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER,optional=true)
    public User user;
    @ManyToOne(cascade = CascadeType.REFRESH, fetch=FetchType.EAGER,optional=true)
    public Doctor doctor;
}
