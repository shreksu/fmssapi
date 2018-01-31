package fmssapi.report.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fmssapi.subject.model.Subject;
import lombok.Data;

import java.util.List;

/**
 * 利润表元素
 * @author suyuanyang
 * @create 2018-01-25 下午2:06
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportPElement {

    private Long id;

    private String comFlag;
    //编号，控制元素之间的关系和顺序
    private String code;
    //名称
    private String name;
    //行次
    private Integer row;
    //类型(汇总，计算)
    private String type;

    /**************
     * 展示用
     */
    private List<Subject> subjectList;

    private String[] subjectCodes;

    private Double monthNum;

    private Double yearNum;
}
