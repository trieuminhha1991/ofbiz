$(function(){
	InventoryObj.init();
});
var InventoryObj = (function(){
	var init = function(){
		var curFacilityId = null;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function (){
		if ("REQ_APPROVED" != curStatusId || requiredFacilityId === undefined || requiredFacilityId === null){
			window.location.href = "viewRequirementDetail?requirementId="+requirementId;
		}
		curFacilityId = requiredFacilityId;
		for (var i = 0; i < facilityData.length; i ++){
			if (requiredFacilityId === facilityData[i].facilityId){
				$('#inventoryFacilityId').text(unescapeHTML(facilityData[i].facilityName));
			}
		}
	};
	var initElementComplex = function (){
	};
	var initEvents = function (){
		$("#jqxgridRequirementItem").on("pagechanged", function (event) {
		    // event arguments.
		    var args = event.args;
		    // page number.
		    var pagenum = args.pagenum;
		    // page size.
		    var pagesize = args.pagesize;
		    var cell = $('#jqxgridRequirementItem').jqxGrid('getselectedcell');
		    $("#jqxgridRequirementItem").jqxGrid('endcelledit', cell.rowindex, cell.datafield, true, true);
		}); 
		
		$("#changeLabel").click(function () {
			if (listRequirementItemSelected.length == 0){
				bootbox.dialog(uiLabelMap.YouNotYetChooseProduct, [{
                    "label" : uiLabelMap.OK,
                    "class" : "btn btn-primary standard-bootbox-bt",
                    "icon" : "fa fa-check",
                    }]
                );
                return false;
			} else {
				if (countItem > listRequirementItemSelected.length){
					bootbox.dialog(uiLabelMap.AllItemInListMustBeUpdated, [{
	                    "label" : uiLabelMap.OK,
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    }]
	                );
					return false;
				}
				for (var i = 0; i < listRequirementItemSelected.length; i ++){
					if (listRequirementItemSelected[i].quantity > 0){
						var data = listRequirementItemSelected[i];
						if (data.inventoryItemId === null || data.inventoryItemId === undefined || data.inventoryItemId === ''){
							bootbox.dialog(uiLabelMap.YouNotYetChooseExpireDate, [{
			                    "label" : uiLabelMap.OK,
			                    "class" : "btn btn-primary standard-bootbox-bt",
			                    "icon" : "fa fa-check",
			                    }]
			                );
							return false;
						}
					}
				}
				bootbox.dialog(uiLabelMap.AreYouSureChange, 
				[{"label": uiLabelMap.CommonCancel, 
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }, 
		        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
		            	for (var i = 0; i < listRequirementItemSelected.length; i ++){
							listRequirementItemSelected[i]["productName"] = '';
		            	}
		            	listRequirementItemSelected = JSON.stringify(listRequirementItemSelected);
		            	Loading.show('loadingMacro');
		            	setTimeout(function(){
			            	$.ajax({
				   				 type: "POST",
				   				 url: "changeInventoryLabelFromRequirement",
				   				 data: {
				   					 inventoryItemLabelId: changeToLabel,
				   					 listInventoryItems: listRequirementItemSelected,
				   				 },
				   				 dataType: "json",
				   				 async: false,
				   				 success: function(data){
				   				 },
				   				 error: function(response){
				   				 }
			   		 		}).done(function(data) {
			   		 			window.location.href = "viewRequirementDetail?requirementId="+requirementId;
				      		});
			            	Loading.hide('loadingMacro');
		            	}, 500);
		            }
				}]);
			}
			
		});
		
		$("#jqxgridRequirementItem").on('cellendedit', function (event) {
			 // event arguments.
		    var args = event.args;
		    // column data field.
		    var dataField = event.args.datafield;
		    // row's bound index.
		    var rowBoundIndex = event.args.rowindex;
		    // cell value
		    var value = args.value;
		    // cell old value.
		    var oldvalue = args.oldvalue;
		    // row's data.
		    var rowData = args.row;
			if (args.datafield == "inventoryItemId") {
			    for (var m = 0; m < listInv.length; m ++){
		    		if (listInv[m].inventoryItemId == value){
		    			if (rowData.quantity > listInv[m].quantityOnHandTotal){
		    				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', rowBoundIndex, 'changeQuantity', listInv[m].quantityOnHandTotal);
		    			} else {
		    				$('#jqxgridRequirementItem').jqxGrid('setcellvaluebyid', rowBoundIndex, 'changeQuantity', rowData.quantity);
		    			}
		    			break;
		    		}
		    	}
			}
		    if (listRequirementItemSelected.length == 0){
		    	listRequirementItemSelected.push(rowData);
		    } else {
		    	var existed = false;
		    	$.each(listRequirementItemSelected, function(i){
	   				var olb = listRequirementItemSelected[i];
	   				if (olb.reqItemSeqId == rowData.reqItemSeqId){
	   					listRequirementItemSelected.splice(i,1);
	   					if (args.datafield == "changeQuantity") {
	   						olb['changeQuantity'] = value;
	   					} else if (args.datafield == "inventoryItemId") {
	   						olb['inventoryItemId'] = value;
	   					} 
	   					listRequirementItemSelected.push(olb);
	   					existed = true;
	   					return false;
	   				}
	   			});
		    	if (existed == false){
   					listRequirementItemSelected.push(rowData);
   				}
		    }
		});
	};
	var initValidateForm = function (){
	};
	var getLocalization = function getLocalization() {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	
	getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	function addZero(i) {
	    if (i < 10) {
	        i = "0" + i;
	    }
	    return i;
	}
	
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
	        async: false,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
    function escapeHtml(string) {
	    return String(string).replace(/[&<>"'\/]/g, function (s) {
	      return entityMap[s];
	    });
	 }
	var unescapeHTML = function unescapeHTML(escapedStr) {
	     var div = document.createElement('div');
	     div.innerHTML = escapedStr;
	     var child = div.childNodes[0];
	     return child ? child.nodeValue : '';
	 };
	 var entityMap = {
			    "&": "&amp;",
			    "<": "&lt;",
			    ">": "&gt;",
			    '"': '&quot;',
			    "'": '&#39;',
			    "/": '&#x2F;'
			 };
	return {
		init: init,
		getLocalization: getLocalization,
		formatFullDate: formatFullDate,
		getFormattedDate: getFormattedDate,
		unescapeHTML: unescapeHTML,
	};
}());