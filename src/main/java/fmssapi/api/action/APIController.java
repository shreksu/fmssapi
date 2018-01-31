package fmssapi.api.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainService;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.voucher.model.Voucher;
import fmssapi.voucher.model.VoucherDetail;
import fmssapi.voucher.service.VoucherDetailService;
import fmssapi.voucher.service.VoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author suyuanyang
 * @create 2017-12-25 下午3:06
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class APIController {

    @Autowired
    private SubjectService subjectService;
    @Autowired
    private CurtainService curtainService;
    @Autowired
    private VoucherService voucherService;
    @Autowired
    private VoucherDetailService voucherDetailService;

    @RequestMapping(value = "getSubjects", method = RequestMethod.POST)
    public String getSubjects(@RequestParam String data){
        Map<String,Object> result = new HashMap<>();
        Map map = (Map) JSON.parse(data);
        if(map==null){
            result.put("errno","0");
            result.put("mess","数据不可以为空！");
        }
        String appId = (String) map.get("appId");
        String curtainCode = (String) map.get("curtainCode");
        String subjectType = (String) map.get("subjectType");
        if(StringUtils.isEmpty(curtainCode)){
            result.put("errno","0");
            result.put("mess","帐套编号不可以为空！");
        }
        if(result.size()==0){
            Curtain curtain = curtainService.getCurtainByCode(appId, curtainCode);
            if(curtain==null){
                result.put("errno","0");
                result.put("mess","帐套编号不正确！");
            }else {
                List<Subject> subjectList = subjectService.findAllByCurtainId(curtain.getId());
                List<Map<String,String>> list = new ArrayList<>();
                for(Subject sub : subjectList){
                    if(!StringUtils.isEmpty(subjectType) && !sub.getCode().startsWith(subjectType))
                        continue;
                    Map<String,String> temp = new HashMap<>();
                    temp.put("code",sub.getCode());
                    temp.put("name",sub.getName());
                    list.add(temp);
                }
                result.put("errno","1");
                result.put("subs",list);
            }
        }
        return JSONObject.toJSONString(result);
    }

    @RequestMapping(value = "createVoucher", method = RequestMethod.POST)
    public String createVoucher(@RequestParam String data){
        log.error("data:" + data);
        Map<String,Object> result = new HashMap<>();
        Map map = (Map) JSON.parse(data);
        if(map==null){
            result.put("errno","0");
            result.put("mess","数据不可以为空！");
            return JSONObject.toJSONString(result);
        }
        String appId = (String) map.get("appId");// 客户端ID
        String curtainCode = (String) map.get("curtainCode");// 帐套编号
        String sysCode = (String) map.get("sysCode");// 业务系统中唯一标示
        log.error("sysCode:" + sysCode);
        Curtain curtain = curtainService.getCurtainByCode(appId, curtainCode);
        if(curtain==null){
            result.put("errno","0");
            result.put("mess","帐套编号不正确！");
            return JSONObject.toJSONString(result);
        }
        //先根据业务系统的标示查询凭证，如果已经推送过则在原来的基础上修改 --暂时不用该方法
        //Voucher voucher = voucherService.getBySysCode(sysCode);
        Voucher voucher = new Voucher();
        voucher.setCurtainId(curtain.getId());
        voucher.setCreateDate(new Date());
        voucher.setZhiName("auto");
        voucher.setDescription(sysCode);
        voucher.setSysCode(sysCode);
        Long debit = 0l;
        Long credit = 0l;
        String subjects_str = (String) map.get("subjects");// 凭证明细
        List<Map> subjects = (List<Map>) JSONObject.parse(subjects_str);
        List<VoucherDetail> voucherDetailList = new ArrayList<>();
        if(subjects!=null){
            for(Map sub : subjects){
                VoucherDetail detail = new VoucherDetail();
                String balance = (String) sub.get("balance");// 记账方向
                String subjectCode = (String)sub.get("subjectCode");// 客户ID
                Long amount = Long.valueOf(sub.get("amount").toString());// 金额
                detail.setSubject(subjectService.getSubjectByCurtainAndCode(curtain.getId(),subjectCode));
                if("debit".equals(balance)){
                    detail.setDebit(amount);
                    debit += amount;
                }else if("credit".equals(balance)){
                    detail.setCredit(amount);
                    credit += amount;
                }
                Object desc = sub.get("desc");// 记账备注
                if(!StringUtils.isEmpty(desc)){// 有备注，则放入备注，没有备注放入系统编号
                    detail.setDescription((String)desc);
                }else {
                    detail.setDescription(sysCode);
                }
                voucherDetailList.add(detail);
            }
        }
        voucher.setDebit(debit);
        voucher.setCredit(credit);
        voucher.setVoucherDetailList(voucherDetailList);
        voucherService.saveOrUpdate(voucher);
        String code = String.valueOf(voucher.getCode());
        result.put("errno","1");
        result.put("code",code);
        String str = JSONObject.toJSONString(result);
        log.error("result:" + str);
        return str;
    }

    @RequestMapping(value = "cancelVoucher", method = RequestMethod.POST)
    public String cancelVoucher(@RequestParam String data){
        log.error("红冲data:" + data);
        Map<String,Object> result = new HashMap<>();
        Map map = (Map) JSON.parse(data);
        String appId = (String) map.get("appId");// 客户端ID
        String curtainCode = (String) map.get("curtainCode");// 帐套编号
        String sysCode = (String) map.get("sysCode");// 业务系统中唯一标示
        Curtain curtain = curtainService.getCurtainByCode(appId, curtainCode);
        if(curtain==null){
            result.put("errno","0");
            result.put("mess","帐套编号不正确！");
            return JSONObject.toJSONString(result);
        }
        List<Voucher> voucherList = voucherService.getListBySysCode(sysCode,curtain.getId());
        Voucher voucher = new Voucher();
        voucher.setCurtainId(curtain.getId());
        voucher.setCreateDate(new Date());
        voucher.setZhiName("chong");
        voucher.setDescription(sysCode + "-冲销");
        voucher.setSysCode(sysCode);
        Long debit = 0l;
        Long credit = 0l;
        if(voucherList!=null){
            List<VoucherDetail> voucherDetailList = new ArrayList<>();
            Map<Long,Map<String,Long>> detailMap = this.getCancelVoucher(voucherList);
            for (Map.Entry<Long,Map<String,Long>> entry : detailMap.entrySet()) {
                Long subjectId = entry.getKey();//科目ID
                Map<String,Long> tempMap = entry.getValue();//科目对应的借贷金额
                VoucherDetail detail = new VoucherDetail();
                detail.setSubject(subjectService.findById(subjectId));
                detail.setDebit(tempMap.get("debit") == 0 ? null : -tempMap.get("debit"));
                detail.setCredit(tempMap.get("credit") == 0 ? null : -tempMap.get("credit"));
                if(detail.getDebit()!=null || detail.getCredit()!=null) {
                    detail.setDescription(sysCode + "-冲销");
                    voucherDetailList.add(detail);
                }
                debit += tempMap.get("debit")==null?0l:-tempMap.get("debit");
                credit += tempMap.get("credit")==null?0l:-tempMap.get("credit");
            }
            voucher.setDebit(debit);
            voucher.setCredit(credit);
            voucher.setVoucherDetailList(voucherDetailList);
            voucherService.saveOrUpdate(voucher);
            result.put("errno", "1");
            result.put("code", voucher.getCode());
        }else{
            result.put("errno", "0");
            result.put("mess", "没有找到需要冲销的凭证！");
        }
        String str = JSONObject.toJSONString(result);
        return str;
    }
    //生成需要红冲的明细
    private Map<Long,Map<String,Long>> getCancelVoucher(List<Voucher> voucherList) {
        Map<Long,Map<String,Long>> detailMap = new LinkedHashMap<>();
        for(Voucher voucher : voucherList){
            List<VoucherDetail> voucherDetailList = voucherDetailService.getOriVoucherDetails(voucher.getId());
            for(VoucherDetail detail : voucherDetailList){
                Long subjectId = detail.getSubject().getId();
                Long debit = detail.getDebit()==null?0l:detail.getDebit();
                Long credit = detail.getCredit()==null?0l:detail.getCredit();
                Map<String,Long> tempMap = new HashMap<>();
                if(detailMap.containsKey(subjectId)){
                    tempMap = detailMap.get(subjectId);
                    tempMap.put("debit",tempMap.get("debit")+debit);
                    tempMap.put("credit",tempMap.get("credit")+credit);
                }else{
                    tempMap.put("debit",debit);
                    tempMap.put("credit",credit);
                    detailMap.put(subjectId,tempMap);
                }
            }
        }
        return detailMap;
    }


}
