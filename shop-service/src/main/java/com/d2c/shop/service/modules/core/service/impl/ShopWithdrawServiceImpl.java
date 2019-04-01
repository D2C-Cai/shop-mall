package com.d2c.shop.service.modules.core.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.core.mapper.ShopWithdrawMapper;
import com.d2c.shop.service.modules.core.model.ShopFlowDO;
import com.d2c.shop.service.modules.core.model.ShopWithdrawDO;
import com.d2c.shop.service.modules.core.service.ShopFlowService;
import com.d2c.shop.service.modules.core.service.ShopWithdrawService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author BaiCai
 */
@Service
public class ShopWithdrawServiceImpl extends BaseService<ShopWithdrawMapper, ShopWithdrawDO> implements ShopWithdrawService {

    @Autowired
    private ShopFlowService shopFlowService;

    @Override
    @Transactional
    public int doWithdraw(ShopWithdrawDO shopWithdraw) {
        this.save(shopWithdraw);
        ShopFlowDO sf = new ShopFlowDO();
        sf.setStatus(1);
        sf.setType(ShopFlowDO.TypeEnum.WITHDRAW.name());
        sf.setShopId(shopWithdraw.getShopId());
        sf.setOrderSn(String.valueOf(shopWithdraw.getId()));
        sf.setPaymentType(shopWithdraw.getPayType());
        sf.setPaymentSn(shopWithdraw.getPaySn());
        sf.setAmount(shopWithdraw.getAmount().multiply(new BigDecimal(-1)));
        return shopFlowService.doFlowing(sf);
    }

}
