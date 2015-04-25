package actor.message;

/**
 * Created by Ronald on 2015/3/12.
 */
public class ReceptInquiry {
    public Long doctorID;
    public Long inquiryID;

    public ReceptInquiry(Long doctorID, Long inquiryID) {
        this.doctorID = doctorID;
        this.inquiryID = inquiryID;
    }
}
