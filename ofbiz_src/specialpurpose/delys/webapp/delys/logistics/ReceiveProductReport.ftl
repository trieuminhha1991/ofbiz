<div id="ReceiveProductReport" class="widget-box transparent no-bottom-border">   
    <div class="widget-body-inner">
    <div class="widget-main">
    	<div id="table-container">
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%;">
          <thead>
          <tr class="header-row">
            <th rowspan="3" style="text-align: center">${uiLabelMap.ProductProductId}</th>
            <th rowspan="3"  style="text-align: center">${uiLabelMap.ProductProductName}</th>
            <th colspan="10" style="text-align: center">${uiLabelMap.ReceiveType}</th>
            <th rowspan="3" style="text-align: center">${uiLabelMap.CommonTotal}</th>
            <th rowspan="3" style="text-align: center">${uiLabelMap.CommonNote}</th>
          </tr>
          
          <tr class="header-row">
            <th rowspan="2" style="text-align: center">${uiLabelMap.ProductImport}</th>
            <th colspan="2" style="text-align: center">${uiLabelMap.ProductChangeDate}</th>
            <th colspan="3" style="text-align: center">${uiLabelMap.ProductReturn}</th>
            <th colspan="3" style="text-align: center">${uiLabelMap.ProductTransfer}</th>
            <th rowspan="2" style="text-align: center">${uiLabelMap.OtherReceive}</th>
          </tr>
          <tr class="header-row">
            <th style="text-align: center">${uiLabelMap.ChanelMT}</th>
            <th style="text-align: center">${uiLabelMap.ChanelGT}</th>
            <th style="text-align: center">${uiLabelMap.ChanelMT}</th>
            <th style="text-align: center">${uiLabelMap.ChanelGT}</th>
            <th style="text-align: center">${uiLabelMap.Marketing}</th>
            <th style="text-align: center">${uiLabelMap.MedialFacility}</th>
            <th style="text-align: center">${uiLabelMap.InternalFacility}</th>
            <th style="text-align: center">${uiLabelMap.ReceiveFromDistributorFacility}</th>
          </tr>
          </thead>
          <#if listProducts?has_content>
          <#list listProducts as item>
          	<tr>
          		<td>${item.productId}</td>
          		<td></td>
          		<td>${item.quantityPurchase?if_exists?default(0)}</td>
          		<td>${item.quantityChangeDateMT?if_exists?default(0)}</td>
          		<td>${item.quantityChangeDateGT?if_exists?default(0)}</td>
          		<td>${item.quantityReturnMT?if_exists?default(0)}</td>
          		<td>${item.quantityReturnGT?if_exists?default(0)}</td>
          		<td>${item.quantityReturnMarketing?if_exists?default(0)}</td>
          		<td></td>
          		<td></td>
          		<td></td>
          		<td></td>
           		<td></td>
          		<td></td>
          	</tr>
          </#list>
          </#if>
        </table>
        </div>
    </div>
    </div>
  </div>

