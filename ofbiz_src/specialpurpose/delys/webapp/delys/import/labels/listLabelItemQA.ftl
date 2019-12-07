<@jqGridMinimumLib/>
<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/additional-methods.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmenu.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxprogressbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollview.js"></script>

<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script>
	
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
	
	<#assign listLabel = delegator.findList("Label", null, null, null, null, false) />
	var labelData = 
	[
		<#list listLabel as label>
		{
			labelId: "${label.labelId}",
			labelName: "${StringUtil.wrapString(label.get('labelName', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByLabelId(labelId) {
		for ( var x in labelData) {
			if (labelId == labelData[x].labelId) {
				return labelData[x].labelName;
			}
		}
	}
	
	<#assign listUomByUomTypeProductPacking = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_LABEL_ITEM"), null, null, null, false)>
	var uomTypeProductPackingData = 
	[
		<#list listUomByUomTypeProductPacking as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByUomIdWithTypeProductPacking(uomId) {
		for ( var x in uomTypeProductPackingData) {
			if (uomId == uomTypeProductPackingData[x].uomId) {
				return uomTypeProductPackingData[x].description;
			}
		}
	}
	
	<#assign listUomByUomTypeWeightMeasure = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var uomTypeWeightMeasureData = 
	[
		<#list listUomByUomTypeWeightMeasure as uom>
		{
			uomId: "${uom.uomId}",
			description: "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}"
		},
		</#list>
	];
	
	function getDescriptionByUomIdWithTypeWeightMeasure(uomId) {
		for ( var x in uomTypeWeightMeasureData) {
			if (uomId == uomTypeWeightMeasureData[x].uomId) {
				return uomTypeWeightMeasureData[x].description;
			}
		}
	}
</script>
<div>
<div id="contentNotificationAddLabelSuccess">
</div>
	<#assign dataField="[
					{ name: 'productId', type: 'string'},
					{ name: 'internalName', type: 'string'},
					{ name: 'description', type: 'string'},
					{ name: 'quantityUomId', type: 'string'},
				]"/>
	<#assign columnlist="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.LogLableId}', datafield: 'productId', align: 'center', 
					},
					{ text: '${uiLabelMap.LogLableItemInternal}', datafield: 'internalName', align: 'center', 
					},
					{ text: '${uiLabelMap.description}', datafield: 'description', align: 'center', 
					},
					{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', align: 'center', cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value){
								return '<span style=\"text-align: right; \">' + getDescriptionByUomIdWithTypeProductPacking(value) + '</span>';
							}
						}, 
					},
				"/>
	
	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataField columnlist=columnlist editable="false" showtoolbar="true"
		id="jqxgirdLableItem"
		customcontrol1="icon-plus-sign open-sans@${uiLabelMap.LogAddLabelItemProduct}@javascript:AddLabelItemProduct()"
		url="jqxGeneralServicer?sname=JQGetListLableItemByQA"
		contextMenuId="contextMenu" mouseRightMenu="true"
	/>				
</div>

<div id='contextMenu' style="display:none;">
	<ul>
		<li id='viewProductPacking'><i class="icon-eye-open"></i>&nbsp;&nbsp;${uiLabelMap.ViewProductPacking}</li>
	</ul>
</div>

