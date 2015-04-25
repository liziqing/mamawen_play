package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/4/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserMarkRequest {
    public Long inquiryID;
    public Long doctorID;
    public Integer servingMark;
    public Integer efftMark;
    public String comment;
}
