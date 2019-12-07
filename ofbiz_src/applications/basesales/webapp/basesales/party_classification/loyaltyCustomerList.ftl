<#assign dataField = "[
			{name: 'loyaltyPointId', type: 'string'}, 
			{name: 'partyId', type: 'string'}, 
			{name: 'partyName', type: 'string'}, 
			{name: 'createdDate', type: 'date', other: 'Timestamp'}, 
			{name: 'productStoreId', type: 'string'}, 
			{name: 'storeName', type: 'string'}, 
			{name: 'point', type: 'number'}, 
			{name: 'ratingTypeId', type: 'string'}, 
			{name: 'ratingName', type: 'string'}, 
			{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
			{name: 'thruDate', type: 'date', other: 'Timestamp'}, 
		]"/>
<#--
cellsrenderer: function(row, colum, value) {
	var data = $('#jqxLoyaltyCustomer').jqxGrid('getrowdata', row);
	return \"<span><a href='listLoyaltyCustomerDetail?loyaltyPointId=\" + data.loyaltyPointId + \"&partyId=\" + value + \"'>\" + value + \"</a></span>\";
}
-->
<#assign columnlist = "
			{text: '${uiLabelMap.BSCustomerId}', dataField: 'partyId', width: 170}, 
          	{text: '${uiLabelMap.BSCustomerName}', dataField: 'partyName'}, 
          	{text: '${uiLabelMap.BSPSSalesChannel}', dataField: 'storeName', width: 180}, 
          	{text: '${uiLabelMap.BSPoint}', dataField: 'point', filtertype: 'number'}, 
			{text: '${uiLabelMap.BSPartyClassificationGroupName}', dataField: 'ratingName'}, 
			{text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'}, 
			{text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
			{text: '${uiLabelMap.BSHistory}', datafield: 'history', width: 80, 
			   	cellsrenderer:  function (row, column, value, a, b, data){
			   		var rowData = $('#jqxLoyaltyCustomer').jqxGrid('getrowdata', row);
				   	var str = \"<div class='cell-custom-grid align-center'><a href='javascript:OlbLoyaltyCustomerViewDetail.viewDetailHistory(\";
				   	if (rowData) {
				   		str += '\"' + rowData.partyId + '\"';
				   	}
				   	str += \")'><i class='fa fa-history'></i></a></div>\";
				   	return str;
			   	}
			}
		"/>
		
<@jqGrid id="jqxLoyaltyCustomer" url="jqxGeneralServicer?sname=JQListLoyaltyCustomer" columnlist=columnlist dataField=dataField
		editable="false" viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" clearfilteringbutton = "true"/>

<div id="alterpopupWindowViewDetailHistory" style="display:none">
	<div>${uiLabelMap.BSListLoyaltyCustomerDetail}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="jqxLoyaltyCustomerHistory"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true/>
<script type="text/javascript">
	$(function(){
		OlbLoyaltyCustomerViewDetail.init();
	});
	var OlbLoyaltyCustomerViewDetail = (function(){
		var loyaltyCustomerHistoryGRID;
		
		var init = function(){
			initElement();
			initElementComplex();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowViewDetailHistory"), {maxWidth: 1020, width: 1020, height: 520, cancelButton: $("#alterCancel")});
		};
		var initElementComplex = function(){
			var configLoyaltyCustomerHistory = {
				showdefaultloadelement: true,
				autoshowloadelement: true,
				dropDownHorizontalAlignment: 'right',
				datafields: [
						{name: 'loyaltyPointId', type: 'string'}, 
						{name: 'loyaltyPointDetailSeqId', type: 'string'}, 
						{name: 'effectiveDate', type: 'date', other: 'Timestamp'}, 
						{name: 'pointDiff', type: 'number'}, 
						{name: 'orderId', type: 'string'}, 
						{name: 'returnId', type: 'string'}, 
					],
				columns: [
						{text: '${uiLabelMap.BSSeqId}', dataField: 'loyaltyPointDetailSeqId', width: 200}, 
			          	{text: '${uiLabelMap.BSPoint}', dataField: 'pointDiff', filtertype: 'number'}, 
						{text: '${uiLabelMap.BSOrderId}', dataField: 'orderId', 
							cellsrenderer: function(row, colum, value) {
			                	return "<span><a href='viewOrder?orderId=" + value + "'>" + value + "</a></span>";
			                }
						}, 
						{text: '${uiLabelMap.BSReturnOrder}', dataField: 'returnId'}, 
						{text: '${uiLabelMap.BsEffectiveDate}', dataField: 'effectiveDate', cellsformat: 'dd/MM/yyyy', filtertype: 'range', width: 250}, 
					],
				useUrl: true,
				root: 'results',
				url: "",
				useUtilFunc: true,
				clearfilteringbutton: false,
				editable: false,
				filterable: true,
				alternativeAddPopup: 'alterpopupWindow',
				pagesize: 12,
				showtoolbar: false,
				editmode: 'click',
				width: '100%',
				bindresize: false,
			};
			loyaltyCustomerHistoryGRID = new OlbGrid($("#jqxLoyaltyCustomerHistory"), null, configLoyaltyCustomerHistory, []);
		};
		var viewDetailHistory = function(partyId){
			if (partyId) {
				$("#alterpopupWindowViewDetailHistory").jqxWindow("open");
				var newUrl = "jqxGeneralServicer?sname=JQListLoyaltyCustomerDetail&partyId=" + partyId;
				loyaltyCustomerHistoryGRID.updateSource(newUrl);
			}
		};
		return {
			init: init,
			viewDetailHistory: viewDetailHistory,
		}
	}());
</script>
