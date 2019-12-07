import sys.process._
import java.time._
import java.time.format._
import java.util._
import util.Random
import scala.collection.mutable._
import scala.util.control.Breaks

object DataPreparation {
	// Pre-defined variables
	final val startDate = "2015-01-01 00:00:00"
	final val endDate = "2016-01-30 23:59:59"
	final val maxSO = 10;
	final val minSO = 5;
	final val maxSOPos = 20;
	final val minSOPos = 10;
	final val maxPO = 8;
	final val minPO = 5;

	def main(args: Array[String]) {
		// Working directory
		val jmeterPath = "/home/rongnguyen/Tools/apache-jmeter-groovy-without-docs/bin/jmeter"
		val scriptPOSPath = "/home/rongnguyen/Olbius/sources/core/scriptjmeter/scala-po/POS.jmx"
		val scriptPOPath = "/home/rongnguyen/Olbius/sources/core/scriptjmeter/scala-po/PO.jmx"
		val scriptSOPath = "/home/rongnguyen/Olbius/sources/core/scriptjmeter/scala-po/SO.jmx"

		// Change value in properties file
		// Run pos.jms script
		//var result = "sudo " + jmeterPath + " -n -t " + scriptPath!

		val startLongDate = convertToEpoch(startDate)	
		val endLongDate = convertToEpoch(endDate)

		var currentLongDate = startLongDate
		var currentMonth = getMonth(currentLongDate) - 1			
		var currentDate = getDay(currentLongDate)			
		var maxLongMonth = getLongEndOfMonth(startLongDate)
		// Random number of PO
		var PONumber = Random.nextInt(maxPO - minPO) + minPO;
		var tmpValue = maxLongMonth - currentLongDate
		var arrayPO = Seq.fill(PONumber)(Random.nextInt((tmpValue).toInt).toLong + startLongDate)
		var tmpPOData = scala.collection.immutable.List[Long]()
		// We have to create one PO first
		// change SystemDate
		"sudo date -s @" + startLongDate!
		
		// If existed data does not have PO, please uncomment next line
		"sudo " + jmeterPath + " -n -t " + scriptPOPath!
		
		while(currentLongDate < endLongDate){			
			if(getMonth(currentLongDate) != currentMonth){
					// Random number of PO
					PONumber = Random.nextInt(maxPO - minPO) + minPO;
					maxLongMonth = getLongEndOfMonth(currentLongDate)
					tmpValue = maxLongMonth - currentLongDate
					// get random time array for PO
					arrayPO = Seq.fill(PONumber)(Random.nextInt((tmpValue).toInt).toLong + currentLongDate)
					tmpPOData = scala.collection.immutable.List[Long]()
					for ( x <- arrayPO ) {
						tmpPOData = x :: tmpPOData
						println(x)
					}
					tmpPOData = tmpPOData.sortWith(_.compareTo(_) < 0)
					currentMonth = getMonth(currentLongDate)
					println("The number of PO for month " + getMonth(currentLongDate) + " : " + tmpPOData.length)
			}
			// Random number for SO
			var SONumber = Random.nextInt(maxSO - minSO) + minSO;
			// Random number for SOPos
			var SOPosNumber = Random.nextInt(maxSOPos - minSOPos) + minSOPos;
			// get random time array for SO
			var startCurrentLongDate = getStartOfDay(currentLongDate);
			var endCurrentLongDate = getEndOfDay(currentLongDate);
			tmpValue = endCurrentLongDate - startCurrentLongDate
			var arraySO = Seq.fill(SONumber)(Random.nextInt((tmpValue).toInt) + startCurrentLongDate)
			// get random time array for SO			
			var arraySOPos = Seq.fill(SOPosNumber)(Random.nextInt((tmpValue).toInt) + startCurrentLongDate)
			var lNumber = arraySOPos.length + arraySO.length -1
			var tmpData = scala.collection.immutable.List[Long]()
			for( cdt <- arraySO){
				tmpData = cdt :: tmpData
			}
			for( cdt <- arraySOPos){
				tmpData = cdt :: tmpData
			}
			
			
			// Add start and ending day: Make sure PO will be run
			tmpData = getStartOfDay(currentLongDate) :: tmpData
			tmpData = getEndOfDay(currentLongDate) :: tmpData
			tmpData = tmpData.sortWith(_.compareTo(_) < 0)
			var previousLong =  tmpData(0);
			for( cdt <- tmpData){
				// change SystemDate
				"sudo date -s @" + cdt!
				
				//var bLoop = new Breaks;
				//bLoop.breakable {
					for(so <- arraySO){
						if(so == cdt){
							// invoke jmeter to create SO
							"sudo " + jmeterPath + " -n -t " + scriptSOPath!
							
							// remove element from list
							arraySO = arraySO.filterNot(elm => elm == so)
							//bLoop.break;
						}
					}
				//}
				//bLoop = new Breaks;
				//bLoop.breakable {
					for(sop <- arraySOPos){
						if(sop == cdt){
							// invoke jmeter to create SOPos
							"sudo " + jmeterPath + " -n -t " + scriptPOSPath!
							
							// remove element from list
							arraySOPos = arraySOPos.filterNot(elm => elm == sop)
							//bLoop.break;
						}
					}
				//}
				//bLoop = new Breaks;
				//bLoop.breakable {
					for(po <- tmpPOData){
						if(po >= previousLong && po <= cdt){
							// invoke jmeter co create PO
							"sudo " + jmeterPath + " -n -t " + scriptPOPath!
							
							// remove element from list
							//tmpPOData = tmpPOData.filterNot(elm => elm == po)
							//bLoop.break;
						}
					}
				//}
				previousLong = cdt
			}
			// go to next day
			currentLongDate = currentLongDate + 86400
			currentLongDate = getStartOfDay(currentLongDate)
		}
	}
	def convertToEpoch(dtime: String): Long={
		var tmpFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);
		var zdt = ZonedDateTime.parse(dtime, tmpFormat);
		return zdt.toEpochSecond()//zdt.toInstant().toEpochMilli();
	}
	def getMonth(x: Long): Int = { 
		var i = Instant.ofEpochSecond(x);
		var z = ZonedDateTime.ofInstant( i, ZoneId.systemDefault());
		return z.getMonth().getValue()
	}
	def getDay(x: Long): Int = { 
		var i = Instant.ofEpochSecond(x);
		var z = ZonedDateTime.ofInstant( i, ZoneId.systemDefault());
		return z.getDayOfMonth()
	}
	def getZDT(x: Long): ZonedDateTime = { 
		var i = Instant.ofEpochSecond(x);
		var z = ZonedDateTime.ofInstant( i, ZoneId.systemDefault());
		return z
	}
	def getDate(x: Long): Date = {	
		return Date.from(getZDT(x).toInstant());
	}
	def getLongEndOfMonth(x: Long): Long = {
		var currentDate = getDate(x);
		var c = Calendar.getInstance();
		c.setTime(currentDate);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return (c.getTime()).getTime()/1000l;
	}	
	def getEndOfDay(x: Long): Long = {
		var date = getDate(x);
		var c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return (c.getTime()).getTime()/1000l;
	}

	def getStartOfDay(x: Long): Long = {
		var date = getDate(x);
		var c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return (c.getTime()).getTime()/1000l;
	}
}
