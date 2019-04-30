package com.d2c.shop.admin.api.base.extension;

import cn.afterturn.easypoi.entity.vo.BigExcelConstants;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import cn.afterturn.easypoi.handler.inter.IExcelExportServer;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.d2c.shop.admin.api.base.BaseCtrl;
import com.d2c.shop.service.common.api.PageModel;
import com.d2c.shop.service.common.api.Response;
import com.d2c.shop.service.common.api.ResultCode;
import com.d2c.shop.service.common.api.base.BaseDO;
import com.d2c.shop.service.common.api.base.BaseQuery;
import com.d2c.shop.service.common.utils.QueryUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author BaiCai
 */
public abstract class BaseExcelCtrl<E extends BaseDO, Q extends BaseQuery> extends BaseCtrl<E, Q> implements IExcelExportServer {

    @Autowired
    public BaseExcelCtrl<E, Q> excelExportServer;
    //
    private static final String ROOT_DIR = "/mnt/shop/";
    private static final String EXCEL_DIR = "/download/excel/";

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
    public R excelPage(Q query, ModelMap map) throws Exception {
        Class class_ = (Class) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        ApiModel annotation_1 = (ApiModel) class_.getAnnotation(ApiModel.class);
        TableName annotation_2 = (TableName) class_.getAnnotation(TableName.class);
        ExportParams params = new ExportParams(annotation_1.value(), annotation_2.value(), ExcelType.XSSF);
        map.put(BigExcelConstants.CLASS, class_);
        map.put(BigExcelConstants.PARAMS, params);
        map.put(BigExcelConstants.FILE_NAME, annotation_2.value());
        map.put(BigExcelConstants.DATA_PARAMS, query);
        map.put(BigExcelConstants.DATA_INTER, excelExportServer);
        return renderExcel(map);
    }

    protected static R renderExcel(Map<String, Object> model) throws Exception {
        String codedFileName = model.get(BigExcelConstants.FILE_NAME) + "_" + DateUtil.currentSeconds() + ".xls";
        Workbook workbook = ExcelExportUtil.exportBigExcel((ExportParams) model.get(BigExcelConstants.PARAMS), (Class) model.get(BigExcelConstants.CLASS), Collections.EMPTY_LIST);
        IExcelExportServer server = (IExcelExportServer) model.get(BigExcelConstants.DATA_INTER);
        int page = 1;
        int next = page + 1;
        Object query = model.get(BigExcelConstants.DATA_PARAMS);
        for (List list = server.selectListForExcelExport(query, page); list != null && list.size() > 0; list = server.selectListForExcelExport(query, next++)) {
            workbook = ExcelExportUtil.exportBigExcel((ExportParams) model.get(BigExcelConstants.PARAMS), (Class) model.get(BigExcelConstants.CLASS), list);
        }
        ExcelExportUtil.closeExportBigExcel();
        String webPath = EXCEL_DIR + DateUtil.today() + "/";
        String filePath = ROOT_DIR + webPath;
        if (!new File(filePath).exists()) {
            new File(filePath).mkdirs();
        }
        FileOutputStream out = new FileOutputStream(filePath + codedFileName);
        workbook.write(out);
        out.flush();
        return Response.restResult(webPath + codedFileName, ResultCode.SUCCESS);
    }

}
