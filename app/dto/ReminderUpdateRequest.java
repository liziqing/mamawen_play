package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/4/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReminderUpdateRequest {
    public Long reminderID;
    public String title;
    public String patientName;
    public String content;
    public String startDate;
    public String endDate;
    public String remindTime;
    public Long doctorID;
    public Boolean remindMe;
    public Long userID;
}
