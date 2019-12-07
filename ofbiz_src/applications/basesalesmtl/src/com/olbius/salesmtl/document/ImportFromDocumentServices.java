package com.olbius.salesmtl.document;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.sql.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.crm.util.ExcelUtil;

import javolution.util.FastMap;

public class ImportFromDocumentServices {
	
	public static Map<String, Object> uploadExcelDocumentAgentData(DispatchContext ctx, Map<String, Object> context) throws IOException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		InputStream stream = null;
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			dispatcher.runSync("deleteAllTempAgent", UtilMisc.toMap("userLogin", context.get("userLogin")));
			ByteBuffer uploadedFile = (ByteBuffer) context.get("uploadedFile");
			stream = new ByteArrayInputStream(uploadedFile.array());
			Workbook workbook = ExcelUtil.getWorkbook(stream, (String)context.get("fileName"));
			Sheet firstSheet = workbook.getSheetAt(0);
	        Iterator<Row> iterator = firstSheet.iterator();
	        while (iterator.hasNext()) {
	        	Row nextRow = iterator.next();
	        	switch (nextRow.getRowNum()) {
				case 0:
					checkTitleLv0(nextRow);
					break;
				default:
					analyzeContent(nextRow, delegator);
					break;
				}
	        }
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(UtilProperties.getMessage("BaseCRMUiLabels", "WrongFormat", locale) + e.getMessage());
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
		return result;
	}
	private static void analyzeContent(Row nextRow, Delegator delegator) throws GenericEntityException {
		Map<String, Object> customer = FastMap.newInstance();
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		
		Object customerName = "";
		Object phoneNumber = "";
		Object emailAddress = "";
		Object website = "";
		Object comments = "";
		String address1 = "";
		Object stateProvinceGeoName = "";
		Object districtGeoName = "";
		Object wardGeoName = "";
		Object representative = "";
		Object representativePhoneNumber = "";
		Object representativeBirthDate = "";
		
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = ExcelUtil.getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				//		if (!"Tên khách hàng".equals(value)) {
					customerName = value;
				break;
			case 1:
				//		if ("Số điện thoại".equals(value)) {
					phoneNumber = value;
				break;
			case 2:
				//		if ("Email".equals(value)) {
					emailAddress = value;
				break;
			case 3:
				//		if ("Website".equals(value)) {
					website = value;
				break;
			case 4:
				//		if ("Số nhà".equals(value)) {
					address1 += value;
				break;
			case 5:
				//		if ("Đường".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					if (UtilValidate.isNotEmpty(address1)) {
						address1 += ", " + value;
					} else {
						address1 += value;
					}
				}
				break;
			case 6:
				//		if ("Phường".equals(value)) {
					wardGeoName = value;
				break;
			case 7:
				//		if ("Quận".equals(value)) {
				districtGeoName = value;
				break;
			case 8:
				//		if ("Tỉnh".equals(value)) {
					stateProvinceGeoName = value;
				break;
			case 9:
				//		if ("Ghi chú".equals(value)) {
					comments = value;
				break;
			case 10:
				//		if ("Người đại diện".equals(value)) {
				representative = value;
				break;
			case 11:
				//		if ("Số điện thoại".equals(value)) {
				representativePhoneNumber = value;
				break;
			case 12:
				//		if ("Ngày sinh".equals(value)) {
				representativeBirthDate = value;
				break;
			default:
				break;
			}
		}
		if (UtilValidate.isNotEmpty(customerName)) {

			customer.put("customerId", delegator.getNextSeqId("TempAgent"));
			customer.put("customerName", customerName);
			customer.put("phoneNumber", phoneNumber);
			customer.put("emailAddress", emailAddress);
			customer.put("website", website);
			customer.put("comments", comments);
			
			customer.put("address1", address1);
			
			Map<String, Object> geo = ExcelUtil.getGeoIdByGeoName(delegator, "Việt Nam", "COUNTRY", null);
			customer.put("countryGeoId", geo.get("geoId"));
			customer.put("countryGeoName", geo.get("geoName"));
			
			geo = ExcelUtil.getGeoIdByGeoName(delegator, stateProvinceGeoName, "PROVINCE", geo.get("geoId"));
			customer.put("stateProvinceGeoId", geo.get("geoId"));
			customer.put("stateProvinceGeoName", geo.get("geoName"));
			
			geo = ExcelUtil.getGeoIdByGeoName(delegator, districtGeoName, "DISTRICT", geo.get("geoId"));
			customer.put("districtGeoId", geo.get("geoId"));
			customer.put("districtGeoName", geo.get("geoName"));
			
			geo = ExcelUtil.getGeoIdByGeoName(delegator, wardGeoName, "WARD", geo.get("geoId"));
			customer.put("wardGeoId", geo.get("geoId"));
			customer.put("wardGeoName", geo.get("geoName"));
			
			customer.put("representative", representative);
			customer.put("representativePhoneNumber", representativePhoneNumber);
			
			if (UtilValidate.isNotEmpty(representativeBirthDate)) {
				if (representativeBirthDate instanceof Long) {
					customer.put("representativeBirthDate", new Date((long) representativeBirthDate));
				}
			}
			delegator.create("TempAgent", customer);
		
		}
	}
	
	private static void checkTitleLv0(Row nextRow) throws Exception {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = ExcelUtil.getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				if (!"Tên khách hàng".equals(value)) {
					throw new Exception("Tên khách hàng");
				}
				break;
			case 1:
				if (!"Số điện thoại".equals(value)) {
					throw new Exception("Số điện thoại");
				}
				break;
			case 2:
				if (!"Email".equals(value)) {
					throw new Exception("Email");
				}
				break;
			case 3:
				if (!"Website".equals(value)) {
					throw new Exception("Website");
				}
				break;
			case 4:
				if (!"Số nhà".equals(value)) {
					throw new Exception("Số nhà");
				}
				break;
			case 5:
				if (!"Đường".equals(value)) {
					throw new Exception("Đường");
				}
				break;
			case 6:
				if (!"Phường".equals(value)) {
					throw new Exception("Phường");
				}
				break;
			case 7:
				if (!"Quận".equals(value)) {
					throw new Exception("Quận");
				}
				break;
			case 8:
				if (!"Tỉnh".equals(value)) {
					throw new Exception("Tỉnh");
				}
				break;
			case 9:
				if (!"Ghi chú".equals(value)) {
					throw new Exception("Ghi chú");
				}
				break;
			case 10:
				if (!"Người đại diện".equals(value)) {
					throw new Exception("Người đại diện");
				}
				break;
			case 11:
				if (!"Số điện thoại".equals(value)) {
					throw new Exception("Số nhà");
				}
				break;
			case 12:
				if (!"Ngày sinh".equals(value)) {
					throw new Exception("Đường");
				}
				break;
			default:
				break;
			}
		}
	}
}
