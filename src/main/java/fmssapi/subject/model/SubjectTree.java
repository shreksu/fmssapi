package fmssapi.subject.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-21 下午1:00
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubjectTree {

    public SubjectTree(Subject subject){
        this.id = subject.getId();
        this.label = subject.getName()+"("+subject.getCode()+")";
        this.fullName = subject.getName();
        this.code = subject.getCode();
        this.children = new ArrayList<>();
    }
    public SubjectTree(){}

    private Long id;

    private String code;

    private String label;

    private String fullName;

    List<SubjectTree> children;
}
