<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign listUom = delegator.findList("Uom",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId","CURRENCY_MEASURE"),null,["description"],null,false) !>
<#assign listStt = delegator.findList("InvoiceType",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("invoiceTypeId","SALES_INVOICE"),null,null,null,false)!>
<#if invoice?exists && invoice.invoiceTypeId == 'SALES_INVOICE'>
	<#assign listRole = delegator.findByAnd("RoleType",{"parentTypeId" : "CUSTOMER"},Static["org.ofbiz.base.util.UtilMisc"].toList("description DESC"),false) !>
</#if>
<#if invoice?exists && invoice.invoiceTypeId == 'PURCHASE_INVOICE'>
	<#assign listRole = delegator.findByAnd("RoleType",{"parentTypeId" : "VENDOR"},Static["org.ofbiz.base.util.UtilMisc"].toList("description DESC"),false) !>
</#if>
<script type="text/javascript">
	 var listU  = [
		<#list listUom as uom>
			{
				uomId : "${uom.uomId?if_exists}",
				description : "${StringUtil.wrapString(uom.description?if_exists)} - ${StringUtil.wrapString(uom.abbreviation?if_exists)}"
			},
		</#list>
		];
	var listInvoiceType = [
		<#list listStt as stt>
			{
				invoiceTypeId : '${stt.invoiceTypeId?if_exists}',
				description : '${StringUtil.wrapString(stt.get("description",locale)?if_exists)}'
			},
		</#list>
	]
	<#if invoice?exists && (invoice.invoiceTypeId == 'SALES_INVOICE' || invoice.invoiceTypeId == 'PURCHASE_INVOICE')>
		var listRole = 	[
		<#list listRole as role>
			{
				roleTypeId : '${role.roleTypeId?if_exists}',
				description : '${StringUtil.wrapString(role.get("description",locale)?if_exists)}'
			},
		</#list>
	]
	</#if>
</script>
<style> 
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
<div id="notification" style="width : 100%;"></div>
<div class="widget-box transparent no-bottom-border">
	<div ></div>
	<div id="container"></div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
			<form name="editInvoice" id="editInvoiceId">	
				<input type="hidden" name="invoiceId" value="${invoice.invoiceId?if_exists}"/>
					<div class="row-fluid">
						<div class="span6">
							<div  class="row-fluid margin-bottom10">
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_invoiceDate}
								</div>
								<div class="span7" >
									<div id="invoiceDate"></div>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_dueDate}
								</div>
								<div class="span7" >
									<div id="duaDate"></div>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_description}
								</div>
								<div class="span7" >
									<input id="description" name="description" type="text"></input>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.accAccountingToParty}
								</div>
								<div class="span7" style="color:red;font-size:14px;">
									<#if invoiceType.parentTypeId == "SALES_INVOICE">
										${invoice.partyIdFrom?if_exists}
										<input type="hidden" name="partyIdFrom" value="${invoice.partyIdFrom?if_exists}"/>
									</#if>
									<#if invoiceType.parentTypeId == "PURCHASE_INVOICE">
										<@htmlTemplate.lookupField name="partyIdFrom" id="partyIdFrom" value='${invoice.partyIdFrom?if_exists}' width="1150"
											formName="editInvoice" fieldFormName="LookupJQPartyName" title="${uiLabelMap.DALookupCustomer}"/>
									</#if>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.accAccountingFromParty}
								</div>
								<div class="span7" >
									<#if invoiceType.parentTypeId == "PURCHASE_INVOICE">
										<input name="partyIdTo" value="${invoice.partyId?if_exists}" type="hidden"/>
										${invoice.partyId?if_exists}
									</#if>
									<#if invoiceType.parentTypeId == "SALES_INVOICE">
										<@htmlTemplate.lookupField name="partyIdTo" id="partyIdTo" value='${invoice.partyId?if_exists}' width="1150"
										formName="editInvoice" fieldFormName="LookupJQPartyName" title="${uiLabelMap.CommonSearch} ${uiLabelMap.accAccountingFromParty}"/>
									</#if>
								</div>
								<#if invoice?exists && (invoice.invoiceTypeId == 'SALES_INVOICE' || invoice.invoiceTypeId == 'PURCHASE_INVOICE')>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_roleTypeId}
								</div>
								<div class="span7" >
									<div id="roleTypeId"></div>
								</div>
								</#if>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_invoiceTypeId}
								</div>
								<div class="span7" >
									<#if invoice?exists>
										<#assign ivType = delegator.findOne("InvoiceType",{"invoiceTypeId" : "${invoice.invoiceTypeId?if_exists}"},false)!>
										${StringUtil.wrapString(ivType.get("description",locale)?if_exists)}
										<input type="hidden" id="invoiceTypeId" value="${invoice.invoiceTypeId?if_exists}"/>
									<#else>
										<div id="invoiceTypeId"/>
									</#if>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.CommonStatus}
								</div>
								<div class="span7" style="color:red;">
									<#if invoice?exists>
										<#assign stt = delegator.findOne("StatusItem",{"statusId" : "${invoice.statusId?if_exists}"},false)!>		
										${stt.get("description",locale)?if_exists}
										<input name="statusId" value="${invoice.statusId?if_exists}" type="hidden"/>
									<#else>	
										<input name="statusId" value="INVOICE_IN_PROCESS" type="hidden"/>
									</#if>
								</div>
							</div>
						</div>
						<div class="span6">
							<div  class="row-fluid margin-bottom10">
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_billingAccountId}
								</div>
								<div class="span7" >
									<@htmlTemplate.lookupField name="billingAccountId" id="billingAccountId" value='${invoice.billingAccountId?if_exists}' width="1150"
											formName="editInvoice" fieldFormName="LookupBillingAccount" title="${uiLabelMap.CommonSearch} ${uiLabelMap.FormFieldTitle_billingAccountId}"/>
								</div>
							</div>
							<div class="row-fluid margin-bottom10" >	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_currencyUomId}
								</div>
								<div class="span7" >
									<div id="currencyUomId"></div>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_recurrenceInfoId}
								</div>
								<div class="span7" >
									<input type="text" id="recurrenceInfoId" value="${invoice.recurrenceInfoId?if_exists}"></input>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">	
								<div class="span5 align-right">
									${uiLabelMap.FormFieldTitle_invoiceMessage}
								</div>
								<div class="span7" >
									<input type="text" id="invoiceMessage" value="${invoice.invoiceMessage?if_exists}"></input>
								</div>
							</div>
							<div class="row-fluid margin-bottom10">
								<div class="span5 align-right">
									${uiLabelMap.AccountingReferenceNumber}
								</div>
								<div class="span7" >
									<input type="text" id="referenceNumber" value="${invoice.referenceNumber?if_exists}"></input>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<div class="span12"><div class="center"><button type="submit" class="btn btn-primary btn-small" id="updateButton" name="updateButton"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button></div></div>
		</div>
	</div>			
