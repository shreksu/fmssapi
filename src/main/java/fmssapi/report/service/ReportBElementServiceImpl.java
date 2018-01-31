package fmssapi.report.service;

import fmssapi.report.mapper.ReportBElementMapper;
import fmssapi.report.model.ReportBElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2018-01-25 上午10:02
 */
@Service
@Transactional
public class ReportBElementServiceImpl implements ReportBElementService {

    @Autowired
    ReportBElementMapper reportBElementMapper;

    @Override
    public List<ReportBElement> findAllByComFlag(String comFlag) {
        List<ReportBElement> reportBElementList = reportBElementMapper.findAllByComFlag(comFlag);
        for(ReportBElement ele : reportBElementList){
            ele.setSubjectCodes(reportBElementMapper.getSubjectCodes(ele.getId()));
        }
        return reportBElementList;
    }

    @Override
    public String add(ReportBElement reportBElement) {
        String message;
        if (reportBElement.getId() == null) {//新增
            reportBElementMapper.insertByObject(reportBElement);
        } else {
            reportBElementMapper.updateByObject(reportBElement);
        }
        message = "0";
        return message;
    }

    @Override
    public String remove(Long id) {
        reportBElementMapper.deleteSubjectsById(id);
        reportBElementMapper.deleteById(id);
        return "0";
    }

    @Override
    public String updateReportBSubjects(Long id, String[] subjectCodes) {
        reportBElementMapper.deleteSubjectsById(id);
        if(subjectCodes!=null && subjectCodes.length>0) {
            reportBElementMapper.insertSubjectsById(id, subjectCodes);
        }
        return "0";
    }

    @Override
    public String[] getSubjectCodes(Long reportBElementId) {
        return reportBElementMapper.getSubjectCodes(reportBElementId);
    }
}
