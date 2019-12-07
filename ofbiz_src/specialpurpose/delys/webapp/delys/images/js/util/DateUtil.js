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
