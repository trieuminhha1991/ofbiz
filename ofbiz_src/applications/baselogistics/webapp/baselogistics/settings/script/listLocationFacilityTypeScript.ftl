<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<style type="text/css">

.bootbox{
    z-index: 99000 !important;
}
.modal-backdrop{
    z-index: 89000 !important;
}
</style>
<script>
$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
	uiLabelMap.POCheckIsEmptyCreateLocationFacility = "${StringUtil.wrapString(uiLabelMap.POCheckIsEmptyCreateLocationFacility)}";
	uiLabelMap.POCheckIsEmptyCreateLocationFacility = "${StringUtil.wrapString(uiLabelMap.POCheckIsEmptyCreateLocationFacility)}";
	uiLabelMap.Edit = "${StringUtil.wrapString(uiLabelMap.Edit)}";
	uiLabelMap.Delete = "${StringUtil.wrapString(uiLabelMap.Delete)}";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}";
	uiLabelMap.CreateNewLocationType = "${StringUtil.wrapString(uiLabelMap.CreateNewLocationType)}";
	uiLabelMap.AreYouSureDelete = "${StringUtil.wrapString(uiLabelMap.AreYouSureDelete)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	uiLabelMap.CheckLinkedData = "${StringUtil.wrapString(uiLabelMap.CheckLinkedData)}";
	uiLabelMap.NotifiDeleteSucess = "${StringUtil.wrapString(uiLabelMap.NotifiDeleteSucess)}";
	uiLabelMap.AreYouSureUpdate = "${StringUtil.wrapString(uiLabelMap.AreYouSureUpdate)}";
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.AlreadyExited = "${StringUtil.wrapString(uiLabelMap.AlreadyExited)}";
	uiLabelMap.CreateSuccessfully = "${StringUtil.wrapString(uiLabelMap.CreateSuccessfully)}";
	uiLabelMap.NotifiUpdateError = "${StringUtil.wrapString(uiLabelMap.NotifiUpdateError)}";
	uiLabelMap.NotifiUpdateSucess = "${StringUtil.wrapString(uiLabelMap.NotifiUpdateSucess)}";
	uiLabelMap.EditLocationTypeInfo = "${StringUtil.wrapString(uiLabelMap.EditLocationTypeInfo)}";
	uiLabelMap.LocationType = "${StringUtil.wrapString(uiLabelMap.LocationType)}";
	uiLabelMap.LocationParentType = "${StringUtil.wrapString(uiLabelMap.LocationParentType)}";
	uiLabelMap.Description = "${StringUtil.wrapString(uiLabelMap.Description)}";
	uiLabelMap.PleaseSelectTitle = "${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}";
	uiLabelMap.ThisFieldMustNotByContainSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.ThisFieldMustNotByContainSpecialCharacter)}";
	uiLabelMap.BLRepresentCharacters = "${StringUtil.wrapString(uiLabelMap.BLRepresentCharacters)}";
	uiLabelMap.BLDefaultChildNumber = "${StringUtil.wrapString(uiLabelMap.BLDefaultChildNumber)}";
</script>
<script type="text/javascript" src="/logresources/js/config/listLocationFacilityType.js"></script>