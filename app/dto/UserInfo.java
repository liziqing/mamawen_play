package dto;

import models.Patient;
import models.User;

/**
 * Created by Ronald on 2015/3/27.
 */
public class UserInfo {
    public Long userID;
    public String name;
    public String nickname;
    public String avatar;
    public String cellPhone;
    public String phase;
    public String lastMensDate;
    public Integer mensCycle;
    public String exptBirthday;
    public Double height;
    public Double weightBeforePreg;

    public static UserInfo genUserInfo(User user){
        UserInfo info = new UserInfo();
        info.userID = user.id;
        info.name =user.name;
        info.cellPhone = user.phoneNumber;
        info.nickname = user.nickame;
        info.avatar = user.avatar;
        info.phase = getPhaseFromStatus(user.status);
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
    public static int getStatusFromPhase(String phase){
        switch (phase){
            case "备孕":
                return 1;
            case "孕期":
                return 2;
            case "育儿":
                return 3;
            default:
                return 1;
        }
    }
    public static  String getPhaseFromStatus(int status){
        switch (status){
            case 1:
                return "备孕";
            case 2:
                return "孕期";
            case 3:
                return "育儿";
            default:
                return "备孕";
        }
    }
}
