<script type="text/javascript">
	var dataSelected = new Array();
	<#assign itlength = listUom.size()/>
	<#if listUom?size gt 0>
	    <#assign lu="var lu = ['" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + "'"/>
		<#assign luValue="var luValue = [\"" + StringUtil.wrapString(listUom.get(0).uomId?if_exists) + ":" + StringUtil.wrapString(listUom.get(0).description?if_exists) +"\""/>
		<#if listUom?size gt 1>
			<#list 1..(itlength - 1) as i>
				<#assign lu=lu + ",'" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + "'"/>
				<#assign luValue=luValue + ",\"" + StringUtil.wrapString(listUom.get(i).uomId?if_exists) + ":" + StringUtil.wrapString(listUom.get(i).description?if_exists) + "\""/>
			</#list>
		</#if>
		<#assign lu=lu + "];"/>
		<#assign luValue=luValue + "];"/>
	<#else>
		<#assign lu="var lu = [];"/>
		<#assign luValue="var luValue = [];"/>
	</#if>
	${lu}
	${luValue}	
	var dataLU = new Array();
	for (var i = 0; i < ${itlength}; i++) {
	    var row = {};
	    row["uomId"] = lu[i];
	    row["description"] = luValue[i];
	    dataLU[i] = row;
	}

	
