package dto;

/**
 * Created by Ronald on 2015/4/11.
 */
public class UserIsUsingPhoneResponse extends GeneralResponse{
    public Boolean exist;
    public UserInfo user;

    public UserIsUsingPhoneResponse(Boolean exist, UserInfo user) {
        this.exist = exist;
        this.user = user;
    }
}
