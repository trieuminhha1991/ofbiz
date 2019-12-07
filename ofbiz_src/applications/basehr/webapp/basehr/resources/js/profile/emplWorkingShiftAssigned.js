var date = new Date(globalVar.nowTimestamp);
var d = date.getDate();
var m = date.getMonth();
var y = date.getFullYear();
$(document).ready(function () {
	var calendar = $('#employeeWorkingShift').fullCalendar({
		 buttonText: {
			prev: '<i class="fa-chevron-left" style="cursor: pointer"></i>',
			next: '<i class="fa-chevron-right open-sans" style="cursor: pointer"></i>',
			today: '<span style="cursor: pointer">' + uiLabelMap.CommonToday + '</span>',
			
		},
		header: {
			left: 'prev,next today',
			center: 'title',
			right: ''
		},		
		//titleFormat: '',
		events: function(start, end, callback){	
			$("#loader_page_common").show();
			$.ajax({
				url: 'getWorkingShiftOfEmpl',
				type: 'POST',
				data:{
					start: start.getTime(),
					end: end.getTime()
				},
				success: function(response) {
					var events = [];
					if(response.responseMessage == 'success'){
						var listReturn = response.listReturn;
						if(listReturn && listReturn.length > 0){
							for(var i = 0; i<listReturn.length; i++){
								var event = {};
								var title = "";
								if(listReturn[i].workTypeId == 'HOLIDAY'){
									event.className = 'label-important';
									title = listReturn[i].holidayName;
									event.start = new Date(listReturn[i].dateWork);
								}else{
									if(listReturn[i].shiftStartTime && listReturn[i].shiftEndTime){
										var shiftStartTime = new Date(listReturn[i].shiftStartTime);
										var shiftEndTime = new Date(listReturn[i].shiftEndTime);
										title += (shiftStartTime.getHours() < 10? '0' : '') + shiftStartTime.getHours();  
										title += ":";
										title += (shiftStartTime.getMinutes() < 10? '0' : '') + shiftStartTime.getMinutes();
										title += " - ";
										title += (shiftEndTime.getHours() < 10? '0' : '') + shiftEndTime.getHours();
										title += ":";
										title += (shiftEndTime.getMinutes() < 10? '0' : '') + shiftEndTime.getMinutes();  
									}								
									event.start = new Date(listReturn[i].dateWork);
									if(listReturn[i].workTypeId == 'ALL_SHIFT'){
										event.className = 'label-success';	
									}else if(listReturn[i].workTypeId == 'FIRST_HALF_SHIFT'){
										event.className = 'label-grey';
									}else if(listReturn[i].workTypeId == 'SECOND_HALF_SHIFT'){
										event.className = 'label-purple';
									}else{
										event.className = 'label-primary';
										title = uiLabelMap.DateHoliday;
									}
								}
								event.title = title;
								events.push(event);	
							}
						}
					}
					callback(events);
				},
				complete: function(jqXHR, textStatus){
					$("#loader_page_common").hide();
				}
			});
		},
		eventClick: function(calEvent, jsEvent, view) {
			
		},
		editable: false,
		droppable: false, // this allows things to be dropped onto the calendar !!!
		selectable: true,
		selectHelper: true,
		select: function(start, end, allDay) {		
			calendar.fullCalendar('unselect');
		},
		dayNames:[uiLabelMap.CommonSunday, uiLabelMap.CommonMonday, uiLabelMap.CommonTuesday, 
		          uiLabelMap.CommonWednesday, uiLabelMap.CommonThursday, uiLabelMap.CommonFriday,
		          uiLabelMap.CommonSaturday],
		dayNamesShort: [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, 
		                uiLabelMap.CommonTuesdayShort, uiLabelMap.CommonWednesdayShort,
		                uiLabelMap.CommonThursdayShort, uiLabelMap.CommonFridayShort,
				        uiLabelMap.CommonSaturdayShort],
		monthNames: [uiLabelMap.wgjanuary, uiLabelMap.wgfebruary, uiLabelMap.wgmarch,
			         uiLabelMap.wgapril, uiLabelMap.wgmay, uiLabelMap.wgjune, 
			         uiLabelMap.wgjuly, uiLabelMap.wgaugust, uiLabelMap.wgseptember,
			         uiLabelMap.wgoctober, uiLabelMap.wgnovember, uiLabelMap.wgdecember],
		height: 490,
		
	});
});