</script>
<#assign createdByUserLogin = shoppingProposalSelected.createdByUserLogin?if_exists>
<div class="row-fluid">
	<h4 id="step-title" class="header smaller lighter blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">
		${uiLabelMap.EditProcuremetProposal} (${shoppingProposalSelected.requirementId?if_exists})</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.GeneralInformation}" data-placement="bottom">1</span>
			</li>
			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.Confirmation}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<a href="<@ofbizUrl>viewProcurementProposal?requirementId=${shoppingProposalSelected.requirementId?if_exists}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.ViewProcuremetProposal}" data-placement="bottom" class="no-decoration"><i class="fa fa-arrow-circle-left" style="font-size:16pt"></i></a>
        
	</div>
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />

	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<form class="form-horizontal basic-custom-form form-size-mini" id="updateProcurementProposal" name="updateProcurementProposal" method="post" action="<@ofbizUrl>updateProcurementProposal</@ofbizUrl>">
			
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label required" for="requirementId">${uiLabelMap.proposalId}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="requirementId" readonly="true" id="requirementId" class="span12 input-small" maxlength="20" value="${shoppingProposalSelected.requirementId?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="estimatedBudget">${uiLabelMap.estimatedBudget}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="estimatedBudget" id="estimatedBudget" class="span12 input-small" maxlength="20" value="${shoppingProposalSelected.estimatedBudget?if_exists}">
								</div>
							</div>
						</div>
						
						<div class="control-group">
							
							<#if shoppingProposalSelected.currencyUomId?exists>
								<#assign currencyUomId = shoppingProposalSelected.currencyUomId>
							</#if>
							<label class="control-label required" for="currencyUomId">${uiLabelMap.currencyUomId}</label>
							<div class="controls">
								<div class="span12">
									<select name="currencyUomId" id="currencyUomId" class="input-mini chzn-select" data-placeholder="${uiLabelMap.ChooseCurrency}...">
						              	<option value=""></option>
						              	<#list listCurrency as currency>
							              	<option value="${currency.uomId}" <#if currencyUomId?default('') == currency.uomId>selected="selected"</#if> />${currency.uomId}
						              	</#list>
						            </select>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.CreatedBy}:</label>
							<div class="controls">
								<input type="text" name="createdByUserLogin" class="span12 input-small" readonly="true" id="createdByUserLogin" value="${createdByUserLogin?if_exists}" />
								
							</div>
						</div>
						
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="requirementStartDate">${uiLabelMap.requirementStartDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="requirementStartDate" id="requirementStartDate" value="${shoppingProposalSelected.requirementStartDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="requiredByDate">${uiLabelMap.requiredByDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="requiredByDate" id="requiredByDate" value="${shoppingProposalSelected.requiredByDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.DepartMent}:</label>
							<div class="controls">
								<#assign department = dispatcher.runSync("getDepartmentFromUserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("createdByUserLogin", createdByUserLogin, "userLogin", userLogin)) !/>
								<input type="text" name="department" class="span12 input-small" readonly="true" id="department" value="${department.departmentName?if_exists}" />
								
								
							</div>
						</div>
						
					</div><!--.span6-->
					
					
				</div><!--.row-fluid-->
				<div class="row-fulid">
						<div class="span12">
							<div class="control-group">
								<label class="control-label required" for="descriptionRequirement">${uiLabelMap.Description}</label>
								<div class="controls">
									<div class="span12">
										<textarea  name="descriptionRequirement" id="descriptionRequirement" class="note-area no-resize" autocomplete="off"  value="${parameters.description?if_exists}"></textarea>
										
									</div>
								</div>
							</div>
						
						</div>
						
					</div>
			</form>
			<div class="row-fluid">
				
				<div class="span12">
					<div id="jqxPanel" style="width:400px;">
						<button type="button" id="jqxButtonAddNewRow">${uiLabelMap.DAAddNewRow}</button>
					</div>
					<#assign dataField="[{ name:'productId', type: 'string' },
					               		{ name: 'internalName', type: 'string'},
					               		{ name: 'description', type: 'string'},
					               		{ name: 'quantityUomId', type: 'string'},
					               		{ name: 'quantity', type: 'number'},
					               		{ name: 'reason', type: 'string'}
					               		
					                	]"/>
					<#assign columnlist="{ text: '${uiLabelMap.ProductId}', dataField: 'productId', width: '100px', editable:false},
										 { text: '${uiLabelMap.ProductName}', dataField: 'internalName', editable:false, width:150},
										 { text: '${uiLabelMap.Description}', dataField: 'description',editable:false, width:200},
										{ text: '${uiLabelMap.QuantityUom}', dataField: 'quantityUomId',width:100,editable:false, cellsrenderer: function (row, column, value) {
												for(i = 0; i < dataLU.length; i++){
													if(dataLU[i].uomId == value){
														return '<span title = ' + value + '>' + dataLU[i].description + '</span>';
													}
												}
												return '<span title = ' + value + '>' + value + '</span>';
					    					} 
					    				 },
										 { text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: '180px', filterable:false, sortable:false},
										 { text: '${uiLabelMap.Reason}', dataField: 'reason', filterable:false, sortable:false}
					              		"/>
					<@jqGrid id="jqxgridProductProcurement" editable="true"  columnlist=columnlist dataField=dataField
							viewSize="30" showtoolbar="false" editmode="click" selectionmode="checkbox"
							filterable="false" sortable="false"
							url="jqxGeneralServicer?requirementId=${requirementId}&sname=JQListRequirementItem" 
							/>
				</div>
			</div>
		</div><!--.step1-->

		<div class="step-pane" id="step2">
			<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
				<div class="row margin_left_10 row-desc">
					<div class="span12">
						<div class="span6">
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.proposalId}:</label>
								<div class="controls-desc">
									<span id="strrequirementId"></span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.estimatedBudget}:</label>
								<div class="controls-desc">
									<span id="strestimatedBudget"></span>
								</div>
							</div>
							
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.currencyUomId}:</label>
								<div class="controls-desc">
									<span id="strcurrencyUomId"></span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.CreatedBy}:</label>
								<div class="controls-desc">
									<span id="strcreatedByUserLogin"></span>
								</div>
							</div>
							
						</div><!--.span6-->
						<div class="span6">
							
							
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.requirementStartDate}:</label>
								<div class="controls-desc" >
									<span id="strrequirementStartDate"></span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.requiredByDate}:</label>
								<div class="controls-desc" >
									<span id="strrequiredByDate"></span>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label-desc">${uiLabelMap.Department}:</label>
								<div class="controls-desc">
									<span id="strdepartment"></span>
								</div>
							</div>
						</div><!--.span6-->
					</div>
					<div class="span12 no-left-margin">
					
						<div class="control-group">
							<label class="control-label" for="strdescription">${uiLabelMap.Description}</label>
							<div class="controls">
								<div class="span12">
									<textarea  name="strdescription" id="strdescription" class="note-area no-resize" autocomplete="off"></textarea>
									
								</div>
							</div>
						</div>
					
					</div>
				</div><!--.row-fluid-->
				
				<div class="row-fluid">
					<div class="span12">
						<div id="jqxgridProductSelected" style="width: 100%">
						</div>
						<script type="text/javascript">
							$(document).ready(function () {
								var sourceSuccess = {
										localdata: dataSelected,
										dataType: "array",
										datafields:[
										   {name: 'productId', type: 'string'},
										   {name: 'productName', type:'string'},
										   {name: 'description', type:'string'},
										   {name: 'quantity', type:'number'},
										   {name: 'quantityUomId', type:'string'},
										   {name: 'reason', type:'string'}
										   
										   ]
									};
								var dataAdapterSuccess = new jQuery.jqx.dataAdapter(sourceSuccess);
								
								jQuery("#jqxgridProductSelected").jqxGrid({
									width: '100%',
									source:dataAdapterSuccess,
									pageable: true,
							        autoheight: true,
							        sortable: false,
							        altrows: true,
							        showaggregates: false,
							        showstatusbar: false,
							        enabletooltips: true,
							        editable: false,
							        selectionmode: 'singlerow',
							        columns:[
											{ text: '${uiLabelMap.ProductId}', dataField: 'productId', width: '100px', editable:false},
											{ text: '${uiLabelMap.ProductName}', dataField: 'productName', editable:false, width:150},
											{ text: '${uiLabelMap.Description}', dataField: 'description',editable:false, width:200},
											{ text: '${uiLabelMap.QuantityUom}', dataField: 'quantityUomId',width:100,editable:false, cellsrenderer: function (row, column, value) {
													for(i = 0; i < dataLU.length; i++){
														if(dataLU[i].uomId == value){
															return '<span title = ' + value + '>' + dataLU[i].description + '</span>';
														}
													}
													return '<span title = ' + value + '>' + value + '</span>';
												} 
											},
											{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: '180px', filterable:false, sortable:false},
											{ text: '${uiLabelMap.Reason}', dataField: 'reason', filterable:false, sortable:false}
							           
							        ]
								});
							});
						</script>
			
					</div>
				</div>
			</div>
		</div><!--.step2-->
	</div>
	
	<hr />
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.Previous}
		</button>
		<button class="btn btn-small btn-success btn-next" data-last="${uiLabelMap.Finish}" id="btnNextWizard">
			${uiLabelMap.Next}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
