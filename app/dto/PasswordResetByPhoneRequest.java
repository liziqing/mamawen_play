package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/26.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PasswordResetByPhoneRequest {
    public String phoneNumber;
    public String newPassword;
}
