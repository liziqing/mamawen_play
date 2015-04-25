package dto;

/**
 * Created by Ronald on 2015/4/16.
 */
public class AddHealthRecordResponse extends GeneralResponse{
    public HealthRecordInfo record;

    public AddHealthRecordResponse(HealthRecordInfo record) {
        this.record = record;
    }
}
