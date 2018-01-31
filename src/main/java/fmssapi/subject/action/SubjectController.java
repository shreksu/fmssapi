package fmssapi.subject.action;

import com.mysql.jdbc.StringUtils;
import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainService;
import fmssapi.subject.model.Subject;
import fmssapi.subject.model.SubjectTree;
import fmssapi.subject.model.SubjectType;
import fmssapi.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:11
 */
@RestController
@RequestMapping("/subject")
public class SubjectController {


    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CurtainService curtainService;

    /**
     * 获得帐套下的所有科目
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "findAllByCurtainId", method = RequestMethod.GET)
    public List<Subject> findAllByCurtainId(Long curtainId){
        return subjectService.findAllByCurtainId(curtainId);
    }
    /****
    @RequestMapping(value = "getTypesByCode", method = RequestMethod.GET)
    public Map<String,String> getTypesByCode(String code){
        SubjectType subjectType = subjectService.getSubjectType(code);
        SubjectBalance subjectBalance = subjectService.getSubjectBalance(code);
        Map<String,String> result = new HashMap<>();
        result.put("type",subjectType.getName());
        result.put("balance",subjectBalance.getName());
        return result;
    }
****/
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Map<String,String> add(@RequestBody Subject subject){
        Map<String,String> result = new HashMap<>();
        result.put("errno",subjectService.add(subject));
        return result;
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public Map<String,String> remove(Long id){
        Map<String,String> result = new HashMap<>();
        result.put("errno",subjectService.remove(id));
        return result;
    }


    @RequestMapping(value = "copySubject", method = RequestMethod.POST)
    public Map<String,String> copySubject(@RequestBody Subject subject){
        Map<String,String> result = new HashMap<>();
        result.put("errno",subjectService.copySubject(subject.getToCurtainId(), subject.getSubjectIds()));
        return result;
    }

    @RequestMapping(value = "getFilterTypes", method = RequestMethod.GET)
     public List<Map<String,String>> getFilterTypes(){
        List<Map<String,String>> list = new ArrayList<>();
        for(SubjectType obj : SubjectType.values()){
            Map<String,String> map = new HashMap<>();
            map.put("value",obj.toString());
            map.put("text",obj.getName());
            list.add(map);
        }
        return list;
    }

    @RequestMapping(value = "getSubjectsCode1List", method = RequestMethod.GET)
    public List<Subject> getSubjectsCode1List(String comFlag){
        Curtain curtain = curtainService.getCurtainByCode(comFlag, "01");
        return subjectService.getSubjectsCode1List(curtain.getId());
    }

    @RequestMapping(value = "validateCode", method = RequestMethod.GET)
    public Map<String,String> validateCode(Long curtainId,String code){
        Map<String,String> result = new HashMap<>();
        Integer level = subjectService.getSubjectLevel(code);
        if(level == 99){
            result.put("errno","1");
            result.put("message","科目编号长度不合规");
            return result;
        }else{
            result.put("level",String.valueOf(level));
        }
        SubjectType subjectType = subjectService.getSubjectType(code);
        if(subjectType == null){
            result.put("errno","1");
            result.put("message","科目编号首位数字不合规");
            return result;
        }else{
            result.put("type",subjectType.getName());
        }
        Map<String,String> resultMap = subjectService.getSubjectParent(curtainId,code);
        String parent = resultMap.get("parent");
        if(StringUtils.isNullOrEmpty(parent)){
            result.put("errno","1");
            result.put("message", "请先录入父类科目");
            return result;
        }else{
            result.put("parent",parent);
            result.put("balance",resultMap.get("balance"));
        }
        Subject subject = subjectService.getSubjectByCurtainAndCode(curtainId,code);
        if(subject!=null){
            result.put("errno","1");
            result.put("message", "录入的科目编号重复");
            return result;
        }
        result.put("errno","0");
        return result;
    }

    @RequestMapping(value = "getSubjectsTree", method = RequestMethod.GET)
    public List<SubjectTree> getSubjectsTree(Long curtainId){
        return subjectService.getSubjectsTree(curtainId);
    }

}
