<#include "macroTree.ftl"/>
<script type="text/javascript">
	<#assign localData = []>
	<#if listCustomTimePeriodRoot?exists>
		<#list listCustomTimePeriodRoot as item>
			<#assign localData = localData + [{"customTimePeriodId": "${item.customTimePeriodId}","periodName": "${item.periodName?if_exists}","parentId": "${item.parentPeriodId?default(-1)}","hasItem": "true"}]/>
		</#list>
	</#if>
	
	function getOrderItemStaticWithRole(){
        var jsc="";
        $.ajax({
            url: '<@ofbizUrl>getCatalogAndCategoryForSalesJSTree</@ofbizUrl>',
            type: 'POST',
            dataType: 'json',
            async:false,
            success:function(data){
                jsc=data.results;
            }
        });
        return jsc;
    }
    <#assign satementTypeList = delegator.findByAnd("SalesStatementType", {"salesTypeId" : "SALES_OUT"}, null, false)/>
    <#if satementTypeList?exists>
		var satementTypeData = [
			<#list satementTypeList as statementTypeItem>
			{	salesTypeId: '${statementTypeItem.salesTypeId}',
				description: '${StringUtil.wrapString(statementTypeItem.get("description", locale))}',
			},
			</#list>
		];
	<#else>
		var satementTypeData = [];
	</#if>
</script>
<#--
<#include "macroTreeGrid.ftl"/>
<@jqxTreeGrid/>
-->
<style type="text/css">
	#jqxTreeProduct{
		float:right;
	}
</style>

<style type="text/css">
	.btn.btn-prev#btnPrevWizard {
		background-color: #87b87f!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:hover {
		background-color: #629b58!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:disabled {
		background-color: #abbac3!important;
	  	border-color: #abbac3;
	}
	.nav-tabs {
		border-bottom:none;
	}
</style>
<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">
		${uiLabelMap.DACreateNewSalesStatement} 
	</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DACreateNewSalesStatement}" data-placement="bottom">1</span>
			</li>

			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DACreateSalesStatementDetail}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<#--
		<#if salesPolicyId?has_content && salesPolicy?exists>
			<a href="<@ofbizUrl>viewSalesPromo?salesPolicyId=${salesPolicy.salesPolicyId?if_exists}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAViewDetail} ${salesPolicy.salesPolicyId?if_exists}" data-placement="bottom" class="no-decoration">
				<i class="fa-search open-sans open-sans-index" style="font-size:16pt"></i>
			</a>
		</#if>
		-->
	</div>
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />
	
	<div id="jqxNotification">
    	<div id="notificationContent"></div>
    </div>
    <div id="container" style="background-color: transparent; overflow: auto;">
    </div>
    
	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<div class="row-fluid">
				<div class="span4">
					<div style="margin:5px 0 10px 0">
						<label class="required" style="display: inline-block; vertical-align:middle; margin: 0 5px 0 0">${uiLabelMap.DAStatementType}</label>
						<div style="display: inline-block; vertical-align:middle">
							<div id="salesStatementTypeId"></div>
						</div>
					</div>
					<div>
						<#assign dataFields = "[
				                    { name: 'customTimePeriodId' },
				                    { name: 'periodName' },
				                    { name: 'parentId' },
				                    { name: 'items'}
				                ]">
						<@jqxTree id="jqxTreeYear" width="98%" height="auto" dataFields=dataFields idSource="customTimePeriodId" labelSource="periodName" localData=localData dataUnFormat=true/>
					</div>
				</div>
				<div class="span8" style="text-align:right">
					<@jqxTree id="jqxTreeProduct" width="98%" height="auto" hasThreeStates=true checkboxes=true dataAjaxFunc="getOrderItemStaticWithRole()" idSource="id" />
					<#--
					<#assign dataFieldsProduct = "[
				                    { name: 'id' },
				                    { name: 'parentId' },
				                    { name: 'dataType' },
				                    { name: 'label'},
				                    { name: 'value'},
				                    { name: 'expanded'},
				                    { name: 'items'}
				                ]">
					<@jqxTree id="jqxTreeProduct" width="98%" height="auto" dataFields=dataFieldsProduct hasThreeStates=true checkboxes=true idSource="id" labelSource="label" localData=listCategoryProductRoot dataUnFormat=true dataUnFormat2=true/>
					-->
				</div>
			</div>
		</div>

		<div class="step-pane" id="step2">
			${screens.render("component://delys/widget/sales/SalesScreens.xml#NewSalesStatementDetail")}
		</div>
	</div>
	
	<div class="row-fluid wizard-actions">
		<button class="btn btn-prev btn-small" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.DAPrev}
		</button>
		<button class="btn btn-success btn-next btn-small" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard" style="display:none">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
		<button class="btn btn-success btn-next btn-small" id="btnNextWizard2" style="display:inline-block">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
<div style="position:relative">
	<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DAProcessing}...</span>
			</div>
		</div>
	</div>
</div>

