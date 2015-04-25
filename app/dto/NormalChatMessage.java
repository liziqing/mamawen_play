package dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/24.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NormalChatMessage extends BaseChatMessage{
    public Integer subCategory;
    public String content;
}
