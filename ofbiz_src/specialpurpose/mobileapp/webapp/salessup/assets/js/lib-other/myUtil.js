
var utils = (function(){
	
	function getLabel(key,locale){
		if(uiLabelMap === undefined) 
			return key;
		
		return uiLabelMap[key] !== undefined ? uiLabelMap[key][locale ? locale : 'vi'] : key;
	}
	
	function getDayLabel(key,locale){
		if(uiLabelMap === undefined) 
			return key;
		
		return uiLabelMap[key] !== undefined ? uiLabelMap[key] : key;
	}
	
	function getLabelCustom(keyLb,appendContent,appendIndex,locale){
		if(typeof appendContent !== "object" || isNaN(appendIndex))
			return "";
		if(uiLabelMap === undefined || uiLabelMap[keyLb] === undefined) 
			return keyLb;
		
		var ui = uiLabelMap[keyLb];
		
		if(appendIndex)
			ui = {
					"vi" : ui["vi"] + " " + appendContent["vi"],
					"en" : ui["en"] + " " + appendContent["en"],
			}
		else
			{
				var partOne = ui["vi"].substring(0,appendIndex);
				var partTwo = ui["vi"].substring(appendIndex,ui["vi"].length);
			
				ui["vi"] = partOne + " " + appendContent["vi"] + " " +  partTwo;
				
				partOne = ui["en"].substring(0,appendIndex);
				partTwo = ui["en"].substring(appendIndex,ui["en"].length);
				
				ui["en"] = partOne + " " + appendContent["en"] + " " +  partTwo;
			}
			
		return ui !== undefined ? ui[locale ? locale : 'vi'] : keyLb;
	}
	
	function getRandomColor() {
		   var letters = '0123456789ABCDEF'.split('');
		   var color = '#';
		   for (var i = 0; i < 6; i++ ) {
		       color += letters[Math.floor(Math.random() * 16)];
		   }
		   
		   return color;
		}
	
	function makeid(length) {
		if (!length) {
			length = 5;
		}
		var text = "";
		var possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

		for (var i = 0; i < length; i++)
			text += possible.charAt(Math.floor(Math.random() * possible.length));

		return text;
	}
	
	
	function removeDuplicate(oldArr){
		return _.filter(oldArr, function (element, index) {
		    // tests if the element has a duplicate in the rest of the array
		    for(index += 1; index < oldArr.length; index += 1) {
		        if (_.isEqual(element, oldArr[index])) {
		            return false;
		        }
		    }
		    return true;
		});
	}
	
	function formatDDMMYYYY(date){
		if(isNaN(date) || date === undefined)
			return date;
		date = new Date(date);
		var mon = date.getMonth()  + 1;
		var year = 1900 + date.getYear()
		var day = date.getDate();
		var hours = date.getHours();
		hours = hours < 10 ? '0' + hours : hours;
		var minutes = date.getMinutes();
		minutes = minutes < 10 ? '0' + minutes : minutes;
		var second = date.getSeconds();
		second = second < 10 ? '0' + second : second;
		return day + '/' + mon + '/' + year + ' ' + hours + ':' + minutes + ":" + second;
	}
	
	
	function formatNumber(number){
		if(isNaN(number))
		{
			try {
				number = parseFloat(number);
				
				if(isNaN(number))
					return number;
			} catch (e) {
				throw e;
			}
		}	

		return  number.toFixed(2).replace(/./g, function(c, i, a) {
		    return i && c !== "." && ((a.length - i) % 3 === 0) ? ',' + c : c;
		});
	}
	
	return {
		getLabel : getLabel,
		getDayLabel : getDayLabel,
		getLabelCustom : getLabelCustom,
		getRandomColor : getRandomColor,
		removeDuplicate : removeDuplicate,
		formatDDMMYYYY : formatDDMMYYYY,
		formatNumber:formatNumber,
		makeid : makeid
	}
})()