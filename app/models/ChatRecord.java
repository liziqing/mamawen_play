package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/3/24.
 */
@Entity
@Table(name = "chat_record",
       indexes = {@Index(name = "chat_sender_id", columnList = "senderID"),
                  @Index(name = "chat_receiver_id", columnList = "receiverID"),
                  @Index(name = "chat_create_time", columnList = "createTime")}
)
public class ChatRecord {
    @Id
    @GeneratedValue
    Long id;

    public Integer category;
    public Integer subCategory;
    public String content;
    public Long senderID;
    public Long receiverID;
    public Long doctorID;
    public Long inquiryID;
    public Long reportID;
    public Long createTime;
    public Integer servingMark;
    public Integer efftMark;
    public String comment;
}
