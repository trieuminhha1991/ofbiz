$(function(){
	OlbFinAccountTypeGlAccount.init();
})
var OlbFinAccountTypeGlAccount = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	
	var init = function(){
		initjqxWindow();
		initDropDownButton();
		initValidator();
		bindEvent();
	}
	var initValidator = function(){
		var mapRules = [
		                {input: '#accountTypeId', type: 'validInputNotNull',action : 'change,close'},
		                {input: '#GlAccountId', type: 'validInputNotNull',action : 'change,close'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	var bindEvent = function(){
		$("#save").click(function () {
			if(!validatorVAL.validate()){return;}
				var row;
		        row = {
		        		finAccountTypeId: $('#accountTypeId').val(),
		        		glAccountId: $('#GlAccountId').val()
		        	  };
		        
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		       $("#alterpopupWindow").jqxWindow('close');
		});
		
		$("#alterpopupWindow").bind('close',function(){
			$('#accountTypeId').jqxDropDownButton('val','');
			$('#GlAccountId').jqxDropDownButton('val','');
		});
	}
	
	var initDropDownGlTypeNotDf = function(dropdown,grid){
		Grid.initDropDownButton({url : 'getFinAccountTypeNotGlAccount&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'finAccountTypeId',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : uiLabelMap.finAccountTypeId,datafield : 'finAccountTypeId',width : '40%'},
			{text : uiLabelMap.FinAccountTypeGlAccount,datafield : 'description'}
		]
		, null, grid,dropdown,'finAccountTypeId');
	}
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#GlAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt : 250,labels : uiLabelMap});
		initDropDownGlTypeNotDf($('#accountTypeId'),$('#jqxgridFinGlAccount'));
	}
	var initjqxWindow = function(){
		$("#alterpopupWindow").jqxWindow({
	        width: 500,height : 180, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
	    });
	}
	var updateData = function(){
		$('#jqxgridFinGlAccount').jqxGrid('updatebounddata');
	};
	return {
		init : init,
		updateData : updateData
		
	}
	
	
}())