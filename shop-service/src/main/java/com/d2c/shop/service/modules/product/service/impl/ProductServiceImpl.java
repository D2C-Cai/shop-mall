package com.d2c.shop.service.modules.product.service.impl;

import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.common.utils.QueryUtil;
import com.d2c.shop.service.modules.product.mapper.ProductMapper;
import com.d2c.shop.service.modules.product.model.ProductDO;
import com.d2c.shop.service.modules.product.model.ProductSkuDO;
import com.d2c.shop.service.modules.product.query.ProductSkuQuery;
import com.d2c.shop.service.modules.product.service.ProductService;
import com.d2c.shop.service.modules.product.service.ProductSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author BaiCai
 */
@Service
public class ProductServiceImpl extends BaseService<ProductMapper, ProductDO> implements ProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductSkuService productSkuService;

    @Override
    @Transactional
    public ProductDO doCreate(ProductDO product) {
        this.initStock(product);
        this.save(product);
        for (ProductSkuDO sku : product.getSkuList()) {
            sku.setProductId(product.getId());
            sku.setShopId(product.getShopId());
            sku.setVirtual(product.getVirtual());
            productSkuService.save(sku);
        }
        return product;
    }

    @Override
    @Transactional
    public boolean doUpdate(ProductDO product) {
        this.initStock(product);
        ProductSkuQuery query = new ProductSkuQuery();
        query.setProductId(product.getId());
        List<ProductSkuDO> oldList = productSkuService.list(QueryUtil.buildWrapper(query));
        Map<Long, ProductSkuDO> oldMap = new ConcurrentHashMap<>();
        oldList.forEach(item -> oldMap.put(item.getId(), item));
        for (ProductSkuDO sku : product.getSkuList()) {
            if (sku.getId() != null) {
                // 更新
                sku.setProductId(product.getId());
                sku.setShopId(product.getShopId());
                sku.setVirtual(product.getVirtual());
                productSkuService.updateById(sku);
                oldMap.remove(sku.getId());
            } else {
                // 新增
                sku.setProductId(product.getId());
                sku.setShopId(product.getShopId());
                sku.setVirtual(product.getVirtual());
                productSkuService.save(sku);
            }
        }
        // 删除
        for (ProductSkuDO sku : oldMap.values()) {
            productSkuService.removeById(sku.getId());
        }
        boolean success = this.updateById(product);
        return success;
    }

    private void initStock(ProductDO product) {
        int stock = 0;
        for (ProductSkuDO sku : product.getSkuList()) {
            stock += sku.getStock();
        }
        product.setStock(stock);
    }

    @Override
    @Transactional
    public int doDeductStock(Long id, Integer quantity) {
        return productMapper.doDeductStock(id, quantity);
    }

    @Override
    @Transactional
    public int doReturnStock(Long id, Integer quantity) {
        return productMapper.doReturnStock(id, quantity);
    }

}
