package com.olbius.basehr.util.date;

import java.sql.Date;
import java.util.Calendar;

public class YearReportDate extends ReportDate{

	@Override
	public Date getFromDate() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(1, 1);
		return new Date(cal.getTimeInMillis());
	}

	@Override
	public Date getThruDate() {
		// TODO Auto-generated method stub
		return now;
	}

}
