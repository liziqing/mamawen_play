package dto;

import models.User;

/**
 * Created by Ronald on 2015/4/21.
 */
public class UserRegisterInfo {
    public Long userID;
    public String jid;
    public String name;
    public String cellPhone;
    public String xmppToken;
    public String sessionKey;

    public UserRegisterInfo(Long userID, String jid, String name, String cellPhone, String xmppToken, String sessionKey) {
        this.userID = userID;
        this.jid = jid;
        this.name = name;
        this.cellPhone = cellPhone;
        this.xmppToken = xmppToken;
        this.sessionKey = sessionKey;
    }

    public static UserRegisterInfo genUserRegisterResponse(User user){
        return new UserRegisterInfo(user.id, user.jid, user.name, user.phoneNumber, user.xmppToken,user.sessionKey);
    }
}
