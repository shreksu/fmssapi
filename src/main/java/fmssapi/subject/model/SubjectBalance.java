package fmssapi.subject.model;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:41
 */
public enum SubjectBalance {


    D {public String getName(){
        return "借方";
    }},
    C {public String getName(){
        return "贷方";
    }};
    public abstract String getName();

}
