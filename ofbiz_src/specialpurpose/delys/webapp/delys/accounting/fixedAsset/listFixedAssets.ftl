<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<script type="text/javascript">
	<#assign uomAndTypeList = delegator.findList("UomAndType", null, null, null, null, false) />
	var dataUomAndTypeListView = [
		<#list uomAndTypeList as uomAndType >
			{
				'uomId' : '${uomAndType.uomId?if_exists}',
				'description' : "[" + '${uomAndType.typeDescription?if_exists}'  + "] " + '${uomAndType.description?if_exists}' 
			},
		</#list>
	];
	<#assign uomCurrencyList = delegator.findList("UomAndType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("typeUomTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "CURRENCY_MEASURE"), null, null, null, false) />
	var dataUomCurrencyListView = [
		<#list uomCurrencyList as uomCurrency>
			{
				'uomId' : '${uomCurrency.uomId?if_exists}',
				'description' : '${uomCurrency.description?if_exists}' + ' [' + '${uomCurrency.uomId?if_exists}' + ']'
			},
		</#list>
	];
	<#assign roleTypeList = delegator.findList("RoleType", null, null, null, null, false) />
	var dataRoleTypeListView = [
		<#list roleTypeList as roleType >
			{
				'roleTypeId' : '${roleType.roleTypeId?if_exists}',
				'description' : '${roleType.description?if_exists}'
			},
		</#list>	
	];
	<#assign fixedAssetTypeList = delegator.findList("FixedAssetType",  null, null, null, null, false) />
	var fixedAssetTypeData = [
		<#list fixedAssetTypeList as fixedAssetType>
			<#assign description = StringUtil.wrapString(fixedAssetType.get("description", locale)) />
			{
				'description' : "${description}",
				'fixedAssetTypeId' : "${fixedAssetType.fixedAssetTypeId}"
			},
		</#list>
	];
	<#assign facilityList = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("facilityTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS , "WAREHOUSE"), null, null, null, false) />
	var dataFacilityListView = new Array();
	<#list facilityList as facility >
		var row = {};
		row['facilityId'] = '${facility.facilityId?if_exists}';
		row['facilityName'] = '${facility.facilityName?if_exists}';
		dataFacilityListView[${facility_index} + 1] = row;
	</#list>	
 	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    }
</script>

<#assign params="jqxGeneralServicer?sname=listFixedAssetsJqx">

