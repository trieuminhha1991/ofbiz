import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

DateFormat df = new SimpleDateFormat("yyyy");
Date now = new Date();
currentYear = df.format(now);
context.currentYear = currentYear; 