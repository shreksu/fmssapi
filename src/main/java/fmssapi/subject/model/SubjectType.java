package fmssapi.subject.model;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:41
 */
public enum SubjectType{

    t1 {public String getName(){
        return "资产";
    }},
    t2 {public String getName(){
        return "负债";
    }},
    t3 {public String getName(){
        return "权益";
    }},
    t5 {public String getName(){
        return "损益";
    }};
    public abstract String getName();





}
