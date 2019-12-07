<#assign listPartyType = delegator.findByAnd("PartyType",null,null,false) !>
<script>
	var listPartyType = [
           <#list listPartyType as type>    
	             {
	            	 'partyTypeId' : '${type.partyTypeId}',
	            	 'description' : '${StringUtil.wrapString(type.get('description',locale))}'
	             },        
	       </#list>      
         ];

</script>

<div id="jqxwindowpartyId" class='hide'>
	<div>${uiLabelMap.BACCSelectParty}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content'>
			<input type="hidden" id="jqxwindowpartyIdkey" value=""/>
			<input type="hidden" id="jqxwindowpartyIdvalue" value=""/>
			<div id="jqxgridpartyid"></div>
		</div>
		<div class="form-action">
			<button id="cancelParty" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveParty" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="jqxwindowpartyIdFrom" class='hide'>
	<div>${uiLabelMap.BACCSelectParty}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content'>
			<input type="hidden" id="jqxwindowpartyIdFromkey" value=""/>
			<input type="hidden" id="jqxwindowpartyIdFromvalue" value=""/>
			<div id="jqxgridpartyidfrom"></div>	 				
		</div>
		<div class="form-action">
			<button id="cancelPartyFrom" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="savePartyFrom" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var cntrlIsPressed = false;
	var filter = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme; 
		var initWindow = function(){
			$("#jqxwindowpartyId").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelParty"), modalOpacity: 0.7, width: '80%', maxWidth: '90%', height: 500     
		    });
		    $("#jqxwindowpartyIdFrom").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelPartyFrom"), modalOpacity: 0.7, width: '80%', maxWidth: '90%', height: 500 
		    });
		};
		var initPartyGrid = function(){
			var datafields = [
                { name: 'partyId', type: 'string' },
        		{ name: 'partyTypeId', type: 'string' },
            	{ name: 'firstName', type: 'string' },
            	{ name: 'lastName', type: 'string' },
            	{ name: 'groupName', type: 'string' }
        	];
			
            var columns = [
				{ text: '${uiLabelMap.BACCPartyId}', datafield: 'partyId', width: 100},
				{ text: '${uiLabelMap.BACCFirstName}', datafield: 'firstName', width: 200},
				{ text: '${uiLabelMap.BACCLastName}', datafield: 'lastName', width: 200},
				{ text: '${uiLabelMap.BACCGroupName}', datafield: 'groupName'},
			];
            
            var config = {
        	   		width: '100%', 
        	   		virtualmode: true,
        	   		showfilterrow: true,
        	   		showtoolbar: false,
        	   		selectionmode: 'singlerow',
        	   		pageable: true,
        	   		sortable: false,
        	        filterable: true,
        	        editable: false,
        	        source: {url: "JqxGetParties"}
    	   	};
			Grid.initGrid(config, datafields, columns, null, $('#jqxgridpartyid'));
			
			 var configFrom = {
        	   		width: '100%', 
        	   		virtualmode: true,
        	   		showfilterrow: true,
        	   		showtoolbar: false,
        	   		selectionmode: 'singlerow',
        	   		pageable: true,
        	   		sortable: false,
        	        filterable: true,
        	        editable: false,
        	        source: {url: "JqxGetParties"}
    	   	};
			Grid.initGrid(configFrom, datafields, columns, null, $('#jqxgridpartyidfrom'));
		};
		var bindEvent = function(){
			$("#savePartyFrom").click(function () {
				filter.saveFilter($("#jqxwindowpartyIdFrom"), $('#jqxgridpartyidfrom'), $('#' + $('#jqxwindowpartyIdFromkey').val()));
			});
			$("#saveParty").click(function () {
				filter.saveFilter($("#jqxwindowpartyId"), $('#jqxgridpartyid'), $('#' + $('#jqxwindowpartyIdkey').val()));
			});
			$(document).keydown(function(event){
			    if(event.ctrlKey)
			        cntrlIsPressed = true;
			});
			
			$(document).keyup(function(event){
				if(event.which=='17')
			    	cntrlIsPressed = false;
			});
		};
		var saveFilter = function(window, grid, key){
			var tIndex = grid.jqxGrid('selectedrowindex');
			var data = grid.jqxGrid('getrowdata', tIndex);
			key.val(data.partyId);
			window.jqxWindow('close');
			var e = jQuery.Event("change");
			e.which = 50;
			key.trigger(e);	
		};
		return {
			init: function(){
				initWindow();
				initPartyGrid();
				bindEvent();
			},
			saveFilter: saveFilter
		};
	}());
	$(document).ready(function(){
		filter.init();
	});
</script>