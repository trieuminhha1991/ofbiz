import java.sql.Timestamp;
import java.util.Date;

date = new Date();
currentTime = new Timestamp(date.getTime());

context.currentTime = currentTime;