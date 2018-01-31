package fmssapi.voucher.service;

import fmssapi.voucher.mapper.OrderNumMapper;
import fmssapi.voucher.model.OrderNum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author suyuanyang
 * @create 2018-01-11 上午10:53
 */
@Service
@Transactional
public class OrderNumServiceImpl implements  OrderNumService {

    @Autowired
    OrderNumMapper orderNumMapper;

    @Override
    public Long findNum(String month, Long curtainId) {
        Long code = orderNumMapper.findNum(month,curtainId);
        if(code==null){
            OrderNum orderNum = new OrderNum();
            orderNum.setCurtainId(curtainId);
            orderNum.setMonth(month);
            orderNum.setNum(1l);
            orderNumMapper.insertByObject(orderNum);
            code = 1l;
        }
        return code;
    }

    @Override
    public void updateNum(String month, Long curtainId, Long num) {
        orderNumMapper.updateNum(month,curtainId,num);
    }
}
