<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxexpander.js"></script> 
<script src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	var contextMenu;
	var listParentTypeGl = new Array();
	var listTmp = new Array();
	var check = false;
	<#assign locationFacilityTypeList = delegator.findList("LocationFacilityType", null, null, null, null, false) />
	var locationFacilityTypeData = new Array();
	<#list locationFacilityTypeList as locationFacilityType>
		var row = {};
		row['locationFacilityTypeId'] = "${locationFacilityType.locationFacilityTypeId}";
		row['description'] = "${locationFacilityType.description?if_exists}";
		locationFacilityTypeData[${locationFacilityType_index}] = row;
	</#list>
	
	function getLocationFacilityType(locationFacilityTypeId) {
		for ( var x in locationFacilityTypeData) {
			if (locationFacilityTypeId == locationFacilityTypeData[x].locationFacilityTypeId) {
				return locationFacilityTypeData[x].description+"["+locationFacilityTypeId+"]";
			}
		}
	}
	
	var arrayLocationFacilityTypeInLocationFacilityTypeData = [];
	for(var i in locationFacilityTypeData){
		arrayLocationFacilityTypeInLocationFacilityTypeData.push(locationFacilityTypeData[i].locationFacilityTypeId);
	}
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${uiLabelMap.FieldRequired}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.SearchByNameOrId = "${StringUtil.wrapString(uiLabelMap.SearchByNameOrId)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.AreYouSureSave = "${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}";
	uiLabelMap.CommonSave = "${StringUtil.wrapString(uiLabelMap.CommonSave)}";
	uiLabelMap.CommonCancel	= "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.Location	= "${StringUtil.wrapString(uiLabelMap.Location)}";
	uiLabelMap.Inventory = "${StringUtil.wrapString(uiLabelMap.Inventory)}";
	uiLabelMap.Edit	= "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.CheckCharacterValidate2To20	= "${StringUtil.wrapString(uiLabelMap.CheckCharacterValidate2To20)}";
	uiLabelMap.CheckCharacterValidate0To10000	= "${StringUtil.wrapString(uiLabelMap.CheckCharacterValidate0To10000)}";
	uiLabelMap.UpdateSuccessfully	= "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}";
	uiLabelMap.NotifiUpdateError	= "${StringUtil.wrapString(uiLabelMap.NotifiUpdateError)}";
	uiLabelMap.NotifiUpdateErrorParent	= "${StringUtil.wrapString(uiLabelMap.NotifiUpdateErrorParent)}";
	uiLabelMap.CheckMaxLength	= "${StringUtil.wrapString(uiLabelMap.CheckMaxLength)}";
	uiLabelMap.AlreadyExited	= "${StringUtil.wrapString(uiLabelMap.AlreadyExited)}";
	uiLabelMap.ClearSelection	= "${StringUtil.wrapString(uiLabelMap.ClearSelection)}";
	uiLabelMap.LocationType	= "${StringUtil.wrapString(uiLabelMap.LocationType)}";
	uiLabelMap.LocationParentType	= "${StringUtil.wrapString(uiLabelMap.LocationParentType)}";
	uiLabelMap.Description	= "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.OK	= "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.wgpagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}";
	uiLabelMap.wgpagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}";
	uiLabelMap.wgpagerrangestring = "${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)}";
	uiLabelMap.wgpagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
	uiLabelMap.wgpagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
	uiLabelMap.wgsortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
	uiLabelMap.wgsortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
	uiLabelMap.wgsortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
	uiLabelMap.wgemptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
	uiLabelMap.wgfilterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
	uiLabelMap.wgfilterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
	uiLabelMap.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
	uiLabelMap.wgdragDropToGroupColumn = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
	uiLabelMap.wgtodaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
	uiLabelMap.wgclearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
	
</script>
<script type="text/javascript" src="/logresources/js/facility/locationType.js?v=0.0.1"></script>