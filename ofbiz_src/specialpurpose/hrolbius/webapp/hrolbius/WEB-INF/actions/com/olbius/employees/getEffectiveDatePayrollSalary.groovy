
Calendar cal = Calendar.getInstance();
cal.setTimeInMillis(fromDate.getTime());
String dateEffective = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);

if(thruDate){
	cal.setTimeInMillis(thruDate.getTime());
	dateEffective += " - " +  cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
}
context.dateEffective = dateEffective;