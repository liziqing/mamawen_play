package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/18.
 */
public class InquiryPushChatResponse extends BaseChatMessageResponse{
    public String description;
    public String content;
    public List<String> photoes;
    public Integer point;
    public String drug;
    public Long createTime;
    public String department;
    public Integer priority;
}
