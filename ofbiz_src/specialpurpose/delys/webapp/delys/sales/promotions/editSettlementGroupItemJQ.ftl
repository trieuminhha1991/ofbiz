<#--
href="#modal-table" role="button" class="green" data-toggle="modal"
-->

<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SETTLE_GRPITM_STATUS"}, null, false) />
<#assign promoTypeList = delegator.findByAnd("ProductPromoType", null, null, false)/>
<script type="text/javascript">
	var statusData = new Array();
	var newStatusData = new Array();
	var newRow = {};
	newRow['statusId'] = '';
	newRow['description'] = '';
	var newIndex = 0;
	<#list statusList as statusItem>
		<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
		var row = {};
		row['statusId'] = '${statusItem.statusId}';
		row['description'] = "${description}";
		statusData[${statusItem_index}] = row;
		if ('STLE_GRPIM_ACCEPTED' == row['statusId']) {
			row['description'] = "${uiLabelMap.DAAccept}";
			newStatusData[newIndex] = row;
			newIndex = newIndex + 1;
		} else if ('STLE_GRPIM_REJECTED' == row['statusId']) {
			row['description'] = "${uiLabelMap.DAReject}";
			newStatusData[newIndex] = row;
			newIndex = newIndex + 1;
		}
	</#list>
	var promoTypeData = new Array();
	<#list promoTypeList as promoTypeItem>
		<#assign description = StringUtil.wrapString(promoTypeItem.get("description", locale))/>
		var row = {};
		row['typeId'] = '${promoTypeItem.productPromoTypeId}';
		row['description'] = "${description}";
		promoTypeData[${promoTypeItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'promoSettleGroupItemId', type: 'string' },
       				{ name: 'promoSettleGroupItemParentId', type: 'string'}, 
       				{ name: 'promoSettleGroupId', type: 'string'},
       				{ name: 'promoSettleGroupType', type: 'string'},
       				{ name: 'productId', type: 'string'},
       				{ name: 'quantityRequired', type: 'string'},
       				{ name: 'amountRequired', type: 'string'},
       				{ name: 'quantityAccepted', type: 'string'},
       				{ name: 'amountAccepted', type: 'string'},
       				{ name: 'statusId', type: 'string'}
        			]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAPromoSettleGroupItemId}', dataField: 'promoSettleGroupItemId', width: '180px', editable: false},
					 { text: '${uiLabelMap.DAPromoSettleGroupItemParentId}', dataField: 'promoSettleGroupItemParentId', width: '180px', editable: false},
					 { text: '${uiLabelMap.DAPromoSettleGroupType}', dataField: 'promoSettleGroupType', width: '180px', filtertype: 'checkedlist', editable: false, 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
    						for(var i = 0 ; i < promoTypeData.length; i++){
    							if (value == promoTypeData[i].typeId){
    								return '<span title = ' + promoTypeData[i].description +'>' + promoTypeData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(promoTypeData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
								renderer: function(index, label, value){
									for(var i = 0; i < promoTypeData.length; i++){
										if(promoTypeData[i].typeId == value){
											return '<span>' + promoTypeData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
					 },
					 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px', editable: false},
					 { text: '${uiLabelMap.DAQuantityRequired}', dataField: 'quantityRequired', width: '120px', editable: false},
					 { text: '${uiLabelMap.DAAmountRequired}', dataField: 'amountRequired', width: '180px', editable: false},
					 { text: '${uiLabelMap.DAQuantityAccepted}', dataField: 'quantityAccepted', width: '120px'},
					 { text: '${uiLabelMap.DAAmountAccepted}', dataField: 'amountAccepted', width: '180px'},
			   		 { text: '${uiLabelMap.DAApprove}', dataField: 'newStatusId', width: '140px', columntype:'dropdownlist', filterable:false, sortable:false, 
			   		 	createeditor: function (row, cellvalue, editor) {
					 		var sourceDataPacking = {
				                localdata: newStatusData,
				                datatype: \"array\"
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'statusId'});
                      	}
			   		 },
			   		 { text: '${uiLabelMap.DASplit}', dataField: 'split', width: '100px', filterable:false, sortable:false, editable:false, 
			   		 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
                        	return \"<span><a href='javascript:void(0)' onClick='splitPromoSettleGroupItem(&#39;\" + data.promoSettleGroupItemId + \"&#39;, &#39;\" + row + \"&#39;)'>${uiLabelMap.DASplit}</a></span>\";
                        }
			   		 },
					 { text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: '200px', filtertype: 'checkedlist', editable: false, 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
    						for(var i = 0 ; i < statusData.length; i++){
    							if (value == statusData[i].statusId){
    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					 	createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									for(var i = 0; i < statusData.length; i++){
										if(statusData[i].statusId == value){
											return '<span>' + statusData[i].description + '</span>';
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			}
			   		 }
              		"/>
<@jqGrid id="jqxgridPOSR" defaultSortColumn="promoSettleGroupItemId;promoSettleGroupItemParentId" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
		viewSize="20" showtoolbar="false" editmode="click" selectionmode="singlerow" 
		mouseRightMenu="true" contextMenuId="contextMenu" 
		updateUrl="jqxGeneralServicer?sname=updateSettlementGroupItem&jqaction=U" editColumns="promoSettleGroupItemId;quantityAccepted;amountAccepted;newStatusId" 
		url="jqxGeneralServicer?sname=JQGetListPromoSettleGroupItem&promoSettleGroupId=${parameters.promoSettleGroupId?if_exists}"/>
<style type="text/css">
	.modal-custom {
		position:absolute;
		outline: none;
		left:30% !important;
	}
	.row-fluid .form-small [class*="span"] {
		min-height: 25px;
		margin-top:2px
	}
	#jqxGP .jqx-widget-header{
		z-index:1060 !important;
	}
</style>
<div id="modal-table" class="modal hide fade modal-custom" tabindex="-1" style="width:85%">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.DASplitPromotionSettlementGroupItem}
		</div>
	</div>

	<div class="modal-body no-padding">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-small">
				<div class="span4">
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAPromoSettleGroupItemId}:</label>
						<div class="controls">
							<div class="span12">
								<input type="hidden" id="mtStatusId" value=""/>
								<input type="hidden" id="mtPromoSettleGroupId" value=""/>
								<input type="hidden" id="mtPromoSettleGroupType" value=""/>
								<span id="mtPromoSettleGroupItemId"></span>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAPromoSettleGroupItemParentId}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtPromoSettleGroupItemParentId"></span>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAProductId}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtProductId"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAQuantityRequired}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtQuantityRequired"></span>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAAmountRequired}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtAmountRequired"></span>
							</div>
						</div>
					</div>
				</div>
				<div class="span4">
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAQuantityAccepted}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtQuantityAccepted"></span>
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="">${uiLabelMap.DAAmountAccepted}:</label>
						<div class="controls">
							<div class="span12">
								<span id="mtAmountAccepted"></span>
							</div>
						</div>
					</div>
				</div>
				<div style="margin-left:10px">
					<div id="jqxPanel" style="width:400px;">
						<button type="button" id="jqxButtonAddNewRow">${uiLabelMap.DAAddNewRow}</button>
					</div>
					<#assign dataField2="[{ name: 'promoSettleGroupItemId', type: 'string' },
										{ name: 'promoSettleGroupItemParentId', type: 'string'}, 
					       				{ name: 'promoSettleGroupId', type: 'string'},
					       				{ name: 'promoSettleGroupType', type: 'string'},
					       				{ name: 'productId', type: 'string'},
					       				{ name: 'quantityRequired', type: 'string'},
					       				{ name: 'amountRequired', type: 'string'},
					       				{ name: 'quantityAccepted', type: 'string'},
					       				{ name: 'amountAccepted', type: 'string'},
					       				{ name: 'statusId', type: 'string'}
					        			]"/>
					<#assign columnlist2="{ text: '${uiLabelMap.DAPromoSettleGroupItemId}', dataField: 'promoSettleGroupItemId', width: '180px', editable: false},
										 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px', columntype: 'template',
					                     	createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					                            editor.append('<div id=\"jqxGP\"></div>');
					                            editor.jqxDropDownButton();
					                            // prepare the data
											    var sourceP =
											    {
											        datafields:
											        [
											            { name: 'productId', type: 'string' },
											            { name: 'internalName', type: 'string' }
											        ],
											        cache: false,
											        root: 'results',
											        datatype: \"json\",
											        updaterow: function (rowid, rowdata) {
											            // synchronize with the server - send update command   
											        },
											        beforeprocessing: function (data) {
									                    sourceP.totalrecords = data.TotalRows;
									                },
											        filter: function () {
									                    // update the grid and send a request to the server.
									                    $(\"#jqxGP\").jqxGrid('updatebounddata');
									                },
									                pager: function (pagenum, pagesize, oldpagenum) {
									                    // callback called when a page or page size is changed.
									                },
									                sort: function () {
									                    $(\"#jqxGP\").jqxGrid('updatebounddata');
									                },
									                sortcolumn: 'productId',
					                				sortdirection: 'asc',
											        type: 'POST',
									                data: {
												        noConditionFind: 'Y',
												        conditionsFind: 'N',
												    },
												    pagesize:5,
									                contentType: 'application/x-www-form-urlencoded',
									                url: 'jqxGeneralServicer?sname=JQGetListProduct',
											    };
											    var dataAdapterP = new $.jqx.dataAdapter(sourceP,
											    {
											    	formatData: function (data) {
												    	if (data.filterscount) {
								                            var filterListFields = \"\";
								                            for (var i = 0; i < data.filterscount; i++) {
								                                var filterValue = data[\"filtervalue\" + i];
								                                var filterCondition = data[\"filtercondition\" + i];
								                                var filterDataField = data[\"filterdatafield\" + i];
								                                var filterOperator = data[\"filteroperator\" + i];
								                                filterListFields += \"|OLBIUS|\" + filterDataField;
								                                filterListFields += \"|SUIBLO|\" + filterValue;
								                                filterListFields += \"|SUIBLO|\" + filterCondition;
								                                filterListFields += \"|SUIBLO|\" + filterOperator;
								                            }
								                            data.filterListFields = filterListFields;
								                        }
								                         data.$skip = data.pagenum * data.pagesize;
								                         data.$top = data.pagesize;
								                         data.$inlinecount = \"allpages\";
								                        return data;
								                    },
								                    loadError: function (xhr, status, error) {
									                    alert(error);
									                },
									                downloadComplete: function (data, status, xhr) {
									                        if (!sourceP.totalRecords) {
									                            sourceP.totalRecords = parseInt(data[\"odata.count\"]);
									                        }
									                }, 
									                beforeLoadComplete: function (records) {
									                	for (var i = 0; i < records.length; i++) {
									                		if(typeof(records[i])==\"object\"){
									                			for(var key in records[i]) {
									                				var value = records[i][key];
									                				if(value != null && typeof(value) == \"object\" && typeof(value) != null){
									                					var date = new Date(records[i][key][\"time\"]);
									                					records[i][key] = date;
									                				}
									                			}
									                		}
									                	}
									                }
											    });
									            $(\"#jqxGP\").jqxGrid({
									            	width:400,
									                source: dataAdapterP,
									                filterable: true,
									                virtualmode: true, 
									                sortable:true,
									                editable: false,
									                autoheight:true,
									                pageable: true,
									                rendergridrows: function(obj)
													{
														return obj.data;
													},
									                columns: [
									                  { text: 'ProductId', datafield: 'productId'},
									                  { text: 'Product name', datafield: 'internalName'}
									                ]
									            });
									            $(\"#jqxGP\").on('rowselect', function (event) {
									            	//$(\"#jqxgrid\").jqxGrid({ disabled: true});
					                                var args = event.args;
					                                var row = $(\"#jqxGP\").jqxGrid('getrowdata', args.rowindex);
					                                var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
					                                nameT = row.productId;
					                                //$('#dropDownButtonContentjqxDD').html(dropDownContent);
					                                editor.jqxDropDownButton('setContent', dropDownContent);
					                            });
					                      	}
					                      	/*
					                      	,
					                        geteditorvalue: function (row, cellvalue, editor) {
					                            // return the editor's value.
					                            editor.jqxDropDownButton(\"close\");
					                            return nameT;
					                        }
					                      	*/
										 },
										 { text: '${uiLabelMap.DAQuantityRequired}', dataField: 'quantityRequired', width: '180px'},
										 { text: '${uiLabelMap.DAAmountRequired}', dataField: 'amountRequired', width: '180px'},
										 { text: '${uiLabelMap.DAQuantityAccepted}', dataField: 'quantityAccepted', width: '180px'},
										 { text: '${uiLabelMap.DAAmountAccepted}', dataField: 'amountAccepted', width: '180px'},
										 { text: '${uiLabelMap.DAStatus}', dataField: 'statusId', filtertype: 'checkedlist', width: '180px', editable: false, 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridPOSR').jqxGrid('getrowdata', row);
					    						for(var i = 0 ; i < statusData.length; i++){
					    							if (value == statusData[i].statusId){
					    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
					    							}
					    						}
					    						return '<span title=' + value +'>' + value + '</span>';
										 	}, 
										 	createfilterwidget: function (column, columnElement, widget) {
												var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
													autoBind: true
												});
												var records = filterDataAdapter.records;
												records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
												widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
													renderer: function(index, label, value){
														for(var i = 0; i < statusData.length; i++){
															if(statusData[i].statusId == value){
																return '<span>' + statusData[i].description + '</span>';
															}
														}
														return value;
													}
												});
												widget.jqxDropDownList('checkAll');
								   			}
								   		 }
					              		"/>
					<@jqGrid id="jqxgridPOSRChild" clearfilteringbutton="true" editable="true" sortable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist2 dataField=dataField2 
							viewSize="20" showtoolbar="false" editmode="click" selectionmode="singlerow" filterable="false"
							url="jqxGeneralServicer?sname=JQGetListPromoSettleGroupItemChild"/>
				</div>
			</div>
		</div>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			Close
		</button>
		<div class="pagination pull-right no-margin">
			<button class="btn btn-small btn-primary pull-left" onClick="javascript:onCreateSettleGroupItemChild();">
				<i class="icon-ok"></i>
				Ok
			</button>
		</div>
	</div>
</div>
<div id='contextMenu'>
	<ul>
	    <li><i class="icon-ok"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="icon-trash"></i>${StringUtil.wrapString(uiLabelMap.DADeleteRow)}</li>
	</ul>
</div>
<script type="text/javascript">
	//Create Theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#contextMenu").jqxMenu({ width: 200, height: 58, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgridPOSR").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgridPOSR").jqxGrid('updatebounddata');
        }else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DADeleteRow)}") {
        	var data = $("#jqxgridPOSR").jqxGrid("getrowdata", rowindex);
        	$.ajax({
				url : "deletePromoSettleGroupItem",
				type : "POST",
				data :{
					promoSettleGroupItemId: data.promoSettleGroupItemId
				},
				success : function(data) {
					$("#jqxgridPOSR").jqxGrid("updatebounddata");
					var errorMessage = "";
			        if (data._ERROR_MESSAGE_LIST_ != null) {
			        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
			        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
			        	}
			        }
			        if (data._ERROR_MESSAGE_ != null) {
			        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
			        }
			        if (errorMessage != "") {
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        } else {
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			        	$("#jqxNotification").jqxNotification("open");
			        }
				},
				error : function(textStatus, errorThrown) {	
				}
			});
        }
	});
