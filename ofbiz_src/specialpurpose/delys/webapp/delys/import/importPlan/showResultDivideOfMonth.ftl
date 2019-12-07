<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<#assign productPlanId = parameters.productPlanId !>
<#assign listProductId = parameters.listProductId !>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
<script type="text/javascript">
	var listProduct = new Array();
	<#list listProductId as item>
		var row = {};
		row['palletProductId'] = "palletNew_${item.productId}";
		row['quantityConvert'] = "${item.quantityConvert}";
		row['quantityUomId'] = "${item.quantityUomId}";
		row['productId'] = "${item.productId}";
		listProduct[${item_index}] = row;
	</#list>
</script>
<div id="windowDivide">
	<div id="windowHeader">
		<span>${uiLabelMap.viewResult}</span>
	</div>
	<div style="overflow: hidden;">
		<div style="overflow: hidden;" id="windowContent" class="row-fluid">
			<div class="ps-container ps-active-x ps-active-y" style="overflow: auto; height: 330px;" id="containJqx">
				<#assign dataField="[{ name: 'week', type: 'string' }," +
						"{name: 'customTimePeriodId', type: 'string'}," +
						"{name: 'productPlanIdMonth', type: 'string'}," +
						"{name: 'productPlanIdWeek', type: 'string'}," +
						"{name: 'internalPartyId', type: 'string'}," +
						"{name: 'editPlanWeek', type: 'bool'}," +
						"{name: 'countCont', type: 'string' }," +
						"{name: 'rePallet', type: 'string' }"/>
				<#assign columnlist="{ text: '${uiLabelMap.DateTime}', datafield: 'week', width: 80, editable: false, filterable: false,
					cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
						if(index != 0){
							return '<div style=\"margin-top: 5px;\">${uiLabelMap.Week}: ' + value+ '</div>'
						}else{
							var arrm = value.split(':');
							return '<div style=\"margin-top: 5px;\">${uiLabelMap.Month}: ' + arrm[1] + '</div>'
						}
					},
					cellclassname: function (row, column, value, data) {
							    	return 'green1';
							}
						}," +
						"{ text: '${uiLabelMap.customTimePeriodId}', hidden: true, datafield: 'customTimePeriodId', editable: false, filterable: false, width: 60}," +
						"{ text: '${uiLabelMap.productPlanIdMonth}', hidden: true, datafield: 'productPlanIdMonth', editable: false, filterable: false, width: 60}," +
						"{ text: '${uiLabelMap.productPlanIdWeek}', hidden: true, datafield: 'productPlanIdWeek', editable: false, filterable: false, width: 60}," +
						"{ text: '${uiLabelMap.internalPartyId}', hidden: true, datafield: 'internalPartyId', editable: false, filterable: false, width: 60}," +
						"{ text: '${uiLabelMap.CountCont}', datafield: 'countCont', editable: false, filterable: false, width: 90,
						 		cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
			                          var total = 0;
			                          for(var i = 0; i < listProduct.length; i++){
			                          	var productId = listProduct[i].palletProductId;
			                          	if(rowdata[productId]){
			                          		total = total + parseFloat(rowdata[productId]);
			                          	
			                          	}
			                          }
			                          var result = parseInt(total/33);
			                          return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +result.toLocaleString('${localeStr}')+ '</div>';
			                      },
			                      cellclassname: function (row, column, value, data) {
			      				    	return 'green1';
			      				}
						},
						{ text: '${uiLabelMap.RePalletCont}', datafield: 'rePallet', editable: false, filterable: false, width:105, 
						 	cellsrenderer: function (index, datafield, value, defaultvalue, column, rowdata) {
			                          var total = 0;
			                          for(var i = 0; i < listProduct.length; i++){
			                          	var productId = listProduct[i].palletProductId;
			                          	if(rowdata[productId]){
			                          		total = total + parseFloat(rowdata[productId]);
			                          	
			                          	}
			                          }
			                          var result = total%33;
			                          if(result == 0){
			                        	  return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +result.toLocaleString('${localeStr}')+ '</div>';
			                          }else{
			                        	  return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px; color: red;\">' +result.toLocaleString('${localeStr}')+ '</div>';
			                          }
			                      },
			                      cellclassname: function (row, column, value, data) {
			      				    	return 'green1';
			      				}
						}
						" />
				<#assign columngrouplist = "" />
				<#assign size = listProductId?size />
				<#if size != 0>
					<#list listProductId as productId>
						<#assign columnlist = columnlist + "," />
						<#assign dataField= dataField + "," />
						<#assign dataField= dataField + "{name: 'palletNew_${productId.productId}', type: 'number'}," />
						<#assign dataField= dataField + "{name: 'palletOld_${productId.productId}', type: 'number'}" />
						<#assign columnlist = columnlist + "{ text: '${uiLabelMap.recentResult}', columngroup: '${productId.productId}', datafield: 'palletNew_${productId.productId}', editable: true, filterable: false, columntype: 'numberinput', width: 80, cellsalign: 'right',
								cellbeginedit : function (row, datafield, columntype, value) {
					                if (row == 0) {return false}else{
									var data = $('#jqxgridWeek').jqxGrid('getrowdata', row);
									return data.editPlanWeek;}
					            },
								cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
									if(row == 0){
										var rows = $('#jqxgridWeek').jqxGrid('getrows');
										var valueRow0 = 0;
										for(var i =1; i < rows.length; i++){
											var row = rows[i];
											if(row[columnfield]){
												valueRow0 = valueRow0 + parseInt(row[columnfield]);
											}
										}
										value = valueRow0;
										return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
									}else{
										if(value){
						     		   		return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
										}else{
											return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">0</div>';
										}
									}
								
				            },
								createeditor: function (row, cellvalue, editor) {
					                editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
					            },
					            cellendedit: function (row, columnfield, columntype, oldvalue, newvalue) {
					            	var rowsE = $('#jqxgridWeek').jqxGrid('getrows');
									var valueRowE0 = 0;
									for(var i =1; i < rowsE.length; i++){
										var rowE = rowsE[i];
										if(i == row){
											valueRowE0 = valueRowE0 + newvalue;
										}else{
											if(rowE[columnfield]){
												valueRowE0 = valueRowE0 + parseInt(rowE[columnfield]);
											}
										}
									}
					            	$('#jqxgridWeek').jqxGrid('setcellvalue', 0, columnfield, valueRowE0);
					            },
					            cellclassname: function (row, column, value, data) {
								    if(row == 0){
								    	return 'gray1';
								    }
								}
					            }," />
						<#assign columnlist = columnlist + "{ text: '${uiLabelMap.prevResult}', columngroup: '${productId.productId}', datafield: 'palletOld_${productId.productId}', editable: false, filterable: false, columntype: 'numberinput', width: 80, cellsalign: 'right',
									cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
										if(value){
						     		   		return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
										}else{
											return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">0</div>';
										}
						            },
						            cellclassname: function (row, column, value, data) {
									    //if(row == 0){
									    	return 'green1';
									    //}else{
									    //	return 'gray1';
									   // }
									}
					            }" />
						<#assign columngrouplist = columngrouplist + "{ text: '${productId.productName}', align: 'center', width: 160, name: '${productId.productId}'}" />
						<#if size - 1 != productId_index>
							<#assign dataField= dataField + "," />
							<#assign columnlist = columnlist + "," />
							<#assign columngrouplist = columngrouplist + "," />
						</#if>
					</#list>
				</#if>
				
				<#assign dataField = dataField + "]" />
				<@jqGrid filtersimplemode="true" id="jqxgridWeek" filterable="false" addType="" dataField=dataField editmode="selectedcell" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="false"
				columngrouplist=columngrouplist editable="true" bindresize="false" pageable="true" showlist="false" sortable="false"
				url="jqxGeneralServicer?sname=JQGetResultDivideOfMonth&productPlanId=${productPlanId}" height="357" width="980" statusbarheight="30"
				autoheight="false" columnsresize="false"
				/>
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity: 0.4;">
		<div class="row-fluid" style="margin-top: 4px; height: 75px;">
	 		<div class="">
		 		<button id='exit' class="btn-mini btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.Exit}</button>
		 		<button id='apply' class="btn-mini btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.Apply}</button>
	        </div>
        </div>
	</div>
