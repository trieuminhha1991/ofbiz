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

<div id="jqxwindowpartyIdTo" class='hide'>
	<div>${uiLabelMap.SelectPartyIdTo}</div>
	<div style="overflow: hidden;">
		<div class='form-window-content'>
			<input type="hidden" id="jqxwindowpartyIdTokey" value=""/>
			<input type="hidden" id="jqxwindowpartyIdTovalue" value=""/>
			<div id="jqxgridpartyidto"></div>
		</div>
		<div class="form-action">
			<button id="cancelPartyTo" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="savePartyTo" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="jqxwindowpartyIdFrom" class='hide'>
	<div>${uiLabelMap.SelectPartyId}</div>
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
	var filterfromto = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme; 
		var initWindow = function(){
			$("#jqxwindowpartyIdTo").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelPartyTo"), modalOpacity: 0.7, width: 1000, maxWidth: 1200, height: 700     
		    });
		    $("#jqxwindowpartyIdFrom").jqxWindow({
		        theme: theme, isModal: true, autoOpen: false, resizable: false, cancelButton: $("#cancelPartyFrom"), modalOpacity: 0.7, width: 1000, maxWidth: 1200, height: 700 
		    });
		    $('#jqxwindowpartyIdFrom').on('open', function (event) {
		    	var offset = $("#jqxgrid").offset();
		   		$("#jqxwindowpartyIdFrom").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
			});
		    $('#jqxWindow').on('open', function (event) {
		    	var offset = $("#jqxgrid").offset();
		   		$("#jqxwindowpartyIdTo").jqxWindow({ position: { x: parseInt(offset.left) + 60, y: parseInt(offset.top) + 60 } });
			});
		};
		var initPartyGrid = function(){
			var datafields = [{ name: 'partyId', type: 'string' },
        		{ name: 'partyTypeId', type: 'string' },
            	{ name: 'firstName', type: 'string' },
            	{ name: 'lastName', type: 'string' },
            	{ name: 'groupName', type: 'string' }];
			<#if popupAgreement?exists && popupAgreement?has_content && popupAgreement == "Y">
				<#assign flag = "Y"/>
			<#else>
				<#assign flag = "N"/>
			</#if>
			
            var columns = [{ text: '<#if flag == "N">${uiLabelMap.accApInvoice_partyId}<#else>${uiLabelMap.PartyPartyId}</#if>', datafield: 'partyId', width: 200, pinned: true},
                   { text: '<#if flag == "N">${uiLabelMap.accApInvoice_partyTypeId}<#else>${uiLabelMap.PartyTypeId}</#if>', datafield: 'partyTypeId',filtertype  : 'checkedlist',
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
					},
				{ text: '<#if flag == "N">${uiLabelMap.accAccountingFromParty}<#else>${uiLabelMap.PartyGroupName}</#if>', datafield: 'groupName', width: 200},
				{ text: '<#if flag == "N">${uiLabelMap.FormFieldTitle_firstName}<#else>${uiLabelMap.PartyFirstName}</#if>', datafield: 'firstName', width: 200, 
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
						var first = rowdata.firstName ? rowdata.firstName : "";
						var last = rowdata.lastName ? rowdata.lastName : "";
						return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
					}
				}
				];
			GridUtils.initGrid({url: "getFromParty", autorowheight: true, filterable: true, width: '100%', height: 400, cache: true, source: {pagesize: 10, cache: true}},datafields,columns, null, $('#jqxgridpartyidfrom'));
			GridUtils.initGrid({url: "getFromParty", autorowheight: true, filterable: true, width: '100%', height: 400, cache: true, source: {pagesize: 10, cache: true}},datafields,columns, null, $('#jqxgridpartyidto'));
		};
		var bindEvent = function(){
			$("#savePartyFrom").click(function () {
				filterfromto.saveFilter($("#jqxwindowpartyIdFrom"), $('#jqxgridpartyidfrom'), $('#' + $('#jqxwindowpartyIdFromkey').val()));
			});
			$("#savePartyTo").click(function () {
				filterfromto.saveFilter($("#jqxwindowpartyIdTo"), $('#jqxgridpartyidto'), $('#' + $('#jqxwindowpartyIdTokey').val()));
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
		filterfromto.init();
	});
</script>