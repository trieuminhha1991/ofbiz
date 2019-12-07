String.prototype.checkContainValue = function(chars){
	 var value = this.toString();
	 if (chars == ".") {
		 chars = "\\.";
	}
	 var patt = new RegExp(chars);
	 var res = patt.test(value);
	 return res;
}
Date.prototype.customFormat = function(formatString){
    var YYYY,YY,MMMM,MMM,MM,M,DDDD,DDD,DD,D,hhh,hh,h,mm,m,ss,s,ampm,AMPM,dMod,th;
    var dateObject = this;
    YY = ((YYYY=dateObject.getFullYear())+"").slice(-2);
    MM = (M=dateObject.getMonth()+1)<10?('0'+M):M;
    MMM = (MMMM=["January","February","March","April","May","June","July","August","September","October","November","December"][M-1]).substring(0,3);
    DD = (D=dateObject.getDate())<10?('0'+D):D;
    DDD = (DDDD=["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"][dateObject.getDay()]).substring(0,3);
    th=(D>=10&&D<=20)?'th':((dMod=D%10)==1)?'st':(dMod==2)?'nd':(dMod==3)?'rd':'th';
    formatString = formatString.replace("#YYYY#",YYYY).replace("#YY#",YY).replace("#MMMM#",MMMM).replace("#MMM#",MMM).replace("#MM#",MM).replace("#M#",M).replace("#DDDD#",DDDD).replace("#DDD#",DDD).replace("#DD#",DD).replace("#D#",D).replace("#th#",th);

    h=(hhh=dateObject.getHours());
    if (h==0) h=24;
    if (h>12) h-=12;
    hh = h<10?('0'+h):h;
    AMPM=(ampm=hhh<12?'am':'pm').toUpperCase();
    mm=(m=dateObject.getMinutes())<10?('0'+m):m;
    ss=(s=dateObject.getSeconds())<10?('0'+s):s;
    return formatString.replace("#hhh#",hhh).replace("#hh#",hh).replace("#h#",h).replace("#mm#",mm).replace("#m#",m).replace("#ss#",ss).replace("#s#",s).replace("#ampm#",ampm).replace("#AMPM#",AMPM);
}
Date.prototype.toTimeStamp = function(){
	return this.customFormat("#YYYY#-#MM#-#DD#");
}
Date.prototype.toSQLTimeStamp = function(){
	return this.customFormat("#YYYY#-#MM#-#DD# #hhh#:#mm#:#ss#");
}
Date.prototype.toTimeOlbius = function(){
	return this.customFormat("#DD#/#MM#/#YYYY#");
}
Date.prototype.toDateTimeOlbius = function(){
	return this.customFormat("#DD#/#MM#/#YYYY# #hhh#:#mm#:#ss#");
}
Date.prototype.toTimeDateOlbius = function(){
	return this.customFormat("#hhh#:#mm#:#ss# #DD#/#MM#/#YYYY#");
}
Number.prototype.toDateTime = function(){
	return new Date(this);
}
//Array.prototype.isEmpty = function(){
//	var thisArray = this;
//	if (thisArray.length == 0) {
//		return true;
//	}
//	return false;
//}
var myStringlibrary = function () {};
myStringlibrary.prototype = {
		replaceAll: function(str1, str2, ignore){
				    	return this.replace(new RegExp(str1.replace(/([\/\,\!\\\^\$\{\}\[\]\(\)\.\*\+\?\|\<\>\-\&])/g,"\\$&"),(ignore?"gi":"g")),(typeof(str2)=="string")?str2.replace(/\$/g,"$$$$"):str2);
					},
		toInt: function(){
						return parseInt(this);
					},
		toFloat: function(){
						var value = String(this);
						value = value.toDecimal();
						if (locale == "vi") {
							value = value.replaceAll(",", ".");
						} else {
							value = value.replaceAll(".", ",");
						}
						return parseFloat(value);
					},
		splitTwoPart: function(chars){
						value = String(this);
						if (chars==".") {
							chars = "\\.";
						}
						var regexp = new RegExp("^([^&]*?)" + chars + "(.*)$");
						splited = value.match(regexp);
						splited == null?console.log(value):splited.shift();
						return splited;
					},
		getQuantityChars: function(chars){
						value = String(this);
						if (chars==".") {
							chars = "//.";
						}
						return (value.match(new RegExp(chars, "g")) || []).length;
					},
		toDecimalFormat: function(){
						value = String(this);
						var thousands;
						var fractional = "";
						var hasFractional = false;
						if (locale == "vi") {
							thousands = ".";
							if (value.checkContainValue(",")) {
								hasFractional = true;
								fractional = "," + value.split(",")[1];
								value = value.split(",")[0];
							}
						} else {
							thousands = ",";
							if (value.checkContainValue(".")) {
								hasFractional = true;
								fractional = "." + value.split(".")[1];
								value = value.split(".")[0];
							}
						}
						var length = value.length;
						var newValue = "";
						if (length < 3) {
							if (hasFractional) {
								return value + fractional;
							}
							return value;
						}
						value = reverseString(value);
						for (var i = 0; i < length; i++) {
							var thisChar = value.charAt(i);
							var unit = i%3;
							if (i!= 0 && unit == 0) {
								newValue += thousands;
							}
							newValue += thisChar;
						}
						if (hasFractional) {
							return reverseString(newValue) + fractional;
						}
						return reverseString(newValue);
					},
		toTimeStamp: function(){
					var date = this.toString();
					if (date == "") {
						return "";
					}else{
						var splDate = date.split('/');
						var timeStamp = splDate[2] + '-' + splDate[1] + '-' + splDate[0];
						return timeStamp;
					}
				},
		timeStampToTimeOlbius: function(){
					var date = this.toString();
					if (date == "") {
						return "";
					}else{
						var splDate = date.split(' ')[0];
						splDate = splDate.split('-');
						var timeStamp = splDate[2] + '/' + splDate[1] + '/' + splDate[0];
						return timeStamp;
					}
				},
		toMilliseconds: function(){
					var date = this.toString();
					if (date == "") {
						return "";
					}
					var splDate = date.split('/');
					if (splDate[2] != null) {
						var d = new Date(splDate[2], splDate[1] - 1, splDate[0]);
						return d.getTime();
					}
//					Sat Apr 04 2015 00:00:00 GMT+0700 (SE Asia Standard Time)
					date = new Date(date);
					return date.getTime();
				},
		containSpecialChars: function(){
					var str = this.toString();
					return /[~`!#$\^+=\\[\]\\';,/{}|\\":<>\?]/.test(str);
				},
		dateToDate: function() {
					var date = this.toString();
					if (date == "") {
						return "";
					}
					var splDate = date.split('-');
					if (splDate[2] != null) {
						var d = new Date(splDate[2], splDate[1] - 1, splDate[0]);
						return d;
					}
				}
};
String.prototype = $.extend(String.prototype, myStringlibrary.prototype);
String.prototype.toJson = function(){
	var data = this.toString();
	data = data.replaceAll("'", '"');
	data = JSON.parse(data);
	return data;
}
String.prototype.toDecimal = function(){
	var data = this.toString();
	if (locale == "vi") {
		data = data.replaceAll(".", '');
	} else {
		data = data.replaceAll(",", '');
	}
	return data;
}
function reverseString(value) {
	value = String(value);
	var length = value.length;
	var newValue = "";
	for (var i1 = length; i1 >= 0; i1--) {
		var thisChar = value.charAt(i1);
		newValue += thisChar;
	}
	return newValue;
}