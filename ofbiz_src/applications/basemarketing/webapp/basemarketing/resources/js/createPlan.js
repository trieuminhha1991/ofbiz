var Planning = (function() {
	var form;
	var list = [];
	var renderPartyManager = function(index, label, x){
		var source = $("#PartyManager").jqxComboBox('source');
		var data = source.records;
		var value = data[index];
		var first = value.firstName ? capitalizeFirstLetter(value.firstName) : "";
		var middle = value.middleName ? capitalizeFirstLetter(value.middleName) : "";
		var last = value.lastName ? capitalizeFirstLetter(value.lastName) : "";
		var str = "<p><b>" + last + ' ' + middle + ' ' + first + "</b></p>";
		return str;
	};
	var renderPartyManagerSelected = function(index, item){
		var source = $("#PartyManager").jqxComboBox('source');
		var data = source.records;
		var value = data[index];
		var first = value.firstName ? capitalizeFirstLetter(value.firstName) : "";
		var middle = value.middleName ? capitalizeFirstLetter(value.middleName) : "";
		var last = value.lastName ? capitalizeFirstLetter(value.lastName) : "";
		var type = value.partyTypeIdFrom;
		var str = last + ' ' + middle + ' ' + first;
		return str;
	};
	var capitalizeFirstLetter = function(string){
	    return string.charAt(0).toUpperCase() + string.slice(1);
	};
	var initElement = function() {
		initTabBasic();
		initTabMain();
		var width = "98%";
		var height = "25px";
		$.jqx.theme='olbius';
		initComboboxParentPlan();
		initComboboxPartyManager();
		var planInput = $('#marketingPlanId');
		planInput.jqxInput({
			width : 'calc('+width+' - 10px)',
			height : height
		});
		planInput.val(planInput.data('value'));
		planInput.keyup(function(){
			var val = $(this).val();
			val = val.replace(/[^\w]/gi, '');
			var res = '';
			for(var x = 0; x < val.length; x++){
				res += val[x].toUpperCase();
			}
			$(this).val(res);
		});
		var nameInput = $('#name');
		nameInput.jqxInput({
			width : 'calc('+width+' - 10px)',
			height : height
		});
		nameInput.val(nameInput.data('value'));
		var mt = $('#marketingTypeId');
		mt.jqxDropDownList({
			source : marketingType,
			width : width,
			height : height,
			dropDownHeight : 200,
			filterable : true,
			displayMember : "description",
			valueMember : "marketingTypeId",
			placeHolder : ""
		});
		var type = mt.data('value');
		if(type){
			mt.jqxDropDownList('val', type);
		}
		var status = $('#statusId');
		status.jqxDropDownList({
			source : statusData,
			width : width,
			height : height,
			dropDownHeight : 200,
			displayMember : "description",
			valueMember : "statusId",
			placeHolder : "",
			selectedIndex: 0
		});
		var valStatus = status.data('value');
		if(valStatus){
			status.jqxDropDownList('val', valStatus);
		}
		var fd = $('#fromDate');
		fd.jqxDateTimeInput({
			width : width,
			height : height,
			clearString: uiLabelMap.clearString,
			todayString: uiLabelMap.todayString,
			showFooter: true
		});
		type = fd.data('value');
		if (type) {
			var date1 = new Date(type);
			fd.jqxDateTimeInput('val', date1);
		}else{
			fd.jqxDateTimeInput('val', null);
		}
		var td = $('#thruDate');
		td.jqxDateTimeInput({
			width : width,
			height : height,
			clearString: uiLabelMap.clearString,
			todayString: uiLabelMap.todayString,
			showFooter: true
		});
		type = td.data('value');
		if (type) {
			var date1 = new Date(type);
			td.jqxDateTimeInput('val', date1);
		}else{
			td.jqxDateTimeInput('val', null);
		}
	};
	var initTabBasic = function(){
		initEditor($('#vision'));
		$('#planHeaderTab').on('shown.bs.tab', function(e) {
			form.jqxValidator('hide');
			var href = $(e.target).attr('href');
			switch(href) {
			case "#missionTab":
				var obj = $('#mission');
				if (!obj.data('init')) {
					setTimeout(function() {
						initEditor(obj);
					}, 100);
					break;
				} else {
					obj.data('init', true);
				}
			case "#messagingTab":
				var obj = $('#messaging');
				if (!obj.data('init')) {
					setTimeout(function() {
						initEditor(obj);
					}, 100);
				} else {
					obj.data('init', true);
				}
				break;
			case "#visionTab":
				var obj = $('#vision');
				if (!obj.data('init')) {
					setTimeout(function() {
						initEditor(obj);
					}, 100);
				} else {
					obj.data('init', true);
				}
				break;
			case "#summaryTab":
				var obj = $('#summary');
				if (!obj.data('init')) {
					setTimeout(function() {
						initEditor(obj);
					}, 100);
				} else {
					obj.data('init', true);
				}
				break;
			}
		});
	};
	var initTabMain = function(){
		initEditor($('#insight'));
		$('#planContentTab').on('shown.bs.tab', function(e) {
			var href = $(e.target).attr('href');
			if(href=="#strategyTab" && $('#ListPlan').length){
				initGridListPlan();
				return;
			}
			href = href.split("Tab").shift();
			var obj = $(href);
			if(!obj.length){
				return;
			}
			if (!obj.data('init')) {
				setTimeout(function() {
					initEditor(obj);
				}, 100);
			} else {
				obj.data('init', true);
			}
		});
	};
	var initEditor = function(element, width, height) {
		element.jqxEditor({
			width : width ? width : '100%',
			height : height ? height : '300px',
			theme : 'olbiuseditor'
		});
		var value = element.data('value');
		if(value){
			element.jqxEditor('val', value);
		}
	};
	var getContent = function(){
		var contents = [];
		var id = "";
		var vis = $('#vision');
		if(vis.jqxEditor('val')){
			contents.push({
				type: "PLAN_VISION",
				name: "Vision",
				id: vis.data('id'),
				description: vis.jqxEditor('val')
			});
		}
		var mis = $('#mission');
		if(mis.jqxEditor('val')){
			contents.push({
				type: "PLAN_MISSION",
				name: "Mission",
				id: mis.data('id'),
				description: mis.jqxEditor('val')
			});
		}
		var mes = $('#messaging');
		if(mes.jqxEditor('val')){
			contents.push({
				type: "PLAN_MESSAGE",
				name: "Messaging",
				id: mes.data('id'),
				description: mes.jqxEditor('val')
			});
		}
		var sw = $('#swot');
		if(sw.jqxEditor('val')){
			contents.push({
				type: "PLAN_SWOT",
				name: "Swot",
				id: sw.data('id'),
				description: sw.jqxEditor('val')
			});
		}
		var pe = $('#pest');
		if(pe.jqxEditor('val')){
			contents.push({
				type: "PLAN_PEST",
				name: "Pest",
				id: pe.data('id'),
				description: pe.jqxEditor('val')
			});
		}
		var ins = $('#insight');
		if(ins.jqxEditor('val')){
			contents.push({
				type: "MARKET_INSIGHT",
				name: "Marketing insight",
				id: ins.data('id'),
				description: ins.jqxEditor('val')
			});
		}
		var com = $('#comparison');
		if(com.jqxEditor('val')){
			contents.push({
				type: "COMP_COMPARISON",
				name: "competitor comparison",
				id: com.data('id'),
				description: com.jqxEditor('val')
			});
		}
		var cus = $('#customer');
		if(cus.jqxEditor('val')){
			contents.push({
				type: "CUSTOMER_GROUP",
				name: "Customer group",
				id: cus.data('id'),
				description: cus.jqxEditor('val')
			});
		}
		var obj = $('#objective');
		if(obj.jqxEditor('val')){
			contents.push({
				type: "PLAN_OBJECTIVE",
				name: "Objective",
				id: obj.data('id'),
				description: obj.jqxEditor('val')
			});
		}
		return contents;
	};
	var checkDataChange = function(){
		var flag = false;
		if(!list.length){
			return true;
		}
		for(var x = 0; x < list.length; x++){
			var obj = $(list[x]);
			var val = obj.data('value');
			var cur = obj.val();
			if(obj.hasClass('jqx-input') || obj.hasClass('jqx-dropdownlist') || obj.hasClass('jqx-combobox') || obj.hasClass('jqx-widget-olbiuseditor')){
				if(obj.hasClass('jqx-datetimeinput')){
					var d1 = new Date(cur);
					var d2 = new Date(val);
					if(d1 == d2){
						flag = true;
						break;
					}
				}else{
					if(cur != val){
						flag = true;
						break;
					}
				}
			}
		}
		return flag;
	};
	var initRules = function(){
		form.jqxValidator({
			rtl: true,
			rules : [{
				input : '#marketingPlanId',
				message : uiLabelMap.MarketingPlanIdRequireNoSpace,
				action : 'blur',
				rule : function(input, commit) {
					var value = input.val();
					if (value.indexOf(' ') == -1)
						return true;
					return false;
				}
			},{
				input : '#name',
				message : uiLabelMap.CommonRequired,
				action : 'blur',
				rule : 'required'
			},{
				input : '#fromDate',
				message : uiLabelMap.FromDateSmallerThanThruDate,
				action : 'change',
				rule : function(input, commit) {
					var thru = $('#thruDate').jqxDateTimeInput('getDate');
					var from = input.jqxDateTimeInput('getDate');
					if(!thru){
						return true;
					}
					if (thru && from && from <= thru)
						return true;
					return false;
				}
			},{
				input : '#thruDate',
				message : uiLabelMap.ThruDateLargerThanFromDate,
				action : 'change',
				rule : function(input, commit) {
					var from = $('#fromDate').jqxDateTimeInput('getDate');
					var thru = input.jqxDateTimeInput('getDate');
					if (thru && from && from <= thru)
						return true;
					if(!thru) return true;
					return false;
				}
			}]
		});
	};
	var save = function(){
		// if(!checkDataChange()){
			// bootbox.alert(uiLabelMap.DataNotChange);
			// return;
		// }
		if(!form.jqxValidator('validate')){
			$('#planHeaderTab a[href="#BasicInfo"]').tab('show');
			return;
		}
		var fr =  $('#fromDate').jqxDateTimeInput('getDate');
		var thr =  $('#thruDate').jqxDateTimeInput('getDate');
		var contents = getContent();
		var data = {
			marketingPlanId : planid,
			parentPlanId : parentPlanId,
			partyId : $('#PartyManager').jqxComboBox('val'),
			code: $('#marketingPlanId').val(),
			name: $('#name').val(),
			fromDate: fr ? fr.format('yyyy-mm-dd') : '',
			thruDate:  thr ? thr.format('yyyy-mm-dd') : '',
			statusId: $('#statusId').val(),
			marketingTypeId: $('#marketingTypeId').val(),
			description: $('#summary').jqxEditor('val'),
			contents: JSON.stringify(contents)
		};
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(res){
				if(url == "createMarketingPlanAndItem"){
					var id = res.marketingPlanId;
					if(id){
						var uri = "EditMarketingPlan?id=" + id;
						if(typeof(parentPlanId)!='undefined'){
							uri += "&parentPlanId=" + parentPlanId;
						}
						window.location.href = uri;
					}else{
						bootbox.alert(uiLabelMap.CreateError);
					}
				}else{
					if(res._ERROR_MESSAGE_LIST_){
						bootbox.alert(uiLabelMap.UpdateFailure);
					}else{
						bootbox.alert(uiLabelMap.UpdateSuccessfully);
					}
				}
			}
		});
	};
	var clear = function(){

	};
	var bindEvent = function(){
		$('#savePlan').click(function(){
			save();
		});
		$('#cancelPlan').click(function(){
			clear();
		});
	};
	var init = function() {
		form = $('#createContentWrapper');
		list = $('#createContentWrapper div, #createContentWrapper input').filter(function(){
			var value = $(this).data('value');
			if(value || $(this).hasClass('[class^="jqx"]')){
				return true;
			}
		});
		initElement();
		initRules();
		bindEvent();
	};
	var getAutoMarketingPlanId = function (input) {
        $.ajax({
            url: 'getAutoMarketingPlanId',
            type: 'POST',
            success: function(data) {
                if(OlbCore.isNotEmpty(data)){
                    input.val(data.result);
                }
            },
            error:function(errorMessage){

            }
        });
    };
	return {
		init : init,
		renderPartyManager: renderPartyManager,
        getAutoMarketingPlanId: getAutoMarketingPlanId,
		renderPartyManagerSelected: renderPartyManagerSelected
	};
})();
$(document).ready(function() {

	Planning.init();
});
