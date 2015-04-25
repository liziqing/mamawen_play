package dto;

/**
 * Created by Ronald on 2015/3/27.
 */
public class UserInfoResponse extends GeneralResponse{
    public UserInfo user;

    public UserInfoResponse(UserInfo user) {
        this.user = user;
    }

    public UserInfoResponse() {
    }
}
