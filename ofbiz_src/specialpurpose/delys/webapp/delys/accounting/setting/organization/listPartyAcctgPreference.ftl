<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpanel.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<#assign partyAccountingPreference = dispatcher.runSync("getPartyAccountingPreferences",{"userLogin" : userLogin,"organizationPartyId" : "${organizationPartyId}"}) !>
<#if partyAccountingPreference?exists && partyAccountingPreference?has_content>
	<#assign aggregatedPartyAcctgPreference = partyAccountingPreference.partyAccountingPreference !>
</#if>
<#assign PartyAcc = delegator.findOne("PartyAcctgPreference",{"partyId":"${partyId}"},false) !>
<style> 
	.custom{
		color : #037c07!important;
		font-size : 14px;
	}
	.icon-spin1{
		height : 0.8em !important;
	}
	.blue{
		color : rgb(47, 42, 255) !important;
	}
	.bigger{
		font-size : 300% !important;
	}
	.test{
		animation: 0.5s !important;
	}
</style>
<#if aggregatedPartyAcctgPreference?exists>
<div id="notification" style="display : none;"></div>
<div class="row-fluid">
	<div id="container" style="width : 100%;"></div>
	<div class="span12">
		<div class="span6">
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label>${uiLabelMap.AccountingOrganizationPartyId}</label>
				</div>
				<div class="span7">
					<label class="custom">${partyId?if_exists}</label>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="fiscalYearStartMonthLabel">${uiLabelMap.FormFieldTitle_fiscalYearStartMonth}</label>
				</div>
				<div class="span7">
					<div class="custom" id="fiscalYearStartMonth"></div>
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="fiscalYearStartDayLabel">${uiLabelMap.FormFieldTitle_fiscalYearStartDay}</label>
				</div>
				<div class="span7">
						<div class="custom" id="fiscalYearStartDay"></div>
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="taxFormIdLabel">${uiLabelMap.FormFieldTitle_taxFormId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="taxFormId"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="cogsMethodIdLabel">${uiLabelMap.FormFieldTitle_cogsMethodId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="cogsMethodId"></div>		
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="baseCurrencyUomIdLabel">${uiLabelMap.FormFieldTitle_baseCurrencyUomId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="baseCurrencyUomId"></div>	
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="invoiceSeqCustMethIdLabel">${uiLabelMap.invoiceSeqCustMethId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="invoiceSeqCustMethId"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="invoiceIdPrefixLabel">${uiLabelMap.FormFieldTitle_invoiceIdPrefix}</label>
				</div>
				<div class="span7">
					<#if !partyAcctgPreference?exists || !partyAcctgPreference?has_content>
						<input type="text" class="custom" id="invoiceIdPrefix"></input>	
					<#else>
						<div class="custom" id="invoiceIdPrefix"></div>
					</#if>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="lastInvoiceNumberLabel">${uiLabelMap.FormFieldTitle_lastInvoiceNumber}</label>
				</div>
				<div class="span7">
					<div class="custom" id="lastInvoiceNumber"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="lastInvoiceRestartDateLabel">${uiLabelMap.FormFieldTitle_lastInvoiceRestartDate}</label>
				</div>
				<div class="span7">
					<div class="custom" id="lastInvoiceRestartDate"></div>	
				</div>
			</div>	
		</div>
		<div class="span6">	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="quoteSeqCustMethIdLabel">${uiLabelMap.QuoteSeqCustMethId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="quoteSeqCustMethId"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="useInvoiceIdForReturnsLabel">${uiLabelMap.FormFieldTitle_useInvoiceIdForReturns}</label>
				</div>
				<div class="span7">
					<div class="custom" id="useInvoiceIdForReturns"></div>	
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="quoteIdPrefixLabel">${uiLabelMap.FormFieldTitle_quoteIdPrefix}</label>
				</div>
				<div class="span7">	
					<#if !partyAcctgPreference?exists || !partyAcctgPreference?has_content>
						<input type="text" class="custom" id="quoteIdPrefix"></input>	
					<#else>
						<div class="custom" id="quoteIdPrefix"></div>
					</#if>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="orderIdPrefixLabel">${uiLabelMap.FormFieldTitle_orderIdPrefix}</label>
				</div>
				<div class="span7">
					<#if !partyAcctgPreference?exists || !partyAcctgPreference?has_content>
						<input type="text" class="custom" id="orderIdPrefix"></input>	
					<#else>
						<div class="custom" id="orderIdPrefix"></div>
					</#if>
				</div>
			</div>	
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="lastQuoteNumberLabel">${uiLabelMap.FormFieldTitle_lastQuoteNumber}</label>
				</div>
				<div class="span7">
					<div class="custom" id="lastQuoteNumber"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="orderSeqCustMethIdLabel">${uiLabelMap.OrderSeqCustMethId}</label>
				</div>
				<div class="span7">
					<div  class="custom" id="orderSeqCustMethId"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="lastOrderNumberLabel">${uiLabelMap.FormFieldTitle_lastOrderNumber}</label>
				</div>
				<div class="span7">
					<div class="custom" id="lastOrderNumber"></div>
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="refundPaymentMethodIdLabel">${uiLabelMap.FormFieldTitle_refundPaymentMethodId}</label>
				</div>
				<div class="span7">
					<div   class="custom" id="refundPaymentMethodId"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="errorGlJournalIdLabel">${uiLabelMap.FormFieldTitle_errorGlJournalId}</label>
				</div>
				<div class="span7">
					<div  class="custom"  id="errorGlJournalId"></div>
				</div>
			</div>
		<#--	<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="oldInvoiceSequenceEnumIdLabel">${uiLabelMap.oldInvoiceSequenceEnumId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="oldInvoiceSequenceEnumId"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10">
				<div class="span5 align-right">
					<label id="oldOrderSequenceEnumIdLabel">${uiLabelMap.oldOrderSequenceEnumId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="oldOrderSequenceEnumId"></div>	
				</div>
			</div>
			<div class="row-fluid margin-bottom10" >
				<div class="span5 align-right">
					<label id="oldQuoteSequenceEnumIdLabel">${uiLabelMap.oldQuoteSequenceEnumId}</label>
				</div>
				<div class="span7">
					<div class="custom" id="oldQuoteSequenceEnumId"></div>	
				</div>
			</div> -->
		</div>				
	</div>
	<div class="span12" id="spinner" style="display:none;margin : -350px 0px 0px 500px;">
		<i class="icon-spinner speed  icon-spin  test blue bigger"></i><label class="blue">Loading...</label>
	</div>
	<div class="form-action" style="bottom : -10px;right : 80px;">
		<div class="row-fluid"> 
			<div class="span12">
				<#if !partyAcctgPreference?exists || !partyAcctgPreference?has_content>
					<button id="add" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonAdd}</button>
				<#else>
					<button id="update" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonUpdate}</button>
				</#if>
			</div>	
		</div>
	</div>
</div>
<script>
<#assign listInvSeqCus = delegator.findByAnd("CustomMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("customMethodTypeId","INVOICE_HOOK"),null,false) !>
<#assign listOrderSeqCus = delegator.findByAnd("CustomMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("customMethodTypeId","ORDER_HOOK"),null,false) !>
<#assign listQuoteSeqCus = delegator.findByAnd("CustomMethod",Static["org.ofbiz.base.util.UtilMisc"].toMap("customMethodTypeId","QUOTE_HOOK"),null,false) !>
var listInvSeqCus = [
			<#list listInvSeqCus as cus>
			{
				'customMethodId' : '${cus.customMethodId?if_exists}',
				'description' : '${cus.get('description',locale)}'
			},
			</#list>
		]
		
var listOrderSeqCus = [
			<#list listOrderSeqCus as cus>
			{
				'customMethodId' : '${cus.customMethodId?if_exists}',
				'description' : '${cus.get('description',locale)}'
			},
			</#list>
		]
var listQuoteSeqCus = [
			<#list listQuoteSeqCus as cus>
			{
				'customMethodId' : '${cus.customMethodId?if_exists}',
				'description' : '${cus.get('description',locale)}'
			},
			</#list>
		]				
		
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var action = (function(){
		var initDataMonth = function(){
			var dataMonth = [
					{
						'key' : 1,
						'description' : '${uiLabelMap.AccountingFiscalMonth01}'
					},
					{
						'key' : 2,
						'description' : '${uiLabelMap.AccountingFiscalMonth02}'
					},
					{
						'key' : 3,
						'description' : '${uiLabelMap.AccountingFiscalMonth03}'
					},
					{
						'key' : 4,
						'description' : '${uiLabelMap.AccountingFiscalMonth04}'
					},
					{
						'key' : 5,
						'description' : '${uiLabelMap.AccountingFiscalMonth05}'
					},
					{
						'key' : 6,
						'description' : '${uiLabelMap.AccountingFiscalMonth06}'
					},
					{
						'key' : 7,
						'description' : '${uiLabelMap.AccountingFiscalMonth07}'
					},
					{
						'key' : 8,
						'description' : '${uiLabelMap.AccountingFiscalMonth08}'
					},
					{
						'key' : 9,
						'description' : '${uiLabelMap.AccountingFiscalMonth09}'
					},
					{
						'key' : 10,
						'description' : '${uiLabelMap.AccountingFiscalMonth10}'
					},
					{
						'key' : 11,
						'description' : '${uiLabelMap.AccountingFiscalMonth11}'
					},
					{
						'key' : 12,
						'description' : '${uiLabelMap.AccountingFiscalMonth12}'
					}
				]
			return dataMonth;
		};
		var initDayList = function(){
			var dayList = [];
			for(var i = 1 ;i <= 31;i++){
				dayList.push({
					'key' : i
				})
			}
			return dayList;
		}
		
		
		var initElementForm = function(){
			$('#notification').jqxNotification({appendContainer : '#container',autoClose :true,opacity : 1,autoCloseDelay : 2000});
			$('#invoiceSeqCustMethId').jqxDropDownList({width : 250,height : 25,dropDownWidth : 350,autoDropDownHeight : true,displayMember : 'description',valueMember : 'customMethodId',source : listInvSeqCus,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if PartyAcc.invoiceSeqCustMethId?exists>
				$('#invoiceSeqCustMethId').jqxDropDownList('val','${PartyAcc.invoiceSeqCustMethId?if_exists}');
			</#if>
			$('#quoteSeqCustMethId').jqxDropDownList({width : 250,height : 25,dropDownWidth : 300,autoDropDownHeight : true,displayMember : 'description',valueMember : 'customMethodId',source : listQuoteSeqCus,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if PartyAcc.quoteSeqCustMethId?exists>
				$('#quoteSeqCustMethId').jqxDropDownList('val','${PartyAcc.quoteSeqCustMethId?if_exists}');
			</#if>
			$('#orderSeqCustMethId').jqxDropDownList({width : 250,height : 25,dropDownWidth : 300,autoDropDownHeight : true,displayMember : 'description',valueMember : 'customMethodId',source : listOrderSeqCus,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if PartyAcc.quoteSeqCustMethId?exists>
				$('#orderSeqCustMethId').jqxDropDownList('val','${PartyAcc.orderSeqCustMethId?if_exists}');
			</#if>
			
			<#if partyAcctgPreference?exists && partyAcctgPreference?has_content>
				$('#invoiceSeqCustMethId').jqxDropDownList('disabled',true);
				$('#quoteSeqCustMethId').jqxDropDownList('disabled',true);
				$('#orderSeqCustMethId').jqxDropDownList('disabled',true);
			</#if>
			
			<#if !partyAcctgPreference?exists || !partyAcctgPreference?has_content>
				<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.fiscalYearStartMonth?exists>
					$('#fiscalYearStartMonthLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#fiscalYearStartMonth').jqxDropDownList({width : 250,height : 25,displayMember : 'description',valueMember : 'key',source : initDataMonth(),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});	
				<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.fiscalYearStartDay?exists>
					$('#fiscalYearStartDayLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#fiscalYearStartDay').jqxDropDownList({width : 250,height : 25,filterable :true,displayMember : 'key',valueMember : 'key',source : initDayList(),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
				<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.taxFormId?exists>
					$('#taxFormIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
			$('#lastOrderNumber').jqxNumberInput({width : 250,height : 25,digits : 15,min : 0,max : 9999999999999,spinButtons : true,decimalDigits : 0});
			$('#lastQuoteNumber').jqxNumberInput({width : 250,height : 25,digits : 15,min : 0,max : 9999999999999,spinButtons : true,decimalDigits : 0});
			$('#taxFormId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',valueMember : 'enumId',autoDropDownHeight : true,
			source : (function(){
				<#assign listEnumTax = delegator.findByAnd("Enumeration",{"enumTypeId" : "TAX_FORMS"},["description"],false) !>	
					var enumList = [
						<#list listEnumTax as enum>
						{
							'enumId' : '${enum.enumId?if_exists}',
							'description' : '${StringUtil.wrapString(enum.description?default(''))}'
						},
						</#list>
					]
					return enumList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.cogsMethodId?exists>
					$('#cogsMethodIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#cogsMethodId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',valueMember : 'enumId',autoDropDownHeight : true,source : (function(){
				<#assign listEnumCogs = delegator.findByAnd("Enumeration",{"enumTypeId" : "COGS_METHODS"},["description"],false) !>	
				var enumList = [
						<#list listEnumCogs as enum>
						{
							'enumId' : '${enum.enumId?if_exists}',
							'description' : '${StringUtil.wrapString(enum.get('description',locale)?default(''))}'
						},
						</#list>
					]
					return enumList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.baseCurrencyUomId?exists>
					$('#baseCurrencyUomIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			
            
			$('#baseCurrencyUomId').jqxDropDownList({width : 250,filterable : true,height : 25,displayMember : 'description',valueMember : 'uomId',source : (function(){
				<#assign listCurrency = delegator.findByAnd("Uom",{"uomTypeId" : "CURRENCY_MEASURE"},["description"],false) !>	
				var currencyList = [
						<#list listCurrency as uom>
						{
							'uomId' : '${uom.uomId?if_exists}',
							'description' : "${StringUtil.wrapString(uom.description?default(''))}" +  '-' +  "${StringUtil.wrapString(uom.abbreviation?default(''))}"
						},
						</#list>
					]
					return currencyList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if defaultOrganizationPartyCurrencyUomId?exists>
				$('#baseCurrencyUomId').jqxDropDownList('val','${defaultOrganizationPartyCurrencyUomId}');
			</#if>
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.invoiceIdPrefix?exists>
					$('#invoiceIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#invoiceIdPrefix').jqxInput({width : 250,height : 25});
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldInvoiceSequenceEnumId?exists>
					$('#oldInvoiceSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#oldInvoiceSequenceEnumId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',autoDropDownHeight : true,valueMember : 'enumId',source : (function(){
				<#assign listinvoiceSeq = delegator.findByAnd("Enumeration",{"enumTypeId" : "INVOICE_SEQMD"},["description"],false) !>	
				var InvoiceSeqList = [
						<#list listinvoiceSeq as seq>
						{
							'enumId' : '${seq.enumId?if_exists}',
							'description' : '${StringUtil.wrapString(seq.description?default(''))}'
						},
						</#list>
					]
					return InvoiceSeqList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.useInvoiceIdForReturns?exists>
					$('#useInvoiceIdForReturnsLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#useInvoiceIdForReturns').jqxDropDownList({width : 250,height : 25,displayMember : 'description',autoDropDownHeight : true,valueMember : 'key',source : (function(){
				var listChoose = [
					{
						'key' : 'Y',
						'description' : '${uiLabelMap.CommonY}'
					},
					{
						'key' : 'N',
						'description' : '${uiLabelMap.CommonN}'
					}			
				]
				return listChoose;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.quoteIdPrefix?exists>
					$('#quoteIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#quoteIdPrefix').jqxInput({width : 250,height : 25});
			
        	<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldQuoteSequenceEnumId?exists>
					$('#oldQuoteSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
        	$('#oldQuoteSequenceEnumId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',valueMember : 'enumId',autoDropDownHeight : true,source : (function(){
				<#assign listinvoiceSeq = delegator.findByAnd("Enumeration",{"enumTypeId" : "QUOTE_SEQMD"},["description"],false) !>	
				var QuoteSeqList = [
						<#list listinvoiceSeq as seq>
						{
							'enumId' : '${seq.enumId?if_exists}',
							'description' : '${StringUtil.wrapString(seq.description?default(''))}'
						},
						</#list>
					];
					return QuoteSeqList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.orderIdPrefix?exists>
					$('#orderIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
			$('#orderIdPrefix').jqxInput({width : 250,height : 25});
			
			<#if aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldOrderSequenceEnumId?exists>
					$('#oldOrderSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
			</#if>
        	$('#oldOrderSequenceEnumId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',valueMember : 'enumId',autoDropDownHeight : true,source : (function(){
				<#assign listOrderSeqList = delegator.findByAnd("Enumeration",{"enumTypeId" : "ORDER_SEQMD"},["description"],false) !>	
				var OrderSeqList = [
						<#list listOrderSeqList as seq>
						{
							'enumId' : '${seq.enumId?if_exists}',
							'description' : '${StringUtil.wrapString(seq.description?default(''))}'
						},
						</#list>
					]
					return OrderSeqList;
			}()),placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			
			<#else>
				<#if partyAcctgPreference?exists && !partyAcctgPreference.fiscalYearStartMonth?exists && aggregatedPartyAcctgPreference.fiscalYearStartMonth?exists>
					$('#fiscalYearStartMonthLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#fiscalYearStartMonth').html('${aggregatedPartyAcctgPreference.fiscalYearStartMonth?default('')}');
	        	<#if partyAcctgPreference?exists && !partyAcctgPreference.fiscalYearStartDay?exists && aggregatedPartyAcctgPreference.fiscalYearStartDay?exists && aggregatedPartyAcctgPreference?exists>
						$('#fiscalYearStartDayLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
       			 $('#fiscalYearStartDay').html('${aggregatedPartyAcctgPreference.fiscalYearStartDay?default('')}');
       			 <#if partyAcctgPreference?exists && !partyAcctgPreference.taxFormId?exists && aggregatedPartyAcctgPreference.taxFormId?exists && aggregatedPartyAcctgPreference?exists>
						$('#taxFormIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				<#assign tax = delegator.findOne("Enumeration",{"enumId" : "${aggregatedPartyAcctgPreference.taxFormId?if_exists}"},false) !>	
				$('#taxFormId').html('${tax.description?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.cogsMethodId?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.cogsMethodId?exists>
				$('#cogsMethodIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				<#assign cogs = delegator.findOne("Enumeration",{"enumId" : "${aggregatedPartyAcctgPreference.cogsMethodId?if_exists}"},false) !>	
				<#if cogs?exists && cogs?has_content>
					$('#cogsMethodId').html('${cogs.get('description',locale)?default('')}');
				</#if>
				<#if partyAcctgPreference?exists && !partyAcctgPreference.baseCurrencyUomId?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.baseCurrencyUomId?exists>
				$('#baseCurrencyUomIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#baseCurrencyUomId').html('${aggregatedPartyAcctgPreference.baseCurrencyUomId?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.invoiceIdPrefix?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.invoiceIdPrefix?exists>
				$('#invoiceIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#invoiceIdPrefix').html('${aggregatedPartyAcctgPreference.invoiceIdPrefix?default('')}');
				 <#if partyAcctgPreference?exists && !partyAcctgPreference.oldInvoiceSequenceEnumId?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldInvoiceSequenceEnumId?exists>
				$('#oldInvoiceSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				<#assign oldInvoiceSeq = delegator.findOne("Enumeration",{"enumId" : "${aggregatedPartyAcctgPreference.oldInvoiceSequenceEnumId?if_exists}"},false) !>	
				$('#oldInvoiceSequenceEnumId').html('${oldInvoiceSeq.description?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.useInvoiceIdForReturns?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.useInvoiceIdForReturns?exists>
				$('#useInvoiceIdForReturnsLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#useInvoiceIdForReturns').html('${aggregatedPartyAcctgPreference.useInvoiceIdForReturns?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.oldQuoteSequenceEnumId?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldQuoteSequenceEnumId?exists>
				$('#oldQuoteSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				<#assign oldQuoteSeq = delegator.findOne("Enumeration",{"enumId" : "${aggregatedPartyAcctgPreference.oldQuoteSequenceEnumId?if_exists}"},false) !>	
				$('#oldQuoteSequenceEnumId').html('${oldQuoteSeq.description?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.quoteIdPrefix?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.quoteIdPrefix?exists>
				$('#quoteIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#quoteIdPrefix').html('${aggregatedPartyAcctgPreference.quoteIdPrefix?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.lastQuoteNumber?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.lastQuoteNumber?exists>
				$('#lastQuoteNumberLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#lastQuoteNumber').html('${aggregatedPartyAcctgPreference.lastQuoteNumber?default('')}');
				
				<#if partyAcctgPreference?exists && !partyAcctgPreference.oldOrderSequenceEnumId?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.oldOrderSequenceEnumId?exists>
				$('#oldOrderSequenceEnumIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				<#assign oldOrderSeq = delegator.findOne("Enumeration",{"enumId" : "${aggregatedPartyAcctgPreference.oldOrderSequenceEnumId?if_exists}"},false) !>	
				$('#oldOrderSequenceEnumId').html('${oldOrderSeq.description?default('')}');
				<#if partyAcctgPreference?exists && !partyAcctgPreference.orderIdPrefix?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.orderIdPrefix?exists>
				$('#orderIdPrefixLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#orderIdPrefix').html('${aggregatedPartyAcctgPreference.orderIdPrefix?default('')}');
				
				<#if partyAcctgPreference?exists && !partyAcctgPreference.lastOrderNumber?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.lastOrderNumber?exists>
				$('#lastOrderNumberLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
				</#if>
				$('#lastOrderNumber').html('${aggregatedPartyAcctgPreference.lastOrderNumber?default('')}');
			</#if>
			
	        <#if (!partyAcctgPreference?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.lastInvoiceNumber?exists) || 
	        	(partyAcctgPreference?exists && !partyAcctgPreference.lastInvoiceNumber?exists && aggregatedPartyAcctgPreference?exists) && aggregatedPartyAcctgPreference.lastInvoiceNumber?exists>	
	        	$('#lastInvoiceNumberLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
	        </#if>
	        $('#lastInvoiceNumber').html('${aggregatedPartyAcctgPreference.lastInvoiceNumber?default('')}');
	        
			<#if (!partyAcctgPreference?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.lastInvoiceRestartDate?exists) || 
	        	(partyAcctgPreference?exists && !partyAcctgPreference.lastInvoiceRestartDate?exists && aggregatedPartyAcctgPreference?exists) && aggregatedPartyAcctgPreference.lastInvoiceRestartDate?exists>	
	        	$('#lastInvoiceRestartDateLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
	        </#if>
	        $('#lastInvoiceRestartDate').html('${aggregatedPartyAcctgPreference.lastInvoiceRestartDate?default('')}');
	        
			<#if (!partyAcctgPreference?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.refundPaymentMethodId?exists) || 
	        	(partyAcctgPreference?exists && !partyAcctgPreference.refundPaymentMethodId?exists && aggregatedPartyAcctgPreference?exists) && aggregatedPartyAcctgPreference.refundPaymentMethodId?exists>	
	        	$('#refundPaymentMethodIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
	        </#if>
	        $('#refundPaymentMethodId').jqxDropDownList({filterable : true,width : 250,height : 25,displayMember : 'description',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',valueMember : 'paymentMethodId',source : (function(){
				<#assign listPayment = delegator.findByAnd("PaymentMethod",{"partyId" : "${organizationPartyId}"},null,false) !>
				var refundPaymentMethod = [
						<#list listPayment as seq>
						{
							'paymentMethodId' : '${seq.paymentMethodId?if_exists}',
							'description' : '${StringUtil.wrapString(seq.description?default(''))}'
						},
						</#list>
					]
					return refundPaymentMethod;
			}())});
			$('#refundPaymentMethodId').jqxDropDownList('val','${aggregatedPartyAcctgPreference.refundPaymentMethodId?if_exists}');
			<#if (!partyAcctgPreference?exists && aggregatedPartyAcctgPreference?exists && aggregatedPartyAcctgPreference.errorGlJournalId?exists) || 
	        	(partyAcctgPreference?exists && !partyAcctgPreference.errorGlJournalId?exists && aggregatedPartyAcctgPreference?exists) && aggregatedPartyAcctgPreference.errorGlJournalId?exists>	
	        	$('#errorGlJournalIdLabel').jqxTooltip({theme : theme,content : '${uiLabelMap.AccountingInheritedValue}',width : 200,height : 30,position : 'top'});
	        </#if>
	        $('#errorGlJournalId').jqxDropDownList({width : 250,height : 25,displayMember : 'description',autoDropDownHeight : true,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}',valueMember : 'glJournalId',source : (function(){
				<#assign errorGlJournal = delegator.findByAnd("GlJournal",{"organizationPartyId" : "${organizationPartyId}"},["glJournalName"],false) !>	
				var errorGlJournal = [
						<#list errorGlJournal as seq>
						{
							'glJournalId' : '${seq.glJournalId?if_exists}',
							'description' : '${StringUtil.wrapString(seq.glJournalName?default(''))} ${StringUtil.wrapString(seq.glJournalId?default(''))}'
						},
						</#list>
					]
					return errorGlJournal;
			}())});
			 $('#errorGlJournalId').jqxDropDownList('val','${aggregatedPartyAcctgPreference.errorGlJournalId?if_exists}');
		}
		
		var bindEvent = function(){
			$('#add').click(function(){
				addPartyAcctgPreference();
			});
			
			$('#update').click(function(){
				updatePartyAcctgPreference();
			});
		}
		
		var clear = function(){
			$('#fiscalYearStartMonth').jqxDropDownList('clearSelection');
			$('#fiscalYearStartDay').jqxDropDownList('clearSelection');
			$('#taxFormId').jqxDropDownList('clearSelection');
			$('#cogsMethodId').jqxDropDownList('clearSelection');
			$('#baseCurrencyUomId').jqxDropDownList('clearSelection');
			$('#invoiceSeqCustMethId').jqxDropDownList('clearSelection');
			$('#quoteSeqCustMethId').jqxDropDownList('clearSelection');
			$('#orderSeqCustMethId').jqxDropDownList('clearSelection');
			$('#invoiceIdPrefix').jqxInput('val','');
			$('#useInvoiceIdForReturns').jqxDropDownList('clearSelection');
			$('#quoteIdPrefix').jqxInput('val','');
			$('#lastQuoteNumber').jqxNumberInput('clear');
			$('#refundPaymentMethodId').jqxDropDownList('clearSelection');
			$('#errorGlJournalId').jqxDropDownList('clearSelection');
			<#--	$('#oldInvoiceSequenceEnumId').jqxDropDownList('clearSelection');
			$('#oldOrderSequenceEnumId').jqxDropDownList('clearSelection');
			$('#oldQuoteSequenceEnumId').jqxDropDownList('clearSelection'); -->
		}
					
		var getData = function(){
			var data = {
				partyId:'${partyId?if_exists}',
				lastInvoiceNumber : (parseInt($('#lastInvoiceNumber').html()) == null || isNaN(parseInt($('#lastInvoiceNumber').html())))?null : parseInt($('#lastInvoiceNumber').html()),
				lastInvoiceRestartDate : (!$('#lastInvoiceRestartDate').html())? null : parseInt($('#lastInvoiceRestartDate').html()),
				organizationPartyId: '${organizationPartyId?if_exists}',
				fiscalYearStartMonth : $('#fiscalYearStartMonth').jqxDropDownList('val'),
				fiscalYearStartDay : $('#fiscalYearStartDay').jqxDropDownList('val'),
				taxFormId: $('#taxFormId').jqxDropDownList('val'),
				cogsMethodId:$('#cogsMethodId').jqxDropDownList('val'),
				baseCurrencyUomId:$('#baseCurrencyUomId').jqxDropDownList('val'),
				invoiceSeqCustMethId : (!$('#invoiceSeqCustMethId').jqxDropDownList('val')) ? "" : $('#invoiceSeqCustMethId').jqxDropDownList('val'),
				invoiceIdPrefix:  (!$('#invoiceIdPrefix').jqxInput('val')) ? "" : $('#invoiceIdPrefix').jqxInput('val'),
				useInvoiceIdForReturns: $('#useInvoiceIdForReturns').jqxDropDownList('val'),
				quoteSeqCustMethId : $('#quoteSeqCustMethId').jqxDropDownList('val') ? $('#quoteSeqCustMethId').jqxDropDownList('val') : '',
				quoteIdPrefix : $('#quoteIdPrefix').jqxInput('val'),
				lastQuoteNumber : $('#lastQuoteNumber').jqxNumberInput('val'),
				orderSeqCustMethId :  $('#orderSeqCustMethId').jqxDropDownList('val') ? $('#orderSeqCustMethId').jqxDropDownList('val') : '',
				orderIdPrefix :  $('#orderIdPrefix').jqxInput('val'),
				lastOrderNumber : $('#lastOrderNumber').jqxNumberInput('val'),
				refundPaymentMethodId: $('#refundPaymentMethodId').jqxDropDownList('val'),
				errorGlJournalId : $('#errorGlJournalId').jqxDropDownList('val')
			<#--	oldInvoiceSequenceEnumId:$('#oldInvoiceSequenceEnumId').jqxDropDownList('val'),
				oldOrderSequenceEnumId:$('#oldOrderSequenceEnumId').jqxDropDownList('val'),
				oldQuoteSequenceEnumId:$('#oldQuoteSequenceEnumId').jqxDropDownList('val') -->
			}
			return data;
		}
								
		var addPartyAcctgPreference = function(){
			sendRequest(getData(),'createPartyAcctgPreferenceJSON',{async : false,cache : false,clear : true,action : 'create'});
		}
		
		var updatePartyAcctgPreference = function(){
			sendRequest(getData(),'updatePartyAcctgPreferenceJSON',{async : false,cache : false,action : 'update'});
		}
		
		
		var sendRequest = function(data,url,config){
			if(data && url){
				$.ajax({
					url : url,
					data : data,
					type : "POST",
					datatype : "json",
					async : config.async ? config.async : false,
					cache : config.cache ? config.cache : false,
					beforeSend : function(){
						$('#spinner').css('display','block');
					},
					success : function(response,xhr,status){
						setTimeout(function(){
							$('#spinner').css('display','none');
							if(response._ERROR_MESSAGE_LIST_){
								$('#notification').empty();		
								$('#notification').jqxNotification({template : 'error'});
								$('#notification').text(response._ERROR_MESSAGE_LIST_);
								$('#notification').jqxNotification('open');
							}else{
								if(config.clear){
										clear();
									}
								$('#notification').empty();
								$('#notification').jqxNotification({template : 'success',icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}});
								if(config.action == 'update'){
									$('#notification').text('${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
								}else if (config.action == 'create'){
									$('#notification').text('${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}');
									window.location.reload();
								}
								$('#notification').jqxNotification('open');
							}	
						},700);
					},	
					error : function(error){
						$('#spinner').css('display','none');
						$('#notification').jqxNotification({template : 'error'});
						$('#notification').text(error);
						$('#notification').jqxNotification('open');
					}
				})		
			}
		}
		
		return {
			init : function(){
				initElementForm();
				bindEvent();
			}
		}
	
	}());
	

	$(document).ready(function(){
		action.init();
	});
</script>

</#if>