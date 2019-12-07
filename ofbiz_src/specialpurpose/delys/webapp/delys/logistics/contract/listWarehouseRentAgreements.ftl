<script type="text/javascript">
	<#assign company = Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
	
	<#assign rentParty = delegator.findList("PartyRoleAndPartyDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "RENT_FACILITY")), null, null, null, false)>
	var partyIdToData = new Array();
	<#list rentParty as item>
		var row = {};
		row['partyId'] = '${item.partyId?if_exists}';
		<#if item.firstName?has_content || item.middleName?has_content || item.lastName?has_content || item.groupName?has_content>
			row['description'] = '${item.firstName?if_exists} ' + '${item.middleName?if_exists} ' + '${item.lastName?if_exists} ' + '${item.groupName?if_exists}';
		<#else>
			row['description'] = '${item.partyId?if_exists}';
		</#if>
		partyIdToData[${item_index}] = row;
	</#list>
	
	var companyName = null;
	<#assign compartyDetail = delegator.findOne("PartyNameView", {"partyId" : company}, false)/>
	<#if compartyDetail.firstName?has_content || compartyDetail.middleName?has_content || compartyDetail.lastName?has_content || compartyDetail.groupName?has_content>
		companyName = '${StringUtil.wrapString(compartyDetail.firstName?if_exists)} ' + '${StringUtil.wrapString(compartyDetail.middleName?if_exists)} ' + '${StringUtil.wrapString(compartyDetail.lastName?if_exists)} ' + '${StringUtil.wrapString(compartyDetail.groupName?if_exists)}';
	<#else>
		companyName = '${compartyDetail.partyId?if_exists}';
	</#if>
	
	var partyIdFromData = new Array();
	var row = {};
	row['partyId'] = '${compartyDetail.partyId?if_exists}';
	row['description'] = companyName;
	partyIdFromData[0] = row;
</script>
<div>
<#assign columnlistContract="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
		{ text: '${uiLabelMap.AgreementId}', dataField: 'agreementId', width: 100, align: 'center', editable: false, pinned: true},
		{ text: '${uiLabelMap.AgreementName}' ,filterable: true, sortable: true, datafield: 'attrValue', align: 'center', width: 200, editable: false},
		{ text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', align: 'center', width: 200, editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy'},
		{ text: '${uiLabelMap.Supplier}', datafield: 'partyIdTo', align: 'center', width: 200, filtertype: 'checkedlist', editable: false,
        },
		{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center', minwidth: 150, editable: false },
		{ text: '${uiLabelMap.Status}', datafield: 'statusId', align: 'center', width: 150, editable: true, columntype: 'dropdownlist', filtertype: 'checkedlist',
        }
	 "/>
<#assign dataFieldContract="[
				{ name: 'agreementId', type: 'string' },
				{ name: 'partyIdTo', type: 'string' },
				{ name: 'description', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'attrValue', type: 'string' },
             	{ name: 'agreementDate',  type: 'date', other: 'Timestamp'},
	 		 	]"/>
</div>
<@jqGrid filtersimplemode="true" customTitleProperties="ListWarehouseRentAgreement" sortdirection="desc" sortable="true" 
	id="jqxgridContract" addType="popup" dataField=dataFieldContract columnlist=columnlistContract 
	clearfilteringbutton="true" addrefresh="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="false" 
	url="jqxGeneralServicer?sname=getWarehouseRentAgreement&agreementTypeId=WAREHOUSE_RENT_AGRE" createUrl="jqxGeneralServicer?sname=createWarehouseRentAgreement&jqaction=C" updateUrl=""
	addColumns="createdDate(java.sql.Timestamp);listTerms(java.util.List);"
/>
<div id="alterpopupWindow" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.CreateNewAgreement}
	</div>
	<div class='form-window-container'>
	<div class='form-window-content'>
		<input type="hidden" id="agreementTypeId" value="WAREHOUSE_RENT_AGRE"></input>
		<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	        ${uiLabelMap.GeneralInfo}
	    </h4>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.SlideA}: </div>
					</div>
					<div class="span7">
						<div id="partyIdFrom" style="width: 100%;" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.Represented}: </div>
					</div>
					<div class="span7">
						<div id="representPartyIdFrom" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addRepresentForCompanyFrom()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
					</div>
				</div>
			</div>
			<div class="span6 no-left-margin">
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div>${uiLabelMap.SlideB}:</div>
					</div>
					<div class="span7">
						<div id="partyIdTo" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addRentFacilityCompany()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div>${uiLabelMap.Represented}:</div>
					</div>
					<div class="span7">
						<div id="representPartyIdTo" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addRepresentForCompanyTo()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
					</div>
				</div>
			</div>
	    </div>
	</div>
	<div class="form-action">
	    <div class='row-fluid'>
	        <div class="span12 margin-top20" style="margin-bottom:10px;">
	            <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	            <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
	    </div>
	</div>
