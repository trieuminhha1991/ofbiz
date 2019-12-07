<#--=================================Init Grid======================================================-->
<#assign dataField="[
						{ name: 'decrementId', type: 'string' },
						{ name: 'decrementTypeId', type: 'string'},
						{ name: 'decreasedDate', type: 'date'},
						{ name: 'description', type: 'string'},
						{ name: 'isClosed', type: 'string'}
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.BACCFixedAssetDecId}', dataField: 'decrementId', width: 150, pinned: true,
						cellsrenderer: function(row, column, value){
							return '<span><a href=ListDecreasedEquip?decrementId=' + value + '>' + value + '</a></span>';
						},
		             },
					 { text: '${uiLabelMap.BACCDecreasedDate}', dataField: 'decreasedDate', width: 200, filtertype: 'range' ,cellsformat: 'dd/MM/yyyy'},
					 { text: '${uiLabelMap.BACCDescription}', dataField: 'description'},
					 { text: '${uiLabelMap.BACCDecIsClosed}', dataField: 'isClosed', width: 150, filtertype: 'checkedlist',
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

<@jqGrid id="equipDecGrid" customcontrol1="icon-plus open-sans@${uiLabelMap.accAddNewRow}@javascript: void(0);@OLBNewEquipDec.openWindow()" customcontrol2="fa-cog open-sans@${uiLabelMap.BACCReady}@javascript: void(0);@postTrans()" filtersimplemode="true" editable="false" addType="popup" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListEquipDecrements" dataField=dataField columnlist=columnlist
		 />
                     
<#--=================================/Init Grid======================================================-->
<script>
	postTrans = function(){
		var rowindex = $('#equipDecGrid').jqxGrid('getselectedrowindex');
		var data = $('#equipDecGrid').jqxGrid('getrowdata', rowindex);
		if(data !== undefined)
		{
			if(data.isClosed == 'Y'){
				accutils.confirm.confirm("${uiLabelMap.BACCPeriodClosed}", function(){}, '${uiLabelMap.wgcancel}', '${uiLabelMap.wgok}');
			}else{
				var submitedData = {};
				submitedData['decrementId'] = data.decrementId;
				
				//Send Ajax Request
				$.ajax({
					url: 'postEquipDecTrans',
					type: "POST",
					data: submitedData,
					dataType: 'json',
					async: false,
					success : function(data) {
						if(!data._ERROR_MESSAGE_ && !data._ERROR_MESSAGE_LIST_){
							$('#equipDecGrid').jqxGrid('updatebounddata');
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