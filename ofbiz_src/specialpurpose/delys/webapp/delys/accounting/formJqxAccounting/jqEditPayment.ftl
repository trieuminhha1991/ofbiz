<#if parameters.paymentId?exists>
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
<#assign typeOfPayment = Static["com.olbius.accounting.utils.AccountingPaymentUtils"].checkPaymentType(delegator,"${paymentId?if_exists}")!>
<#assign isDisbursement = Static["org.ofbiz.accounting.util.UtilAccounting"].isDisbursement(payment) />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#if isDisbursement>
	<#assign parentTypeCond = "DISBURSEMENT" />
	<#assign partyIdCond  = "${payment.partyIdFrom?if_exists}"/>
<#else>
	<#assign partyIdCond  = "${payment.partyIdTo?if_exists}"/>
	<#assign parentTypeCond= "RECEIPT" />	
</#if>
<#assign listPaymentMethod = delegator.findList("PaymentMethod",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("partyId",partyIdCond),null,["paymentMethodTypeId"],null,false)!>
<#assign listPaymentType = delegator.findList("PaymentType",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId",parentTypeCond),null,["description"],null,false)!>
<#assign listCurrency = delegator.findList("Uom",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId","CURRENCY_MEASURE"),null,["description"],null,false)!>
<#assign listGlAccountType = delegator.findByAnd("GlAccountType",null,null,false) !>
<#assign listGlAccountClass = delegator.findByAnd("GlAccountClass",null,null,false) !>
<#assign listPartyType = delegator.findByAnd("PartyType",null,null,false) !>
<script>
	var listPartyType = [
	<#if listPartyType?exists && listPartyType?has_content>
		<#list listPartyType as type>
		<#assign des = type.get("description",locale) !>
			{
				partyTypeId : '${type.partyTypeId?if_exists}',
				description : "${StringUtil.wrapString(des?default(''))}"
			},
		</#list>
	</#if>	
	];
	
	var listGlAccountType = [
	<#if listGlAccountType?exists && listGlAccountType?has_content>
		<#list listGlAccountType as accountType>
		<#assign des = accountType.get("description",locale) !>
			{
				glAccountTypeId : '${accountType.glAccountTypeId?if_exists}',
				description : "${StringUtil.wrapString(des?default(''))}"
			},
		</#list>
	</#if>	
	];
	
	var listGlAccountClass = [
	<#if listGlAccountClass?exists && listGlAccountClass?has_content>
		<#list listGlAccountClass as accountClass>
		<#assign des = accountClass.get("description",locale) !>
			{
				glAccountClassId : '${accountClass.glAccountClassId?if_exists}',
				description : "${StringUtil.wrapString(des?default(''))}"
			},
		</#list>
	</#if>	
	];
	
	var listPaymentType = [
	<#if listPaymentType?exists && listPaymentType?has_content>
		<#list listPaymentType as paymentType>
		<#assign des = paymentType.get("description",locale) !>
			{
				paymentTypeId : '${paymentType.paymentTypeId?if_exists}',
				description : "${StringUtil.wrapString(des?default(""))}"
			},
		</#list>
	</#if>	
	];
	var listPaymentMethod = [
	<#if listPaymentMethod?exists && listPaymentMethod?has_content>
		<#list listPaymentMethod as paymentMethod>
			{
				paymentMethodId : '${paymentMethod.paymentMethodId?if_exists}',
				description : '${StringUtil.wrapString(paymentMethod.paymentMethodTypeId?default(''))} ' + '(' +"${StringUtil.wrapString(paymentMethod.get('description',locale)?default(''))}" +') '
			},
		</#list>
	</#if>	
	]
	<#assign paymentMt = '${payment.paymentMethodId?if_exists}'/>
	<#if paymentMt?exists && paymentMt?has_content && !listPaymentMethod?has_content>
		<#assign pm = delegator.findOne("PaymentMethod",{"paymentMethodId" : "${paymentMt?if_exists}"},false) !>
		listPaymentMethod.unshift({
			paymentMethodId : '${paymentMt?if_exists}',
			description : '${StringUtil.wrapString(pm.paymentMethodTypeId?default(''))} ' + '(' +'${StringUtil.wrapString(pm.paymentMethodId?default(''))}' +') '
		});
	</#if>
	var listCurrency = [
	<#if listCurrency?exists && listCurrency?has_content>
		<#list listCurrency as curr>
			{
				currencyUomId : '${curr.uomId?if_exists}',
				description : "${StringUtil.wrapString(curr.description?default(''))}" + "-" +"${StringUtil.wrapString(curr.abbreviation?default(''))}"
			},
		</#list>
	</#if>	
	];
	
