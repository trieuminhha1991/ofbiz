var Utils = {
	getCurrentDate : function() {
		var today = new Date();
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!

		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = dd + '-' + mm + '-' + yyyy;

		return today;
	},

	getCurrentDateYMD : function(delimiter) {
		var today = new Date();
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var a = "-";
		if(delimiter){
			a = delimiter;
		}
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = yyyy + a + mm + a + dd;

		return today;
	},

	formatDateDMY : function(date, delimiter) {
		var today = new Date(date);
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var a = "-";
		if(delimiter){
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
	},
	formatDateYMD : function(date, delimiter) {
		var today = new Date(date);
		var dd = today.getDate();
		var mm = today.getMonth() + 1;
		// January is 0!
		var a = "-";
		if(delimiter){
			a = delimiter;
		}
		var yyyy = today.getFullYear();
		if (dd < 10) {
			dd = '0' + dd;
		}
		if (mm < 10) {
			mm = '0' + mm;
		}
		today = yyyy + a + mm + a + dd;
		return today;
	},
	getUrlParameter : function(sParam) {
		var sPageURL = window.location.search.substring(1);
		var sURLVariables = sPageURL.split('&');
		for (var i = 0; i < sURLVariables.length; i++) {
			var sParameterName = sURLVariables[i].split('=');
			if (sParameterName[0] == sParam) {
				return sParameterName[1];
			}
		}
	},
	formatcurrency : function(num, uom) {
		if (num == null) {
			return "";
		}
		decimalseparator = ",";
		thousandsseparator = ".";
		currencysymbol = "đ";
		if ( typeof (uom) == "undefined" || uom == null) {
			uom = "VND";
		}
		if (uom == "USD") {
			currencysymbol = "$";
			decimalseparator = ".";
			thousandsseparator = ",";
		} else if (uom == "EUR") {
			currencysymbol = "€";
			decimalseparator = ".";
			thousandsseparator = ",";
		}
		var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
		if (str.indexOf(".") > 0) {
			parts = str.split(".");
			str = parts[0];
		}
		str = str.split("").reverse();
		for (var j = 0, len = str.length; j < len; j++) {
			if (str[j] != ",") {
				output.push(str[j]);
				if (i % 3 == 0 && j < (len - 1)) {
					output.push(thousandsseparator);
				}
				i++;
			}
		}
		formatted = output.reverse().join("");
		return (formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : ""));
	},
	currencyToNumber : function(cur, uom){
		if(!uom){
			return;
		}	
		var res = 0;
		switch (uom){
			case "USD" : 
				var tmp = cur.replace(/,/g, "");
				res = parseFloat(tmp);
				break;
			case "EUR" : 
				var tmp = cur.replace(/,/g, "");
				res = parseFloat(tmp);
				break;
			case "VND" : 
				var tmp = cur.replace(/\./g, "");
				tmp = tmp.replace(/,/g,".");
				res = parseFloat(tmp);	
				break;
		}
		return res;
	},
	getFormData : function(id){
		var data = {};
	 	$.each($('#' + id).serializeArray(), function(_, kv) {
	 		if(kv.value && kv.value.replace(/\s/g, '').length){
	 			data[kv.name] = kv.value;	
	 		}
		});
		return data;
	}
};
