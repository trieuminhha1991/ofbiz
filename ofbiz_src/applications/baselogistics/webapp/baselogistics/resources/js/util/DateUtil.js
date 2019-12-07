$(function(){
	
});
var DatetimeUtilObj = (function() {
	//Change date format to dd/MM/yyyy
	function formatDate (date){
		if(date == "undefined" || date == null || date == ""){
			return "";
		}else{
			var regex = /(\d{4})-(\d{2})-(\d{2})/;
			var dateArray = regex.exec(date);
			return dateArray[2] + "/" + dateArray[3] + "/" + dateArray[1];
		}
	}
	var addZero = function(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	};
	
	var getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	var formatFullDate = function(value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	};
	
	var formatToMinutes = function(value) {
		if (value) {
			var dateStr = "";
			dateStr += addZero(value.getDate()) + '/';
			dateStr += addZero(value.getMonth()+1) + '/';
			dateStr += addZero(value.getFullYear()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes());
			return dateStr;
		} else {
			return "";
		}
	};
	
	var getDateTimeFullFromString = function(str){
		var elm = str.split(" ");
		var date = elm[0].split("/");
		date = date.reverse();
		var dateString = "";
		for(x of date){
			dateString += (x+"-");
		}
		dateString = dateString.slice(0, -1);
		return dateString+"T"+elm[1];
	}
	
	return {
		formatFullDate: formatFullDate,
		formatDate: formatDate,
		getFormattedDate: getFormattedDate,
		formatToMinutes: formatToMinutes,
		getDateTimeFullFromString: getDateTimeFullFromString,
	}
}());