<!-- start add new row -->
<form id="alterpopupWindowform" name="alterpopupWindowform" class="form-horizontal basic-custom-form form-size-mini" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
	<div id="alterpopupWindow">
		<div>${uiLabelMap.accCreateNew}</div>
		<div class="form-horizontal basic-custom-form form-size-small">
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label required" for="jqxdropdownbuttonProduct">${uiLabelMap.Product}</label>
	    				<div class="controls">
	    					<div class="span12">
	    						<input id="addProductName" name="addProductName" type="hidden"/>
								<input id="addProductDescription" name="addProductDescription" type="hidden"/>
								<input id="addProductQuantityUomId" name="addProductQuantityUomId" type="hidden"/>
	    						<div id="jqxgridListProduct">
						       	 	<div id="jqxgridProduct"></div>
						       	</div>
						       	<a href="javascript:showAddNewProduct();"  style="float:left"> 
					       		  <i class="icon-plus"></i>${uiLabelMap.NewProduct}
					       		</a>
	    					</div>
	    				</div>
	    			</div>
	    			
	    			<div class="control-group">
	    				<label class="control-label required" for="addQuantity">${uiLabelMap.Quantity}</label>
	    				<div class="controls">
	    					<div class="span12">
	    						<input type="text" name="addQuantity" id="addQuantity" value="" class="span12">
	    					</div>
	    				</div>
	    			</div>
	    			<div class="control-group">
	    				<label class="control-label required" for="addReason">${uiLabelMap.Reason}</label>
	    				<div class="controls">
	    					<div class="span12">
	    						<textarea name="addReason" id="addReason" value="" class="span12"  rows="5" cols="5"></textarea>
	    					</div>
	    				</div>
	    			</div>
	    			
	    		</div>
	    	</div><!--.row-fluid-->
	    	<div class="row-fluid" style="text-align: center;">
	    		<button type="button" id="alterCancel" class="btn btn-small btn-danger " style="padding: 5px 10px"><i class="icon-remove open-sans"></i>${uiLabelMap.CommonCancel}</button>
				<button type="button" id="alterSave" class="btn btn-primary btn-small" style="padding: 5px 10px"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSave}</button>
	    	</div>
	    </div>
	</div>		
