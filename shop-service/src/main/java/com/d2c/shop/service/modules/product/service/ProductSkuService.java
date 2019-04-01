package com.d2c.shop.service.modules.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;

/**
 * @author BaiCai
 */
public interface ProductSkuService extends IService<ProductSkuDO> {

    int doDeductStock(Long id, Long productId, Integer quantity);

    int doReturnStock(Long id, Long productId, Integer quantity);

}
