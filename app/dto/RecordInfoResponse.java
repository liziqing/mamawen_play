package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/3/21.
 */
public class RecordInfoResponse extends GeneralResponse{
    public UserRecordInfo record;

    public RecordInfoResponse(UserRecordInfo record) {
        this.record = record;
    }
}
