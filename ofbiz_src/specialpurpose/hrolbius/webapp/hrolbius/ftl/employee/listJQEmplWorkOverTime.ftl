<#--<!-- <#assign listStt = delegator.findList("StatusItem",null,null,null,null,false) !>
<#assign listPs = delegator.findList("Person",null,null,null,null,false) !>
<script type="text/javascript">
var arrStt = [

			<#list listStt as stt>
				{
					sttId : "${stt.statusId?if_exists}",
					description : "${StringUtil.wrapString(stt.description?if_exists)}"
				},
			</#list>	
			];
	var person = [
	<#list listPs as ps>
	<#assign pos = Static["com.olbius.util.PartyUtil"].getCurrPosTypeOfEmplOverview(delegator,ps.partyId) !>
	<#assign deptId = Static["com.olbius.util.PartyUtil"].getDepartmentOfEmployee(delegator,ps.partyId) !>
	<#if deptId?exists>
		<#assign Pg = delegator.findOne("PartyGroup",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId",deptId.partyIdFrom?if_exists),false) !>
	</#if>
		{
			partyId : "${ps.partyId?if_exists}",
			name : "${StringUtil.wrapString(ps.lastName?default(''))} ${StringUtil.wrapString(ps.middleName?default(''))} ${StringUtil.wrapString(ps.firstName?default(''))}",
			position : "${pos?if_exists}",
			department : "${Pg.groupName?if_exists}"			
		},
	</#list>	
	];
</script>
<#assign dataField ="[
					{name : 'partyId' , type : 'string'},
					{name : 'dateRegistration' , type : 'date',other: 'Date'},
					{name : 'statusId' , type : 'string'},
					{name : 'dateRegistration', type : 'date'},
					{name : 'overTimeFromDate' , type : 'date'},
					{name : 'overTimeThruDate', type : 'date'},
					{name : 'reasonRegister' ,type : 'string'},
					{name : 'reasonApproval' , type : 'string'}
]"/>
<#assign columnlist="	{text : '${uiLabelMap.EmployeeId}' , dataField : 'partyId',width : '10%',cellsrenderer : 
							function(row,columnfield,value){
								for(var i = 0;i < person.length ; i++){
									if(person[i].partyId == value){
										return '<span><a href=\"EmployeeProfile?partyId='+ value +'\">'+ value +'</a></span>';
									}
								}
							}
						},
						{text : '${uiLabelMap.EmployeeName}',width : '10%',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
							var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
								for(var i = 0;i < person.length ; i++){
									if(person[i].partyId == data.partyId){
										return '<span>' + person[i].name + '</span>';
									}
								}
							}
						},
						{text : '${uiLabelMap.HREmplFromPositionType}',width : '10%',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
								var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
								for(var i = 0;i < person.length ; i++){
									if(person[i].partyId == data.partyId){
										return '<span>' + person[i].position + '</span>';
									}
								}
							}
						},
						{text : '${uiLabelMap.EmployeeCurrentDept}',width : '10%',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
								var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
								for(var i = 0;i < person.length ; i++){
									if(person[i].partyId == data.partyId){
										return '<span>' + person[i].department + '</span>';
									}
								}
							}
						},
						{text : '${uiLabelMap.HRWorkOvertimeDateRegis}' , dataField : 'dateRegistration',width : '10%',filtertype : 'range',cellsformat : 'dd/MM/yyyy'},
						{text : '${uiLabelMap.EmplProposalCheck}' , dataField : 'statusId',width : '10%',filterable : false,cellsrenderer : 
							function(row,columnfield,value){
								for(var i = 0 ;i < arrStt.length ; i++){
									if(value == arrStt[i].sttId){
										return '<span>'+ arrStt[i].description +'</span>'
									}
								}
							}
						},
						{text : '${uiLabelMap.HREmplOvertimeDateRegis}' , dataField : 'dateRegistration'},
						{text : '${uiLabelMap.HREmplOverTimeFromDate}' ,filterable : false, width : '10%',dataField : 'overTimeFromDate',filtertype : 'range',cellsformat : 'hh:mm:ss tt'},
						{text : '${uiLabelMap.HREmplOverTimeThruDate}' ,filterable : false,width : '10%',dataField : 'overTimeThruDate',filtertype : 'range',cellsformat : 'hh:mm:ss tt',width : '110px'},
						{text : '${uiLabelMap.HREmplReasonRegisOvertime}' , dataField : 'reasonRegister',width : '10%',filterable : false},
						{text : '${uiLabelMap.HREmplReasonAcceptReject}' , dataField : 'reasonApproval',width : '10%',filterable : false}
"/>

<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmplWorkOverTime" /> -->