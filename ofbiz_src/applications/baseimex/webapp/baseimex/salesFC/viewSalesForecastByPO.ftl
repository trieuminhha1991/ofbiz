<style type="text/css">
	#tab-content2{
		border: none 0px red; 
		overflow-x: scroll; 
		overflow-y:hidden;
		height: 20px;
		position:fixed;
		bottom:0;
		right:20px;
		z-index:500;
		opacity: 0.5;
	}
	#tab-content2:hover{
		opacity: 1;
	}
	#tab-content{overflow:hidden}
	#tab-pane2{ height: 20px;}
	
	#sale-forecast input[type="text"] {
		padding: 0 2px;
	}
	#sale-forecast td.sf-month, #sale-forecast.table td {
		border-bottom:0;
	}
	#sale-forecast > tbody > tr > td:first-child {
		text-align:left !important;
		font-weight: normal;
	}
	#sale-forecast {
		font-family: arial;
	}
	#sale-forecast td {
		padding: 2px 5px !important;
	}
	#sale-forecast tr {
		background: #FFF !important;
	}
	#sale-forecast td.sf-month, #sale-forecast .sf-adjusted td {
    	background: transparent !important;
    }
    #sale-forecast td.sf-row-title {
    	text-align:left !important;
    }
    #sale-forecast tr.row-group {
    	background: #D9D9D9 !important;
    }
    #sale-forecast tr.row-single {
    	background: #EEEEEE !important;
    }
    #sale-forecast.table-bordered th, #sale-forecast.table-bordered td {
	    border-left: 1px solid #393939;
	    border-top: 1px solid #393939;
	}
	#sale-forecast tr:last-child td {
		border-bottom: 1px solid #393939;
	}
