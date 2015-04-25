package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/13.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginRequest {
    public String phoneNumber;
    public String password;
    public Integer deviceType;
}
