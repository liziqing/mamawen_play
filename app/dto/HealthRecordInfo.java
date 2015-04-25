package dto;

import models.HealthRecord;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ronald on 2015/4/16.
 */
public class HealthRecordInfo {
    public Long id;
    public String category;
    public String subCategory;
    public Double value;
    public Integer cycle;
    public String recordDate;
    public String createTime;
    public String updatedTime;
    public static HealthRecordInfo genRecordInfo(HealthRecord record){
        Date cdt = new Date();
        cdt.setTime(record.createTime);
        Date udt = new Date();
        udt.setTime(record.updateTime);
        return new HealthRecordInfo(record.id, record.category, record.subCategory, record.value, record.cycle, record.recordDate, cdt, udt);
    }

    public HealthRecordInfo() {
    }

    public HealthRecordInfo(Long id, String category, String subCategory, Double value, Integer cycle, Date recordDate,Date createTime, Date updatedTime) {
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.value = value;
        this.cycle = cycle;
        SimpleDateFormat smt = new SimpleDateFormat("yyyy-MM-dd");
        if (recordDate != null) {
            this.recordDate = smt.format(recordDate);
        }
        if (createTime != null){
            this.updatedTime= smt.format(createTime);
        }
        if (updatedTime != null){
            this.updatedTime= smt.format(updatedTime);
        }
    }
}
