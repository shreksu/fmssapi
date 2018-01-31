package fmssapi.report.service;

import fmssapi.report.model.ReportBElement;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-25 上午10:02
 */
public interface ReportBElementService {
    /**
     * 获得所有元素
     * @param comFlag
     * @return
     */
    List<ReportBElement> findAllByComFlag(String comFlag);

    /**
     * 新增，保存
     * @param reportBElement
     * @return
     */
    String add(ReportBElement reportBElement);

    /**
     * 删除
     * @param id
     * @return
     */
    String remove(Long id);

    /**
     * 配置科目
     * @param id
     * @param subjectCodes
     * @return
     */
    String updateReportBSubjects(Long id, String[] subjectCodes);

    /**
     * 根据计算元素获得对应的科目编号
     * @param reportBElementId
     * @return
     */
    String[] getSubjectCodes(Long reportBElementId);
}
