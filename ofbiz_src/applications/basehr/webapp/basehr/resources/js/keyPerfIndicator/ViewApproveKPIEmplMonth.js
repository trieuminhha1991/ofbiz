var month_li = (function(){
	var periodTypeId = "MONTHLY";
    var isApprove = "";
	var now = new Date();
	var year = now.getFullYear();
	var month = now.getMonth() + 1;
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
		initGridEvent();
		initJqxNotification();
		refreshGridData(month, year, month, year);
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf_jqxgrid_monthly").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf_jqxgrid_monthly", opacity : 0.9});
	};
	
	var initJqxNumberInput = function(){
		$('#month_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12, value : month});
		$('#year_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
		$('#month_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12, value : month});
		$('#year_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
	};
	
	var initBtnEvent = function(){
		$('#searchApproveKPI').click(function(){
			var month_from = $('#month_from').val();
			var year_from = $('#year_from').val();
			var month_to = $('#month_to').val();
			var year_to = $('#year_to').val();
			
			if(month_from == 0 || year_from == 0 || month_to == 0 || year_to == 0){
				bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
					"label" : uiLabelMap.CommonClose,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}])
			}else{
				refreshGridData(month_from,year_from,month_to,year_to);
			}
		});
        $('#btnAcceptAllMonth').click(function(){
            var rows = $('#jqxgrid_monthly').jqxGrid('getrows');
            if(rows.length == 0){
                bootbox.dialog(uiLabelMap.HRNoDataChoosedToApprove, [{
                    "label" : uiLabelMap.CommonClose,
                    "class" : "btn-danger btn-small icon-remove open-sans",
                }]);
                return false;
            }
            var data = getDataMonthAppr();
            var flag = "";
            if(periodTypeId == "DAILY"){
                flag = "d";
            }else if(periodTypeId == "WEEKLY"){
                flag = "w";
            }else if(periodTypeId = "MONTHLY"){
                flag = "m";
            }else if(periodTypeId = "QUARTERLY"){
                flag = "q";
            }else if(periodTypeId = "YEARLY"){
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
    var getDataMonthAppr = function(){
        var data = {};
        var monthFrom = $('#month_from').val();
        var yearFrom = $('#year_from').val();
        var monthTo = $('#month_to').val();
        var yearTo = $('#year_to').val();

        if(monthFrom == 0 || yearFrom == 0 || monthTo == 0 || yearTo == 0){
            bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
                "label" : uiLabelMap.CommonClose,
                "class" : "btn-danger btn-small icon-remove open-sans",
            }])
        }else{
            isApprove = "KAS_ACCEPTED";
            data.statusId = isApprove;
            data.sendNtfToEmpl = "Y";
            data.monthFrom = monthFrom;
            data.yearFrom = yearFrom;
            data.monthTo = monthTo;
            data.yearTo = yearTo;
            data.periodTypeId = periodTypeId;
        }
        return data;

    };
	
	var refreshGridData = function(month_from,year_from,month_to,year_to){
		var tmpS = $('#jqxgrid_monthly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListKPIEmplToApprove&month_from=" + month_from + "&year_from=" + year_from + 
							"&month_to=" + month_to + "&year_to=" + year_to + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_monthly').jqxGrid('source', tmpS);
	};
	
	var initGridEvent = function(){
		$("#jqxgrid_monthly").on('rowdoubleclick', function(event){
			var index = event.args.rowindex;
			var data = $('#jqxgrid_monthly').jqxGrid('getrowdata', index);
			approve_kpi.dataSubmit.fromDate = data.fromDate.getTime();
			approve_kpi.dataSubmit.criteriaId = data.criteriaId;
			approve_kpi.dataSubmit.partyId = data.partyId;
			approve_kpi.dataSubmit.dateReviewed = data.dateReviewed.getTime();
			approve_kpi.dataSubmit.periodTypeId = periodTypeId;
			approve_kpi.setWindowApproveData(data);
			openJqxWindow($('#approveKPIWindow'));
		});
	};
	
	var refreshData = function(message){
		$('#jqxgrid_monthly').jqxGrid('updatebounddata');
		$('#approveKPIWindow').jqxWindow('close');
		$('#ajaxLoading').hide();
		$("#containerNtf_jqxgrid_monthly").empty();
		$("#jqxNotificationNtf_jqxgrid_monthly").jqxNotification('closeLast');
		$("#notificationContentNtf_jqxgrid_monthly").text(message);
		$("#jqxNotificationNtf_jqxgrid_monthly").jqxNotification('open');
	};
	
	return{
		init : init,
		refreshData : refreshData,
        getDataMonthAppr: getDataMonthAppr,
	}
}())

$(document).ready(function(){
	month_li.init();
})