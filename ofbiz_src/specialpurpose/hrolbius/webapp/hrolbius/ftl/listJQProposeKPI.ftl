 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <#assign listEmplType = delegator.findList("EmplPositionType",null,null,null,null,false) !>
 <#assign listStatus = delegator.findList("StatusItem",null,null,null,null,false) !>
<#assign dataField="[
						{ name: 'KPIId', type: 'string'},
						{ name: 'emplPositionTypeId', type: 'string'},
						{ name: 'description', type: 'string'},
						{ name: 'fromDate', type: 'date',other : 'Timestamp'},
						{ name: 'thruDate', type: 'date',other : 'Timestamp'},
						{ name: 'jobStatus', type: 'string'},
						{ name: 'proposeKPI', type: 'string'}
					]"/>
<#assign columnlist= " {
						text : '${uiLabelMap.KPIId}',dataField : 'KPIId',filtertype : 'checkedlist',filterable : false,width : '70px',cellsrenderer : 
							function(row,columnfield,value){
									var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
									return '<a href=\"'+ 'StandardRating?emplPositionTypeId=' + data.emplPositionTypeId +'\">'+ value +'</a>';	
								}
						},
						{ 
						text : '${uiLabelMap.HROlbiusEmplPositionId}' ,dataField : 'emplPositionTypeId',filtertype : 'checkedlist',cellsrenderer : 
							function(row,columnfield,value){
								for(var i =0 ;i < ArrEmplType.length ;i++){
									if(ArrEmplType[i].emplType == value){
									 return '<span>'+ArrEmplType[i].description+'</span>';
									}					
								}
							},createfilterwidget : function(column,columnElement,widget){
								var source = {
									localdata : listDetailEmplType,
									datatype : \"array\"
								};
								var dataAdapter = new $.jqx.dataAdapter(source,{
										autoBind : true									
									});
								var records = dataAdapter.records;
								records.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({selectedIndex : 0,source : records,displayMember : 'description' , valueMember : 'emplPositionTypeId',dropDownWidth : 250,
									renderer : function(column,label,value){
										for(var i =0 ;i< listDetailEmplType.length;i++){
												if(listDetailEmplType[i].emplPositionTypeId == value){
													return '<span>'+listDetailEmplType[i].description+'</span>';
												}
											}
										return value;
									}								
								});
								widget.jqxDropDownList('checkAll');
							}
						},{
						text : '${uiLabelMap.description}' , dataField : 'description'
						},{
						text : '${uiLabelMap.fromDate}' , dataField : 'fromDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range'
						},{
						text : '${uiLabelMap.thruDate}' , dataField : 'thruDate',cellsformat : 'dd/MM/yyyy',filtertype : 'range'
						},{
						text : '${uiLabelMap.jobStatus}' , dataField : 'jobStatus',cellsrenderer : 
							function(row,columnfield,value){
								for(var i =0 ;i < stt.length ;i++){
									if(stt[i].sttId == value){
									 return '<span>'+stt[i].description+'</span>';
									}					
								}
							}
						},{
						text : '${uiLabelMap.PageTitleProposeKPI}' , dataField : 'proposeKPI',width : '80px',filterable: false,cellsrenderer : 
							function(row,columnfield,value){
							var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\",row);
								return '<a style=\"width : 30px;padding-bottom : 5px;\" class=\"btn btn-mini btn-position btn-primary open-sans icon-sign-in\" href=\"proposeKPI?emplPositionTypeId='+ data.emplPositionTypeId + '\"></a>';
							}
						}" />
						
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=dataField columnlist=columnlist  clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQgetListProposeKPI"
		 />
<script language="JavaScript" type="text/javascript">
		var data = '';
		var stt = [
			<#list listStatus as st>
					{
						sttId : "${st.statusId?if_exists}",
						description : "${StringUtil.wrapString(st.description?if_exists)}"
					},
			</#list>	
		];
		var ArrEmplType = [
			<#list listEmplType as type>
					{
						emplType : "${type.emplPositionTypeId?if_exists}",
						description : "${StringUtil.wrapString(type.description?if_exists)}"
					},
			</#list>
		];
		var listDetailEmplType = [];
		<#assign MapEmplOfManager = Static["com.olbius.util.PartyUtil"].getListEmplPositionInOrgOfManager(delegator,userLogin.partyId) !>
	 	<#list MapEmplOfManager.entrySet() as entry>
	 			var tmp = {
				 		emplPositionTypeId :  '${entry.key}',
				 		description : '${entry.value}'	
				 	};
			if(tmp){
				listDetailEmplType.push(tmp);
			}	 	
	 	</#list>
</script>
