/*this function set range startDate and endDate  for form when change value*/
var filterDate = (function(){
	var start,end;
	var defaultDate;
	var init = function(startDate,endDate){
		startDate ? start = $('#' + startDate) : start = null;
		endDate ? end = $('#' + endDate) : end = null;
		defaultDate  = {
			start : getDefault(start) ? getDefault(start) : null,
			end : getDefault(end) ? getDefault(end) : null
		};
		bindEvent();
	};
	
	var getDefault = function(obj){
		var date = new Object();
		date = {
			min : (obj.jqxDateTimeInput('getMinDate') != null) ? obj.jqxDateTimeInput('getMinDate') : null,
			max : (obj.jqxDateTimeInput('getMaxDate') !=null)? obj.jqxDateTimeInput('getMaxDate') : null
		};
		return date;
	};
	
	var resetDate = function(){
		if(start != null && defaultDate.start != null){
			start.jqxDateTimeInput('setMinDate',defaultDate.start.min);	
			start.jqxDateTimeInput('setMaxDate',defaultDate.start.max);
		}
		
		if(end != null && defaultDate.end != null){
			end.jqxDateTimeInput('setMinDate',defaultDate.end.min);	
			end.jqxDateTimeInput('setMaxDate',defaultDate.end.max);		
		}
	};
	
	var bindEvent = function(){
		if(start != null){
			start.on('change',function(){
				var date = start.jqxDateTimeInput('getDate');
				if(date && typeof(date) != 'undefined' && date != null){
					end.jqxDateTimeInput('setMinDate',new Date(date.getYear() + 1900,date.getMonth(),date.getDate()));					
				}
			});
		}
			
		if(end != null){
			end.on('change',function(){
					var date = end.jqxDateTimeInput('getDate');
					if(date && typeof(date) != 'undefined' && date != null){
						start.jqxDateTimeInput('setMaxDate',new Date(date.getYear() + 1900,date.getMonth(),date.getDate()));					
					}
				});
			}	
	};
	
	return {
		init : init,
		resetDate : resetDate		
	};
}());
