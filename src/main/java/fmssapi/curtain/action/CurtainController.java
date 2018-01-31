package fmssapi.curtain.action;

import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainService;
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
 * @create 2017-12-11 上午9:17
 */
@RestController
@RequestMapping("/curtain")
public class CurtainController {

    @Autowired
    private CurtainService curtainService;

    /**
     * 新增帐套时，自动生成帐套编号
     * @param comFlag
     * @return
     */
    @RequestMapping(value = "getNewCode", method = RequestMethod.GET)
    public String getNewCode(String comFlag){
        String code = curtainService.getNewCode(comFlag);
        return code;
    }

    /**
     * 新增帐套时，自动生成帐套编号
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "getCurtainById", method = RequestMethod.GET)
    public Curtain getCurtainById(Long curtainId){
        Curtain curtain = curtainService.findById(curtainId);
        return curtain;
    }

    /**
     * 获得所有帐套
     * @return
     */
    @RequestMapping(value = "list", method = RequestMethod.GET)
    public List<Curtain> findAllByComFlag(String comFlag){
        return curtainService.findAllByComFlag(comFlag);
    }


    /**
     *
     * @param curtain
     * @return errno:0(成功)，1(用户已存在)
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public Map<String,String> add(@RequestBody Curtain curtain){
        Map<String,String> result = new HashMap<>();
        result.put("errno",curtainService.add(curtain));
        return result;
    }

    @RequestMapping(value = "remove", method = RequestMethod.GET)
    public Map<String,String> remove(Long id){
        Map<String,String> result = new HashMap<>();
        result.put("errno",curtainService.remove(id));
        return result;
    }

    /**
     * 获得帐套的操作员
     * @param curtainId
     * @return
     */
    @RequestMapping(value = "getUsersByCurtain", method = RequestMethod.GET)
    public List<String> getUsersByCurtain(Long curtainId){
        return curtainService.getUsersByCurtain(curtainId);
    }

    /**
     * 修改帐套的操作员和帐套主管
     * @param curtain 帐套
     * @return
     */
    @RequestMapping(value = "updateUserByCurtainId", method = RequestMethod.POST)
    public Map<String,String> updateUserByCurtainId(@RequestBody Curtain curtain){
        Map<String,String> result = new HashMap<>();
        result.put("errno",curtainService.updateUserByCurtainId(curtain.getId(), curtain.getUserId(), curtain.getUserIds()));
        return result;
    }

}
