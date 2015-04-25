package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorLoginRequest {
    public String phoneNumber;
    public String password;
    public Integer devceType;
}
