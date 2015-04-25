package dto;

/**
 * Created by Ronald on 2015/3/19.
 */
public class DoctorRegisterResponse extends GeneralResponse {
    public DoctorRegisterInfo doctor;

    public DoctorRegisterResponse(DoctorRegisterInfo doctor) {
        this.doctor = doctor;
    }
}