<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	function isNotEmptyComboBoxOneById(comboBox) {
		var count = 0;
		var dataListSelected = $(comboBox).jqxComboBox('getSelectedItem');
		if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
			if (dataListSelected != null) count++;
		}
		if (count > 0) return true;
		return false;
	}
	function getDataStrUrlComboBoxOneById(id) {
		var data = "";
		var dataListSelected = $("#" + id).jqxComboBox('getSelectedItem');
		if (dataListSelected != undefined && dataListSelected != null && !(/^\s*$/.test(dataListSelected))) {
		 	if (dataListSelected != null) data += "&" + id + "=" + dataListSelected.value;
		}
		return data;
	}
	function createSalesStatementAsync() {
		if (!isNotEmptyComboBoxOneById($("#salesStatementTypeId"))) {
			var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseSalesStatementType}!</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
		}
		
		var catalogId = [];
		
		var data = getDataStrUrlComboBoxOneById("salesStatementTypeId");
		var customTimePeriodId = $('#jqxTreeYear').jqxTree('getSelectedItem');
		if (customTimePeriodId != null) data += "&customTimePeriodId=" + customTimePeriodId.value;
		var categoryList = [];
       	var productList = [];
       	var categoryOnlyList = [];
       	var categoryBrowsed = [];
       	var categoryHasProd = [];
       	var treeItems = $('#jqxTreeProduct').jqxTree('getItems');
       	
		for (var i = 0; i < treeItems.length; i++) {
           	var item = treeItems[i];
           	var parentItem = $('#jqxTreeProduct').jqxTree('getItem', item.parentElement);
           	var id = item.id;
           	var dataType = id.split("_")[0];
           	if (item.checked) {
           		if (parentItem) {
           			if ("CATEONLYSPECIAL" == dataType) {
		           		if (jQuery.inArray(parentItem.value, categoryHasProd) < 0) {
	           				categoryOnlyList.push(item.value);
	       				}
		           		categoryBrowsed.push(parentItem.value);
	           		} else if ("PROD" == dataType) {
		           		if (jQuery.inArray(parentItem.value, categoryBrowsed) >= 0) {
		           			var index2 = categoryOnlyList.indexOf(parentItem.value);
	           				if (index2 > -1) categoryOnlyList.splice(index2, 1);
		           			
	           				var index2 = categoryList.indexOf(parentItem.value);
	           				if (index2 > -1) categoryList.splice(index2, 1);
	           			}
		           		productList.push(item.value);
		           		categoryBrowsed.push(parentItem.value);
		           		categoryHasProd.push(parentItem.value);
		           	} else if ("CATE" == dataType) {
		           		if (jQuery.inArray(item.value, categoryHasProd) < 0) {
	           				categoryList.push(item.value);
	       				}
		           		categoryBrowsed.push(item.value);
		           	}
		           	var parentDataType = parentItem.id.split("_")[0];
		           	if ("CATA" == parentDataType) {
		           		var valueCatalog = parentItem.value;
		           		if (parentItem.id == parentItem.value) {
		           			valueCatalog = parentItem.value.split("_")[1];
		           		}
		           		if (jQuery.inArray(valueCatalog, catalogId) < 0) {
		           			catalogId.push(valueCatalog);
		           		}
		           	}
           		}
           	}
       	}
       	
       	if (catalogId.length < 0) {
       		var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>";
       		message0 += "${uiLabelMap.DAYouNotYetChooseCatalog}!";
       		message0 += "</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
       	} else if (catalogId.length > 1) {
       		var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>";
       		message0 += "${uiLabelMap.DAYouCannotChooseGreateThanOneCatalog}!";
       		message0 += "</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
       	}
       	
       	for (var i = 0; i < categoryList.length; i++) {
       		data += "&categoryId=" + categoryList[i];
       	}
       	for (var i = 0; i < productList.length; i++) {
       		data += "&productId=" + productId[i];
       	}
       	for (var i = 0; i < categoryOnlyList.length; i++) {
       		data += "&categoryOnlyId=" + categoryOnlyList[i];
       	}
		data += "&catalogId=" + catalogId[0];
		$.ajax({
            type: "POST", 
            url: "initNewSalesSatementDetail",
            data: data,
            beforeSend: function () {
				$("#info_loader").show();
				$("#btnNextWizard2").addClass("disabled");
			}, 
            success: function (data) {
            	if (data.thisRequestUri == "json") {
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
			        return false;
            	} else {
            		jQuery('#btnNextWizard').css("display", "inline-block");
            		jQuery('#btnNextWizard2').css("display", "none");
            		jQuery('#btnNextWizard').trigger("click");
            		$("#step-title").html("${uiLabelMap.DACreateSalesStatementDetail}");
            		$("#step2").html(data);
            		return true;
            	}
            },
            error: function () {
                //commit(false);
            },
            complete: function() {
		        $("#info_loader").hide();
		        $("#btnNextWizard2").removeClass("disabled");
		    }
        });
        return false;
	}
	
	$(function() {
		initSalesStatementTypeId($("#salesStatementTypeId"), ["SALES_OUT"]);
		$('[data-rel=tooltip]').tooltip();
	
		$(".wizard-steps li").click(function(e){
			var target = $(this).attr('data-target');
			var wiz = $('#fuelux-wizard').data('wizard');
			currentStep = wiz.currentStep;
			if (currentStep == 1) {
				return;
			} else if (currentStep == 2) {
				if (target == "#step1") {
				    $("#step-title").html("${uiLabelMap.DACreateNewSalesStatement}");
				}
			}
		});
		
		$("#btnNextWizard2").on("click", function(){
			$('#container').empty();
			createSalesStatementAsync();
		});
		
		var $validation = false;
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
			} else if ((info.step == 2) && (info.direction == "previous")) {
				jQuery('#btnNextWizard').css("display", "none");
        		jQuery('#btnNextWizard2').css("display", "inline-block");
        		$("#step-title").html("${uiLabelMap.DAInputInfoOrder}");
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result) {
				if(result) {
					$("#btnPrevWizard").addClass("disabled");
					$("#btnNextWizard").addClass("disabled");
					//window.location.href = "processOrderSales";
					
					buildDataBeforeSend();
				}
			});
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
	});
	
	function buildDataBeforeSend(){};
