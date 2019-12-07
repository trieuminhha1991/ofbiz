<div class="row-fluid">
	<div class="span6">
		<H4><a>${uiLabelMap.ScanFileForOrder}: </a><a id="orderIdLabel"></a><H4>
	</div>
	<div class="span6 row-fluid" style="margin: 8px;">
			<#-- <div id="btnListOrder"></div> 
			<input id="orderId" type ="text" style="margin: 5px;" placeholder="Nhap ma don de tim kiem" /> -->
			<div class="span6">
				<div id="orderHeaderBtn" style="width: 100%;">
					<div style="border-color: transparent;" id="orderHeaderGrid"></div>
				</div>
			</div>
			<div class="span4">
				<input type="hidden" id="orderHeaderId"/>
				<button id="searchImages" class="btn btn-primary btn-mini"><i class="icon-search"></i>${uiLabelMap.Search}</button>
				<button id="uploadImages" class="btn btn-primary btn-mini"><i class="fa fa-upload"></i>${uiLabelMap.Upload}</button>
			</div>
	</div>
</div>
<div class="row-fluid">
</div>
<hr/>
<div class="row-fluid">
	<div id="listFileScan"></div>
</div>

<#assign url = "" />
<#assign orderId = parameters.orderId?if_exists !>
<#if orderId??>
	<#assign orderId = parameters.orderId !>
	<#assign url="jqxGeneralServicer?sname=getListImagesByOrderId&orderId="+orderId !>
</#if>

<div id="jqxFileScanUpload" style="display: none" class="popup-bound">
	<div>
		<span>
			${uiLabelMap.UploadFileScan}
		</span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile" accept="image/*">
		</input>
		<div class="form-action popup-footer">
			<button id="uploadCancelButton" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
			<button id="uploadOkButton" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<#assign dataField="[{ name: 'createByDate', type: 'date', other: 'Timestamp' },
					{ name: 'orderId', type: 'string' },
					{ name: 'orderPathFileId', type: 'string' },
					{ name: 'path', type: 'string' }]"/>

<#assign columnlist = "{ text: '${uiLabelMap.pathFile}', dataField: 'path', filterable: false, pinned: true,
						cellsrenderer: function(row, colum, value) {
							var nameImg = value.split('/');
							var length = nameImg.length - 1;
							return \"<span><a target='_blank' href='\"+value+\"'><i class='fa fa-file-image-o'></i>\" + nameImg[length] + \"</a></span>\";
						}
					},
					{ text: '${uiLabelMap.DACreateDate}', dataField: 'createByDate', width: '15%', cellsformat: 'dd/MM/yyyy', filtertype:'range' }"/>

<#assign dataFieldOrder="[{ name: 'orderId', type: 'string' },
						{ name: 'orderDate', type: 'date', other: 'Timestamp' }]"/>

<#assign columnlistOrder = "{ text: '${uiLabelMap.DAOrderId}', dataField: 'orderId' },
							{ text: '${uiLabelMap.DACreateDate}', dataField: 'orderDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype:'range' }"/>

<@jqGrid id="listFileScan" url=url editable="false"
	filtersimplemode="false" filterable="true" dataField=dataField columnlist=columnlist clearfilteringbutton="false" 
	viewSize="20" showtoolbar="false" selectionmode="singlecell"/>

<@jqGrid id="orderHeaderGrid" url="jqxGeneralServicer?sname=getPurchaseOrder" editable="false"
	filtersimplemode="false" filterable="true" dataField=dataFieldOrder columnlist=columnlistOrder clearfilteringbutton="false" 
	mouseRightMenu="true" viewSize="5" showtoolbar="false" bindresize="true" width="450"/>

<script type="text/javascript" src="/poresources/js/order/viewFileScanOrder.js"></script>
<script>
var messageError = "${uiLabelMap.messageError}";
var NameOfImagesMustBeLessThan50Character = "${uiLabelMap.NameOfImagesMustBeLessThan50Character}";

$(document).ready(function(){
	var orderId = $("#orderHeaderId").val();
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + orderId + '</div>';
	$("#orderHeaderBtn").jqxDropDownButton("setContent", dropDownContent);
	$("#orderIdLabel").text(orderId);
});

$("#orderHeaderGrid").on("rowselect", function (event) {
	var args = event.args;
	var row = args.row;
	$("#orderHeaderId").val(row.orderId);
	var orderId = row.orderId;
	if(orderId == null){
		orderId = uiLabelMapDMSCtmNull;
	}
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + orderId + '</div>';
	$("#orderHeaderBtn").jqxDropDownButton("setContent", dropDownContent);
});
<#if orderId??>
	var orderId = "${orderId}";
	$("#orderHeaderId").val(orderId);
	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + orderId + '</div>';
	$("#orderHeaderBtn").jqxDropDownButton("setContent", dropDownContent);
</#if>
</script>