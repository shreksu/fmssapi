package fmssapi.curtain.service;

import fmssapi.curtain.model.CurtainBalance;
import fmssapi.subject.model.Subject;

import java.util.List;
import java.util.Map;

/**
 * @author suyuanyang
 * @create 2018-01-20 下午12:13
 */
public interface CurtainBalanceService {
    /**
     * 获得帐套余额
     * @param curtainId 帐套
     * @param month 月份
     * @return
     */
    List<CurtainBalance> getCurtainBalances(Long curtainId, String month);

    /**
     * 装载数据到科目集合（期初余额）
     * @param subjectList
     * @param curtainBalanceList
     */
    void loadSubject(List<Subject> subjectList, List<CurtainBalance> curtainBalanceList);

    /**
     * 装载数据到科目集合（每月余额表）
     * @param subjectList
     * @param curtainBalanceList
     */
    void loadMonthSubject(List<Subject> subjectList,List<CurtainBalance> curtainBalancePreList, List<CurtainBalance> curtainBalanceList);

    /**
     * 新增/更新 起初余额
     * @param curtainId
     * @param month
     * @param subjectCode
     * @param amount
     */
    void update(Long curtainId, String month, String subjectCode, Double amount);

    /**
     * 获得登账状态
     * @param curtainId 帐套
     * @param month 月份
     * @return
     */
    String getStatus(Long curtainId, String month);

    /**
     * 删除登账记录
     * @param curtainId 帐套
     * @param month 月份
     */
    void deleteBalances(Long curtainId, String month);
    /**
     * 删除登账记录
     * @param curtainId 帐套
     * @param month 月份
     */
    void createBalances(Long curtainId, String month, String type);

    /**
     * 删除期初余额
     * @param curtainId
     * @param month
     * @param subjectCode
     */
    void delete(Long curtainId, String month, String subjectCode);

    /**
     * 试算平衡 资产 = 负债 + 资产
     * @param curtainId
     * @param month
     * @return
     */
    Map<String,String> checkSum(Long curtainId, String month);

    /**
     * 计算合计
     * @param subjectList
     */
    void calculateTotal(List<Subject> subjectList);

    /**
     * 是否结账
     * @param curtainId
     * @param month
     * @return
     */
    boolean getHasJie(Long curtainId, String month);

    /**
     * 是否自动结转
     * @param curtainId
     * @param month
     * @return
     */
    boolean getHasZhuan(Long curtainId, String month);

    /**
     * 反结转，将原来的结转凭证明细删除，并且将制单人改成void,并且修改备注与金额
     * @param curtainId
     * @param month
     */
    void autoSettleCancel(Long curtainId, String month);

    /**
     * 查看是否已经生成报表
     * @param curtainId
     * @param month
     * @return
     */
    boolean hasReport(Long curtainId, String month);

    /**
     * 将月末帐套的状态改成普通登账
     * @param curtainId
     * @param month
     */
    void changeStatusA(Long curtainId, String month);
}
