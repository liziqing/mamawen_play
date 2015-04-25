package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/12.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetInquiryListRequest {
    public String department;
}
