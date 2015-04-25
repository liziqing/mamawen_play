package models;

import javax.persistence.*;

/**
 * Created by Ronald on 2015/3/20.
 */
@Entity
@Table(name = "inquiry_report")
public class InquiryReport {
    @Id
    @GeneratedValue
    public Long id;

    public String description;
    public String suggestion;
    @OneToOne(mappedBy = "report", fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    public Inquiry inquiry;
    public Long createTime;
}
