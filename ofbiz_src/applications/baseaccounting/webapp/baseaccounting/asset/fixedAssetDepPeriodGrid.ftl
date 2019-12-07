<#assign dataField="[
		{name: 'customTimePeriodId', type: 'string'},
		{ name: 'periodName', type: 'number' },
		{ name: 'fromDate', type: 'date', other:'Timestamp' },
		{ name: 'thruDate', type: 'date', other:'Timestamp' },
		{ name: 'isClosed', type: 'number' }
]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCDepCustomTimePeriodId}',filterable : false, datafield: 'customTimePeriodId', width: 180,
						cellsrenderer: function(row, column, value){
							return '<span><a href=ListDepFixedAssets?customTimePeriodId=' + value + '>' + value + '</a></span>';
						},
					 },
					 { text: '${uiLabelMap.BACCDepPeriodName}', dataField: 'periodName' },
					 { text: '${uiLabelMap.CommonFromDate}', dataField: 'fromDate', width: 150, cellsformat: 'dd/MM/yyyy' },
					 { text: '${uiLabelMap.CommonThruDate}', dataField: 'thruDate', width: 150, cellsformat: 'dd/MM/yyyy' },
					 { text: '${uiLabelMap.BACCDepIsClosed}', dataField: 'isClosed', width: 150,
						 cellsrenderer: function(row, column, value){
							for(var i = 0; i < booleanData.length; i++){
								if(value == booleanData[i].value){
									return '<span title=' + value + '>' + booleanData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						 }
					 }
				  "/>

<@jqGrid id="jqxgridDepPeriod" customTitleProperties="BACCListFADepPeriods" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewDepPeriod.openWindow()" customcontrol2="fa-cog open-sans@${uiLabelMap.BACCReady}@javascript: void(0);@postTrans()" filtersimplemode="true" filterable="false" editable="false" addType="popup" showtoolbar="true"
		 url="jqxGeneralServicer?sname=JqxGetListDepPeriods" dataField=dataField columnlist=columnlist
		/>
<script>
	postTrans = function(){
		var rowindex = $('#jqxgridDepPeriod').jqxGrid('getselectedrowindex');
		if(rowindex != -1)
		{
			var data = $('#jqxgridDepPeriod').jqxGrid('getrowdata', rowindex);
			
			if(data.isClosed == 'Y'){
				accutils.confirm.confirm("${uiLabelMap.BACCPeriodClosed}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
			}else{
				var submitedData = {};
				submitedData['customTimePeriodId'] = data.customTimePeriodId;
				
				//Send Ajax Request
				$.ajax({
					url: 'postFADepTrans',
					type: "POST",
					data: submitedData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
							accutils.confirm.confirm("${uiLabelMap.BACCPosted}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
							$('#jqxgridDepPeriod').jqxGrid('updatebounddata');
						}else if(data._ERROR_MESSAGE_){
							accutils.confirm.confirm(data._ERROR_MESSAGE_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
						}else if(data._ERROR_MESSAGE_LIST_){
							accutils.confirm.confirm(data._ERROR_MESSAGE_LIST_, function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
						}
					}
				});
			}
		}	
	}
</script>