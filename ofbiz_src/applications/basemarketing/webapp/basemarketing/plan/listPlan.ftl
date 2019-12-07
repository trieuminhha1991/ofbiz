<script>
	<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "MKTG_PLAN_STATUS"), null, null, null, false) />
	var listStatusItem = [<#if listStatusItem?exists><#list listStatusItem as item>{
							statusId: '${item.statusId?if_exists}',
				description: '${StringUtil.wrapString(item.get("description", locale)?if_exists)}'
						},</#list></#if>];
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
						'${item.statusId?if_exists}': '${StringUtil.wrapString(item.get("description", locale)?if_exists)}',
					</#list></#if>};
	<#assign listMarketingType = delegator.findList("MarketingType", null, null, null, null, false) />
	var listMarketingType = [<#if listMarketingType?exists><#list listMarketingType as item>{
							marketingTypeId: '${item.marketingTypeId?if_exists}',
				description: '${StringUtil.wrapString(item.get("name", locale)?if_exists)}'
						},</#list></#if>];
	var mapMarketingType = {<#if listMarketingType?exists><#list listMarketingType as item>
						'${item.marketingTypeId?if_exists}': '${StringUtil.wrapString(item.get("name", locale)?if_exists)}',
					</#list></#if>};
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
</script>
<#if !showdetail?exists || (showdetail?exists && showdetail=="true")>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
        [
			{ name: 'marketingPlanId', type: 'string'},
			{ name: 'code', type: 'string'},
			{ name: 'name', type: 'string'},
			{ name: 'marketingTypeId', type: 'string'},
			{ name: 'budgetId', type: 'string'},
			{ name: 'parentPlanId', type: 'string'},
			{ name: 'statusId', type: 'string'},
			{ name: 'partyId', type: 'string'},
			{ name: 'description', type: 'string'},
			{ name: 'fromDate', type: 'date', other: 'Timestamp'},
			{ name: 'thruDate', type: 'date', other: 'Timestamp'}
        ],
        id: 'marketingPlanId',
        addrow: function (rowid, rowdata, position, commit) {
            commit(addMemberAjax({partyId: rowdata.partyId, familyId: rowdata.familyId, roleTypeIdFrom: rowdata.roleTypeIdFrom}, false));
        },
        deleterow: function (rowid, commit) {
		var data = grid.jqxGrid('getrowdatabyid', rowid);
            commit(deleteMember(data));
        },
        updaterow: function (rowid, newdata, commit) {

            commit(true);
        }
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
	localization: getLocalization(),
        width: '98%',
        height: '92%',
        theme: 'olbius',
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
		pageable: true,
        selectionmode: 'singlerow',
        columns: [
				{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (row + 1) + '</div>';
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.marketingPlanId)}', datafield: 'code', width: 150 },
				{ text: '${StringUtil.wrapString(uiLabelMap.planName)}', datafield: 'name', width: 200 },
				{ text: '${uiLabelMap.marketingTypeId}', datafield: 'marketingTypeId', filtertype: 'checkedlist', width: '200px', filterable: true,
					 cellsrenderer: function(row, colum, value){
							value?value=mapMarketingType[value]:value;
							return '<span>' + value + '</span>';
					 },createfilterwidget: function (column, htmlElement, editor) {
					editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listMarketingType), displayMember: 'marketingTypeId', valueMember: 'marketingTypeId' ,
	                            renderer: function (index, label, value) {
					if (index == 0) {
						return value;
									}
								    return mapMarketingType[value];
				                }
					});
					editor.jqxDropDownList('checkAll');
					 }
				},
				{ text: '${uiLabelMap.DAStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
					 cellsrenderer: function(row, colum, value){
							value?value=mapStatusItem[value]:value;
							return '<span>' + value + '</span>';
					 },createfilterwidget: function (column, htmlElement, editor) {
					editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatusItem), displayMember: 'statusId', valueMember: 'statusId' ,
	                            renderer: function (index, label, value) {
					if (index == 0) {
						return value;
									}
								    return mapStatusItem[value];
				                }
					});
					editor.jqxDropDownList('checkAll');
					 }
				 },
				{ text: '${uiLabelMap.DAFromDate}', datafield: 'fromDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
				{ text: '${uiLabelMap.DAThruDate}', datafield: 'thruDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
				{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'description', minwidth: 200 }
                 ]
    });
}"/>
<#elseif showdetail?exists && showdetail=="false">
	<#assign initrowdetailsDetail=""/>
