package dto;

import models.Patient;
import models.User;

/**
 * Created by Ronald on 2015/3/13.
 */
public class UserLoginInfo {
    public Long userID;
    public String name;
    public String nickname;
    public String cellPhone;
    public Integer point;
    public String avatar;
    public String jid;
    public String xmpToken;
    public String sessionKey;
    public String phase;
    public String lastMensDate;
    public Integer mensCycle;
    public String exptBirthday;
    public Double height;
    public Double weightBeforePreg;
    public static UserLoginInfo genUserLoginInfo(User user){
        UserLoginInfo info = new UserLoginInfo();
        info.userID = user.id;
        info.jid = user.jid;
        info.xmpToken = user.xmppToken;
        info.name =user.name;
        info.cellPhone = user.phoneNumber;
        info.nickname = user.nickame;
        info.avatar = user.avatar;
        info.sessionKey = user.sessionKey;
        info.phase = UserInfo.getPhaseFromStatus(user.status);
        if (user.patients != null) {
            for (Patient pt : user.patients){
                if (pt.status.equals(user.status)){
                    info.lastMensDate = pt.lastMensDate;
                    info.mensCycle = pt.mensCycle;
                    info.exptBirthday = pt.exptBirthday;
                    info.height = pt.height;
                    info.weightBeforePreg = pt.pastWeight;
                    break;
                }
            }
        }
        return info;
    }
}
