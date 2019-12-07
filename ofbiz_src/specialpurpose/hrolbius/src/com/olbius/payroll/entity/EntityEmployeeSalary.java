package com.olbius.payroll.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityEmployeeSalary implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<EntitySalaryAmount> listSalaryAmount;
	Map<String, String> mapFormulaVqalue;

	public List<EntitySalaryAmount> getListSalaryAmount() {
		return listSalaryAmount;
	}

	public EntityEmployeeSalary() {
		listSalaryAmount = new ArrayList<EntitySalaryAmount>();
	}

	public void setListSalaryAmount(List<EntitySalaryAmount> listSalaryAmount) {
		this.listSalaryAmount = listSalaryAmount;
	}

	public Map<String, String> getMapFormulaVqalue() {
		return mapFormulaVqalue;
	}

	public void setMapFormulaVqalue(Map<String, String> mapFormulaVqalue) {
		this.mapFormulaVqalue = mapFormulaVqalue;
	}

}
