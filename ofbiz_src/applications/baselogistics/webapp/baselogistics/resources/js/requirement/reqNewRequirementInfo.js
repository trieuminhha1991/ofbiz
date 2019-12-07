$(function(){
	ReqInfoObj.init();
});
var ReqInfoObj = (function() {
	var validatorVAL;
	
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		$("#description").jqxInput({ width: 300, height: 90});
		$('#requirementTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, selectedIndex: 0, source: requirementTypeData, theme: theme, displayMember: 'description', valueMember: 'requirementTypeId',});
		$('#reasonEnumId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, selectedIndex: 0, source: reasonEnumData, theme: theme, displayMember: 'description', valueMember: 'enumId',});
		
		$("#originFacility").jqxDropDownButton({width: 300}); 
		$('#originFacility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#destFacility").jqxDropDownButton({width: 300}); 
		$("#destFacility").jqxDropDownButton({disabled: true}); 
		$('#destFacility').jqxDropDownButton('setContent', '<div class="green-label button-label">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		if (requirementTypeId != null && requirementTypeId != undefined && requirementTypeId != ''){
			$('#requirementTypeId').val(requirementTypeId);
		}
		
		$("#requirementStartDate").jqxDateTimeInput({width: 300, formatString: 'dd/MM/yyyy HH:mm', disabled: false, theme: theme});
		$("#requirementStartDate").jqxDateTimeInput('clear');
		
		if ($('#requirementTypeId')){
			if ($('#requirementTypeId').val() != null){
				setTimeout(function(){
					update({
						requirementTypeId: $('#requirementTypeId').val(),
						}, 'getReasonByRequirementTypeId' , 'listEnumReasons', 'enumId', 'description', 'reasonEnumId');
					requirementTypeId = $('#requirementTypeId').val();
				}, 300);
			}
		}
	};
	
	var initElementComplex = function() {
		if ($('#requirementTypeId').val() && $('#reasonEnumId').val()) {
			requirementTypeId = $('#requirementTypeId').val();
			reasonEnumId = $('#reasonEnumId').val();
			var url = '';
			if ("TRANSFER_REQUIREMENT" == requirementTypeId && reasonEnumId == "TRANS_INTERNAL"){
				url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
			} else {
				url = 'jqGetFacilities';
			}
			initFacilityGrid($("#jqxGridOriginFacility"), url);
			initFacilityGrid($("#jqxGridDestFacility"), url);
		}
	};
	
	var initEvents = function(){
		
		$("#jqxGridOriginFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        if (rowData){
	        	originFacilitySelected = $.extend({}, rowData);
		        var description = uiLabelMap.PleaseSelectTitle; 
		        if (originFacilitySelected) {
		        	if (originFacilitySelected.facilityCode != null){
		        		description = '['+ originFacilitySelected.facilityCode +'] ' + originFacilitySelected.facilityName;
		        	} else {
		        		description = '['+ originFacilitySelected.facilityId +'] ' + originFacilitySelected.facilityName;
		        	}
		        }
				
		        var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
		        $('#originFacility').jqxDropDownButton('setContent', dropDownContent);
	        } else {
	        	setTimeout(function(){
	        		$('#jqxGridOriginFacility').jqxGrid('clearselection');
	        	}, 100);
	        }
	    });
		
		$('#jqxGridOriginFacility').on('rowdoubleclick', function (event) { 
			$('#originFacility').jqxDropDownButton('close');
		});
		
		$("#jqxGridOriginFacility").on('bindingcomplete', function (event) {
			if (originFacilitySelected != null){
				var rows = $('#jqxGridOriginFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == originFacilitySelected.facilityId){
							var index = $('#jqxGridOriginFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxGridOriginFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$('#originFacility').on('close', function (event) { 
			updateProductGridData();
		});
		
		$("#jqxGridDestFacility").on('rowselect', function (event) {
			var args = event.args;
			var rowData = args.row;
			destFacilitySelected = $.extend({}, rowData);
			var description = uiLabelMap.PleaseSelectTitle; 
			if (destFacilitySelected) {
				if (destFacilitySelected.facilityCode != null){
					description = '['+ destFacilitySelected.facilityCode +'] ' + destFacilitySelected.facilityName;
				} else {
					description = '['+ destFacilitySelected.facilityId +'] ' + destFacilitySelected.facilityName;
				}
			}
			
			var dropDownContent = '<div class="green-label button-label">'+ description +' </div>';
			$('#destFacility').jqxDropDownButton('setContent', dropDownContent);
		});
		
		$('#jqxGridDestFacility').on('rowdoubleclick', function (event) { 
			$('#destFacility').jqxDropDownButton('close');
		});
		
		$("#jqxGridDestFacility").on('bindingcomplete', function (event) {
			if (originFacilitySelected != null){
				var rows = $('#jqxGridDestFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						if (data1.facilityId == destFacilitySelected.facilityId){
							var index = $('#jqxGridDestFacility').jqxGrid('getrowboundindexbyid', data1.uid);
							$('#jqxGridDestFacility').jqxGrid('selectrow', index);
						}
					}
				}
			}
		});
		
		$("#reasonEnumId").on('change', function(event){
			reasonEnumId = $("#reasonEnumId").jqxDropDownList('val'); 
			changeFacilitySources(requirementTypeId, reasonEnumId);
			if ($("#jqxGridProduct").length > 0 && originFacilitySelected != null && requirementTypeId != null){
				updateProductGridData();
			}
		});
		
		$("#requirementTypeId").on('change', function(event){
			setTimeout(function(){
				update({
					requirementTypeId: $('#requirementTypeId').val(),
					}, 'getReasonByRequirementTypeId' , 'listEnumReasons', 'enumId', 'description', 'reasonEnumId');
			}, 300);
			requirementTypeId = $('#requirementTypeId').val();
		    if ($("#requirementTypeId").val() == "TRANSFER_REQUIREMENT"){
		    	$("#destFacility").jqxDropDownButton({disabled: false}); 
		    } else {
		    	$("#destFacility").jqxDropDownButton({disabled: true}); 
		    } 
		});
	};
	
	var initFacilityGrid = function(grid, url){
//		var url = "jqGetFacilities";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.facilityId;
						}
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.BLFacilityName, datafield: 'facilityName', width: '80%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initValidateForm = function(){
		var extendRules = [
                {input: '#originFacility', message: uiLabelMap.FieldRequired, action: 'valueChanged', position: 'right',
					rule: function(input, commit){
						if (!originFacilitySelected){
					   		return false;
						}
						return true;
					}
				},
				{input: '#destFacility', message: uiLabelMap.FieldRequired, action: 'valueChanged', position: 'right',
					rule: function(input, commit){
						if (destFacilitySelected == null && requirementTypeId == 'TRANSFER_REQUIREMENT'){
							return false;
						}
						return true;
					}
				},
				{input: '#destFacility', message: uiLabelMap.BLCannotTransferSameFacility, action: 'valueChanged', position: 'right',
					rule: function(input, commit){
						if (originFacilitySelected != null && destFacilitySelected != null && requirementTypeId == 'TRANSFER_REQUIREMENT'){
							if (originFacilitySelected.facilityId == destFacilitySelected.facilityId){
								return false;
							}
						}
						return true;
					}
				},
				{input: '#requirementStartDate', message: uiLabelMap.CannotBeforeNow , action: 'change', position: 'topcenter',
					rule: function(input, commit){
						var value = $('#requirementStartDate').jqxDateTimeInput('getDate');
						var nowDate = new Date();
						if(value < nowDate){
							return false;
						}
						return true;
					}
				},
              ];
   		var mapRules = [
   				{input: '#requirementStartDate', type: 'validInputNotNull'},
   				{input: '#requirementTypeId', type: 'validInputNotNull'},
   				{input: '#reasonEnumId', type: 'validInputNotNull'},
               ];
   		validatorVAL = new OlbValidator($('#initRequirement'), mapRules, extendRules, {position: 'right'});
	};
	
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
    function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    
    var getValidator = function(){
    	return validatorVAL;
    }
    
    function updateProductGridData(){
    	var element = $("#jqxGridProduct");
		var facId = null;
		if (originFacilitySelected && requirementTypeId){
			facId = originFacilitySelected.facilityId;
			var tmpS = element.jqxGrid('source');
			if (tmpS){
				var curUrl = tmpS._source.url;
				var newUrl = url = "jqxGeneralServicer?sname=JQGetListProductByOrganiztion&inventoryInfo=Y&facilityId="+facId+"&requirementTypeId="+requirementTypeId;
				if (newUrl != curUrl){
					tmpS._source.url = newUrl;
					element.jqxGrid('source', tmpS);
				}	
			}
		} 
    }
    
    var changeFacilitySources = function (reqTypeId, reasonEnumId){
    	var url = "";
    	if ("TRANSFER_REQUIREMENT" == requirementTypeId && reasonEnumId == "TRANS_INTERNAL"){
			url = "jqGetFacilities&facilityGroupId=FACILITY_INTERNAL";
		} else {
			url = 'jqGetFacilities';
		}
    	var tmpS = $('#jqxGridOriginFacility').jqxGrid('source');
		if (tmpS){
			var curUrl = tmpS._source.url;
			var newUrl = "jqxGeneralServicer?sname="+url;
			if (newUrl != curUrl){
				tmpS._source.url = newUrl;
				$('#jqxGridOriginFacility').jqxGrid('source', tmpS);
			}	
		}
		tmpS = $('#jqxGridDestFacility').jqxGrid('source');
		if (tmpS){
			var curUrl = tmpS._source.url;
			var newUrl = "jqxGeneralServicer?sname="+url;
			if (newUrl != curUrl){
				tmpS._source.url = newUrl;
				$('#jqxGridDestFacility').jqxGrid('source', tmpS);
			}	
		}
    }
    
	return {
		init: init,
		getValidator: getValidator,
	}
}());