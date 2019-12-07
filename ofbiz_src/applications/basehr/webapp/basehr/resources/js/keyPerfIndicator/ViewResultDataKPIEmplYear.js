var yearly_li = (function(){
	var periodTypeId = "YEARLY";
	var now = new Date();
	var _year = now.getFullYear();
	var isApprove = "";
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
        initGridEvent();
		initJqxNotification();
		refreshGridData(_year);
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf_jqxgrid_yearly").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf_jqxgrid_yearly", opacity : 0.9});
	};
	
	var initJqxNumberInput = function(){
		$('#year_yearly').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : _year});
	};
	
	var initBtnEvent = function(){
		$('#year_yearly').on('valueChanged', function(event){
			var value = event.args.value;
			refreshGridData(value);
		});
        $('#btnAcceptAllYear').click(function(){
            var rows = $('#jqxgrid_weekly').jqxGrid('getrows');
            if(rows.length == 0){
                bootbox.dialog(uiLabelMap.HRNoDataChoosedToApprove, [{
                    "label" : uiLabelMap.CommonClose,
                    "class" : "btn-danger btn-small icon-remove open-sans",
                }]);
                return false;
            }
            var data = getDataYearlyAppr();
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
    var getDataYearlyAppr = function(){
        var data = {};
        var year = $('#year_yearly').val();
        if(OlbCore.isEmpty(year)){
            bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
                "label" : uiLabelMap.CommonClose,
                "class" : "btn-danger btn-small icon-remove open-sans",
            }])
        }else{
            isApprove = "KAS_ACCEPTED";
            data.statusId = isApprove;
            data.sendNtfToEmpl = "Y";
            data.year = year;
            data.periodTypeId = periodTypeId;
        }
        return data;

    };
    var initGridEvent = function(){
        $("#jqxgrid_yearly").on('rowdoubleclick', function(event){
            var index = event.args.rowindex;
            var date =new Date();
            var data = $('#jqxgrid_yearly').jqxGrid('getrowdata', index);
            approve_kpi.dataSubmit.fromDate = data.fromDate.getTime();
            approve_kpi.dataSubmit.thruDate = date.getTime();
            approve_kpi.dataSubmit.criteriaId = data.criteriaId;
            approve_kpi.dataSubmit.partyId = data.partyId;
            //approve_kpi.dataSubmit.dateReviewed = data.dateReviewed.getTime();
            approve_kpi.dataSubmit.periodTypeId = periodTypeId;
            approve_kpi.setWindowPushResultData(data);
            openJqxWindow($('#pushResultKPIWindow'));
        });
    };
	var refreshGridData = function(year){
		var tmpS = $('#jqxgrid_yearly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListKPIResultEmpl&year=" + year + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_yearly').jqxGrid('source', tmpS);
	};
	
	var refreshData = function(message){
		$('#jqxgrid_yearly').jqxGrid('updatebounddata');
		$('#pushResultKPIWindow').jqxWindow('close');
		$('#ajaxLoading').hide();
		$("#containerNtf_jqxgrid_yearly").empty();
		$("#jqxNotificationNtf_jqxgrid_yearly").jqxNotification('closeLast');
		$("#notificationContentNtf_jqxgrid_yearly").text(message);
		$("#jqxNotificationNtf_jqxgrid_yearly").jqxNotification('open');
	};
	
	return{
		init : init,
		refreshData : refreshData,
        getDataYearlyAppr: getDataYearlyAppr,
	}
}());

$(document).ready(function(){
	yearly_li.init();
})