package models;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Ronald on 2015/3/19.
 */
@Entity
@Table(name = "outpatienttime",
       indexes = {@Index(name = "index_weekday", columnList = "weekday"),
                  @Index(name = "index_time_seg", columnList = "timeSegment")
       }
)
public class OutPatientTime {
    @Id
    @GeneratedValue
    public Long id;
    @Column(name = "weekday", nullable = false)
    public Integer weekday;
    @Column(name = "timeSegment", nullable = false)
    public Integer timeSegment;

    @ManyToMany(cascade = CascadeType.REFRESH, mappedBy = "workTimes", fetch = FetchType.EAGER)
    public List<Doctor> doctor;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OutPatientTime that = (OutPatientTime) o;

        if (timeSegment != null ? !timeSegment.equals(that.timeSegment) : that.timeSegment != null) return false;
        if (weekday != null ? !weekday.equals(that.weekday) : that.weekday != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = weekday != null ? weekday.hashCode() : 0;
        result = 31 * result + (timeSegment != null ? timeSegment.hashCode() : 0);
        return result;
    }

    public OutPatientTime(Integer weekday, Integer timeSegment) {
        this.weekday = weekday;
        this.timeSegment = timeSegment;
    }

    public OutPatientTime() {
    }
}
