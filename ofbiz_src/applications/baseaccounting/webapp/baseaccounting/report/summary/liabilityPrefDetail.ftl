<#include "script/liabilityPrefData.ftl">
<style>
	.display-info{
		color: #037c07;
    	font-weight: bold;
		vertical-align: bottom;
    	line-height: 20px;
	}
	.div-inline-block{
		display: inline-block;
		word-wrap: break-word;
	}
	.display-number{
		color: red; 
		font-weight: bold; 
		position: relative; 
		margin: 6px; 
		text-align: right; 
		overflow: hidden;
	}
</style>

<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc">
					<div class="row-fluid">
						<div class="span10">
							<ul class="nav nav-tabs" id="recent-tab">
								<li class="active">
									<a data-toggle="tab" id="invoice-overview" aria-expanded="true">
										${uiLabelMap.BACCLiabilityPref}
									</a>
								</li>
							</ul>
						</div>
						<div class="span2" style="height:34px; text-align:right; font-size: 20px;">
							<a href="javascript:document.LiabilityPrefPDF.submit()" data-rel="tooltip" title="" data-placement="bottom" class="button-action" data-original-title="PDF"><i class="fa fa-file-pdf-o"></i></a>
						  	<form name="LiabilityPrefPDF" method="post" action="<@ofbizUrl>liabilityPref.pdf</@ofbizUrl>" style="position:absolute;">
						        <input type="hidden" name="organizationPartyId" value="${parameters.organizationPartyId}">
						        <input type="hidden" name="partyId" value="${parameters.partyId}">
						        <input type="hidden" name="prefDate" value="${parameters.prefDate}">
						  	</form>
						</div>
					</div>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div id="container" style="width: 100%;"></div>
					<div class="tab-content">
						<div class="row-fluid">
							<div class="span12">
								<div class="span6">
									<div class='row-fluid'>
										<div class='div-inline-block'>
											<label>${uiLabelMap.BACCAParty}:</label>
										</div>
										<div class="div-inline-block">
											<span id="partyIdFrom" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class='div-inline-block'>
											<label>${uiLabelMap.BACCAAddress}:</label>
										</div>
										<div class="div-inline-block">
											<span id="addressFrom" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class='div-inline-block'>
											<label>${uiLabelMap.BACCAPhoneNumber}:</label>
										</div>
										<div class="div-inline-block">
											<span id="phoneNumberFrom" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class='div-inline-block'>
											<label>${uiLabelMap.BACCARep}:</label>
										</div>
										<div class="div-inline-block">
											<span id="repFrom" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCAPosition}:</label>
										</div>
										<div class="div-inline-block">
											<span id="posFrom" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCAFax}:</label>
										</div>
										<div class="div-inline-block">
											<span id="faxFrom" class="display-info"></span>
								   		</div>
									</div>
								</div><!--span6-->
								<div class="span6">
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBParty}:</label>
										</div>
										<div class="div-inline-block">
											<span id="partyIdTo" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBAddress}:</label>
										</div>
										<div class="div-inline-block">
											<span id="addressTo" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBPhoneNumber}:</label>
										</div>
										<div class="div-inline-block">
											<span id="phoneNumberTo" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBRep}:</label>
										</div>
										<div class="div-inline-block">
											<span id="repTo" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBPosition}:</label>
										</div>
										<div class="div-inline-block">
											<span id="posTo" class="display-info"></span>
								   		</div>
									</div>
									<div class='row-fluid'>
										<div class="div-inline-block">
											<label>${uiLabelMap.BACCBFax}:</label>
										</div>
										<div class="div-inline-block">
											<span id="faxTo" class="display-info"></span>
								   		</div>
									</div>
								</div><!--span6-->
							</div><!--.span12-->
						</div><!--.row-fluid-->
						<div>
							<b>1. Công nợ đầu kỳ:</b> <span class="display-number">${openingBalStr}</span>
						</div>
						<div class="margin-top10">
							<b>2. Các khoản phát sinh trong kỳ</b>
							<div class="margin-bottom10"></div>
							<#include "accruedLiabilityPref.ftl">
						</div>
						<div class="margin-top10">
							<b>3. Số tiền bên A đã thanh toán:</b> <span class="display-number">${paidAmountStr}</span>
						</div>
						<div class="margin-top10">
							<b>4. Kết luận:</b><span>Tính đến hết ngày ${parameters.prefDate?if_exists} bên A phải thanh toán cho bên B số tiền là: <span class="display-number">${notPaidAmountStr}</span></span>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script>
	if(typeof(detail) == "undefined"){
		$.jqx.theme = 'olbius';
		
		var detail = function(){
			var initDetail = function(){
				$('#partyIdTo').text('${StringUtil.wrapString(partyNameTo?if_exists)}');
				$('#addressTo').text('${StringUtil.wrapString(partyAddressTo?if_exists)}');
				$('#phoneNumberTo').text('${StringUtil.wrapString(partyTelePhoneTo?if_exists)}');
				$('#faxTo').text('${StringUtil.wrapString(faxNumber?if_exists)}');
				$('#repTo').text('${StringUtil.wrapString(repNameTo?if_exists)}');
				<#if emplPosTo?exists>
					$('#posTo').text('${StringUtil.wrapString(emplPosTo.description?if_exists)}');
				</#if>
				$('#partyIdFrom').text('${StringUtil.wrapString(partyNameFrom?if_exists)}');
				$('#addressFrom').text('${StringUtil.wrapString(partyAddressFrom?if_exists)}');
				$('#phoneNumberFrom').text('${StringUtil.wrapString(partyTelePhoneFrom?if_exists)}');
				$('#faxFrom').text('${StringUtil.wrapString(faxNumberFrom?if_exists)}');
				$('#repFrom').text('${StringUtil.wrapString(repNameFrom?if_exists)}');
				<#if emplPosFrom?exists>
					$('#posFrom').text('${StringUtil.wrapString(emplPosFrom.description?if_exists)}');
				</#if>
			}
			
			return {
				initDetail: initDetail,
			}
		}();
	}
	
	$( document ).ready(function(){
		detail.initDetail();
	});
</script>
