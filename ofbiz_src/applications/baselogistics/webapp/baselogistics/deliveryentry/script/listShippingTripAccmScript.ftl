<@jqGridMinimumLib />
<@jqOlbCoreLib hasDropDownList=true hasValidator=true hasGrid=true hasCore=false/>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js" ></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<style>
	.bootbox{
	    z-index: 990009 !important;
	}
	.modal-backdrop{
	    z-index: 890009 !important;
	}
	 .loading-container{
	 	z-index: 999999 !important;
	 }
</style>
<script type="text/javascript">
    var globalVar = {};
    globalVar.invoiceTypeData = [];
    globalVar.shipperSelected = [];
    globalVar.partyInfo = [];
    globalVar.formData = {};
    globalVar.dataGridInvoiceInfo = [];
    var THEME = 'olbius';
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var curShipmentStatusId = "SHIPMENT_INPUT";
	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" />
	</#if>
	<#assign statusDEes = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "TRIP_STATUS"), null, null, null, false)/>
	<#assign organizationParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", '${userLogin.lastOrg}'), false)>
    var groupName = '${StringUtil.wrapString(organizationParty.get("groupName", locale))}';
    var lastOrg = '${userLogin.lastOrg}';
    globalVar.partyInfo = [{"partyId": lastOrg, "partyName": groupName}];
	var statusDataDE = new Array();
	var statusAllStatusDE = new Array();
	<#list statusDEes as item>
		var row = {};
		<#assign descStatus = StringUtil.wrapString(item.get("description",locale))>
		<#if item.statusId != 'TRIP_CANCELLED'>
			statusDataDE.push(row);
		</#if>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${descStatus?if_exists}';
		statusAllStatusDE.push(row);
	</#list>

	//get all the shipper thuoc LOGM
    var listShippers = [];
        <#assign shippers = Static["com.olbius.basehr.util.PartyUtil"].getEmployeeHasRoleInDepartment(delegator, "DLOG", "LOG_DELIVERER","EMPLOYEE", nowTimestamp)!/>;
        <#if shippers?has_content>
            <#list shippers as shipper>
                var row = {};
                <#assign partyName = delegator.findOne("PartyNameView", {"partyId" : shipper?if_exists}, false)/>
                row['lastName'] = "${StringUtil.wrapString(partyName.lastName?if_exists)}";
                row['middleName'] = "${StringUtil.wrapString(partyName.middleName?if_exists)}";
                row['firstName'] = "${StringUtil.wrapString(partyName.firstName?if_exists)}";
                row['partyId'] = "${shipper?if_exists}";
                listShippers.push(row);
            </#list>
        </#if>
        if (listShippers.length > 0){
        	for (var shipper of listShippers){
        		var fullName = null;
        		if (shipper.lastName){
        			if (fullName){
        				fullName = fullName + ' ' + shipper.lastName;
        			} else {
        				fullName = shipper.lastName;
        			}
        		}
        		if (shipper.middleName){
        			if (fullName){
        				fullName = fullName + ' ' + shipper.middleName;
        			} else {
        				fullName = shipper.middleName;
        			}
        		}
        		if (shipper.firstName){
        			if (fullName){
        				fullName = fullName + ' ' + shipper.firstName;
        			} else {
        				fullName = shipper.firstName;
        			}
        		}
        		shipper["description"] = fullName;
        	}
        }
     //
    <#assign listInvoiceTypes = delegator.findList("InvoiceType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("invoiceTypeId", "PURCHASE_INVOICE"), null, null, null, false) />
    globalVar.invoiceTypeData = [
     	<#if listInvoiceTypes?exists>
     		<#list listInvoiceTypes as item>
     		{
     			invoiceTypeId : "${item.invoiceTypeId}",
     			description : "${StringUtil.wrapString(item.get('description',locale))}",
     		},
     	</#list>
    </#if>
    ];

    var addZero = function(i) {
    	if (i < 10) {i = "0" + i;}
    	return i;
    };
    function formatFullDate (value) {
    	if (value) {
    		var dateStr = "";
    		dateStr += addZero(value.getDate()) + '/';
    		dateStr += addZero(value.getMonth()+1) + '/';
    		dateStr += addZero(value.getFullYear()) + ' ';
    		dateStr += addZero(value.getHours()) + ':';
    		dateStr += addZero(value.getMinutes()) + ':';
    		dateStr += addZero(value.getSeconds());
    		return dateStr;
    	} else {
    		return "";
    	}
    };


	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.CannotBeforeNow = "${StringUtil.wrapString(uiLabelMap.CannotBeforeNow)}";
	uiLabelMap.CannotAfterNow = "${StringUtil.wrapString(uiLabelMap.CannotAfterNow)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
	uiLabelMap.AreYouSureDetele = "${StringUtil.wrapString(uiLabelMap.AreYouSureDetele)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.ViewDetailInNewPage = "${StringUtil.wrapString(uiLabelMap.ViewDetailInNewPage)}";
	uiLabelMap.BSViewDetail = "${StringUtil.wrapString(uiLabelMap.BSViewDetail)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.DAYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.DAYouNotYetChooseProduct)}";
	uiLabelMap.BSClickToChoose = "${uiLabelMap.BSClickToChoose}";
	uiLabelMap.BACCCreateInvoiceConfirm = '${StringUtil.wrapString(uiLabelMap.BACCCreateInvoiceConfirm)}';
	uiLabelMap.CommonSubmit = '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}';
	uiLabelMap.BLChooseAShipper = '${StringUtil.wrapString(uiLabelMap.BLChooseAShipper)}';
</script>
<script type="text/javascript" src="/logresources/js/deliveryentry/listShippingTripAccm.js"></script>
