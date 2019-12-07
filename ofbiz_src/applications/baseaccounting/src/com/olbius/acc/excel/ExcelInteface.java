package com.olbius.acc.excel;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Namdn
 * 
 */
interface ExcelInteface {

	public static final String TITLE = "TITLE";
	public static final String DATA = "DATA";
	public static final String SHEET_NAME = "SHEET_NAME";
	public static final String defaultResource = "BaseAccountingUiLabels";
	public static final String MODULE = ExcelInteface.class.getName();

	/**
	 * required override method cause method get Data for excel sheet
	 * 
	 */
	void getData();

	/**
	 * required name and title of excel sheet
	 * 
	 * @param name
	 *            The Name of excel sheet
	 * @param title
	 *            The title of excel sheet
	 */
	void otherConfigs(String name, String title);

	abstract <T> Map<T, T> prepareColumns(Map<T, T> datafields);

	abstract void initExcel(HttpServletResponse response, String fromDate, String thruDate);

	/**
	 * the method return list datafields for excel sheet, if datafields of excel
	 * not init in constructor of instance implements, method return list
	 * datafields'
	 * 
	 */
	abstract List<Map<String, Object>> prepareDataFields();
}