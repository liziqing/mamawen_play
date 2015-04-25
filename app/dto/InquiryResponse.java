package dto;

/**
 * Created by Ronald on 2015/3/4.
 */
public class InquiryResponse extends GeneralResponse{
    public InquiryMessage inquiry;

    public InquiryResponse(InquiryMessage inquiry) {
        this.inquiry = inquiry;
    }
}
