package com.d2c.shop.service.modules.product.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.product.mapper.ProductSkuMapper;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiCai
 */
@Service
public class ProductSkuServiceImpl extends BaseService<ProductSkuMapper, ProductSkuDO> implements ProductSkuService {

    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductService productService;

    @Override
    @Transactional
    public int doDeductStock(Long id, Long productId, Integer quantity) {
        int success = 1;
        success *= productSkuMapper.doDeductStock(id, quantity);
        success *= productService.doDeductStock(productId, quantity);
        return success;
    }

    @Override
    @Transactional
    public int doReturnStock(Long id, Long productId, Integer quantity) {
        int success = 1;
        success *= productSkuMapper.doReturnStock(id, quantity);
        success *= productService.doReturnStock(productId, quantity);
        return success;
    }

}
