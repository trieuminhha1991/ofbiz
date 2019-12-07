<@jqGridMinimumLib />
<#include "script/importInventoryFromExcelScript.ftl"/>
<h4 class="row header smaller lighter blue" style="margin-left: 10px !important;font-weight:500;line-height:20px;font-size:18px;">
	${uiLabelMap.SelectExcelFile}
	<a style="float:right;font-size:16px; cursor: pointer;" id="excel" href="/logresources/files/InventoryTemplate.xls" download="InventoryTemplate.xls" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExcelTemplate}" data-placement="bottom" data-original-title="${uiLabelMap.ExcelTemplate}"><i class="fa fa-file-excel-o"></i></a>
</h4>
<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>
<div class="rowfluid" id="formInfo">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="span12">
			<div class='row-fluid'>	
				<div class="span4">
					<input type="file" id="id-input-file-1" name="document" accept=".xlsx, .xls" />
				</div>
				<div class="span4 align-left">
					<button id="btnUpload" type="button" class="btn btn-primary form-action-button disabled fa fa-refresh">
						${uiLabelMap.Load}
					</button>
				</div>
			</div>
		</div>
	</div>
	<div class="row-fluid">
		<div class="span12">
			<h4 class="row header smaller lighter blue" style="margin-left: 10px !important;font-weight:500;line-height:20px;font-size:18px;">
			    ${uiLabelMap.ListProduct}
			</h4>
			<div id = "jqxgridInventoryItemFromExcel"></div>
		</div>
	</div>
	<div class="row-fluid margin-top10"> 
		<div class="span12 align-right">
			<button id="btnImport" type="button" class="btn btn-primary form-action-button fa fa-download open-sans">
				${uiLabelMap.ReceiveProduct}
			</button>
		</div>
	</div>
</div>
