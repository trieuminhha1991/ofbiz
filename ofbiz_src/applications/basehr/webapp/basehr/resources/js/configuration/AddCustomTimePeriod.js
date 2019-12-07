var addCustomTimePeriodObject = (function(){
	var init = function(){
		initJqxNumberInput();
		initJqxGrid();
		initBtnEvent();
		initJqxWindow();
	};
	
	var initBtnEvent = function(){
		$("#cancelCreate").click(function(event){
			$("#alternativeAddPopup").jqxWindow('close');
		});
		$("#saveCreate").click(function(event){
			bootbox.dialog(uiLabelMap.CreateNewPayrollPeriodConfirm,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		 		 createPayrollPeriod();  	
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
		});
	};
	
	var createPayrollPeriod = function(){
		var year = $("#yearCustomTimePeriod").val();
		var dataRecord = $("#monthCustomTimePeriodGrid").jqxGrid('getrows');
		var periodPayrollArr = [];
		for(var index in dataRecord){
			var data = dataRecord[index];
			periodPayrollArr.push({periodName: data.periodName, fromDate: data.fromDate.getTime(), thruDate: data.thruDate.getTime()});
		}
		$.ajax({
			url: 'createPayrollCustomTimePeriod',
			data: {year: year, periodPayroll: JSON.stringify(periodPayrollArr)},
			type: 'POST',
			success: function(response){
				$('#jqxNotification').jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'info'});
		        	$("#jqxNotification").html(response.successMessage);
		        	$("#jqxNotification").jqxNotification("open");
		        	$("#jqxCustomTimePeriod").jqxTreeGrid('updateBoundData')
				}else{
					$('#container').empty();
		        	$('#jqxNotification').jqxNotification({ template: 'error'});
		        	$("#jqxNotification").html(response.errorMessage);
		        	$("#jqxNotification").jqxNotification("open");
				}
			},
			complete: function(jqXHR, textStatus){
				$("#alternativeAddPopup").jqxWindow('close');
    		}
		});
	};
	
	var initJqxNumberInput = function(){
		$("#yearCustomTimePeriod").jqxNumberInput({ width: '65px', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0, digits: 4});
		$('#yearCustomTimePeriod').on('valueChanged', function (event){
			var value = event.args.value;
			var data = getLocalDataInYear(value);
			updateGridData(data);
		}); 
	};
	
	var updateGridData = function(data){
		var source = $("#monthCustomTimePeriodGrid").jqxGrid('source');
		source._source.localdata = data;
		$("#monthCustomTimePeriodGrid").jqxGrid('source', source);
	}
	
	var initJqxWindow = function(){
		createJqxWindow($("#alternativeAddPopup"), 600, 515);
		$("#alternativeAddPopup").on('open', function(event){
			$('#yearCustomTimePeriod').val(globalVar.year);
		});
		$("#alternativeAddPopup").on('close', function(event){
			$('#yearCustomTimePeriod').val(0);
			updateGridData([]);
		});
	};
	
	var getLocalDataInYear = function(year){
		var data = new Array();
		for(var i = 0; i < 12; i++){
			var monthName = globalVar.monthNames[i];
			var fromDate = new Date(year, i, 1);
			var thruDate = new Date(year, i + 1, 0);
			data.push({periodName: monthName, fromDate: fromDate, thruDate: thruDate});
		}
		return data;
	};
	
	var initJqxGrid = function(){
		var data = [];
		var source = {
				localdata: data,
				datatype: "array",
				updaterow: function (rowid, rowdata, commit) {
					commit(true);
				},
				datafields:
                [
                    { name: 'periodName', type: 'string' },
                    { name: 'fromDate', type: 'date' },
                    { name: 'thruDate', type: 'date' }
                ]
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		var columns = [
		               {text: uiLabelMap.HRPeriodName, datafield: 'periodName', width: '30%' },
		               {text: uiLabelMap.CommonFromDate, datafield: 'fromDate', columntype: 'datetimeinput', width: '35%', cellsformat: 'dd/MM/yyyy'},
		               {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', columntype: 'datetimeinput', width: '35%', cellsformat: 'dd/MM/yyyy',}
		]; 
		$("#monthCustomTimePeriodGrid").jqxGrid({
			width: '100%',
			height: 363,
			source: dataAdapter,
            editable: true,
            columns: columns,
            pagesize: 12,
            pagesizeoptions: ['12', '15', '20'], 
            localization: getLocalization(),
            pageable: true
		});
	};
	return{
		init: init
	}
}());

$(function(){
	addCustomTimePeriodObject.init();
});
