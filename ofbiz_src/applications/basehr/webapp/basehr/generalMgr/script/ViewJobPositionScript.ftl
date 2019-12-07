<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>

<#if !rootOrgList?exists>
<#assign rootOrgList = Static["com.olbius.basehr.util.PartyUtil"].getListOrgManagedByParty(delegator, userLogin.userLoginId)/>
</#if>

var globalObject = (function(){
	<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
	return{
		createJqxTreeDropDownBtn${defaultSuffix?if_exists}:createJqxTreeDropDownBtn${defaultSuffix?if_exists}
	}
}());

var globalVar = {
		monthStart: ${monthStart.getTime()},
		monthEnd: ${monthEnd.getTime()},
		rootPartyArr: [
   			<#if rootOrgList?has_content>
   				<#list rootOrgList as rootOrgId>
   				<#assign rootOrg = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
   				{
   					partyId: "${rootOrgId}",
   					partyName: "${rootOrg.groupName}"
   				},
   				</#list>
   			</#if>
   		],
};
var uiLabelMap = {
		AddNewRowConfirm: "${uiLabelMap.AddNewRowConfirm}",
		CommonSubmit: "${uiLabelMap.CommonSubmit}",
		CommonCancel: "${uiLabelMap.CommonCancel}",
		FieldRequired: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}',
		EmplFulfillmentPosition: '${uiLabelMap.EmplFulfillmentPosition}',
		AssignPosForEmpl: "${StringUtil.wrapString(uiLabelMap.AssignPosForEmpl)}",
		HRNotYet: "${StringUtil.wrapString(uiLabelMap.HRNotYet)}",
		EmployeeId: '${uiLabelMap.EmployeeId}',
		FromDateFulfillment: '${uiLabelMap.FromDateFulfillment}',
		PositionActualFromDate: '${uiLabelMap.PositionActualFromDate}',
		EmployeeName: '${uiLabelMap.EmployeeName}',
		HrCommonPosition: '${uiLabelMap.HrCommonPosition}',
		CommonDepartment: '${uiLabelMap.CommonDepartment}',
		DateJoinCompany: '${uiLabelMap.DateJoinCompany}',
		EnterEmployeeId: "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}",
		AssignEmplToPositionConfirm: "${StringUtil.wrapString(uiLabelMap.AssignEmplToPositionConfirm)}",
		CommonThruDate : "${StringUtil.wrapString(uiLabelMap.CommonThruDate)}",
		ValueMustBeGreateThanZero : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}",
		ValueMustBeGreateThanZero : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}",
		ValueMustBeGreateThanEffectiveDate : "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanEffectiveDate)}",
		CommonClose : "${StringUtil.wrapString(uiLabelMap.CommonClose)}",
		CommonGroup : "${StringUtil.wrapString(uiLabelMap.CommonGroup)}",
		CommonDescription : "${StringUtil.wrapString(uiLabelMap.CommonDescription)}",
		HREmplPositionTypeId : "${StringUtil.wrapString(uiLabelMap.HREmplPositionTypeId)}",
};
<#assign rowsDetails = "function (index, parentElement, gridElement, datarecord){
	var partyId = datarecord.partyId;
	var emplPositionTypeId = datarecord.emplPositionTypeId;
	var url = 'getPositionByPositionTypeAndParty';
	var id = datarecord.uid.toString();
	var grid = $($(parentElement).children()[0]);
	var gridId = 'jqxgridDetail_'+ id;
    $(grid).attr('id', gridId);
    var selectionRange = $('#dateTimeInput').jqxDateTimeInput('getRange');
    var fromDate;
    var thruDate;
    if(selectionRange){
    	fromDate = selectionRange.from.getTime();
    	thruDate = selectionRange.to.getTime();
    }
    var jqxGridDetailsSource = {
    		datafields: [
    			{name: 'emplPositionId', type: 'string'},
    			{name: 'description', type: 'string'},
    			{name: 'employeePartyId', type: 'string'},
    			{name: 'partyId', type: 'string'},
    			{name: 'partyCode', type: 'string'},
    			{name: 'employeePartyName', type: 'string'},
    			{name: 'fromDate', type: 'date', other: 'Timestamp'},
    			{name: 'thruDate', type: 'date', other: 'Timestamp'},
    			{name: 'actualFromDate', type: 'date', other: 'Timestamp'},
    			{name: 'actualThruDate', type: 'date', other: 'Timestamp'},
    		],
    		cache: false,
    		datatype: 'json',
			type: 'POST',
			data: {partyId: partyId, emplPositionTypeId: emplPositionTypeId, fromDate: fromDate, thruDate: thruDate},
	        url: url,
	        
	        beforeprocessing: function (data) {
	        	jqxGridDetailsSource.totalrecords = data.TotalRows;
	        },
	        pagenum: 0,
	        pagesize: 5,
	        root: 'listReturn'
    };
    var nestedGridColums = [
		{text: '${uiLabelMap.EmplFulfillmentPosition}', datafield: 'employeePartyName', width: 180,
			cellsrenderer: function(row, column, value){
				var data = grid.jqxGrid('getrowdata', row);
				var gridId = grid.attr('id');
				if(data.employeePartyId){
					return '<span>' + value +'</span>';
				}else{
					return '<span><a href=\"javascript:void(0)\" onclick=\"assignPositionForEmpl(' + gridId + ', ' + row +')\" title=\"${StringUtil.wrapString(uiLabelMap.AssignPosForEmpl)}\" ><i class=\"icon-plus\"></i><i>${StringUtil.wrapString(uiLabelMap.HRNotYet)}</i></a></span>';
				}
			}	
		},
		{text: '${uiLabelMap.EmployeeId}', datafield: 'partyCode', width: 130},
		{text: '${uiLabelMap.FromDateFulfillment}', datafield: 'fromDate', cellsalign: 'left', width: 170, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
		{text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', cellsalign: 'left', width: 130, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
		{text: '${uiLabelMap.PositionActualFromDate}', datafield: 'actualFromDate', cellsalign: 'left', cellsformat: 'dd/MM/yyyy ', columntype: 'template'}
	];
    var nestedGridAdapter = new $.jqx.dataAdapter(jqxGridDetailsSource);
    if (grid != null) {
    	grid.jqxGrid({
    		source: nestedGridAdapter, 
    		width: '96%', 
    		height: 220,
    		autoheight: false,
    		virtualmode: true,
    		showtoolbar: true,
    		localization: getLocalization(),
    		rendertoolbar: function (toolbar) {
				var container = $(\"<div id='toolbarcontainer' class='widget-header'><h4>\" + \"</h4></div>\");
				toolbar.append(container);
				container.append('<button id=\"viewInWindow\" class=\"grid-action-button fa-eye\" style=\"margin-left:20px;\">${uiLabelMap.ViewInWindow}</button>');
				var buttonView = $('#viewInWindow');
				buttonView.click(function(){
					initOpenJqxWindow(partyId, emplPositionTypeId, datarecord.emplPositionTypeDesc, datarecord.partyName);
				});
    		},
    		rendergridrows: function () {
	            return nestedGridAdapter.records;
	        },
	        pageSizeOptions: ['5', '10', '15', '50'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: nestedGridColums,
	        selectionmode: 'singlerow',
	        theme: 'olbius'
    	});
    }
}
"/> 

globalVar.emplPositionTypeArr =[
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
			{
				emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
				description: '${StringUtil.wrapString(emplPositionType.description?if_exists)}'
			},
		</#list>
	</#if>                                    
];		

globalVar.partyGroupId = '${Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)}';

function refreshGridData(partyId, fromDate, thruDate){
	if(typeof(partyId) != 'undefined' && typeof(fromDate) != 'undefined' 
			&& typeof(thruDate) != 'undefined' && partyId.length > 0){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=getListEmplPositionInOrg&hasrequest=Y&partyGroupId=" + partyId + "&actualFromDate=" + fromDate + "&actualThruDate=" + thruDate;
		$("#jqxgrid").jqxGrid('source', tmpS);
	}
}

function initOpenJqxWindow(partyId, emplPositionTypeId, emplPositionTypeDesc, groupName){
	var selectionRange = $('#dateTimeInput').jqxDateTimeInput('getRange');
	var fromDate;
	var thruDate;
	if(selectionRange){
		fromDate = selectionRange.from.getTime();
		thruDate = selectionRange.to.getTime();
	}
    var source = $("#jqxGridEmplPosition").jqxGrid("source");
    source._source.data = {partyId: partyId, emplPositionTypeId: emplPositionTypeId, fromDate: fromDate, thruDate: thruDate};
    $("#jqxGridEmplPosition").jqxGrid("source", source);
    var title = "${StringUtil.wrapString(uiLabelMap.EmplListHavePositionType)} " + emplPositionTypeDesc + " ${StringUtil.wrapString(uiLabelMap.CommonOf)} " + groupName;
   	setWindowTitle($("#jqxWindowPositionDetail"), title);
   	openJqxWindow($("#jqxWindowPositionDetail"));
}

function setWindowTitle(jqxWindowDiv, title){
	jqxWindowDiv.jqxWindow('setTitle', title);
}
</script>