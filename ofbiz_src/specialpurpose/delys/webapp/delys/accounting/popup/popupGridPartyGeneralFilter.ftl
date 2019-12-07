<#if isOrganization?exists && isOrganization?has_content && isOrganization == "true">
<div id="jqxwindoworganizationPartyId" class='hide'>
	<div>${uiLabelMap.SelectPartyIdFrom}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content'>
			<input type="hidden" id="jqxwindoworganizationPartyIdkey" value=""/>
			<input type="hidden" id="jqxwindoworganizationPartyIdvalue" value=""/>
			<div id="jqxgridorganizationPartyId"></div>
		</div>
		<div class="form-action">
			<button id="cancelParty" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveParty" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	
<#else>
<div id="jqxwindowpartyId" class='hide'>
	<div>${uiLabelMap.SelectPartyId}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content' >
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
</#if>
	
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var cntrlIsPressed = false;
	var filter = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme; 
		var initWindow = function(){
		<#if isOrganization?exists && isOrganization?has_content && isOrganization == "true">	
			$("#jqxwindoworganizationPartyId").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelParty"), modalOpacity: 0.7, width: 1000, maxWidth: 1000, height: 800 
		    });
		    $('#jqxWindow').on('open', function (event) {
		    	var offset = $("#jqxgrid").offset();
		   		$("#jqxwindoworganizationPartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
			});
		<#else>
			$("#jqxwindowpartyId").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelParty"), modalOpacity: 0.7, width: 1000, maxWidth: 1000, height: 800     
		    });
		    $('#jqxWindow').on('open', function (event) {
	    		var offset = $("#jqxgrid").offset();
	   			$("#jqxwindowpartyId").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
			});
		
		</#if>	
		};
		var initPartyGrid = function(){
			var datafields = [{ name: 'partyId', type: 'string' },
        		{ name: 'partyTypeId', type: 'string' },
            	{ name: 'firstName', type: 'string' },
            	{ name: 'lastName', type: 'string' },
            	{ name: 'groupName', type: 'string' }];
            	<#if isOrganization?exists && isOrganization?has_content && isOrganization == "true">	
		            	var columns = [
						  { text: '${uiLabelMap.OrganizationParty}', datafield: 'partyId', width:150},
				          { text: '${uiLabelMap.accPeriodPartyTypeId}', datafield: 'partyTypeId', width:200},
				          { text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width:150},
				          { text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:150},
				          { text: '${uiLabelMap.organizationName}', datafield: 'groupName'}
						]
				GridUtils.initGrid({url: "JQGetListPartiesAcctgPreference", autorowheight: true, filterable: true, width: 800, height: 400, cache: true, source: {pagesize: 10, cache: true}},datafields,columns, null, $('#jqxgridorganizationPartyId'));		
					<#else>
						var columns = [{ text: '${uiLabelMap.accApInvoice_partyId}', datafield: 'partyId', width: 200, pinned: true},
						{ text: '${uiLabelMap.accAccountingFromParty}', datafield: 'groupName', width: 200},
						{ text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width: 200, 
							cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
								var first = rowdata.firstName ? rowdata.firstName : "";
								var last = rowdata.lastName ? rowdata.lastName : "";
								return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
							}
						},
						{ text: '${uiLabelMap.FormFieldTitle_lastName}', datafield: 'lastName', width:200},
						{ text: '${uiLabelMap.accApInvoice_partyTypeId}', datafield: 'partyTypeId', filtertype : 'checkedlist',
							cellsrenderer: function(row, columns, value){
								var group = "${uiLabelMap.PartyGroup}";
								var person = "${uiLabelMap.Person}";
								if(value == "PARTY_GROUP"){
									return "<div class='custom-cell-grid'>"+group+"</div>";
								}else if(value == "PERSON"){
									return "<div class='custom-cell-grid'>"+person+"</div>";
								}
								return value;
							},
							createfilterwidget : function(row,column,widget){
								var records;
								if(listPartyType && listPartyType.length > 0){
									var filter = new $.jqx.dataAdapter(listPartyType,{autoBind : true});
									records = filter.records;
									records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
								}else records = [];
								widget.jqxDropDownList({source : records,displayMember : 'description',valueMember : 'partyTypeId'});
							}
						}];		
					GridUtils.initGrid({url: "getFromParty", autorowheight: true, filterable: true, width: '100%', height: 500, cache: true, source: {pagesize: 10, cache: true}},datafields,columns, null, $('#jqxgridpartyid'));	
					</#if>
		};
		var bindEvent = function(){
			$("#saveParty").click(function () {
				<#if isOrganization?exists && isOrganization?has_content && isOrganization == "true">	
					filter.saveFilter($("#jqxwindoworganizationPartyId"), $('#jqxgridorganizationPartyId'), $('#' + $('#jqxwindoworganizationPartyIdkey').val()));
				<#else>
					filter.saveFilter($("#jqxwindowpartyId"), $('#jqxgridpartyid'), $('#' + $('#jqxwindowpartyIdkey').val()));
				</#if>
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