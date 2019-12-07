$(function(){
	OlbGlAccount.init();
})

var OlbGlAccount = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	var init = function(){
		initInputs();
		initjqxWindow();
		initDropDownList();
		initDropDownButton();
		initValidator();
		bindEvent();
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#glAccountTypeId2',{autoDropDownHeight : false, source: dataGLAT, displayMember: "description", valueMember: "glAccountTypeId",obj : accCm});
		accCm.createDropDownList('#glAccountClassId2',{ autoDropDownHeight : false,source: dataGLAC, displayMember: "description", valueMember: "glAccountClassId",obj : accCm});
		accCm.createDropDownList('#glResourceTypeId2',{autoDropDownHeight : false, source: dataGRT, displayMember: "description", valueMember: "glResourceTypeId",obj : accCm});
		accCm.createDropDownList('#glTaxFormId2',{autoDropDownHeight : false,source: dataTFAI, displayMember: "description", valueMember: "glTaxFormId",obj : accCm});
	}
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg('JQGetListChartOfAccountOriginationTrans',$('#parentGlAccountId2'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 250,labels :uiLabelMap});
	}
	
	var initInputs = function(){
		jOlbUtil.input.create("#glAccountId2",{width:245, theme:theme, placeHolder: uiLabelMap.BACCglAccountId});
		jOlbUtil.input.create("#accountName2",{width:245, theme:theme, placeHolder: uiLabelMap.BACCaccountName});
		jOlbUtil.input.create("#accountCode2",{width:245, theme:theme, placeHolder: uiLabelMap.BACCaccountCode});
		jOlbUtil.input.create("#description2",{width:245, theme:theme, placeHolder: uiLabelMap.BACCdescription});
	}
	
	/*clear form*/
	var clear = function(){
		clearInput();
		clearDropdown();
	}
	
	var clearInput = function(input){
		var inputArr = $('input[role=textbox]');
		for(var key in inputArr){
			if(typeof(inputArr[key].id) != 'undefined' && inputArr[key].id){
				$('#'+inputArr[key].id).jqxInput('val','');
			}
		}
	}
	
	var clearDropdown = function(){
		$('#glAccountTypeId2').jqxDropDownList('clearSelection');
		$('#glAccountClassId2').jqxDropDownList('clearSelection');
		$('#glResourceTypeId2').jqxDropDownList('clearSelection');
		$('#parentGlAccountId2').jqxDropDownButton('val','');
		$('#glTaxFormId2').jqxDropDownList('clearSelection');
	}
	
	var initjqxWindow = function(){
		$("#alterpopupWindow").jqxWindow({
	        width: 550, height : 480,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
	    });
	}
	
	var save = function(){
		var row;
        row = {
    		glAccountId:$('#glAccountId2').val(),
    		glAccountTypeId: $('#glAccountTypeId2').val(),
    		glResourceTypeId: $('#glResourceTypeId2').val(),
    		parentGlAccountId: $('#parentGlAccountId2').val(),
    		glTaxFormId: $('#glTaxFormId2').val(),
    		accountCode: $('#accountCode2').val(),
    		accountName: $('#accountName2').val(),
    		description: $('#description2').val(),
    		glAccountClassId:$('#glAccountClassId2').val()              
        };
	   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	}
	
	var bindEvent = function(){
		 // update the edited row when the user clicks the 'Save' button.
	    $('#alterpopupWindow').on('close', function (event) {
	    	$('#formAdd').jqxValidator('hide');
	    });
	    
	    $("#save").click(function () {
	    	if(validatorVAL.validate()){
	    		save();
	    		$("#alterpopupWindow").jqxWindow('close');
	    	} else {
	    		return;
	    	}
	    });
	    
	    $('#saveAndContinue').click(function(){
	    	if (validatorVAL.validate()) {
	    		save();
	    		clear();
	    	} else {
	    		return;
	    	}
	    });
	    
	    $('#alterpopupWindow').on('close',function(){
	    	clear();
	    	$('#formAdd').jqxValidator('hide');
	    })
	}
	
	var initValidator = function(){
		var mapRules = [
		                {input: '#glAccountId2', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#glAccountTypeId2', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#glTaxFormId2', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#accountCode2', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#accountName2', type: 'validInputNotNull',action : 'change,close'}
		             ];
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	return {
		init : init
	}
}())