$(function(){
	OlbGlAccountTypePayment.init();
})

var OlbGlAccountTypePayment = (function(){
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
		                {input: '#paymentTypeId', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#glAccountTypeId', type: 'validInputNotNull',action : 'change,close,blur'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	var initDropDownPM = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getPaymentTypeGlAccountTypeNotDedault&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : false,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'paymentTypeId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : uiLabelMap.BACCpaymentTypeId,datafield : 'paymentTypeId',width : '30%'},
			{text : uiLabelMap.description,datafield : 'description'}
		]
		, null, grid,dropdown,'paymentTypeId');
	}
	var initDropDownList = function(){
		accCm.createDropDownList('#glAccountTypeId',{dropDownHorizontalAlignment: 'right',autoDropDownHeight : false,dropDownHeight : 300,source: dataGLAT, displayMember: "description", valueMember: "glAccountTypeId",obj:accCm});
	};
	
	var initDropDownButton = function(){
		 initDropDownPM( $('#paymentTypeId'), $('#jqxgridPaymentType'));
	}
	
	var updateData = function(){
		$('#jqxgridPaymentType').jqxGrid('updatebounddata');
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	           row = {
	        		paymentTypeId: $('#paymentTypeId').val(),
	        		glAccountTypeId: $('#glAccountTypeId').val()
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		   $("#jqxgrid").jqxGrid('clearSelection');                        
  			$("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#glAccountTypeId').jqxDropDownList('clearSelection');
	    	  $('#paymentTypeId').jqxDropDownButton('val','');
	    });
	}
	return {
		init : init,
		updateData :updateData
	}
	
	
}())