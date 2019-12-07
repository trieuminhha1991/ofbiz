<#assign dataField="[
		{name: 'customTimePeriodId', type: 'string'},
		{ name: 'periodName', type: 'string' },
		{ name: 'fromDate', type: 'date'},
		{ name: 'thruDate', type: 'date'},
		{ name: 'isClosed', type: 'string' }
]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCPeriod}', datafield: 'customTimePeriodId', width: 100,
						cellsrenderer: function(row, column, value){
							return '<span><a href=ListAllocatedPE?customTimePeriodId=' + value + '>' + value + '</a></span>';
						},
					 },
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'periodName' },
					 { text: '${uiLabelMap.BACCFromDate}', dataField: 'fromDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCThruDate}', dataField: 'thruDate', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCDepIsClosed}', dataField: 'isClosed', width: 150,  filtertype: 'checkedlist',
						 cellsrenderer: function(row, column, value){
							for(var i = 0; i < booleanData.length; i++){
								if(value == booleanData[i].value){
									return '<span title=' + value + '>' + booleanData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						 },
						 createfilterwidget: function (column, columnElement, widget) {
			   				var uniqueRecords2 = [] ;
			   				if(booleanData && booleanData.length > 0 ){
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(booleanData, {
				                    autoBind: true
				                });
				                uniqueRecords2 = filterBoxAdapter2.records;
			   				}
			   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'value'});			   				
						}
					 }
				  "/>

<@jqGrid id="peAllocGrid" customTitleProperties="BACCListPEAllocPeriods" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewPEAllocPeriod.openWindow()" customcontrol2="fa-cog open-sans@${uiLabelMap.BACCReady}@javascript: void(0);@postTrans()" filtersimplemode="true" filterable="true" editable="false" addType="popup" showtoolbar="true"
		 url="jqxGeneralServicer?sname=JqxGetListPEAllocPeriods" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	/>
<script>
	postTrans = function(){
		var rowindex = $('#peAllocGrid').jqxGrid('getselectedrowindex');
		var data = $('#peAllocGrid').jqxGrid('getrowdata', rowindex);
		
		if(data.isClosed == 'Y'){
			accutils.confirm.confirm("${uiLabelMap.BACCPeriodClosed}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
		}else{
			var submitedData = {};
			submitedData['customTimePeriodId'] = data.customTimePeriodId;
			
			//Send Ajax Request
			$.ajax({
				url: 'postPEAllocTrans',
				type: "POST",
				data: submitedData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
						$('#peAllocGrid').jqxGrid('updatebounddata');
						accutils.confirm.confirm("${uiLabelMap.BACCPosted}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}else if(data._ERROR_MESSAGE_LIST_){
						accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
					}
				}
			});
		}
	}
</script>