package com.olbius.basehr.util.date;

import java.sql.Date;
import java.util.Calendar;

public class QuarterReportDate extends ReportDate{

	@Override
	public Date getFromDate() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		int quarterNum = cal.get(Calendar.MONTH)/3 + 1;
		
		//result
		Date result = null;
		switch (quarterNum) {
		case 1:
			cal.set(1, 1);
			result = new Date(cal.getTimeInMillis());
			break;
		case 2:
			cal.set(4, 1);
			result = new Date(cal.getTimeInMillis());
		case 3:
			cal.set(7, 1);
			result = new Date(cal.getTimeInMillis());
		case 4:
			cal.set(10, 1);
			result = new Date(cal.getTimeInMillis());
		default:
			break;
		}
		return result;
	}

	@Override
	public Date getThruDate() {
		// TODO Auto-generated method stub
		return now;
	}

}
