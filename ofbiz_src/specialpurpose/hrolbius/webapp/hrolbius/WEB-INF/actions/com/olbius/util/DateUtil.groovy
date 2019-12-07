import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

//Get current time
Date now = new Date();
currentTime = new Timestamp(now.getTime());

Calendar cal = Calendar.getInstance();
cal.setTime(now);
int currentYear =  cal.get(Calendar.YEAR);

context.currentTime = currentTime;
context.currentYear = String.valueOf(currentYear);