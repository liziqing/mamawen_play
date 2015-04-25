package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/4/8.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReminderDeleteRequest {
    public Long reminderID;
}
