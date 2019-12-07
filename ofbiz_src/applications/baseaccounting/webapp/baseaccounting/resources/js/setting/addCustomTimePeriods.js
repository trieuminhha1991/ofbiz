$(function(){
	OlbCustomTimePeriod.init();
})

var OlbCustomTimePeriod = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	var init = function(){
		initjqxWindow();
		initDropDownButton();
		initDropDownList();
		initNumberInput();
		initDateTimeInput();
		initInput();
		initValidator();
		bindEvent();
	}
	var initjqxWindow = function(){
		$("#alterpopupWindow").jqxWindow({
	        width: 580, height : 380,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
	    });
	}
	var initValidator = function(){
		var mapRules = [
		                {input: '#fromDate', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#thruDate', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#orgPartyId', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#periodTypeIdAdd', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#periodNameAdd', type: 'validInputNotNull',action : 'change,close,blur'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
		accCm.dateUtil.init('fromDate','thruDate');
	}
	var initDateTimeInput = function(){
		jOlbUtil.dateTimeInput.create('#fromDate',{width: '250px', height: '25px',allowNullDate : true,value : null});
		jOlbUtil.dateTimeInput.create('#thruDate',{width: '250px', height: '25px',allowNullDate : true,value : null});
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#periodTypeIdAdd',{ source: dataPT, displayMember: "description", valueMember: "periodTypeId",obj : accCm});
		accCm.createDropDownList('#parentPeriodIdAdd',{autoDropDownHeight : false, source: dataOtp, displayMember: "periodName", valueMember: "customTimePeriodId",obj : accCm})
	}
	
	var initInput = function(){
		jOlbUtil.input.create('#periodNameAdd',{width: 245,height : 25});
	}
	
	var initNumberInput = function(){
		jOlbUtil.numberInput.create('#periodNum',{width: 250,height : 25,min : 0,max : 999999999999,digits : 17,decimalDigits : 0,spinButtons : true});
	}
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initOrganizationSelect($('#orgPartyId'),$("#jqxOrgPartyIdGridId"),{labels : (uiLabelMap ? uiLabelMap : null),dropDownHorizontalAlignment : true,wgrid : 480});
	}
	
	var save  = function(){
		if(!validatorVAL.validate()) { return;}
			var row = getData();	
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	    
		return true;
	}
	
	var getData = function(){
		var row;
        row = { 
        		fromDate:$('#fromDate').jqxDateTimeInput('getDate'),
        		parentPeriodId:$('#parentPeriodIdAdd').val(),
        		organizationPartyId:$('#orgPartyId').val(),
        		periodName:$('#periodNameAdd').val(),
        		periodNum:$('#periodNum').val(),
        		periodTypeId:$('#periodTypeIdAdd').val(),
        		thruDate: $('#thruDate').jqxDateTimeInput('getDate'),            
        	  };
        	  return row;
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
		$("#save").click(function () {
			if(save())  $("#alterpopupWindow").jqxWindow('close');
		});
		
	    $('#saveAndContinue').click(function(){
	    	save();
	    })
	    
	    $('#alterpopupWindow').on('close',function(){
	    	$('#orgPartyId').val('');
			$('#periodTypeIdAdd').jqxDropDownList('clearSelection');
			$('#parentPeriodIdAdd').jqxDropDownList('clearSelection');
			$("#fromDate").jqxDateTimeInput('value',null);
			$("#thruDate").jqxDateTimeInput('value',null);
			$("#periodNameAdd").jqxInput('val','');
			$("#periodNum").jqxNumberInput('clear');
			$('#formAdd').jqxValidator('hide');
			$('#jqxOrgPartyIdGridId').jqxGrid('clearSelection');
			accCm.dateUtil.resetDate();
	    });
	}
	
	return {
		init : init
	}
	
}())

