package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportChatMessage extends BaseChatMessage{
    public String description;
    public String suggestion;
}
