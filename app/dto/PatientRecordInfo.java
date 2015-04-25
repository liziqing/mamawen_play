package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/3/21.
 */
public class PatientRecordInfo {
    public Long patientID;
    public String name;
    public String gender;
    public int age;
    public List<InquiryResult> inquiries;
}
