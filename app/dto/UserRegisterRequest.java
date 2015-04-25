package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRegisterRequest {
    public String phoneNumber;
    public String name;
    public String password;
    public String phase;
    public Integer deviceType;
}
