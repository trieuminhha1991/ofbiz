package com.olbius.acc.excel;

import com.olbius.acc.report.ColumnConfig;
import com.olbius.acc.report.DataType;
import com.olbius.acc.report.HSSFBuilder;
import com.olbius.acc.report.SheetConfig;
import org.apache.poi.ss.usermodel.Workbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 
 * @author Namdn
 * 
 */
public abstract class AbstractExcel<U> implements ExcelInteface {

	private HSSFBuilder builder;
	private SheetConfig sheetConfig;
	private List<ColumnConfig> columnConfigs;
	private ColumnConfig columnConfig;
	private Map<U, U> datafields;
	private Map<String, Object> mapFieldsDescribe;
	public String resourceExcel;
	public static Set<?> resourceList;
	public Locale locale;

	/**
	 * This constructor init excel
	 * 
	 * @param Locale
	 *            object get locale by user
	 * @param datafields
	 *            The List datafields is list name fields of excel sheet
	 * @author Namdn
	 */
	protected AbstractExcel(Locale locale) {
		sheetConfig = new SheetConfig();
		builder = new HSSFBuilder();
		columnConfigs = new ArrayList<ColumnConfig>();
		this.locale = locale;
	}

	@SuppressWarnings("unchecked")
	public <T> void setDataFields(Map<T, T> datafields) {
		this.datafields = (Map<U, U>) datafields;
	}

	/**
	 * return sheet config object
	 */
	public SheetConfig getSheetConfig() {
		return sheetConfig;
	}
	
	/**
	 * Useful for gets Labels with multiple resource labels
	 * @param Set of resource Labels
	 * 
	 * */
	public String getLabels(String key){
		if(this.resourceList == null || this.resourceList.isEmpty() || key.equals("") || key == null) return "";
		StringBuffer sb = null;
		Iterator<?> it = this.resourceList.iterator();
		while((sb == null || sb.toString().equals(key)) && it.hasNext())
			sb = new StringBuffer(UtilProperties.getMessage((String) it.next(),key, locale));
		return sb.toString();
	}
	
	public static void setMultiSource(Set<?> resources){
		resourceList = resources;
	};

	/**
	 * return date with convert of java.sql.Date
	 * 
	 * @throws ParseException
	 */
	public Date convertDateFromString(String date) throws ParseException {
		Date parsedDate = new Date();
		try {
			if (date == null || date.equals(""))
				return new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			parsedDate = (Date) dateFormat.parse(date);
		} catch (Exception e) {
			return convertDate(date);
		}

		return parsedDate;
	}

	/**
	 * return date with convert of java.sql.Date
	 */
	public Date convertDate(String date) {

		if (date.equals("undefined") || date.equals(""))
			return null;

		return new Date(Long.parseLong(date));
	}

	public Timestamp convertDateToTimestamp(String date) {

		if (date.equals("undefined") || date.equals(""))
			return null;

		return new Timestamp(Long.parseLong(date));
	}

	/**
	 * 
	 * process data for excel
	 * 
	 */
	protected abstract <T> List<T> process();

	/*
	 * @Override public void getData(Map<String, Object> parameters, Delegator
	 * delegator) {
	 * 
	 * }
	 */

	/**
	 * method set title and name for excel sheet
	 * 
	 */
	/*
	 * public void setOtherConfigs(String title,String name) {
	 * sheetConfig.setTitle(UtilProperties.getMessage(resourceExcel,title,
	 * locale)); sheetConfig.setSheetName(name); };
	 */

	@Override
	public void getData() {
		sheetConfig.setDataConfig(prepareDataFields());
	}

	/**
	 * prepare columns for excels
	 * 
	 */

	@Override
	public List<Map<String, Object>> prepareDataFields() {
		return process();
	}

