package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseChatMessageResponse {
    public ChatParticipantInfo sender;
    public ChatParticipantInfo receiver;
    public Long inquiryID;
    public Integer category;
}
