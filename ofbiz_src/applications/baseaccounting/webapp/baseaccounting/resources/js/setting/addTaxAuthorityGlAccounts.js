$(function(){
	OlbTaxAuthority.init();
})

var OlbTaxAuthority = (function(){
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
		                {input: '#taxAuthPartyGeoIdAdd', type: 'validInputNotNull',action : 'change,close,blur'},
		                {input: '#glAccountIdAdd', type: 'validInputNotNull',action : 'change,close,blur'}
		                ]
		validatorVAL = new OlbValidator($('#formAdd'),mapRules,null,{position : 'bottom'});
	}
	
	var initDropDownList = function(){
		accCm.createDropDownList('#taxAuthPartyGeoIdAdd',{dropDownHorizontalAlignment: 'right',source: tahArray, displayMember: "description", valueMember: "taxAuthPartyId",obj : accCm});
	};
	
	var initDropDownButton = function(){
		accCm.DropDownUtils.initDropDownGlAccountOrg(null,$('#glAccountIdAdd'),$('#jqxgridGlAccount'),{wgrid : 400,wbt  :250,labels : uiLabelMap});
	}
	
	var bindEvent = function(){
		// update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if(!$('#formAdd').jqxValidator('validate')){return;}
	    	var row;
	    	var taxAuthPartyGeoIdAdd = $('#taxAuthPartyGeoIdAdd').val();
			var temp = taxAuthPartyGeoIdAdd.split(";");
	    	var taxAuthPartyId = temp[0];
	    	var taxAuthGeoId = temp[1];
	        row = { 
	        		taxAuthPartyId:taxAuthPartyId,
	        		taxAuthGeoId:taxAuthGeoId,
	        		glAccountId:$('#glAccountIdAdd').jqxDropDownButton('val')           
	        	  };
	      	rowdataAdd = row;
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		   $("#jqxgrid").jqxGrid('clearSelection');                        
  			$("#jqxgrid").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	    });
	    
	    $('#alterpopupWindow').on('close',function(){
	    	 $('#taxAuthPartyGeoIdAdd').jqxDropDownList('clearSelection');
	    	  $('#glAccountIdAdd').jqxDropDownButton('val','');
	    });
	    
	    //listener tahArray change and update dropDownList source
		$('#jqxgrid').on('myEvent',function(handlerObj,param){
			if(param == 'delete'){
				
			}else {
				var obj = JSON.parse(param);
				if(rowdataAdd && obj){
					if(obj.results.responseMessage == 'success'){
						$.each(tahArray,function(index){
							var arrTmp = tahArray[index].taxAuthPartyId.split(";");
							if(arrTmp[0] == rowdataAdd['taxAuthPartyId']){
								tahArray.splice(index,1);
								$('#taxAuthPartyGeoIdAdd').jqxDropDownList({ source: tahArray});
								return false;
							}
						})
					}
				}
			}
		})
	}
	return {
		init : init
	}
	
	
}())