</div>

<script type="text/javascript">
//customcontrol1="icon-save open-sans@${uiLabelMap.Save}@javascript: void(0);@btnSaveDivide()"
//	$('#containJqx').perfectScrollbar({
//	    wheelSpeed: 1,
//	    wheelPropagation: false
//	});
	$('#apply').on('click', function(){
		btnSaveDivide();
	});
	$('#exit').on('click', function(){
		$('#windowDivide').jqxWindow('close');
	});
	$('#windowDivide').jqxWindow({
	    showCollapseButton: false, theme:'olbius', resizable: false, zIndex: 9999,
	    isModal: true, autoOpen: false, height: 425, width: 1000, maxWidth: '90%',
	});
	var wtmp = window;
	var tmpwidth = $('#windowDivide').jqxWindow('width');
	$('#windowDivide').jqxWindow({
	    position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 }
	});
	
	$('#windowDivide').on('close', function (event) {
		$('#windowDivide').jqxWindow('destroy');
	});
	
	function btnSaveDivide(){
		var rows = $('#jqxgridWeek').jqxGrid('getrows');
		var jsonArrTotal = [];
		var jsonArr = [];
		for(var i = 0; i < rows.length; i++){
			var row = rows[i];
				var editPlanWeek = row.editPlanWeek;
				if(editPlanWeek){
					var productPlanHeader = row.productPlanIdMonth;
					var customTimePeriodId = row.customTimePeriodId;
					var productPlanName = '';
					var internalPartyId = row.internalPartyId;
					var json = [];
					for(var a = 0; a < listProduct.length; a++){
						var productId = listProduct[a].productId;
						var quantity = 0;
						var pallet = listProduct[a].palletProductId;
						var quantityConvert = listProduct[a].quantityConvert;
						var quantityUomId = listProduct[a].quantityUomId;
						if(row[pallet]){
							quantity = parseInt(row[pallet]) * parseInt(quantityConvert);
						}
						json.push({
							quantity: quantity,
							productId: productId,
							quantityUomId: quantityUomId
						});
					}
					jsonArr.push({
						productPlanHeader: productPlanHeader,
						customTimePeriodId: customTimePeriodId,
						productPlanName: productPlanName,
						internalPartyId: internalPartyId,
						product: json
					});
				}
		}
		jsonArrTotal.push({
			total: jsonArr
		});
		$.ajax({
			url: 'createImportPlanWeek',
	    	type: "POST",
	    	data: {json: JSON.stringify(jsonArrTotal)},
	    	success: function(data) {
	    	},
	    	error: function(data){
	    	}
			}).done(function(){
				location.reload();
			});
	};
</script>
<style>     
.green1 {
    color: black;
    background-color: #d9edf7;
}
.gray1 {
    color: black;
    background-color: #E8E5E5;
}
.yellow1 {
    color: black\9;
    background-color: yellow\9;
}
.red1 {
    color: black\9;
    background-color: #e83636\9;
}
.green1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #d9edf7;
}
.gray1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #E8E5E5;
}
.yellow1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: yellow;
}
.red1:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #e83636;
}

</style>