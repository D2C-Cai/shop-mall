package com.d2c.shop.service.modules.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.d2c.shop.service.modules.order.model.CrowdGroupDO;
import org.apache.ibatis.annotations.Param;

/**
 * @author BaiCai
 */
public interface CrowdGroupMapper extends BaseMapper<CrowdGroupDO> {

    int doAttend(@Param("id") Long id, @Param("avatars") String avatars);

    int doCancel(@Param("id") Long id, @Param("avatars") String avatars);

}
