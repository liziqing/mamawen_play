package dto;

/**
 * Created by Ronald on 2015/3/13.
 */
public class UserLoginResponse extends GeneralResponse{
    public UserLoginInfo user;

    public UserLoginResponse(UserLoginInfo user) {
        this.user = user;
    }
}