</style>
<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>${uiLabelMap.DASalesForecast}: 
	            	<#if customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, false)!/>
                		${customTimeParent.periodName?if_exists}
                	</#if>
                </h4>
                <#-- <div class="widget-toolbar no-border">
                    <ul class="nav nav-tabs" id="myTab2">
                        <#list listSalesForecastAndItems as salesForecastPartyItem>
							<li <#if salesForecastPartyItem_index == 0> class="active"</#if> style="margin-top:0; margin-bottom:-1px; padding-top:0 !important; padding-bottom:0 !important">
								<a data-toggle="tab" href="#${salesForecastPartyItem_index}-tab" onClick="javascript:pageCommon.onActiveTab('${salesForecastPartyItem_index}')">${salesForecastPartyItem.internalPartyIds}</a>
							</li>
						</#list>
                    </ul>
                </div> -->
            </div>
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right">
                	<div id="tab-content2">
					    <div id="tab-pane2">
					    </div>
					</div>
                	<div id="tab-content" class="tab-content padding-4">
                		<#if listSalesForecastAndItems?exists && (listSalesForecastAndItems?size > 0)>
                    		<#list listSalesForecastAndItems as salesForecastPartyItem>
	                    		<#if salesForecastPartyItem_index == 0>
	                    			<input id="tabActiveDefault" type="hidden" value="${salesForecastPartyItem_index}"/>
	                    		</#if>
	                    		<input id="tabActiveInput_${salesForecastPartyItem_index}" type="hidden" value="0"/>
								<div id="${salesForecastPartyItem_index}-tab" class="tab-pane<#if salesForecastPartyItem_index == 0> active</#if>">
								    <form id="formCreateUpdateSalesForecast_${salesForecastPartyItem_index}" name="formCreateUpdateSalesForecast_${salesForecastPartyItem_index}" method="POST" action="<@ofbizUrl>createUpdateForecastAdvance</@ofbizUrl>">    
								        <input name="salesForecastId" type="hidden" value="${salesForecastId?if_exists}"/>
								        <input name="parentSalesForecastId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastId?if_exists}"/>
								        <input name="customTimePeriodId_${salesForecastPartyItem_index}" type="hidden" value="${customTimePeriodId?if_exists}"/>
								        <input name="internalPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.internalPartyIds?if_exists}"/>
								        <input name="organizationPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.organizationPartyId?if_exists}"/>
								        <input name="currencyUomId_${salesForecastPartyItem_index}" type="hidden" value="${currencyUomId?if_exists}"/>
								        <table id="sale-forecast" class="table table-striped table-bordered table-hover">
								            <thead>
								                <tr class="sf-product">
								                	<td class="sf-row-title" rowspan="2">${uiLabelMap.DAId}</td>
								                	<td class="sf-row-title" rowspan="2" width="150px">${uiLabelMap.DAProduct}</td>
								                	<td class="sf-row-title" rowspan="2">${uiLabelMap.DmsProductTaste}</td>
								                	<td class="sf-row-title" rowspan="2">${uiLabelMap.DACapacity}</td>
								                	<td class="sf-row-title" rowspan="2">${uiLabelMap.unitPrice}</td>
								                	<#list listPeriodThisAndChildren as periodItem>
									                    <td class="align-center" colspan="2">
									                    	${periodItem.periodName?default(periodItem.customTimePeriodId)}
								                    	</td>
								                	</#list>
								                </tr>
								                <tr class="sf-product">
								                	<#list listPeriodThisAndChildren as periodItem>
								                	<td class="align-center">${uiLabelMap.DAQuantity}<br/>(${uiLabelMap.DACrate})</td>
								                	<td class="align-center">${uiLabelMap.DAItemTotal}</td>
								                	</#list>
								                </tr>
								            </thead>
								            <tbody>
								            <#if salesForecastPartyItem.forecastAndItems?exists>
								            <#list salesForecastPartyItem.forecastAndItems as forecastAndItem>
								            	<#list forecastAndItem.entrySet() as entryRow>
								            		<#assign productId = entryRow.key/>
								            		<#assign rowData = entryRow.value/>
								            		<#assign product = rowData.product/>
								            		<#assign colData = rowData.colData/>
							                		<tr class="sf-adjusted<#if product.isVirtual == "Y"> row-group<#elseif product.isVariant == "N"> row-single</#if>">
									                    <td class="sf-row-title">
									                    	${productId?if_exists}
									                    </td>
									                    <td class="sf-row-title" width="150px">
									                    	<#if product.internalName?exists>
									                    	<#assign arrayString = product.internalName?word_list/>
									                    	<span title="${product.internalName}">
									                    		<#if arrayString?size &gt; 3>
									                    			<#assign count = 0/>
									                    			<#list arrayString as item>
									                    				${item}&nbsp;<#if count &gt; 3><#break/><#else><#assign count = count + 1/></#if>
									                    			</#list>
									                    		<#else>
									                    			${product.internalName}
									                    		</#if>
									                    	</span>
									                    	</#if>
									                    </td>
									                    <td class="sf-row-title">
									                    	${product.features?if_exists}
									                    </td>
									                    <td>#</td>
									                    <td>#</td>
									                    <#assign colNumber = 0>
									                    <#list colData.entrySet() as entryCol>
									            			<#assign entryContent = entryCol.value/>
									            			<#assign salesForecastId = entryContent.salesForecastId?default("")/>
									            			<#assign salesForecastDetailId = entryContent.salesForecastDetailId?default("")/>
									            			<#assign quantity = entryContent.quantity?default("")/>
									                    	<#assign customTimePeriod = entryContent.customTimePeriod?default([])/>
									            			<td>
					                                			<#if product.isVirtual == "Y">
					                                				<#if quantity?exists && quantity?has_content>
											        					<span>${quantity?string.number}</span>
											        				<#else>
											        					<span></span>
											        				</#if>
					                                			<#else>
					                                				<input name="customTimePeriodId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="hidden" value="${customTimePeriod.customTimePeriodId?if_exists}"/>
					                                				<input id="salesForecastId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="salesForecastId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="hidden" value="${salesForecastId?if_exists}"/>
										                    		<input id="salesForecastDetailId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="salesForecastDetailId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="hidden" value="${salesForecastDetailId?if_exists}"/>
										                    		<input id="productId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="productId_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="hidden" value="${productId?if_exists}"/>
										                    		<input id="quantity_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="quantity_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="hidden" value="${quantity?if_exists}"/>
											                    	<#if quantity?exists && quantity?has_content>
											        					<input id="forecastInput_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="text" value="${quantity}"/>
											        				<#else>
											        					<input id="forecastInput_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${colNumber}_${forecastAndItem_index}" type="text"/>
											        				</#if>
					                                			</#if>
									                    	</td>
									                    	<td>_</td>
									                    	<#assign colNumber = colNumber + 1>
									                    </#list>
									                </tr>
								            	</#list>
								         	</#list>
								         	</#if>
								            </tbody>
								        </table>
									</form>
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
					<label class="control-label" for="percentNumber">${uiLabelMap.DAPercent}</label>
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
			Close
		</button>
		<div class="pagination pull-right no-margin">
			<button class="btn btn-small btn-primary pull-left" onClick="javascript:pageCommon.onResetPercent();">
				<i class="icon-ok"></i>
				Ok
			</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	var listForecastInput = [];
	var body = $("html, body");
	$(function(){
		pageCommon.init();
	});
	
	var pageCommon = (function(){
		var init = function(){
			initScroller();
			initEvent();
		};
		var initScroller = function(){
			var width = $("#tab-content").width();
			$("#tab-pane2").css("width", $("#sale-forecast").width() + "px");
			$("#tab-content2").css("width", "40%");
			$("#tab-content2").scroll(function(){
	        	$("#tab-content").scrollLeft($("#tab-content2").scrollLeft());
		    });
		    $("#tab-content").scroll(function(){
		        $("#tab-content2").scrollLeft($("#tab-content").scrollLeft());
		    });
		};
		var initEvent = function(){
			$(window).resize(function(){
				initScroller();
			});
			$('#modal-table').on('show.bs.modal', function (e) {
			  	setTimeout(function() {$("#percentNumber").focus()}, 1000);
			});
			$('#percentNumber').live('keyup',function(e){
		     	var p = e.which;
		     	if(p==13){
		         	onResetPercent();
		     	}
		 	});
		 	$('input[id^="forecastInput_"]').live('keyup',function(e){
		     	var p = e.which;
		     	if(p==13){
		     		var element = $("#" + e.currentTarget.id);
		     		if (element != null) {
		     			updateThisInputs(element);
		         		focusForecastInputBelow(element);
		     		}
		     	}
		 	});
		 	$('input[id^="forecastInput_"]').blur(function(){
		 		updateThisInputs($(this));
		 	});
		};
		var focusForecastInputBelow = function(thisElement){
			var idElementProcess = thisElement.attr("id");
			var idElementProcessSplit = idElementProcess.split("_");
			var idElementProcessParseInt = parseInt(idElementProcessSplit[3]);
			idElementProcessSplit[3] = idElementProcessParseInt + 1;
			var idElementNext = idElementProcessSplit.join("_");
			//var loop = true;
			if ($("#" + idElementNext).length > 0) {
				$("#" + idElementNext).select();
				//$("#" + idElementNext).focus();
			}
			if ($("#" + idElementNext).length <= 0) {
				idElementProcessParseInt = parseInt(idElementProcessSplit[3]);
				idElementProcessSplit[3] = idElementProcessParseInt + 1;
				idElementNext = idElementProcessSplit.join("_");
				if ($("#" + idElementNext).length > 0) {
					$("#" + idElementNext).select();
					//$("#" + idElementNext).focus();
				}
			}
			if ($("#" + idElementNext).length <= 0) {
				idElementProcessParseInt = parseInt(idElementProcessSplit[2]);
				idElementProcessSplit[3] = 0;
				idElementProcessSplit[2] = idElementProcessParseInt + 1;
				idElementNext = idElementProcessSplit.join("_");
				if ($("#" + idElementNext).length > 0) {
					$("#" + idElementNext).select();
					//$("#" + idElementNext).focus();
				}
			}
			//var currentScroll = $(document).scrollTop();
	        //body.animate({scrollTop: currentScroll + 100}, '500', 'swing');
			//var inputs = $(this).closest('form').find(':input');
	  		//listForecastInput.eq(listForecastInput.index(thisElement)+1).focus();
		};
		var updateThisInputs = function(thisElement){
			var valueInputChange = thisElement.val();
	 		var idInputChange = thisElement.attr("id");
	 		var subIdInputChange = idInputChange.substring(13, idInputChange.length);
			var ayoForecastInputProcess = parseFloat($("#ayoForecastInput" + subIdInputChange).val());
			var valueInputChangeFloat = parseFloat(valueInputChange);
			if (isNaN(valueInputChangeFloat)) {
				valueInputChangeFloat = 0;
			}
			if (!isNaN(valueInputChangeFloat) && !isNaN(ayoForecastInputProcess) && ayoForecastInputProcess > 0) {
				var percentInputProcess = (valueInputChangeFloat / ayoForecastInputProcess) * 100;
				percentInputProcess = Number((percentInputProcess).toFixed(2));
				$("#percentInput" + subIdInputChange).val(percentInputProcess);
				$("#percentDiv" + subIdInputChange).html(FormatNumberBy3(percentInputProcess, ",", ".") + "%");
			}
		};
		var onResetPercent = function(){
			$("#modal-table").modal("hide");
			bootbox.confirm("${uiLabelMap.DAThisChangeWillApplyForSalesForesastTableInThisScreen}", function(result){
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
			$("#formCreateUpdateSalesForecast_" + tabActiveCurrent).submit();
		};
		return {
			init: init,
			onResetPercent: onResetPercent,
			onActiveTab: onActiveTab,
			onSubmitFormCreateUpdateSalesForecast: onSubmitFormCreateUpdateSalesForecast,
		};
	}());
</script>