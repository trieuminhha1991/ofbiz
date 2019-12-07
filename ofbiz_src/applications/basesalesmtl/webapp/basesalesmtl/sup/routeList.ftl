<#assign id="ListRoute"/>
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<!--<script type="text/javascript" src="/salesmtlresources/js/common/map.js"></script>-->
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>
<#if roleTypeId?if_exists == "SALESMAN_EMPL">
<#assign hidden="true"/>
<#else>
<script>
	function initmap(){
		$('body').trigger('mapinit');
	};
</script>
<#assign hidden="false"/>
</#if>
<script>
	var flagPopupLoad = true;
	var flagPopupLoadGenSchedule = true;
	var flagPopupLoadContext = true;
	var flagPopupLoadUpdateCustRoute = true;
    var flagPopupLoadRouteOnMap = true;
    var flagPopupLoadListOfCustomer = true;
    var flagPopupLoadCustomerOnMap = true;
	var sortByDay = function(arr) {
		var standard = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
		return arr.sort(function(acc, curr) {
			return standard.indexOf(acc.value) - standard.indexOf(curr.value);
		});
	}
	
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

<#assign dataField="[{ name: 'routeId', type: 'string'},
					{ name: 'routeCode', type: 'string'},
					{ name: 'executorId', type: 'string'},
					{ name: 'salesmanId', type: 'string'},
					{ name: 'salesmanCode', type: 'string'},
					{ name: 'salesmanName', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'routeName', type: 'string'},
					{ name: 'weeks', type: 'string'},
					{ name: 'scheduleRoute', type: 'string'}]"/>
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BsRouteId)}', datafield: 'routeCode', width: '16%',
                        cellsrenderer: function(row, column, value, a, b, data){
                            var link = 'RouteDetail?routeId=' + data.routeId;
                            return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					    }
                    },
					{ text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName'},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesman)}', datafield: 'salesmanName', sortable: false, width: '16%', hidden: ${hidden},},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: '20%'},
					{ text: '${StringUtil.wrapString(uiLabelMap.BSScheduleDescription)}' , datafield: 'scheduleRoute', 'minWidth' : 250, width: '25%', sortable: false, cellsalign: 'left', filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value, a, b, data){
							var scheduleRoute = [];
							var scheduleRouteDesc = [];
							var result = '';
							scheduleRoute = value.match(/\\w+/gm);
							$.each(scheduleRoute, function (i, v){
								scheduleRouteDesc.push(dayMap[v]);
							});
							return '<div class=\"custom-cell-grid\">' + scheduleRouteDesc.sort().join(', ') + '</div>';
						},createfilterwidget: function (column, htmlElement, editor) {
							editor.jqxDropDownList({ autoDropDownHeight: true, source: days, displayMember: 'description', valueMember: 'value'});
						},
					},{ text: '${StringUtil.wrapString(uiLabelMap.Edit)}', datafield: 'edit', cellsalign: 'center', maxwidth:80,columntype: 'button',filterable:false,hidden: true,
						renderer: function(value){
							return '${StringUtil.wrapString(uiLabelMap.Edit)}';
						},
						cellsrenderer: function (row, column, value, a, b, data) {
		                     return '${StringUtil.wrapString(uiLabelMap.Edit)}';
		                  }, buttonclick: function (row) {
	                        RouteForm.updatePopup(row);
	                    }
	                }"/>

<#if hasOlbPermission("MODULE", "PARTY_ROUTER_NEW", "")>
	<#assign addrow = "true"/>
<#else>
	<#assign addrow = "false"/>
</#if>
<#if hasOlbPermission("MODULE", "PARTY_ROUTER_DELETE", "")>
	<#assign deleterow = "true"/>
<#else>
	<#assign deleterow = "false"/>
</#if>

<#if roleTypeId?if_exists != "SALESMAN_EMPL">
    <#assign customcontrol1="fa fa-globe open-sans@${uiLabelMap.BSRouteOnMap}@javascript: void(0)@OlbRouteOnMap.open()"/>
	<@jqGrid id="ListRoute" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" alternativeAddPopup="alterpopupWindowAdd" autorowheight="true"
		url="jqxGeneralServicer?sname=JQGetListRoute" contextMenuId="Context${id}" mouseRightMenu="true"
		deleterow=deleterow removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteRoute" deleteColumn="routeId"
		addColumns="routeCode;routeName;description;scheduleRoute;salesmanId;weeks"
		addrow=addrow createUrl="jqxGeneralServicer?jqaction=C&sname=createRoute"
		updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRoute"
        editColumns="routeCode;routeName;description;scheduleRoute;salesmanId;routeId;weeks"
        customcontrol1=customcontrol1
    />
	<#include "../loader/loader.ftl"/>
	<#include "routeAddNewPopup.ftl"/>
	<#--<#include "routeUpdateAddress.ftl"/>-->
	<#include "routeListOfCustomer.ftl"/>
    <#include "popupUpdateCustomerRoute.ftl"/>
    <#include "popupUpdateCustomerLocation.ftl"/>
    <#include "genScheduleRoute.ftl"/>
	<#include "routeContextMenu.ftl"/>
	<#include "routeOnMap.ftl"/>
    <#include "genCustomerSequenceOfRoute.ftl"/>
<#else>
	<@jqGrid id="ListRoute" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" alternativeAddPopup="AddRouteForm" autorowheight="true" filterable="false"
		url="jqxGeneralServicer?sname=JQGetListRoute"/>
</#if>
	<script>
		function initMap() {
			CustomerRoute.initMap()
		}
	</script>
	
