<script type="text/javascript">
<#assign itlength = listCurrency.size()/>
<#if listCurrency?size gt 0>
    <#assign lc="var lc = ['" + StringUtil.wrapString(listCurrency.get(0).uomId?if_exists) + "'"/>
	<#assign lcValue="var lcValue = [\"" + StringUtil.wrapString(listCurrency.get(0).abbreviation?if_exists) + ":" + StringUtil.wrapString(listCurrency.get(0).description?if_exists) +"\""/>
	<#if listCurrency?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lc=lc + ",'" + StringUtil.wrapString(listCurrency.get(i).uomId?if_exists) + "'"/>
			<#assign lcValue=lcValue + ",\"" + StringUtil.wrapString(listCurrency.get(i).abbreviation?if_exists) + ":" + StringUtil.wrapString(listCurrency.get(i).description?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lc=lc + "];"/>
	<#assign lcValue=lcValue + "];"/>
<#else>
	<#assign lc="var lc = [];"/>
	<#assign lcValue="var lcValue = [];"/>
</#if>
${lc}
${lcValue}	
var dataLC = new Array();
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["uomId"] = lc[i];
    row["description"] = lcValue[i];
    dataLC[i] = row;
}

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

<#assign itlength = listUom.size()/>
<#if listProduct?size gt 0>
    <#assign lp="var lp = ['" + StringUtil.wrapString(listProduct.get(0).productId?if_exists) + "'"/>
	<#assign lpValue="var lpValue = [\"" + StringUtil.wrapString(listProduct.get(0).productId?if_exists) + ":" + StringUtil.wrapString(listProduct.get(0).internalName?if_exists) +"\""/>
	<#if listProduct?size gt 1>
		<#list 1..(itlength - 1) as i>
			<#assign lp=lp + ",'" + StringUtil.wrapString(listProduct.get(i).productId?if_exists) + "'"/>
			<#assign lpValue=lpValue + ",\"" + StringUtil.wrapString(listProduct.get(i).productId?if_exists) + ":" + StringUtil.wrapString(listProduct.get(i).internalName?if_exists) + "\""/>
		</#list>
	</#if>
	<#assign lp=lp + "];"/>
	<#assign lpValue=lpValue + "];"/>
<#else>
	<#assign lp="var lp = [];"/>
	<#assign lpValue="var lpValue = [];"/>
</#if>
${lp}
${lpValue}	
var dataLP = new Array();
for (var i = 0; i < ${itlength}; i++) {
    var row = {};
    row["productId"] = lp[i];
    row["description"] = lpValue[i];
    dataLP[i] = row;
}

</script>
<#assign statusId = shoppingProposalSelected.statusId?if_exists>
<#assign createdByUserLogin = shoppingProposalSelected.createdByUserLogin?if_exists>
<div class="form-horizontal basic-custom-form form-decrease-padding" id="showProductInRequirement" name="showProductInRequirement" style="display: block;">
	<div class="row-fluid">
		<div class="span12"> 
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.estimatedBudget}:</label>
					<div class="controls">
						<b>${shoppingProposalSelected.estimatedBudget?if_exists}</b>
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.currencyUomId}:</label>
					<div class="controls">
						${shoppingProposalSelected.currencyUomId?if_exists}
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.DAStatus}:</label>
					<div class="controls">
						<#if statusId?exists && statusId?has_content>
							<#assign status = delegator.findOne("StatusItem", {"statusId" : statusId}, true)>
							<#if status.statusCode?has_content>${status.get("description",locale)}</#if>
		                </#if>
					</div>
				</div>
				
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.CreatedBy}:</label>
					<div class="controls">
						${createdByUserLogin?if_exists}
					</div>
				</div>
			</div>
			
			<div class="span6">
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.requirementStartDate}:</label>
					<div class="controls">
						<#if shoppingProposalSelected.requirementStartDate?exists>${shoppingProposalSelected.requirementStartDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.requiredByDate}:</label>
					<div class="controls">
						<#if shoppingProposalSelected.requiredByDate?exists>${shoppingProposalSelected.requiredByDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.DepartMent}:</label>
					<div class="controls">
						<#assign department = dispatcher.runSync("getDepartmentFromUserLogin", Static["org.ofbiz.base.util.UtilMisc"].toMap("createdByUserLogin", createdByUserLogin, "userLogin", userLogin)) !/>
	
						<#if department?exists>
							${department.departmentName?if_exists}
						</#if>
						
					</div>
				</div>
				
			</div><!--.span6-->
			
		</div>
		<div class="span12 no-left-margin">
			<div class="control-group">
				<label class="control-label" for="description">${uiLabelMap.Description}:</label>
				<div class="controls">
					<div class="span12">
						<textarea  name="descriptionRequirement" id="descriptionRequirement" class="note-area no-resize" autocomplete="off"  value=""></textarea>
					</div>
				</div>
			</div>
		
	</div>
		
	</div><!--.row-->
