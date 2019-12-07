<#assign featureTypeList = delegator.findList("ProductFeatureType", null , null, orderBy,null, false)>
<style>
	.line-height-25 {
		line-height: 25px;
	}
	#statusbarjqxgridFeatureType {
		width: 0 !important;
	}
</style>
<script type="text/javascript">
	var featureTypeList = [
	    <#list featureTypeList as featureTypeL>
	    {	productFeatureTypeId : "${featureTypeL.productFeatureTypeId}",
	    	description : "${StringUtil.wrapString(featureTypeL.description)}"
	    },
	    </#list>
	];
</script>

<div id="container" class="container-noti"></div>
<div id="jqxNotification">
    <div id="notificationContent">
    </div>
</div>

<div id="productFeatureTypeInfo" class="productFeatureTypeInfo">
	<div class='row-fluid'>
		<div id="jqxgridFeatureType"></div>
	</div>
</div>

<#--
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/crmresources/js/Underscore1.8.3.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
-->
<#--<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>-->

<@jqTreeGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasValidator=true/>

<#include "productFeatureTypeNewPopup.ftl">
<#include "productFeatureTypeEditPopup.ftl">

<script type="text/javascript">
	$(function(){
		OlbPageFeatureTypeList.init();
		
		lockRowParents();
		function lockRowParents() {
			var data = $("#jqxgridFeatureType").jqxTreeGrid('getRows');
			for (var x in data) {
				if (!data[x].description) {
					$("#jqxgridFeatureType").jqxTreeGrid('lockRow', data[x].uid);
				}
			}
		}
	});
	
	var OlbPageFeatureTypeList = (function(){
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initElementComplex = function(){
			var configFeatureType = {
				width: '100%',
				filterable: false,
				pageable: true,
				showfilterrow: false,
				key: 'productFeatureTypeId',
				parentKeyId: 'parentTypeId',
				localization: getLocalization(),
				datafields: [
					{name: 'productFeatureTypeId', type: 'string'},
					{name: 'parentTypeId', type: 'string'},
					{name: 'description', type: 'string'},
				],
				columns: [
					{text: '${StringUtil.wrapString(uiLabelMap.BSFeatureType)}', datafield: 'productFeatureTypeId', editable: false, width: '50%' },
					{text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: '50%' },
				],
				useUrl: true,
				root: 'listFeatureType',
				url: 'getListFeatureTypee',
				showtoolbar: true,
				rendertoolbarconfig: {
					<#assign customcontrol1 = "icon-plus open-sans@${uiLabelMap.BSQuickCreateNew}@javascript:OlbPageFeatureTypeNew.openWindowNew();"/>
					titleProperty: "${StringUtil.wrapString(uiLabelMap.BSFeatureTypeList)}",
					customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
					expendButton: true,
				},
			};
			new OlbTreeGrid($("#jqxgridFeatureType"), null, configFeatureType, []);
		};
		var initEvent = function(){
			$("#jqxgridFeatureType").on('rowDoubleClick', function (event) {
		        var args = event.args;
		        var key = args.key;
		        var row = args.row;
		        $("#alterpopupWindowEdit").jqxWindow('setTitle', "${StringUtil.wrapString(uiLabelMap.BSUpdateFeatureType)}: " + row.productFeatureTypeId);
		        $("#alterpopupWindowEdit").jqxWindow('open');
		        $("#alterpopupWindowEdit").attr('data-row', key);
		        if (row != null) {
		           if(row.productFeatureTypeId != null) $("#featureTypeIdEdit").val(row.productFeatureTypeId);
		           if(row.parentTypeId != null) $("#parentTypeIdEdit").jqxDropDownList('selectItem', row.parentTypeId);
		           if(row.description != null) $("#descriptionEdit").val(row.description);
		        }
		        $("#jqxgridFeatureType").jqxTreeGrid({ disabled: true });
		    });
		};
		return {
			init: init
		};
	}());
</script>