<#assign dataField="[{ name: 'fixedAssetId', type: 'string'},
					 { name: 'fixedAssetName', type: 'string'},
					 { name: 'serialNumber', type: 'string'},
					 { name: 'partyId', type: 'string'},
					 { name: 'parentFixedAssetId', type: 'string'},
					 { name: 'fixedAssetTypeId', type: 'string'},
					 { name: 'acquiredOrderId', type: 'string'},
					 { name: 'roleTypeId', type: 'string'},
					 { name: 'dateAcquired', type: 'date', other: 'Timestamp'},
					 { name: 'dateLastServiced', type: 'date', other: 'Timestamp'},
					 { name: 'dateNextService', type: 'date', other: 'Timestamp'},
					 { name: 'actualEndOfLife', type: 'date', other: 'Timestamp'},
					 { name: 'expectedEndOfLife', type: 'date', other: 'Timestamp'},
					 { name: 'purchaseCost', type: 'number'},
					 { name: 'salvageValue', type: 'number'},
					 { name: 'depreciation', type: 'number'},
					 { name: 'plannedPastDepreciationTotal', type: 'number'},
					 { name: 'productionCapacity', type: 'number'},
					 { name: 'uomId', type: 'string'},
					 { name: 'locatedAtFacilityId', type: 'string'},
					 { name: 'purchaseCostUomId', type: 'string'}
				   ]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.serialNumber)}',editable : false, datafield: 'serialNumber', width: '150px', cellsrenderer:
				       function(row, colum, value, a, b, data){
			        		return '<span><a href=\"' + 'EditFixedAsset?fixedAssetId=' + data.fixedAssetId + '\">' + data.serialNumber + '</a></span>';
			         }},
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetName)}', datafield: 'fixedAssetName', width: '250px'},
					 { text: '${StringUtil.wrapString(uiLabelMap.AccountingFixedAssetTypeId)}', datafield: 'fixedAssetTypeId', width: '200px', filtertype: 'checkedlist', cellclassname: cellclass,columntype: 'dropdownlist',
						 	cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	        						for(i = 0 ; i < fixedAssetTypeData.length; i++){
	        							if(data.fixedAssetTypeId == fixedAssetTypeData[i].fixedAssetTypeId){
	        								return '<span title=' + value +'>' + fixedAssetTypeData[i].description + '</span>';
	        							}
	        						}
	        						
	        						return '<span title=' + value +'>' + value + '</span>';
	    						},
	    					createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(fixedAssetTypeData,
				                {
				                    autoBind: true
				                });
				   				var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({ source: uniqueRecords2, dropDownWidth: 300, displayMember: 'description', valueMember : 'fixedAssetTypeId', height: '21px'});
								widget.jqxDropDownList('checkAll');
				   			},
							createeditor: function (row, column, editor) {
                            	editor.jqxDropDownList({ theme: theme, source: fixedAssetTypeData, displayMember: 'description', valueMember: 'fixedAssetTypeId', width: '200', dropDownWidth: 300, dropDownHeight: 300, height: '25'});
                        	},
					 	},
	                    { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_dateAcquired)}', width:150,columntype : 'datetimeinput', datafield: 'dateAcquired', cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass,createeditor : function(row,cellvalue,editor){
	                    	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
	                    	editor.jqxDateTimeInput({formatString : 'dd/MM/yyyy',allowNullDate : true,value : data.dateAcquired ? data.dateAcquired : null});
	                    }},
	                    { text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_expectedEndOfLife)}', width:220, datafield: 'expectedEndOfLife',columntype : 'datetimeinput' ,cellsformat: 'dd/MM/yyyy', filtertype: 'range', cellclassname: cellclass,createeditor : function(row,cellvalue,editor){
	                    	var data = $('#jqxgrid').jqxGrid('getrowdata',row);
	                    	editor.jqxDateTimeInput({formatString : 'dd/MM/yyyy',allowNullDate : true,value : data.expectedEndOfLife ? data.expectedEndOfLife : null});
	                    }},
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_purchaseCost)}', width:'150px', datafield: 'purchaseCost', 
							columntype : 'numberinput',filtertype: 'number',cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
							 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);							 		
							 		return \"<span>\" + formatcurrency(data.purchaseCost,data.purchaseCostUomId) + \"</span>\";
							 	},
							createeditor : function(row,column,editor){
					 			editor.jqxNumberInput({width: '150px',digits : 15, spinButtons : false, min : 0, max: 999999999999999, decimalDigits : 0});
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.FormFieldTitle_salvageValue)}', width:'150px', datafield: 'salvageValue', 
							columntype : 'numberinput',filtertype: 'number',cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		return \"<span>\" + formatcurrency(data.salvageValue,data.purchaseCostUomId) + \"</span>\";
						 	},
							createeditor : function(row,column,editor){
					 			editor.jqxNumberInput({width: '150px',digits : 15, spinButtons : false, min : 0, max: 999999999999999, decimalDigits : 0});
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.AccountingDepreciation)}', width:'150px', datafield: 'depreciation', 
							columntype : 'numberinput',filtertype: 'number',cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						 		return \"<span>\" + formatcurrency(data.depreciation,data.purchaseCostUomId) + \"</span>\";
						 	},
							createeditor : function(row,column,editor){
					 			editor.jqxNumberInput({width: '150px',digits : 15, spinButtons : false, min : 0, max: 999999999999999, decimalDigits : 0});
							}
						},							 							 	
	                    { text: '${StringUtil.wrapString(uiLabelMap.accPlannedPastDepreciationTotal)}', width:200, editable : false,datafield: 'plannedPastDepreciationTotal', 
							columntype : 'numberinput', filtertype: 'number',cellsalign: 'right',
							cellsrenderer: function(row, colum, value){
					 			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
					 			return \"<span>\" + formatcurrency(data.plannedPastDepreciationTotal,data.purchaseCostUomId) + \"</span>\";
					 		},
							createeditor : function(row,column,editor){
					 			editor.jqxNumberInput({width: '200px',digits : 15, spinButtons : false, min : 0, max: 999999999999999, decimalDigits : 0});
							}
						}
					"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgrid" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" addrefresh="true"
		otherParams="plannedPastDepreciationTotal:S-calculateFixedAssetDepreciation(fixedAssetId{fixedAssetId})<plannedPastDepreciationTotal>"
		url=params 
		updateUrl="jqxGeneralServicer?sname=updateFixedAsset&jqaction=U"
		editColumns="fixedAssetId;fixedAssetName;fixedAssetTypeId;parentFixedAssetId;partyId;roleTypeId;acquireOrderId;dateAcquired(java.sql.Timestamp);dateLastServiced(java.sql.Timestamp);dateNextService(java.sql.Timestamp);expectedEndOfLife(java.sql.Timestamp);actualEndOfLife(java.sql.Timestamp);serialNumber;productionCapacity;uomId;locatedAtFacilityId;locatedAtLocationSeqId;salvageValue(java.math.BigDecimal);depreciation(java.math.BigDecimal);purchaseCost(java.math.BigDecimal);purchaseCostUomId"
		createUrl="jqxGeneralServicer?sname=createFixedAsset&jqaction=C" 
		addColumns="fixedAssetId;fixedAssetName;fixedAssetTypeId;parentFixedAssetId;partyId;roleTypeId;acquireOrderId;dateAcquired(java.sql.Timestamp);dateLastServiced(java.sql.Timestamp);dateNextService(java.sql.Timestamp);expectedEndOfLife(java.sql.Timestamp);actualEndOfLife(java.sql.Timestamp);serialNumber;productionCapacity;uomId;locatedAtFacilityId;locatedAtLocationSeqId;salvageValue(java.math.BigDecimal);depreciation(java.math.BigDecimal);purchaseCost(java.math.BigDecimal);purchaseCostUomId" 
		showlist="true" autoMeasureHeight="true" autoheight="true" scrollmode="logical"
	/>	
<style type="text/css">
	.bordertop{
		border-top:solid 1px #CCC;
		padding-top: 10px;
	}
</style>
<#include '../popup/popupAddFixedAsset.ftl'/>