	@SuppressWarnings({ "unchecked" })
	private String newKey(U t) {
		if (t == null || t.equals(""))
			return null;

		String pattern = "_%";
		t = (U) t.toString();

		if (((String) t).indexOf(pattern) != -1) {
			return ((String) t).split(pattern)[0];
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<T, T> prepareColumns(Map<T, T> datafields) {

		for (Map.Entry<?, ?> t : datafields.entrySet()) {
			String _n = newKey((U) t.getKey());
			columnConfig = new ColumnConfig();
			columnConfig.setName(_n != null ? _n : (String) t.getKey());
			String header = resourceExcel != null ?  UtilProperties.getMessage(resourceExcel, (String) t.getValue(), locale) : null;
			header = (header == null || header.equals((String) t.getValue())) ? getLabels((String) t.getValue()) : header;
			columnConfig.setHeader(header);
			columnConfig.setDataType(_n != null ? DataType.NUMBERIC : DataType.STRING);
			columnConfigs.add(columnConfig);
		}

		if (this.mapFieldsDescribe != null) {
			for (Map.Entry<String, Object> ob : this.mapFieldsDescribe.entrySet()) {
				for (ColumnConfig cc : columnConfigs) {
					if (cc.getName().equals(ob.getKey())) {
						Object obj = ob.getValue();
						if (obj instanceof List) {
							List<String> s = (List<String>) obj;
							if (s.size() >= 2)
								cc.setDescribe(s.get(0), s.get(1));
						}

					}

				}
			}
		}
		return null;
	}

	public void setFieldDescribe(Map<String, Object> object) {
		this.mapFieldsDescribe = object;
	}

	public void setGroupConfig(Map<String, Object> groupConfigs) {
		getSheetConfig().setGroupConfigs(groupConfigs);
	}
	
	private void clearSource(){
		resourceList.clear();
		resourceList = null;
	}

	/**
	 * init excel
	 * 
	 * @param fromDate
	 * @param thruDate
	 */
	public void initExcel(HttpServletResponse response, String fromDate, String thruDate) {
		Workbook wb = null;
		try {
			prepareColumns(this.datafields);
			sheetConfig.setColumnConfig(columnConfigs);
			getData();
		} catch (Exception e) {
			Debug.logError("[EXCEL SHEET] - Error when init excel sheet cause : " + e.getMessage(), MODULE);
			e.printStackTrace();
		}

		try {

			wb = builder.build(UtilMisc.toList(sheetConfig), locale,
					fromDate != null ? convertDateFromString(fromDate) : null,
					thruDate != null ? convertDateFromString(thruDate) : null);
		} catch (ParseException e) {
			Debug.logError("[EXCEL SHEET] - Error when init excel sheet cause : " + e.getMessage(), MODULE);
			e.printStackTrace();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + sheetConfig.getSheetName() + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (IOException e) {
			Debug.log(e.getMessage(), MODULE);
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					Debug.log(e.getMessage(), MODULE);
				}
			if(resourceList != null)
				clearSource();
		}

	}

	/**
	 * init excel without fromDate,thruDate when display on excel sheet
	 * 
	 * @param fromDate
	 * @param thruDate
	 * 
	 */
	public void initExcel(HttpServletResponse response) {
		Workbook wb = null;
		try {
			prepareColumns(this.datafields);
			sheetConfig.setColumnConfig(columnConfigs);
			sheetConfig.setLocale(locale);
			getData();
		} catch (Exception e) {
			Debug.logError("[EXCEL SHEET] - Error when init excel sheet cause : " + e.getMessage(), MODULE);
			e.printStackTrace();
		}

		try {

			wb = builder.build(UtilMisc.toList(sheetConfig));

		} catch (ParseException e) {
			Debug.logError("[EXCEL SHEET] - Error when init excel sheet cause : " + e.getMessage(), MODULE);
			e.printStackTrace();
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			wb.write(baos);
			byte[] bytes = baos.toByteArray();
			response.setHeader("content-disposition", "attachment;filename=" + sheetConfig.getSheetName() + ".xls");
			response.setContentType("application/vnd.xls");
			response.getOutputStream().write(bytes);
		} catch (IOException e) {
			Debug.log(e.getMessage(), MODULE);
		} finally {
			if (baos != null)
				try {
					baos.close();
				} catch (IOException e) {
					Debug.log(e.getMessage(), MODULE);
				}
			if(resourceList != null)
				clearSource();
		}
	}

}