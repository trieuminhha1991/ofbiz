<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/hrresources/js/shim.js" type="text/javascript"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<script type="text/javascript">
	var cellClassCommon = function (row, columnfield, value, gridObj) {
 		var data = $(gridObj).jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if (data.isVirtual == "Y") {
 				return "row-group";
 			} else if (data.isVariant == "Y") {
 				return "row-single";
 			}
 		}
    }
</script>

<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>
                	${uiLabelMap.BSViewDetailSalesForecast}
	            	<#--<#if customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, false)!/>
                		${customTimeParent.periodName?if_exists}
                	</#if>
                	(<a id="percentClick" href="#modal-table" role="button" class="green" data-toggle="modal">0%</a>)
                	&nbsp;&nbsp;&nbsp;
                	<#if hasOlbPermission("MODULE", "SALESFORECAST_NEW", "")>
	                	<button class="btn btn-mini btn-primary" onClick="javascript:OlbForecastView.onSubmitFormCreateUpdateSalesForecast()"><i class="icon-save"></i>${uiLabelMap.CommonSave}</button>
	                	<button class="btn btn-mini btn-primary" onClick="javascript:window.location.href='newSalesForecastDetailVer?salesForecastId=${salesForecastId?if_exists}';"><i class="fa-refresh open-sans"></i>&nbsp;${uiLabelMap.CommonReset}</button>
                	</#if>-->
                </h4>
                <div class="widget-toolbar no-border">
                    <ul class="nav nav-tabs" id="myTab2">
                        <#list sfTabsContent as salesForecastPartyItem>
							<li <#if salesForecastPartyItem_index == 0> class="active"</#if> style="margin-top:0; margin-bottom:-1px; padding-top:0 !important; padding-bottom:0 !important">
								<a data-toggle="tab" href="#${salesForecastPartyItem_index}-tab" onClick="javascript:OlbForecastView.onActiveTab('${salesForecastPartyItem_index}')">${salesForecastPartyItem.internalPartyIds}</a>
							</li>
						</#list>
						<#--<li style="margin-top:0; margin-bottom:-1px; padding-top:0 !important; padding-bottom:0 !important" <#if !sfTabsContent?exists || !(sfTabsContent?size > 0)>class="active"</#if>>
							<a id="active-tab-plus" data-toggle="tab" href="#plus-forecast-tab"><i class="fa fa-plus-circle blue"></i></a>
						</li>-->
                    </ul>
                </div>
            </div>
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right no-padding-top">
                	<div id="tab-content" class="tab-content padding-4">
                		<div id="containerSf" style="background-color: transparent; overflow: auto;"></div>
					    <div id="jqxNotificationSf" style="margin-bottom:5px">
					        <div id="notificationContentSf">
					        </div>
					    </div>
                		<#assign totalRow = 0/>
                		<#if sfTabsContent?exists && (sfTabsContent?size > 0)>
                    		<#list sfTabsContent as salesForecastPartyItem>
	                    		<#if salesForecastPartyItem_index == 0>
	                    			<input id="tabActiveDefault" type="hidden" value="${salesForecastPartyItem_index}"/>
	                    		</#if>
	                    		<input id="tabActiveInput_${salesForecastPartyItem_index}" type="hidden" value="0"/>
								<div id="${salesForecastPartyItem_index}-tab" class="tab-pane<#if salesForecastPartyItem_index == 0> active</#if>">
							        <input id="salesForecastId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastId?if_exists}"/>
							        <input id="parentSalesForecastId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastId?if_exists}"/>
							        <input id="customTimePeriodId_${salesForecastPartyItem_index}" type="hidden" value="${customTimePeriodId?if_exists}"/>
							        <input id="internalPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.internalPartyIds?if_exists}"/>
							        <input id="organizationPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.organizationPartyId?if_exists}"/>
							        <input id="currencyUomId_${salesForecastPartyItem_index}" type="hidden" value="${currencyUomId?if_exists}"/>
							        
							        <#include "salesForecastDetailCreateTabContentJQ.ftl"/>
								</div>
							</#list>
						<#else>
	                    	TAO MOI
	                    </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<div id="modal-table" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			Set percent for all Sales forecast items
		</div>
	</div>

	<div class="modal-body no-padding">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-small">
				<div class="control-group">
					<label class="control-label" for="percentNumber">${uiLabelMap.BSPercent}</label>
					<div class="controls">
						<div class="span12">
							<input type="text" size="30" name="percentNumber" id="percentNumber" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			${uiLabelMap.BSClose}
		</button>
		<div class="pagination pull-right no-margin">
			<button class="btn btn-small btn-primary pull-left" onClick="javascript:OlbForecastView.onResetPercent();">
				<i class="icon-ok"></i>
				${uiLabelMap.BSOk}
			</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true/>
