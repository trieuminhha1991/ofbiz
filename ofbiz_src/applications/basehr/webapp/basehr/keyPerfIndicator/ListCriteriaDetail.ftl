<#include "script/ListCriteriaDetailScript.ftl" />
<#assign dataField = "[
	{name: 'criteriaId', type : 'string'},
	{name: 'criteriaName', type : 'string'},
	{name: 'description', type : 'string'},
	{name: 'perfCriteriaTypeId', type: 'string'},
	{name: 'periodTypeId', type: 'string'},
	{name: 'uomId', type: 'string'},
	{name: 'target', type: 'number'},
	{name: 'perfCriDevelopmetTypeId', type: 'string'},
	{name: 'statusId', type: 'string'},
	{name: 'perfCriteriaTypeName', type: 'string'},
	{name : 'perfCriteriaPolicyId', type : 'string'},
	{name : 'fromDate', type : 'date', other : 'Timestamp'},
	{name : 'thruDate', type : 'date', other : 'Timestamp'},
]"/>

<#assign columnlist="
	{dataField : 'perfCriteriaTypeId', hidden: true},
	{dataField : 'perfCriteriaPolicyId', hidden: true},
	{dataField : 'fromDate', hidden: true},
	{dataField : 'thruDate', hidden: true},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonId)}', width : '10%', dataField : 'criteriaId', editable: false},
	{text : '${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}', dataField : 'criteriaName', width: '15%', editable: false},
	{text : '${StringUtil.wrapString(uiLabelMap.HRCommonFields)}', width : '18%', dataField : 'perfCriteriaTypeName', editable: false,
	},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonDescription)}', dataField : 'description', width: '23%', columntype: 'custom',editable: false,
		 cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
			return defaulthtml;
         }
	},
	{text : '${StringUtil.wrapString(uiLabelMap.HRFrequency)}', width : '15%', dataField : 'periodTypeId', columntype: 'dropdownlist',
		filtertype: 'checkedlist', editable: false,
		cellsrenderer: function (row, column, value) {
			for(var i = 0; i < globalVar.periodTypeArr.length; i++){
				if(value == globalVar.periodTypeArr[i].periodTypeId){
					return '<span>' + globalVar.periodTypeArr[i].description + '</span>'; 
				}
			}
			return '<span>' + value + '</span>';
		},
		createfilterwidget: function(column, columnElement, widget){
			var source = {
			        localdata: globalVar.periodTypeArr,
			        datatype: 'array'
			};		
			var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
		    var dataSoureList = filterBoxAdapter.records;
		    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
		}
	},
	{text : '${StringUtil.wrapString(uiLabelMap.HRTarget)}', width : '15%', dataField : 'target',  columntype: 'numberinput',
		filtertype: 'number', editable: false,
		cellsrenderer: function (row, column, value) {
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			var uomId = data.uomId;
			var retVal = value;
			var uomDes = uomId;
			if(value){
				retVal = formatNumber(value);
				if(uomId){
					for(var i = 0; i < globalVar.uomArr.length; i++){
						if(globalVar.uomArr[i].uomId == uomId){
							uomDes = globalVar.uomArr[i].abbreviation;
							break;
						}
					}
				}
				retVal += ' ' + uomDes;
			}
			return '<span>' + retVal + '<span>';
		}
	},
	{text : '${StringUtil.wrapString(uiLabelMap.KPIDevelopmentTrend)}', width : '15%', dataField : 'perfCriDevelopmetTypeId', columntype: 'dropdownlist',
		filtertype: 'checkedlist', editable: false,
		cellsrenderer: function (row, column, value) {
			for(var i = 0; i < globalVar.perfCriDevelopmentTypeArr.length; i++){
				if(value == globalVar.perfCriDevelopmentTypeArr[i].perfCriDevelopmetTypeId){
					return '<span>' + globalVar.perfCriDevelopmentTypeArr[i].perfCriDevelopmetName + '</span>'; 
				}
			}
			return '<span>' + value + '</span>';
		},
		createfilterwidget: function(column, columnElement, widget){
			var source = {
			        localdata: globalVar.perfCriDevelopmentTypeArr,
			        datatype: 'array'
			};		
			var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
		    var dataSoureList = filterBoxAdapter.records;
		    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'perfCriDevelopmetName', valueMember : 'perfCriDevelopmetTypeId'});
		    if(dataSoureList.length > 8){
		    	widget.jqxDropDownList({autoDropDownHeight: false});
		    }else{
		    	widget.jqxDropDownList({autoDropDownHeight: true});
		    }
		}
	},
	{text : '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', width : '15%', dataField : 'statusId', columntype: 'dropdownlist', 
		filtertype: 'checkedlist', editable: false,
		cellsrenderer: function (row, column, value) {
			for(var i = 0; i < globalVar.statusArr.length; i++){
				if(value == globalVar.statusArr[i].statusId){
					return '<span>' + globalVar.statusArr[i].description + '</span>'; 
				}
			}
			return '<span>' + value + '</span>';
		},
		createfilterwidget: function(column, columnElement, widget){
			var source = {
			        localdata: globalVar.statusArr,
			        datatype: 'array'
			};		
			var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
		    var dataSoureList = filterBoxAdapter.records;
		    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
		    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
		    if(globalVar.statusArr.length < 8){
		    	widget.jqxDropDownList({autoDropDownHeight : true});
		    }else{
		    	widget.jqxDropDownList({autoDropDownHeight : false});
		    }
		}
	},
	{datafield: 'uomId', hidden: true}
