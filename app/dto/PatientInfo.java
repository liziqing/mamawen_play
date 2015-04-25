package dto;

/**
 * Created by Ronald on 2015/3/3.
 */
public class PatientInfo {
    public Long userID;
    public String userName;
    public String jid;

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public PatientInfo(Long userID, String userName, String jid) {
        this.userID = userID;
        this.userName = userName;
        this.jid = jid;
    }

    public PatientInfo() {
    }
}
