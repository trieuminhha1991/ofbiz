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
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>

<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>

<div id="container"></div>
<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid">
	  <ul class="wizard-steps">
		<li data-target="#step1" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.generalInformation}</span></li>
		<li data-target="#step2"><span class="step">2</span> <span class="title">${uiLabelMap.HrolbiusDetailInformation}</span></li>
		<li data-target="#step3"><span class="step">3</span> <span class="title">${uiLabelMap.aggregation}</span></li>
	  </ul>
	</div>
	<hr />
	<div class="step-content row-fluid position-relative">
		<div class="step-pane active" id="step1">
			<div class='row-fluid'>
				<div class='span1'>
					<div class='row-fluid margin-bottom10'>
						<div>
							<li id="addNewImages" class="green icon-plus" title="${uiLabelMap.ClickToUploadNewImage}" style="margin-left: 172px;margin-top: -4px;position: absolute;"></li>
							<div id="productImageViewer">
								<img id="blah" src="/delys/images/logo/product_demo.png" height="180" width="170" style="max-width: none;"/>
							</div>
						</div>
					</div>
				</div>
				<div class='span11'>
					<div class="row-fluid">
		        		<div class="span12">
		    	 			<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductProductId}</label></div>
		    	 			<div class="span3"><input type="text" name="txtProductId" id="txtProductId" /></div>
		    	 			<div class="span3"><label class="text-right asterisk">${uiLabelMap.productCategoryIdAdd}</label></div>
		    	 			<div class="span3"><div id="txtPrimaryProductCategoryId"></div>
		    	 				<li id="addCatagory" class="green icon-plus" title="${uiLabelMap.AddProductCategory}" style="margin-left: 225px;margin-top: -34px;position: absolute;"></li>
		    	 			</div>
			 			</div>
			 		</div>
			 		
			 		<div class="row-fluid">
		    	 		<div class="span12" style="margin-top:8px;">
			    	 		<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductInternalName}</label></div>
		    	 			<div class="span3"><input type="text" name="txtInternalName" id="txtInternalName" /></div>
		    	 			<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductProductName}</label></div>
		    	 			<div class="span3"><input type="text" name="txtProductName" id="txtProductName" /></div>
			 			</div>
			 		</div>
			 		
			 		<div class="row-fluid">
		    	 		<div class="span12" style="margin-top:8px;">
			    	 		<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductBrandName}</label></div>
		        	 		<div class="span3"><input type="text" name="txtBrandName" id= "txtBrandName" /></div>
		    	 		</div>
			 		</div>
			 		
			 		<div class="row-fluid">
		    	 		<div class="span12" style="margin-top:8px;">
			    	 		<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductAvailableFromDate}</label></div>
		    	 			<div class="span3"><div id="txtFromDate" ></div></div>
		    	 			<div class="span3"><label class="text-right">${uiLabelMap.ProductAvailableThruDate}</label></div>
		    	 			<div class="span3"><div id="txtThruDate" ></div></div>
			 			</div>
		 			</div>
			 		
			 		<div class="row-fluid">
		    	 		<div class="span12" style="margin-top:8px;">
			    	 		<div class="span3"><label class="text-right">${uiLabelMap.description}&nbsp;&nbsp;&nbsp;</label></div>
		    	 			<div class="span9"><textarea id="description1"></textarea></div>
		    	 		</div>
			 		</div>
		 		</div>
			</div>
		</div>
		
		<div class="step-pane" id="step2">
			<div class="row-fluid">
    	 		<div class="span12" style="margin-top:8px;">
	    	 		<div class="span3"><label class="text-right asterisk">${uiLabelMap.Weight}</label></div>
    	 			<div class="span3"><div id="txtWeight"></div></div>
    	 			<div class="span3"><label class="text-right asterisk">${uiLabelMap.NetWeight}</label></div>
    	 			<div class="span3"><div id="txtProductWeight"></div></div>
    	 		</div>
	 		</div>
	 		
	 		<div class="row-fluid">
    	 		<div class="span12" style="margin-top:8px;">
	    	 		<div class="span3"><label class="text-right asterisk">${uiLabelMap.ProductWeightUomId}</label></div>
    	 			<div class="span3"><div id="txtWeightUomId"></div></div>
    	 			<div class="span3"><label class="text-right asterisk">${uiLabelMap.UnitLess}</label></div>
    	 			<div class="span3"><div id="txtQuantityUomId"></div></div>
    	 		</div>
	 		</div>
		</div>
		
		<div class="step-pane" id="step3">
		<form method="post" action="" class="basic-form form-horizontal">
		
		<div class='row-fluid'>
		
		<div class='span2'>
			<div class='row-fluid margin-bottom10'>
				<div>
					<div id="productImageViewerTotal">
						<img src="/delys/images/logo/product_demo.png" height="180" width="170" style="max-width: none;"/>
					</div>
				</div>
			</div>
		</div>
		
		<div class='span10'>
				<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductProductId}</label>
							    <div class="controls" id="productIdTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.productCategoryIdAdd}</label>
							    <div class="controls" id= "productCategoryIdTotal"></div>
						    </div>
	    	 			</div>
		 			</div>
	 			</div>
	 			<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductInternalName}</label>
							    <div class="controls" id="productInternalNameTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductProductName}</label>
							    <div class="controls" id= "productNameTotal"></div>
						    </div>
	    	 			</div>
		 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductBrandName}</label>
							    <div class="controls" id="productBrandNameTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.description}</label>
							    <div class="controls" id= "descriptionTotal"></div>
						    </div>
					    </div>
		 			</div>
	 			</div>
 			
	 			<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.Weight}</label>
							    <div class="controls" id="weightTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.NetWeight}</label>
							    <div class="controls" id= "netWeightTotal"></div>
						    </div>
	    	 			</div>
		 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductWeightUomId}</label>
							    <div class="controls" id="weightUomIdTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.UnitLess}</label>
							    <div class="controls" id= "quantityUomIdTotal"></div>
						    </div>
	    	 			</div>
		 			</div>
	 			</div>
	 			
	 			<div class="row-fluid">
	        		<div class="span12">
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductAvailableFromDate}</label>
							    <div class="controls" id= "txtFromDateTotal"></div>
						    </div>
	    	 			</div>
	    	 			<div class="span6">
		    	 			<div class="control-group no-left-margin ">
							    <label>${uiLabelMap.ProductAvailableThruDate}</label>
							    <div class="controls" id= "txtThruDateTotal"></div>
						    </div>
					    </div>
		 			</div>
	 			</div>
 			</div>
			</div>
		</form>
		</div>
	</div>
	<hr />
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPreveiw"><i class="icon-arrow-left"></i>${uiLabelMap.DAPrev}</button>
		<button class="btn btn-small btn-success btn-next" id="btnNext">${uiLabelMap.DANext}<i class="icon-arrow-right icon-on-right"></i></button>
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
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

