package dto;

/**
 * Created by Ronald on 2015/3/4.
 */
public class GeneralResponse {
    public Integer code = 0;
    public String message = "success";

    public GeneralResponse(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public GeneralResponse() {
    }
}
