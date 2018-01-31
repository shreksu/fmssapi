package fmssapi.report.service;

import fmssapi.report.model.ReportP;
import fmssapi.report.model.ReportPElement;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-25 下午2:17
 */
public interface ReportPElementService {

    /**
     * 获得所有元素
     * @param comFlag
     * @return
     */
    List<ReportPElement> findAllByComFlag(String comFlag);

    /**
     * 新增，保存
     * @param reportPElement
     * @return
     */
    String add(ReportPElement reportPElement);

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
     * 获得对应的科目
     * @param id
     * @return
     */
    String[] getSubjectCodes(Long id);

    /**
     * 装配数据，用于显示列表
     * @param reportPList
     * @param reportPElementList
     * @return
     */
    void assemblingList(List<ReportP> reportPList, List<ReportPElement> reportPElementList);
}
