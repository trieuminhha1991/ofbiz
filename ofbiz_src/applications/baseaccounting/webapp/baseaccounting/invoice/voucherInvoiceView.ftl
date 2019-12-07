<style>
.cell-green-color {
    color: black !important;
    background-color: #FFCCFF !important;
}
</style>
<#assign listTaxCategory = delegator.findByAnd("ProductCatergoryAndTaxRate", null, null, false)!/>
<#assign invoiceNoTaxTotal = Static["com.olbius.accounting.invoice.InvoiceWorker"].getInvoiceNoTaxTotal(invoice)/>
<#assign invoiceTaxTotal = Static["com.olbius.accounting.invoice.InvoiceWorker"].getInvoiceTaxTotalByTaxRateSeq(invoice)/>
<#assign isExistVoucher = Static["com.olbius.accounting.invoice.InvoiceWorker"].isExistsVoucher(invoice)/>
<script type="text/javascript">
var taxCategoryData = [
	<#if listTaxCategory?has_content>
		<#list listTaxCategory as item>
		{	productCategoryId: "${item.productCategoryId}",
			categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}",
			taxPercentage: <#if item.taxPercentage?exists>${item.taxPercentage}<#else>0</#if>
		},
		</#list>
	</#if>
];
if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
<#assign invVoucherDiffValueConfig = dispatcher.runSync("getInvoiceVoucherDiffValueConfig", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId, "userLogin", userLogin))/>
globalVar.invoiceNoTaxTotal = "${invoiceNoTaxTotal}";
<#if invVoucherDiffValueConfig.systemValue?exists>
	globalVar.invVoucherDiffValue = ${invVoucherDiffValueConfig.systemValue};
</#if>
</script>
<@jqOlbCoreLib hasCore=false hasDropDownList=true/>
<#assign datafieldVoucher = "[{name: 'voucherId', type: 'string'},
							  {name: 'invoiceId', type: 'string'},	
							  {name: 'voucherForm', type: 'string'},	
							  {name: 'voucherSerial', type: 'string'},
							  {name: 'voucherNumber', type: 'string'},
							  {name: 'issuedDate', type: 'date'},
							  {name: 'voucherCreatedDate', type: 'date'},
							  {name: 'taxProductCategoryId', type: 'string'},							  
							  {name: 'dataResourceId', type: 'string'},
							  {name: 'dataResourceName', type: 'string'},
							  {name: 'objectInfo', type: 'string'},
							  {name: 'amount', type: 'number'},
							  {name: 'taxAmount', type: 'number'},	
							  {name: 'totalAmount', type: 'number'},
							  {name: 'totalAmountVoucher', type: 'number'},
							  {name: 'currencyUomId', type: 'string'},	
							  ]"/>
<#if businessType == 'AP'>
	<#assign voucherCreatedDateAPTitle = StringUtil.wrapString(uiLabelMap.ReceivingVoucherDate)/>							  
<#else>
	<#assign voucherCreatedDateAPTitle = StringUtil.wrapString(uiLabelMap.PublicationVoucherDate)/>
</#if>
							  
<#assign columnlistVoucher = "{text: '${StringUtil.wrapString(uiLabelMap.VoucherForm)}', datafield: 'voucherForm', width: '12%', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.VoucherSerial)}', datafield: 'voucherSerial', width: '11%', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.VoucherNumber)}', datafield: 'voucherNumber', width: '11%', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCIssueDate)}', datafield: 'issuedDate', width: '13%', cellsformat: 'dd/MM/yyyy', 
					   		columntype: 'datetimeinput', filtertype: 'range', editable: false},
					   {text: '${voucherCreatedDateAPTitle}', datafield: 'voucherCreatedDate', width: '13%', cellsformat: 'dd/MM/yyyy', 
					   		columntype: 'datetimeinput', filtertype: 'range', editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceTypeId)}', datafield: 'taxProductCategoryId', width: '11%', editable: false},					   		
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonAttactFile)}', datafield: 'dataResourceName', width: '18%', editable: false,
					   		cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
						   		return '<span><a href=\"javascript:void(0)\" onclick=\"javascript: voucherInvoiceViewObj.changeLinkImg(' + row + ')\">' + value + '</a></span>'
					   		}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.AmountNotIncludeTax)}', datafield: 'amount', filtertype: 'number', width: '14%', editable: false,
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxVoucherList').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonTax)}', datafield: 'taxAmount', filtertype: 'number', width: '13%', editable: false,
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxVoucherList').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonTotal)}', datafield: 'totalAmount', columntype: 'numberinput',
						  filtertype: 'number', width: '14%', editable: false,
						  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxVoucherList').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}   
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.BACCTotalAmountVoucher)}', datafield: 'totalAmountVoucher', filtertype: 'number', width: '14%', editable: false,
						  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(typeof(value) == 'number'){
									var data = $('#jqxVoucherList').jqxGrid('getrowdata', row);
									return '<span style=\"text-align: right\">' + formatcurrency(value, data.currencyUomId) + '</span>';
								}
							}
					   }			   				   
				"/>							  

