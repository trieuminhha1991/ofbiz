var currentMarketingCampaignId = 0;
function showCampaign(id, row) {
	var formContainer = $("#approveRequest");
	var data = $("#listRequest").jqxGrid('getrowdata', row);
	renderForm(data);
	formContainer.modal("show");
	$.ajax({
		url: "getRequestDetail",
		data: {id : id},
		type: "POST",
		success: function(res){
			console.log(res);
			if(res.data){
				var products = res.data.products;
				var costs = res.data.costs;
				renderProduct(products);
				renderCostList(costs);
			}
		}
	});
	currentMarketingCampaignId = id;
	setId(id);
}
function renderForm(data){
	var fromDate = Utils.formatDateDMY(data.fromDate);
	var thruDate = Utils.formatDateDMY(data.thruDate);
	$("#campaignName").val(data.campaignName);
	$("#campaignSummary").val(data.campaignSummary);
	$("#marketing").val(data.campaignName);
	$("#campaignName").val(data.campaignName);
	$("#place").val(data.marketingPlace);
	$("#budgetedCost").val(data.budgetedCost);
	$("#estimatedCost").val(data.estimatedCost);
	$("#fromDate").val(fromDate);
	$("#thruDate").val(thruDate);
	$("#people").val(data.people);
}
function renderProduct(data){
	var container = $("#list-product");
	var str = "";
	for(var x in data){
		str += "<li>" + data[x].productName + "</li>";
	}
	container.html(str);
}
function renderCostList(data){
	var container = $("#cost-form");
	var str = "";
	var currentCost = "";
	for(var x in data){
		var name = data[x].name;
		if(!currentCost){
			currentCost = name;
			str += getCostCategory(name);
			str += getRow(data[x]);
			// str += "</div>";
		}else if(currentCost && name != currentCost){
			str += "</div></div></div></div>";	
			str += getCostCategory(name);
			str += getRow(data[x]);
			currentCost = name;
		}else if(currentCost && name == currentCost){
			str += getRow(data[x]);
		}
	}
	str += "</div></div></div></div>";
	//console.log(str);
	container.html(str);
}
function getCostCategory(name){
	var str = "";
	str += "<div class='row-al paddingtop-10 cost-row'>";
	str += "<div class='col-al4 aligncenter'><div class='title-inner'>" + name + "</div></div>" 
		+ "<div class='col-al8'><div class='row-al'><div class='col-al12'>";
	return str;
}
function getRow(data){
	var total = 0;
	if(data.quantity && data.unitPrice){
		total = parseInt(data.quantity) * (data.unitPrice);
	}
	var str = "<div class='row-al'>"
		+"<div class='col-al4 borderleft'><input class='fullwidth' disabled type='text' name='description' value='"+ data.description +"'/></div>"
		+ "<div class='col-al2 borderleft'><input class='fullwidth' type='number' value='"+ data.quantity +"' name='quantity' disabled/></div>"
		+ "<div class='col-al3 borderleft'><input class='fullwidth' type='text'  value='"+ data.unitPrice +"' name='price' disabled/></div>"
		+ "<div class='col-al3 borderleft'><input class='fullwidth' type='text' value='"+ total +"' name='total' disabled/></div>"
		+ "</div>";
	return str;
}
function approve(obj) {
	var msg = uiLabelMap && uiLabelMap.confirmApprove ? uiLabelMap.confirmApprove : "Confirm Approve";
	var id = currentMarketingCampaignId;	
	if($(obj).hasClass("active")){
		return;
	}
	$(obj).addClass("active");
	bootbox.confirm(msg, function(res) {
		if (res && id) {
			$.ajax({
				url : "acceptRequest",
				data : {
					marketingCampaignId : id
				},
				type : "POST",
				success : function(res) {
					if (res && res.status == "success") {
						$("#listRequest").jqxGrid("updatebounddata");
						$("#approveRequest").modal("hide");
					}
					$(obj).removeClass("active");	
				}
			});
		}else{
			$(obj).removeClass("active");
		}
	});
}

function reject() {
	$("#rejectNote").modal("show");	
}
function confirmReject(obj){
	if($(obj).hasClass("active")){
		return;
	}
	var id = currentMarketingCampaignId;
	var note = CKEDITOR.instances.noteArea.getData();
	if (id && note) {
		$(obj).addClass("active");
		$.ajax({
			url : "refuseRequest",
			data : {
				marketingCampaignId : id,
				note: note
			},
			type : "POST",
			success : function(res) {
				if (res && res.message == "success") {
					bootbox.alert(uiLabelMap.approveCampaignSuccess);
					$("#rejectNote").modal("hide");
					$("#approveRequest").modal("hide");
				}else{
					$("#rejectNote").hide();
					bootbox.alert(uiLabelMap.approveCampaignFail);
				}
				$(obj).removeClass("active");
			}
		});
	}
}
function cancelReject(){
	$("#rejectNote").modal("hide");
}
function setId(id) {
	$("#approve").data('id', id);
	$("#reject").data('id', id);
}

$(document).ready(function() {
	function init() {
		$("#approve").click(function(){
			approve(this);
		});
		$("#reject").click(function(){
			reject(this);
		});
		$("#confirmReject").click(function(){
			confirmReject(this);
		});
		$("#cancelReject").click(function(){
			cancelReject(this);
		});
		var editor = CKEDITOR.replace('noteArea', {
		    height: '220px',
		    skin: 'office2013',
		    startupFocus : true,
		    on : {
		        instanceReady : function( ev )
		        {
		            this.focus();
		        }
		    }
		});
	}
	init();
});
