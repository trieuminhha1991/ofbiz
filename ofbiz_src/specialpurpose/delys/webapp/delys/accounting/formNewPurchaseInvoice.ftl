<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
    <form method="post" action="<@ofbizUrl>accApcreateInvoice</@ofbizUrl>" id="NewPurchaseInvoice" class="basic-form form-horizontal"  name="NewPurchaseInvoice" novalidate="novalidate">
        <div class="row-fluid">

            <input type="hidden" name="statusId" value="INVOICE_IN_PROCESS" id="NewPurchaseInvoice_statusId">
            <input type="hidden" name="currencyUomId" value="${defaultOrganizationPartyCurrencyUomId}" id="NewPurchaseInvoice_currencyUomId">
            <input type="hidden" name="invoiceTypeId"  id="NewPurchaseInvoice_invoiceTypeId">
            <input type="hidden" name="partyId"  id="NewPurchaseInvoice_organizationPartyId">
            <input type="hidden" name="partyIdFrom"  id="NewPurchaseInvoice_partyIdFrom">
			<div class="span12">
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right ">
						 <label class="asterisk" for="NewPurchaseInvoice_invoiceTypeIdShow" id="NewPurchaseInvoice_invoiceTypeIdShow_title">${uiLabelMap.FormFieldTitle_invoiceTypeId}</label>
					</div>
					<div class="span7">
						<div id="invoiceTypeIdShow"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right ">
						 <label class="asterisk" for="NewPurchaseInvoice_organizationPartyIdShow" id="NewPurchaseInvoice_organizationPartyIdShow_title">${uiLabelMap.CompanyAndUnit}</label>  </label>
					</div>
					<div class="span7">
						 <div id="organizationPartyIdShow"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">
					<div class="span5 align-right" >
						 <label class="asterisk" for="NewPurchaseInvoice_partyIdFromdShow" id="NewPurchaseInvoice_partyIdFromdShow_title">${uiLabelMap.SupplierAndEmployee}</label>
					</div>
					<div class="span7">
						<@htmlTemplate.lookupField name="partyIdFromdShow" id="partyIdFromdShow" value='' width="900" height="600"
							formName="NewPurchaseInvoice" fieldFormName="LookupJQPartyName" title="${uiLabelMap.CommonSearch} ${uiLabelMap.SupplierAndEmployee}" />
					</div>
				</div>
			</div>
			<div class="form-action">
				<div class="row-fluid margin-bottom10">
					<div class="span12">
						<div class="span5">
						</div>
						<div class="span7">
							<button style="left:300px;top:30px;" id="commSave" type="button" class="btn btn-small btn-primary" name="submitButton" ><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
						</div>
					</div>
				</div>
			</div>
    </form>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
    <#assign invoiceType= delegator.findList("InvoiceType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId","PURCHASE_INVOICE"),null,null,null,false)/>
    var resourceInvoiceType= new Array();
    <#list invoiceType as item>
        var row={};
        row['description']=  "${StringUtil.wrapString(item.get("description", locale))}";
        row['invoiceType']="${item.invoiceTypeId}";
        resourceInvoiceType[${item_index}]= row;
    </#list>

    <#assign organList= delegator.findList("PartyAcctgPrefAndGroup",null,null,null,null,false)/>
    var organSource=new Array();
    <#if organList?has_content>
        <#list organList as orItem>
            var rowOran= {};
            rowOran['partyId']= "${orItem.partyId}";
            rowOran['groupName']="${orItem.groupName}";
            organSource[${orItem_index}]=rowOran;
        </#list>
    </#if>
    $.jqx.theme = 'olbius';
    $("#invoiceTypeIdShow").jqxDropDownList({source: resourceInvoiceType, width: 217 ,autoDropDownHeight : true, displayMember:"description" ,valueMember: "invoiceType",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
    var invoiceValue=$("#invoiceTypeIdShow").val();
    $("#NewPurchaseInvoice_invoiceTypeId").val(invoiceValue);

    $('#invoiceTypeIdShow').on('select', function (event) {
        var args = event.args;
        if (args) {
            var item = args.item;
            invoiceValue = item.value;
        }
        $("#NewPurchaseInvoice_invoiceTypeId").val(invoiceValue);
    });

    $("#organizationPartyIdShow").jqxDropDownList({source: organSource, width: 217, displayMember:"groupName",autoDropDownHeight : true ,valueMember: "partyId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
    var orgParty=$("#organizationPartyIdShow").val();
    $("#NewPurchaseInvoice_organizationPartyId").val(orgParty);

    $('#organizationPartyIdShow').on('select', function (event) {
        var args = event.args;
        if (args) {
            var item = args.item;
            orgParty = item.value;
        }
        $("#NewPurchaseInvoice_organizationPartyId").val(orgParty);
    });
	
	
    var initRule = function(){
		$("#NewPurchaseInvoice").jqxValidator({
	        	rules: [{
	        	input: "#invoiceTypeIdShow", message: "${uiLabelMap.CommonRequired}", action: 'blur,change', 
	        	rule: function (input, commit) {
	                var index = input.jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	   		},{
	        	input: "#organizationPartyIdShow", message: "${uiLabelMap.CommonRequired}", action: 'blur,change', 
	        	rule: function (input, commit) {
	                var index = input.jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	   		},{
	        	input: "input[name=partyIdFromdShow]", message: "${uiLabelMap.CommonRequired}", action: 'change,close', 
	        	rule: function (input, commit) {
	                if($("input[name=partyIdFromdShow]").val()){
	                	return true;
	                }
	                return false;
	            }
	   		}]
	    });
	};
	initRule();
	$("#commSave").click(function(){
		if(!$("#NewPurchaseInvoice").jqxValidator('validate')){
			return;
		};
		var partyIdFrom = $('#0_lookupId_partyIdFromdShow').val();
		if(partyIdFrom){
			$('#NewPurchaseInvoice_partyIdFrom').val(partyIdFrom);
		};
		$("#NewPurchaseInvoice").submit();
	});
	
	
	$("form :input").on("keypress", function(e) {
		if(e.keyCode == 13){
			e.preventDefault();
  			return false;
		}
	});
</script>