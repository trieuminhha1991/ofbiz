package com.olbius.salesmtl;

import com.olbius.basehr.importExport.ImportExportWorker;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.LocalDispatcher;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SalesStatementWorker {
    public static final String module = SalesStatementWorker.class.getName();
    public static List<Map<String,Object>> getSalesStatementSheetDetail(LocalDispatcher dispatcher, Delegator delegator, GenericValue userLogin, Locale locale,
                                                                 ByteBuffer uploadedFile, Map<Integer, Object> columnExcelMap, Integer sheetIndex, int startLine) throws IOException {
        List<Map<String, Object>> result = FastList.newInstance();
        Map<String, Object> partyAndProductRows = FastMap.newInstance();
        ByteArrayInputStream bais = new ByteArrayInputStream(uploadedFile.array());
        Workbook wb = new XSSFWorkbook(bais);
        Sheet sheetImport = wb.getSheetAt(sheetIndex);
        int rows = sheetImport.getLastRowNum();
        int cols = 0; // No of columns
        for(int i = startLine; i <= rows; i++){
            partyAndProductRows = FastMap.newInstance();
            Row row = sheetImport.getRow(i);
            if(!ImportExportWorker.isEmptyRow(row)){
                cols = row.getLastCellNum();
                for (int c = 0; c < cols; c++) {
                    if(columnExcelMap.get(c) != null){
                        Object fieldValue = columnExcelMap.get(c);
                        Cell cell = row.getCell(c);
                        Object cellValue = ImportExportWorker.getCellValue(cell);
                        if(cell != null && cellValue != null){
                            if("partyCode".equals(fieldValue) && cellValue instanceof String){
                                partyAndProductRows.put("partyCode", cellValue);
                            }else if(!("partyIdFrom".equals(fieldValue)) && cellValue instanceof Double){
                                partyAndProductRows.put( (String)fieldValue, cellValue);
                            }
                        }
                    }
                }
                result.add(partyAndProductRows);
            }
        }
        return result;
    }
}