</script>

<@jqGridMinimumLib/>
<div class="tab-pane" id="payment-header">
	<div class="span12">
		<div class="row-fluid margin-bottom10">
			<div class="span6 ">
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right">${uiLabelMap.AccountingPaymentType}</div>
					<div class="span7">
						<div id="paymentTypeId">
						</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_statusId}
					</div>
					<div class="span7">
						<#assign listStt = delegator.findByAnd("StatusItem",null,null,false) !> 
						<#list listStt as stt>
							<#if stt.statusId == '${payment.statusId?if_exists}'>
								${stt.get('description',locale)?if_exists}
							</#if>
						</#list>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						${uiLabelMap.AccountingPaymentMethodId}
					</div>
					<div class="span7">
						<div id="paymentMethodId"></div>
					</div>
				</div>	
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						<#if typeOfPayment == "PAYMENT_IS_AR">
							${uiLabelMap.accAccountingToParty}
						</#if>
						<#if typeOfPayment == "PAYMENT_IS_AP">
						${uiLabelMap.accAccountingFromParty}
						</#if>
					</div>
					<div class="span7">
						<#if typeOfPayment == "PAYMENT_IS_AR">
						<div id="dropdownPartyFrom">
							<div id="jqxgridPartyFrom"></div>
						</div>
						</#if>
						<#if typeOfPayment == "PAYMENT_IS_AP">
						<div id="dropdownPartyTo">
							<div id="jqxgridPartyTo"></div>
						</div>
						</#if>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						<#if typeOfPayment == "PAYMENT_IS_AR">
							${uiLabelMap.accAccountingFromParty}
						</#if>
						<#if typeOfPayment == "PAYMENT_IS_AP">
						${uiLabelMap.accAccountingToParty}
						</#if>
					</div>
					<div class="span7">
						<#if typeOfPayment == "PAYMENT_IS_AR">
						<div id="dropdownPartyTo">
							<div id="jqxgridPartyTo"></div>
						</div>
						</#if>
						<#if typeOfPayment == "PAYMENT_IS_AP">
						<div id="dropdownPartyFrom">
							<div id="jqxgridPartyFrom"></div>
						</div>
						</#if>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_amount}
					</div>
					<div class="span7">
						<div id="amount"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_overrideGlAccountId}
					</div>
					<div class="span7">
						<div id="dropdownGlAccount">
							<div id="overrideGlAccountId"></div>
						</div>
					</div>
				</div>	
			</div>
			<div class="span6">
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_currencyUomId}
					</div>
					<div class="span7">
						<div id="currencyUomId"></div>
					</div>
				</div>	
				<div class="row-fluid margin-bottom10">	
					<div class="span5 align-right">
						${uiLabelMap.AccountingActualCurrencyAmount}
					</div>
					<div class="span7">
						<div id="actualCurrencyAmount"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.AccountingActualCurrencyUomId}
					</div>
					<div class="span7">
						<div id="actualCurrencyUomId"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_effectiveDate}
					</div>
					<div class="span7">
						<div id="effectiveDate"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_paymentRefNum}
					</div>
					<div class="span7">
						<div id="paymentRefNum"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_comments}
					</div>
					<div class="span7">
						<input id="comments" type="text"/>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">		
					<div class="span5 align-right">
						${uiLabelMap.FormFieldTitle_finAccountTransId}
					</div>
					<div class="span7">
						<input id="finAccountTransId" type="text"/>
					</div>
				</div>
			</div>
			<div>
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button style="position:absolute;right : 135px;" id="updatePayment" class="btn btn-small btn-primary form-action-button pull-right"><i class="fa-check"></i>&nbsp;${uiLabelMap.CommonUpdate}</button>
					</div>
				</div>
			</div>
		</div>		
	</div>
