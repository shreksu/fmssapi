package fmssapi.report.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 资产负债表
 * @author suyuanyang
 * @create 2018-01-27 上午9:57
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportB {

    public ReportB(){}
    public ReportB(Long curtainId,String month,Long reportBElementId){
        this.curtainId=curtainId;
        this.month=month;
        this.reportBElementId=reportBElementId;
    }

    private Long id;

    private Long curtainId;

    private String month;

    private Long reportBElementId;

    private Double initNum;

    private Double lastNum;

}