<div id="alterpopupWindow" class='hide'>
	<div>${uiLabelMap.LogAddLabelItemTitle}</div>
	<div class='form-window-container'>
		<div class='row-fluid'>
			<input type="hidden" value="LABEL_ITEM" id="primaryProductCategoryId"></input>
			<input type="hidden" value="RAW_MATERIAL" id="productTypeId"></input>
			<input type="hidden" value="LEN_mm" id="heightUomId"></input>
			<input type="hidden" value="LEN_mm" id="widthUomId"></input>  
			<div class='span12' class="margin-bottom10">
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ProductProductId)}</label>
					</div>  
					<div class="span7">
						<input id="productId"></input>
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ProductInternalName)}</label>
					</div>  
					<div class="span7">
						<input id="internalName"></input> 
					</div>
				</div>
			</div>
			<div class='span12' class="margin-bottom10">
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ProductProductName)}</label>
					</div>  
					<div class="span7">
						<input id="productName"></input> 
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ProductBrandName)}</label>
					</div>  
					<div class="span7">
						<input id="brandName"></input> 
					</div>
				</div>
			</div>
			<div class='span12' class="margin-bottom10">
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.ProductAvailableFromDate)}</label>
					</div>  
					<div class="span7">
						<div id="fromDate"></div> 
			   		</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.ProductAvailableThruDate)}</label>
					</div>  
					<div class="span7">
						<div id="thruDate"></div> 
					</div>
				</div>
			</div>
			<div class='span12' class="margin-bottom10">
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.LogProductHeightLabelItemTitle)}</label>
					</div>  
					<div class="span7">
						<div id="productHeight"></div> 
					</div>
				</div>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label>${StringUtil.wrapString(uiLabelMap.LogProductWidthLabelItemTitle)}</label>
					</div>  
					<div class="span7">
						<div id="productWidth"></div>  
			   		</div>
				</div>
			</div>
			<div class='span12' class="margin-bottom10">
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${StringUtil.wrapString(uiLabelMap.UnitLess)}</label>
					</div>  
					<div class="span7">
						<div id="quantityUomId"></div> 
			   		</div>
			   	</div>	
			</div>
			<div class='span12' style="margin-left: 62px;">
				<div class='span2 text-algin-right'>
					<label>${StringUtil.wrapString(uiLabelMap.description)}</label>
				</div>  
				<div class="span10">
					<textarea id="description"></textarea>
				</div>
			</div>
			<div class="form-action">
		        <div class='row-fluid'>
		            <div class="span12 margin-top20" style="margin-bottom:10px;">
		                <button id="alterCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		                <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		            </div>
		        </div>
		    </div>
		</div> 
	</div>
</div>

<div id="jqxwindowUploadFile" style="display:none;">
	<div>${uiLabelMap.SaveFileScan}</div>
	<div>
		<div class="row-fluid">
			<div class="span12" style="overflow-y: hidden;overflow-y: auto;">
				<input multiple type="file" id="id-input-file-3" />
			</div>
		</div>
		<hr style="margin: 10px 0px 0px 0px; border-top: 1px solid grey;opacity:0.2;">
		<div class="form-action">
	    	<div class="row-fluid">
	    		<div class="span12 margin-top10">
	    			<button id='cancelUpload' class="btn btn-danger form-action-button pull-right"><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
	    			<button id='btnUploadFile' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.CommonSave}</button>
	    		</div>
	    	</div>
		</div>
	</div>
</div>

<div id="jqxNotificationAddLabelProductSuccess" >
	<div id="notificationAddLabelProductSuccess">
	</div>
</div>

