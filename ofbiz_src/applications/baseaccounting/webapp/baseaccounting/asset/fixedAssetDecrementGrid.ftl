<#--=================================Init Grid======================================================-->
<#assign dataField="[
						{ name: 'decrementId', type: 'string' },
						{ name: 'decrementTypeId', type: 'string'},
						{ name: 'enumId', type: 'string'},
						{ name: 'decreasedDate', type: 'date', other:'Timestamp'},
						{ name: 'description', type: 'string'},
						{ name: 'isClosed', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCFixedAssetDecId}', dataField: 'decrementId', width: 150, pinned: true,
						cellsrenderer: function(row, column, value){
							return '<span><a href=ListDecFixedAssets?decrementId=' + value + '>' + value + '</a></span>';
						},
		             },
					 { text: '${uiLabelMap.BACCDecrementReasonTypeId}', dataField: 'enumId', width: 200, pinned: true,  filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < enumData.length; i++){
								if(value == enumData[i].enumId){
									return '<span title=' + value + '>' + enumData[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						},
						createfilterwidget: function (column, columnElement, widget) {
			   				var uniqueRecords2 = [] ;
			   				if(enumData && enumData.length > 0 ){
			   					var filterBoxAdapter2 = new $.jqx.dataAdapter(enumData,
						                {
						                    autoBind: true
						                });
				                uniqueRecords2 = filterBoxAdapter2.records;
			   				}
			   				widget.jqxDropDownList({ filterable:true,source: uniqueRecords2, displayMember: 'description', valueMember : 'enumId'});			   				
						}
					 },
					 { text: '${uiLabelMap.BACCDecreasedDate}', dataField: 'decreasedDate', width: 200, filtertype: 'range' ,cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'description'},
					 { text: '${uiLabelMap.BACCDecIsClosed}', dataField: 'isClosed', width: 150,
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

<@jqGrid id="faDecGrid" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewFADec.openWindow()" customcontrol2="fa-cog open-sans@${uiLabelMap.BACCReady}@javascript: void(0);@postTrans()" filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListFADecrements" dataField=dataField columnlist=columnlist
		 />
                     
<#--=================================/Init Grid======================================================-->
<script>
	postTrans = function(){
		var rowindex = $('#faDecGrid').jqxGrid('getselectedrowindex');
		
		if(rowindex != -1)
		{
				var data = $('#faDecGrid').jqxGrid('getrowdata', rowindex);
				
				if(data.isClosed == 'Y'){
					accutils.confirm.confirm("${uiLabelMap.BACCPeriodClosed}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
				}else{
					var submitedData = {};
					submitedData['decrementId'] = data.decrementId;
					
					//Send Ajax Request
					$.ajax({
						url: 'postFADecTrans',
						type: "POST",
						data: submitedData,
						dataType: 'json',
						async: false,
						success : function(data) {
							if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
								$('#faDecGrid').jqxGrid('updatebounddata');
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
	}
</script>	