</form>	
<!-- end add new row -->

<#-- add product -->
<form id="alterpopupAddProductForm" name="alterpopupAddProductForm" id="alterpopupAddProductForm" class="form-horizontal basic-custom-form form-size-mini" method="post">
	<div id="alterpopupAddProduct">
		<div>${uiLabelMap.accCreateNew}</div>
		<div class="form-horizontal basic-custom-form form-size-small">
	    	<div class="row-fluid">
	    		<div class="span12">
	    		
	    			<div class="control-group">
	    				<label class="control-label required" for="productName">${uiLabelMap.ProductName}</label>
	    				<div class="controls">
	    					<div class="span8">
	    						<input type="text" name="productName" id="productName" value="" class="span12">
	    					</div>
	    				</div>
	    			</div>
	    			<div class="control-group">
	    				<label class="control-label required" for="description">${uiLabelMap.Description}</label>
	    				<div class="controls">
	    					<div class="span8">
	    						<input type="text" name="description" id="description" value="" class="span12">
	    					</div>
	    				</div>
	    			</div>
	    		</div>
	    	</div><!--.row-fluid-->
	    	<div class="row-fluid" style="text-align: center;">
	    		<button type="button" id="alterCancelProduct" class="btn btn-small btn-danger " style="padding: 5px 10px"><i class="icon-remove open-sans"></i>${uiLabelMap.CommonCancel}</button>
				<button type="button" id="alterSaveProduct" class="btn btn-primary btn-small" style="padding: 5px 10px"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSave}</button>
	    	</div>
	    </div>
	</div>		
</form>	
<#--end add product -->
<div id="popUpShowError">
		<div>Model Window</div>
		<div class="form-horizontal basic-custom-form form-size-small">
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<label id="showError"></label>
	    			
	    		</div>
	    	</div><!--.row-fluid-->
	    	
	    </div>
	</div>		
