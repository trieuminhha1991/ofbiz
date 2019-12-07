package com.olbius.basehr.util.date;

import java.security.InvalidParameterException;

public class FactoryReportDate {
	public static final String MONTH = "mm";
	public static final String DAY = "dd";
	public static final String WEEK = "ww";
	public static final String QUARTER = "qq";
	public static final String YEAR = "yy";
	public static final String CUSTOM = "oo";
	
	public static ReportDate getReportDate(String customTime){
		switch (customTime) {
		case DAY:
			return new DayReportDate();
		case MONTH:
			return new MonthReportDate();
		case WEEK:
			return new WeekReportDate();
		case QUARTER:
			return new QuarterReportDate();
		case YEAR:
			return new YearReportDate();
		case CUSTOM:
			return new CustomReportDate();
		default:
			String msg = "Custom time must be mm or dd or ww or qq or yy or oo"; 
			throw new InvalidParameterException(msg);
		}
	}
}
