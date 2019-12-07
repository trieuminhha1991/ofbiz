import javolution.util.FastList;
import javolution.util.FastMap;

Calendar cal = Calendar.getInstance();
cal.set(Calendar.DAY_OF_MONTH, 1);
Map<String, String> monthDisplay = FastMap.newInstance();
int maxMonth = cal.getActualMaximum(Calendar.MONTH);
int minMonth = cal.getActualMinimum(Calendar.MONTH);
//context.feb = "";
for(int i = minMonth; i <= maxMonth; i++){
	cal.set(Calendar.MONTH, i);
	String monthName = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, locale); 
	monthDisplay.put(String.valueOf(i), monthName);
	
}
List<String> listYear = FastList.newInstance();
listYear.add(cal.get(Calendar.YEAR).toString());
cal.add(Calendar.YEAR, -1);
listYear.add(cal.get(Calendar.YEAR).toString());
cal.add(Calendar.YEAR, -1);
listYear.add(cal.get(Calendar.YEAR).toString());
context.listYear = listYear;
context.monthDisplay = monthDisplay;