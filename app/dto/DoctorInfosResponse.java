package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/3/11.
 */
public class DoctorInfosResponse extends GeneralResponse{
    public List<DoctorInfo> doctorInfos;

    public DoctorInfosResponse(List<DoctorInfo> doctorInfos) {
        this.doctorInfos = doctorInfos;
    }

    public DoctorInfosResponse() {
    }
}
