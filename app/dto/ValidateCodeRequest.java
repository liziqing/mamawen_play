package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Ronald on 2015/3/19.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidateCodeRequest {
    public String phoneNumber;
    public String code;
}