"/>

<#assign mouseRightMenu = "true"/>
<#assign contextMenuId = "contextMenu"/>

	<#--comment enable button new KPIWindow-->
	<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" addType="popup" editable="true" deleterow="false" mouseRightMenu=mouseRightMenu contextMenuId=contextMenuId
	alternativeAddPopup="newKPIWindow" addrow="true" showlist="false"
	url="jqxGeneralServicer?sname=JQGetListKeyPerformanceIndicator" autorowheight="true"
 	createUrl="jqxGeneralServicer?sname=CreateCriteria&jqaction=C" jqGridMinimumLibEnable="false"
 	addColumns="criteriaName;description;perfCriteriaTypeId;periodTypeId;target(java.math.BigDecimal);uomId;perfCriDevelopmetTypeId"
 	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCriteria"
	editColumns="criteriaId;description;criteriaName;perfCriteriaTypeId;periodTypeId;target(java.math.BigDecimal);uomId;perfCriDevelopmetTypeId"
	removeUrl="jqxGeneralServicer?sname=DeleteCriteria&jqaction=C" deleteColumn="criteriaId" 
	/>
	<#--<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true" addType="popup" editable="true" deleterow="false" mouseRightMenu=mouseRightMenu contextMenuId=contextMenuId
	alternativeAddPopup="newKPIWindow" addrow="false" showlist="false"
	url="jqxGeneralServicer?sname=JQGetListKeyPerformanceIndicator" autorowheight="true"
	createUrl="jqxGeneralServicer?sname=CreateCriteria&jqaction=C" jqGridMinimumLibEnable="false"
	addColumns="criteriaName;description;perfCriteriaTypeId;periodTypeId;target(java.math.BigDecimal);uomId;perfCriDevelopmetTypeId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateCriteria"
	editColumns="criteriaId;description;criteriaName;perfCriteriaTypeId;periodTypeId;target(java.math.BigDecimal);uomId;perfCriDevelopmetTypeId"
	removeUrl="jqxGeneralServicer?sname=DeleteCriteria&jqaction=C" deleteColumn="criteriaId"
	/>-->
	
<div id="contextMenu" style="display:none;">
	<ul>
		<li action="editPolicy">
			<i class="fa fa-pencil-square-o"></i>${uiLabelMap.ViewKpiPolicy}
	    </li>
	    <li action="viewKpiDetail">
	    	<i class="fa-search"></i>${uiLabelMap.ViewKpiDetail}
	    </li>
	</ul>
</div>

<div id="contextMenuEdit" style="display:none;">
	<ul>
		<li action="ViewDetail">
			<i class="fa fa-pencil-square-o"></i>${uiLabelMap.ViewDetails}
		</li>   
	</ul>
</div>
	
	
<#include "kpiEditWindow.ftl">
<#include "kpiCreateNewWindow.ftl">
