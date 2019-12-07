$(function(){
	OlbGlAccountTypeDefault.init();
})

var OlbGlAccountTypeDefault = (function(){
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
		                {input: '#glAccountId', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#glAccountTypeId', type: 'validInputNotNull',action : 'change,close'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var updateData = function(){
		$('#jqxgridGlAccountType').jqxGrid('updatebounddata');
	};
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#glAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt  :250,labels : uiLabelMap});
		initDropDownGlTypeNotDf($('#glAccountTypeId'),$('#jqxgridGlAccountType'));
	}
	
	var initDropDownGlTypeNotDf = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getGLAccountTypeNotDedault&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'glAccountTypeId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : uiLabelMap.BACCglAccountTypeId,datafield : 'glAccountTypeId',width : '40%'},
			{text : uiLabelMap.description,datafield : 'description'}
		]
		, null, grid,dropdown,'glAccountTypeId');
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	        row = { 
	        		glAccountId:$('#glAccountId').val(),
	        		glAccountTypeId:$('#glAccountTypeId').val()           
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#glAccountId').jqxDropDownButton('val','');
	    	  $('#glAccountTypeId').jqxDropDownButton('val','');
	    });
	}
	
	return {
		init : init,
		updateData : updateData
	}
	
}())