</div>
<script>
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var sortFunc = function(array,args,key){
		if(array && array.length > 0 && args){
			for(var i = 0 ; i < array.length;i++){
				if(array[i][key] == args){
					var element = array[i];
					array.splice(i,1);
					array.unshift(element);
					break;
				}
			}
		}
		return array;
	}
	var action = (function(){
		var initElement = function(){
			$('#paymentTypeId').jqxDropDownList({theme : theme,width : '250px',height : '25px',autoDropDownHeight : false,scrollBarSize :10,displayMember : 'description',valueMember : 'paymentTypeId',source : listPaymentType});
			$('#currencyUomId').jqxDropDownList({filterable: true, theme : theme,width : '250px',height : '25px',autoDropDownHeight : false,scrollBarSize :10,displayMember : 'description',valueMember : 'currencyUomId',source : listCurrency});
			$('#paymentMethodId').jqxDropDownList({theme : theme,width : '250px',height : '25px',autoDropDownHeight : false,scrollBarSize :10,selectedIndex : 0,source : listPaymentMethod,displayMember : 'description',valueMember : 'paymentMethodId',source : listPaymentMethod});
			<#if payment.paymentTypeId?exists>
				$('#paymentTypeId').jqxDropDownList('val','${payment.paymentTypeId?if_exists}');
			</#if>
			<#if defaultOrganizationPartyCurrencyUomId?exists>
				$('#currencyUomId').jqxDropDownList('val','${defaultOrganizationPartyCurrencyUomId?if_exists}');
			</#if>
			$('#actualCurrencyUomId').jqxDropDownList({filterable: true, theme : theme,width : '250px',height : '25px',autoDropDownHeight : false,placeHolder : '',scrollBarSize :10,source : listCurrency,displayMember : 'description',valueMember : 'currencyUomId',source : listCurrency});
			<#if payment.actualCurrencyUomId?exists>
				$('#actualCurrencyUomId').jqxDropDownList('val','${payment.actualCurrencyUomId?if_exists}');
			</#if>
			$('#amount').jqxNumberInput({theme:theme,width : '250px',height : '25px',digits: 15,spinButtons : true, max: 999999999999999, min: 0});
			<#if payment.amount?exists>
				$('#amount').jqxNumberInput({value : parseInt('${payment.amount?if_exists}')});
			</#if>
			$('#paymentRefNum').jqxNumberInput({theme:theme,width : '250px',height : '25px',digits: 15,spinButtons : true, max: 999999999999999, min: 0});
			<#if payment.paymentRefNum?exists>
				$('#paymentRefNum').jqxNumberInput({value : '${payment.paymentRefNum?if_exists}'});
			</#if>
			$('#comments').jqxInput({width  : '250px',height : '25px',value:'${payment.comments?if_exists}'});
			$('#finAccountTransId').jqxInput({width  : '250px',height : '25px',value : '${payment.finAccountTransId?if_exists}'});
			$('#actualCurrencyAmount').jqxNumberInput({width  : '250px',height : '25px',digits : 15,spinButtons : true, max: 999999999999999, min: 0});
			<#if payment.actualCurrencyAmount?exists>
			$('#actualCurrencyAmount').jqxNumberInput({value : '${payment.actualCurrencyAmount?if_exists}'});
			</#if>
			$('#effectiveDate').jqxDateTimeInput({width : '250px',height : '25px',allowNullDate : true,value : '${payment.effectiveDate?if_exists}',formatString : 'dd/MM/yyyy HH:mm:ss'});
			$('#notification').jqxNotification({autoClose : true,opacity : 1,autoCloseDelay : 5000,appendContainer : '#container'});
		}
		
			var initGridGlAccount = function(){
				var datafields = [
					{name : 'glAccountId',type : 'string'},
					{name : 'accountName',type : 'string'},
					{name : 'glAccountTypeId',type : 'string'},
					{name : 'glAccountClassId',type : 'string'},
				]
				var columns = [
					{text : '${uiLabelMap.AccountingGlAccountId}',datafield : 'glAccountId',width : '10%'},
					{text : '${uiLabelMap.CommonName}',datafield : 'accountName',width : '30%'},
					{text : '${uiLabelMap.CommonType}',datafield : 'glAccountTypeId',width : '30%',filtertype : 'checkedlist',cellsrenderer : function(row){
						var data = $('#overrideGlAccountId').jqxGrid('getrowdata',row);
						for(var i = 0;i < listGlAccountType.length ; i++){
							if(listGlAccountType[i].glAccountTypeId == data.glAccountTypeId){
								return '<span>' + listGlAccountType[i].description + '</span>';
							}
						}
						return data.glAccountTypeId;
					},createfilterwidget : function(column,columnElement,widget){
						var filterBoxAdapter2 = new $.jqx.dataAdapter(listGlAccountType,
		                {
		                    autoBind: true
		                });
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						widget.jqxDropDownList({width : '240px',source : uniqueRecords2, displayMember : 'description',valueMember :'glAccountTypeId'});
						//widget.jqxDropDownList('checkAll');
					}
					},
					{text : '${uiLabelMap.AccountingGlAccountClass}',datafield : 'glAccountClassId',filtertype : 'checkedlist',width : '30%',cellsrenderer : function(row){
						var data = $('#overrideGlAccountId').jqxGrid('getrowdata',row);
						for(var i = 0;i < listGlAccountClass.length ; i++){
							if(listGlAccountClass[i].glAccountClassId == data.glAccountClassId){
								return '<span>' + listGlAccountClass[i].description + '</span>';
							}
						}
						return data.glAccountClassId;
					},createfilterwidget : function(column,columnElement,widget){
						var filterBoxAdapter2 = new $.jqx.dataAdapter(listGlAccountClass,
		                {
		                    autoBind: true
		                });
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						widget.jqxDropDownList({width : '240px',source : uniqueRecords2, displayMember : 'description',valueMember :'glAccountClassId'});
						//widget.jqxDropDownList('checkAll');
					}
					}
				]
				GridUtils.initDropDownButton({url : 'JQGetListGLAccountsDetail',filterable : true,width : 800,dropdown : {width : '250px',height : '25px'}},datafields,columns,null,$('#overrideGlAccountId'),$('#dropdownGlAccount'),'glAccountId');
				$('#dropdownGlAccount').val('<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${payment.overrideGlAccountId?if_exists}</div>');
			}
			
			var initGridParty = function(){
				var datafields = [
					{name : 'partyId',type : 'string'},
					{name : 'partyTypeId',type : 'string'},
					{name : 'firstName',type : 'string'},
					{name : 'lastName',type : 'string'},
					{name : 'groupName',type : 'string'}
				]
				columnlistPartyFrom = initColumnParty('jqxgridPartyFrom');
				columnlistPartyTo =  initColumnParty('jqxgridPartyTo');
				GridUtils.initDropDownButton({url : 'getFromParty',filterable : true,width : 800,pagesize : 10,dropdown : {width : '250px',height : '25px',value : '${payment.dropdownPartyFrom?if_exists}'}},datafields,columnlistPartyFrom,null,$('#jqxgridPartyFrom'),$('#dropdownPartyFrom'),'partyId');
				GridUtils.initDropDownButton({url : 'getFromParty',filterable : true,width : 800,pagesize : 10,dropdown : {width : '250px',height : '25px',value : '${payment.dropdownPartyTo?if_exists}'}},datafields,columnlistPartyTo,null,$('#jqxgridPartyTo'),$('#dropdownPartyTo'),'partyId');
				$('#dropdownPartyFrom').val('<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${payment.partyIdFrom?if_exists}</div>');
				$('#dropdownPartyTo').val('<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">${payment.partyIdTo?if_exists}</div>');
			}
			
			var initColumnParty = function(id){
					var columnlistParty = [
					{text : '${uiLabelMap.PartyPartyId}',datafield : 'partyId',width : '15%'},
					{text : '${uiLabelMap.PartyTypeId}',datafield : 'partyTypeId',width : '250', filtertype : 'checkedlist',
					cellsrenderer : function(row){
						var data = $('#' + id).jqxGrid('getrowdata',row);
						for(var i = 0;i < listPartyType.length ; i++){
							if(listPartyType[i].partyTypeId == data.partyTypeId){
								return '<span>' + listPartyType[i].description + '</span>';
							}
						}
						return data.partyTypeId;
					},  
					   createfilterwidget : function(column,columnElement,widget){
						var filterBoxAdapter2 = new $.jqx.dataAdapter(listPartyType,
		                {
		                    autoBind: true
		                });
		                var uniqueRecords2 = filterBoxAdapter2.records;
		   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						widget.jqxDropDownList({width : '240px',source : uniqueRecords2, displayMember : 'description',valueMember :'partyTypeId'});
						//widget.jqxDropDownList('checkAll');
					}},
					{text : '${uiLabelMap.PartyFirstName}',datafield : 'firstName',width : '30%'},
					{text : '${uiLabelMap.PartyLastName}',datafield : 'lastName',width : '30%'},
					{text : '${uiLabelMap.PartyGroupName}',datafield : 'groupName',width : '250px'}
				]
				return columnlistParty;
			}
			
			
			var getData = function(){
				var row = {
					paymentId:'${payment.paymentId?if_exists}',
					statusId: '${payment.statusId?if_exists}',
					paymentTypeId: $('#paymentTypeId').jqxDropDownList('val')?$('#paymentTypeId').jqxDropDownList('val') : '${payment.paymentTypeId?if_exists}',
					paymentMethodId: $('#paymentMethodId').jqxDropDownList('val') ? $('#paymentMethodId').jqxDropDownList('val') : '${payment.paymentMethodId?if_exists}',
					partyIdFrom:$('#dropdownPartyFrom').jqxDropDownButton('val')?$('#dropdownPartyFrom').jqxDropDownButton('val') : '${payment.partyIdFrom?if_exists}',
					partyIdTo:$('#dropdownPartyTo').jqxDropDownButton('val')?$('#dropdownPartyTo').jqxDropDownButton('val') : '${payment.partyIdTo?if_exists}',
					amount:$('#amount').val() ? $('#amount').val() : '${payment.amount?if_exists}',
					currencyUomId:$('#currencyUomId').jqxDropDownList('val')?$('#currencyUomId').jqxDropDownList('val') : '${payment.currencyUomId?if_exists}',
					actualCurrencyAmount:$('#actualCurrencyAmount').jqxNumberInput('val')?$('#actualCurrencyAmount').jqxNumberInput('val') : '${payment.actualCurrencyAmount?if_exists}',
					actualCurrencyUomId:$('#actualCurrencyUomId').jqxDropDownList('val')?$('#actualCurrencyUomId').jqxDropDownList('val') : '${payment.actualCurrencyUomId?if_exists}',
					effectiveDate:$('#effectiveDate').jqxDateTimeInput('getDate').format('yyyy-mm-dd hh:MM:ss')?$('#effectiveDate').jqxDateTimeInput('getDate').format('yyyy-mm-dd HH:MM:ss') : '${payment.effectiveDate?if_exists}',
					paymentRefNum: $('#paymentRefNum').val() ? $('#paymentRefNum').val() : '${payment.paymentRefNum?if_exists}',
					comments:$('#comments').jqxInput('val')?$('#comments').jqxInput('val'):'${payment.comments?if_exists}',
					finAccountTransId:$('#finAccountTransId').jqxInput('val')?$('#finAccountTransId').jqxInput('val'):'${payment.finAccountTransId?if_exists}',
					overrideGlAccountId:$('#dropdownGlAccount').jqxDropDownButton('val')?$('#dropdownGlAccount').jqxDropDownButton('val') : '${payment.overrideGlAccountId?if_exists}'
				}
				return row;
			}
			
			var sendRequest = function(){
				var data = getData();
				if(data){
					$.ajax({
						<#if typeOfPayment == "PAYMENT_IS_AP">
						url : 'accApupdatePaymentJSON',
						</#if>
						<#if typeOfPayment == "PAYMENT_IS_AR">
						url : 'accArupdatePaymentJSON',
						</#if>
						data : data,
						type : 'POST',
						dataType : 'json',
						async : false,
						cache : false,
						success : function(response){
								if(response._ERROR_MESSAGE_LIST_){
									$('#container').empty();
									$('#notification').jqxNotification({template : 'error'});
									$('#notification').text(response._ERROR_MESSAGE_LIST_);
									$('#notification').jqxNotification('open');
								}else{
									$('#container').empty();
									$('#notification').jqxNotification({template : 'success'});
									$('#notification').text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
									$('#notification').jqxNotification('open');
								}
						},
						error : function(err){
						}
					});
				}
			};
			
			var updatePayment = function(){
				sendRequest();
			}
			
			var bindEvent = function(){
				$('#updatePayment').click(function(){
					if(!action.updatePayment()){
						return;
					}else action.updatePayment();
				})
			}
			
			
		return {
			init : function(){
				initElement();
				initGridGlAccount();
				initGridParty();
				bindEvent();
			},
			updatePayment : updatePayment
		}
	}())

	$(document).ready(function(){
		action.init();
	})
</script>
</#if>