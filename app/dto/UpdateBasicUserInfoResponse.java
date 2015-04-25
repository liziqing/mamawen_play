package dto;

/**
 * Created by Ronald on 2015/4/22.
 */
public class UpdateBasicUserInfoResponse extends GeneralResponse{
    public UserInfo user;

    public UpdateBasicUserInfoResponse(UserInfo user) {
        this.user = user;
    }
}
