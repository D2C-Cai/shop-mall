package com.d2c.shop.service.modules.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.core.model.ShopWithdrawDO;

/**
 * @author BaiCai
 */
public interface ShopWithdrawService extends IService<ShopWithdrawDO> {

    int doWithdraw(ShopWithdrawDO shopWithdraw);

}
