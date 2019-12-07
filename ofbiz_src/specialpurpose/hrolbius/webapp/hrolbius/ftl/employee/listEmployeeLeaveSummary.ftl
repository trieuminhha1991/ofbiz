<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<#assign listPerson  = delegator.findList("Person",null,null,null,null,false)/>
<script type="text/javascript">
	var listPs = [
		<#list listPerson as ps>
			{
				partyId : "${ps.partyId?if_exists}",
				name : "${StringUtil.wrapString(ps.firstName?if_exists)} ${StringUtil.wrapString(ps.middleName?if_exists)} ${StringUtil.wrapString(ps.lastName?if_exists)}"
			},
		</#list>	
	]; 
	var showDetail  = function(id,value){
			$('#' + id).jqxTooltip({content : value,position : 'right'});
		};
	var showDetailHr  = function(id,value){
		$('#' + id).jqxTooltip({content : value,position : 'right'});
	};
	var processData = function(data){
		var newData = '';
		if(data.length > 40){
			newData = data.substr(0,40) + '...';
		}else {
			newData = data;
		}
		return newData;
	}
</script>
<#assign dataField = "[{ name : 'partyId' , type : 'string' },
						{ name : 'currPositionsStr' , type : 'string'},
						{ name : 'currDept' , type : 'string'},
						{ name : 'dayLeaveJan' , type : 'number'},
						{ name : 'dayLeaveFer' , type : 'number'},
						{ name : 'dayLeaveMar' , type : 'number'},
						{ name : 'dayLeaveApr' , type : 'number'},
						{ name : 'dayLeaveMay' , type : 'number'},
						{ name : 'dayLeaveJune' , type : 'number'},
						{ name : 'dayLeaveJuly' , type : 'number'},
						{ name : 'dayLeaveAug' , type : 'number'},
						{ name : 'dayLeaveSep' , type : 'number'},
						{ name : 'dayLeaveOct' , type : 'number'},
						{ name : 'dayLeaveNov' , type : 'number'},
						{ name : 'dayLeaveDec' , type : 'number'},
						{ name : 'dayLeaveRegulation' , type : 'number'},
						{ name : 'numberDayLeave' , type : 'number'},
						{ name : 'dayLeaveRemain' , type : 'number'},
						{ name : 'numberDayLeaveUnpaid' , type : 'number'}
						]"/>
<#assign columnlist = "
					{text : '${uiLabelMap.EmployeeName}', dataField : 'partyId',cellsrenderer :
						function(row,columnfield,value){
							for(var i = 0;i < listPs.length ; i++){
								if(listPs[i].partyId == value){
									return  '<a href=\"'+ 'EmployeeProfile?partyId=' + value  +'\">'+ listPs[i].name + '<br>' + value +'</a>' ;
								}
							}
						}
					},
					{text : '${uiLabelMap.HREmployeePosition}', dataField : 'currPositionsStr',cellsrenderer : 
						function(row,columnfield,value){
							var dtrow = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
							var data = processData(dtrow.currPositionsStr);
							return '<span id=\"' + row  + '\" onmouseenter = \"showDetail('+ \"'\"+ row + \"'\" +',' +\"'\" + data + \"'\" +')\">' + value +'</span>';	
						}
					},
					{text : '${uiLabelMap.EmployeeCurrentDept}', dataField : 'currDept',cellsrenderer : 
						function(row,columnfield,value){
							var dtrow = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
							var data = processData(dtrow.currDept);
							return '<span id=\"' + row  + '\" onmouseenter = \"showDetailHr('+ \"'\"+ row + \"'\" +',' +\"'\" + data + \"'\" +')\">' + value +'</span>';	
						}},
					{text : '${uiLabelMap.January}', dataField : 'dayLeaveJan',width : '3%'},
					{text : '${uiLabelMap.February}', dataField : 'dayLeaveFer',width : '3%'},
					{text : '${uiLabelMap.March}', dataField : 'dayLeaveMar',width : '3%'},
					{text : '${uiLabelMap.April}', dataField : 'dayLeaveApr',width : '3%'},
					{text : '${uiLabelMap.May}', dataField : 'dayLeaveMay',width : '3%'},
					{text : '${uiLabelMap.June}', dataField : 'dayLeaveJune',width : '3%'},
					{text : '${uiLabelMap.July}', dataField : 'dayLeaveJuly',width : '3%'},
					{text : '${uiLabelMap.August}', dataField : 'dayLeaveAug',width : '3%'},
					{text : '${uiLabelMap.September}', dataField : 'dayLeaveSep',width : '3%'},
					{text : '${uiLabelMap.October}', dataField : 'dayLeaveOct',width : '3%'},
					{text : '${uiLabelMap.November}', dataField : 'dayLeaveNov',width : '3%'},
					{text : '${uiLabelMap.December}', dataField : 'dayLeaveDec',width : '3%'},
					{text : '${uiLabelMap.DayLeaveRegulation}', dataField : 'dayLeaveRegulation'},
					{text :  '${uiLabelMap.numberDayLeave}', dataField : 'numberDayLeave'},
					{text : '${uiLabelMap.dayLeaveRemain}', dataField : 'dayLeaveRemain'},
					{text : '${uiLabelMap.numberDayLeaveUnpaid}', dataField : 'numberDayLeaveUnpaid'}
"/>
<@jqGrid filtersimplemode="true" filterable="false" addType="popup" rowsheight="50"  dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmplSummary" />