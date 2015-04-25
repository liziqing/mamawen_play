package dto;

/**
 * Created by Ronald on 2015/3/15.
 */
public class RegisterDoctorTestResponse extends GeneralResponse {
    public Long doctorID;

    public RegisterDoctorTestResponse(Long doctorID) {
        this.doctorID = doctorID;
    }
}
