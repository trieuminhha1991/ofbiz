<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script>
var codes = [
	<#list formulaList as formula>
		{
			code: "${formula.code}",
			name: "${StringUtil.wrapString(formula.name?default(''))}"
		},
	</#list>
];
var uiLabelMap = {
		Department: "${StringUtil.wrapString(uiLabelMap.Department)}",	
		CommonThruDate: "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}",
		fromDate: "${StringUtil.wrapString(uiLabelMap.fromDate)}",
		AccountingInvoiceItemType: "${StringUtil.wrapString(uiLabelMap.AccountingInvoiceItemType)}",	
		FormulaCode: "${StringUtil.wrapString(uiLabelMap.FormulaCode)}",	
		filterselectallstring: "${StringUtil.wrapString(uiLabelMap.filterselectallstring)}",	
		Department: "${StringUtil.wrapString(uiLabelMap.Department)}",	
};

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

var globalVar = {
	<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
    	editable: true,
    <#else>
		editable: false,
    </#if>
	rootPartyArr: [
		<#if rootOrgList?has_content>
			<#list rootOrgList as rootOrgId>
			<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
			{
				partyId: "${rootOrgId}",
				partyName: "${rootOrg.groupName}"
			},
			</#list>
		</#if>
	],
}

var globalObject = (function(){
	<#assign defaultSuffix = ""/>
	${setContextField("defaultSuffix", defaultSuffix)}
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

</script>
<script type="text/javascript" src="/hrresources/js/payroll/ViewListPartyFormulaIIType.js"></script>

<style>
.backgroundWhiteColor{
	background-color: #fff !important
}
</style>

<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.ListPartyFormulaInvoiceItemType}</h4>
		<div class="widget-toolbar none-content" >
			<div class="row-fluid">
				<div class="span12">
					<div class="span5" style="margin: 0; padding: 0">
						<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
							<button id="deleteBtn" class="grid-action-button icon-trash open-sans" style="float: right; font-size: 14px">${uiLabelMap.accDeleteSelectedRow}</button>
							<button id="addNewBtn" class="grid-action-button icon-plus open-sans" style="float: right; font-size: 14px">${uiLabelMap.accAddNewRow}</button>
						</#if>
					</div>
					<div class="span7" style="margin: 0; padding: 0">
						<div id="searchDropDownButton" class="">
							<div style="border: none;" id="searchJqxTree">
							</div>
						</div>
					</div>
				</div>
			</div>
				
			
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div id="jqxDataTable"></div>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>	
	<#include "CreatePartyFormulaIIType.ftl"/>
</#if>