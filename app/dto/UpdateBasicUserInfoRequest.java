package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/4/22.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateBasicUserInfoRequest {
    public String name;
    public String nickname;
    public String phoneNumber;
    public String phase;
}
