package com.d2c.shop.admin.api.base.extension;

import cn.afterturn.easypoi.entity.vo.BigExcelConstants;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.afterturn.easypoi.view.PoiBaseView;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.utils.QueryUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiCai
 */
public abstract class BaseExcelCtrl<E extends BaseDO, Q extends BaseQuery> extends BaseCtrl<E, Q> implements IExcelExportServer {

    @Autowired
    public BaseExcelCtrl<E, Q> excelExportServer;

    @Override
    public List<Object> selectListForExcelExport(Object o, int i) {
        Q query = (Q) o;
        Page page = new Page(i, PageModel.MAX_SIZE, false);
        List<E> list = service.page(page, QueryUtil.buildWrapper(query, false)).getRecords();
        List<Object> result = new ArrayList<>();
        result.addAll(list);
        return result;
    }

    @ApiOperation(value = "分页导出数据")
    @RequestMapping(value = "/excel/page", method = RequestMethod.GET)
    public void excelPage(PageModel page, Q query, ModelMap map, HttpServletRequest request,
                          HttpServletResponse response) {
        Class class_ = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        ApiModel annotation_1 = (ApiModel) class_.getAnnotation(ApiModel.class);
        TableName annotation_2 = (TableName) class_.getAnnotation(TableName.class);
        ExportParams params = new ExportParams(annotation_1.value(), annotation_2.value(), ExcelType.XSSF);
        map.put(BigExcelConstants.CLASS, class_);
        map.put(BigExcelConstants.PARAMS, params);
        map.put(BigExcelConstants.FILE_NAME, annotation_2.value());
        map.put(BigExcelConstants.DATA_PARAMS, query);
        map.put(BigExcelConstants.DATA_INTER, excelExportServer);
        PoiBaseView.render(map, request, response, BigExcelConstants.EASYPOI_BIG_EXCEL_VIEW);
    }

}
