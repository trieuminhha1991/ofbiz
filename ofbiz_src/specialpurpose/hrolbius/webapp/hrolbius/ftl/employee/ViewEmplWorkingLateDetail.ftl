<#assign listPs = delegator.findList("Person",null,null,null,null,false) !>

<script type="text/javascript">
	var psName = [
		<#list listPs as ps>
		{
			partyId : "${ps.partyId?if_exists}",
			name : "${StringUtil.wrapString(ps.lastName?default(''))} ${StringUtil.wrapString(ps.middleName?default(''))} ${StringUtil.wrapString(ps.firstName?default(''))}"
		},	
		</#list>
	];
</script>

<#assign dataField = "[
			{name : 'partyId' ,type : 'string'},
			{name : 'dateWorkingLate' ,type : 'date',other : 'Timestamp'},
			{name : 'delayTime' ,type : 'string'},
			{name : 'reason' ,type : 'string'}
]"/>

<#assign columnlist = "
					{text : '${uiLabelMap.EmployeeName}',dataField : 'partyId',filterable : false,cellsrenderer : 
						function(row,columnfield,value){
							for(var i =0 ; i < psName.length ; i++){
								if(value == psName[i].partyId){
									return '<span>' + psName[i].name + ' <a href=\"EmployeeProfile?partyId='+ value +'\">['+ value +']</a></span>';
								}							
							}
						}
					},
					{text : '${uiLabelMap.HRDateWorkingLate}',dataField : 'dateWorkingLate',cellsformat : 'dd/MM/yyyy',filtertype : 'range'},
					{text : '${uiLabelMap.HRDelayTime} (${uiLabelMap.HRMinute})',dataField : 'delayTime',filterable : false},
					{text : '${uiLabelMap.HRReasonLate}',dataField : 'reason'}
"/>

<@jqGrid filtersimplemode="true" filterable="true"  addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQgetInfoDetailsEmplworkingLate&partyId=${parameters.partyId}" 
		/>	
		
	
		
