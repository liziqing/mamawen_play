package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/16.
 */
public class GetHealthRecordsResponse extends GeneralResponse {
    public List<HealthRecordInfo> records;

    public GetHealthRecordsResponse(List<HealthRecordInfo> records) {
        this.records = records;
    }
}
