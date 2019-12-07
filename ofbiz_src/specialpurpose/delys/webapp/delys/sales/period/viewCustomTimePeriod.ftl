<#assign parentPeriod = delegator.findByAnd("CustomTimePeriod", null, null, false)>
<#assign periodTypePT = delegator.findByAnd("PeriodType", null, null, false)>
<script>

var parentPeriod = [
                    <#list parentPeriod as pP>
                    {
                   parentPeriodId : "${pP.parentPeriodId?if_exists}",
                 	   periodName : "${StringUtil.wrapString(pP.periodName?if_exists)}"
                    },
                    </#list>	
                ];

var periodTypePT = [
                    <#list periodTypePT as pT>
                    {
                    	periodTypeId : "${pT.periodTypeId?if_exists}",
                 	   description : "${StringUtil.wrapString(pT.description?if_exists)}"
                    },
                    </#list>	
                ];

</script>



<div class="row-fluid">				
	<div id="viewInfoCustomTimePeriod" class="row margin_left_10 row-desc">
		<div class="span6">
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.CommonId}:</label>
				<div class="controls-desc">
					<b>${customTimePeriod.customTimePeriodId?if_exists}</b>
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.DAParentCustomTimePeriod}:</label>
				<div class="controls-desc">
					<#if customTimePeriod.parentPeriodId?exists>
					<#assign parentPeriodV = delegator.findOne("CustomTimePeriod", Static["org.ofbiz.base.util.UtilMisc"].toMap("customTimePeriodId", customTimePeriod.parentPeriodId), false)/>
					${customTimePeriod.parentPeriodId} - ${parentPeriodV.periodName?if_exists}
					</#if>
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_organizationPartyId}:</label>
				<div class="controls-desc">
					${customTimePeriod.organizationPartyId?if_exists}
				</div>
			</div>
			
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.AccountingPeriodType}:</label>
				<div class="controls-desc">
				<#assign periodTypeV = delegator.findOne("PeriodType", Static["org.ofbiz.base.util.UtilMisc"].toMap("periodTypeId", customTimePeriod.periodTypeId), false)/>
					${periodTypeV.description?if_exists}
				</div>
			</div>
		</div>
		<div class="span6">
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.AccountingPeriodName}:</label>
				<div class="controls-desc">
					${customTimePeriod.periodName?if_exists}
				</div>
			</div>
	
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.AccountingPeriodNumber}:</label>
				<div class="controls-desc">
					${customTimePeriod.periodNum?if_exists}
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.CommonThruDate}:</label>
				<div class="controls-desc">
					${customTimePeriod.fromDate?if_exists}
				</div>
			</div>
		
			<div class="control-group">
				<label class="control-label-desc">${uiLabelMap.CommonThruDate}:</label>
				<div class="controls-desc">
					${customTimePeriod.thruDate?if_exists}
				</div>
			</div>
		</div>
	</div>
</div>

<script>
function fixSelectAll(dataList) {
	var sourceST = {
	        localdata: dataList,
	        datatype: "array"
    };
	var filterBoxAdapter2 = new $.jqx.dataAdapter(sourceST, {autoBind: true});
    var uniqueRecords2 = filterBoxAdapter2.records;
	uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
	return uniqueRecords2;
}

<#assign listPeriodType = delegator.findList("PeriodType", null, null, null, null, false) >

var periodTypeData = new Array();
<#list listPeriodType as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.description?if_exists) />
	row['periodTypeId'] = '${item.periodTypeId}';
	row['description'] = '${description}';
	periodTypeData[${item_index}] = row;
</#list>
</script>

<#assign dataField = "[{name: 'customTimePeriodId', type: 'string'}, 
{name: 'parentPeriodId', type: 'string'}, 
{name: 'organizationPartyId', type: 'string'}, 
{name: 'periodTypeId', type: 'string'},
{name: 'periodNum', type: 'number'},
{name: 'periodName', type: 'string'}, 
{name: 'fromDate', type: 'date', other: 'Date'},
{name: 'thruDate', type: 'date', other: 'Date'}

]"/>

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.CommonId)}', dataField: 'customTimePeriodId', width: '7%', editable: false,
	cellsrenderer: function(row, colum, value) {
    	var data = $('#jqxgridView').jqxGrid('getrowdata', row);
    	return \"<span style='color:#08c;cursor:pointer;'>\" + data.customTimePeriodId + \"</span>\";
    }	
}, 
{text: '${StringUtil.wrapString(uiLabelMap.DAParentCustomTimePeriod)}', dataField: 'parentPeriodId',
	cellsrenderer: function(column, row, value){
		for(var i = 0;  i < parentPeriod.length; i++){
			if(parentPeriod[i].parentPeriodId == value){
				return '<span title=' + value + '>' + parentPeriod[i].periodName + ' - ['+ parentPeriod[i].parentPeriodId +']' + '</span>'
			}
		}
		return '<span>' + value + '</span>'
	}
},
{text: '${StringUtil.wrapString(uiLabelMap.DAPartyGroupId)}', dataField: 'organizationPartyId',},
{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodType)}', dataField: 'periodTypeId',  filtertype: 'list', columntype: 'dropdownlist',
	cellsrenderer: function(column, row, value){
		for(var i = 0;  i < periodTypeData.length; i++){
			if(periodTypeData[i].periodTypeId == value){
				return '<span title=' + value + '>' + periodTypeData[i].description + '</span>'
			}
		}
		return '<span>' + value + '</span>'
	},
    createfilterwidget: function (column, htmlElement, editor) {
        editor.jqxDropDownList({ source: fixSelectAll(periodTypeData), displayMember: 'description', valueMember: 'periodTypeId' ,
        	renderer: function (index, label, value) {
        		if (index == 0) {
        			return value;
        		}
                for(var i = 0; i < periodTypeData.length; i++){
                	if(value == periodTypeData[i].periodTypeId){
                		return periodTypeData[i].description; 
                	}
                }
            }});
        editor.jqxDropDownList('checkAll');
    },
},
{text: '${StringUtil.wrapString(uiLabelMap.CommonNbr)}', dataField: 'periodNum'},
{text: '${StringUtil.wrapString(uiLabelMap.AccountingPeriodName)}', dataField: 'periodName'},
{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', dataField: 'fromDate', cellsformat: 'd', filtertype:'range'},
{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', dataField: 'thruDate', cellsformat: 'd', filtertype:'range'},
"/>


<@jqGrid id="jqxgridView" addrow="false" clearfilteringbutton="true" editable="false" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="false"
url="jqxGeneralServicer?sname=JQGetListCustomTimePeriodChildren&parentPeriodId=${customTimePeriod.customTimePeriodId?if_exists}&hasrequest=Y"
/>