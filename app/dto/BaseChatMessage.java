package dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/24.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseChatMessage {
    public Integer category;
    public ChatParticipant sender;
    public ChatParticipant receiver;
    public Long inquiryID;
}
