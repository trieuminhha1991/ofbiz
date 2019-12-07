var quarter_li = (function(){
	var periodTypeId = "QUARTERLY";
	var now = new Date();
    var isApprove = "";
	var year = now.getFullYear();
	var quarter = parseInt(now.getMonth()/3) + 1;
	var init = function(){
		initJqxNumberInput();
		initBtnEvent();
        initGridEvent();
		initJqxNotification();
		refreshGridData(quarter, year, quarter, year);
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationNtf_jqxgrid_quarterly").jqxNotification({width: '100%', autoClose: true, template : 'info', appendContainer : "#containerNtf_jqxgrid_quarterly", opacity : 0.9});
	};
	
	var initJqxNumberInput = function(){
		$('#quarter_from').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 4, value : quarter});
		$('#year_from_quarter').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
		$('#quarter_to').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 4, value : quarter});
		$('#year_to_quarter').jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple', value : year});
	};
	
	var initBtnEvent = function(){
		$('#searchApproveKPI_quarter').click(function(){
			var quarter_from = $('#quarter_from').val();
			var year_from_quarter = $('#year_from_quarter').val();
			var quarter_to = $('#quarter_to').val();
			var year_to_quarter = $('#year_to_quarter').val();
			
			if(quarter_from == 0 || year_from_quarter == 0 || quarter_to == 0 || year_to_quarter == 0){
				bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
					"label" : uiLabelMap.CommonClose,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}])
			}else{
				refreshGridData(quarter_from, year_from_quarter, quarter_to, year_to_quarter);
			}
		});
        $('#btnAcceptAllQuarter').click(function(){
            var rows = $('#jqxgrid_quarterly').jqxGrid('getrows');
            if(rows.length == 0){
                bootbox.dialog(uiLabelMap.HRNoDataChoosedToApprove, [{
                    "label" : uiLabelMap.CommonClose,
                    "class" : "btn-danger btn-small icon-remove open-sans",
                }]);
                return false;
            }
            var data = getDataQuarterlyAppr();
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
    var getDataQuarterlyAppr = function(){
        var data = {};
        var quarterFrom = $('#quarter_from').val();
        var yearFromQuarter = $('#year_from_quarter').val();
        var quarterTo = $('#quarter_to').val();
        var yearToQuarter = $('#year_to_quarter').val();

        if(quarterFrom == 0 || yearFromQuarter == 0 || quarterTo == 0 || yearToQuarter == 0){
            bootbox.dialog(uiLabelMap.ChooseTimeRangeAppKPI, [{
                "label" : uiLabelMap.CommonClose,
                "class" : "btn-danger btn-small icon-remove open-sans",
            }])
        }else{
            isApprove = "KAS_ACCEPTED";
            data.statusId = isApprove;
            data.sendNtfToEmpl = "Y";
            data.quarterFrom = quarterFrom;
            data.yearFromQuarter = yearFromQuarter;
            data.quarterTo = quarterTo;
            data.yearToQuarter = yearToQuarter;
            data.periodTypeId = periodTypeId;
        }
        return data;

    };
    var initGridEvent = function(){
        $("#jqxgrid_quarterly").on('rowdoubleclick', function(event){
            var index = event.args.rowindex;
            var date =new Date();
            var data = $('#jqxgrid_quarterly').jqxGrid('getrowdata', index);
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
	var refreshGridData = function(quarter_from, year_from_quarter, quarter_to, year_to_quarter){
		var tmpS = $('#jqxgrid_quarterly').jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListKPIResultEmpl&quarter_from=" + quarter_from + "&year_from_quarter=" + year_from_quarter +
							"&quarter_to=" + quarter_to + "&year_to_quarter=" + year_to_quarter + "&periodTypeId=" + periodTypeId;
		$('#jqxgrid_quarterly').jqxGrid('source', tmpS);
	};
	
	var refreshData = function(message){
		$('#jqxgrid_quarterly').jqxGrid('updatebounddata');
		$('#pushResultKPIWindow').jqxWindow('close');
		$('#ajaxLoading').hide();
		$("#containerNtf_jqxgrid_quarterly").empty();
		$("#jqxNotificationNtf_jqxgrid_quarterly").jqxNotification('closeLast');
		$("#notificationContentNtf_jqxgrid_quarterly").text(message);
		$("#jqxNotificationNtf_jqxgrid_quarterly").jqxNotification('open');
	};
	
	return{
		refreshData : refreshData,
		init : init,
        getDataQuarterlyAppr: getDataQuarterlyAppr,
	}
}())

$(document).ready(function(){
	quarter_li.init();
})