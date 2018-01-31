package fmssapi.subject.service;

import fmssapi.subject.model.Subject;
import fmssapi.subject.model.SubjectTree;
import fmssapi.subject.model.SubjectType;

import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2017-12-11 下午1:11
 */
public interface SubjectService {

    /**
     * 获得所有科目（包括类别等信息）
     * @param curtainId 帐套
     * @return
     */
    List<Subject> findAllByCurtainId(Long curtainId);

    /**
     * 获得所有科目（包括是否叶子节点）
     * @param curtainId
     * @return
     */
    List<Subject> findSimpleAllByCurtainId(Long curtainId);



    Subject findById(Long id);

    /**
     * 获得科目的类型
     * @param code
     * @return
     */
    Integer getSubjectLevel(String code);
    /**
     * 获得科目的类型
     * @param code
     * @return
     */
    SubjectType getSubjectType(String code);

    /**
     * 获得科目余额方向
     * @param code
     * @return
     */
    //SubjectBalance getSubjectBalance(String code);

    /**
     * 新增科目
     * @param subject
     * @return
     */
    String add(Subject subject);

    /**
     * 复制科目到一个新的帐套
     * @param toCurtainId
     * @param subjectIds
     * @return
     */
    String copySubject(Long toCurtainId, Long[] subjectIds);

    /**
     * 查subject
     * @param curtainId 帐套ID
     * @param code  科目编号
     * @return
     */
    Subject getSubjectByCurtainAndCode(Long curtainId,String code);

    /**
     * 获得父类科目
     * @param code
     * @return
     */
    Map<String,String> getSubjectParent(Long curtainId,String code);

    /**
     * 删除科目
     * @param id
     * @return
     */
    String remove(Long id);

    /**
     * 获得科目树
     * @param curtainId
     * @return
     */
    List<SubjectTree> getSubjectsTree(Long curtainId);

    /**
     * 获得科目的全名
     * @param subject
     * @return
     */
    String getFullName(Subject subject);

    /**
     * 获得上一级科目
     * @param subject
     * @return
     */
    Subject getParent(Subject subject);

    /**
     * 获得一级科目集合
     * @param curtainId
     * @return
     */
    List<Subject> getSubjectsCode1List(Long curtainId);
}
