package dto;

/**
 * Created by Ronald on 2015/3/20.
 */
public class DoctorInfoResponse extends GeneralResponse{
    public DoctorInfo doctor;

    public DoctorInfoResponse(DoctorInfo doctor) {
        this.doctor = doctor;
    }
}
