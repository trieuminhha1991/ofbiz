<#include "script/ViewListInsuranceParticipateReportScript.ftl"/>
<#assign datafield = "[{name: 'reportId', type: 'string'},
					   {name: 'reportName', type: 'string'},
					   {name: 'month', type: 'number', other: 'Long'},	
					   {name: 'year', type: 'number', other: 'Long'},	
					   "/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceReportName)}', datafield: 'reportName', width: '14%',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								return '<a href=\"ViewInsuranceReportPartyOriginate?reportId='+ data.reportId + '\">' + value + '</a>';
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonMonth)}', datafield: 'month', width: '7%', columntype: 'numberinput', 
					   		filterType: 'number', cellsalign: 'right', columngroup: 'time'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonYear)}', datafield: 'year', width: '7%', columntype: 'numberinput', 
					   		filterType: 'number', cellsalign: 'right', columngroup: 'time'},
						"/>	
<#assign remainWidth = 100 - 14 - 7 - 7/>
<#assign mindWidth = 11/>										   
<#if insuranceOriginateTypeList?has_content>
	<#assign size = insuranceOriginateTypeList?size/> 
	<#if ( size > 1)>
		<#assign columnWidth = (remainWidth/size)?round/>
		<#assign widthLast = remainWidth - (columnWidth * (size - 1))/>
		<#if (columnWidth < mindWidth)>
			<#assign columnWidth = mindWidth/>
			<#assign widthLast = mindWidth/>
		</#if> 
	<#else>
		<#assign widthLast = remainWidth/> 	
	</#if>
	<#list insuranceOriginateTypeList as insuranceOriginateType>
		<#if !(insuranceOriginateType_has_next)>
			<#assign columnWidth = widthLast>
		</#if>
		<#assign datafield = datafield + "{name: '${insuranceOriginateType.insuranceOriginateTypeId}', type: 'number'},"/>
		<#assign columnlist = columnlist + "{text: '${insuranceOriginateType.description}', cellsalign: 'right', datafield: '${insuranceOriginateType.insuranceOriginateTypeId}',
				columntype: 'numberinput', filterable: false, width: '${columnWidth}%', columngroup: 'originate', sortable: false},"/>  
	</#list>
</#if>					   
<#assign datafield = datafield + "]"/>
<#assign columngrouplist = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceOriginate)}', name: 'originate', align: 'center'},
							{text: '${StringUtil.wrapString(uiLabelMap.CommonTime)}', name: 'time', align: 'center'}"/>

<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="true" columngrouplist=columngrouplist
					 filterable="true" alternativeAddPopup="addInsuranceReportWindow" editable="false" clearfilteringbutton="true" 
					 url="jqxGeneralServicer?sname=JQGetListInsuranceParticipateReport" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="" deleteColumn="" addType="popup"
					 updateUrl=""  editColumns="" 
					 addrow="true"  addColumns="" addrefresh="true"
					 deleterow="true" removeUrl=""
					 selectionmode="singlerow" 
					 mouseRightMenu="true" contextMenuId="contextMenu"
					 />	
					 
<#include "AddInsuranceParticipateReport.ftl"/>
<div id="contextMenu" class="hide">
	<ul>
		<li action="updateData" id="updateData"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.RefreshData)}</li>
	</ul>
</div>
<script type="text/javascript" src="/hrresources/js/insurance/ViewListInsuranceParticipateReport.js"></script>