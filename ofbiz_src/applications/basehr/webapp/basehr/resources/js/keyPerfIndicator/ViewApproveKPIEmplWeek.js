var week_li = (function(){
	var periodTypeId = "WEEKLY";
	var curr = new Date();
	var isApprove = "";
	var first = curr.getDate() - curr.getDay() + 1; // First day is the day of the month - the day of the week
	var last = first + 6; // last day is the first day + 6

	var firstday = new Date(curr.setDate(first));
	var lastday = new Date(curr.setDate(last));
	
	var firstday_time = firstday.getTime();
	var lastday_time = lastday.getTime();
	
	var init = function(){
		initJqxDateTimeInput();
		initJqxDateTimeInputEvent();
		initJqxNotification();
		refreshGridData(firstday_time, lastday_time);
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf_jqxgrid_weekly").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf_jqxgrid_weekly", opacity : 0.9});
	};
	
	var initJqxDateTimeInput = function(){
		$('#dateTimeInput_jqxgrid_weekly').jqxDateTimeInput({width : 250, height : 20, selectionMode: 'range'});
		$('#dateTimeInput_jqxgrid_weekly').jqxDateTimeInput('setRange', firstday, lastday);
	};
	
	var initJqxDateTimeInputEvent = function(){
		$('#dateTimeInput_jqxgrid_weekly').on('change', function(event){
			var _fromDate = event.args.time.from;
			var _thruDate = event.args.time.to;
			
			var fromDate = _fromDate.getTime();
			var thruDate = _thruDate.getTime();
			refreshGridData(fromDate, thruDate);
		});
        $('#btnAcceptAllWeek').click(function(){
            var rows = $('#jqxgrid_weekly').jqxGrid('getrows');
            if(rows.length == 0){
                bootbox.dialog(uiLabelMap.HRNoDataChoosedToApprove, [{
                    "label" : uiLabelMap.CommonClose,
                    "class" : "btn-danger btn-small icon-remove open-sans",
                }]);
                return false;
            }
            var data = getDataWeeklyAppr();
            var flag = "";
            if(data.periodTypeId == "DAILY"){
                flag = "d";
            }else if(data.periodTypeId == "WEEKLY"){
                flag = "w";
            }else if(data.periodTypeId = "MONTHLY"){
                flag = "m";
            }else if(data.periodTypeId = "QUARTERLY"){
                flag = "q";
            }else if(data.periodTypeId = "YEARLY"){
                flag = "y";
            }

            $('#ajaxLoading').show();
            $.ajax({
                type : 'POST',
                data : data,
                datatype : 'json',
                url : 'approveAllKPIEmpl',
                success : function(response){
                    if(response._EVENT_MESSAGE_){
                        if(flag == "d"){
                            daily_start.refreshData(response._EVENT_MESSAGE_);
                        }else if(flag == "w"){
                            week_li.refreshData(response._EVENT_MESSAGE_);
                        }else if(flag == "m"){
                            month_li.refreshData(response._EVENT_MESSAGE_);
                        }else if(flag == "q"){
                            quarter_li.refreshData(response._EVENT_MESSAGE_);
                        }else{
                            yearly_li.refreshData(response._EVENT_MESSAGE_);
                        }
                    }
                }
            });
        });
	};
    var getDataWeeklyAppr = function(){
        var data = {};
        var range = $("#dateTimeInput").jqxDateTimeInput('getRange');
        var fromDate = null;
        var thruDate = null;
        if(OlbCore.isEmpty(range.from) && OlbCore.isEmpty(range.to)){
            bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
                "label" : uiLabelMap.CommonClose,
                "class" : "btn-danger btn-small icon-remove open-sans",
            }])
        }else{
            fromDate = range.from.getTime();
            thruDate = range.to.getTime();
            isApprove = "KAS_ACCEPTED";
            data.statusId = isApprove;
            data.sendNtfToEmpl = "Y";
            data.fromDate = fromDate;
            data.thruDate = thruDate;
            data.periodTypeId = periodTypeId;
        }
        return data;

    };
	var refreshGridData = function(fromDate, thruDate){
		var tmpS = $('#jqxgrid_weekly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListKPIEmplToApprove&periodTypeId=" + periodTypeId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$('#jqxgrid_weekly').jqxGrid('source', tmpS);
	};
	
	var refreshData = function(message){
		$('#jqxgrid_weekly').jqxGrid('updatebounddata');
		$('#approveKPIWindow').jqxWindow('close');
		$('#ajaxLoading').hide();
		$("#containerNtf_jqxgrid_weekly").empty();
		$("#jqxNotificationNtf_jqxgrid_weekly").jqxNotification('closeLast');
		$("#notificationContentNtf_jqxgrid_weekly").text(message);
		$("#jqxNotificationNtf_jqxgrid_weekly").jqxNotification('open');
	};
	
	return{
		refreshData : refreshData,
		init : init,
        getDataWeeklyAppr: getDataWeeklyAppr,
	}
}())

$(document).ready(function(){
	week_li.init();
})