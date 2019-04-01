package com.d2c.shop.service.modules.member.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.base.BaseService;
import com.d2c.shop.service.modules.member.mapper.DummyMapper;
import com.d2c.shop.service.modules.member.model.DummyDO;
import com.d2c.shop.service.modules.member.service.DummyService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author BaiCai
 */
@Service
public class DummyServiceImpl extends BaseService<DummyMapper, DummyDO> implements DummyService {

    @Override
    public List<DummyDO> findRandom(Integer size) {
        if (size > 100) {
            return new ArrayList<>();
        }
        PageModel page = new PageModel();
        page.setP((int) (Math.random() * 99));
        page.setPs(100);
        Page<DummyDO> pager = (Page<DummyDO>) this.page(page);
        List<DummyDO> list = pager.getRecords();
        Collections.shuffle(list);
        return list.subList(0, size);
    }

}
