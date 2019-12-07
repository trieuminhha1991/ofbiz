<div id="info" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.TransferId}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="transferId" name="transferId">${delivery.transferId?if_exists}</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.FacilityFrom}</span>
					</div>
					<div class="span8">
						<div class="green-label" id="facilityId" name="facilityId">
						<#if originFacility?has_content>
							<#if originFacility.facilityCode?has_content>
								[${originFacility.facilityCode?if_exists}] ${originFacility.facilityName?if_exists}
							<#else>
								[${originFacility.facilityId?if_exists}] ${originFacility.facilityName?if_exists}
							</#if>
						</#if>
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.Address}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="shippingAddress" name="shippingAddress">
							${originAddress?if_exists}
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.DatetimeDelivery}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="dateDelivery" name="toParty">
							<#if transferHeader?has_content>
								<#if transferHeader.shipAfterDate?exists && transferHeader.shipBeforeDate?exists>
									<span>${transferHeader.shipAfterDate?datetime?string('dd/MM/yyyy HH:mm')} ${uiLabelMap.LogTo} ${transferHeader.shipBeforeDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
								<#else>
									<span>${transferHeader.transferDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
								</#if>
							</#if>
						</div>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='row-fluid'>
						<div class='span3 align-right'>
							<span>${uiLabelMap.CreatedDate}</span>
						</div>
						<div class="span9">
							<div class="green-label" style="text-align: left;" id="entryDate" name="entryDate">${delivery.createDate?datetime?string('dd/MM/yyyy HH:mm')}</div>
				   		</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span>${uiLabelMap.FacilityTo}</span>
					</div>
					<div class="span9">
						<div class="green-label" id="facilityId" name="facilityId">
						<#if destFacility?has_content>
							<#if destFacility.facilityCode?has_content>
								[${destFacility.facilityCode?if_exists}] ${destFacility.facilityName?if_exists}
							<#else>
								[${destFacility.facilityId?if_exists}] ${destFacility.facilityName?if_exists}
							</#if>
						</#if>
						</div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span3 align-right">
						<span>${StringUtil.wrapString(uiLabelMap.Address)}</span>
					</div>
					<div class="span9">
						<span class="green-label">${destAddress?if_exists}</span>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div>
<#if requireDate?has_content && requireDate == 'Y'>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="splitterProduct" style="border-top: none; border-left: none; border-right: none;">
		<div id="leftPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
			<div id="jqxGridProduct"></div>
		</div>
		<div id="rightPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
           <div id="jqxGridProductInfo">
           </div>
    	</div>
	</div>
</div>
<script type="text/javascript" src="/logresources/js/delivery/transferDeliveryExportProductWithDate.js?v=1.1.1"></script>
<#else>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="jqxGridProduct">
	</div>
</div>
<script type="text/javascript" src="/logresources/js/delivery/transferDeliveryExportProduct.js?v=1.1.1"></script>
</#if>
</div>