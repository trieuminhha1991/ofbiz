<div id="reqInfo" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ReturnFrom}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;">
							${returnHeader.fromGroupName?if_exists}
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ReturnTo}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;">
							${returnHeader.toGroupName?if_exists}
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
							<div class="green-label" id="contactMechId" name="contactMechId">
								<#if returnHeader.entryDate?has_content>
									${returnHeader.entryDate?datetime?string('dd/MM/yyyy HH:mm')}
								</#if>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<#if originFacility?has_content>
							<div class='span3 align-right'>
								<span>${uiLabelMap.ReceiveToFacility}</span>
							</div>
							<div class="span9">
								<div class="green-label" id="facilityId" name="facilityId">
								<#if originFacility.facilityCode?has_content>
									[${originFacility.facilityCode?if_exists}] ${originFacility.facilityName?if_exists}
								<#else>
									[${originFacility.facilityId?if_exists}] ${originFacility.facilityName?if_exists}
								</#if>
								</div>
					   		</div>
				   		<#else>
				   			<div class='span3 align-right'>
								<span class="asterisk">${uiLabelMap.ReceiveToFacility}</span>
							</div>
							<div class="span9">
					   			<div class="green-label" id="destFacility" name="destFacility">
									<div id="jqxgridFacility"></div>
								</div>
							</div>
				   		</#if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/logresources/js/return/custReturnReceiveReturnInfo.js?v=1.1.1"></script>
<div>
<#if requireDate?exists && requireDate?has_content && requireDate == 'Y'>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<div id="product">
	<div id="splitterProduct"  style="border-top: none; border-left: none; border-right: none;">
		<div id="leftPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
			<div id="jqxGridProduct"></div>
		</div>
		<div id="rightPanel" class="splitter-panel jqx-hideborder jqx-hidescrollbars">
           <div id="jqxGridProductInfo">
           </div>
    	</div>
	</div>
</div>
<script type="text/javascript" src="/logresources/js/return/custReturnReceiveReturnProductWithDate.js?v=1.1.1"></script>
<#else>
<div id="product">
	<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
	<div id="jqxGridProduct">
	</div>
</div>
<script type="text/javascript" src="/logresources/js/return/custReturnReceiveReturnProduct.js?v=1.1.1"></script>
</#if>
</div>