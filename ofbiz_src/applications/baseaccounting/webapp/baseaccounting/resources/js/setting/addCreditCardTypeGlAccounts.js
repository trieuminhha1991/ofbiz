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
		                {input: '#GlAccountId', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#CardType', type: 'validInputNotNull',action : 'change,close,blur'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var updateData = function(){
		$('#jqxgridCardType').jqxGrid('updatebounddata');
	};
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#GlAccountId'),$('#jqxgridGlAccount'),{wgrid : 400,wbt  :250,labels : uiLabelMap});
		initDropDownGlTypeNotDf($('#CardType'),$('#jqxgridCardType'));
	}
	
	var initDropDownGlTypeNotDf = function(dropdown,grid){
		Grid.initDropDownButton({url : 'jqgetListCreditCardTypeNotGlAccount&organizationPartyId=${parameters.organizationPartyId?if_exists}',autoshowloadelement : true,width : 400,filterable : true,source : {pagesize : 5,cache  : false},dropdown : {width : 250, dropDownHorizontalAlignment: true}},
		[
			{name : 'cardType',type : 'string'},
			{name : 'description',type : 'string'}
		], 
		[
			{text : uiLabelMap.CreditCardType,datafield : 'cardType',width : '50%'},
			{text : uiLabelMap.description,datafield : 'description'}
		]
		, null, grid,dropdown,'cardType');
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
	           row = {
	        		cardType:$('#CardType').val(),
    				glAccountId:$('#GlAccountId').val()        
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		   $("#jqxgrid").jqxGrid('clearSelection');                        
  			$("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#GlAccountId').jqxDropDownButton('val','');
	    	  $('#CardType').jqxDropDownButton('val','');
	    	  $('#jqxgridGlAccount').jqxGrid('clearSelection');
	    	  $('#jqxgridCardType').jqxGrid('clearSelection');
	    });
	}
	
	return {
		init : init,
		updateData : updateData
	}
	
	
}())

