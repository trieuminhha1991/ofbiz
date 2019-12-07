<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script>
    var sortByDay = function(arr) {
        var standard = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
        return arr.sort(function(acc, curr) {
            return standard.indexOf(acc.value) - standard.indexOf(curr.value);
        });
    };
    var days = sortByDay([<#if Days?exists><#list Days as day>{
		value: 	'${StringUtil.wrapString(day.dayOfWeek)}',
		description: '${StringUtil.wrapString(day.description)}'
	},</#list></#if>]);
    var dayMap = {};
    <#if Days?exists>
        <#list Days as day>
		dayMap['${StringUtil.wrapString(day.dayOfWeek)}'] = '${StringUtil.wrapString(day.description)}';
        </#list>
    </#if>

</script>
<#assign dataField = "[
		{name : 'customerId',type : 'string'},
		{name : 'customerCode',type : 'string'},
		{name : 'customerName',type : 'string'},
		{name : 'salesRouteScheduleId',type : 'string'},
		{name : 'sequenceNum',type : 'number'},
		{name : 'date', type : 'date', other : 'Timestamp'},
		{name : 'salesmanId',type : 'string'},
		{name : 'salesmanCode',type : 'string'},
		{name : 'salesmanName',type : 'string'},
		{name : 'routeId',type : 'string'},
		{name : 'routeCode',type : 'string'},
		{name : 'routeName',type : 'string'},
		{name : 'scheduleRoute',type : 'string'},
		]"/>

<#assign columnlist= "
                 { text : '${uiLabelMap.BSDays}',datafield : 'date', cellsformat: 'dd/MM/yyyy',filterable: false, sortable: true, width : '120px'},
                 { text : '${uiLabelMap.CommonSequence}',datafield : 'sequenceNum',width : '60px',filterable: false, sortable: true},
				 { text : '${uiLabelMap.BsRouteId}',datafield : 'routeCode',width : '150px'},
				 { text : '${uiLabelMap.BSRouteName}',datafield : 'routeName'},
				 { text : '${uiLabelMap.BSCustomerId}',datafield : 'customerCode', width : '120px'},
				 { text : '${uiLabelMap.BSRetailOutlet}',datafield : 'customerName', width : '180px'},
				 { text : '${uiLabelMap.BSRouteSchedule}',datafield : 'scheduleRoute',width : '100px',filterable: true, filtertype: 'checkedlist',
				    cellsrenderer: function(row, colum, value){
                           value?value=dayMap[value]:value;
                           return '<span>' + value + '</span>';
					},
                    createfilterwidget: function (column, columnElement, widget) {
                        widget.jqxDropDownList({ source: days, displayMember: 'description', valueMember: 'value' });
                    }
				 },
				 { text : '${uiLabelMap.BSSalesman}',datafield : 'salesmanName',width : '200px'},
"/>
<div id="notification" style="width : 100%;"></div>
<@jqGrid id="jqxgridListScheduleDate" filtersimplemode="true" filterable="true" editable="false" addrefresh="true" editrefresh="true" updateoffline="false" showtoolbar="true" addType="popup" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" rowdetailsheight="275" initrowdetails="false"
url="jqxGeneralServicer?sname=JQgetListRouteScheduleDetailDate"
/>