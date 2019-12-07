<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form id="formAdd">
			    <div class="span12">
			    	<div class="row-fluid">		
			    		<div class='span6'>
			    			<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.accProductName}
			    				</div>
			    				<div class='span7'>
									<div id="fixedAssetProductId">
					 				</div>
			    				</div>
							</div>
							
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right asterisk'>
			    					${uiLabelMap.AccountingFixedAssetProductTypeId}
			    				</div>
			    				<div class='span7'>
			    					<div id="fixedAssetProductTypeIdAdd">
					 				</div>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right'>
			    					${uiLabelMap.fromDate}
			    				</div>
			    				<div class='span7'>
			    					<div id="fromDateAdd" >
			 						</div>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right'>
			    					${uiLabelMap.thruDate}
			    				</div>
			    				<div class='span7'>
			    					<div id="thruDateAdd"></div>
			    				</div>
							</div>
						</div>	
						<div class="span6">	
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right '>
			    					${uiLabelMap.comments}
			    				</div>
			    				<div class='span7'>
			    					<input id="commentsAdd"></input>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right '>
			    					${uiLabelMap.sequenceNum}
			    				</div>
			    				<div class='span7'>
			    					<div id="sequenceNumAdd"></div>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right '>
			    					${uiLabelMap.Quantity}
			    				</div>
			    				<div class='span7'>
			    					<div id="quantityAdd"></div>
			    				</div>
							</div>
							<div class='row-fluid margin-bottom10'>
			    				<div class='span5 align-right '>
			    					${uiLabelMap.DAUom}
			    				</div>
			    				<div class='span7'>
			    					<div id="quantityUomIdAdd"></div>
			    				</div>
							</div>
						</div>
					</div>
				</div>		
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
    </div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/filterDate.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		
		var tmpLcl = '${locale}';
		if(tmpLcl=='vi'){
			tmpLcl = 'vi-VN';
		}else{
			tmpLcl = 'en-EN';
		}
