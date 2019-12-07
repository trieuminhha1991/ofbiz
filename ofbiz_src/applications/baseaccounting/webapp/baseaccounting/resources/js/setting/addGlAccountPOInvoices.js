$(function(){
	OlbGlAccountPOInvoices.init();
})

var OlbGlAccountPOInvoices = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	var init = function(){
		initjqxWindow();
		initDropDownButton();
		initDropDownList();
		initValidator();
		bindEvent();
	}
	var initjqxWindow = function(){
		  $("#alterpopupWindow").jqxWindow({
		        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
	}
	var initValidator = function(){
		var mapRules = [
		                {input: '#glAccountId', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#invoiceItemTypeId', type: 'validInputNotNull',action : 'change,close'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var customMessageError = function(errorMsg){
		var _newMsg = "";
		if(errorMsg.indexOf('duplicate')){
			_newMsg = msg_custom
			return _newMsg;
		}
		return errorMsg;
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#invoiceItemTypeId',{dropDownHorizontalAlignment: 'right',source: dataITT, displayMember: "description", valueMember: "invoiceItemTypeId",obj : accCm});
	};
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#glAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt  :250,labels : uiLabelMap});
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	          row = { 
	        		glAccountId:$('#glAccountId').val(),
	        		invoiceItemTypeId: $('#invoiceItemTypeId').val(),
	        		organizationPartyId:'${parameters.organizationPartyId}'              
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#glAccountId').jqxDropDownButton('val','');
	    	  $('#invoiceItemTypeId').jqxDropDownList('clearSelection');
	    });
		
	}
	
	
	return {
		init : init,
		customMessageError : customMessageError
	}
	
}())