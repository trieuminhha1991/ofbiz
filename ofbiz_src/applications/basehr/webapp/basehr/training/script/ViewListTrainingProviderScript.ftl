<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
var globalVar = {};
globalVar.geoCountryArr = [
	<#if geoCountryList?has_content>
		<#list geoCountryList as geo>
		{
			geoId: '${geo.geoId}',
			geoName: '${geo.geoName}'
		},
		</#list>
	</#if>
];

<#if defaultCountry?exists>
	globalVar.defaultCountry = "${defaultCountry}";
</#if>


var cellClass = function (row, columnfield, value) {
	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		var now = new Date();
		if (now > new Date(data.thruDate) && data.thruDate != null && data.thruDate != undefined) {
			return "background-cancel";
		} else {
			return "background-prepare";
		}
	}
}

var uiLabelMap = {};
uiLabelMap.CommonCountryCode = "${StringUtil.wrapString(uiLabelMap.CommonCountryCode)}";
uiLabelMap.PartyAreaCode = "${StringUtil.wrapString(uiLabelMap.PartyAreaCode)}";
uiLabelMap.CommonEdit = "${StringUtil.wrapString(uiLabelMap.CommonEdit)}";
uiLabelMap.CommonAddNew = "${StringUtil.wrapString(uiLabelMap.CommonAddNew)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.CreateTrainingProviderConfirm = "${StringUtil.wrapString(uiLabelMap.CreateTrainingProviderConfirm)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";
uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
uiLabelMap.ExpiredRelationship = "${StringUtil.wrapString(uiLabelMap.ExpiredRelationship)}";
uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
uiLabelMap.FromDateMustBeBeforeThruDate = "${StringUtil.wrapString(uiLabelMap.FromDateMustBeBeforeThruDate)}";
uiLabelMap.ThruDateMustBeAfterFromDate = "${StringUtil.wrapString(uiLabelMap.ThruDateMustBeAfterFromDate)}";

uiLabelMap.EmailFormatExample = "${StringUtil.wrapString(uiLabelMap.EmailFormatExample)}";
uiLabelMap.FormatWrong = "${StringUtil.wrapString(uiLabelMap.FormatWrong)}";
</script>