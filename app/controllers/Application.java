package controllers;

import models.*;
import org.springframework.beans.factory.annotation.Autowired;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Controller;
import service.InquiryService;
import service.UserService;
//import repo.InquiryRepository;
//import repo.PatientRepository;

import java.util.List;
import java.util.Map;

/**
 * The main set of web services.
 */
@org.springframework.stereotype.Controller
public class Application extends BaseController {

    // We are using constructor injection to receive a repository to support our desire for immutability.
    @Autowired
    UserService userService;
    @Autowired
    InquiryService inquiryService;
    public Result index() {

        // For fun we save a new person and then find that one we've just saved. The id is auto generated by
        // the db so we know that we're round-tripping to the db and back in order to demonstrate something
        // interesting. Spring Data takes care of transactional concerns and the following code is all
        // executed on the same thread (a requirement of the JPA entity manager).

        /*Doctor doc1 = new Doctor();
        doc1.name = "张晓蕊";
        doc1.gender = "女";
        doc1.name = "zangxw4";
        doc1.birthday = new Date();
        //doc1.inHospital = new Hospital("北京大学人民医院", "三甲");
        //doc1.department = new Department("儿科");
        doc1.graduated = "北京大学医学部";
        doc1.title = "副主任医师";
        doc1.cellPhone = "1234567";
        doc1.level = 1;
        doc1.goodAt = "新生儿疾病、婴幼儿生长发育、儿科常见病。";
        doc1.achievement = "参与国家自然科学基金项目及多项院内科研及教学基金研究，在国家核心期刊及SCI杂志上发表了多篇中英文研究论著，多次荣获院、校、部级优秀教师称号，入选教育部青年英才计划。";
        doc1.recept = true;
        doc1.background = "博士毕业于北京大学医学部，现任儿科副主任医生，副教授，硕士生导师，研究方向为新生儿疾病及婴幼儿生长发育";
        Doctor persDoc = docRepo.save(doc1);

        Patient p = new Patient();
        p.name = "王茜";
        p.name = "wangq";
        Patient persPatient = patientRepo.save(p);

        Inquiry inq = new Inquiry();
        inq.description = "abcd";
        //inq.patient = persPatient;
        Inquiry persInq = inquiryRepo.save(inq);
        persInq.patient = persPatient;
        inquiryRepo.save(persInq);

        persInq.doctor = persDoc;
        inquiryRepo.save(persInq);*/
        //Deliver the index page with a message showing the id that was generated.

        //return ok(views.html.index.render("Found id: " + retrievedPerson.id + " of person/people"));
        return ok("ok");
    }
    public Result receptRoom(String username){
        if (username == null || username.isEmpty()){
            flash("error", "Please input a user name");
            return redirect(routes.Application.index());
        }
        return ok("hello");
    }
    public Result addPatients(){
        userService.testAddPaitient();
        return ok("Add successfully");
    }
    public Result addDoctor(){
        userService.testAddDoctor();
        return ok("Add successfully");
    }
    public Result testQuery() {
        //List<Inquiry> inquiries = inquiryService.getInquiriesInRange();
        boolean ret = inquiryService.testLock();
        if (ret) {
            return ok("ok");
        } else {
            return ok("fail");
        }
    }
    //public Result getRushEvents(){

    //}
    public Result hello(){
        return ok(views.html.Hello.render("Hello World!"));
    }

    public Result next(String param){
        session().put("data", "abcd");
        return ok(views.html.nextpage.render(param));
    }
    public Result testPost(){
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Map<String, String[]> data = body.asFormUrlEncoded();
        String[] contents = data.get("content");
        return ok(views.html.post_test.render(contents[0]));
    }
}
