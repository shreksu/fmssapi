package fmssapi.report.service;

import fmssapi.report.mapper.ReportPElementMapper;
import fmssapi.report.model.ReportP;
import fmssapi.report.model.ReportPElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2018-01-25 下午2:18
 */
@Service
@Transactional
public class ReportPElementServiceImpl implements  ReportPElementService{

    @Autowired
    ReportPElementMapper reportPElementMapper;

    @Override
    public List<ReportPElement> findAllByComFlag(String comFlag) {
        List<ReportPElement> reportBElementList = reportPElementMapper.findAllByComFlag(comFlag);
        for(ReportPElement ele : reportBElementList){
            ele.setSubjectCodes(reportPElementMapper.getSubjectCodes(ele.getId()));
        }
        return reportBElementList;
    }

    @Override
    public String add(ReportPElement reportPElement) {
        String message;
        if (reportPElement.getId() == null) {//新增
            reportPElementMapper.insertByObject(reportPElement);
        } else {
            reportPElementMapper.updateByObject(reportPElement);
        }
        message = "0";
        return message;
    }

    @Override
    public String remove(Long id) {
        reportPElementMapper.deleteSubjectsById(id);
        reportPElementMapper.deleteById(id);
        return "0";
    }

    @Override
    public String updateReportBSubjects(Long id, String[] subjectCodes) {
        reportPElementMapper.deleteSubjectsById(id);
        if(subjectCodes!=null && subjectCodes.length>0) {
            reportPElementMapper.insertSubjectsById(id, subjectCodes);
        }
        return "0";
    }

    @Override
    public String[] getSubjectCodes(Long id) {
        return reportPElementMapper.getSubjectCodes(id);
    }

    @Override
    public void assemblingList(List<ReportP> reportPList, List<ReportPElement> reportPElementList) {
        Map<Long,Double[]> map = new HashMap<>();
        for(ReportP p : reportPList){
         map.put(p.getReportPElementId(),new Double[]{p.getMonthNum(),p.getYearNum()});
        }
        for(ReportPElement element : reportPElementList){
            if(map.get(element.getId())!=null) {
                element.setMonthNum(map.get(element.getId())[0]);
                element.setYearNum(map.get(element.getId())[1]);
            }
        }
    }
}
