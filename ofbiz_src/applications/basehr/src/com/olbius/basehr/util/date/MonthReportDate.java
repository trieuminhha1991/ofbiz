package com.olbius.basehr.util.date;

import java.sql.Date;
import java.util.Calendar;

public class MonthReportDate extends ReportDate{

	@Override
	public Date getFromDate() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return new Date(cal.getTime().getTime());
	}

	@Override
	public Date getThruDate() {
		// TODO Auto-generated method stub
		return now;
	}

}