var action = (function(){
	var initElement = function(){
			$("#comments").jqxInput({width: '202px', height: '22px'});	
			$("#thruDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#fromDateAdd").jqxDateTimeInput({width: '208px', height: '25px', formatString: 'dd/MM/yyyy', culture: tmpLcl});
			$("#thruDateAdd").jqxDateTimeInput("val",null);
			$("#fromDateAdd").jqxDateTimeInput("val",null);
			$("#sequenceNumAdd").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0,min : 0, spinButtons: true });	
			$("#quantityAdd").jqxNumberInput({spinMode: 'simple', width: '208px', height: '25px', decimalDigits: 0, min : 0,spinButtons: true });	
			$("#quantityAdd").jqxNumberInput("val",null);
			$("#sequenceNumAdd").jqxNumberInput("val",null);
			$("#fixedAssetProductTypeIdAdd").jqxDropDownList({autoDropDownHeight : true,source: dataFixedAssetProductTypeListView, width: '208px', displayMember:"description", selectedIndex: 0 ,valueMember: "fixedAssetProductTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#quantityUomIdAdd").jqxDropDownList({source: dataUomAndTypeListView,  width: '208px', filterable: true, displayMember:"description", selectedIndex: 0 ,valueMember: "uomId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#alterpopupWindow").jqxWindow({
			        width: 800, height: 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme         
			    });
			initCombobox();
			filterDate.init('fromDateAdd','thruDateAdd');
		}
	var initCombobox = function(){
		var sourceProduct =
            {
                datatype: "json",
                datafields: [
                    { name: 'productId' },
                    { name: 'brandName' },
                    { name: 'internalName' },
                    { name: 'productName' },
                    { name: 'description' }
                ],
                type: "POST",
                root: "listProducts",
                contentType: 'application/x-www-form-urlencoded',
                url: "fixedAssetProductableList"
            };
            var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct,
                {
                    formatData: function (data) {
                        if ($("#fixedAssetProductId").jqxComboBox('searchString') != undefined) {
                            data.searchKey = $("#fixedAssetProductId").jqxComboBox('searchString');
                            return data;
                        }
                    }
                }
            );
            $("#fixedAssetProductId").jqxComboBox(
            {
                width: 208,
                placeHolder: "${StringUtil.wrapString(uiLabelMap.wmparty)}",
                dropDownWidth: 500,
                height: 25,
                source: dataAdapterProduct,
                remoteAutoComplete: true,
                autoDropDownHeight: true,               
                selectedIndex: 0,
                displayMember: "productName",
                valueMember: "productId",
                renderer: function (index, label, value) {
                    var item = dataAdapterProduct.records[index];
                    if (item != null) {
	                        var label = item.productName + "&nbsp;" + item.internalName + " (" + item.productId + ")";
	                        return label;
	                    }
                    return "";
                },
                renderSelectedItem: function(index, item)
                {
                    var item = dataAdapterProduct.records[index];
                    if (item != null) {                        
                        var label = item.productName;
                        return label;
                    }
                    return "";   
                },
                search: function (searchString) {
                	dataAdapterProduct.dataBind();
                }
            });           
	
		
	}
	
	var clear = function(){
			$('#formAdd').jqxValidator('hide');
			$("#thruDateAdd").jqxDateTimeInput("val",null);
	    	$("#fromDateAdd").jqxDateTimeInput("val",null);	   	
			$("#commentsAdd").jqxDateTimeInput("val",null); 
			$("#quantityAdd").jqxNumberInput('clear');
			$("#sequenceNumAdd").jqxNumberInput('clear');			
			$("#fixedAssetProductTypeIdAdd").jqxDropDownList('clearSelection');
			$("#quantityUomIdAdd").jqxDropDownList('clearSelection');
			$("#fixedAssetProductId").jqxComboBox('clear');
			filterDate.resetDate();
	}
	
	var save = function(){
		var row;
		        row = { 
		        		thruDate:$('#thruDateAdd').jqxDateTimeInput('getDate'),
		        		fromDate:$('#fromDateAdd').jqxDateTimeInput('getDate'),
		        		comments:$('#commentsAdd').val(),
		        		fixedAssetProductTypeId:$('#fixedAssetProductTypeIdAdd').val(),
		        		quantity:$("#quantityAdd").jqxNumberInput('val'),
		        		sequenceNum:$("#sequenceNumAdd").jqxNumberInput('val'),
		        		quantityUomId:$('#quantityUomIdAdd').val(),	        		
		        		productId: $('#fixedAssetProductId').val(),
		        	  };
			   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');                        
		        $("#jqxgrid").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		        return true;
	}
	var bindEvent = function(){
		$("#alterpopupWindow").on('close',function(){
			clear();
		})
	    // update the edited row when the user clicks the 'Save' button.
	    $("#save").click(function () {
	    	if($('#alterpopupWindow').jqxValidator('validate')){
		    	if(save()) $('#alterpopupWindow').jqxWindow('close');
	    	}else{
	        	return;
	        }
	    });
	     $("#saveAndContinue").click(function () {
	    	if($('#alterpopupWindow').jqxValidator('validate')){
		    	save();
	    	}else{
	        	return;
	        }
	    });
	}
	
	var initRules = function(){
	    $('#alterpopupWindow').jqxValidator({
	        rules: [
	                   { input: '#fixedAssetProductId', message: '${uiLabelMap.CommonRequired}', action: 'select', rule: function(input){
	                	   var val = $("#fixedAssetProductId").jqxComboBox('val');
	                	   if(val==""){
	                	   return false;
	                	   }
	                	   return true;
	                	   }},
	                   { input: '#fixedAssetProductTypeIdAdd', message: '${uiLabelMap.CommonRequired}', action: 'select', rule: 
	                	   function (input) {
	                	   var val = $("#fixedAssetProductTypeIdAdd").jqxDropDownList('val');
	                	   if(val==""){
	                	   return false;
	                	   }
	                	   return true;
	                	   }                            
	                   },
	                   {
	                       input: '#thruDateAdd', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
	                           var fromDate = $('#fromDateAdd').jqxDateTimeInput('value');
	                           var thruDate = $('#thruDateAdd').jqxDateTimeInput('value');
	                           if (!thruDate)  return true;
	                           if(fromDate > thruDate){
	                        	   return false;
	                           }
	                           return true;
	                       }
	                   }
	               ]
	    });	 
	}

	return {
		init : function(){
			initElement();
			bindEvent();
			initRules();
		}
	}

}())	
	
$(document).ready(function () {
	action.init();
});
	</script>