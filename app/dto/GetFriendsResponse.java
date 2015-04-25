package dto;

import java.util.List;

/**
 * Created by Ronald on 2015/4/10.
 */
public class GetFriendsResponse extends GeneralResponse{
    public List<UserInfo> friends;

    public GetFriendsResponse(List<UserInfo> friends) {
        this.friends = friends;
    }
}