</script>

<script type="text/javascript">
	$(document).ready(function () {
		$('#jqxTreeYear').on('expand', function (event) {
	    	var _item = $('#jqxTreeYear').jqxTree('getItem', event.args.element);
	        var label = _item.label;
	        var value = _item.value;
	        var $element = $(event.args.element);
	        var loader = false;
	        var loaderItem = null;
	        var children = $element.find('ul:first').children();
	        $.each(children, function () {
	            var item = $('#jqxTreeYear').jqxTree('getItem', this);
	            if (item && item.label == 'Loading...') {
	                loaderItem = item;
	                loader = true;
	                return false
	            }
	        });
	        if (loader) {
	            jQuery.ajax({
	                url: 'getCustomTimePeriodJson',
	                
	                type: 'POST',
	                data: {"parentPeriodId": value},
	                success: function (data) {
	                    var items = [];
	                    var dataList = data.listCustomTimePeriod;
	                    for (var i in dataList) {
	                    	var itemData = dataList[i];
	                        var _value = itemData.customTimePeriodId;
	                        var _label = itemData.periodName;
	                        var _id = itemData.customTimePeriodId;
	                        items.push({
	                            label: _label,
	                            id: _id,
	                            value: _value,
	                            "items": [{"label": "Loading..."}]
	                        });
	                    }
	                    $('#jqxTreeYear').jqxTree('addTo', items, $element[0]);
	                    $('#jqxTreeYear').jqxTree('removeItem', loaderItem.element);
	                }
	            });
	        }
	    });
	    $('#jqxTreeProduct').on('expand', function (event) {
	    	var tree = $('#jqxTreeProduct');
	    	var _item = $(tree).jqxTree('getItem', event.args.element);
	        var label = _item.label;
	        var value = _item.value;
	        var $element = $(event.args.element);
	        var loader = false;
	        var loaderItem = null;
	        var isChecked = false;
	        var children = $element.find('ul:first').children();
	        $.each(children, function () {
	            var item = $(tree).jqxTree('getItem', this);
	            if (item && item.label == '${StringUtil.wrapString(uiLabelMap.DALoading)}...') {
	                loaderItem = item;
	                loader = true;
	                if (item.checked) isChecked = true;
	                return false
	            }
	        });
	        if (loader) {
	            jQuery.ajax({
	                url: 'getCateAndProdChildInCate',
	                
	                type: 'POST',
	                data: {"productCategoryId": value},
	                success: function (data) {
	                    var items = [];
	                    var dataList = data.results;
	                    for (var i in dataList) {
	                    	var itemData = dataList[i];
	                        items.push({
	                            id: itemData.id,
	                            parentId: itemData.parentId,
	                            dataType: itemData.dataType,
	                            value: itemData.value,
	                            label: itemData.label,
	                            items: itemData.items,
	                        });
	                    }
	                    $(tree).jqxTree('addTo', items, $element[0]);
	                    $(tree).jqxTree('removeItem', loaderItem.element);
	                    
	                    if (isChecked) {
	                    	for (var j = 0; j < items.length; j++) {
	                    		var item = items[j];
	                    		$(tree).jqxTree('checkItem', $("#" + item.id)[0], true);
	                    	}
	                    }
	                }
	            });
	        }
	    });
	});
	
	function initSalesStatementTypeId(comboBox, selectArr){
		var sourceStatementType = {
			localdata: satementTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'salesTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterStatementType = new $.jqx.dataAdapter(sourceStatementType, {
	        	formatData: function (data) {
	                if ($(comboBox).jqxComboBox('searchString') != undefined) {
	                    data.searchKey = comboBox.jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    comboBox.jqxComboBox({source: dataAdapterStatementType, 
	    	multiSelect: false, 
	    	width: 200, 
	    	height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	valueMember: "salesTypeId", 
	    	autoDropDownHeight: true, 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterStatementType.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterStatementType.dataBind();
	        }
	    });
	    if (selectArr != undefined && selectArr != null && selectArr.length > 0){
	    	for (var i = 0; i < selectArr.length; i++) {
	    		var item = selectArr[i];
	    		$(comboBox).jqxComboBox('selectItem', item);
	    	}
		}
	}
</script>