package dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

/**
 * Created by Ronald on 2015/3/3.
 */
public class InquiryMessage {
    public Long id;
    public String description;
    public String content;
    public List<String> photoes;
    public PatientInfo user;
    public Integer point;
    public String drug;
    public Long createTime;
    public String department;
    public Integer priority;

    public InquiryMessage() {
    }
}
