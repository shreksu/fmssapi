package fmssapi.report.action;

import fmssapi.report.model.ReportBElement;
import fmssapi.report.service.ReportBElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产负债表元素
 * @author suyuanyang
 * @create 2018-01-25 上午10:22
 */
@RestController
@RequestMapping("/reportbelement")
public class ReportBElementController {

    @Autowired
    ReportBElementService reportBElementService;

    /**
     * 获得帐套下的所有科目
     * @param comFlag
     * @return
     */
    @RequestMapping(value = "getReportBElements", method = RequestMethod.GET)
    public List<ReportBElement> getReportBElements(String comFlag){
        return reportBElementService.findAllByComFlag(comFlag);
    }


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Map<String,String> add(@RequestBody ReportBElement reportBElement){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportBElementService.add(reportBElement));
        return result;
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public Map<String,String> remove(Long id){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportBElementService.remove(id));
        return result;
    }

    /**
     * 配置科目关系
     * @param reportBElement 帐套
     * @return
     */
    @RequestMapping(value = "updateReportBSubjects", method = RequestMethod.POST)
    public Map<String,String> updateReportBSubjects(@RequestBody ReportBElement reportBElement){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportBElementService.updateReportBSubjects(reportBElement.getId(), reportBElement.getSubjectCodes()));
        return result;
    }
}