<script type="text/javascript">
	var listForecastInput = [];
	var body = $("html, body");
	$(function(){
		OlbForecastView.init();
	});
	
	var OlbForecastView = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			var tmpWidth = '100%';
			$("#containerSf").width(tmpWidth);
            $("#jqxNotificationSf").jqxNotification({ 
            	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
            	width: tmpWidth, 
            	appendContainer: "#containerSf", 
            	opacity: 1, autoClose: true, template: "success" 
            });
		};
		var initEvent = function(){
			$('#modal-table').on('show.bs.modal', function (e) {
			  	setTimeout(function() {$("#percentNumber").focus()}, 1000);
			});
			$('#percentNumber').live('keyup',function(e){
		     	var p = e.which;
		     	if(p==13){
		         	onResetPercent();
		     	}
		 	});
		};
		var onResetPercent = function(){
			$("#modal-table").modal("hide");
			bootbox.confirm("${uiLabelMap.BSThisChangeWillApplyForSalesForesastTableInThisScreen}", function(result){
				if(result){
					resetPercent();
				} else {
					$("#modal-table").modal("show");
				}
			});
		};
		var resetPercent = function(){
			var percentNumberVal = $("#percentNumber").val();
			var tabActiveCurrent = $("#tabActiveDefault").val();
			if (percentNumberVal == null || percentNumberVal == "") {
				percentNumberVal = 0;
			}
			$("#percentClick").text(percentNumberVal + "%");
			$("#tabActiveInput_" + tabActiveCurrent).val(percentNumberVal);
			$('div[id^="percentDiv_' + tabActiveCurrent + '"]').html(percentNumberVal + "%");
			$('input[id^="percentInput_' + tabActiveCurrent + '"]').val(percentNumberVal);
			//var listForecast = $('div[id^="forecastInput_"]');
			
			//console.log(listForecastInput.length);
			var listForecastInput = $('input[id^="forecastInput_"]');
			for (var i = 0; i < listForecastInput.length; i++) {
				var objectProcess = listForecastInput[i];
				var idObjectProcess = $(objectProcess).attr("id");
				var subIdProcess = idObjectProcess.substring(13, idObjectProcess.length);
				var ayoForecastInputProcess = parseFloat($("#ayoForecastInput" + subIdProcess).val());
				var percentInputProcess = parseFloat($("#percentInput" + subIdProcess).val());
				if (isNaN(ayoForecastInputProcess)) {ayoForecastInputProcess = 0;}
				if (isNaN(percentInputProcess)) {percentInputProcess = 0;}
				var valueAfterCaculate = (ayoForecastInputProcess * percentInputProcess / 100);
				$("#forecastInput" + subIdProcess).val(valueAfterCaculate);
				<#if locale == "vi">
					$("#forecastDiv" + subIdProcess).html(FormatNumberBy3(valueAfterCaculate, ",", "."));
				<#else>
					$("#forecastDiv" + subIdProcess).html(valueAfterCaculate);
				</#if>
			}
		};
		var onActiveTab = function(text){
			$("#tabActiveDefault").val(text);
			$("#percentClick").text($("#tabActiveInput_" + text).val() + "%");
			$('input[id^="forecastInput_' + text + '_"]').each(function(){
				if (listForecastInput.indexOf($(this).attr("id")) == -1) {
					listForecastInput.push($(this).attr("id"));
				}
				//$(this).text()
			});
		};
		var onSubmitFormCreateUpdateSalesForecast = function(){
			var tabActiveCurrent = $("#tabActiveDefault").val();
			//$("#formCreateUpdateSalesForecast_" + tabActiveCurrent).submit();
			var jqxGridId = "jqxSalesForecast" + tabActiveCurrent;
			var dataRow = $("#" + jqxGridId).jqxGrid("getboundrows");
			var dataList = [];
			if (typeof(dataRow) != 'undefined') {
			    dataRow.forEach(function (dataItem) {
                    if (dataItem != window) {
                        var itemMap = dataItem;
                        itemMap.internalName = "";
                        itemMap.features = "";
                        dataList.push(itemMap);
                    }
                });
			}
			var salesForecastId = $("#salesForecastId_" + tabActiveCurrent).val();
			var parentSalesForecastId = $("#parentSalesForecastId_" + tabActiveCurrent).val();
			var customTimePeriodId = $("#customTimePeriodId_" + tabActiveCurrent).val();
			var internalPartyId = $("#internalPartyId_" + tabActiveCurrent).val();
			var organizationPartyId = $("#organizationPartyId_" + tabActiveCurrent).val();
			var currencyUomId = $("#currencyUomId_" + tabActiveCurrent).val();
			
			var listPeriodData = [
			<#if listPeriodThisAndChildren?exists>
				<#list listPeriodThisAndChildren as item>
				"${item.customTimePeriodId}",
				</#list>
			</#if>
			];
			
			if (dataList.length > 0) {
				$.ajax({
					type: 'POST',
					url: 'createUpdateForecastAdvanceJson',
					datatype: 'json',
					data: {
						salesForecastId: salesForecastId,
						parentSalesForecastId: parentSalesForecastId,
						customTimePeriodId: customTimePeriodId,
						internalPartyId: internalPartyId,
						organizationPartyId: organizationPartyId,
						currencyUomId: currencyUomId,
						productList : JSON.stringify(dataList),
						customTimePeriodList: JSON.stringify(listPeriodData),
					},
					beforeSend: function(){
						$("#loader_page_common").show();
					},
					success: function(data){
						processResultInit(data);
						window.location.href = 'newSalesForecastDetailVer?salesForecastId=${salesForecastId?if_exists}';
					},
					error: function(data){
						alert("Send request is error");
					},
					complete: function(data){
						$("#loader_page_common").hide();
					},
				});
			} else {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseProduct}!");
				return false;
			}
		};
		var onDowloadEditFileExcel = function(){
			
		};
		var processResultInit = function(data){
			if (data.thisRequestUri == "json") {
        		var errorMessage = "";
		        if (data._ERROR_MESSAGE_LIST_ != null) {
		        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
		        		errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
		        	}
		        }
		        if (data._ERROR_MESSAGE_ != null) {
		        	errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
		        }
		        if (errorMessage != "") {
		        	$('#containerSf').empty();
		        	$('#jqxNotificationSf').jqxNotification({ template: 'error'});
		        	$("#jqxNotificationSf").html(errorMessage);
		        	$("#jqxNotificationSf").jqxNotification("open");
		        	return false;
		        } else {
		        	$('#containerSf').empty();
		        	$('#jqxNotificationSf').jqxNotification({ template: 'info'});
		        	$("#jqxNotificationSf").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		        	$("#jqxNotificationSf").jqxNotification("open");
		        	return true;
		        }
        	} else {
        		return true;
        	}
		};
		return {
			init: init,
			onResetPercent: onResetPercent,
			onActiveTab: onActiveTab,
			onSubmitFormCreateUpdateSalesForecast: onSubmitFormCreateUpdateSalesForecast,
		};
	}());
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
uiLabelMap.DateNotValid = "${StringUtil.wrapString(uiLabelMap.DateNotValid)}";
uiLabelMap.ColumnDataInSystem = "${StringUtil.wrapString(uiLabelMap.ColumnDataInSystem)}";
uiLabelMap.ColumnDataInImportFile = "${StringUtil.wrapString(uiLabelMap.ColumnDataInImportFile)}";
uiLabelMap.ColumnMapAuto = "${StringUtil.wrapString(uiLabelMap.ColumnMapAuto)}";
uiLabelMap.BSCommonReset = "${StringUtil.wrapString(uiLabelMap.BSCommonReset)}";
uiLabelMap.JoinColumnDataExcel = "${StringUtil.wrapString(uiLabelMap.JoinColumnDataExcel)}";
uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
uiLabelMap.CommonDate = "${StringUtil.wrapString(uiLabelMap.CommonDate)}";
uiLabelMap.BSFeature = "${StringUtil.wrapString(uiLabelMap.BSFeature)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
uiLabelMap.ConfirmCreateFileSalesFCSheetDetail = "${StringUtil.wrapString(uiLabelMap.ConfirmCreateFileSalesFCSheetDetail)}";
</script>
