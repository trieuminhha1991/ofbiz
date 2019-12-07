<div id="info" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.OrderId}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="orderId" name="orderId">${delivery.orderId?if_exists}</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ShippingAddress}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="shippingAddress" name="shippingAddress">
							${customerAddress?if_exists}
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.DatetimeDelivery}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="dateDelivery" name="toParty">
							<#if orderHeader?has_content>
								<#if orderHeader.shipAfterDate?exists && orderHeader.shipBeforeDate?exists>
									<span>${orderHeader.shipAfterDate?datetime?string('dd/MM/yyyy HH:mm')} ${uiLabelMap.LogTo} ${orderHeader.shipBeforeDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
								<#else>
									<span>${orderHeader.estimatedDeliveryDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
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
						<span>${uiLabelMap.ExportFromFacility}</span>
					</div>
					<div class="span9">
						<div class="green-label" id="facilityId" name="facilityId">
						<#if facility?has_content>
							<#if facility.facilityCode?has_content>
								[${facility.facilityCode?if_exists}] ${facility.facilityName?if_exists}
							<#else>
								[${facility.facilityId?if_exists}] ${facility.facilityName?if_exists}
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
						<span class="green-label">${originAddress?if_exists}</span>
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
<script type="text/javascript" src="/logresources/js/delivery/salesDeliveryExportDeliveryProductWithDate.js?v=1.1.1"></script>
<#else>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="jqxGridProduct">
	</div>
</div>
<script type="text/javascript" src="/logresources/js/delivery/salesDeliveryExportDeliveryProduct.js?v=1.1.1"></script>
</#if>
</div>