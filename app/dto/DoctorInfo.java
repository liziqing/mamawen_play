package dto;

import models.Doctor;
import models.OutPatientTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ronald on 2015/3/11.
 */
public class DoctorInfo {
    public Long doctorID;
    public String name;
    public String title;
    public String department;
    public String cellPhone;
    public String hospital;
    public String plateUrl;
    public Integer level;
    public String avatar;
    public String licenseUrl;
    public String goodAt;
    public String background;
    public String achievement;
    public Boolean serveMore;
    public Boolean freeServing;
    public Integer imgTxtDiagFee;
    public Integer phnDiagFee;
    public Integer prvtDiagFee;
    public Integer diagPlusFee;
    public Integer voltrWeekday;
    public Integer freeCount;
    public List<OutPatientTimeInfo> workTimes = new ArrayList<>();

    public static DoctorInfo genDoctorInfo(Doctor doctor){
        DoctorInfo info = new DoctorInfo();
        info.doctorID = doctor.id;
        info.name = doctor.name;
        info.title = doctor.title;
        info.department = doctor.department.name;
        info.cellPhone = doctor.phoneNumber;
        info.hospital = doctor.inHospital.name;
        info.plateUrl = doctor.plateUrl;
        info.level = doctor.level;
        info.avatar = doctor.avatar;
        info.licenseUrl = doctor.licenceUrl;
        for (OutPatientTime op: doctor.workTimes){
            info.workTimes.add(new OutPatientTimeInfo(op.weekday, op.timeSegment));
        }
        info.goodAt = doctor.goodAt;
        info.achievement = doctor.achievement;
        info.background = doctor.background;
        info.serveMore = doctor.serveMore;
        info.freeServing = doctor.freeServing;
        info.imgTxtDiagFee = doctor.txtDiagFee;
        info.phnDiagFee = doctor.phnDiaFee;
        info.prvtDiagFee = doctor.prvtDiaFee;
        info.diagPlusFee = doctor.diagPlusFee;
        info.voltrWeekday = doctor.voltrDay;
        info.freeCount = doctor.freeCount;
        return info;
    }
}
