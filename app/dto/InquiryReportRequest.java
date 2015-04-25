package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/21.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InquiryReportRequest {
    public Long inquiryID;
    public String description;
    public String suggestion;
}
