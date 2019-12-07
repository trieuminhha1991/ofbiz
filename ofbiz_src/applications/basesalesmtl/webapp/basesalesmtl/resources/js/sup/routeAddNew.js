/* TODOCHANGE delete */
$(function(){
	routeSupOlb.init();
})

var routeSupOlb = (function(){
	var validatorVAL;
	
	var routeAdd =  $('#routeAdd');
	var routeScheduleAdd = $('#routeScheduleAdd');
	var window = $("#alterpopupWindow");
	var jqxgrid = $('#jqxgrid');
	var schedule  =  [
	                   {routeScheduleId : 'T2',name : uiLabelMap.CommonMonday},
	                   {routeScheduleId : 'T3',name : uiLabelMap.CommonTuesday},
	                   {routeScheduleId : 'T4',name : uiLabelMap.CommonWednesday},
	                   {routeScheduleId : 'T5',name : uiLabelMap.CommonThursday},
	                   {routeScheduleId : 'T6',name : uiLabelMap.CommonFriday},
	           	 		{routeScheduleId : 'T7',name: uiLabelMap.BSSaturday} 
                   ];
	
	var init = function(){
		initElement();
		initValidator();
		bindEvent();
		
	}
	
	var initJqxWindow = function(){
		window.jqxWindow({
	        width: 390, height : 220,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7          
	    });
	}
	
	var initElement = function(){
		initJqxWindow();
		jOlbUtil.input.create('#routeAdd',{width : '195px',height : '25px'});
		new OlbComboBox('#routeScheduleAdd', schedule, {multiSelect : true,displayMember : 'name',autoDropDownHeight : true,valueMember : 'routeScheduleId',source : schedule,width : '200px',height : '25px'});
	}
	
	var initValidator = function(){
		var mapRules = [
                {input: '#routeAdd', type: 'validInputNotNull',action : 'change,close,blur'},
            	{input: '#routeScheduleAdd', type: 'validInputNotNull',action : 'change,close,blur'},
    		];
		validatorVAL = new OlbValidator($('#formAdd'), mapRules, null, {position : 'bottom'});
	}
	
	var bindEvent = function(){
		$('#alterSave').click(function(){
			
			if(!validatorVAL.validate()){
				return;
			};
			
			var itemsVal = "";
			(function(){
				var items = routeScheduleAdd.jqxComboBox('getSelectedItems');
				if(items && items.length > 0 ){
					for(var i = 0 ; i < items.length ; i ++){
						if(items[i].value){
							itemsVal += items[i].value + ",";
						}
					}
				}
			})();
			
			var row =  {
				routeName : routeAdd.val(),
				infoRoute : itemsVal
			};
			
			jqxgrid.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        jqxgrid.jqxGrid('clearSelection');                        
	        jqxgrid.jqxGrid('selectRow', 0);  
	        window.jqxWindow('close');
		});
	}
	
	return {
		init : init
	}
}())
	