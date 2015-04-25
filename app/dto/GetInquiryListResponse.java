package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/3/12.
 */
public class GetInquiryListResponse extends GeneralResponse {
    public List<InquiryMessage> inquiries;

    public GetInquiryListResponse(List<InquiryMessage> inquiries) {
        this.inquiries = inquiries;
    }
}