</div>

<div id="addRentCompanyWindow" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.NewCompaniesRentingWarehouse}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.CompanyName)}</div>
					</div>  
					<div class="span7">
						<input id="groupName"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.Address)}</div>
					</div>  
					<div class="span7">
						<input id="address1"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.TelephoneNumber)}</div>
					</div>  
					<div class="span7">
						<input id="telephoneNumber"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.FaxNumber)}</div>
					</div>  
					<div class="span7">
						<input id="faxNumber"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.BankAccount)}</div>
					</div>  
					<div class="span7">
						<input id="bankAccount"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.EmailAddress)}</div>
					</div>  
					<div class="span7">
						<input id="email"></input>
					</div>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="newComCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
							<button id="newComOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="addCompanyRepresentFrom" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.NewRepresentForCompany}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.lastName)}</div>
					</div>  
					<div class="span7">
						<input id="lastName"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.middleName)}</div>
					</div>  
					<div class="span7">
						<input id="middleName"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.firstName)}</div>
					</div>  
					<div class="span7">
						<input id="firstName"></input>
					</div>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="newRepFromCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
							<button id="newRepFromOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="addCompanyRepresentTo" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.NewRepresentForCompany}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.lastName)}</div>
					</div>  
					<div class="span7">
						<input id="lastName"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.middleName)}</div>
					</div>  
					<div class="span7">
						<input id="middleName"></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px" class="asterisk">${StringUtil.wrapString(uiLabelMap.firstName)}</div>
					</div>  
					<div class="span7">
						<input id="firstName"></input>
					</div>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="newRepToCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
							<button id="newRepToOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	
	var partyCompanyGl = null; 
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 590, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
	});
	$("#partyIdTo").jqxDropDownList({selectedIndex: 0, source: partyIdToData, width: 200, displayMember: "description", valueMember: "partyId", autoDropDownHeight: true});
	$("#partyIdFrom").jqxDropDownList({selectedIndex: 0, source: partyIdFromData, width: 200, displayMember: "description", valueMember: "partyId", autoDropDownHeight: true});
	
	var representPartyIdFrom = new Array();
	var representPartyIdTo = new Array();
	
	$("#representPartyIdFrom").jqxDropDownList({selectedIndex: 0, source: representPartyIdFrom, width: 200, displayMember: "description", valueMember: "partyId", autoDropDownHeight: true});
	$("#representPartyIdTo").jqxDropDownList({selectedIndex: 0, source: representPartyIdTo, width: 200, displayMember: "description", valueMember: "partyId", autoDropDownHeight: true});
	
	$("#addRentCompanyWindow").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newComCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	
	$("#addCompanyRepresentFrom").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newRepFromCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	
	$("#addCompanyRepresentTo").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newRepToCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	
	$("#firstName").jqxInput({height: 20, width: '195', minLength: 1});
	$("#middleName").jqxInput({height: 20, width: '195', minLength: 1});
	$("#lastName").jqxInput({height: 20, width: '195', minLength: 1});
	$("#groupName").jqxInput({height: 20, width: '195', minLength: 1});
	
	$("#telephoneNumber").jqxInput({height: 20, width: '195', minLength: 1});
	$("#faxNumber").jqxInput({height: 20, width: '195', minLength: 1});
	$("#email").jqxInput({height: 20, width: '195', minLength: 1});
	$("#bankAccount").jqxInput({height: 20, width: '195', minLength: 1});
	
	var listDetailName = new Array();
	listDetailName.push("firstName");
	listDetailName.push("middleName");
	listDetailName.push("lastName");
	update(
		{
			partyId: $("#partyIdFrom").jqxDropDownList('val'),
			partyRelationshipTypeId: "REPRESENT_LEGAL",
			roleTypeIdFrom: "INTERNAL_ORGANIZATIO",
			roleTypeIdTo: "REPRESENT_LEGAL",
		},
		"getPartyRepresents", "listPartyRepresents", "partyId", listDetailName, "representPartyIdFrom"
	);
	// add new company 
	function addRentFacilityCompany(){
		partyCompanyGl = $("#representPartyIdFrom").val();
		$("#addRentCompanyWindow").jqxWindow('open');
	}
	
	// add new company's represent
	function addRepresentForCompanyFrom(){
		$("#addCompanyRepresentFrom").jqxWindow('open');
	}
	
	function addRepresentForCompanyTo(){
		$("#addCompanyRepresentTo").jqxWindow('open');
	}
	
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			if (typeof value === 'string'){
				row['description'] = data[x][value];
			}
			if (value instanceof Array){
				var description = null;
				for (var i = 0; i < value.length; i ++){
					if (data[x][value[i]]){
						if (description){
							description = data[x][value[i]];
						} else {
							description = description + ' ' + data[x][value[i]];
						}
					}
				}
				row['description'] = description;
			}
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
</script>