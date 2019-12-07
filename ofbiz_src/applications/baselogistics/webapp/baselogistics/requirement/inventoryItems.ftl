<#include 'script/requirementDetailScript.ftl'/>
<#include "script/inventoryItemScript.ftl"/>
<div class="active">					
<div>
	<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
		${uiLabelMap.RequirementInformation}
	</h3>
</div>
<div style="position:relative" class="margin-top20">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom content-description" style="margin:10px">
			<div class="span12">
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.RequirementId}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementId" name="requirementId">
									${requirement.requirementId?if_exists}
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Status}</span>
							</div>
							<div class="span7">
								<div class="span5">
									<div id="reqStatusId" style="text-align: left;" class="green-label"></div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.RequirementType}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementTypeId" name="requirementTypeId"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.ReasonRequirement}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="reasonEnumId" name="reasonEnumId"></div>
					   		</div>
				   		</div>
				   		<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.CreatedBy}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="createdByUserLogin" name="createdByUserLogin">
									${createdBy.lastName?if_exists} ${createdBy.middleName?if_exists} ${createdBy.firstName?if_exists} 
								</div>
					   		</div>
				   		</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.LogRequiredByDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requiredByDate"></div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.LogRequirementStartDate}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="requirementStartDate">
								</div>
					   		</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.EstimatedBudget}</span>
							</div>
							<div class="span7">	
								<div id="estimatedBudget" style="text-align: left;" class="green-label"></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class='span5'>
								<span>${uiLabelMap.Facility}</span>
							</div>
							<div class="span7">
								<div class="green-label" style="text-align: left;" id="inventoryFacilityId" name="inventoryFacilityId"></div>
					   		</div>
						</div>
					</div>
				</div>
				<div class='row-fluid' style="margin-bottom: -10px !important">
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridRequirementItem" style="width: 100%"></div>
		</div>
	</div>
