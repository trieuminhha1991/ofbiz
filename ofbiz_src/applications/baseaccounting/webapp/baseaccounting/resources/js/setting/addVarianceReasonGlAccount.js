$(function(){
	OlbVarianceReason.init();
})

var OlbVarianceReason = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	var rowAdd = {};
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
		                {input: '#varianceReasonIdPop', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#glAccountIdPop', type: 'validInputNotNull',action : 'change,close'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	var initDropDownPM = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getPaymentTypeGlAccountTypeNotDedault&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'paymentTypeId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : '${uiLabelMap.BACCpaymentTypeId}',datafield : 'paymentTypeId',width : '30%'},
			{text : '${uiLabelMap.BACCCommonDescription}',datafield : 'description'}
		]
		, null, grid,dropdown,'paymentTypeId');
	}
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#glAccountIdPop'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 250,labels : uiLabelMap});
		initDropDownPM($('#varianceReasonIdPop'),$('#jqxgridReason'));
	}
	
	var initDropDownPM = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getVarianceReasonNotGlAccounts&organizationPartyId=${parameters.organizationPartyId}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'varianceReasonId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : uiLabelMap.varianceReasonId,datafield : 'varianceReasonId',width : '30%'},
			{text : uiLabelMap.description,datafield : 'description'}
		]
		, null, grid,dropdown,'varianceReasonId');
	}
	var updateData = function(){
		$('#jqxgridReason').jqxGrid('updatebounddata');
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	           row = {
	        		varianceReasonId:$('#varianceReasonIdPop').val(),
    				glAccountId:$('#glAccountIdPop').val()        
	        	  };
	        rowAdd = row;
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		   $("#jqxgrid").jqxGrid('clearSelection');                        
  			$("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#glAccountIdPop').jqxDropDownButton('val','');
	    	  $('#varianceReasonIdPop').jqxDropDownButton('val','');
	    	  $('#jqxgridReason').jqxGrid('clearSelection');
	    	   $('#jqxgridGlAccount').jqxGrid('clearSelection');
	    });
	}
	return {
		init : init,
		updateData : updateData
	}
	
	
}())