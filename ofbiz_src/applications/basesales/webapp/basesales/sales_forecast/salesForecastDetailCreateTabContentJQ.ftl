<div id="jqxSalesForecast${salesForecastPartyItem_index}"></div>
<style>
#toolbarButtonContainerjqxSalesForecast0 div:first-child {
	float: left!important;
	margin-top: 2.5px!important;
}
</style>
<script type="text/javascript">
	var cellClass${salesForecastPartyItem_index} = function(row, columnfield, value){
		return cellClassCommon(row, columnfield, value, $("#jqxSalesForecast${salesForecastPartyItem_index}"));
	};
	var filterObjData = new Object();
	var salesFCDetailOLB;
	var listPeriod = [];
	<#if listPeriodThisAndChildren?exists>
		<#list listPeriodThisAndChildren as periodItem>
			var obj = {};
			obj["customTimePeriodId"] = "${periodItem.customTimePeriodId?if_exists}";
			<#assign name = StringUtil.wrapString(periodItem.get("periodName", locale)?if_exists) />
			obj["periodName"] = "${name}";
			listPeriod.push(obj);
		</#list>
	</#if>
	
	$.jqx.theme = 'olbius';
	var theme = $.jqx.theme;
	$(function(){
		OlbPageS4CDetailContent.init();
	});
	var OlbPageS4CDetailContent = (function(){
		var init = function(){
			initElementComplex();
		};
		var initElementComplex = function(){
			var datafields = [
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'features', type: 'string' },
				{ name: 'productCapacity', type: 'string' },
           		{ name: 'unitPrice', type: 'string' },
           		{ name: 'isVirtual', type: 'string' },
           		{ name: 'isVariant', type: 'string' },
           		<#list listPeriodThisAndChildren as periodItem>
           		{ name: '${periodItem.customTimePeriodId}', type: 'number', formatter: 'integer'},
           		{ name: '${periodItem.customTimePeriodId}_old', type: 'number', formatter: 'integer'},
           		{ name: '${periodItem.customTimePeriodId}_sf', type: 'string' },
           		{ name: '${periodItem.customTimePeriodId}_sfi', type: 'string' },
           		</#list>
           	];
           	var columns = [
           		{ text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productCode', width: '16%', editable:false, pinned: true},
           		{ text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', width: '25%', editable:false, cellClassName: cellClass${salesForecastPartyItem_index}},
		 		{ text: '${StringUtil.wrapString(uiLabelMap.BSFeature)}', dataField: 'features', width: '10%', editable:false, cellClassName: cellClass${salesForecastPartyItem_index}},
			 	<#--
			 	{ text: '${StringUtil.wrapString(uiLabelMap.BSCapacity)}', dataField: 'productCapacity', width: '15%', editable:false, cellClassName: cellClass${salesForecastPartyItem_index}},
			 	{ text: '${StringUtil.wrapString(uiLabelMap.unitPrice)}', dataField: 'unitPrice', width: '20%', editable:false, cellClassName: cellClass${salesForecastPartyItem_index}},
			 	-->
			 	<#list listPeriodThisAndChildren as periodItem>
           		{ text: '${StringUtil.wrapString(periodItem.periodName?default(periodItem.customTimePeriodId))}', dataField: '${periodItem.customTimePeriodId}', 
           			width: '12%', cellsalign: 'right', cellClassName: cellClass${salesForecastPartyItem_index}, cellsformat: 'd', columntype: 'numberinput', <#if periodItem.periodTypeId != "SALES_MONTH">editable: false, </#if>
			 		cellsrenderer: function(row, column, value){
				 		var returnVal = '<div class=\"innerGridCellContent align-right<#if periodItem.periodTypeId != "SALES_MONTH"> font-bold</#if>\">';
			   			returnVal += formatnumber(value) + '</div>';
		   				return returnVal;
				 	},
					validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
						}
						return true;
					},
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					}
				},
           		</#list>
           	];
           	<#assign tmpEdit = false/>
			<#if hasOlbPermission("MODULE", "SALESFORECAST_EDIT", "")>
				<#assign tmpEdit = true/>
			</#if>
			var configProductList = {
				showdefaultloadelement: true,
				autoshowloadelement: true,
				datafields: datafields,
				columns: columns,
				useUtilFunc: true,
				useUrl: true,
				url: "JQGetSalesForecastContent&salesForecastId=${salesForecastId?if_exists}&internalPartyId=${salesForecastPartyItem.internalPartyIds?if_exists}",
				clearfilteringbutton: false,
				alternativeAddPopup: 'alterpopupWindow',
				pageable: true,
				pagesize: 15,
				<#if tmpEdit>
				editable: true,
				editmode: 'click',
				</#if>
				selectionmode: 'singlecell',
				width: '100%',
				bindresize: false,
				groupable: false,
				localization: getLocalization(),
				showtoolbar: true,
				showdefaultloadelement: true,
				autoshowloadelement: true,
				virtualmode: true,
				rendertoolbar: function(toolbar){
					<#assign customTitleProperty = "${uiLabelMap.BSSalesForecast}: (${(salesForecast.salesForecastId)?if_exists})"/>
					<#if customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, false)!/>
	            		<#assign customTitleProperty = customTitleProperty + " ${customTimeParent.periodName?if_exists}"/>
	            	</#if>
					<#assign jqxGridId = "jqxSalesForecast${salesForecastPartyItem_index}"/>
					
					<#assign customcontrol1 = ""/>
					<#assign customcontrol2 = ""/>
					<#assign customcontrol3 = ""/>
					<#if hasOlbPermission("MODULE", "SALESFORECAST_NEW", "")>
						<#assign customControlAdvance="<div class='custom-control-toolbar' id='customControlUpload'><a style='color:#438eb9;' href='javascript:uploadFileSalesFC();'><i class='fa-upload'></i>&nbsp;<span>${uiLabelMap.UploadFileTemplateSalesForecastEdited}</span></a></div>"/>
						<#assign customcontrol1="fa-download@${uiLabelMap.DownloadFileTemplateSalesForecast}@javascript:exportExcel();"/>
						<#assign customcontrol2="icon-save@${uiLabelMap.CommonSave}@javascript:OlbForecastView.onSubmitFormCreateUpdateSalesForecast();"/>
						<#assign customcontrol3="fa-refresh open-sans@${uiLabelMap.CommonReset}@javascript:window.location.href='newSalesForecastDetailVer?salesForecastId=${salesForecastId?if_exists}';"/>
					</#if>

					<@renderToolbar id="${jqxGridId}" isShowTitleProperty="true" customTitleProperties="${customTitleProperty?if_exists}" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="true" 
						addrow=tmpCreate addType="popup" alternativeAddPopup="" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customControlAdvance=customControlAdvance customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3=customcontrol3 customtoolbaraction=""/>
				},
				isSaveFormData: true,
				formData: "filterObjData",
			};
			<#--
			var localDataSf = [
			<#if salesForecastPartyItem.localData?exists>
				<#list salesForecastPartyItem.localData as itemList>
				{<#list itemList.entrySet() as item>
					'${item.key}': '${item.value?default('')}',
				</#list>},
				</#list>
			</#if>
			];
			-->
			salesFCDetailOLB = new OlbGrid($("#jqxSalesForecast${salesForecastPartyItem_index}"), null, configProductList, []);
		};
		return {
			init: init
		}
	}());
	var exportExcel = function(){
		var isExistData = salesFCDetailOLB.isExistData();
		if(!isExistData){
			OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
		var salesForecastId = "${parameters.salesForecastId}";
		var internalPartyId = "${salesForecastPartyItem.internalPartyIds?if_exists}";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", "exportSaleFCDetailExcel");
		
		var hiddenField0 = document.createElement("input");
		hiddenField0.setAttribute("type", "hidden");
		hiddenField0.setAttribute("name", "salesForecastId");
		hiddenField0.setAttribute("value", salesForecastId);
		form.appendChild(hiddenField0);
		
		var hiddenField1 = document.createElement("input");
		hiddenField1.setAttribute("type", "hidden");
		hiddenField1.setAttribute("name", "internalPartyId");
		hiddenField1.setAttribute("value", internalPartyId);
		form.appendChild(hiddenField1);
		
		if(OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)){
			$.each(filterObjData.data, function(key, value){
				var hiddenField2 = documnet.creatElement("input");
				hiddenField2.setAttribute("type", "hidden");
				hiddenField2.setAttribute("name", key);
				hiddenField2.setAttribute("value", value);
				form.appendChild(hiddenField2);
			});
		}
		document.body.appendChild(form);
		form.submit();
	};
	var uploadFileSalesFC = function(){
		addSalesFCSheetDetailObj.openWindow();
	};
</script>
<#include "UploadFileSalesFCEditedPopup.ftl"/>