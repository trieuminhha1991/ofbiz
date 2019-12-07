<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearStart(nowTimestamp)/>
<#assign endYear = Static["org.ofbiz.base.util.UtilDateTime"].getYearEnd(nowTimestamp, timeZone, locale)/>
<script type="text/javascript">
var stateProvinceGeoArr = [
   	<#if listStateProvinceGeoVN?has_content>
   		<#list listStateProvinceGeoVN as geo>
   			{
   				geoId: '${geo.geoId}',
   				geoName: '${StringUtil.wrapString(geo.geoName)}',
   				codeNumber: '${geo.codeNumber?if_exists}'
   			},
   		</#list>
   	</#if>
];
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>

var globalVar = {
		<#if expandTreeId?has_content>
		expandTreeId: "${expandTreeId}",	
		</#if>
		nowTimestamp: ${nowTimestamp.getTime()},
		startYear: ${startYear.getTime()},
		endYear: ${endYear.getTime()},
		
};

var uiLabelMap = {};
uiLabelMap.CommonAreaCode = "${StringUtil.wrapString(uiLabelMap.CommonAreaCode)}";
uiLabelMap.CommonSearch = "${StringUtil.wrapString(uiLabelMap.CommonSearch)}";
uiLabelMap.ConfirmAddEmplInsuranceHealth = "${StringUtil.wrapString(uiLabelMap.ConfirmAddEmplInsuranceHealth)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";
uiLabelMap.EnterEmployeeId = "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}";
uiLabelMap.EnterInsuranceNbr = "${StringUtil.wrapString(uiLabelMap.EnterInsuranceNbr)}";
uiLabelMap.CommonId = "${StringUtil.wrapString(uiLabelMap.CommonId)}";
uiLabelMap.ThruDateMustGreaterThanFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate)}";
uiLabelMap.CommonMonth = "${StringUtil.wrapString(uiLabelMap.CommonMonth)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";

function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>
