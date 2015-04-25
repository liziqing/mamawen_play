package models;

/**
 * Created by Ronald on 2015/4/19.
 */
public class FriendsHealthRecord {
    public User user;
    public HealthRecord record;

    public FriendsHealthRecord(User user, HealthRecord record) {
        this.user = user;
        this.record = record;
    }

    public FriendsHealthRecord() {
    }
}
