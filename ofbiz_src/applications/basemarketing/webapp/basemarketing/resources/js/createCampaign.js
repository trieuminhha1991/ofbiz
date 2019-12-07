var Campaign = (function() {
	var initTabs = function() {
		var tab = $('#campaignDetailTabs');
		tab.on('shown.bs.tab', function(e) {
			var tab = $(e.target).attr('href');
			switch(tab) {
				case "#place":
					var grid = $('#MarketingPlace');
					if(!grid.data('init')){
						grid.data('init', true);
						initGridListContactBusiness();
						initGridMarketingPlace();
						bindSelectContact();
						if(typeof(places) != "undefined" && places.length){
							setTimeout(function(){
								initGridData($('#MarketingPlace'), places);
							}, 500);
						}
						break;
					}
				case "#product":
					var grid = $('#MarketingProductGrid');
					if(!grid.data('init')){
						grid.data('init', true);
						initGridMarketingProductGrid();
						if(typeof(products) != "undefined" && products.length){
							setTimeout(function(){
								initGridData($('#MarketingProductGrid'), products);
							}, 500);
						}
					}
					break;
				case "#cost":
					var grid = $('#dMarketingCost');
					if(!grid.data('init')){
						grid.data('init', true);
						initGridMarketingCost();
						if(typeof(costs) != "undefined" && costs.length){
							setTimeout(function(){
								initGridData($('#MarketingCost'), costs);
							}, 500);
						}
					}
					break;
				case "#role":
					var grid = $('#MarketingRole');
					if(!grid.data('init')){
						grid.data('init', true);
						initGridMarketingRole();
						if(typeof(roles) != "undefined" && roles.length){
							setTimeout(function(){
								initGridData($('#MarketingRole'), roles);
							}, 500);
						}
					}
					break;
			}
		});
	};
	var initElement = function() {
		var width = "80%";
		$('#marketingCampaignId').jqxInput({
			width: width,
			height: 25
		});
		if($('#marketingCampaignId').val()){
			$('#marketingCampaignId').jqxInput('disabled', true);
		}
		$('#marketingTypeId').jqxDropDownList({
			source : marketingType,
			width : width,
			dropDownHeight : 200,
			filterable : true,
			displayMember : "description",
			valueMember : "marketingTypeId",
			placeHolder : ""
		});
		var type = $('#marketingTypeId').data('value');
		$('#marketingTypeId').jqxDropDownList('val', type);
		$('#budgetedCost').jqxNumberInput({
			width : width,
			max : 999999999999999999,
			digits : 18,
			decimalDigits : 2,
			spinButtons : false,
			min : 0
		});
		type = $('#budgetedCost').data('value');
		$('#budgetedCost').jqxNumberInput('val', type);
		$('#estimatedCost').jqxNumberInput({
			width : width,
			max : 999999999999999999,
			digits : 18,
			decimalDigits : 2,
			spinButtons : false,
			min : 0
		});
		type = $('#estimatedCost').data('value');
		$('#estimatedCost').jqxNumberInput('val', type);
		$('#actualCost').jqxNumberInput({
			width : width,
			max : 999999999999999999,
			digits : 18,
			decimalDigits : 2,
			spinButtons : false,
			min : 0
		});
		type = $('#actualCost').data('value');
		$('#actualCost').jqxNumberInput('val', type);
		$('#currencyUomId').jqxDropDownList({
			source : currencyUomData,
			width : width,
			dropDownHeight : 200,
			filterable : true,
			displayMember : "description",
			valueMember : "uomId",
			placeHolder : ""
		});
		type = $('#currencyUomId').data('value');
		if(type){
			$('#currencyUomId').jqxDropDownList('val', type);
		}else{
			$('#currencyUomId').val('VND');
		}
		$('#campaignSummary').jqxEditor({
			width: '265%',
	        height: '300px',
	        theme: 'olbiuseditor'
		});
		type = $('#campaignSummary').data('value');
		$('#campaignSummary').jqxEditor('val', type);
		$('#dueDate').jqxDateTimeInput({ width: width,  selectionMode: 'range' });
		type = $('#dueDate').data('value');
		var tmp = type.split(' - ');
		if(tmp.length == 2){
			var d1 = tmp[0].split(' ');
			var d2 = tmp[1].split(' ');
			var date1 = new Date(d1);
			var date2 = new Date(d2);
			$('#dueDate').jqxDateTimeInput('val', date1, date2);
		}
	};
	var initGridData = function(grid, data){
		for(var x in data){
			grid.jqxGrid('addRow', null, data[x], "last");
		}
	};
	var getProduct = function() {
		$("#MarketingProductGrid").jqxGrid('refreshdata');
		var rows = $("#MarketingProductGrid").jqxGrid('getboundrows');
		if(!rows){
			return [];
		}
		var results = [];
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var obj = {
				productId : row.productId,
				quantity : row.quantity,
				uomId : row.uomId,
				marketingPlaceId : row.marketingPlaceId,
				productTypeId : row.productTypeId
			};
			if(row.marketingProductId){
				obj.marketingProductId = row.marketingProductId;
			}
			results.push(obj);
		}
		return results;
	};
	var getCost = function() {
		$("#MarketingProductGrid").jqxGrid('refreshdata');
		var rows = $("#MarketingCost").jqxGrid('getboundrows');
		if(!rows){
			return [];
		}
		var results = [];
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var obj = {
				marketingCostTypeId : row.marketingCostTypeId,
				description : row.description,
				unitPrice : row.unitPrice,
				currencyUomId : row.currencyUomId,
				quantity : row.quantity,
				quantityUomId : row.quantityUomId
			};
			if(row.marketingCostId){
				obj.marketingCostId = row.marketingCostId;
			}
			results.push(obj);
		}
		return results;
	};
	var getRole = function() {
		var rows = $("#MarketingRole").jqxGrid('getboundrows');
		if(!rows){
			return [];
		}
		var results = [];
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			results.push({
				marketingPlaceId : row.marketingPlaceId,
				partyId : row.partyId,
				roleTypeId : row.roleTypeId,
				description: row.description
			});
		}
		return results;
	};
	var getPlace = function() {
		var rows = $("#MarketingPlace").jqxGrid('getboundrows');
		if(!rows){
			return [];
		}
		var results = [];
		for (var i = 0; i < rows.length; i++) {
			var row = rows[i];
			var obj = {
				organizationId : row.organizationId,
				geoId: row.geoId,
				contactMechId: row.contactMechId,
				fromDate: row.fromDate,
				thruDate: row.thruDate
			};
			if(row.marketingPlaceId){
				obj.marketingPlaceId = row.marketingPlaceId;
			}
			results.push(obj);
		}
		return results;
	};
	var bindEvent = function(){
		$("#saveCampaign").click(function(){
			save();
		});
		$("#cancelCampaign").click(function(){
			window.location.reload();
			// Grid.clearForm($('#campaignDetailTabs'));
		});
	};
	var save = function(){
		var form = $('#campaignInfo');
		if(!form.jqxValidator('validate')){
			$('#campaignDetailTabs').jqxTabs('select', 0);
			return false;
		}
		var index = $('#marketingTypeId').jqxDropDownList('getSelectedItem');
		var type = index ? index.value : "";
		index = $('#currencyUomId').jqxDropDownList('getSelectedItem');
		var uom = index ? index.value : "";
		var d = $('#dueDate').val().split(' - ');
		var d1 = d.length == 2 ? formatDate(d[0]) : "";
		var d2 = d.length == 2 ? formatDate(d[1]) : "";
		var active = $('#isActive').is(':checked') ? "Y" : "N";
		var done = $('#isDone').is(':checked') ? "Y" : "N";
		var products = getProduct();
		var places = getPlace();
		var costs = getCost();
		var roles = getRole();
		var data = {
			marketingCampaignId: $('#marketingCampaignId').val(),
			campaignName: $('#campaignName').val(),
			marketingTypeId: type,
			fromDate: d1,
			thruDate: d2,
			budgetedCost: $('#budgetedCost').jqxNumberInput('val'),
			estimatedCost: $('#estimatedCost').jqxNumberInput('val'),
			actualCost: $('#actualCost').jqxNumberInput('val'),
			currencyUomId: uom,
			isActive: active,
			isDone: done,
			campaignSummary: $('#campaignSummary').jqxEditor('val'),
			products: products.length ? JSON.stringify(products) : "",
			places: places.length ? JSON.stringify(places) : "",
			costs: costs.length ? JSON.stringify(costs) : "",
			roles: roles.length ? JSON.stringify(roles) : "",
		};
		var send = function(url, data){
			$.ajax({
				url: url,
				type: "POST",
				data: data,
				success: function(res){
					if(url == "createMarketingCampaignAndItem"){
						var id = res.marketingCampaignId;
						if(id){
							window.location.href = "CreateCampaignMarketing?id=" + id;
						}
						success(res, uiLabelMap.CreateSuccess, uiLabelMap.CreateError);
					}else{
						success(res, uiLabelMap.UpdateSuccessfully, uiLabelMap.UpdateError);
					}
				}
			});
		};
		if(url == "createMarketingCampaignAndItem"){
			bootbox.dialog(uiLabelMap.ConfirmCreateCampaign, [{
	            "label"   : uiLabelMap.Cancel,
	            "icon"    : 'fa fa-remove',
	            "class"   : 'btn  btn-danger form-action-button pull-right',
	            "callback": function() {
                  bootbox.hideAll();
                }
	        }, {
	            "label"   : uiLabelMap.OK,
	            "icon"    : 'fa-check',
	            "class"   : 'btn btn-primary form-action-button pull-right',
	            "callback": function() {
				send(url, data);
			}
	        }]);
		} else {
			send(url, data);
		}
		return true;
	};
	var success = function(res, success, error) {
		var obj = $("#contentCampaign");
		if (!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_) {
			obj.notify(success, {
				position : "top right",
				className : "success",
			});
		} else {
			obj.notify(error, {
				position : "top right",
				className : "error",
			});
		}
	};
	var formatDate = function(date){
		var d = date.split("/");
		var da = d[2]+"-"+d[1]+"-"+d[0];
		return da;
	};
	var bindSelectContact = function(){
		var bu = $('#ListContactBusiness');
		var sch = $('#ListContactSchool');
		var mk = $('#MarketingPlace');
		var updateSelection = function(){
			select(bu);
			select(sch);
		};
		var select = function(grid, id){
			grid.jqxGrid('clearSelection');
		};
		var action = function(element){
			element.on('rowselect', function(event){
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    var rowData = args.row;
			    addPlace(rowData);
			});
			element.on('rowunselect', function(event){
			    var args = event.args;
			    var rowBoundIndex = args.rowindex;
			    var rowData = args.row;
			    removePlace(rowData);
			});

		};
		mk.on('deletecompleted', function(e, data){
			updateSelection();
		});
		action(bu);
		action(sch);
	};

	var addPlace = function(data){
		var due = $('#dueDate').jqxDateTimeInput('getRange');
		var from = due.from ? due.from.format('dd/mm/yyyy') : '';
		var to = due.to ? due.to.format('dd/mm/yyyy') : '';
		var row = {
			organizationId: data.partyId,
			groupName: data.groupName,
			address: data.address1,
			contactNumber: data.contactNumber,
			fromDate: from,
			thruDate: to
		};
		var places = getPlace();
		if(_.findIndex(places, {organizationId : data.partyId}) == -1){
			var grid = $('#MarketingPlace');
			grid.jqxGrid('addRow', null, row, "last");
			grid.jqxGrid('clearSelection');
		}
	};
	var removePlace = function(data){
		var id = data.partyId;
		var grid = $('#MarketingPlace');
		grid.jqxGrid('deleterow', id);
	};
	var initRules = function(){
		var form = $('#campaignInfo');
		form.jqxValidator({
			rules : [{
				input : '#campaignName',
				message : uiLabelMap.CommonRequired,
				action : 'blur, change',
				rule : function(input, commit) {
					var value = input.val();
					if (!value)
						return false;
					return true;
				}
			},{
				input : '#marketingTypeId',
				message : uiLabelMap.CommonRequired,
				action : 'blur',
				rule : function(input, commit) {
					var value = input.jqxDropDownList('getSelectedIndex');
					return value != -1;
				}
			},{
				input : '#dueDate',
				message : uiLabelMap.CommonRequired,
				action : 'blur',
				rule : function(input, commit) {
					var value = input.jqxDateTimeInput('getDate');
					if (!value)
						return false;
					return true;
				}
			}]
		});
	};
	var init = function() {
		initTabs();
		initElement();
		bindEvent();
		initRules();
	};
	return {
		init : init
	};
})();
$(document).ready(function() {
	Campaign.init();
});