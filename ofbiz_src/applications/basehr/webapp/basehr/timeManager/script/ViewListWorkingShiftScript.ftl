<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/hrresources/js/CommonFunction.js" type="text/javascript"></script>
<script type="text/javascript">
var workingShiftWorkTypeArr = [
	<#if workingShiftWorkTypeList?has_content>        
		<#list workingShiftWorkTypeList as workingShiftWorkType>
		{
			workTypeId: "${workingShiftWorkType.workTypeId}",
			description: "${StringUtil.wrapString(workingShiftWorkType.description?if_exists)}",
			sign: '${StringUtil.wrapString(workingShiftWorkType.sign?if_exists)}'
		},	
		</#list>
	</#if>
];

var globalVar = {
		<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
		editable: true,
		<#else>
		editable: false,
		</#if>
		editColumns: "<#if dayOfWeekList?has_content><#list dayOfWeekList as dayOfWeek>${dayOfWeek.dayOfWeek};</#list></#if>workingShiftId"
};

var dayOfWeekArr = [
		<#if dayOfWeekList?has_content>
			<#list dayOfWeekList as dayOfWeek>
			{
				dayOfWeek: "${dayOfWeek.dayOfWeek}",
				description: "${StringUtil.wrapString(dayOfWeek.description?if_exists)}"
			},		
			</#list>
		</#if>
];
globalVar.getAllWorkingShift = true;
globalVar.allWorkingShiftArr = [];

var workingShiftWorkTypeArr = [
	<#if workingShiftWorkTypeList?has_content>
		<#list workingShiftWorkTypeList as workingShiftWorkType>
		{
			workTypeId: "${workingShiftWorkType.workTypeId}",
			description: '${StringUtil.wrapString(workingShiftWorkType.description)}'
		},
		</#list>
	</#if>
];

var globalObject = (function(){
	var getDayOfWeekList = function(){
		var retData = [
			<#if dayOfWeekList?has_content>
				<#list dayOfWeekList as dayOfWeek>
					{name: '${dayOfWeek.dayOfWeek}', type: 'string'},
				</#list>
			</#if>  
	    ];
		return retData;
	};
	
	var getColumnGridCreateNewWS = function(){
		var retData = [
	   		<#if dayOfWeekList?has_content>
	   			<#list dayOfWeekList as dayOfWeek>
	   				{text: '${StringUtil.wrapString(dayOfWeek.description)}', datafield: '${dayOfWeek.dayOfWeek}', 
	   					<#if dayOfWeek_has_next>width: 110,</#if> columntype: 'dropdownlist',
	   					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
	   						for(var i = 0; i < workingShiftWorkTypeArr.length; i++){
	   							if(value == workingShiftWorkTypeArr[i].workTypeId){
	   								return '<span>' + workingShiftWorkTypeArr[i].description +'</span>';
	   							}
	   						}
	   						return '<span>' + value +'</span>';
	   					},
	   					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
	   				        var dataSource = {
	   				        		localdata: workingShiftWorkTypeArr,
	   				                datatype: "array"
	   				        };
	   				        var dataAdapter = new $.jqx.dataAdapter(dataSource);
	   				        editor.jqxDropDownList({source: dataAdapter,  displayMember: "description", valueMember: "workTypeId", autoDropDownHeight: true});
	   				    },
	   				 geteditorvalue: function (row, cellvalue, editor) {
	   				    // return the editor's value.
	   				    return editor.val();
	   				}
	   				},
	   			</#list>
	   		</#if>
	   		{hidden: true, datafield: 'workingShiftId'}
	   	];
		return retData;
	};
	
	var getDayWeekArr = function(){
		var retData = [
			<#if dayOfWeekList?has_content>
				<#list dayOfWeekList as dayOfWeek>
					"${dayOfWeek.dayOfWeek}",
				</#list>
			</#if>
		];
		return retData;
	};
	return{
		getDayOfWeekList: getDayOfWeekList,
		getColumnGridCreateNewWS: getColumnGridCreateNewWS,
		getDayWeekArr: getDayWeekArr
	}
}());

var uiLabelMap = {};
uiLabelMap.WorkingShifWorkTypeWorkWeek = "${StringUtil.wrapString(uiLabelMap.WorkingShifWorkTypeWorkWeek)}";
uiLabelMap.WorkingShiftInfo = "${StringUtil.wrapString(uiLabelMap.WorkingShiftInfo)}";
uiLabelMap.CreateNewWorkingShiftConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewWorkingShiftConfirm)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.OrganizationUnit = "${StringUtil.wrapString(uiLabelMap.OrganizationUnit)}";
uiLabelMap.OrgUnitName = "${StringUtil.wrapString(uiLabelMap.OrgUnitName)}";
uiLabelMap.OrgUnitId = "${StringUtil.wrapString(uiLabelMap.OrgUnitId)}";
uiLabelMap.HrCommonWorkingShift = "${StringUtil.wrapString(uiLabelMap.HrCommonWorkingShift)}";
uiLabelMap.MustntHaveSpaceChar = "${StringUtil.wrapString(uiLabelMap.MustntHaveSpaceChar)}";
uiLabelMap.InvalidChar = "${StringUtil.wrapString(uiLabelMap.InvalidChar)}";

</script>