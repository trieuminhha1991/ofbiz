<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
	var uiLabelMap = {
		CriteriaName : "${StringUtil.wrapString(uiLabelMap.CriteriaName)}",
		CriteriaId : '${StringUtil.wrapString(uiLabelMap.CriteriaId)}',
		CreateSuccess : '${StringUtil.wrapString(uiLabelMap.CreateSuccess)}',
		CancelStatus : '${StringUtil.wrapString(uiLabelMap.CancelStatus)}',
		EditStatus : '${StringUtil.wrapString(uiLabelMap.EditStatus)}',
		EditSuccess : '${StringUtil.wrapString(uiLabelMap.EditSuccess)}',
		DeleteSuccess : '${StringUtil.wrapString(uiLabelMap.DeleteSuccess)}',
		EmpPosType : '${StringUtil.wrapString(uiLabelMap.EmpPosType)}',
		HRCommonKPIName : '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}',
		HRCommonFields : '${StringUtil.wrapString(uiLabelMap.HRCommonFields)}',
		HRFrequency : '${StringUtil.wrapString(uiLabelMap.HRFrequency)}',
		HRTarget : '${StringUtil.wrapString(uiLabelMap.HRTarget)}',
		CommonStatus : '${StringUtil.wrapString(uiLabelMap.CommonStatus)}',
		HrCommonPosition : '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}',
		ChooseEmplPositionType : '${StringUtil.wrapString(uiLabelMap.ChooseEmplPositionType)}',
		ChoosePerfCriteriaType : '${StringUtil.wrapString(uiLabelMap.ChoosePerfCriteriaType)}',
		CommonClose : '${StringUtil.wrapString(uiLabelMap.CommonClose)}',
		YouHavenNotChoosePosition : "${StringUtil.wrapString(uiLabelMap.YouHavenNotChoosePosition)}",
		ChooseStatus : '${StringUtil.wrapString(uiLabelMap.ChooseStatus)}',
		deleteSuccessfully : '${StringUtil.wrapString(uiLabelMap.deleteSuccessfully)}',
		FieldRequired : '${StringUtil.wrapString(uiLabelMap.FieldRequired)}',
		ValueMustBeGreateThanZero : '${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}',
		AssignKPIEmplPositionTypeConfirm : '${StringUtil.wrapString(uiLabelMap.AssignKPIEmplPositionTypeConfirm)}',
		CommonSubmit : '${StringUtil.wrapString(uiLabelMap.CommonSubmit)}',
		NoRowSelected : '${StringUtil.wrapString(uiLabelMap.NoRowSelected)}',
		wgdeleteconfirm : '${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}',
		KPIWeigth : '${StringUtil.wrapString(uiLabelMap.KPIWeigth)}',
	};
	var globalVar = {};
	
	globalVar.statusArr = [
   		<#if KPIStatusList?has_content>
   			<#list KPIStatusList as status>
   			{
   				statusId: '${status.statusId}',
   				description: '${StringUtil.wrapString(status.description?if_exists)}'
   			},
   			</#list> 
   		</#if>                       
   	];
   	globalVar.uomArr = [
   		<#if uomList?has_content>
   			<#list uomList as uom>
   			{
   				uomId: '${uom.uomId}',
   				abbreviation: '${StringUtil.wrapString(uom.abbreviation?if_exists)}',
   				description: '${StringUtil.wrapString(uom.description?if_exists)}'
   			},
   			</#list>
   		</#if>
   	];
   	
   	globalVar.periodTypeArr = [
   		<#if periodTypeList?has_content>
   			<#list periodTypeList as periodType>
   			{
   				periodTypeId: '${periodType.periodTypeId}',
   				description: '${periodType.description?if_exists}'
   			},
   			</#list>
   		</#if>
   	];
   	
   	globalVar.perfCriteriaTypeArr = [
   		<#if perfCriteriaTypeList?has_content>
   			<#list perfCriteriaTypeList as perfCriteriaType>
   			{
   				perfCriteriaTypeId: '${perfCriteriaType.perfCriteriaTypeId}',
   				description: '${StringUtil.wrapString(perfCriteriaType.description?if_exists)}'
   			},
   			</#list>
   		</#if>
   	];

   	globalVar.emplPositionTypeArr = [
   		<#if emplPositionTypeList?has_content>
   			<#list emplPositionTypeList as emplPositionType>
   			{
   				emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
   				description: '${StringUtil.wrapString(emplPositionType.description?if_exists)}'
   			},
   			</#list>
   		</#if>
   	];
   	
</script>