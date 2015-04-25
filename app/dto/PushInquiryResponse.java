package dto;

/**
 * Created by Ronald on 2015/3/11.
 */
public class PushInquiryResponse extends GeneralResponse{
    public Long inquiryID;

    public PushInquiryResponse(Long inquiryID) {
        this.inquiryID = inquiryID;
    }
}
