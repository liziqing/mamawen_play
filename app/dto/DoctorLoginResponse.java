package dto;

/**
 * Created by Ronald on 2015/3/13.
 */
public class DoctorLoginResponse extends GeneralResponse{
    public DoctorLoginInfo doctor;

    public DoctorLoginResponse(DoctorLoginInfo doctor) {
        this.doctor = doctor;
    }
}