</#if>
<#assign dataField="[{ name: 'marketingPlanId', type: 'string'},
				   { name: 'code', type: 'string'},
				   { name: 'name', type: 'string'},
				   { name: 'marketingTypeId', type: 'string'},
				   { name: 'budgetId', type: 'string'},
				   { name: 'parentPlanId', type: 'string'},
				   { name: 'statusId', type: 'string'},
				   { name: 'partyId', type: 'string'},
				   { name: 'description', type: 'string'},
				   { name: 'fromDate', type: 'date', other: 'Timestamp'},
				   { name: 'thruDate', type: 'date', other: 'Timestamp'},
				   { name: 'rowDetail', type: 'string'}
				   ]"/>

<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.DmsSequenceId)}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50, cellsalign: 'left',
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (row + 1) + '</div>';
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.marketingPlanId)}', datafield: 'code', width: 150 },
					{ text: '${StringUtil.wrapString(uiLabelMap.planName)}', datafield: 'name', width: 200 },
					{ text: '${uiLabelMap.marketingTypeId}', datafield: 'marketingTypeId', filtertype: 'checkedlist', width: '200px', filterable: true,
						 cellsrenderer: function(row, colum, value){
								value?value=mapMarketingType[value]:value;
								return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
						editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listMarketingType), displayMember: 'marketingTypeId', valueMember: 'marketingTypeId' ,
		                            renderer: function (index, label, value) {
						if (index == 0) {
							return value;
										}
									    return mapMarketingType[value];
					                }
						});
						editor.jqxDropDownList('checkAll');
						 }
					},
					{ text: '${uiLabelMap.DAStatus}', datafield: 'statusId', filtertype: 'checkedlist', width: 200,
						 cellsrenderer: function(row, colum, value){
								value?value=mapStatusItem[value]:value;
								return '<span>' + value + '</span>';
						 },createfilterwidget: function (column, htmlElement, editor) {
						editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listStatusItem), displayMember: 'statusId', valueMember: 'statusId' ,
		                            renderer: function (index, label, value) {
						if (index == 0) {
							return value;
										}
									    return mapStatusItem[value];
					                }
						});
						editor.jqxDropDownList('checkAll');
						 }
					 },
					{ text: '${uiLabelMap.DAFromDate}', datafield: 'fromDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
					{ text: '${uiLabelMap.DAThruDate}', datafield: 'thruDate', width: '200px', filtertype: 'date', filterable: true, cellsformat:'dd/MM/yyyy'},
					{ text: '${StringUtil.wrapString(uiLabelMap.DmsDescription)}', datafield: 'description', minwidth: 200 }
					"/>
<#if !parameters.id?exists>
	<#assign url="jqxGeneralServicer?sname=JQGetListMarketingPlan"/>
	<#assign addnew =  "icon-plus-sign open-sans@${uiLabelMap.CommonAdd}@EditMarketingPlan$target='_blank'"/>
<#else>
	<#assign addnew = "icon-plus-sign open-sans@${uiLabelMap.CommonAdd}@EditMarketingPlan?parentPlanId=${parameters.id}$target='_blank' "/>
	<#assign url="jqxGeneralServicer?sname=JQGetListMarketingPlan&marketingPlanId=${parameters.id}" />
</#if>
<#if !customLoadFunction?exists>
	<#assign customLoadFunction="false"/>
</#if>
<#if !showtoolbar?exists>
	<#assign showtoolbar="true"/>
</#if>
<#if !showdetail?exists>
	<#assign showdetail="true"/>
</#if>
<#if !autoheight?exists>
	<#assign autoheight="true"/>
</#if>
<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar=showtoolbar addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
	customcontrol1=addnew
	id="ListPlan" sourceId="marketingPlanId"
	initrowdetails=showdetail initrowdetailsDetail=initrowdetailsDetail rowdetailsheight="203" height="300" autoheight=autoheight
	url=url
	contextMenuId="contextMenu" mouseRightMenu="true" customLoadFunction=customLoadFunction
/>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='addPlanChild'><i class="fa-plus"></i>&nbsp;&nbsp;${uiLabelMap.AddPlanChild}</li>
	</ul>
</div>