<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">

var insuranceContentTypeArr = [
	<#if insuranceContentTypeList?has_content>
		<#list insuranceContentTypeList as insuranceContentType>
			{
				insuranceContentTypeId: '${insuranceContentType.insuranceContentTypeId}',
				description: '${StringUtil.wrapString(insuranceContentType.description)}'
			},
		</#list>
	</#if>
];

var contentTypeArr = [
	<#if contentTypeList?has_content>
		<#list contentTypeList as contentType>
			{
				contentTypeId: '${contentType.contentTypeId}',
				description: '${StringUtil.wrapString(contentType.description)}'
			},
		</#list>
	</#if>
];

var uiLabelMap = {};
uiLabelMap.ConfirmCreateInsuranceContent = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateInsuranceContent)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";

</script>