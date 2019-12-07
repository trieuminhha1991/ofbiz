<#if fieldFilter?exists && fieldFilter?has_content>
<div id="jqxwindow${fieldFilter}" style="display:none;">
	<div>${FilterName?if_exists?default('${uiLabelMap.DACustomer}')}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content' style="height: 410px;">
			<input type="hidden" id="jqxwindow${fieldFilter}key" value=""/>
			<input type="hidden" id="jqxwindow${fieldFilter}value" value=""/>
			<div id="jqxgrid${fieldFilter}"></div>
		</div>
		<div class="form-action">
			<button id="cancelParty" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveParty" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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
			$("#jqxwindow${fieldFilter}").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelParty"), modalOpacity: 0.7, width: 820, maxWidth: 1000, height: 513     
		    });
		};
		var initGrid = function(){
			var datafields = [{ name: 'partyId', type: 'string' },
        		{ name: 'partyTypeId', type: 'string' },
            	{ name: 'firstName', type: 'string' },
            	{ name: 'lastName', type: 'string' },
            	{ name: 'groupName', type: 'string' }];
            	
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
				{ text: '${uiLabelMap.accApInvoice_partyTypeId}', datafield: 'partyTypeId', width: 200, 
					cellsrenderer: function(row, columns, value){
						var group = "${uiLabelMap.PartyGroup}";
						var person = "${uiLabelMap.Person}";
						if(value == "PARTY_GROUP"){
							return "<div class='custom-cell-grid'>"+group+"</div>";
						}else if(value == "PERSON"){
							return "<div class='custom-cell-grid'>"+person+"</div>";
						}
						return value;
					}
				}];		
			GridUtils.initGrid({url: "${url?default('getFromParty')}", autorowheight: true, filterable: true, width: 800, height: 400, cache: true, source: {pagesize: 10, cache: true}},datafields,columns, null, $('#jqxgrid${fieldFilter}'));	
		};
		var bindEvent = function(){
			$("#saveParty").click(function () {
				filter.saveFilter($("#jqxwindow${fieldFilter}"), $('#jqxgrid${fieldFilter}'), $('#' + $('#jqxwindow${fieldFilter}key').val()));
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
				initGrid();
				bindEvent();
			},
			saveFilter: saveFilter
		};
	}());
	$(document).ready(function(){
		filter.init();
	});
</script>

</#if>