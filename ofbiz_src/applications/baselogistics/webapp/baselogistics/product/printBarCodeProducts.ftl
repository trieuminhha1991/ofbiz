<#include 'script/printBarCodeProductsScript.ftl'/>
<form class="form-horizontal form-window-content-custom" id="barCodeInfo" name="barCodeInfo" method="post" action="">
	<div class="row-fluid margin-top20" style="margin-bottom: 0px !important">
		<div class='span12'>
			<div class='row-fluid'>
				<div class='span2'>
					<div class='row-fluid'>
						<div class='span10'>
							<div style='font-weight: bold'>${uiLabelMap.DisplayInfo}</div>
						</div>
						<div class='span2'>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span10' style="text-align: right">
							<span class="title">${uiLabelMap.ProductId}</span>
						</div>
						<div class='span2'>
							<div id='includeProductId' title='${uiLabelMap.Default}' style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span10' style="text-align: right">
							<span class="title">${uiLabelMap.ProductName}</span>
						</div>
						<div class='span2'>
							<div id='includeProductName' style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
					</div>
				</div>
				<div class='span2 margin-left0'>
					<div class='row-fluid'>
						<div class='span10'>
						</div>
						<div class='span2'>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span10' style="text-align: right;">
							<span class="title">${uiLabelMap.UnitPrice}</span>
						</div>
						<div class='span2'>
							<div id='includeUnitPrice' style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span10' style="text-align: right;">
							<span class="title">${uiLabelMap.CompanyName}</span>
						</div>
						<div class='span2'>
							<div id='includeCompanyName' style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
					</div>
				</div>
				<div class='span5'>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div style='font-weight: bold'>${uiLabelMap.PageSizeCustom}</div>
						</div>
						<div class='span7'>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div>${uiLabelMap.PageSizeStandard}</div>
						</div>
						<div class='span7'>
							<div id="pageSizeId" class="green-label"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div>${uiLabelMap.PDFPageWidth} (cm)</div>
						</div>
						<div class='span7'>
							<div id="pageWidth" class="green-label"></div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div>${uiLabelMap.PDFPageHeight} (cm)</div>
						</div>
						<div class='span7'>
							<div id="pageHeight" class="green-label"></div>
						</div>
					</div>
				</div>
				<div class='span3'>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div style='font-weight: bold'>${uiLabelMap.TemplatePrint}</div>
						</div>
						<div class='span7'>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div>${uiLabelMap.Page105x22}</div>
						</div>
						<div class='span2'>
							<div id="page105x22" style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
						<div class='span5'>
							<div>
								<a href="/logresources/images/KhoGiay105x22.jpg" target='_blank'>	
									<img src="/logresources/images/KhoGiay105x22Small.jpg" style="margin-top: -55px; width: 96px;">
								</a>
							</div>
						</div>
					</div>
					<div class='row-fluid'>
						<div class='span5 align-right'>
							<div>${uiLabelMap.Page70x22}</div>
						</div>
						<div class='span2'>
							<div id="page70x22" style='float: left; margin-left: -2px !important; margin-top: 2px !important;'></div>
						</div>
						<div class='span5'>
							<div>
								<a href="/logresources/images/KhoGiay70x22.jpg" target='_blank'>	
									<img src="/logresources/images/KhoGiay70x22Small.jpg" style="margin-top: -10px; width: 100%">
								</a>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid'>
				<div class="row-fluid margin-top10">
					<div id = "gridProductToPrint"></div>
				</div>
			</div>
		</div>
	</div>
</form>