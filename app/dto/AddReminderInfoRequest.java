package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by Ronald on 2015/3/31.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddReminderInfoRequest {
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
