var daily_start = (function(){
	var periodTypeId = "DAILY";
	var now = new Date();
    var isApprove = "";
	var first_day_month = new Date(now.getFullYear(), now.getMonth(), 1);
	var last_day_month = new Date(now.getFullYear(), now.getMonth() + 1, 0);
	
	var first_day_month_time = first_day_month.getTime();
	var last_day_month_time = last_day_month.getTime();
	var init = function(){
		initJqxDateTimeInput();
		initJqxDateTimeInputEvent();
		initGridEvent();
		initJqxNotification();
		refreshGridData(first_day_month_time, last_day_month_time);
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
	};
	
	var initJqxDateTimeInput = function(){
		$('#dateTimeInput').jqxDateTimeInput({width : 250, height : 20, selectionMode: 'range'});
		$('#dateTimeInput').jqxDateTimeInput('setRange', first_day_month, last_day_month);
	};
	
	var initJqxDateTimeInputEvent = function(){
		$('#dateTimeInput').on('change', function(event){
			var fromDate = event.args.date.from;
			var thruDate = event.args.date.to;
			var fromDate_timestamp = fromDate.getTime();
			var thruDate_timestamp = thruDate.getTime();
			refreshGridData(fromDate_timestamp, thruDate_timestamp);
		})
	};
	
	var refreshGridData = function(fromDate, thruDate){
		var tmpS = $('#jqxgrid').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListKPIEmplToApprove&periodTypeId=" + periodTypeId + "&fromDate=" + fromDate + "&thruDate=" + thruDate;
		$('#jqxgrid').jqxGrid('source', tmpS);
	};
	
	var initGridEvent = function(){
		$("#jqxgrid").on('rowdoubleclick', function(event){
			var index = event.args.rowindex;
			var data = $('#jqxgrid').jqxGrid('getrowdata', index);
			approve_kpi.dataSubmit.fromDate = data.fromDate.getTime();
			approve_kpi.dataSubmit.criteriaId = data.criteriaId;
			approve_kpi.dataSubmit.partyId = data.partyId;
			approve_kpi.dataSubmit.dateReviewed = data.dateReviewed.getTime();
			approve_kpi.dataSubmit.periodTypeId = periodTypeId;
			
			approve_kpi.setWindowApproveData(data);
			openJqxWindow($('#approveKPIWindow'));
		});
        $('#btnAcceptAllDaily').click(function(){
            var rows = $('#jqxgrid').jqxGrid('getrows');
            if(rows.length == 0){
                bootbox.dialog(uiLabelMap.HRNoDataChoosedToApprove, [{
                    "label" : uiLabelMap.CommonClose,
                    "class" : "btn-danger btn-small icon-remove open-sans",
                }]);
                return false;
            }
            var data = getDataDailyAppr();
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
    var getDataDailyAppr = function(){
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

	var refreshData = function(message){
		$('#jqxgrid').jqxGrid('updatebounddata');
		$('#approveKPIWindow').jqxWindow('close');
		$('#ajaxLoading').hide();
		$("#containerNtf").empty();
		$("#jqxNotificationNtf").jqxNotification('closeLast');
		$("#notificationContentNtf").text(message);
		$("#jqxNotificationNtf").jqxNotification('open');
	};
	
	return{
		init : init,
		refreshData : refreshData,
        getDataDailyAppr: getDataDailyAppr,
	}
}())

$(document).ready(function(){
	daily_start.init();
})