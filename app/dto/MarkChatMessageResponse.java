package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/4/22.
 */
public class MarkChatMessageResponse extends BaseChatMessageResponse{
    public Integer servingMark;
    public Integer efftMark;
    public String comment;
}
