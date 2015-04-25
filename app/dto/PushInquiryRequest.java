package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by Ronald on 2015/3/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PushInquiryRequest {
    public String department;
    public List<String> keyWords;
    public String questionTo;
    public String textContent;
    public String description;
    public String drug;
}