</div>

<div style="clear:both"></div>
<hr/>

<#assign dataField="[{ name: 'requirementId', type: 'string' },
					 { name: 'reqItemSeqId', type: 'string' },
					 { name: 'productId', type: 'string' },
					 { name: 'quantity', type: 'string' },
					 { name: 'quantityUomId', type: 'string'},
					 { name: 'currencyUomId', type: 'string'},
					 { name: 'estimatedPrice' , type: 'number' },
					 { name: 'estimatedReceiveDate', type: 'date', other: 'Timestamp' },
					 { name: 'reason', type: 'string' }
					 ]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.requirementId}', datafield: 'requirementId', editable: false, width: 100, 
						cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridProductProcurement').jqxGrid('getrowdata', row);
       					return '<a style = \"margin-left: 10px\" href=' + 'showProductInRequirement?requirementId=' + data.requirementId + '>' +  data.requirementId + '</a>'
   						}
					 },
					 { text: '${uiLabelMap.ProductId}', datafield: 'productId', editable: false, width: 300,
					 	cellsrenderer: function (row, column, value) {
						for(i = 0; i < dataLP.length; i++){
								if(dataLP[i].productId == value){
									return dataLP[i].description;
								}
							}
       					
   						}
   					},
					 { text: '${uiLabelMap.quantity}', datafield: 'quantity', width: 50},
					 { text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', width: 100},
					 { text: '${uiLabelMap.currencyUomId}', datafield: 'currencyUomId', width: 100},
					 { text: '${uiLabelMap.Reason}', datafield: 'reason'}
					
					"/>
<@jqGrid url="jqxGeneralServicer?requirementId=${requirementId}&sname=JQListRequirementItem" dataField=dataField columnlist=columnlist
		 id="jqxgridProductProcurement" filtersimplemode="true" showtoolbar="true"
		 filterable="false" sortable="false"
		 editable="false"
		 customcontrol1="icon-plus@${uiLabelMap.NewProduct}@#javascript:void(0)@showPopupNewAdd()"
 />
 <form id="alterpopupWindowform" name="alterpopupWindowform" class="form-horizontal basic-custom-form form-size-mini" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
	<div id="alterpopupWindow">
		<div>${uiLabelMap.accCreateNew}</div>
		<div class="form-horizontal basic-custom-form form-size-small">
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label required" for="jqxgridListProduct">${uiLabelMap.Product}</label>
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
	    					<div class="span8">
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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/select2.min.js"></script>

<script type="text/javascript">
var description;
$(document).ready(function (){
	 description = CKEDITOR.replace('descriptionRequirement', {
	    height: '100px',
	    width: '87%',
	    skin: 'office2013',
	    readOnly:true
	});
	 var descStr = "${shoppingProposalSelected.description?if_exists?trim}";
	 description.setData(descStr); 
	 //description.readOnly(true);
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
	var sourceLC =
	{
	    localdata: dataLC,
	    datatype: "array"
	};
	var dataAdapterLC = new $.jqx.dataAdapter(sourceLC);
	var sourceLU =
	{
	    localdata: dataLU,
	    datatype: "array"
	};
	var dataAdapterLU = new $.jqx.dataAdapter(sourceLU);
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
</script> 
 <script type="text/javascript">
 function showPopupNewAdd(){

	$('#alterpopupWindow').jqxWindow('open');

 	}
 function showAddNewProduct(){
	 $('#alterpopupAddProduct').jqxWindow('open');
 }
$(function() {
    $("#alterCancel").jqxButton({theme: theme});
    $("#alterSave").jqxButton({theme: theme});
    
    $("#alterCancelProduct").jqxButton({theme: theme});
    $("#alterSaveProduct").jqxButton({theme: theme});
    
    $("#alterpopupWindow").jqxWindow({width: 700, height: 330, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme});
    $("#alterpopupAddProduct").jqxWindow({width: 700, height: 330, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancelProduct"), modalOpacity: 0.7, theme:theme});
    $("#popUpShowError").jqxWindow({width: 700, height: 330, resizable: false, isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme});
    
    
    //To update listProduct when user click save in screen new row
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
    	var data = "productName=" + $("#productName") + "&description=" + $('#description');
    	if($('#alterpopupAddProductForm').jqxValidator('validate')){

    		jQuery.ajax({
    			url : 'createProductInProcurementProposal',
    			data : data,
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
	/*set up jqxgridProduct*/
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	
	var productIds = [];
	var sourceProduct =
	    {
	        datafields:[{name: 'productId', type: 'string'},
	            		{name: 'internalName', type: 'string'},
	            		
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