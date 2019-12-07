package com.olbius.dashboard;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;

import com.olbius.dashboard.ChartModel;
import com.olbius.util.DateUtil;
import com.olbius.util.PartyUtil;

public class AgeChartModel extends ChartModel{

	@Override
	public void buildModel(Delegator delegator) {
		List<GenericValue> employeeList = PartyUtil.getEmployeeInOrg(delegator);
		
		double range1 = getAgeEmployeeNumber(0, 30, employeeList);
		double range2 = getAgeEmployeeNumber(30, 40, employeeList);
		double range3 = getAgeEmployeeNumber(40, 60, employeeList);
		double range4 = getAgeEmployeeNumber(60, 100, employeeList);
		double range0 = employeeList.size() - range1 - range2 - range3 - range4;
		//FIXME Age range
		model.put("range1", range1);
		model.put("range2", range2);
		model.put("range3", range3);
		model.put("range4", range4);
		model.put("range0", range0);
	}
	
	private double getAgeEmployeeNumber(int fromAge, int thruAge, List<GenericValue> employeeList){
		Timestamp now = new Timestamp(new Date().getTime());
		int count = 0;
		for(GenericValue empl: employeeList){
			java.sql.Date birthDate = (java.sql.Date)empl.get("birthDate");
			if(birthDate != null){
				Timestamp birthDateTmp = new Timestamp(birthDate.getTime());
				int age = DateUtil.getYearPeriodNumber(now, birthDateTmp);
				if (age >= fromAge && age < thruAge) {
					count++;
				}
			}
		}
		return count;
	}
}