<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/select2.min.js"></script>
<script type="text/javascript">
var description;
$(document).ready(function (){
	 description = CKEDITOR.replace('descriptionRequirement', {
	    height: '100px',
	    width: '87%',
	    skin: 'office2013'
	});
	 var descStr = "${shoppingProposalSelected.description?if_exists?trim}";
	 description.setData(descStr); 
	
});
$(function() {
	
	$("#jqxButtonAddNewRow").jqxButton({ width: '150', theme: theme});
    $("#alterCancel").jqxButton({theme: theme});
    $("#alterSave").jqxButton({theme: theme});
    
    $("#alterpopupWindow").jqxWindow({width: 600, height: 330, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme});
    $("#alterpopupAddProduct").jqxWindow({width: 600, height: 330, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancelProduct"), modalOpacity: 0.7, theme:theme});
    $("#popUpShowError").jqxWindow({width: 600, height: 330, resizable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme});
    $("#jqxButtonAddNewRow").on('click', function () {
		$('#alterpopupWindow').jqxWindow('open');
    });
    
    // update the edited row when the user clicks the 'Save' button.
    $("#alterSave").click(function () {
    	if($('#alterpopupWindowform').jqxValidator('validate')){
    		var selectedProductId = $('#jqxgridListProduct').val();
	    	var row;
	        row = { internalName:$('#addProductName').val(),
	        		productId:$('#jqxgridListProduct').val(),
	        		description:$('#addProductDescription').val(),
	        		quantity:$('#addQuantity').val(),
	        		quantityUomId: $('#addQuantityUomId').val(),
	        		reason:$('#addReason').val()
	        		
	        	  };
	        
	    	var griddata = $('#jqxgridProductProcurement').jqxGrid('getdatainformation');
	    	var rowCount = griddata.rowscount;
	    	if(rowCount > 0){
	    		for (var i = 0 ; i < rowCount ; i ++){
	    			var rowData = $('#jqxgridProductProcurement').jqxGrid('getrowdata', i);
	    			if(selectedProductId == rowData.productId){
	    					$('#alterpopupWindow').jqxWindow('close');
	    					bootbox.dialog("${uiLabelMap.ProductIsSelected}!", [{
	    						"label" : "OK",
	    						"class" : "btn-small btn-primary",
	    						}]
	    					);
	    			}else{
	    				addNewRow(row);
	    			}
	    		}
	    	}else{
	    		addNewRow(row);
	    	}
			
        }else{
        	return;
        }
    });
    
  //To new product when user click save in screen new row
    $("#alterSaveProduct").click(function () {
    	var dataProduct = "productName=" + $("#productName").val() + "&description=" + $('#description').val();
    	if($('#alterpopupAddProductForm').jqxValidator('validate')){

    		jQuery.ajax({
    			url : 'createProductInProcurementProposal',
    			data : dataProduct,
    			type : 'post',
    			async : false,
    			success : function(data) {
    				getResultOfCreateProduct(data);
    			},
    			error : function(data) {

    				getResultOfCreateProduct(data);
    			}
    		});	
    	}
    		
    });
    $('#alterpopupWindowform').jqxValidator({
        rules: [
			
           	{input: '#addQuantity', message: '${uiLabelMap.CommonRequired}. ${uiLabelMap.ValidateDataOnlyNumber}', action: 'keyup, blur', 
           		rule: function (input) {
           			var value = $(input).val();
           			if (/^\s*$/.test(value) || isNaN(value)) return false;
           			else return true;
           		}
       		}, 
       		
           	{input: "#addReason", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
       			rule: function (input, commit) {
    				var value = $(input).val();
                    return value != "";
                }
           	}]
    });
    $('#alterpopupAddProductForm').jqxValidator({
        rules: [
			
           	{input: '#productName', message: '${uiLabelMap.CommonRequired}. ${uiLabelMap.CommonRequired}', action: 'blur', 
           		rule: function (input, commit) {
    				var value = $(input).val();
                    return value != "";
                }
       		}, 
       		
           	{input: "#description", message: "${uiLabelMap.CommonRequired}", action: 'blur', 
       			rule: function (input, commit) {
    				var value = $(input).val();
                    return value != "";
                }
           	}]
    });
})
	
</script>
<script type="text/javascript">
	var alterData = null;
	var undefined = "undefined";
	var editorStrDescription;
	$(document).ready(function (){
		
		
		editorStrDescription = CKEDITOR.replace('strdescription', {
		    height: '100px',
		    width: '87%',
		    skin: 'office2013'
		   /*  config.readOnly = true; */
		});
		
		
		
	});
	function addNewRow(row){
		$("#jqxgridProductProcurement").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgridProductProcurement").jqxGrid('clearSelection');                        
        $("#jqxgridProductProcurement").jqxGrid('selectRow', 0);  
        $("#alterpopupWindow").jqxWindow('close');
        // reset value on window
		$('#addProductName').val("");
		$('#addProductDescription').val("");
		$('#addQuantity').val("");
		$('#addReason').val("");
	}
	$(function() {
		
		$('[data-rel=tooltip]').tooltip();
	
		$(".select2").css('width','150px').select2({allowClear:true})
		.on('change', function(){
			$(this).closest('form').validate().element($(this));
		}); 
		
		$(".chzn-select").chosen({allow_single_deselect:true , no_results_text: "${uiLabelMap.NotFound}"});
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
				if(!$('#updateProcurementProposal').valid()) return false;
				$('#container').empty();
				$("#step-title").html("${uiLabelMap.Confirm}");
				
		        var selectedRowIndexes = $('#jqxgridProductProcurement').jqxGrid('selectedrowindexes');
				
				dataSelected = new Array();
				$('#container').empty();
				for(var index in selectedRowIndexes) {
					var data = $('#jqxgridProductProcurement').jqxGrid('getrowdata', selectedRowIndexes[index]);
					var row = {};
					if(data.productId != undefined){
						row["productId"] = data.productId;
					}else{
						row["productId"] = "";
					}
					
					row["productName"] = data.internalName;
					if(data.description != undefined){
						row["description"] = data.description;
					}else{
						row["description"] = "";
					}
					if(data.quantity != undefined){
						row["quantity"] = data.quantity;
					}else{
						row["quantity"] = 0;
					}
					if(data.reason != undefined){
						row["reason"] = data.reason;
					}else{
						row["reason"] = "";
					}
					if(data.quantityUomId != undefined || data.quantityUomId ==""){
						row["quantityUomId"] = data.quantityUomId;
					}else{
						row["quantityUomId"] = "";
					}
					
					
					dataSelected[index] = row;
				}
				var estimatedBudget = $("#estimatedBudget").val();
				var currencyUomId = $("#currencyUomId").val();
				estimatedBudget  = formatcurrency (estimatedBudget, currencyUomId);
				$("#strestimatedBudget").html(estimatedBudget);
				/* $("#strdescription").val($("#description").val()); */
				$("#strrequirementId").html($("#requirementId").val());
				$("#strcreatedByUserLogin").html($("#createdByUserLogin").val());
				$("#strdepartment").html($("#department").val());
				editorStrDescription.setData(description.getData()); 
				editorStrDescription.setReadOnly(true);
				/* $("#strreason").val($("#reason").val()); */
				$("#strcurrencyUomId").html($("#currencyUomId").val());
				
				$("#strrequirementStartDate").html($("#requirementStartDate").val());
				$("#strrequiredByDate").html($("#requiredByDate").val());
				var sourceSuccessTwo = {
						localdata: dataSelected,
						dataType: "array",
						datafields:[
						   {name: 'productId', type: 'string'},
						   {name: 'productName', type:'string'},
						   {name: 'description', type:'string'},
						   {name: 'quantity', type:'number'},
						   {name: 'quantityUomId', type:'string'},
						   {name: 'reason', type:'string'}
						   
						   ]
					};
				var columnsSuccessTwo = [
						{ text: '${uiLabelMap.ProductId}', dataField: 'productId', width: '100px', editable:false},
						{ text: '${uiLabelMap.ProductName}', dataField: 'productName', editable:false, width:150},
						{ text: '${uiLabelMap.Description}', dataField: 'description',editable:false, width:200},
						{ text: '${uiLabelMap.QuantityUom}', dataField: 'quantityUomId',width:100,editable:false, cellsrenderer: function (row, column, value) {
								for(i = 0; i < dataLU.length; i++){
									if(dataLU[i].uomId == value){
										return '<span title = ' + value + '>' + dataLU[i].description + '</span>';
									}
								}
								return '<span title = ' + value + '>' + value + '</span>';
							} 
						},
						{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: '180px', filterable:false, sortable:false},
						{ text: '${uiLabelMap.Reason}', dataField: 'reason', filterable:false, sortable:false}

			          
			     ];
				 
				var dataAdapter = new $.jqx.dataAdapter(sourceSuccessTwo);
                $("#jqxgridProductSelected").jqxGrid({ source: dataAdapter, columns: columnsSuccessTwo });
			} else if ((info.step == 2) && (info.direction == "previous")) {
				alterData = null;
				$("#step-title").html("${uiLabelMap.DACreateQuotationForProduct}");
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.AreYouCertainlyCreated}", function(result) {
				if(result) {
				var formSend = document.getElementById('updateProcurementProposal');
					var prodSelectedRows = $('#jqxgridProductSelected').jqxGrid('getrows');
					var prodSelecteds = new Array();
					
					for (var i = 0; i < prodSelectedRows.length; i++) {
						var itemSelected = prodSelectedRows[i];
						var rowProductSelected = $("#jqxgridProductSelected").jqxGrid('getrowdata', i);
						
						var productSelected = new Object;
						productSelected.productId = rowProductSelected.productId;
						productSelected.description = rowProductSelected.description;
						productSelected.quantity = rowProductSelected.quantity;
						productSelected.productName = rowProductSelected.productName;
						productSelected.quantityUomId = rowProductSelected.quantityUomId;
						productSelected.reason = rowProductSelected.reason;
						prodSelecteds.push(productSelected);
				        
					}
				    var hiddenField = document.createElement("input");
		            hiddenField.setAttribute("type", "hidden");
		            hiddenField.setAttribute("name", "productListStr");
		            hiddenField.setAttribute("id", "productListStr");
		            hiddenField.setAttribute("value", JSON.stringify(prodSelecteds));
		            
		            var hiddenDescription = document.createElement("input");
		            hiddenDescription.setAttribute("type", "hidden");
		            hiddenDescription.setAttribute("name", "description");
		            hiddenDescription.setAttribute("id", "description");
		            var descriptionRequirement = description.getData();
		           
		            hiddenDescription.setAttribute("value", descriptionRequirement);
		            formSend.appendChild(hiddenField);
		            formSend.appendChild(hiddenDescription);
				    formSend.submit();
				}
			});
		});
		
		$('#updateProcurementProposal').validate({
			errorElement: 'span',
			errorClass: 'help-inline',
			focusInvalid: false,
			rules: {
				currencyUomId: {
					required: true
				},
				
				estimatedBudget: {
				
					required: true,
					 number: true
				},
				requiredByDate: {
					required: true
				},
				requirementStartDate: {
					required: true
				}
			},
			messages: {
				currencyUomId: {
					required: "${uiLabelMap.FieldNotEmpty}"
				},
				estimatedBudget: {
					required: "${uiLabelMap.FieldNotEmpty}"
				},
				requirementStartDate: {
					required: "${uiLabelMap.FieldNotEmpty}"
				}
			},
			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},
			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},
			unhighlight: function(element, errorClass) {
	    		var parentControls = $(element).closest(".controls");
	    		if (parentControls != undefined) {
	    			parentControls.find("ul.chzn-choices").css("border", "1px solid #64a6bc");
	    		}
	    	},
			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},
			errorPlacement: function (error, element) {
				var parentControls = element.closest(".controls");
				if (parentControls != undefined) {
					error.appendTo(parentControls);
					parentControls.find("ul.chzn-choices").css("border", "1px solid #f09784");
				}
			}
		});
	})
