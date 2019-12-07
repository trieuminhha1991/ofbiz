$(function(){
	OlbGlAccountNrPayment.init();
})

var OlbGlAccountNrPayment = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	var init = function(){
		initjqxWindow();
		initDropDownButton();
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
		                {input: '#paymentMethodTypeId', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#glAccountId', type: 'validInputNotNull',action : 'change,close'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var initDropDownPM = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getPaymentMethodTypeNotDedault&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'paymentMethodTypeId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text :uiLabelMap.paymentMethodTypeId,datafield : 'paymentMethodTypeId',width : '50%'},
			{text : uiLabelMap.description,datafield : 'description'}
		]
		, null, grid,dropdown,'paymentMethodTypeId');
	}
	var updateData = function(){
		$('#jqxgridPaymentType').jqxGrid('updatebounddata');
	}
	var initDropDownButton = function(){
		initDropDownPM($('#paymentMethodTypeId'),$('#jqxgridPaymentType'));
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#glAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt  :250,labels : uiLabelMap});
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	          row = { 
	        		paymentMethodTypeId: $('#paymentMethodTypeId').val(),
					glAccountId: $('#glAccountId').val()            
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#glAccountId').jqxDropDownButton('val','');
	    	  $('#paymentMethodTypeId').jqxDropDownButton('val','');
	    });
		
	}
	return {
		init : init,
		updateData : updateData
	}
	
}())