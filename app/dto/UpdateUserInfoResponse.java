package dto;

/**
 * Created by Ronald on 2015/4/21.
 */
public class UpdateUserInfoResponse extends GeneralResponse{
    public UserInfo user;

    public UpdateUserInfoResponse(UserInfo user) {
        this.user = user;
    }
}