</script>
<script type="text/javascript">
	/*set up jqxgridProduct*/
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	
	var productIds = [];
	var sourceProduct =
	    {
	        datafields:[{name: 'productId', type: 'string'},
	            		{name: 'internalName', type: 'string'},
	            		{name: 'productTypeId', type: 'string'},
	            		{name: 'description', type: 'string'},
	            		{name: 'quantityUomId', type: 'string'},
	            		
        			],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	            // synchronize with the server - send update command   
	        },
	        beforeprocessing: function (data) {
	        	sourceProduct.totalrecords = data.TotalRows;
	        },
	        filter: function () {
	            // update the grid and send a request to the server.
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        pager: function (pagenum, pagesize, oldpagenum) {
	            // callback called when a page or page size is changed.
	        },
	        sort: function () {
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        sortcolumn: 'productId',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		        productIds: productIds,
		        
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQListProductForProcurementProposal&productCateogryId=${procurementCategory?if_exists}',
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
		    	if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourceProduct.totalRecords) {
	                    sourceProduct.totalRecords = parseInt(data["odata.count"]);
	                }
	        }, 
	        beforeLoadComplete: function (records) {
	        	for (var i = 0; i < records.length; i++) {
	        		if(typeof(records[i])=="object"){
	        			for(var key in records[i]) {
	        				var value = records[i][key];
	        				if(value != null && typeof(value) == "object" && typeof(value) != null){
	        					//var date = new Date(records[i][key]["time"]);
	        					//records[i][key] = date;
	        				}
	        			}
	        		}
	        	}
	        }
	    });
	    $("#jqxgridListProduct").jqxDropDownButton({ theme: theme, width: 200, height: 25});
	    $("#jqxgridProduct").jqxGrid({
	    	width:610,
	        source: dataAdapterProduct,
	        filterable: true,
	        showfilterrow: true,
	        virtualmode: true, 
	        sortable:true,
	        theme: theme,
	        editable: false,
	        autoheight:true,
	        pageable: true,
	        rendergridrows: function(obj){
				return obj.data;
			},
	        columns: [{text: '${uiLabelMap.ProductId}', datafield: 'productId', width:'100px'},
	          			{text: '${uiLabelMap.ProductName}', datafield: 'internalName', width:'200px'},
	          			{text: '${uiLabelMap.ProductTypeId}', datafield: 'productTypeId', width:'180px'},
	          			{text: '${uiLabelMap.Description}', datafield: 'description'},
	          			{ text: '${uiLabelMap.QuantityUom}', dataField: 'quantityUomId',width:'100px',editable:false, cellsrenderer: function (row, column, value) {
							for(i = 0; i < dataLU.length; i++){
								if(dataLU[i].uomId == value){
									return '<span title = ' + value + '>' + dataLU[i].description + '</span>';
								}
							}
							return '<span title = ' + value + '>' + value + '</span>';
							} 
						}
	        		]
	    });
	    
	    $("#jqxgridProduct").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxgridProduct").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
	        $('#jqxgridListProduct').jqxDropDownButton('setContent', dropDownContent);
			$('#addProductName').val(row['internalName']);
			$('#addProductDescription').val(row['description']);
			$('#addProductQuantityUomId').val(row['quantityUomId']);
	    });
	    
