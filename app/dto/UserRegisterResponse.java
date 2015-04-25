package dto;

import models.User;

/**
 * Created by Ronald on 2015/3/15.
 */
public class UserRegisterResponse extends GeneralResponse{
    public UserRegisterInfo user;

    public UserRegisterResponse(UserRegisterInfo user) {
        this.user = user;
    }
}