<a class="hide" data-rel='colorbox' href="javascript:void(0)" id="viewImgVoucher"></a>
<#if voucherTotalList?has_content>
	<#assign voucherTotal = voucherTotalList.get(0)/>
</#if>
<#assign hasAccGeneralRole = Static["com.olbius.basehr.util.SecurityUtil"].hasRole("ACC_MANAGER_EMP", userLogin.partyId, delegator)/>
<div class="row-fluid" style=" position: relative;">
	<#if voucherTotal?exists>
		<div style="top: -10px; right: 5px; position: absolute; background: rgba(182, 189, 179, 0.3); padding: 5px 20px">
			<#if !invoice.isVerified?exists || invoice.isVerified != "Y">
				<a href="javascript:void(0)" id="editVoucherInvoiceVerify" style="font-size: 14px"><i class="fa fa-wrench"></i>${StringUtil.wrapString(uiLabelMap.CommonUpdate)}</a>
			<#else>
				<span style="font-size: 14px; color: #53539e"><i class="fa fa-thumbs-o-up"></i>${StringUtil.wrapString(uiLabelMap.BACCVerified)}</span>
			</#if>
		</div>
	</#if>
	<div class="form-horizontal form-window-content-custom label-text-left content-description">
		<div class='row-fluid'>
			<div class="span5">
				<div class='row-fluid margin-bottom10'>
					<div class="span7 text-algin-right">
						<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueNotTaxInSystem)}</span>
					</div>
					<div class="span5">
						<div class="green-label" style="text-align: left;">
							<@ofbizCurrency amount=invoiceNoTaxTotal isoCode=invoice.currencyUomId/>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span7 text-algin-right">
						<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueNotTaxInActual)}</span>
					</div>
					<div class="span5">
						<div class="green-label" style="text-align: left;" id="actualTotalAmount">
						<#if voucherTotal?exists>
							<@ofbizCurrency amount=voucherTotal.amount isoCode=invoice.currencyUomId/>
						<#else>
							_______________
						</#if>
						</div>
					</div>
				</div>
				<#if voucherTotal?exists>
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right"><b>${StringUtil.wrapString(uiLabelMap.BACCDifference)}</b></span>
						</div>
						<div class="span5">
							<div class="green-label" style="text-align: left;">
								<#assign diffInvoiceAmount = invoiceNoTaxTotal - voucherTotal.amount/>
								<b id="diffTotalAnount"><@ofbizCurrency amount=diffInvoiceAmount isoCode=invoice.currencyUomId/></b>
								<#if voucherTotal?exists && businessType == 'AP' && (diffInvoiceAmount != 0) && (!invoice.isVerified?exists || invoice.isVerified != "Y")>
									&nbsp;
									<#--<!-- <a id="addInvoiceItemDiffBtn" tabindex="-1" href="javascript:void(0);" title="${uiLabelMap.BACCAddInvoiceItemForDiffValue}">
										<i class="fa fa-file-text-o blue"></i>
									</a> -->
								</#if>
							</div>
						</div>
					</div>
				</#if>
				<div class='row-fluid'>
					<div class="span7 text-algin-right">
						<span style="float: right"><i>${StringUtil.wrapString(uiLabelMap.BACCDifferenceValueAllow)}</i></span>
					</div>
					<div class="span5">
						<div class="green-label" style="text-align: left;">
							<i id="editInvVoucherDiffValueDiv">
								<#if invVoucherDiffValueConfig.systemValue?exists>
									<#assign diffValue = Static["java.lang.Double"].parseDouble(invVoucherDiffValueConfig.systemValue)/>
									<#if (diffValue > 0)>
										&le;&nbsp;<@ofbizCurrency amount=diffValue isoCode=invoice.currencyUomId/>
									<#else>
										<@ofbizCurrency amount=diffValue isoCode=invoice.currencyUomId/>	
									</#if>
								<#else>
									<@ofbizCurrency amount=0 isoCode=invoice.currencyUomId/>	
								</#if>
							</i>
							<#if hasAccGeneralRole && (!invoice.isVerified?exists || invoice.isVerified != "Y")>
								&nbsp;
								<a id="editInvVoucherDiffValueBtn" tabindex="-1" href="javascript:void(0);" title="${uiLabelMap.CommonEdit}">
									<i class="fa fa-pencil blue"></i>
								</a>
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid margin-bottom10'>
					<div class="span7 text-algin-right">
						<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueTaxInSystem)}</span>
					</div>
					<div class="span5">
						<div class="green-label" style="text-align: left;">
							<@ofbizCurrency amount=invoiceTaxTotal isoCode=invoice.currencyUomId/>
						</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span7 text-algin-right">
						<span style="float: right">${StringUtil.wrapString(uiLabelMap.InvoiceValueTaxInActual)}</span>
					</div>
					<div class="span5">
						<div class="green-label" style="text-align: left;" id="actualTaxAmount">
						<#if voucherTotal?exists>
							<@ofbizCurrency amount=voucherTotal.taxAmount isoCode=invoice.currencyUomId/>
						<#else>
							_______________
						</#if>
						</div>
					</div>
				</div>
				<#if voucherTotal?exists>
					<div class='row-fluid'>
						<div class="span7 text-algin-right">
							<span style="float: right"><b>${StringUtil.wrapString(uiLabelMap.BACCDifference)}</b></span>
						</div>
						<div class="span5">
							<div class="green-label" style="text-align: left;">
								<#assign diffInvoiceTaxAmount = invoiceTaxTotal - voucherTotal.taxAmount/>
								<b id="diffTaxAnount"><@ofbizCurrency amount=diffInvoiceTaxAmount isoCode=invoice.currencyUomId/></b>
							</div>
						</div>
					</div>
				</#if>
			</div>
		</div>
		<div class="hr hr8 hr-double hr-dotted"></div> 
	</div>
