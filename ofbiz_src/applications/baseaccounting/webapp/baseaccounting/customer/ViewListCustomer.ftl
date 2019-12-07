<#include "script/ViewListCustomerScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'partyCode', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'addressDetail', type: 'string'},
					   {name: 'taxCode', type: 'string'}, 	
					   {name: 'taxAuthGeoId', type: 'string'},					   	
					   {name: 'taxAuthPartyId', type: 'string'},
					   {name: 'finAccountId', type: 'string'}, 	
					   {name: 'finAccountCode', type: 'string'}, 	
					   {name: 'finAccountName', type: 'string'},
					   {name: 'partyBeneficiary', type: 'string'},
					   {name: 'contactNumber', type: 'string'},
					   {name: 'faxNumber', type: 'string'},
					   {name: 'emailAddress', type: 'string'},
					   {name: 'partyRepresentative', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					   {name: 'activatedDate', type: 'date'},
					   {name: 'locationContactMechId', type: 'string'},					   	
					   {name: 'phoneContactMechId', type: 'string'},					   	
					   {name: 'emailContactMechId', type: 'string'},					   	
					   {name: 'faxContactMechId', type: 'string'},					   	
					   {name: 'locCountryGeoId', type: 'string'},					   	
					   {name: 'locStateProvinceGeoId', type: 'string'},					   	
					   {name: 'locDistrictGeoId', type: 'string'},					   	
					   {name: 'locWardGeoId', type: 'string'},					   	
					   {name: 'locAddress1', type: 'string'},					   	
					   ]"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCSeqId)}', sortable: false, filterable: false, editable: false, finned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: '6%',
						    cellsrenderer: function (row, column, value) {
						        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerIdShort)}', datafield: 'partyCode', width: '10%', pinned: true,
						cellsrenderer: function (row, column, value){
					        var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					        return '<span><a href=\"viewCustomer?partyId=' + data.partyId + '\">' + value + '</a></span>';
					    }},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}', datafield: 'fullName', width: '16%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonAddress1)}', datafield: 'addressDetail', width: '25%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}', datafield: 'taxCode', width: '11%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}', datafield: 'finAccountCode', width: '11%'},
						{text: '${StringUtil.wrapString(uiLabelMap.PartyBeneficiary)}', datafield: 'partyBeneficiary', width: '16%'},
						{text: '${StringUtil.wrapString(uiLabelMap.AccountingBank)}', datafield: 'finAccountName', width: '16%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonTelephoneAbbr)}', datafield: 'contactNumber', width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.PartyFaxNumber)}', datafield: 'faxNumber', width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BSEmail)}', datafield: 'emailAddress', width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.PartyRepresent)}', datafield: 'partyRepresentative', width: '14%'},
						{text: '${StringUtil.wrapString(uiLabelMap.ActiveDate)}', datafield: 'activatedDate', width: '12%', cellsformat: 'dd/MM/yyyy', filtertype:'range'},
						"/>
</script>	

<@jqGrid dataField=datafield columnlist=columnlist
		clearfilteringbutton="true" width="100%" filterable="true"
		showtoolbar="true" jqGridMinimumLibEnable="false" id="jqxgrid"
		alternativeAddPopup="addNewCustomerWindow" addrow="true" addType="popup"
		url="" customControlAdvance="<div id='customerStatus'></div>"
		mouseRightMenu="true" contextMenuId="contextMenu" 
		/>	
		
<div id='contextMenu' class="hide">
	<ul>
		<li action="edit" id="editCustomerMenu">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
		<li action="expire" id="expireCustomerMenu">
			<i class="fa fa-ban"></i>${uiLabelMap.ExpireRelationship}
        </li>
		<li action="active" id="activeCustomerMenu">
			<i class="fa fa-check-square-o"></i>${uiLabelMap.BACCCommonActive}
        </li>
	</ul>
</div>		
		
<div id="addNewCustomerWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCCustomerId)}</label>
							</div>
							<div class="span8">
								<input type="text" id="partyCode">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCCustomerName)}</label>
							</div>
							<div class="span8">
								<input type="text" id="groupName">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.PartyRepresent)}</label>
							</div>
							<div class="span8">
								<input type="text" id="partyRepresent">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ActiveDate)}</label>
							</div>
							<div class="span8">
								<div id="fromDate"></div>
							</div>
						</div>
					</div><!-- ./span6 -->
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BACCTaxCode)}</label>
							</div>
							<div class="span8">
								<input type="text" id="taxCode">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}</label>
							</div>
							<div class="span8">
								<input type="text" id="finAccountCode">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.PartyBeneficiary)}</label>
							</div>
							<div class="span8">
								<input type="text" id="partyBeneficiaryName">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.AccountingBank)}</label>
							</div>
							<div class="span8">
								<input type="text" id="finAccountName">
							</div>
						</div>
					</div><!-- ./span6 -->
				</div><!-- ./span12 -->
			</div><!-- ./row-fluid -->
			<div class="legend-container">
				<span>${uiLabelMap.PartyContactMechs}</span>
				<hr/>
			</div>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.CommonTelephoneAbbr)}</label>
							</div>
							<div class="span8">
								<input type="text" id="primayPhone">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.PartyFaxNumber)}</label>
							</div>
							<div class="span8">
								<input type="text" id="faxNumber">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BSEmail)}</label>
							</div>
							<div class="span8">
								<input type="text" id="emailAddress">
							</div>
						</div>
					</div><!-- ./span6 -->
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.CommonCountry)}</label>
							</div>
							<div class="span8">
								<div id="countryGeoId"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.CommonStateProvince)}</label>
							</div>
							<div class="span8">
								<div id="stateProvinceGeoId"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BSCounty)}</label>
							</div>
							<div class="span8">
								<div id="countyGeoId"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.BSWard)}</label>
							</div>
							<div class="span8">
								<div id="wardGeoId"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${StringUtil.wrapString(uiLabelMap.CommonAddress1)}</label>
							</div>
							<div class="span8">
								<input type="text" id="address1">
							</div>
						</div>
					</div><!-- ./span6 -->
				</div><!-- ./span12 -->
			</div><!-- ./row-fluid -->
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddCustomer">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddCustomer">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/accresources/js/customer/ViewListCustomer.js"></script>
<script type="text/javascript" src="/accresources/js/customer/editCustomer.js"></script>
						   