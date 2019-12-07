package com.olbius.crm;

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

import com.olbius.administration.util.UniqueUtil;
import com.olbius.crm.util.ExcelUtil;

import javolution.util.FastMap;

public class ImportFromDocumentServices {
	
	public static Map<String, Object> uploadExcelDocument(DispatchContext ctx, Map<String, Object> context) throws IOException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		InputStream stream = null;
		try {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			dispatcher.runSync("deleteAllTempCustomer", UtilMisc.toMap("userLogin", context.get("userLogin")));
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
				case 1:
					checkTitleLv1(nextRow);
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
		
		Object partyFullName = "";
		Object birthDate = "";
		
		String address1 = "";
		Object wardGeoName = "";
		Object districtGeoName = "";
		Object stateProvinceGeoName = "";
		
		Object phoneHome = "";
		Object phoneMobile = "";
		Object phoneWork = "";
		Object emailAddress = "";
		
		Object childName1 = "";
		Object childName1birthDate = "";
		Object childName2 = "";
		Object childName2birthDate = "";
		
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = ExcelUtil.getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				//		if (!"Nguồn".equals(value)) {
					
				break;
			case 1:
				//		if ("Tên trường/Công ty".equals(value)) {
					
				break;
			case 2:
				//		if ("Địa chỉ".equals(value)) {
					
				break;
			case 3:
				//		if ("Quận".equals(value)) {
				
				break;
			case 4:
				//		if ("Tỉnh".equals(value)) {
					
				break;
			case 5:
				//		if ("Program Name".equals(value)) {
					
				break;
			case 6:
				//		if ("CODE SALE STAFF".equals(value)) {
					
				break;
			case 7:
				//		if ("Activity Code".equals(value)) {
					
				break;
			case 8:
				//		if ("Mã KH (Kế toán)".equals(value)) {
					
				break;
			case 9:
				//		if ("Serial Number".equals(value)) {
					
				break;
			case 10:
				//		if ("Tên mẹ".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					partyFullName = value;
				}
				break;
			case 11:
				//		if ("Số nhà".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					address1 += value;
				}
				break;
			case 12:
				//		if ("Đường".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					if (UtilValidate.isNotEmpty(address1)) {
						address1 += ", " + value;
					} else {
						address1 += value;
					}
				}
				break;
			case 13:
				//		if ("Phường".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					wardGeoName = value;
				}
				break;
			case 14:
				//		if ("Quận".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					districtGeoName = value;
				}
				break;
			case 15:
				//		if ("Tỉnh".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					stateProvinceGeoName = value;
				}
				break;
			case 16:
				//		if ("HomePhone".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					phoneHome = value;
				}
				break;
			case 17:
				//		if ("MobilePhone".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					phoneMobile = value;
				}
				break;
			case 18:
				//		if ("OfficePhone".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					phoneWork = value;
				}
				break;
			case 19:
				//		if ("Refer phone".equals(value)) {
					
				break;
			case 20:
				//		if ("Refer Phone formula".equals(value)) {
					
				break;
			case 21:
				//		if ("Email".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					emailAddress = value;
				}
				break;
			case 22:
				//		if ("Mother's DOB".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					birthDate = value;
				}
				break;
			case 23:
				//		if ("Mother Previous Main Brand".equals(value)) {
					
				break;
			case 24:
				//		if ("Mother Current Main Brand".equals(value)) {
					
				break;
			case 25:
				//		if ("Child's Name 1".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					childName1 = value;
				}
				break;
			case 26:
				//		if ("DOB Child;s Name 1".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					childName1birthDate = value;
				}
				break;
			case 27:
				//		if ("Previous Main Brand (SP dùng trước)".equals(value)) {
					
				break;
			case 28:
				//		if ("Current Main Brand (Sp đang dùng)".equals(value)) {
					
				break;
			case 29:
				//		if ("Child's Name 2".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					childName2 = value;
				}
				break;
			case 30:
				//		if ("DOB Child;s Name 2".equals(value)) {
				if (UtilValidate.isNotEmpty(value)) {
					childName2birthDate = value;
				}
				break;
			case 31:
				//		if ("Previous Main Brand".equals(value)) {
					
				break;
			case 32:
				//		if ("Current Main Brand".equals(value)) {
					
				break;
			default:
				break;
			}
		}
		if (UtilValidate.isNotEmpty(partyFullName)) {
			if (UtilValidate.isNotEmpty(phoneHome) || UtilValidate.isNotEmpty(phoneMobile) || UtilValidate.isNotEmpty(phoneWork)) {
				if (UniqueUtil.checkContactNumber(delegator, phoneHome) && UniqueUtil.checkContactNumber(delegator, phoneMobile) && UniqueUtil.checkContactNumber(delegator, phoneWork)) {
					customer.put("customerId", delegator.getNextSeqId("TempCustomer"));
					customer.put("partyFullName", partyFullName);
					if (UtilValidate.isNotEmpty(birthDate)) {
						if (birthDate instanceof Long) {
							customer.put("birthDate", new Date((long) birthDate));
						}
					}
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
					
					customer.put("phoneHome", phoneHome);
					customer.put("phoneMobile", phoneMobile);
					customer.put("phoneWork", phoneWork);
					customer.put("emailAddress", emailAddress);
					
					customer.put("childName1", childName1);
					if (UtilValidate.isNotEmpty(childName1birthDate)) {
						if (childName1birthDate instanceof Long) {
							customer.put("childName1birthDate", new Date((long) childName1birthDate));
						}
					}
					customer.put("childName2", childName2);
					if (UtilValidate.isNotEmpty(childName2birthDate)) {
						if (childName2birthDate instanceof Long) {
							customer.put("childName2birthDate", new Date((long) childName2birthDate));
						}
					}
					
					delegator.create("TempCustomer", customer);
				}
			}
		}
	}
	
	private static void checkTitleLv0(Row nextRow) throws Exception {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            switch (cell.getColumnIndex()) {
			case 1:
				if (!"Source Detail".equals(ExcelUtil.getCellValue(cell))) {
					throw new Exception("Source Detail");
				}
				break;
			default:
				break;
			}
        }
	}
	private static void checkTitleLv1(Row nextRow) throws Exception {
		Iterator<Cell> cellIterator = nextRow.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			Object value = ExcelUtil.getCellValue(cell);
			switch (cell.getColumnIndex()) {
			case 0:
				if (!"Nguồn".equals(value)) {
					throw new Exception("Nguồn");
				}
				break;
			case 1:
				if (!"Tên trường/Công ty".equals(value)) {
					throw new Exception("Tên trường/Công ty");
				}
				break;
			case 2:
				if (!"Địa chỉ".equals(value)) {
					throw new Exception("Địa chỉ");
				}
				break;
			case 3:
				if (!"Quận".equals(value)) {
					throw new Exception("Quận");
				}
				break;
			case 4:
				if (!"Tỉnh".equals(value)) {
					throw new Exception("Tỉnh");
				}
				break;
			case 5:
				if (!"Program Name".equals(value)) {
					throw new Exception("Program Name");
				}
				break;
			case 6:
				if (!"CODE SALE STAFF".equals(value)) {
					throw new Exception("CODE SALE STAFF");
				}
				break;
			case 7:
				if (!"Activity Code".equals(value)) {
					throw new Exception("Activity Code");
				}
				break;
			case 8:
				if (!"Mã KH (Kế toán)".equals(value)) {
					throw new Exception("Mã KH (Kế toán)");
				}
				break;
			case 9:
				if (!"Serial Number".equals(value)) {
					throw new Exception("Serial Number");
				}
				break;
			case 10:
				if (!"Tên mẹ".equals(value)) {
					throw new Exception("Tên mẹ");
				}
				break;
			case 11:
				if (!"Số nhà".equals(value)) {
					throw new Exception("Số nhà");
				}
				break;
			case 12:
				if (!"Đường".equals(value)) {
					throw new Exception("Đường");
				}
				break;
			case 13:
				if (!"Phường".equals(value)) {
					throw new Exception("Phường");
				}
				break;
			case 14:
				if (!"Quận".equals(value)) {
					throw new Exception("Quận");
				}
				break;
			case 15:
				if (!"Tỉnh".equals(value)) {
					throw new Exception("Tỉnh");
				}
				break;
			case 16:
				if (!"HomePhone".equals(value)) {
					throw new Exception("HomePhone");
				}
				break;
			case 17:
				if (!"MobilePhone".equals(value)) {
					throw new Exception("MobilePhone");
				}
				break;
			case 18:
				if (!"OfficePhone".equals(value)) {
					throw new Exception("OfficePhone");
				}
				break;
			case 19:
				if (!"Refer phone".equals(value)) {
					throw new Exception("Refer phone");
				}
				break;
			case 20:
				if (!"Refer Phone formula".equals(value)) {
					throw new Exception("Refer Phone formula");
				}
				break;
			case 21:
				if (!"Email".equals(value)) {
					throw new Exception("Email");
				}
				break;
			case 22:
				if (!"Mother's DOB".equals(value)) {
					throw new Exception("Mother's DOB");
				}
				break;
			case 23:
				if (!"Mother Previous Main Brand".equals(value)) {
					throw new Exception("Mother Previous Main Brand");
				}
				break;
			case 24:
				if (!"Mother Current Main Brand".equals(value)) {
					throw new Exception("Mother Current Main Brand");
				}
				break;
			case 25:
				if (!"Child's Name 1".equals(value)) {
					throw new Exception("Child's Name 1");
				}
				break;
			case 26:
				if (!"DOB Child;s Name 1".equals(value)) {
					throw new Exception("DOB Child;s Name 1");
				}
				break;
			case 27:
				if (!"Previous Main Brand (SP dùng trước)".equals(value)) {
					throw new Exception("Previous Main Brand (SP dùng trước)");
				}
				break;
			case 28:
				if (!"Current Main Brand (Sp đang dùng)".equals(value)) {
					throw new Exception("Current Main Brand (Sp đang dùng)");
				}
				break;
			case 29:
				if (!"Child's Name 2".equals(value)) {
					throw new Exception("Child's Name 2");
				}
				break;
			case 30:
				if (!"DOB Child;s Name 2".equals(value)) {
					throw new Exception("DOB Child;s Name 2");
				}
				break;
			case 31:
				if (!"Previous Main Brand".equals(value)) {
					throw new Exception("Previous Main Brand");
				}
				break;
			case 32:
				if (!"Current Main Brand".equals(value)) {
					throw new Exception("Current Main Brand");
				}
				break;
			default:
				break;
			}
		}
	}
}