<div id="alterpopupWindow" style="display:none;">	
	<div>${uiLabelMap.AddProductCategory}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DACategoryId}<span style="color:red;"> *</span></div>
	        	<div class="span9"><input type="text" id="txtProductCategoryId" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DACategoryName}<span style="color:red;"> *</span></div>
	        	<div class="span9"><input type="text" id="txtCategoryName" /></div>
	        </div>
	    </div>
	    <div class="row-fluid">
	        <div class="span12 no-left-margin">
	        	<div class="span3" style="text-align: right;margin-top: 8px;">${uiLabelMap.DADescription}&nbsp;&nbsp;&nbsp;</div>
	        	<div class="span9"><textarea id="tarDescription"></textarea></div>
	        </div>
	    </div>
	    <div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<#assign listProductCategory = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, null, null, false) />
<#assign listProducts = delegator.findList("Product", null, null, null, null, false) />
<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
			 
<script>
	$("#alterpopupWindow").jqxWindow({
	    width: 650, maxWidth: 1000, theme: "olbius", minHeight: 420, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7
	});
	$('#alterpopupWindow').on('open', function () {
		$('#tarDescription').jqxEditor({
	        theme: 'olbiuseditor',
	        width: '98%',
	        height: 240
	    });
		$("#tarDescription").jqxEditor('val', "");
		$("#txtProductCategoryId").val("");
		$("#txtCategoryName").val("");
	});
	$('#alterpopupWindow').on('close', function () {
		$('#alterpopupWindow').jqxValidator('hide');
	});
	
	$("#alterSave").click(function () {
		if ($('#alterpopupWindow').jqxValidator('validate')) {
			var row = {};
	    	row.productCategoryId = $("#txtProductCategoryId").val();
	    	row.productCategoryTypeId = "CATALOG_CATEGORY";
	    	row.categoryName = $("#txtCategoryName").val();
	    	row.longDescription = $("#tarDescription").val();
	    	saveProductCategory(row, getProductCategoryAjax);
	        $("#alterpopupWindow").jqxWindow('close');
		}
	});
	$('#alterpopupWindow').jqxValidator({
	    rules: [
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductCategoryId").val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.CategoryIdAlreadyExists)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductCategoryId").val().toLowerCase();
							if (_.indexOf(listProductCategorys, value) === -1) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtCategoryName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' }
	           ]
	});
	function hasWhiteSpace(s) {
		  return /\s/g.test(s);
	}
	function saveProductCategory(data, callback) {
		$('#jqxNotificationNested').jqxNotification('closeLast');
		$.ajax({
			  url: "createProductCategory",
			  type: "POST",
			  data: data,
			  success: function(res) {
			  }
		  	}).done(function() {
		  		$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
		  		callback();
		  	});
	}
	$("#addNewImages").on("click", function() {
		$("#jqxwindowUploadFile").jqxWindow("open");
	});
	$("#addCatagory").on("click", function() {
//		deleteCookie("categoryChange");
//		window.open("listProductCatalogImport", '_blank');
		$("#alterpopupWindow").jqxWindow('open');
	});
	$("#jqxwindowUploadFile").jqxWindow({ theme: 'olbius',
	    width: 480, maxWidth: 1845, height: 250, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancelUpload"), modalOpacity: 0.7
	});
	var hidden, visibilityChange; 
	if (typeof document.hidden !== "undefined") {
	  hidden = "hidden";
	  visibilityChange = "visibilitychange";
	} else if (typeof document.mozHidden !== "undefined") {
	  hidden = "mozHidden";
	  visibilityChange = "mozvisibilitychange";
	} else if (typeof document.msHidden !== "undefined") {
	  hidden = "msHidden";
	  visibilityChange = "msvisibilitychange";
	} else if (typeof document.webkitHidden !== "undefined") {
	  hidden = "webkitHidden";
	  visibilityChange = "webkitvisibilitychange";
	}
	function handleVisibilityChange() {
		  if (document[hidden]) {
		  } else {
			  if(getCookie().checkContainValue("categoryChange")){
				  deleteCookie("categoryChange");
				  getProductCategoryAjax();
			  }
		  }
	}
	document.addEventListener(visibilityChange, handleVisibilityChange, false);
	
	function getProductCategoryAjax() {
		$.ajax({
			  url: "getProductCategory",
			  type: "POST",
			  data: {productCategoryTypeId: "CATALOG_CATEGORY"},
			  success: function(res) {
				  listProductCategory = res["listProductCategorys"];
			  }
		  	}).done(function() {
		  		$("#txtPrimaryProductCategoryId").jqxDropDownList({ source: listProductCategory });
		  	});
	}
	var listImage = [];
	$(document).ready(function() {
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
	//		readURL(this.files);
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
	    		$('.jqxScrollView').jqxScrollView({ width: 170, height: 180, buttonsOffset: [0, 0]});
			}, 100);
	    }
	}
	$("#btnUploadFile").click(function () {
		readURL(listImage);
		$("#jqxwindowUploadFile").jqxWindow("close");
	});
	$(function() {
					$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
						if(info.step == 1) {
							return getProductInfoGeneral();
						}
						if(info.step == 2) {
							renderViewTotals();
							return getProductInfoDetails();
						}
					}).on('finished', function(e) {
						saveDataAjax();
					});
					$('#txtThruDate').val(null);
	});
	var listProducts = [
					<#if listProducts?exists>
						<#list listProducts as item>
							"${item.productId?if_exists}".toLowerCase(),
						</#list>
					</#if>
	                    ];
	
	var listProductCategory = [
								<#if listProductCategory?exists>
									<#list listProductCategory as item>
									{
										productCategoryId: "${item.productCategoryId?if_exists}",
										categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
									},
									</#list>
								</#if>
	                           ];
	var listProductCategorys = [
		                       	<#if listProductCategory?exists>
									<#list listProductCategory as item>
										"${item.productCategoryId?if_exists}".toLowerCase(),
									</#list>
								</#if>
		                           ];
	var mapProductCategory = {
								<#if listProductCategory?exists>
									<#list listProductCategory as item>
										"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.categoryName?if_exists)}",
									</#list>
								</#if>
							};
	
	var listQuantityUom = [
	                           <#if listQuantityUom?exists>
		                           <#list listQuantityUom as item>
		                           {
		                        	   uomId: "${item.uomId?if_exists}",
		                        	   description: "${StringUtil.wrapString(item.description?if_exists)}"
		                           },
		                           </#list>
	                           </#if>
	                           ];
	var mapQuantityUom = {
						<#if listQuantityUom?exists>
					        <#list listQuantityUom as item>
					     	   "${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
					        </#list>
					    </#if>
						};
	var listWeightUom = [
	                   <#if listWeightUom?exists>
	                       <#list listWeightUom as item>
	                       {
	                    	   uomId: "${item.uomId?if_exists}",
	                    	   description: "${StringUtil.wrapString(item.description?if_exists)}"
	                       },
	                       </#list>
	                   </#if>
	                       ];
	var mapWeightUom = {
					<#if listWeightUom?exists>
				        <#list listWeightUom as item>
				     	    "${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
				        </#list>
				    </#if>
					};
	
	$("#txtPrimaryProductCategoryId").jqxDropDownList({ theme: 'olbius', width: 218, height: 30, source: listProductCategory, displayMember: "categoryName", valueMember: "productCategoryId", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'});
	$("#txtQuantityUomId").jqxDropDownList({ theme: 'olbius', width: 218, height: 30, source: listQuantityUom, displayMember: "description", valueMember: "uomId", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'});
	$("#txtWeightUomId").jqxDropDownList({ theme: 'olbius', width: 218, height: 30, source: listWeightUom, displayMember: "description", valueMember: "uomId", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'});
	 
	$("#txtWeight").jqxNumberInput({ theme: 'olbius', width: 218, height: 30, inputMode: 'simple', spinButtons: true, decimalDigits: 3 });
	$("#txtProductWeight").jqxNumberInput({ theme: 'olbius', width: 218, height: 30, inputMode: 'simple', spinButtons: true, decimalDigits: 3 });
	$('#description1').jqxEditor({
	    theme: 'olbiuseditor',
	    width:"99%"
	});
	$("#txtFromDate").jqxDateTimeInput({theme: "olbius", width: '220px' });
	$("#txtThruDate").jqxDateTimeInput({theme: "olbius", width: '220px' });
	$('#txtThruDate ').jqxDateTimeInput('setMinDate', new Date(new Date().setDate(new Date().getDate() - 1)));
	$('#txtFromDate').on('valueChanged', function (event){  
		var jsDate = event.args.date; 
		$('#txtThruDate ').jqxDateTimeInput('setMinDate', jsDate);
	});
	function getProductInfoGeneral() {
		if (!$('#step1').jqxValidator('validate')) {
			return false;
		}
		var row;
		var description = $("#description1").val();
		var productId = $("input[name='txtProductId']").val();
		var primaryProductCategoryId = $("#txtPrimaryProductCategoryId").val();
		var internalName = $("input[name='txtInternalName']").val();
		var brandName = $("input[name='txtBrandName']").val();
		var productName = $("input[name='txtProductName']").val();
		var fromDate = $("#txtFromDate").val().toTimeStamp();
		var thruDate = $("#txtThruDate").val().toTimeStamp();
	    row = {productId: productId, primaryProductCategoryId: primaryProductCategoryId, internalName: internalName, 
	    		brandName: brandName, productName: productName, description: description, fromDate: fromDate, thruDate: thruDate };
	    return row;
	}
	$('#step1').jqxValidator({
	    rules: [
					{ input: '#txtProductId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtProductId', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductId").val();
							if (!value.containSpecialChars() && !hasWhiteSpace(value)) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtProductId', message: '${StringUtil.wrapString(uiLabelMap.ProductIdAlreadyExists)}', action: 'keyup, blur',
						rule: function (input, commit) {
							if (updateMode) {
								return true;
							}
							var value = $("#txtProductId").val().toLowerCase();
							if (_.indexOf(listProducts, value) === -1) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtPrimaryProductCategoryId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#txtPrimaryProductCategoryId").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtInternalName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtInternalName', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtInternalName").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtProductName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtProductName', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductName").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtBrandName', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'keyup, blur', rule: 'required' },
					{ input: '#txtBrandName', message: '${StringUtil.wrapString(uiLabelMap.ContainSpecialSymbol)}', action: 'keyup, blur',
						rule: function (input, commit) {
							var value = $("#txtProductName").val();
							if (!value.containSpecialChars()) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtFromDate', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
	                	rule: function (input, commit) {
	                		var value = $("#txtFromDate").val().toMilliseconds();
	                		if (value > 0) {
	                			return true;
							}
	                		return false;
	                	}
	                },
	                { input: '#txtThruDate', message: '${StringUtil.wrapString(uiLabelMap.DateNotValid)}', action: 'valueChanged', 
	                	rule: function (input, commit) {
	                		var thruDate = $("#txtThruDate").val().toMilliseconds();
	                		if (!thruDate) {
	                			return true;
							}
	                		var fromDate = $("#txtFromDate").val().toMilliseconds();
	                		if (fromDate <= thruDate) {
	                			return true;
							}
	                		return false;
	                	}
	                }
	           ],
	           position: 'bottom'
	});
	function hasWhiteSpace(s) {
		  return /\s/g.test(s);
	}
	function getProductInfoDetails() {
		if (!$('#step2').jqxValidator('validate')) {
			return false;
		}
		var row;
		var quantityUomId = $("#txtQuantityUomId").val();
		var weightUomId = $("#txtWeightUomId").val();
		var weight = $("#txtWeight").val();
		var productWeight = $("#txtProductWeight").val();
	    row = { quantityUomId: quantityUomId, weightUomId: weightUomId, weight: weight, productWeight: productWeight};
	    return row;
	}
	$('#step2').jqxValidator({
	    rules: [
					{ input: '#txtWeight', message: '${StringUtil.wrapString(uiLabelMap.WeightNotValid)}', action: 'valueChanged', 
						rule: function (input, commit) {
							var value = $("#txtWeight").val();
							if (value > 0) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtProductWeight', message: '${StringUtil.wrapString(uiLabelMap.WeightNotValid)}', action: 'valueChanged', 
						rule: function (input, commit) {
							var value = $("#txtProductWeight").val();
							if (value > 0) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtWeightUomId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#txtWeightUomId").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
					{ input: '#txtQuantityUomId', message: '${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}', action: 'change', 
						rule: function (input, commit) {
							var value = $("#txtQuantityUomId").val();
							if (value) {
								return true;
							}
							return false;
						}
					},
	           ],
	           position: 'bottom'
	});
	function getTotalInformation() {
		var generalRow = getProductInfoGeneral();
		var detailsRow = getProductInfoDetails();
	    row = $.extend(generalRow, detailsRow);
	    return row;
	}
	function renderViewTotals() {
		var data = getTotalInformation();
		$("#productIdTotal").text(data.productId);
		$("#productCategoryIdTotal").text(mapProductCategory[data.primaryProductCategoryId]);
		$("#productInternalNameTotal").text(data.internalName);
		$("#productNameTotal").text(data.productName);
		$("#productBrandNameTotal").text(data.brandName);
		$("#weightTotal").text(data.weight);
		$("#netWeightTotal").text(data.productWeight);
		$("#weightUomIdTotal").text(mapWeightUom[data.weightUomId]);
		$("#quantityUomIdTotal").text(mapQuantityUom[data.quantityUomId]);
		$("#descriptionTotal").html(data.description);
		$("#txtFromDateTotal").text(data.fromDate.timeStampToTimeOlbius());
		$("#txtThruDateTotal").text(data.thruDate.timeStampToTimeOlbius());
	}
	function saveDataAjax() {
		var data = getTotalInformation();
		data.productTypeId = "FINISHED_GOOD";
		data.detailImageUrl = "/DELYS/delys/productImage" + data.productId;
		if (locale=="vi") {
			data.weight = data.weight.toString().replaceAll(".", ",");
			data.productWeight = data.productWeight.toString().replaceAll(".", ",");
		}
		var url = "createProduct";
		if (updateMode) {
			url = "updateProduct";
			setCookie("updateProduct");
		}else {
			setCookie("newProduct");
		}
		$.ajax({
			  url: url,
			  type: "POST",
			  data: data,
			  success: function(res) {
				  
			  }
		  	}).done(function() {
		  		addProductToCategoryAjax({
		  			fromDate : data.fromDate,
		  			thruDate : data.thruDate,
		  			productCategoryId: data.primaryProductCategoryId,
		  			productId: data.productId
		  		});
		  	});
	}
	function addProductToCategoryAjax(data) {
		$.ajax({
			  url: "addProductToCategoryCustom",
			  type: "POST",
			  data: data,
			  success: function(res) {
				  
			  }
		  	}).done(function() {
		  		deleteOldFolder();
	  	});
	}
	function deleteOldFolder(curPath) {
		if (listImage.length == 0) {
			window.history.back();
			return;
		}
		if (!curPath) {
			curPath = "/DELYS/delys/productImage" + $("input[name='txtProductId']").val();
		}
		$.ajax({
			  url: "jackrabbitDeleteNodeAjax",
			  type: "POST",
			  data: {nodePath: curPath},
			  success: function(res) {
				  if (res["_ERROR_MESSAGE_"]) {
				  }
			  }
	  	}).done(function() {
	  		uploadNewImages();
	  	});
	}
	function uploadNewImages() {
		var newFolder = "/delys/productImage" + $("input[name='txtProductId']").val();
		for ( var d in listImage) {
			var path = "";
			var form_data= new FormData();
			form_data.append("uploadedFile", listImage[d]);
			form_data.append("folder", newFolder);
			jQuery.ajax({
				url: "uploadDemo",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				async: false,
				success: function(res) {
					path = res["path"];
		        }
			}).done(function() {
				
			});
		}
		setTimeout(function() {
			window.history.back();
		}, 200);
	}
	
	$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#container", opacity: 0.9, autoClose: true, template: "info" });
	var locale = '${locale}';
	
	var updateMode = false;
	<#if thisProduct?exists>
		$(document).ready(function() {
			getProductImages("${thisProduct.detailImageUrl?if_exists}");
			updateMode = true;
			$(".widget-header").find('h4').text("${StringUtil.wrapString(uiLabelMap.PageTitleEditProduct)}");
			$("#txtProductId").val("${StringUtil.wrapString(productId?if_exists)}");
			$("#txtPrimaryProductCategoryId").jqxDropDownList('val', '${thisProduct.primaryProductCategoryId?if_exists}');
	//		$("#txtPrimaryProductCategoryId").jqxDropDownList({ disabled: true });
			
			$('#txtPrimaryProductCategoryId').on('change', function (event){
				    var args = event.args;
				    if (args) {
					    var index = args.index;
					    var item = args.item;
					    var label = item.label;
					    var value = item.value;
					    $('#txtFromDate').jqxDateTimeInput({disabled: false});
					} 
			});
			
			$("#txtProductId").attr('disabled','disabled');
			$("#txtInternalName").val("${StringUtil.wrapString(thisProduct.internalName?if_exists)}");
			$("#txtProductName").val("${StringUtil.wrapString(thisProduct.productName?if_exists)}");
			$("#txtBrandName").val("${StringUtil.wrapString(thisProduct.brandName?if_exists)}");
			$("#description1").jqxEditor('val', '${StringUtil.wrapString(thisProduct.description?if_exists)}');
			
			var fromDate = "${thisProduct.fromDate?if_exists}";
			fromDate?fromDate=new Date(fromDate):fromDate = null;
			
			$('#txtFromDate').jqxDateTimeInput('val', fromDate);
			$('#txtFromDate').jqxDateTimeInput({disabled: true});
			
			var thruDate = "${thisProduct.thruDate?if_exists}";
			thruDate?thruDate=new Date(thruDate):thruDate = null;
			$('#txtThruDate').jqxDateTimeInput('val', thruDate);
			
			var weight = "${thisProduct.weight?if_exists}";
			weight = weight.replace(",", ".");
			$("#txtWeight").val(weight);
			var productWeight = "${thisProduct.productWeight?if_exists}";
			productWeight = productWeight.replace(",", ".");
			$("#txtProductWeight").val(productWeight);
			$("#txtWeightUomId").jqxDropDownList('val', '${thisProduct.weightUomId?if_exists}');
			$("#txtQuantityUomId").jqxDropDownList('val', '${thisProduct.quantityUomId?if_exists}');
			locale == "vi_VN"?locale="vi":locale=locale;
			if (locale=="vi") {
				$("#txtWeight").jqxNumberInput({ decimalSeparator: ',' });
				$("#txtProductWeight").jqxNumberInput({ decimalSeparator: ',' });
			}
			
		});
		function getProductImages(nodePath) {
			if (nodePath) {
				var fileUrl = [];
		    	jQuery.ajax({
		            url: "getFileScanAjax",
		            type: "POST",
		            data: {nodePath : nodePath },
		            success: function(res) {
		            	fileUrl = res["childNodes"];
		            }
		        }).done(function() {
		        	if (fileUrl) {
		        		showProductImages(fileUrl);
					}
		    	});
			}
		}
		function showProductImages(fileUrl) {
			if($('.jqxScrollView').length > 0){
	    		$('.jqxScrollView').jqxScrollView("destroy");
	    	}
			htmlImage = "<div class='jqxScrollView'>";
			for ( var i in fileUrl) {
				var link = "/webdav/repository/default" + "/DELYS/delys/productImage" + "${StringUtil.wrapString(productId?if_exists)}" + "/" + fileUrl[i];
				link = encodeURI(link);
				htmlImage += "<div><div class='photo' style='background-image:url(\"" + link + "\")'></div></div>";
			}
			setTimeout(function() {
	    		htmlImage += "</div>";
	    		$("#productImageViewer").html(htmlImage);
	    		$("#productImageViewerTotal").html(htmlImage);
	    		$('.jqxScrollView').jqxScrollView({ width: 170, height: 180, buttonsOffset: [0, 0]});
			}, 100);
		}
	</#if>
	
	<#if security.hasEntityPermission("PRODUCT", "_ADMIN", session)>
		$(document).ready(function() {
			
		});
	<#else>
		$(document).ready(function() {
			$(".widget-header").find('h4').text("${StringUtil.wrapString(uiLabelMap.ViewDetailsProduct)}");
			btnNext.click();
			btnNext.click();
			$(".wizard-actions").hide();
		});
	</#if>
</script>
<style type="text/css">
	.photo{
	    width: 170px;
	    height: 180px;
	    background-color: white;
	    background-position: center;
		background-size: contain;
	    background-repeat: no-repeat;
	}
	body {
		  -webkit-user-select: none;
		     -moz-user-select: -moz-none;
		      -ms-user-select: none;
		          user-select: none;
	}.controls {
		word-wrap: break-word;
	}
</style>