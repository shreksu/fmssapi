package fmssapi.report.service;

import fmssapi.report.model.ReportP;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-30 上午9:08
 */
public interface ReportPService {

    /**
     * 生成利润表
     * @param curtainId
     * @param month
     */
    void createReport(Long curtainId, String month);

    /**
     * 获得利润表数据
     * @param curtainId
     * @param month
     * @return
     */
    List<ReportP> findAll(Long curtainId, String month);

    /**
     * 删除报表
     * @param curtainId
     * @param month
     */
    void removeAll(Long curtainId, String month);
}
