package com.olbius.basehr.util.date;

import java.sql.Date;
import java.util.Calendar;

public class WeekReportDate extends ReportDate{

	@Override
	public Date getFromDate() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
		return new Date(cal.getTimeInMillis());
	}

	@Override
	public Date getThruDate() {
		// TODO Auto-generated method stub
		return now;
	}

}
