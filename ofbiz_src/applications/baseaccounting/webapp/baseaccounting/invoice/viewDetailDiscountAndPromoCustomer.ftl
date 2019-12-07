<@jqGridMinimumLib/>
<script type="text/javascript" src="/accresources/js/acc.utils.js"></script>
<link rel="stylesheet" href="/aceadmin/assets/css/colorbox.css" />
<script type="text/javascript" src="/aceadmin/assets/js/jquery.colorbox-min.js"></script>
<script type="text/javascript">
    var globalVar = {};
    var uiLabelMap = {};
    uiLabelMap.VoucherForm = "${StringUtil.wrapString(uiLabelMap.VoucherForm)}";
    uiLabelMap.VoucherSerial = "${StringUtil.wrapString(uiLabelMap.VoucherSerial)}";
    uiLabelMap.VoucherNumber = "${StringUtil.wrapString(uiLabelMap.VoucherNumber)}";
    uiLabelMap.BACCIssueDate = "${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}";
    uiLabelMap.ReceivingVoucherDate = "${StringUtil.wrapString(uiLabelMap.ReceivingVoucherDate)}";
    uiLabelMap.PublicationVoucherDate = "${StringUtil.wrapString(uiLabelMap.PublicationVoucherDate)}";
    uiLabelMap.BACCInvoiceTypeId = "${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}";
    uiLabelMap.HRCommonAttactFile = "${StringUtil.wrapString(uiLabelMap.HRCommonAttactFile)}";
    uiLabelMap.AmountNotIncludeTax = "${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}";
    uiLabelMap.CommonTax = "${StringUtil.wrapString(uiLabelMap.CommonTax)}";
    uiLabelMap.CommonTotal = "${StringUtil.wrapString(uiLabelMap.CommonTotal)}";
    uiLabelMap.ListVouchers = "${StringUtil.wrapString(uiLabelMap.ListVouchers)}";
    uiLabelMap.ListVoucherOfInvoice = "${StringUtil.wrapString(uiLabelMap.ListVoucherOfInvoice)}";
    uiLabelMap.BSNoDataToExport = "${StringUtil.wrapString(uiLabelMap.BSNoDataToExport)}";

    <#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
    <#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp).getTime()/>
    <#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale).getTime()/>
</script>

<link rel="stylesheet" type="text/css" href="/accresources/css/grid.css">
<div class="row-fluid" style="position: relative;">
    <h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
    ${uiLabelMap.BACCViewDetailDiscount}
    </h3>
</div>
<div class="row-fluid">
    <div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
        <div class="row-fluid">
            <div class="span6">
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BACCCustomerId}:</label>
                    </div>
                    <div class="div-inline-block">
                        <span><i>${parameters.partyId?if_exists?default('')}</i></span>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="div-inline-block">
                        <label>${uiLabelMap.BACCStatusId}:</label>
                    </div>
                    <div class="div-inline-block">
                    <#if parameters.newStatusId?exists>
                        <#assign invoiceNewStatus = delegator.findOne("StatusItem",{"statusId" : "${parameters.newStatusId}"},false)!>
                        <span><i>${StringUtil.wrapString(invoiceNewStatus.get("description", locale))}</i></span>
                    </#if>
                    </div>
                </div>
            </div><!--./span6-->
        </div>
    </div>
</div>

<#assign datafield = "[{name: 'invoiceId', type: 'string'},
		               {name: 'productId', type: 'string'},
		               {name: 'productCode', type: 'string'},
		               {name: 'productName', type: 'string'},
		               {name: 'quantity', type: 'number'},
     	 		 	   {name: 'amount', type: 'number'},
     	 		 	   {name: 'description', type: 'string'},
     	 		 	   {name: 'totalAmount', type: 'number'},
					   ]"/>

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCSeqId)}', sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false, pinned: true,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=\"margin:4px;\">' + (value + 1) + '</div>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceId)}', datafield: 'invoiceId', width: '20%', pinned: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxGridDetailDiscount').jqxGrid('getrowdata', row);
							if(data != undefined && data){
									return '<span><a href=ViewARInvoice?invoiceId=' + data.invoiceId + ' style=\"color: blue !important;\">' + data.invoiceId + '</a></span>';
							}
						}},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}', datafield: 'productCode', width: '13%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}', datafield: 'productName', width: '22%',},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}', datafield: 'quantity', width: '9%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								if(typeof(value)== 'number'){
									return '<span class=\"align-right\">' + value + '</span>';
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}', datafield: 'amount', width: '14%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxGridDetailDiscount').jqxGrid('getrowdata', row);
								if(data){
									return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCTotal)}', datafield: 'totalAmount', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxGridDetailDiscount').jqxGrid('getrowdata', row);
								if(data){
									return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}
						},
						"/>
<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.BACCViewDetailDiscount)/>
<@jqGrid id="jqxGridDetailDiscount" filtersimplemode="true" addrefresh="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
url="jqxGeneralServicer?sname=jqxGetDetailDiscountForParty&partyId=${parameters.partyId}&newStatusId=${parameters.newStatusId}&productPromoId=${parameters.productPromoId}" dataField=datafield columnlist=columnlist
jqGridMinimumLibEnable="false" customTitleProperties=customTitleProperties
customcontrol1="fa fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript:void(0);exportExcel()"
isSaveFormData="true" formData="filterObjData" mouseRightMenu="true" contextMenuId="contextMenu"
/>

<script>
    $( document ).ready(function(){
    });
</script>

<script>
    var filterObjData = new Object();
    var exportExcel = function(){
        var dataGrid = $("#jqxGridDetailDiscount").jqxGrid('getrows');
        if (dataGrid.length == 0) {
            jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
            return false;
        }

        var partyId = "${parameters.partyId}";
        var newStatusId = "${parameters.newStatusId}";
        var productPromoId = "${parameters.productPromoId}";

        var winURL = "exportDetailDiscountCustomer";
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", winURL);
        form.setAttribute("target", "_blank");

        var hiddenField0 = document.createElement("input");
        hiddenField0.setAttribute("type", "hidden");
        hiddenField0.setAttribute("name", "partyId");
        hiddenField0.setAttribute("value", partyId);
        form.appendChild(hiddenField0);

        var statusField = document.createElement("input");
        statusField.setAttribute("type", "hidden");
        statusField.setAttribute("name", "newStatusId");
        statusField.setAttribute("value", newStatusId);
        form.appendChild(statusField);

        var productPromoField = document.createElement("input");
        productPromoField.setAttribute("type", "hidden");
        productPromoField.setAttribute("name", "productPromoId");
        productPromoField.setAttribute("value", productPromoId);
        form.appendChild(productPromoField);

        if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
            $.each(filterObjData.data, function(key, value) {
                var hiddenField1 = document.createElement("input");
                hiddenField1.setAttribute("type", "hidden");
                hiddenField1.setAttribute("name", key);
                hiddenField1.setAttribute("value", value);
                form.appendChild(hiddenField1);
            });
        }

        document.body.appendChild(form);
        form.submit();
    }
</script>

