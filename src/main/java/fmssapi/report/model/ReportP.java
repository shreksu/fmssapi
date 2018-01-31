package fmssapi.report.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * 利润表
 * @author suyuanyang
 * @create 2018-01-30 上午9:00
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportP {

    public ReportP(){}
    public ReportP(Long curtainId,String month,Long reportPElementId){
        this.curtainId=curtainId;
        this.month=month;
        this.reportPElementId=reportPElementId;
    }

    private Long id;

    private Long curtainId;

    private String month;

    private Long reportPElementId;

    private Double monthNum;

    private Double yearNum;
}
