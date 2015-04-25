package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/19.
 */
public class GetFriendRecordInfoResponse extends GeneralResponse{
    public List<FriendHealthRecordInfo> records;

    public GetFriendRecordInfoResponse(List<FriendHealthRecordInfo> records) {
        this.records = records;
    }
}
