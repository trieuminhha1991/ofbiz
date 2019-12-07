var approve_kpi = (function(){
	var isApprove = "";
	var _comment = "";
	var dataSubmit = {};
	var init = function(){
		initJqxWindow();
		initJqxInput();
		initJqxRadioButton();
		initRadioEvent();
		initJqxWindowEvent();
		initBtnEvent();
		initJqxValidator();
		$("#sendNtfToEmpl").jqxCheckBox({width: 90, height: 25});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($('#approveKPIWindow'), 555, 554, initJqxEditor);
	};
	var initJqxValidator = function(){
		$('#approveKPIWindow').jqxValidator({
			rules: [
				{input : '#rejectKPI', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var accept= $("#approveKPI").jqxRadioButton('checked');
						var reject= $("#rejectKPI").jqxRadioButton('checked');
						if(!accept && !reject){
							return false;
						}
						return true;
					}
				},  
			]
		
		});
	};
	var initJqxInput = function(){
		$('#kpiType').jqxInput({width : '96%', height : '20px', disabled : true, valueMember : ''});
		$('#kpiName').jqxInput({width : '96%', height : '20px', disabled : true});
		$('#partyName').jqxInput({width : '96%', height : '20px', disabled : true});
		$('#kpiWeight').jqxInput({width : '96%', height : '20px', disabled : true});
		$('#statusKPI').jqxInput({width : '96%', height : '20px', disabled : true});

		$('#kpiTarget').jqxNumberInput({width : '97%', height : '20px', disabled : true});
		$('#kpiActual').jqxNumberInput({width : '97%', height : '20px', disabled : true});
	};
	
	var initJqxEditor = function(){
		$("#kpiComment").jqxEditor({ 
    		width: '98%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });
	};
	
	var initJqxRadioButton = function(){
		$('#rejectKPI').jqxRadioButton({checked : false, theme : 'olbius', width: '98%'});
		$('#approveKPI').jqxRadioButton({checked : false, theme : 'olbius'});
	};
	
	var initRadioEvent = function(){
		$('#approveKPI').on('checked', function(){
			isApprove = "KAS_ACCEPTED";
		});
		$('#rejectKPI').on('checked', function(){
			isApprove = "KAS_REJECTED";
		});
	};
	var initJqxWindowEvent = function(){
		$('#approveKPIWindow').on('close', function(event){
			Grid.clearForm($(this));
			$('#rejectKPI').jqxRadioButton({checked : false});
			$('#approveKPI').jqxRadioButton({checked : false});
			
		});
		$('#approveKPIWindow').on('open', function(event){
			$('#kpiComment').jqxEditor('val', _comment);
		});
	};
	
	var initBtnEvent = function(){
		$('#cancelApprKpiType').click(function(){
			$('#approveKPIWindow').jqxWindow('close');
		});
		
		$('#saveApprKpiType').click(function(){
			var valid = $("#approveKPIWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getDataAppr();
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
				url : 'approveKPIEmpl',
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
	
	var getDataAppr = function(){
		var data = {};
		data = $.extend({}, dataSubmit);
		data.comment = $('#kpiComment').jqxEditor('val');
		data.statusId = isApprove;
		if($("#sendNtfToEmpl").jqxCheckBox('checked')){
			data.sendNtfToEmpl = "Y";
		}else{
			data.sendNtfToEmpl = "N";
		}
		return data;
	};
	
	var setWindowApproveData = function(data){
		var perfCriteriaTypeId = "";
		var status = "";
		var des = data.description;
		
		for(var i=0;i<globalVar.perfCriteriaTypeArr.length;i++){
			if(globalVar.perfCriteriaTypeArr[i].perfCriteriaTypeId == data.perfCriteriaTypeId){
				perfCriteriaTypeId = globalVar.perfCriteriaTypeArr[i].description;
				break;
			}
		}
		for(var i=0;i<globalVar.statusArr.length;i++){
			if(globalVar.statusArr[i].statusId == data.statusId){
				status = globalVar.statusArr[i].description;
				break;
			}
		}
		$('#kpiType').jqxInput('val', {label : perfCriteriaTypeId, value : data.perfCriteriaTypeId});
		$('#kpiName').jqxInput('val', {label : data.criteriaName, value : data.criteriaId});
		$('#partyName').val(data.fullName);
		$('#kpiWeight').jqxInput('val', {label : data.weight * 100 + "%", value : data.weight});
		$('#kpiTarget').jqxNumberInput('val', data.target);
		$('#kpiActual').jqxNumberInput('val', data.result);
		_comment = data.comment;
		$('#statusKPI').jqxInput('val', status);
	};
	
	return{
		init : init,
		setWindowApproveData : setWindowApproveData,
		getDataAppr : getDataAppr,
		dataSubmit : dataSubmit,
	}
}());

$(document).ready(function(){
	approve_kpi.init();
})