</div>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},		
					{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 150, editable:false, 
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
						},
					},
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable:false,
						 cellsrenderer: function(row, column, value){
							 return '<span>'+InventoryObj.unescapeHTML(value)+'<span>';
						 }
					},
					{ text: '${uiLabelMap.InventoryItem}', dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 250, editable: true, sortable: false,
					    cellsrenderer: function(row, column, value){
					        var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
					        if(data != null && data != undefined){
					        	if(value != null && value != '' && value != undefined){
					        		if (data.expiredDate === null || data.expiredDate === undefined){
					        			for (var i = 0; i < listInv.length; i ++){
						        			if (listInv[i].inventoryItemId == data.inventoryItemId){
						        				var exp = listInv[i].expireDate;
						        				var rcd = listInv[i].datetimeReceived;
						        				if (exp){
						        					return '<span style=\"text-align: right\" class=\"focus-color\"> RC: ' +$.datepicker.formatDate('dd/mm/yy', new Date(rcd))+ ' - EXP: ' + $.datepicker.formatDate('dd/mm/yy', new Date(exp)) + '</span>';
						        				} else {
						        					return '<span style=\"text-align: right\" class=\"focus-color\"> RC: ' +$.datepicker.formatDate('dd/mm/yy', new Date(rcd))+ ' - [${uiLabelMap.NotHasExpDate}]</span>';
						        				}
						        			}
						        		}
					        		} else {
					        			var exp = data.expiredDate;
						                return '<span style=\"text-align: right\" class=\"focus-color\">' + $.datepicker.formatDate('dd/mm/yy', new Date(exp)) + '</span>';
					        		}
				        		} else {
					            	return '<span style=\"text-align: right\" class=\"focus-color\">_NA_<span>';
					            }
					        	return '<span style=\"text-align: right\" class=\"focus-color\">_NA_<span>';
					        }
					    }, 
					    initeditor: function(row, value, editor){
						    var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
						    var uid = data.uid;
						    var invData = [];
					        for(i = 0; i < listInv.length; i++){
					        	if (curFacilityId == null){
					        		curFacilityId = $('#inventoryFacilityId').jqxDropDownList('val');
					        	}
					        	if (curFacilityId == listInv[i].facilityId){
					        		if(listInv[i].productId == data.productId){
						                var tmpDate ;
						                var tmpValue = new Object();
						                if(listInv[i].expireDate != null && listInv[i].expireDate != undefined && listInv[i].expireDate != ''){
						                	var tmp = listInv[i].expireDate;
						                    tmpValue.expireDate =  $.datepicker.formatDate('dd/mm/yy', new Date(tmp));
						                } else{
						                    tmpValue.expireDate = '_NA_';
						                }
						                if(listInv[i].datetimeReceived != null && listInv[i].datetimeReceived != undefined && listInv[i].datetimeReceived != ''){
						                	var tmp = listInv[i].datetimeReceived;
						                    tmpValue.receivedDate =  $.datepicker.formatDate('dd/mm/yy', new Date(tmp));
						                } else{
						                    tmpValue.receivedDate = '_NA_';
						                }
						                tmpValue.inventoryItemId = listInv[i].inventoryItemId;
						                tmpValue.productId = listInv[i].productId;
						                tmpValue.quantityOnHandTotal = listInv[i].quantityOnHandTotal;
						                tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
						                
						                var qtyUom = '';
						                for(var j = 0; j < quantityUomData.length; j++){
											if(listInv[i].quantityUomId == quantityUomData[j].uomId){
												qtyUom = quantityUomData[j].description;
										 	}
										}
						                tmpValue.qtyUom = qtyUom;
						                invData.push(tmpValue);
						            }
					        	}
					        }
					        if (invData.length > 0){
					        	editor.off('change');
					        	editor.jqxDropDownList({selectedIndex: 0, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, dropDownWidth: '600px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
						            renderer: function(index, label, value) {
						                var item = editor.jqxDropDownList('getItem', index);
						                return '[<span style=\"color:blue;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + item.originalItem.quantityOnHandTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + item.originalItem.availableToPromiseTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp;</span>';
						            }
						        });
					        	editor.on('change', function (event){
									var args = event.args;
						     	    if (args) {
					     	    		var item = args.item;
						     		    if (item){
						     		    	for (var m = 0; m < listInv.length; m ++){
						     		    		if (listInv[m].inventoryItemId == item.value){
						     		    			if (data.quantity > listInv[m].quantityOnHandTotal){
						     		    				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', uid, 'changeQuantity', listInv[m].quantityOnHandTotal);
						     		    			} else {
						     		    				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', uid, 'changeQuantity', data.quantity);
						     		    			}
						     		    			break;
						     		    		}
						     		    	}
						     		    } 
						     	    }
						        });
					        } else {
					        	editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.NotEnough)}', source: invData, dropDownWidth: '150px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
						        });
					        }
					    },
					    validation: function (cell, value) {
					    	if (listInv.length < 1){
					    		return { result: false, message: '${uiLabelMap.FacilityNotEnoughProduct}'};
						    }
					        if (value == null || value == undefined || value == '') {
					            return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
					        }
					        return true;
					    },
					}, 	
					{ text: '${uiLabelMap.ChangeQuantity}', dataField: 'changeQuantity', columntype: 'numberinput', width: 150, editable: true,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							if (value === undefined || value === null || value === ''){
								if (data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId === ''){
									value = 0;
									return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
								} else {
									for (var j = 0; j < listInv.length; j ++){
										if (data.inventoryItemId == listInv[j].inventoryItemId){
											if (data.quantity > listInv[j].quantityOnHandTotal){
												value = listInv[j].quantityOnHandTotal;
											} else {
												value = data.quantity;
											}
											return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
										}
									}
									value = 0;
									return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
								}
							} else {
								return '<span style=\"text-align: right; background-color: #e6ffb8;\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
							}
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					    },
					    validation: function (cell, value) {
					        if (value < 0){
					        	return { result: false, message: '${uiLabelMap.ExportValueMustBeGreaterThanZero}'};
					        }
					        return true;
						 },
					},
					{ text: '${uiLabelMap.RequiredNumber}', dataField: 'quantity', columntype: 'numberinput', width: 150, editable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
						},
					},
					{ text: '${uiLabelMap.QuantityChanged}', dataField: 'actualExecutedQuantity', width: 150, editable:false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridRequirementItem').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							if (value){
								return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
							} else {
								value = 0;
								return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') +' (' + descriptionUom +  ')</span>';
							}
						},
					},
					 "/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'inventoryStatusId', type: 'string' },
					{ name: 'requirementId', type: 'string' },
					{ name: 'reqItemSeqId', type: 'string' },
					{ name: 'unitCost', type: 'number' },
					{ name: 'returnReasonId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'lotId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'inventoryItemId', type: 'string' },
					{ name: 'returnTypeId', type: 'string' },
					{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' },
					{ name: 'actualExecutedQuantity', type: 'number' },
					{ name: 'changeQuantity', type: 'number' },
					]"/>
<@jqGrid filtersimplemode="true" id="jqxgridRequirementItem" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="false" filterable="false" editable="true" 
	url="jqxGeneralServicer?sname=jqGetRequirementItems&requirementId=${parameters.requirementId?if_exists}" selectionmode='multiplecellsadvanced' editmode='click' viewSize="10" 
	customTitleProperties="ListProduct" jqGridMinimumLibEnable="true" 
/>
<div class="row-fluid wizard-actions margin-top5 bottom-action">
	<button class="btn btn-small btn-primary btn-next" id="changeLabel" data-last="${uiLabelMap.LogFinish}">
		<i class="fa-refresh"></i>
		${uiLabelMap.Change}
	</button>
</div>