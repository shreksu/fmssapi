package fmssapi.curtain.service;

import com.mysql.jdbc.StringUtils;
import fmssapi.curtain.mapper.CurtainMapper;
import fmssapi.curtain.model.Curtain;
import fmssapi.manager.model.User;
import fmssapi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author suyuanyang
 * @create 2017-12-11 上午9:02
 */
@Service
@Transactional
public class CurtainServiceImpl implements CurtainService {

    @Autowired
    CurtainMapper curtainMapper;

    @Override
    public List<Curtain> findAllByComFlag(String comFlag) {
        return curtainMapper.findAllByComFlag(comFlag);
    }

    @Override
    public String add(Curtain curtain) {
        Curtain obj = curtainMapper.findById(curtain.getId());
        if (obj == null) {//新增
            curtain.setState("Y");
            curtainMapper.insertByObject(curtain);
        } else {
            curtainMapper.updateByObject(curtain);
        }
        return "0";
    }

    @Override
    public String getNewCode(String comFlag) {
        String lastCode = curtainMapper.getNewCode(comFlag);
        if(StringUtils.isNullOrEmpty(lastCode)){
            lastCode = "0";
        }
        String code = StringUtil.getCode(Integer.valueOf(lastCode) + 1, 2, null);
        return code;
    }

    @Override
    public List<String> getUsersByCurtain(Long curtainId) {
        return curtainMapper.getUserByCurtainId(curtainId);
    }

    @Override
    public String remove(Long id) {
        curtainMapper.changeState(id,"N");
        return "0";
    }


    @Override
    public String updateUserByCurtainId(Long curtainId, String userId, String[] userIds) {
        curtainMapper.updateCurtainUser(curtainId, userId);
        curtainMapper.deleteUserByCurtainId(curtainId);
        curtainMapper.insertUserByCurtainId(curtainId, userIds);
        return "0";
    }

    @Override
    public List<Curtain> getCurtainsByUser(User user) {
        if(user != null){
            return curtainMapper.getCurtainsByUser(user.getLoginName());
        }else {
            return null;
        }
    }

    @Override
    public Curtain getCurtainByCode(String comFlag, String code) {
        return curtainMapper.findByCode(comFlag,code);
    }

    @Override
    public Curtain findById(Long curtainId) {
        return curtainMapper.findById(curtainId);
    }
}