<script>
var listImage = [];
	$("#contextMenu").jqxMenu({ width: 230, height: 30, autoOpenPopup: false, mode: 'popup', theme: 'olbius' });
	$("#jqxgirdLableItem").on('contextmenu', function () {
	    return false;
	});
	
	$("#viewProductPacking").on("click", function() {
    	var rowIndexSelected = $('#jqxgirdLableItem').jqxGrid('getSelectedRowindex');
    	var rowData = $('#jqxgirdLableItem').jqxGrid('getrowdata', rowIndexSelected);
    	var productId = rowData.productId;
    	var form = "<form method='POST' action='getListLabelItemConfigPackingQA' id='ListItemConfigPacking'><input type='hidden' name='productId' value=" + productId + " /></form>";
    	$('body').append(form);
    	$("#ListItemConfigPacking").submit();
    });
	
	$(document).ready(function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		}).on('finished', function(e) {
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
		
		$('#id-input-file-3').ace_file_input({
			style:'well',
			btn_choose:'Drop files here or click to choose',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop().toLowerCase();
					if (extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
						listImage.push(files[int]);
					}else {
						return false;
					}
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		}).on('change', function(){
			//	readURL(this.files);
		});
	});
	$("#jqxNotificationAddLabelProductSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddLabelSuccess", opacity: 0.9, autoClose: true, template: "success" });
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#jqxwindowUploadFile").jqxWindow({ theme: 'olbius',
	    width: 480, maxWidth: 1000, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"), modalOpacity: 0.7
	});
	
	$("#addNewImages").on("click", function() {
		$("#jqxwindowUploadFile").jqxWindow("open");
	});
	
	function AddLabelItemProduct(){
		$('#alterpopupWindow').jqxWindow('open');
//		window.location.href = "addLabelItemProduct";
	}
	
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1300, minWidth: 700, height: 500, width:1000, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$("#productId").jqxInput({ placeHolder: '${StringUtil.wrapString(uiLabelMap.LogEnterDataText)}' });
	$("#primaryProductCategoryId").jqxInput();
	$("#productTypeId").jqxInput();
	$("#internalName").jqxInput({placeHolder: '${StringUtil.wrapString(uiLabelMap.LogEnterDataText)}'});
	$("#productName").jqxInput({ placeHolder: '${StringUtil.wrapString(uiLabelMap.LogEnterDataText)}' });
	$("#brandName").jqxInput({placeHolder: '${StringUtil.wrapString(uiLabelMap.LogEnterDataText)}'});
	$("#fromDate").jqxDateTimeInput({width: '211px'});
	$("#thruDate").jqxDateTimeInput({showFooter:true, clearString:'Clear', width: '211px'});
	$("#productHeight").jqxNumberInput({ theme: 'olbius', width: 211, spinButtons: true, decimalDigits: 0, min: 0});
	$("#productWidth").jqxNumberInput({ theme: 'olbius', width: 211, spinButtons: true, decimalDigits: 0, min: 0});
	$("#heightUomId").jqxInput();
	$("#widthUomId").jqxInput();
	$("#quantityUomId").jqxDropDownList({source: uomTypeProductPackingData, autoDropDownHeight: true ,placeHolder: '${uiLabelMap.LogPleaseSelect}' ,displayMember: 'description', valueMember: 'uomId', width:'211',});
	$('#alterpopupWindow').on('open', function (event) { 
		$('#description').jqxEditor({
		    theme: 'olbiuseditor',
		    width:'715px'
		});
	});  
	
	var htmlImage = "";
	function readURL(files) {
		htmlImage = "<div class='jqxScrollView'>";
	    if (files) {
	    	
	    	if($('.jqxScrollView').length > 0){
	    		$('.jqxScrollView').jqxScrollView("destroy");
	    	}
	    	
	    	for (var i = 0; i < files.length; i++) {
	    		var reader = new FileReader();
	    		 reader.onload = function (e) {
	             	htmlImage += "<div><div class='photo' style='background-image:url(" + e.target.result + ")'></div></div>";
	             }
	         	reader.readAsDataURL(files[i]);
			}
	    	setTimeout(function() {
	    		htmlImage += "</div>";
	    		$("#productImageViewer").html(htmlImage);
	    		$("#productImageViewerTotal").html(htmlImage);
	    		$('.jqxScrollView').jqxScrollView({ width: 130, height: 150, buttonsOffset: [0, 0]});
			}, 100);
	    }
	}
	$("#btnUploadFile").click(function () {
		readURL(listImage);
		$("#jqxwindowUploadFile").jqxWindow("close");
	});
	
	$('#alterpopupWindow').jqxValidator({
        rules: [
	               { input: '#productId', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var productId = $('#productId').val();
		            	    if(productId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	    }
	               } , 
	               { input: '#internalName', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var internalName = $('#internalName').val();
		            	    if(internalName == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	   }
	               },
	               { input: '#brandName', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var brandName = $('#brandName').val();
		            	    if(brandName == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	   }
	               },
	               { input: '#productName', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var productName = $('#productName').val();
		            	    if(productName == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	   }
	               },
//	               { input: '#productHeight', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
//	            	   rule: function () {
//	            		    var productHeight = $('#productHeight').val();
//		            	    if(productHeight == null){
//		            	    	return false; 
//		            	    }else{
//		            	    	return true; 
//		            	    }
//		            	    return true;    
//	            	   }
//	               },
//	               { input: '#productWidth', message: '${uiLabelMap.DSCheckIsEmptyCreateLocationFacility}', action: 'valueChanged, blur', 
//	            	   rule: function () {
//	            		    var productWidth = $('#productWidth').val();
//		            	    if(productWidth == 0){
//		            	    	return false; 
//		            	    }else{
//		            	    	return true; 
//		            	    }
//		            	    return true; 
//	            	   }
//	               },
	               
	               { input: '#quantityUomId', message: '${uiLabelMap.LogCheckSelectedDropDownList}', action: 'valueChanged, blur', 
	            	   rule: function () {
	            		    var quantityUomId = $('#quantityUomId').val();
		            	    if(quantityUomId == ""){
		            	    	return false; 
		            	    }else{
		            	    	return true; 
		            	    }
		            	    return true; 
	            	   }
	               },
	               {
	            	   input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.faFromDateLTThruDate)}', action: 'valueChanged', rule: function (input, commit) {
		            		if(input.jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == null){
		            			return true;
		            		}
		            		if(input.jqxDateTimeInput('getDate') < $('#fromDate').jqxDateTimeInput('getDate')){
		            			return false;
		            		}
		            		return true;
	            	   }
				   },
	           ]
    });
	
	$("#alterSave").click(function () {
		var productId = $('#productId').val();
		var primaryProductCategoryId = $('#primaryProductCategoryId').val();
		var productTypeId = $('#productTypeId').val();
		var internalName = $('#internalName').val();
		var productName = $('#productName').val();
		var brandName = $('#brandName').val();
		var fromDate = $('#fromDate').val().toTimeStamp();
		var thruDate = $('#thruDate').val().toTimeStamp();
		
		var productHeight = $('#productHeight').val(); 
		var productWidth = $('#productWidth').val();
		var quantityUomId = $('#quantityUomId').val();
		var heightUomId = $('#heightUomId').val();
		var widthUomId = $('#widthUomId').val();
		var description = $('#description').val();
		var validate = $('#alterpopupWindow').jqxValidator('validate');
		if(validate != false){
			bootbox.confirm("${uiLabelMap.LogAddNewReally}", function(result) {
	            if(result) {
	            	createLabelItemProduct(productId, primaryProductCategoryId, productTypeId, internalName, productName, brandName, fromDate, thruDate, productHeight, widthUomId, quantityUomId, heightUomId, widthUomId, description);
	            }
			});    
		}
    });
	
	function createLabelItemProduct(productId, primaryProductCategoryId, productTypeId, internalName, productName, brandName, fromDate, thruDate, productHeight, widthUomId, quantityUomId, heightUomId, widthUomId, description){
		$.ajax({
			url: "createLabelItemProduct",
			type: "POST",
			async: false,
			data: {productId: productId, primaryProductCategoryId: primaryProductCategoryId, productTypeId: productTypeId, internalName: internalName, productName: productName, brandName: brandName, fromDate: fromDate, thruDate: thruDate, productHeight: productHeight, widthUomId: widthUomId,
				quantityUomId: quantityUomId, heightUomId: heightUomId, widthUomId: widthUomId, description: description
			},
			dataType: "json",
			success: function(data) {  
			}
		}).done(function(data) {
			var successAdd = data._FORWARDED_FROM_SERVLET_;
			if(successAdd == true){
				$('#alterpopupWindow').jqxWindow('close');
				$("#notificationAddLabelProductSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiAddSucess)}');
				$("#jqxNotificationAddLabelProductSuccess").jqxNotification('open');
				$('#jqxgirdLableItem').jqxGrid('updatebounddata'); 
			}
		});
	}
	
	$('#alterpopupWindow').on('close', function (event) { 
    	$('#alterpopupWindow').jqxValidator('hide');
		$('#productId').val("");
		$('#internalName').val("");
		$('#productName').val("");
		$('#brandName').val("");
		$('#thruDate').jqxDateTimeInput('setDate', new Date(""));
		$("#quantityUomId").jqxDropDownList('clearSelection');
		$('#description').val("");
	}); 
	
	$('#alterpopupWindow').on('open', function (event) { 
		$('#productHeight').jqxNumberInput('clear');
		$('#productWidth').jqxNumberInput('clear');
		$('#heightUomId').jqxNumberInput('clear');
		$('#widthUomId').jqxNumberInput('clear');
		$('#thruDate').jqxDateTimeInput('setDate', new Date(""));
//		$('#productHeight').jqxNumberInput('setDecimal', 0);
//		$('#productWidth').jqxNumberInput('setDecimal', 0);
//		$('#heightUomId').jqxNumberInput('setDecimal', 0);
//		$('#widthUomId').jqxNumberInput('setDecimal', 0);
	}); 
	</script>
