package fmssapi.voucher.action;

import com.mysql.jdbc.StringUtils;
import fmssapi.curtain.model.Curtain;
import fmssapi.curtain.service.CurtainBalanceService;
import fmssapi.curtain.service.CurtainService;
import fmssapi.subject.model.Subject;
import fmssapi.subject.service.SubjectService;
import fmssapi.util.CnUpperCaser;
import fmssapi.util.ExcelUtil;
import fmssapi.util.StringUtil;
import fmssapi.voucher.model.Voucher;
import fmssapi.voucher.model.VoucherDetail;
import fmssapi.voucher.model.VoucherStatus;
import fmssapi.voucher.service.VoucherDetailService;
import fmssapi.voucher.service.VoucherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author suyuanyang
 * @create 2017-12-21 上午10:53
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {


    @Autowired
    VoucherService voucherService;
    @Autowired
    VoucherDetailService voucherDetailService;
    @Autowired
    CurtainBalanceService curtainBalanceService;
    @Autowired
    SubjectService subjectService;
    @Autowired
    CurtainService curtainService;

    @RequestMapping(value = "listPage", method = RequestMethod.GET)
    public Map<String,Object> listPage(Long curtainId,
                                  String startDate,
                                  String endDate,
                                  Long startCode,
                                  Long endCode,
                                  String zhiName,
                                  String shenName,
                                  String status,
                                  String sysCode,
                                  Integer page){
        List<Voucher> voucherList = voucherService.listPage(curtainId,
                startDate, endDate, startCode, endCode, zhiName, shenName,
                status, sysCode, page);
        Long pageTotal = voucherService.listPageTotal(curtainId,
                startDate, endDate, startCode, endCode, zhiName, shenName,
                status, sysCode);
        Map<String,Object> result = new HashMap<>();
        result.put("pageTotal",pageTotal);
        result.put("voucherList",voucherList);
        return result;
    }

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public Map<String,String> save(@RequestBody Voucher voucher){
        Map<String,String> result = new HashMap<>();
        if(voucher.getVoucherDetailList()!=null && voucher.getVoucherDetailList().size()>0) {
            voucher.setDescription(voucher.getVoucherDetailList().get(0).getDescription());
            voucherService.saveOrUpdate(voucher);
            result.put("errno","0");
        }else{
            result.put("errno","1");
            result.put("message","请录入凭证明细，再保存!");
        }
        return result;
    }

    /**
     * 获得凭证明细
     * @param voucherId
     * @return
     */
    @RequestMapping(value = "getVoucherDetails", method = RequestMethod.GET)
    public List<VoucherDetail> getVoucherDetails(Long voucherId){
        return voucherDetailService.getVoucherDetails(voucherId);
    }


    /**
     * 获得凭证的完整信息（包括凭证和凭证明细）
     * @param voucherId
     * @return
     */
    @RequestMapping(value = "getVoucherInfo", method = RequestMethod.GET)
    public Map<String,Object> getVoucherInfo(Long voucherId){
        Map<String,Object> result = new HashMap<>();
        Voucher voucher = voucherService.findById(voucherId);
        result.put("voucher",voucher);
        result.put("details",voucherDetailService.getVoucherDetails(voucherId));
        return result;
    }

    /**
     * 获得打印凭证所需信息
     * @param voucherId
     * @return
     */
    @RequestMapping(value = "getVoucherPrintInfo", method = RequestMethod.GET)
    public Map<String,Object> getVoucherPrintInfo(Long voucherId){
        Map<String,Object> result = new HashMap<>();
        Voucher voucher = voucherService.findById(voucherId);
        result.put("voucher",voucher);
        result.put("curtain", curtainService.findById(voucher.getCurtainId()));
        List<VoucherDetail> details = voucherDetailService.getVoucherDetails(voucherId);
        VoucherDetail obj = new VoucherDetail();
        obj.setDescription("附单据数" + (voucher.getBillNum() == null ? " " : String.valueOf(voucher.getBillNum())) + "张");
        Subject subject = new Subject();
        subject.setFullName("合计 "+ CnUpperCaser.number2CNMontrayUnit(new BigDecimal(voucher.getDebit())));
        obj.setSubject(subject);
        obj.setDebit(voucher.getDebit());
        obj.setCredit(voucher.getCredit());
        details.add(obj);
        result.put("details",details);
        return result;
    }

    /**
     * 获得状态选项
     * @return
     */
    @RequestMapping(value = "getStatusOptions", method = RequestMethod.GET)
    public List<Map<String,String>> getStatusOptions(){
        List<Map<String,String>> list = new ArrayList<>();
        for(VoucherStatus obj : VoucherStatus.values()){
            Map<String,String> map = new HashMap<>();
            map.put("value",obj.toString());
            map.put("label",obj.getName());
            list.add(map);
        }
        return list;
    }

    /**
     * 根据id审核凭证
     * @return
     */
    @RequestMapping(value = "auditVouchers", method = RequestMethod.GET)
    public Map<String,String> auditVouchers(String userName,Long[] ids){
        int length = ids.length;
        int hasAudit = 0;
        String mess = "";
        for(Long id : ids){
            Voucher voucher = voucherService.findById(id);
            mess = voucherService.auditVoucher(userName,voucher);
            if(mess==null){ // 审核成功
                hasAudit++;
            }
        }
        String message = "审核成功"+hasAudit+"张凭证，未成功"+(length-hasAudit)+"张凭证;"
                + (mess==null?"":mess);
        Map<String,String> result = new HashMap<>();
        result.put("message",message);
        return result;
    }


    /**
     * 审核全部
     * @return
     */
    @RequestMapping(value = "auditAllVouchers", method = RequestMethod.GET)
    public Map<String,String> auditAllVouchers(Long curtainId,
                                               String startDate,
                                               String endDate,
                                               Long startCode,
                                               Long endCode,
                                               String zhiName,
                                               String shenName,
                                               String status,
                                               String sysCode,
                                               String userName){
        List<Voucher> voucherList = voucherService.listPage(curtainId,
                startDate,endDate,startCode,endCode,zhiName,shenName,
                status,sysCode,null);
        int length = voucherList.size();
        int hasAudit = 0;
        String mess = "";
        for(Voucher voucher : voucherList){
            mess = voucherService.auditVoucher(userName,voucher);
            if(mess == null){
                hasAudit++;
            }
        }
        String message = "审核成功"+hasAudit+"张凭证，未成功"+(length-hasAudit)+"张凭证;"+ (mess==null?"":mess);
        Map<String,String> result = new HashMap<>();
        result.put("message",message);
        return result;
    }
    /**
     * 上一页或下一页
     */
    @RequestMapping(value = "getVoucherForm", method = RequestMethod.GET)
    public Map<String,Object> getVoucherForm(Long id,String flag){
        Voucher voucher = voucherService.findById(id);
        Long code = voucher.getCode();
        // 当前月份的第一天和最后一天
        Date[] dates = StringUtil.getDatesByMonth(voucher.getCreateDate());
        if("pre".equals(flag)){ // 上一页
            code--;
        }else{
            code++;
        }
        Map<String,Object> result = new HashMap<>();
        Voucher obj = voucherService.getByCodeAndMonth(code,voucher.getCurtainId(),dates);
        if(obj == null){
            result.put("errno","1");
            result.put("message","pre".equals(flag)?"该凭证为本月第一张凭证":"该凭证为本月最后一张凭证");
        }else{
            obj.setVoucherDetailList(voucherDetailService.getVoucherDetails(obj.getId()));
            result.put("errno","0");
            result.put("voucher",obj);
        }
        return result;
    }

    /**
     * 取消审核
     * @param id
     * @return
     */
    @RequestMapping(value = "cancelAudit", method = RequestMethod.GET)
    public Map<String,String> cancelAudit(Long id){
        Voucher voucher = voucherService.findById(id);
        Map<String,String> result = new HashMap<>();
        if(voucher!=null){
            // 查询是否已经自动结转
            boolean hasZhuan = curtainBalanceService.getHasZhuan(voucher.getCurtainId(),StringUtil.getMonth(voucher.getCreateDate()));
            if(hasZhuan){
                result.put("message","该帐套已经自动结转，不可以取消审核！");
                return result;
            }
            voucherService.cancelAudit(voucher);
        }
        result.put("message","1");
        return result;
    }

    /**
     * 作废一张凭证
     * @param voucherId
     * @return
     */
    @RequestMapping(value = "cancelVoucher", method = RequestMethod.GET)
    public Map<String,String> cancelVoucher(Long voucherId){
        Voucher voucher = voucherService.findById(voucherId);
        Map<String,String> result = new HashMap<>();
        voucherService.cancelVoucher(voucher);
        result.put("message", "1");
        return result;
    }

    /**
     * 获得明细账
     * @return
     */
    @RequestMapping(value = "getDetailList", method = RequestMethod.GET)
    public Map<String,Object> getDetailList(Long curtainId,
                                            String startDate,
                                            String endDate,
                                            Long subjectId,
                                            Integer page){
        Subject subject = subjectService.findById(subjectId);
        String subjectCode = null;
        if(subject!=null){
            subjectCode = subject.getCode();
        }
        List<VoucherDetail> detailList = voucherDetailService.listPage(curtainId,
                startDate, endDate, subjectCode, page);
        Long pageTotal = voucherDetailService.listPageTotal(curtainId,
                startDate, endDate, subjectCode);
        Map<String,Object> result = new HashMap<>();
        result.put("pageTotal",pageTotal);
        result.put("detailList",detailList);
        return result;
    }

    /**
     * 导出明细账
     * @return
     */
    @RequestMapping(value = "exportDetailExcel", method = RequestMethod.POST)
    public void exportDetailExcel(@RequestBody Map<String,String> map,HttpServletResponse response){
        Long curtainId = Long.valueOf(map.get("curtainId"));
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        String subjectId = map.get("subjectId");
        String subjectCode = null;
        if(!StringUtils.isNullOrEmpty(subjectId)) {
            Subject subject = subjectService.findById(Long.valueOf(subjectId));
            if (subject != null) {
                subjectCode = subject.getCode();
            }
        }
        List<VoucherDetail> detailList = voucherDetailService.listPage(curtainId,
                startDate, endDate, subjectCode, null);
        OutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Curtain curtain = curtainService.findById(curtainId);
        String fileName = curtain.getName()+"-"+"明细账.xlsx";
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + URLEncoder.encode(fileName, "UTF-8"));
            String[] headers = {"记账日期", "凭证号", "科目编码", "科目名称", "摘要",
                    "借方", "贷方", "关联号"};
            List<List<String>> dataList = new ArrayList<>();
            for(VoucherDetail detail : detailList){
                Voucher voucher = detail.getVoucher();
                List<String> list = new ArrayList<>();
                list.add(StringUtil.dateToString(voucher.getCreateDate(), 0));
                list.add(String.valueOf(voucher.getCode()));
                list.add(detail.getSubject().getCode());
                list.add(detail.getSubject().getName());

                list.add(detail.getDescription());
                list.add(String.valueOf(detail.getDebit() == null ? "" : detail.getDebit()));
                list.add(String.valueOf(detail.getCredit() == null ? "" : detail.getCredit()));
                list.add(voucher.getSysCode());
                dataList.add(list);
            }
            ExcelUtil.exportExcel("明细账", headers, dataList, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(outputStream!=null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