</div>
<!-- <div class="hr hr8 hr-double hr-dotted"></div> -->

<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.ListVouchers)/>
<#if !invoice.isVerified?exists || invoice.isVerified != "Y">
	<#assign addrow = "true">
	<#assign deleterow = "true">
	<#assign mouseRightMenu = "true">
<#else>	
	<#assign addrow = "false">
	<#assign deleterow = "false">
	<#assign mouseRightMenu = "false">
</#if>
<#if isExistVoucher == false && businessType == "AR">
    <#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BACCQuickCreate}@javascript: void(0);@quickCreateVoucherObj.open()">
    <@jqGrid dataField=datafieldVoucher columnlist=columnlistVoucher
    clearfilteringbutton="true" id="jqxVoucherList"
    width="100%" filterable="true"
    showtoolbar="true" jqGridMinimumLibEnable="false"
    editmode="dblclick" mouseRightMenu=mouseRightMenu contextMenuId="contextMenu" customcontrol1=customcontrol1
    alternativeAddPopup="addNewVoucherWindow" addrow=addrow addType="popup"
    deleterow=deleterow removeUrl="jqxGeneralServicer?jqaction=D&sname=removeInvoiceVoucher" deleteColumn="voucherId"
    url="jqxGeneralServicer?sname=JQGetListVoucherInvoice&invoiceId=${parameters.invoiceId}"
    customTitleProperties=customTitleProperties deletesuccessfunction="voucherInvoiceViewObj.deleteSuccess"
    functionAfterUpdate="voucherInvoiceViewObj.updateSuccess"/>
<#else>
    <@jqGrid dataField=datafieldVoucher columnlist=columnlistVoucher
    clearfilteringbutton="true" id="jqxVoucherList"
    width="100%" filterable="true"
    showtoolbar="true" jqGridMinimumLibEnable="false"
    editmode="dblclick" mouseRightMenu=mouseRightMenu contextMenuId="contextMenu"
    alternativeAddPopup="addNewVoucherWindow" addrow=addrow addType="popup"
    deleterow=deleterow removeUrl="jqxGeneralServicer?jqaction=D&sname=removeInvoiceVoucher" deleteColumn="voucherId"
    url="jqxGeneralServicer?sname=JQGetListVoucherInvoice&invoiceId=${parameters.invoiceId}"
    customTitleProperties=customTitleProperties deletesuccessfunction="voucherInvoiceViewObj.deleteSuccess"
    functionAfterUpdate="voucherInvoiceViewObj.updateSuccess"/>
</#if>
		
<div id='contextMenu' style="display:none">
	<ul>
	    <li id="edit"><i class="fa fa-edit"></i>${StringUtil.wrapString(uiLabelMap.CommonEdit)}</li>
	</ul>
</div>

<#if !invoice.isVerified?exists || invoice.isVerified != "Y">
    <#include "quickCreateVoucherInvoice.ftl"/>
    <#include "createVoucherInvoice.ftl"/>
	<#include "updateVoucherInvoice.ftl"/>
	<#include "verifyVoucherInvoice.ftl"/>
	<#include "voucherInvItemDiffValue.ftl"/>
</#if>
<#if hasAccGeneralRole && (!invoice.isVerified?exists || invoice.isVerified != "Y")>
	<#include "editInvVoucherDiffValueConfig.ftl"/>
</#if>
<script type="text/javascript" src="/accresources/js/invoice/voucherInvoiceView.js?v=0.0.1"></script>