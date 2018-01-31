package fmssapi.report.service;

import fmssapi.report.model.ReportB;
import fmssapi.report.model.ReportBElement;
import fmssapi.report.model.ReportBTable;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-27 上午10:11
 */
public interface ReportBService {
    /**
     * 生成报表
     * @param curtainId
     * @param month
     */
    void createReport(Long curtainId, String month);

    /**
     * 获得报表信息
     * @param curtainId
     * @param month
     * @return
     */
    List<ReportB> findAll(Long curtainId, String month);

    /**
     * 装配展示用的资产负债表
     * @param reportBElementList
     * @param reportBList
     * @return
     */
    List<ReportBTable> getReportBTable(List<ReportBElement> reportBElementList, List<ReportB> reportBList);

    /**
     * 删除报表
     * @param curtainId
     * @param month
     */
    void removeAll(Long curtainId, String month);
}
