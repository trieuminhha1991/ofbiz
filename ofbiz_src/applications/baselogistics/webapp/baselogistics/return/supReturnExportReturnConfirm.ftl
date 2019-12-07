<script>
	<#assign activeTab = "tab_general"/>
</script>
<div>
	<div class="row-fluid">
		<div class="span6">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.GeneralInfo}
			</h4>
			<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
				<tr>
					<td align="right" valign="top" width="25%">
						<span><b>${uiLabelMap.ReturnFrom}</b> </span>
					</td>
					<td valign="top" width="70%">
						<span id="fromPartyDT">${fromPartyName?if_exists}</span>
					</td>
				</tr>
				<tr>
					<td align="right" valign="top" width="25%">
						<span><b>${uiLabelMap.ReturnTo}</b> </span>
					</td>
					<td valign="top" width="70%">
						<span id="toPartyDT">${toPartyName?if_exists}</span>
					</td>
				</tr>
			</table>
		</div><!--.span6-->
		<div class="span6">
			<h4 class="smaller green" style="display:inline-block">
				<!-- title -->
			</h4>
			<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable margin-top15">
				<tr>
					<td align="right" valign="top" width="30%">
						<div><b>${uiLabelMap.CreatedDate}</b></div>
					</td>
					<td valign="top" width="70%">
						<span id="entryDateDT">
						${returnHeader.entryDate?datetime?string('dd/MM/yyyy HH:mm')}
						</span>	
					</td>
				</tr>
				<tr>
					<td align="right" valign="top" width="25%">
						<div><b>${uiLabelMap.ExportFromFacility}</b></div>
					</td>
					<td valign="top" width="70%">
						<span id="facilityDT">
							<#if facility?has_content>
								<#if facility.facilityCode?has_content>
									[${facility.facilityCode?if_exists}] ${facility.facilityName?if_exists}
								<#else>
									[${facility.facilityId?if_exists}] ${facility.facilityName?if_exists}
								</#if>
							</#if>
						</span>	
					</td>
				</tr>
			</table>
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
							<th width="8%" class="align-center"><span><b>${uiLabelMap.RequiredNumber}</b></span></th>
							<th width="8%" class="align-center"><span><b>${uiLabelMap.ActualExportedQuantity}</b></span></th>
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