</div>			
<div id="spinner" style="display:none;margin : -200px 0px 0px 500px;">
		<i class="icon-spinner speed  icon-spin  test blue bigger"></i><label class="blue">Loading...</label>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var sortListCurrency = function(array,element){
		for(var key in array){
			if(array[key].uomId == element){
				var tmp  = {
					uomId : array[key].uomId,
					description : array[key].description
				};
				array.splice(1,key);
				array.unshift(tmp);
				break;
			}
		}	
		return array;
	}
	var prepareListCurrency = function(){
		<#assign currency = delegator.findOne("Uom",{"uomId" : "${invoice.currencyUomId}"},false) !>
		sortListCurrency(listU,'${currency.uomId?if_exists}');
	}
	var action = (function(){
		var initElement = function(){
			prepareListCurrency();
			$('#currencyUomId').jqxDropDownList({theme : theme,filterable : true,source : listU,selectedIndex : 0,displayMember : 'description',valueMember : 'uomId',width : '220px',height : '25px'});
			<#if invoice.invoiceDate?exists && invoice.invoiceDate?has_content>
				$('#invoiceDate').jqxDateTimeInput({theme : theme,width : '220px',height : '25px',allowNullDate : true,value : '${invoice.invoiceDate?if_exists}',formatString : 'dd/MM/yyyy hh:mm:ss'});	
			<#else>
				$('#invoiceDate').jqxDateTimeInput({theme : theme,width : '220px',height : '25px',allowNullDate : true,formatString : 'dd/MM/yyyy hh:mm:ss',value : null});
			</#if>
			
			<#if invoice.dueDate?exists && invoice.dueDate?has_content>
				$('#duaDate').jqxDateTimeInput({theme : theme,width : '220px',height : '25px',allowNullDate : true,value : '${invoice.dueDate?if_exists}',formatString : 'dd/MM/yyyy hh:mm:ss'});	
			<#else>
				$('#duaDate').jqxDateTimeInput({theme : theme,width : '220px',height : '25px',allowNullDate : true,formatString : 'dd/MM/yyyy hh:mm:ss',value : null});
			</#if>
			<#if invoice?exists>
			<#else>
				$('#invoiceTypeId').jqxDropDownList({theme : theme,filterable : true,source : listInvoiceType,displayMember : 'description',valueMember : 'invoiceTypeId',width : '220px',height : '25px'});				
			</#if>
			$('#recurrenceInfoId').jqxInput({width : '220px',height : '25px'});
			$('#invoiceMessage').jqxInput({width : '220px',height : '25px'});
			$('#referenceNumber').jqxInput({width : '220px',height : '25px'});
			<#if invoice.description?exists && invoice.description?has_content>
				$('#description').jqxInput({width : '220px',height : '25px',value : '${StringUtil.wrapString(invoice.description?if_exists)}'});	
			<#else>
				$('#description').jqxInput({width : '220px',height : '25px',value : null});
			</#if>
			$('#notification').jqxNotification({appendContainer : '#container',autoClose :true,opacity : 1,autoCloseDelay : 2000});
			<#if invoice?exists&& (invoice.invoiceTypeId == 'SALES_INVOICE' || invoice.invoiceTypeId == 'PURCHASE_INVOICE')>
				$('#roleTypeId').jqxDropDownList({theme : theme,filterable : true,source : listRole,displayMember : 'description',valueMember : 'roleTypeId',width : '225px',height : '25px',placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			</#if>
		}
		var formatDate = function(val){
			var date = new Date(val);
			var newFormat;
			if(date){
				newFormat = date.format('yyyy-mm-dd hh:MM:ss');
			}
			return newFormat;
		}
		var update = function(){
		if(!$('#editInvoiceId').jqxValidator('validate')){return;}
			var time,timeDue;
			if($('#invoiceDate').jqxDateTimeInput('getDate') != null && typeof($('#invoiceDate').jqxDateTimeInput('getDate')) != 'undefined'){
				time = formatDate($('#invoiceDate').jqxDateTimeInput('getDate').getTime());
			}
			if($('#duaDate').jqxDateTimeInput('getDate') != null && typeof($('#duaDate').jqxDateTimeInput('getDate')) != 'undefined'){
				timeDue = formatDate($('#duaDate').jqxDateTimeInput('getDate').getTime());
			}
			var row = {};
			row = {
				invoiceId : '${invoice.invoiceId?if_exists}',
				<#if invoice?exists>
				invoiceTypeId: '${invoice.invoiceTypeId?if_exists}',
				<#else>
				invoiceTypeId : $('#invoiceTypeId').jqxDropDownList('val'),
				</#if>
				currencyUomId : $('#currencyUomId').jqxDropDownList('val'),
				invoiceDate : time,
				dueDate : timeDue,
				recurrenceInfoId : $('#recurrenceInfoId').val(),
				invoiceMessage : $('#invoiceMessage').val(),
				referenceNumber : $('#referenceNumber').val(),
				description : $('#description').val(),
				partyIdFrom : $('input[name=partyIdFrom]').val(),
				partyId :  $('input[name=partyIdTo]').val(),
				<#if invoice?exists>
				statusId :  '${invoice.statusId?if_exists}',
				<#else>
				statusId : 'INVOICE_IN_PROCESS',
				</#if>
				billingAccountId : $('input[name=billingAccountId]').val(),
				<#if invoice?exists && (invoice.invoiceTypeId == 'SALES_INVOICE' || invoice.invoiceTypeId == 'PURCHASE_INVOICE')>
				roleTypeId : $('#roleTypeId').jqxDropDownList('val')
				</#if>
			};
			
				if(row){
					$.ajax({
						<#if invoice?exists> 
						url : 'accArupdateInvoiceJSON', 
						</#if>
						 <#if !invoice?exists>
						  url : 'accArcreateInvoiceJSON', 
						  </#if>
						data : row,
						dataType : 'json',
						type : 'POST',
						async : false,
						cache : false,
						beforeSend : function(){
							$('#spinner').css('display','block');	
						},
						success : function(response){
							setTimeout(function(){
							$('#spinner').css('display','none');	
								if(response._ERROR_MESSAGE_LIST_){
									$('#notification').jqxNotification({template : 'error'});
									$('#notification').text(response._ERROR_MESSAGE_LIST_);
									$('#notification').jqxNotification('open');
								}else{
									$('#notification').jqxNotification({template : 'success'});
									$('#notification').text('${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}');
									$('#notification').jqxNotification('open');
								}	
							
							},700);
						},
						error : function(err){
							$('#notification').text(err);
							$('#notification').jqxNotification('open');
						}
					});
					
				}else return;
						
		}
		
		var initRules = function(){
			$('#editInvoiceId').jqxValidator({
				rules : [
					{ input: '#duaDate', message: '${StringUtil.wrapString(uiLabelMap.DateRequired?default(''))}', action: 'change,close', rule: function(){
						var val = $('#duaDate').jqxDateTimeInput('getDate');
						var valInvoiceDate = $('#invoiceDate').jqxDateTimeInput('getDate'); 
						if(val >= valInvoiceDate || val == null || !val){
							return true;
						}
						return false;
					} }
				]
			})
		
		}
		
		var clearForm = function(){
			$('#invoiceTypeId').val('');
			$('#currencyUomId').jqxDropDownList('val',null);
			$('#invoiceDate').jqxDateTimeInput('val',null);
			$('#duaDate').jqxDateTimeInput('val',null);
			$('#recurrenceInfoId').val(''),
			$('#invoiceMessage').val(''),
			$('#referenceNumber').val(''),
			$('#description').val(''),
			$('input[name=billingAccountId]').val('')
		}
		
		var bindEvent = function(){
			$('#updateButton').click(function(){
				if(!action.update()){
					return;
				}else {
					action.update();
				}
			});
		}
		
		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			},
			update : update,
		}
	}())	

$(document).ready(function(){
	action.init();
});
</script>