</script>
<script type="text/javascript">
	
	function showAddNewProduct(){
		 $('#alterpopupAddProduct').jqxWindow('open');
	}
 	function formatcurrency(num, uom){
			decimalseparator = ",";
	     	thousandsseparator = ".";
	     	currencysymbol = "đ";
	     	if(typeof(uom) == "undefined" || uom == null){
	     		uom = "${currencyUomId?if_exists}";
	     	}
			if(uom == "USD"){
				currencysymbol = "$";
				decimalseparator = ".";
	     		thousandsseparator = ",";
			}else if(uom == "EUR"){
				currencysymbol = "€";
				decimalseparator = ".";
	     		thousandsseparator = ",";
			}
		    var str = num.toString().replace(currencysymbol, ""), parts = false, output = [], i = 1, formatted = null;
		    if(str.indexOf(".") > 0) {
		        parts = str.split(".");
		        str = parts[0];
		    }
		    str = str.split("").reverse();
		    
		    for(var j = 0, len = str.length; j < len; j++) {
		        if(str[j] != ",") {
		            output.push(str[j]);
		            if(i%3 == 0 && j < (len - 1)) {
		                output.push(thousandsseparator);
		            }
		            i++;
		        }
		    }
		    formatted = output.reverse().join("");
		    return(formatted + ((parts) ? decimalseparator + parts[1].substr(0, 2) : "") + "&nbsp;" + currencysymbol);
		};
 </script>	