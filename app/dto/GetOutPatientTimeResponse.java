package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/8.
 */
public class GetOutPatientTimeResponse extends GeneralResponse{
    public List<OutPatientTimeInfo> workTimes;

    public GetOutPatientTimeResponse(List<OutPatientTimeInfo> workTimes) {
        this.workTimes = workTimes;
    }
}
