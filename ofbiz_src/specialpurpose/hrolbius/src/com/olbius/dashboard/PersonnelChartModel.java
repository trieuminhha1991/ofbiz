package com.olbius.dashboard;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.util.PartyUtil;

public class PersonnelChartModel extends ChartModel{

	@Override
	public void buildModel(Delegator delegator) {
		//FIXME Fix year
		List<GenericValue> employeeList = PartyUtil.getEmployeeInOrg(delegator);
		//Setup 2010, 2011, 2012, 2013, 2014
		model.put("y2010", getEmployeeInYear(2010, employeeList));
		model.put("y2011", getEmployeeInYear(2011, employeeList));
		model.put("y2012", getEmployeeInYear(2012, employeeList));
		model.put("y2013", getEmployeeInYear(2013, employeeList));
		model.put("y2014", getEmployeeInYear(2014, employeeList));
	}
	public double getEmployeeInYear(int year, List<GenericValue> employeeList){
		double count = 0;
		for(GenericValue empl : employeeList){
			Timestamp date = empl.getTimestamp("fromDate");
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			if( cal.get(Calendar.YEAR)== year){
				count++;
			}
		}
		return count;
	}
}
