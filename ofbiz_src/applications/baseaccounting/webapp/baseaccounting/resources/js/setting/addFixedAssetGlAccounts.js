$(function(){
	OlbFixedAssetType.init();
})

var OlbFixedAssetType = (function(){
	var validatorVAL;
	var accCm = new accCommon();
	
	var init = function(){
		initjqxWindow();
		initDropDownList();
		initValidator();
		bindEvent();
	}
	
	var initjqxWindow = function(){
	 $("#alterpopupWindow").jqxWindow({
	        width: 500,height : 400, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
	    });
	}
	
	var initValidator = function(){
		var mapRules = [
            {input: '#assetGlAccountId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#depGlAccountId', type: 'validObjectNotNull', objType: 'dropDownList'},
            {input: '#profitGlAccountId', type: 'validObjectNotNull', objType: 'dropDownList'},
        ];
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#assetGlAccountId',{source: dataLAGLA, displayMember: "description", valueMember: "glAccountId",obj : accCm});
		accCm.createDropDownList('#accDepGlAccountId',{source: dataLADGLA, displayMember: "description", valueMember: "glAccountId",obj : accCm});
		accCm.createDropDownList('#depGlAccountId',{source: dataLDGLA, displayMember: "description", valueMember: "glAccountId",obj : accCm});
		accCm.createDropDownList('#profitGlAccountId',{source: dataLPGLA, displayMember: "description", valueMember: "glAccountId",obj : accCm});
		accCm.createDropDownList('#lossGlAccountId',{source: dataLLGLA, displayMember: "description", valueMember: "glAccountId",obj : accCm});
		accCm.createDropDownList('#fixedAssetTypeId',{source: dataLFLAT, displayMember: "description", valueMember: "fixedAssetTypeId",obj : accCm});
		accCm.createDropDownList('#fixedAssetId',{source: dataLFA, displayMember: "description", valueMember: "fixedAssetId",obj : accCm});
	};
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!validatorVAL.validate()){return;}
	    	var row;
			var fixedAssetId = $('#fixedAssetId').val(); 
			if(fixedAssetId == ""){
				fixedAssetId = "_NA_";
			}
			var fixedAssetTypeId = $('#fixedAssetTypeId').val();
			if(fixedAssetTypeId == ""){
				fixedAssetTypeId = "_NA_";
			}
	        row = {
	        		assetGlAccountId: $('#assetGlAccountId').val(),
	        		accDepGlAccountId: $('#accDepGlAccountId').val(),
	        		depGlAccountId: $('#depGlAccountId').val(),
	        		profitGlAccountId: $('#profitGlAccountId').val(),
	        		lossGlAccountId: $('#lossGlAccountId').val(),
	        		fixedAssetTypeId: fixedAssetTypeId,
	        		fixedAssetId: fixedAssetId        		        		
	        	  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		   $("#jqxgrid").jqxGrid('clearSelection');                        
  			$("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#assetGlAccountId').jqxDropDownList('clearSelection');
	    	  $('#accDepGlAccountId').jqxDropDownList('clearSelection');
	    	  $('#depGlAccountId').jqxDropDownList('clearSelection');
	    	  $('#profitGlAccountId').jqxDropDownList('clearSelection');
	    	  $('#lossGlAccountId').jqxDropDownList('clearSelection');
	    	  $('#fixedAssetTypeId').jqxDropDownList('clearSelection');
	    	  $('#fixedAssetId').jqxDropDownList('clearSelection');
	    });
	}
	return {
		init : init,
		accCm : accCm
	}
	
	
}())