package fmssapi.curtain.service;

import fmssapi.curtain.model.Curtain;
import fmssapi.manager.model.User;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-11 上午9:02
 */
public interface CurtainService {

    List<Curtain> findAllByComFlag(String comFlag);

    String add(Curtain curtain);

    String getNewCode(String comFlag);

    List<String> getUsersByCurtain(Long curtainId);

    /**
     * 将状态设置为关闭
     * @param id
     * @return
     */
    String remove(Long id);

    /**
     * 修改帐套的操作员和帐套主管
     * @param curtainId 帐套
     * @param userId    帐套主管
     * @param userIds   帐套所有操作员
     * @return
     */
    String updateUserByCurtainId(Long curtainId, String userId, String[] userIds);

    /**
     * 查询人员可以操作的帐套
     * @param user
     * @return
     */
    List<Curtain> getCurtainsByUser(User user);

    /**
     * h偶的帐套
     * @param comFlag 管理员帐啊后
     * @param code 帐套号
     * @return
     */
    Curtain getCurtainByCode(String comFlag,String code);

    /**
     * 获得curtain
     * @param curtainId
     * @return
     */
    Curtain findById(Long curtainId);
}
