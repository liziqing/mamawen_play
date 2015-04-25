package dto;

/**
 * Created by Ronald on 2015/3/27.
 */
public class OutPatientTimeInfo {
    public Integer weekday;
    public Integer timeSegment;

    public OutPatientTimeInfo(Integer weekday, Integer timeSegment) {
        this.weekday = weekday;
        this.timeSegment = timeSegment;
    }

    public OutPatientTimeInfo() {
    }
}
