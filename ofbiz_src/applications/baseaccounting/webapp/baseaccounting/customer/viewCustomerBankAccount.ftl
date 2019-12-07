<script type="text/javascript">
var statusFinAccArr = [
	<#if statusFinAccList?has_content>
		<#list statusFinAccList as statusFinAcc>
		{
			statusId: '${statusFinAcc.statusId}',
			description: '${statusFinAcc.get("description", locale)}'
		},
		</#list>
	</#if>
];

var cellClass = function (row, columnfield, value) {
		var data = $('#jqxgirdSupplierFinAcc').jqxGrid('getrowdata', row);
		if (typeof(data) != 'undefined') {
			if ("FNACT_MANFROZEN" == data.statusId) {
				return "background-cancel";
			} 
		}
};
if(typeof(uiLabelMap) == "undefined"){
	uiLabelMap = {};
}
if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
uiLabelMap.DeactiveSupplierFinAccountConfirm = "${StringUtil.wrapString(uiLabelMap.DeactiveSupplierFinAccountConfirm)}";
uiLabelMap.ActiveSupplierFinAccountConfirm = "${StringUtil.wrapString(uiLabelMap.ActiveSupplierFinAccountConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateSupplierFinAccountConfirm = "${StringUtil.wrapString(uiLabelMap.CreateSupplierFinAccountConfirm)}";
globalVar.partyId = "${parameters.partyId?if_exists}";
<#if defaultCountryGeoId?exists>
	globalVar.defaultCountryGeoId = "${defaultCountryGeoId}";
</#if>
</script>
<div id="finAccount-tab" class="tab-pane">
	<div class="row-fluid">
		<#assign datafieldFinAcc = "[{name: 'finAccountId', type: 'string'},
									 {name: 'finAccountCode', type: 'string'},
									 {name: 'finAccountName', type: 'string'},
									 {name: 'statusId', type: 'string'},
									 {name: 'stateProvinceGeoName', type: 'string'},
									 {name: 'countryGeoName', type: 'string'},
									]"/>
		<#assign columnlistFinAcc = "
								{text: '${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}', datafield: 'finAccountCode', width: '22%%',
									cellClassName: cellClass
								},
								{text: '${StringUtil.wrapString(uiLabelMap.AtTheBank)}', datafield: 'finAccountName', width: '23%', cellClassName: cellClass},
								{text: '${StringUtil.wrapString(uiLabelMap.BPOStateProvince)}', datafield: 'stateProvinceGeoName', width: '17%', cellClassName: cellClass},
								{text: '${StringUtil.wrapString(uiLabelMap.BPOCountry)}', datafield: 'countryGeoName', width: '17%', cellClassName: cellClass},
								{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', columntype: 'dropdownlist', 
									filtertype: 'checkedlist', cellClassName: cellClass,
									cellsrenderer: function(row, column, value){
										for(var i = 0; i < statusFinAccArr.length; i++){
											if(statusFinAccArr[i].statusId == value){
												return '<span>' + statusFinAccArr[i].description + '</span>'
											}
										}
									},
									createfilterwidget: function (column, htmlElement, editor) {
										var filterBoxAdapter = new $.jqx.dataAdapter(statusFinAccArr,{autoBind: true});
				                		var uniqueRecords = filterBoxAdapter.records;
										editor.jqxDropDownList({source: uniqueRecords, displayMember: 'description', valueMember: 'statusId'});
									}
								}
								"/>	
								
		<@jqGrid filtersimplemode="true" filterable="true" dataField=datafieldFinAcc columnlist=columnlistFinAcc editable="false" showtoolbar="true"
							addrow="true" addType="popup" alternativeAddPopup="AddSupplierFinAccountWindow"
							id="jqxgirdSupplierFinAcc" filterable="true" clearfilteringbutton="true" mouseRightMenu="true" contextMenuId="contextMenu"
							url="jqxGeneralServicer?sname=JQGetListSupplierFinAccount&partyId=${parameters.partyId?if_exists}" customTitleProperties="AccountingBankAccount"/>
	</div>
</div>

<div id="AddSupplierFinAccountWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCAccountNumber)}</label>
				</div>
				<div class="span8">
					<input type="text" id="addFinAccountCode" >
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AtTheBank)}</label>
				</div>
				<div class="span8">
					<input type="text" id="addFinAccountName" >
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BPOCountry)}</label>
				</div>
				<div class="span8">
					<div id="addFinAccountCountry"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.BPOStateProvince)}</label>
				</div>
				<div class="span8">
					<div id="addFinAccountState"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class=""></label>
				</div>
				<div class="span8">
					<div id="useAccountCheck" style="margin-left: -3px !important"><span style="font-size: 14px">${uiLabelMap.UseThisAccount}</span></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddSupplierFinAcc">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddSupplierFinAcc">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="contextMenu" class="hide">
	<ul>
		<li id="activeFinAccount"><i class="fa fa-check-circle"></i>${StringUtil.wrapString(uiLabelMap.ActiveFinAcc)}</li>
		<li id="deactiveFinAccount"><i class="fa fa-ban"></i>${StringUtil.wrapString(uiLabelMap.DeactiveFinAcc)}</li>
	</ul>
</div>
<script type="text/javascript" src="/accresources/js/customer/viewCustomerFinAccount.js"></script>