</script>
<script type="text/javascript">
	var alterData = new Object();
	function onCreateSettleGroupItemChild() {
		var data = $('#jqxgridPOSRChild').jqxGrid("getrows");
		//console.log(data);
		var postData = new Array();
		var index = 0;
		for (var item in data) {
			var promoSettleGroupItemId = "";
			if (data[item].promoSettleGroupItemId != undefined) promoSettleGroupItemId = data[item].promoSettleGroupItemId;
			var quantityRequired = "";
			if (data[item].quantityRequired != undefined) quantityRequired = data[item].quantityRequired;
			var amountRequired = "";
			if (data[item].amountRequired != undefined) amountRequired = data[item].amountRequired;
			var quantityAccepted = "";
			if (data[item].quantityAccepted != undefined) quantityAccepted = data[item].quantityAccepted;
			var amountAccepted = "";
			if (data[item].amountAccepted != undefined) amountAccepted = data[item].amountAccepted;
			
			var row = {"promoSettleGroupItemId": data[item].promoSettleGroupItemId, 
										"promoSettleGroupItemParentId" : data[item].promoSettleGroupItemParentId, 
										"promoSettleGroupId" : data[item].promoSettleGroupId,
										"promoSettleGroupType": data[item].promoSettleGroupType,
										"productId": data[item].productId, 
										"quantityRequired": quantityRequired, 
										"amountRequired": amountRequired, 
										"quantityAccepted": quantityAccepted, 
										"amountAccepted": amountAccepted, 
										"statusId": data[item].statusId
										};
			postData[index] = row;
			index = index + 1;
		}
		$.ajax({ // ajax call starts
			url : "splitSettleGroupItem",
			type : "POST",
			data :{
				listData: JSON.stringify(postData),
				promoSettleGroupId: ${parameters.promoSettleGroupId}
			},
			dataType : 'json', // Choosing a JSON datatype
			success : function(data) {// Variable data contains the data we get from serverside
				//console.log(data);
				$("#modal-table").modal("hide");
				$("#jqxgridPOSR").jqxGrid("updatebounddata");
			},
			error : function(textStatus, errorThrown) {	
			}
		});
	}
	function splitPromoSettleGroupItem(promoSettleGroupItemId, rowIndex) {
		data = $('#jqxgridPOSR').jqxGrid("getrowdata", rowIndex);
		if (data.promoSettleGroupItemId != undefined && data.promoSettleGroupItemId != null && promoSettleGroupItemId == data.promoSettleGroupItemId) {
			if (data.promoSettleGroupItemId != null) {$("#mtPromoSettleGroupItemId").text(data.promoSettleGroupItemId);
			} else {$("#mtPromoSettleGroupItemId").text("");}
			
			if (data.promoSettleGroupItemParentId != null) {$("#mtPromoSettleGroupItemParentId").text(data.promoSettleGroupItemParentId);
			} else {$("#mtPromoSettleGroupItemParentId").text("");}
			
			if (data.promoSettleGroupId != null) {$("#mtPromoSettleGroupId").val(data.promoSettleGroupId);
			} else {$("#mtPromoSettleGroupId").val("");}
			if (data.promoSettleGroupType != null) {$("#mtPromoSettleGroupType").val(data.promoSettleGroupType);
			} else {$("#mtPromoSettleGroupType").val("");}
			
			if (data.productId != null) {$("#mtProductId").text(data.productId);
			} else {$("#mtProductId").text("");}
			
			if (data.quantityRequired != null) {$("#mtQuantityRequired").text(data.quantityRequired);
			} else {$("#mtQuantityRequired").text("");}
			
			if (data.amountRequired != null) {$("#mtAmountRequired").text(data.amountRequired);
			} else {$("#mtAmountRequired").text("");}
			
			if (data.quantityAccepted != null) {$("#mtQuantityAccepted").text(data.quantityAccepted);
			} else {$("#mtQuantityAccepted").text("");}
			
			if (data.amountAccepted != null) {$("#mtAmountAccepted").text(data.amountAccepted);
			} else {$("#mtAmountAccepted").text("");}
			
			if (data.statusId != null) {$("#mtStatusId").val(data.statusId);
			} else {$("#mtStatusId").val("")}
			
			alterData.pagenum = "0";
		    alterData.pagesize = "20";
		    alterData.noConditionFind = "Y";
		    alterData.conditionsFind = "N";
			alterData.promoSettleGroupItemId = data.promoSettleGroupItemId;
			
			$("#jqxgridPOSRChild").jqxGrid("updatebounddata");
			alterData = new Object();
			
			$("#modal-table").modal("show");
		}
	}
	$(function() {
		$("#jqxButtonAddNewRow").jqxButton({ width: '150', theme: theme});
		$("#jqxButtonAddNewRow").click(function () {
	    	var row;
	        row = { promoSettleGroupItemId:'',
	        		promoSettleGroupItemParentId:$('#mtPromoSettleGroupItemId').text(),
	        		promoSettleGroupId:$('#mtPromoSettleGroupId').val(),
	        		promoSettleGroupType:$('#mtPromoSettleGroupType').val(),
	        		productId:$('#mtProductId').text(),
	        		statusId:$('#mtStatusId').val()
	        	  };
		   	$("#jqxgridPOSRChild").jqxGrid('addRow', null, row, "first");
	        <#--
	        // select the first row and clear the selection.
	        $("#jqxgridQuotationItems").jqxGrid('clearSelection');                        
	        $("#jqxgridQuotationItems").jqxGrid('selectRow', 0);  
	        $("#alterpopupWindow").jqxWindow('close');
	        productIds[productIds.length] = row["productId"];
	        -->
	    });
	});
</script>