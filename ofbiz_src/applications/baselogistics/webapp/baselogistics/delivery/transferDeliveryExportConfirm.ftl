<script>
	<#assign activeTab = "tab_general"/>
</script>
<div id="confirm" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.TransferId}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="transferIdCf" name="transferIdCf">${delivery.transferId?if_exists}</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span>${uiLabelMap.FacilityFrom}</span>
					</div>
					<div class="span9">
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

<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
		<#if requireDate?has_content && requireDate == 'Y'>
			<div class="row-fluid margin-top5">
				<div class="span12">
					<div class="tabbable">
						<ul class="nav nav-tabs" id="recent-tab">
							<li class="<#if activeTab?exists && activeTab == "tab_general">active</#if>" id="li_general">
								<a data-toggle="tab" href="#tab_general">
									<span>${uiLabelMap.BLGeneral?if_exists}</span>
								</a>
							</li>
							<li class="<#if activeTab?exists && activeTab == "tab_detail">active</#if>" id="li_detail">
								<a data-toggle="tab" href="#tab_detail">
									<span>${uiLabelMap.BLDetail?if_exists} Date</span>
								</a>
							</li>
						</ul>
					</div>
				</div>
			</div>
		</#if>
		<div class="tab-content overflow-visible" style="padding:8px 0; border: none !important;">
			<div class="tab-pane<#if activeTab?exists && activeTab == "tab_general"> active</#if>" id="tab_general">
				<table id="tableProduct" width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
					<thead>
						<tr valign="bottom" style="height: 40px">
							<th width="3%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.ProductId}</b></span></th>
							<th width="25%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.Note}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.Unit}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.QOH}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.RequiredNumberSum}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.ActualDeliveryQuantitySum}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.UnitPrice}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.BPOTotal} </br></span></th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>
			<div class="tab-pane<#if activeTab?exists && activeTab == "tab_detail"> active</#if>" id="tab_detail">
				<table id="tableProductDetail" width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
					<thead>
						<tr valign="bottom" style="height: 40px">
							<th width="3%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.ProductId}</b></span></th>
							<th width="25%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.Quantity}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.ExpireDate}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.ManufactureDate}</b></span></th>
							<th width="10%" class="align-center"><span><b>${uiLabelMap.Batch}</b></span></th>
						</tr>
					</thead>
					<tbody>
						
					</tbody>
				</table>
			</div>
		</div>
	</div>
</div>
<script>
	$('.nav.nav-tabs li').on('click', function(){
    	// clear parameter
    	var thisHref = location.href;
    	var queryParam = thisHref.split("?");
    	var newHref = "";
    	if (queryParam != null && queryParam != undefined) {
    		newHref = queryParam[0] + "?";
    	}
    	var isAdded = false;
    	if (queryParam.length > 1) {
    		var varsParam = queryParam[1].split("&");
		    for (var i = 0; i < varsParam.length; i++) {
		        var pairParam = varsParam[i].split("=");
		        if(pairParam[0] != 'activeTab'){
		        	if (isAdded) newHref += "&";
		        	newHref += varsParam[i];
		        	isAdded = true;
		        }
		    }
    	}
    	var tabObj = $(this).find("a[data-toggle=tab]");
    	if (tabObj != null && tabObj != undefined) {
    		var tabHref = tabObj.attr("href");
    		if (tabHref.indexOf("#") == 0) {
    			var tabId = tabHref.substring(1);
    			window.history.pushState({}, "", newHref + '&activeTab=' + tabId);
    		}
    	}
    });
</script>
