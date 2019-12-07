var GridUtils = {};
if(typeof(Grid) != "undefined"){
	GridUtils = Grid;
}else{
	$.getScript( "/aceadmin/jqw/jqwidgets/jqx.utils.js").done(function(){
		GridUtils = Grid;
	});
}

var Utils = {
	formatDateDMY : function(date, delimiter) {
		var today;
		if ( typeof (date) == 'string') {
			today = new Date(date);
		} else if ( typeof (date) == 'object') {
			today = date;
		}
		if ( typeof (today) == "undefined" || !today) {
			return date;
		}
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var a = "-";
		if (delimiter) {
			a = delimiter;
		}
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = dd + a + mm + a + yyyy;
		return today;
	}
};
	
