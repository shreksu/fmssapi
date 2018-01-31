package fmssapi.report.action;

import fmssapi.report.model.ReportPElement;
import fmssapi.report.service.ReportPElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2018-01-25 下午2:20
 */
@RestController
@RequestMapping("/reportpelement")
public class ReportPElementController {

    @Autowired
    ReportPElementService reportPElementService;

    /**
     * 获得帐套下的所有科目
     * @param comFlag
     * @return
     */
    @RequestMapping(value = "getReportPElements", method = RequestMethod.GET)
    public List<ReportPElement> getReportBElements(String comFlag){
        return reportPElementService.findAllByComFlag(comFlag);
    }


    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Map<String,String> add(@RequestBody ReportPElement reportPElement){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportPElementService.add(reportPElement));
        return result;
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public Map<String,String> remove(Long id){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportPElementService.remove(id));
        return result;
    }

    /**
     * 配置科目关系
     * @param reportPElement 帐套
     * @return
     */
    @RequestMapping(value = "updateReportPSubjects", method = RequestMethod.POST)
    public Map<String,String> updateReportBSubjects(@RequestBody ReportPElement reportPElement){
        Map<String,String> result = new HashMap<>();
        result.put("errno",reportPElementService.updateReportBSubjects(reportPElement.getId(), reportPElement.getSubjectCodes()));
        return result;
    }
}
