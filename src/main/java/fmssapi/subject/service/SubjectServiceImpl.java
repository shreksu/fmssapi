package fmssapi.subject.service;

import fmssapi.curtain.service.CurtainService;
import fmssapi.subject.mapper.SubjectMapper;
import fmssapi.subject.model.Subject;
import fmssapi.subject.model.SubjectTree;
import fmssapi.subject.model.SubjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:12
 */
@Service
@Transactional
public class SubjectServiceImpl implements SubjectService {

    @Autowired
    private SubjectMapper subjectMapper;
    @Autowired
    private CurtainService curtainService;

    @Override
    public List<Subject> findAllByCurtainId(Long curtainId) {
        List<Subject> subjectList = subjectMapper.findAllByCurtainId(curtainId);
        for(Subject subject : subjectList){
            subject.setLevel(this.getSubjectLevel(subject.getCode()));
            subject.setType(this.getSubjectType(subject.getCode()).getName());
            //subject.setBalance(this.getSubjectBalance(subject.getCode()).getName());
        }
        return subjectList;
    }

    @Override
    public List<Subject> findSimpleAllByCurtainId(Long curtainId) {
        return subjectMapper.findAllByCurtainId(curtainId);
    }

    @Override
    public Subject findById(Long id) {
        return subjectMapper.findById(id);
    }


    @Override
    public Integer getSubjectLevel(String code) {
        switch (code.length()) {
            case 4: return 1;
            case 7: return 2;
            case 10: return 3;
        }
        return 99;
    }

    @Override
    public SubjectType getSubjectType(String code) {
        SubjectType subjectType = null;
        try {
            subjectType = SubjectType.valueOf("t" + code.substring(0, 1));
        }catch (Exception e){

        }
        return subjectType;
    }
    /**
    @Override
    public SubjectBalance getSubjectBalance(String code) {
        String firstChar = code.substring(0, 1);
        SubjectBalance subjectBalance;
        if("1".equals(firstChar)){
            subjectBalance = SubjectBalance.D;
        }else if("2".equals(firstChar) || "3".equals(firstChar)){
            subjectBalance = SubjectBalance.C;
        }else{
            subjectBalance = SubjectBalance.T;
        }
        return subjectBalance;
    }
     **/

    @Override
    public String add(Subject subject) {
        Subject obj = this.getSubjectByCurtainAndCode(subject.getCurtainId(),subject.getCode());
        String message;
        if(obj!=null && obj.getId().longValue()!=subject.getId()){
            message = "1";
        }else {
            if (subject.getId() == null) {//新增
                subjectMapper.insertByObject(subject);
            } else {
                subjectMapper.updateByObject(subject);
            }
            message = "0";
        }
        return message;
    }

    @Override
    public String copySubject(Long toCurtainId, Long[] subjectIds) {
        if(subjectIds==null) return "0";
        for(Long subjectId : subjectIds){
            Subject obj = subjectMapper.findById(subjectId);
            Subject temp = this.getSubjectByCurtainAndCode(toCurtainId, obj.getCode());
            if(temp==null) {
                Subject subject = subjectMapper.findById(subjectId).clone(toCurtainId);
                subjectMapper.insertByObject(subject);
            }
        }
        return "0";
    }

    @Override
    public Subject getSubjectByCurtainAndCode(Long curtainId, String code) {
        return subjectMapper.getSubjectByCurtainAndCode(curtainId,code);
    }

    @Override
    public Map<String,String> getSubjectParent(Long curtainId, String code) {
        Map<String,String> result = new HashMap<>();
        String parent = null,parentCode=null;
        String balance = null;
        if(code.length()==4){
            parent = "无";
        }else if(code.length()==7){
            parentCode = code.substring(0,4);
        }else if(code.length()==10){
            parentCode = code.substring(0,7);
        }
        if(parentCode!=null){
            Subject subject = this.getSubjectByCurtainAndCode(curtainId,parentCode);
            if(subject!=null){
                parent = subject.getName();
                balance = subject.getBalance();
            }
        }
        result.put("parent",parent);
        result.put("balance",balance);
        return result;
    }

    @Override
    public String remove(Long id) {
        subjectMapper.deleteById(id);
        return "0";
    }

    @Override
    public List<SubjectTree> getSubjectsTree(Long curtainId) {
        List<SubjectTree> subjectTrees = new ArrayList<>();
        Map<String,SubjectTree> map = new HashMap<>();
        List<Subject> subjectList = this.findAllByCurtainId(curtainId);
        for(SubjectType obj : SubjectType.values()){
            SubjectTree tree = new SubjectTree();
            tree.setLabel(obj.getName());
            tree.setId(0L);
            tree.setCode("");
            tree.setChildren(new ArrayList<SubjectTree>());
            subjectTrees.add(tree);
            map.put(obj.toString(), tree);
        }
        for(Subject sub : subjectList){
            if(sub.getCode().length() == 4){
                SubjectTree tree = new SubjectTree(sub);
                map.put(sub.getCode(), tree);
                map.get("t" + sub.getCode().substring(0,1)).getChildren().add(tree);
            }else if(sub.getCode().length() == 7){
                SubjectTree tree = new SubjectTree(sub);
                map.put(sub.getCode(), tree);
                map.get(sub.getCode().substring(0,4)).getChildren().add(tree);
                tree.setFullName(map.get(sub.getCode().substring(0,4)).getFullName()+"/"+tree.getFullName());
            }else if(sub.getCode().length() == 10){
                SubjectTree tree = new SubjectTree(sub);
                tree.setChildren(null);
                map.put(sub.getCode(), tree);
                map.get(sub.getCode().substring(0,7)).getChildren().add(tree);
                tree.setFullName(map.get(sub.getCode().substring(0, 7)).getFullName() +"/"+tree.getFullName());
            }
        }
        return subjectTrees;
    }

    @Override
    public String getFullName(Subject subject) {
        String name = subject.getName();
        Subject parent = this.getParent(subject);
        if(parent==null){
            name = subject.getName();
        }else{
            name = this.getFullName(parent) +"/"+ name;
        }
        return name;
    }

    @Override
    public Subject getParent(Subject subject) {
        if(subject.getCode().length()==4){
            return null;
        }else if(subject.getCode().length()==7){
            return this.getSubjectByCurtainAndCode(subject.getCurtainId(),subject.getCode().substring(0,4));
        }else if(subject.getCode().length()==10) {
            return this.getSubjectByCurtainAndCode(subject.getCurtainId(), subject.getCode().substring(0, 7));
        }
        return null;
    }

    @Override
    public List<Subject> getSubjectsCode1List(Long curtainId) {
        return subjectMapper.getCode1ByCurtain(curtainId);
    }

}
