package com.olbius.payroll;

import java.sql.Timestamp;
import java.util.Calendar;

public class EmplPositionTypeWorkDay {
	private Timestamp fromDate;
	private Timestamp thruDate;
	private String emplPositionTypeId;
	
	public EmplPositionTypeWorkDay(Timestamp fromDate, Timestamp thruDate,
			String emplPositionTypeId) {
		super();
		this.fromDate = fromDate;
		this.thruDate = thruDate;
		this.emplPositionTypeId = emplPositionTypeId;
	}
	
	public EmplPositionTypeWorkDay() {
		super();
	}

	public Timestamp getFromDate() {
		return fromDate;
	}
	public void setFromDate(Timestamp fromDate) {
		this.fromDate = fromDate;
	}
	public Timestamp getThruDate() {
		return thruDate;
	}
	public void setThruDate(Timestamp thruDate) {
		this.thruDate = thruDate;
	}
	public String getEmplPositionTypeId() {
		return emplPositionTypeId;
	}
	public void setEmplPositionTypeId(String emplPositionTypeId) {
		this.emplPositionTypeId = emplPositionTypeId;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		if(fromDate == null || thruDate == null){
			return 0;
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromDate);
		int fromDateDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		cal.setTime(thruDate);
		int thruDateDayOfWeek  = cal.get(Calendar.DAY_OF_WEEK);
		return (fromDateDayOfWeek + thruDateDayOfWeek);
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if(obj instanceof EmplPositionTypeWorkDay){
			EmplPositionTypeWorkDay temp = (EmplPositionTypeWorkDay)obj;
			if(((temp.getFromDate() != null && temp.getFromDate().equals(fromDate)) || (fromDate == null && temp.getFromDate() == null))
					&& ((temp.getThruDate() != null && temp.getThruDate().equals(thruDate)) || (thruDate == null && temp.getThruDate() == null)) 
					&& temp.getEmplPositionTypeId().equals(emplPositionTypeId)){
				return true;
			}
			return false;
		}
		return false;
	}
}
