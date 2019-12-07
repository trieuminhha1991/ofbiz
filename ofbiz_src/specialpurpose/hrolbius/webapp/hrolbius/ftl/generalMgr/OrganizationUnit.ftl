<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	//Preparre roleType
	<#assign roleTypeList = delegator.findByAnd("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "DEPARTMENT"), null, false)>
	var roleTypeData = [
		<#list roleTypeList as roleType>
			{roleTypeId: "${roleType.roleTypeId}", description: "${StringUtil.wrapString(roleType.description)}"}
			<#if roleType_has_next>
			,
			</#if>
		</#list>
	];
	 
	//Prepare Country Geo
	<#assign countrylList = delegator.findByAnd("Geo", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "COUNTRY"), null, false)>
	var countryData = new Array();
	<#list countrylList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['description'] = "${description}";
		countryData[${item_index}] = row;
	</#list>
	
	//Prepare Province/City Geo
	<#assign provincelList = delegator.findList("GeoAssocAndGeoToDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["CITY", "PROVINCE"]), null, null, null, false)>
	var provinceData = new Array();
	<#list provincelList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		provinceData[${item_index}] = row;
	</#list>
	
	//Prepare District Geo
	<#assign districtlList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "DISTRICT"), null, false)>
	var districtData = new Array();
	<#list districtlList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		districtData[${item_index}] = row;
	</#list>
	
	//Prepare Ward Geo
	<#assign wardList = delegator.findByAnd("GeoAssocAndGeoToDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("geoTypeId", "WARD"), null, false)>
	var wardData = new Array();
	<#list wardList as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.geoName?if_exists) />
		row['geoId'] = '${item.geoId?if_exists}';
		row['geoIdFrom'] = '${item.geoIdFrom}';
		row['description'] = "${description}";
		wardData[${item_index}] = row;
	</#list>
	
	$(document).ready(function () {
		$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});		
		initTreeDropDownBtn();
		/* ======================= jqxWindow defind ========================================*/
		$("#partyGroupDetailWindow").jqxWindow({showCollapseButton: false, maxHeight: 480, maxWidth: 640, minHeight: 480, minWidth: 640, height: 480, width: 640,
			theme: 'olbius', isModal: true, autoOpen: false, 
            initContent: function () {            	
            	initPartyGroupDetail();
            }
		});
		
		$("#partyContactMechWindow").jqxWindow({
			showCollapseButton: false, maxHeight: 300, maxWidth: 550, minHeight: 300, minWidth: 500, height: 300, width: 550,
			theme: 'olbius', isModal: true, autoOpen: false,
			initContent: function () {
				initContactMechContent();
			}
		});
		/* ======================= ./end jqxWindow defind ========================================*/
		
		var partyGroupsSource = {
				dataType: "json",
				dataFields: [
                   { name: 'partyId', type: 'string' },
                   { name: 'partyIdFrom', type: 'string' },
                   { name: 'partyName', type: 'string' },
                   { name: 'postalAddress', type: 'string'},
                   { name: 'contactMechId', type: 'string'},
                   { name: 'totalEmployee', type: 'number' },                   
                   { name: 'expanded',type: 'bool'},
               ],
               hierarchy:
               {
                   keyDataField: { name: 'partyId' },
                   parentDataField: { name: 'partyIdFrom' }
               },
               id: 'partyId',
               url: "getOrganizationUnit",
               root: 'listReturn'
		};
		var dataAdapter = new $.jqx.dataAdapter(partyGroupsSource);
		var columns = [
			{text: '${uiLabelMap.OrgUnitName}', datafield: 'partyName', width: 400,},			
			{text: '${uiLabelMap.OrgUnitId}', datafield: 'partyId', width: 180},
			{text: '${uiLabelMap.NumEmployees}', datafield: 'totalEmployee', width: 120, cellsalign: 'right'},
			{text: '${uiLabelMap.CommonAddress}', datafield: 'postalAddress'},
			{datafield: 'contactMechId', hidden: true}
		];
		$("#treePartyGroupGrid").jqxTreeGrid({
			width: "100%",
			icons: true,
			source: dataAdapter,
            sortable: true,
            columns: columns,
            theme: 'olbius',
            columnsResize: true,
            showToolbar: false,
            renderToolbar: function(toolBar){
            	var container = $("<div id='toolbarcontainer' class='widget-header'><h4>${uiLabelMap.OrganizationUnit}</h4></div>");
            	toolBar.append(container);
            }
		});

	
		$('#treePartyGroupGrid').on('rowDoubleClick', function(event){
			var args = event.args;
	        var row = args.row;
	        openJqxWindow($("#partyGroupDetailWindow"));
	        setJqxWindowContent(row);
		});
		
		$('#jqxTreeOrgUnit').on('select', function(event){			
			var id = event.args.element.id;
		    var item = $('#jqxTreeOrgUnit').jqxTree('getItem', event.args.element);
	    	setDropdownContent(event.args.element, $('#jqxTreeOrgUnit'), $("#belongOrgUnitButton"));
	     });
		
		
		/*==================================button event defind====================================*/
		$("#contactMechCancel").click(function(){
			$("#partyContactMechWindow").jqxWindow('close');
		});
		$("#contactMechSave").click(function(){
			$(this).attr("disabled", "disabled");
			$("#partyContactMechWindow").jqxWindow({disabled: true});
			editPartyContactMech();
		});
		$("#partyGroupDetailClose").click(function(){
			$("#partyGroupDetailWindow").jqxWindow('close');
		});
		$("#partyGroupSave").click(function(){
			$(this).attr("disabled", "disabled");
			$("#partyGroupDetailWindow").jqxWindow({disabled: true});
			updatePartyGroup();
		});
		$("#addrowbutton").click(function(){
			addNewPartyGroup();
		});
		/*==================================./end button event defind==============================*/

	});
	
	function addNewPartyGroup(){
		openJqxWindow($("#newPartyGroupWindow"));
	}
	
	function updatePartyGroup(){
		var data = {};
		data["partyId"] = $("#orgUnitId").val();
		data["groupName"] = $("#orgUnitName").val();
		data["partyIdFromNew"] = $("#jqxTreeOrgUnit").jqxTree('getSelectedItem').value;
		data["comments"] = $("#comments").val();
		if($("#partyIdFromOld").val() != -1){
			data["partyIdFromOld"] = $("#partyIdFromOld").val();			
		}
		$.ajax({
			url: "updatePartyOrgInfo",
			type: 'POST',
			data: data,
			success: function(data){
				if(data.responseMessage == "success"){
					$("#treePartyGroupGrid").jqxTreeGrid('updateBoundData');			
				}
			},
			complete: function(){
				$("#partyGroupSave").removeAttr("disabled");
				$("#partyGroupDetailWindow").jqxWindow({disabled: false});
				$("#partyGroupDetailWindow").jqxWindow('close');
			}
		});
	}
	
	function openJqxWindow(jqxWindowDiv){
		var wtmp = window;
		var tmpwidth = jqxWindowDiv.jqxWindow('width');
		jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
		jqxWindowDiv.jqxWindow('open');
	}
	
	function setDropdownContent(element, treeEle, dropdownBtnEle){
		 var item = treeEle.jqxTree('getItem', element);
		 var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		 dropdownBtnEle.jqxDropDownButton('setContent', dropDownContent);
	}
	
	function setJqxWindowContent(row){		
		$("#orgUnitId").val(row.partyId);
        $("#orgUnitName").val(row.partyName);
        var partyIdFrom = row.partyIdFrom;
        $("#partyIdFromOld").val(partyIdFrom);
        if(partyIdFrom != -1){
        	$("#belongOrgUnitButton").jqxDropDownButton({disabled:false});
        	$('#jqxTreeOrgUnit').jqxTree({disabled:false}); 
        	$('#jqxTreeOrgUnit').jqxTree('selectItem', $("#" + partyIdFrom + "_partyGroupId")[0]);
        }else{
        	$("#belongOrgUnitButton").jqxDropDownButton({disabled:true});
        	$('#jqxTreeOrgUnit').jqxTree({disabled:true});
        	$('#jqxTreeOrgUnit').jqxTree('selectItem', $("#" + row.partyId + "_partyGroupId")[0]);
        }
        setOrgPostalAddressInfo($("#orgAddress"), row.contactMechId, row.partyId, row.postalAddress);
	}
	
	function setOrgPostalAddressInfo(divEle, contactMechId, partyId, postalAddressDes){		
		divEle.empty();
        if(contactMechId){
        	divEle.append('<a href="javascript:void(0);" onclick="updatePartyContactMech(\'' + contactMechId + '\', \'' + partyId + '\')" title="${uiLabelMap.ClickToEdit}">' + postalAddressDes + '</a>');        	
        }else{
        	var buttonEdit = $('<button class="jqx-rc-all-olbius jqx-button jqx-button-olbius ' 
	 						   + 'jqx-widget jqx-widget-olbius jqx-fill-state-pressed '
	 						   +  'jqx-fill-state-pressed-olbius" title="${uiLabelMap.CommonEdit}" onclick="createPartyContactMech(\'' + partyId + '\')" style="cursor: pointer;">'
	 						   +  '<i class="icon-edit blue"></i></button>');
        	divEle.append("<span>${uiLabelMap.HRCommonNotSetting}<span>");
        	divEle.append(buttonEdit);
        }
	}
	
	function updatePartyContactMech(contactMechId, partyId){
		$('#partyContactMechWindow').jqxWindow('setTitle', '${uiLabelMap.UpdateAddress}');
		$("#partyContactMechWindow").jqxWindow({disabled: true});
		$.ajax({
			url: 'getPostalAddressGeoDetail',
			data: {contactMechId: contactMechId},
			type: 'POST',
			success: function(data){
				if(data.responseMessage == "success"){
					if(data.address1){
						$("#prAddress").val(data.address1);
					}
					if(data.countryGeoId){
						$("#prCountry").jqxDropDownList('val', data.countryGeoId);	
					}
					if(data.stateProvinceGeoId){
						$("#prProvince").jqxDropDownList('val', data.stateProvinceGeoId);
					}	
					if(data.districtGeoId){
						$("#prDistrict").jqxDropDownList('val', data.districtGeoId);
					}
					if(data.wardGeoId){
						$("#prWard").jqxDropDownList('val', data.wardGeoId);
					}
				}
			},
			complete: function(){
				$("#partyContactMechWindow").jqxWindow({disabled: false});		
			}
		});
		$("#partyId").val(partyId);
		$("#contactMechId").val(contactMechId);
		openJqxWindow($("#partyContactMechWindow"));
	}		
	
	function createPartyContactMech(partyId){
		$('#partyContactMechWindow').jqxWindow('setTitle', '${uiLabelMap.DAAddNewAddress}');
		$("#partyId").val(partyId);
		openJqxWindow($("#partyContactMechWindow"));
	}
	
	function editPartyContactMech(){
		var partyId = $("#partyId").val();
		var contactMechId = $("#contactMechId").val();
		var url;
		var data = {};
		data["partyId"] = partyId;
		if(contactMechId){
			data["contactMechId"] = contactMechId;
		}
		data["address1"] = $("#prAddress").val();
		data["countryGeoId"] = $("#prCountry").jqxDropDownList('val');
		data["stateProvinceGeoId"] = $("#prProvince").jqxDropDownList('val');
		data["districtGeoId"] = $("#prDistrict").jqxDropDownList('val');
		data["wardGeoId"] = $("#prWard").jqxDropDownList('val');
		$.ajax({
			url: "editOrgPostalAddress",
			type: 'POST',
			data: data,
			success: function(data){
				if(data.responseMessage == "success"){
					setOrgPostalAddressInfo($("#orgAddress"), data.contactMechId, partyId, data.postAddressDesc);
				}
			},
			complete: function(){
				$("#contactMechSave").removeAttr("disabled");
				$("#partyContactMechWindow").jqxWindow({disabled: false});
				$("#partyContactMechWindow").jqxWindow('close');
			}
		});
	}
	
	function initPartyGroupDetail(){
		$("#orgUnitId").jqxInput({height: 24, width: 195, theme: 'olbius', disabled: true});  
    	$("#orgUnitName").jqxInput({height: 24, width: 195, theme: 'olbius'});
    	$('#comments').jqxEditor({            
            width: '100%',
            theme: 'olbiuseditor',
            tools: 'datetime | clear | backcolor | font | bold italic underline',
            height: 200,
        });
    	$('#comments').val("");
	}
	
	function initTreeDropDownBtn(){
		var treePartyGroupArr = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}_partyGroupId";
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}_partyGroupId";
			row["value"] = "${tree.idValueEntity}";
			row["icon"] = "${tree.iconUrl?if_exists}";
			treePartyGroupArr[${tree_index}] = row;
		</#list>  
		var sourceTreePartyGroup =
		{
		    datatype: "json",
		    datafields: [
		        { name: 'id'},
		        { name: 'parentId'},
		        { name: 'text'} ,
		        { name: 'value'}	        
		    ],
		    id: 'id',
		    localdata: treePartyGroupArr
		};
		 
		var dataAdapterPartyTreeGroup = new $.jqx.dataAdapter(sourceTreePartyGroup, {
			beforeLoadComplete: function (records) {
				for (var i = 0; i < records.length; i++) {
					if(treePartyGroupArr[i].icon){
						records[i].icon = treePartyGroupArr[i].icon;	
					}
	            }
	            return records;
			}
		});
		dataAdapterPartyTreeGroup.dataBind();
		 
		var recordPartyGroupTree = dataAdapterPartyTreeGroup.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		$("#belongOrgUnitButton").jqxDropDownButton({ width: '99%', height: 25, theme: 'olbius'});
    	$('#jqxTreeOrgUnit').jqxTree({ source: recordPartyGroupTree,width: '100%', height: 150, theme: 'olbius'});
    	<#if expandedList?has_content>
    	 	<#list expandedList as expandId>
    	 		$('#jqxTreeOrgUnit').jqxTree('expandItem', $("#${expandId}_partyGroupId")[0]);
    	 	</#list>
    	 </#if>
	}
	
	function initContactMechContent(){
		$("#prAddress").jqxInput({theme: 'olbius', height: 23, width: '97%'});
		$("#prCountry").jqxDropDownList({source: countryData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
		$('#prCountry').on('change', function (event){    
		    var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	
		    	//Create Province
		    	var prProvinceData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < provinceData.length; i++){
		    		if(value == provinceData[i].geoIdFrom){
		    			prProvinceData[index] = provinceData[i];
		    			index++;
		    		}
		    	}
		    	$("#prProvince").jqxDropDownList({source: prProvinceData});
		    	$("#prProvince").jqxDropDownList({selectedIndex: 0}); 
		    	if(prProvinceData.length < 8){
		    		$("#prProvince").jqxDropDownList({autoDropDownHeight: true});
		    	}else{
		    		$("#prProvince").jqxDropDownList({autoDropDownHeight: false});
		    	}
		    	//Create district
		    	var prDistrictData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < districtData.length; i++){
		    		if(prProvinceData[0] && prProvinceData[0].geoId == districtData[i].geoIdFrom){
		    			prDistrictData[index] = districtData[i];
		    			index++;
		    		}
		    	}
		    	$("#prDistrict").jqxDropDownList({source: prDistrictData});
		    	$("#prDistrict").jqxDropDownList({selectedIndex: 0});
		    	if(prDistrictData.length < 8){
		    		$("#prDistrict").jqxDropDownList({autoDropDownHeight: true});
		    	}else{
		    		$("#prDistrict").jqxDropDownList({autoDropDownHeight: false});
		    	}
		    	//Create ward
		    	var prWardData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < wardData.length; i++){
		    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
		    			prWardData[index] = wardData[i];
		    			index++;
		    		}
		    	}
		    	$("#prWard").jqxDropDownList({source: prWardData});
		    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	if(prWardData.length < 8){
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: true});
		    	}else{
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: false});
		    	}
	    	} 
		});
		
		var prProvinceData = new Array();
		$("#prProvince").jqxDropDownList({source: prProvinceData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
		$('#prProvince').on('change', function (event){    
		    var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	
		    	//Create district
		    	var prDistrictData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < districtData.length; i++){
		    		if(value == districtData[i].geoIdFrom){
		    			prDistrictData[index] = districtData[i];
		    			index++;
		    		}
		    	}
		    	$("#prDistrict").jqxDropDownList({source: prDistrictData});
		    	$("#prDistrict").jqxDropDownList({selectedIndex: 0});
		    	if(prDistrictData.length < 8){
		    		$("#prDistrict").jqxDropDownList({autoDropDownHeight: true});
		    	}
		    	//Create ward
		    	var prWardData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < wardData.length; i++){
		    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
		    			prWardData[index] = wardData[i];
		    			index++;
		    		}
		    	}
		    	$("#prWard").jqxDropDownList({source: prWardData});
		    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	if(prWardData.length < 8){
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: true});
		    	}else{
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: false});
		    	}
	    	} 
		});
		
		var prDistrictData = new Array();
		$("#prDistrict").jqxDropDownList({source: prDistrictData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
		$('#prDistrict').on('change', function (event){    
		    var args = event.args;
		    if (args) {
		    	var item = args.item;
		    	var value = item.value;
		    	
		    	//Create ward
		    	var prWardData = new Array();
		    	var index = 0;
		    	for(var i = 0; i < wardData.length; i++){
		    		if(value == wardData[i].geoIdFrom){
		    			prWardData[index] = wardData[i];
		    			index++;
		    		}
		    	}
		    	$("#prWard").jqxDropDownList({source: prWardData});
		    	$("#prWard").jqxDropDownList({selectedIndex: 0});
		    	if(prWardData.length < 8){
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: true});
		    	}else{
		    		$("#prWard").jqxDropDownList({autoDropDownHeight: false});
		    	}
	    	} 
		});
		//Create prWard
		var prWardData = new Array();		
		$("#prWard").jqxDropDownList({source: prWardData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
		<#if countryGeoIdDefault?exists>
			$("#prCountry").jqxDropDownList('val', '${countryGeoIdDefault}');
		</#if>
	}
</script>
<div class="widget-box transparent no-bottom-border">
	<div id="jqxNotifyContainer">
		<div id="jqxNotification"></div>
	</div>
	<div class="widget-header">
		<h4>${uiLabelMap.OrganizationUnit}</h4>
		<span class="widget-toolbar none-content">
			<button id="addrowbutton" style="margin-left:20px; cursor: pointer;" 
			role="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius 
			jqx-widget jqx-widget-olbius jqx-fill-state-pressed jqx-fill-state-pressed-olbius" aria-disabled="false">
			<i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>
		</span>
	</div>
	<div class="widget-body">
		<div id="treePartyGroupGrid"></div>		
	</div>
</div>
<div id="partyGroupDetailWindow" style="display: none;">
	<div id="windowHeader">${uiLabelMap.CommonEdit} ${uiLabelMap.OrganizationUnit}</div>
	<div style="overflow: hidden;">
		<input type="hidden" name="partyIdFromOld" id="partyIdFromOld"/>
	 	<div class='row-fluid form-window-content' style="overflow: hidden;">
	 		<div class="span12">
	 			<div class='row-fluid margin-bottom10'>
	 				<div class='span4 align-right'>
	 					${uiLabelMap.OrgUnitId}
	 				</div>
	 				<div class='span8'>
	 					<input type="text" id="orgUnitId"/>
					</div>
	 			</div>
	 			<div class='row-fluid margin-bottom10'>
	 				<div class='span4 align-right'>
	 					${uiLabelMap.OrgUnitName}
	 				</div>
	 				<div class='span8'>
	 					<input type="text" id="orgUnitName"/>
					</div>
	 			</div>
	 			<div class='row-fluid margin-bottom10'>
	 				<div class='span4 align-right'>
	 					${uiLabelMap.BelongOrgUnit}
	 				</div>
	 				<div class='span8'>
	 					<div id="belongOrgUnitButton">
	 						 <div style="border: none;" id='jqxTreeOrgUnit'>
	 						 </div>
	 					</div>
					</div>
	 			</div>
	 			<div class='row-fluid margin-bottom10'>
	 				<div class='span4 align-right'>
	 					${uiLabelMap.CommonAddress}
	 				</div>
	 				<div class='span8'>
	 					<div id="orgAddress"></div>	 		
	 					
					</div>
	 			</div>
	 			<div class='row-fluid margin-bottom10'>
	 				<div class='span4 align-right'>
	 					${uiLabelMap.FunctionAndDuties}
	 				</div>
	 				<div class='span8'>
	 					<textarea id="comments"></textarea>
					</div>
	 			</div>
	 		</div>
	 	</div>
	 	<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right'  id="partyGroupDetailClose"><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
					<button class='btn btn-primary form-action-button pull-right'  id="partyGroupSave"><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="partyContactMechWindow" style="display: none;">
	<div id="contactMechWindowHeader"></div>
	<div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form name="editContactMech" id="editContactMech">
				<div class='row-fluid margin-bottom10'>
					<div class='span12'>
						<input type="hidden" name="partyId" id="partyId"/>
						<input type="hidden" name="contactMechId" id="contactMechId"/>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right asterisk'>
								${uiLabelMap.PartyAddressLine}
							</div>
							<div class='span8'>
								<input type="text" name="prAddress" id="prAddress"/>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								${uiLabelMap.CommonCountry}
							</div>
							<div class='span8'>
								<div id="prCountry"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								${uiLabelMap.PartyState}
							</div>
							<div class='span8'>
								<div id="prProvince"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								${uiLabelMap.PartyDistrictGeoId}
							</div>
							<div class='span8'>
								<div id="prDistrict"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								${uiLabelMap.PartyWardGeoId}
							</div>
							<div class='span8'>
								<div id="prWard"></div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="contactMechCancel"><i class='fa-remove'></i>${uiLabelMap.Cancel}</button>
					<button class='btn btn-primary form-action-button pull-right' id="contactMechSave"><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<#include "createNewPartyGroup.ftl"/>
