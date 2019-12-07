package com.olbius.basehr.report;

public class ReportUtil {
	public static int getMonth(String month){
		int month_int = 1;
		if(month.equals("jan")){
			month_int = 1;
		}else if(month.equals("feb")){
			month_int = 2;
		}else if(month.equals("mar")){
			month_int = 3;
		}else if(month.equals("apr")){
			month_int = 4;
		}else if(month.equals("may")){
			month_int = 5;
		}else if(month.equals("jun")){
			month_int = 6;
		}else if(month.equals("jul")){
			month_int = 7;
		}else if(month.equals("aug")){
			month_int = 8;
		}else if(month.equals("sep")){
			month_int = 9;
		}else if(month.equals("oct")){
			month_int = 10;
		}else if(month.equals("nov")){
			month_int = 11;
		}else{
			month_int = 12;
		}
		return month_int;
	}
}
