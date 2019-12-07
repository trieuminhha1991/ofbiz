package com.olbius.acc.excel;

import org.ofbiz.base.util.UtilProperties;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author Namdn
 * 
 */
public class ExcelAccounting<T, U> extends AbstractExcel<T> {

	private List<Map<T, U>> listdata;

	public ExcelAccounting(Locale locale, Map<T, T> datafields, String... configs) {
		super(locale);
		if(configs.length >= 2)
		{
			setResource(configs.length > 2 ? configs[2] : null);
			otherConfigs(configs[0], configs[1]);
		}
		setDataFields(datafields);
	}

	/**
	 * init data for excel
	 * 
	 */
	public void setDataExcel(List<Map<T, U>> data) {
		this.listdata = data;
	}
	
	public void setResource(String resource){
			resourceExcel = resource;
	}
	
	public String getResource(){
		return resourceExcel != null ? resourceExcel : defaultResource;
	}

	/**
	 * use group column on excel
	 */
	public void useGroupColumn(boolean use) {
		getSheetConfig().setGroup(use);
	}

	@Override
	public void otherConfigs(String name, String title) {
		getSheetConfig().setSheetName(getCurrentLabels(name));
		getSheetConfig().setTitle(getCurrentLabels(title));
	}
	
	public String getCurrentLabels(String key){
		String labels = UtilProperties.getMessage(getResource(), key, locale);
		return ((labels == null || labels.equals(key)) ? getLabels(key):labels);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<Map<T, U>> process() {
		return this.listdata;
	}
}