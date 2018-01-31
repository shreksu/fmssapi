package fmssapi.report.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import fmssapi.subject.model.Subject;
import lombok.Data;

import java.util.List;

/**
 * 资产负债表的元素
 * @author suyuanyang
 * @create 2018-01-25 上午9:35
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportBElement {

    private Long id;

    private String comFlag;
    //编号，控制元素之间的关系和顺序
    private String code;
    //名称
    private String name;
    //行次
    private Integer row;
    //类型(标题T，汇总N，计算C, 空行B)
    private String type;
    //计算公式
    private String calFun;
    /**************
     * 展示用
     */
    private List<Subject> subjectList;

    private String[] subjectCodes;
}
