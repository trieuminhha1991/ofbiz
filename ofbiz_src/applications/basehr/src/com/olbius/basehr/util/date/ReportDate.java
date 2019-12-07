package com.olbius.basehr.util.date;

import java.sql.Date;
import java.util.Calendar;

public abstract class ReportDate {
	protected Date now;
	protected Date fromDate;
	protected Date thruDate;
	
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setThruDate(Date thruDate) {
		this.thruDate = thruDate;
	}
	public ReportDate(){
		now = getNow();
	}
	//Get now
	private Date getNow(){
		Calendar cal = Calendar.getInstance();
		return new Date(cal.getTimeInMillis());
	}
	
	public abstract Date getFromDate();
	public abstract Date getThruDate();
}
