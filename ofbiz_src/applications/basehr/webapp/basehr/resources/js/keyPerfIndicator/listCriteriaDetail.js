var contextMenuObject = (function(){
	var init = function(){
		initJqxMenu();
		initMenuEvent();
	};
	
	var initJqxMenu = function(){
		$('#contextMenu').jqxMenu({width : 175, height : 65, mode: 'popup', autoOpenPopup : false});
		$('#contextMenuEdit').jqxMenu({width : 175, height : 35, mode: 'popup', autoOpenPopup : false, popupZIndex : 20000});
	};
	
	var initMenuEvent = function(){
		$("#contextMenu").on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'editPolicy'){
            	openJqxWindow($('#setupPolicyWindow'));
            	setupPolicyGrid.setData(dataRecord);
            }else if(action == 'viewKpiDetail'){
    			editKPIObj_Detail.set_rowData(dataRecord);
    			openJqxWindow($("#DetailWindow"));
            }
		});
		
		$('#contextMenuEdit').on('itemclick', function(event){
			var args = event.args;
			var rowindex = $("#setupPolicyGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#setupPolicyGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'ViewDetail'){
            	openJqxWindow($('#editPolicyWindow'));
            	editPolicyObject.setData(dataRecord);
            	editPolicyObject.setId(dataRecord.criteriaId, dataRecord.perfCriteriaPolicyId);
            }
		});
	};
	
	
	return {
		init : init
	}
}());

var createKpiPolicyWindow = (function(){
	var criteriaId = "";
	var init = function(){
		initWindow();
		initJqxDateTime();
		initInput();
		initValidator();
		initBtnEvent();
		initWindowEvent();
	};
	
	var initInput = function(){
		$('#policyId').jqxInput({width : '250px', height : '20px'});
	};
	
	var initWindow = function(){
		createJqxWindow($('#createKpiPolicyWindow'), 415, 250);
	};
	
	var initJqxDateTime = function(){
		$('#fromDateNew').jqxDateTimeInput({width : '255px', height : '25px'});
		$('#thruDateNew').jqxDateTimeInput({width : '255px', height : '25px', clearString:'Clear', showFooter:true});
	};
	
	var initValidator = function(){
		$('#createKpiPolicyWindow').jqxValidator({
			rules : [
			         {
			        	 input : '#policyId', message : uiLabelMap.HRContainSpecialSymbol, action : 'blur',
			        	 rule : function(input, commit){
			        		 var val = input.val();
			        		 if(val){
			        			 if(isContainSpecialChar(val)){
				        			 return false;
				        		 }
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#fromDateNew', message : uiLabelMap.FieldRequired, action : 'blur',
			        	 rule : function(input, commit){
			        		 if(!input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	 input : '#fromDateNew', message : uiLabelMap.FromDateLessThanEqualThruDate, action : 'blur',
			        	 rule : function(input, commit){
			        		 if($('#thruDateNew').jqxDateTimeInput('getDate')){
			        			 if(input.jqxDateTimeInput('getDate') > $('#thruDateNew').jqxDateTimeInput('getDate')){
				        			 return false;
				        		 }
			        		 }
			        		 return true;
			        	 }
			         }
	         ]
		});
		
	};
	var getData = function(){
		var data = {};
		data.policyId = $('#policyId').val();
		data.fromDate = $('#fromDateNew').jqxDateTimeInput('getDate').getTime();
		if($('#thruDateNew').jqxDateTimeInput('getDate')){
			data.thruDate = $('#thruDateNew').jqxDateTimeInput('getDate').getTime();
		}else{
			data.thruDate = null;
		}
		
		return data;
	};
	
	var initBtnEvent = function(){
		$('#alterSave_new').click(function(){
			var valid = $('#createKpiPolicyWindow').jqxValidator('validate');
			if(!valid){
				return false;
			}
			var data = getData();
			createPolicyKpi(data);
		});
		$('#alterCancel_new').click(function(){
			$('#createKpiPolicyWindow').jqxWindow('close');
		});
		
	};
	
	var initWindowEvent = function(){
		$('#createKpiPolicyWindow').bind('close', function(){
			$('#policyId').jqxInput('val', null);
			$('#fromDateNew').jqxDateTimeInput('val', new Date());
			$('#thruDateNew').jqxDateTimeInput('val', null);
		})
	};
	
	var closeWindow = function(){
		$("#createKpiPolicyWindow").jqxWindow('close');
	};
	
	var createPolicyKpi = function(data){
		$.ajax({
			type : 'POST',
			data : {
				'policyId' : data.policyId,
				'fromDate' : data.fromDate,
				'thruDate' : data.thruDate,
				'criteriaId' : criteriaId
			},
			url : 'createPolicyKpi',
			success : function(response){
				if(response.responseMessage == 'success'){
					var mess = uiLabelMap.CreateSuccess;
					$("#jqxNotificationsetupPolicyGrid").jqxNotification('closeLast');
					$("#notificationContentsetupPolicyGrid").text(mess);
					$("#jqxNotificationsetupPolicyGrid").jqxNotification('open');
					closeWindow();
					$("#setupPolicyGrid").jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage, [{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}])
				}
			}
		})
	};
	
	var setId = function(criteriaIdTmp){
		criteriaId = criteriaIdTmp;
	};
	
	return {
		init : init,
		setId : setId
	}
}());


var setupPolicyGrid = (function(){
	var init = function(){
		initWindow();
		initGrid();
	};
	
	var initGrid = function(){
		var rendertoolbar = function(toolbar){
			var jqxheader = $("<div id='toolbarcontainerPolicy_detail' class='widget-header'><h5><b>" + uiLabelMap.KpiPolicy + "</b></h5><div id='toolbarButtonContainer_detail' class='pull-right'></div></div>");
			if(toolbar.children().length == 0)
			{
				toolbar.append(jqxheader);
				var container = $('#toolbarButtonContainer_detail');
				var grid = $("#setupPolicyGrid");
				//comment hide new KpiPolicy
				//Grid.createAddRowButton(grid, container, uiLabelMap.CommonAddNew, {type: "popup", container: $("#createKpiPolicyWindow")});
			}
			Grid.createContextMenu(grid, $("#contextMenuEdit"), false);
			return toolbar;	
		};
		
		var config = {
			width: '100%', 
      		height: 275,
      		autoheight: false,
      		virtualmode : true,
      		showfilterrow: false,
      		showtoolbar: true,
      		rendertoolbar: rendertoolbar,
      		pageable: true,
      		sortable: true,
      		filterable: false,
      		editable: true,
      		url: '',
      		source : {
      			updateUrl : 'jqxGeneralServicer?jqaction=U&sname=updateKpiPolicy',
      			editColumns : "perfCriteriaPolicyId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
      		}
		};
		var column = [
			{datafield : 'criteriaId' , hidden : 'true'},
			{text : uiLabelMap.KpiPolicyId , width : '30%', datafield : 'perfCriteriaPolicyId', editable : false},
			{text : uiLabelMap.CommonFromDate, width : '30%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy',columntype: 'datetimeinput',
					createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
						editor.jqxDateTimeInput({width : cellwidth, height : cellheight});
					}
			},
			{text : uiLabelMap.CommonThruDate, width : '40%', datafield : 'thruDate', cellsformat : 'dd/MM/yyyy',columntype: 'datetimeinput',
					createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
						editor.jqxDateTimeInput({width : cellwidth, height : cellheight});
					}
			},
      ];
		var datafield  = [
			{name : 'criteriaId', type : 'string'},
			{name : 'perfCriteriaPolicyId', type : 'string'},
			{name : 'fromDate', type : 'date', other : 'Timestamp'},
			{name : 'thruDate', type : 'date', other : 'Timestamp'},
      ];
		Grid.initGrid(config, datafield, column, null, $('#setupPolicyGrid'));
	};
	
	var setData = function(data){
		refeshGrid(data.criteriaId);
		createKpiPolicyWindow.setId(data.criteriaId);
	};
	
	var refeshGrid = function(criteriaId){
		var src = $('#setupPolicyGrid').jqxGrid('source');
		src._source.url = "jqxGeneralServicer?sname=getListKpiPolicy&criteriaId=" + criteriaId;
		$('#setupPolicyGrid').jqxGrid('source', src);
	};
	
	var initWindow = function(){
		createJqxWindow($('#setupPolicyWindow'), 600, 375);
	};
	
	return {
		init : init,
		setData : setData
	}
}());

var editPolicyObject = (function(){
	var criteriaId = "";
	var perfCriteriaPolicyId = "";
	var init = function(){
		initGrid();
		initValidate();
		initNotification();
		initWindow();
		initBtnEvent();
	};
	
	var initNotification = function(){
		$('#jqxNotificationsetupPolicyGrid').jqxNotification({width: '100%',
			autoClose: true, template : 'info', appendContainer : "#containersetupPolicyGrid", opacity : 0.9});
		
		$('#jqxNotificationsetupGrid_edit').jqxNotification({width: '100%',
			autoClose: true, template : 'info', appendContainer : "#containersetupGrid_edit", opacity : 0.9});
	};
	
	var initWindow = function(){
		createJqxWindow($('#editPolicyWindow'), 700, 375);
	};
	
	
	var initGrid = function(){
		var rendertoolbar = function(toolbar){
			var jqxheader = $("<div id='toolbarcontainerPolicy_edit' class='widget-header'><h5><b>" + uiLabelMap.SetupKPIPolicy + "</b></h5><div id='toolbarButtonContainer_edit' class='pull-right'></div></div>");
			if(toolbar.children().length == 0)
			{
				toolbar.append(jqxheader);
				var container = $('#toolbarButtonContainer_edit');
				var grid = $("#setupGrid_edit");
				Grid.createAddRowButton(grid, container, uiLabelMap.CommonAddNew, {type: "popup", container: $("#setupKpiPolicyWindow")});
			}
			return toolbar;	
		};
		
		var config = {
				width: '100%', 
	      		height: 275,
	      		autoheight: false,
	      		virtualmode : true,
	      		showfilterrow: false,
	      		showtoolbar: true,
	      		rendertoolbar: rendertoolbar,
	      		pageable: true,
	      		sortable: false,
	      		filterable: false,
	      		editable: true,
	      		url : '',
	      		source : {
	      			updateUrl : 'jqxGeneralServicer?jqaction=U&sname=updateKpiPolicyitem',
	      			editColumns : "perfCriteriaPolicyId;fromRating(java.math.BigDecimal);toRating(java.math.BigDecimal);amount(java.math.BigDecimal);criteriaPolSeqId(java.lang.Long);kpiPolicyEnumId;"
	      		}
		};
		
		
		var column = [
	              {datafield : 'kpiPolicyEnumId', hidden : 'true'},
	              {datafield : 'criteriaPolSeqId', hidden : 'true'},
	              {text : uiLabelMap.KPIFromRating, datafield : 'fromRating', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
 	            	  	},
	            	  	cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
	            	  		if(newvalue < 0){
	            	  			return oldvalue;
	            	  		}
	            	  		return newvalue;
	            	  	}
	              },
	              {text : uiLabelMap.KPIToRating, datafield : 'toRating', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
	            	  	},
	            	  	cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
	            	  		if(newvalue < 0){
	            	  			return oldvalue;
	            	  		}
	            	  		return newvalue;
	            	  	}
	              },
	              {text : uiLabelMap.HRCommonAmount, datafield : 'amount', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
	            	  	},
	            	  	cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
	            	  		if(newvalue < 0){
	            	  			return oldvalue;
	            	  		}
	            	  		return newvalue;
	            	  	}
	              },
	              {text : uiLabelMap.RewardPunishment, datafield : 'description', width : '25%', columntype : 'dropdownlist',
            	  		createeditor : function(row, cellvalue, editor, celltext, cellwidth, cellheight){
            	  			editor.jqxDropDownList({source : globalVar.enumKpi, width : cellwidth,
            	  				height : cellheight, displayMember : 'description', valueMember : 'enumId'});
            	  		},
            	  		cellsrenderer : function(row, column, value){
            	  			if(value){
            	  				if(value == 'KPI_PUNISHMENT'){
            	  					return '<span>' + uiLabelMap.KpiPunishment + '</span>';
            	  				}else if(value == 'KPI_REWARD'){
            	  					return '<span>' + uiLabelMap.KpiReward + '</span>';
            	  				}else{
            	  					return '<span>' + value + '</span>';
            	  				}
            	  			}
            	  		},
            	  		cellendedit : function(row, datafield, columntype, oldvalue, newvalue){
            	  			var data = $('#setupGrid_edit').jqxGrid('getrowdata', row);
            	  			data.kpiPolicyEnumId = newvalue;
            	  		}
	              }
        ];
		var datafield = [
                 {name : 'perfCriteriaPolicyId', type : 'string'},
                 {name : 'fromRating', type : 'number'},
                 {name : 'toRating', type : 'number'},
                 {name : 'amount', type : 'number'},
                 {name : 'kpiPolicyEnumId', type : 'string'},
                 {name : 'description', type : 'string'},
                 {name : 'criteriaPolSeqId', type : 'number'}
         ];
		
		 Grid.initGrid(config, datafield, column, null, $('#setupGrid_edit'));
	};
	
	var refreshGridData = function(criteriaId, perfCriteriaPolicyId){
		var tmpSrc = $('#setupGrid_edit').jqxGrid('source');
		tmpSrc._source.url = "jqxGeneralServicer?sname=getKpiPolicyItem&criteriaId=" + criteriaId + "&perfCriteriaPolicyId=" + perfCriteriaPolicyId;
		$('#setupGrid_edit').jqxGrid('source', tmpSrc);
	};
	
	var initValidate = function(){
		$('#editPolicyWindow').jqxValidator({
			rules : [
			         {
			        	 input : '#pagersetupGrid_edit', message : uiLabelMap.FieldRequired,
			        	 rule : function(input, commit){
			        		 var data = $('#setupGrid_edit').jqxGrid('getrows');
			        		 if(!data.length){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         }
			         
         ]
		});
	};
	var validate = function(){
		return $('#editPolicyWindow').jqxValidator('validate');
	};
	
	
	var setData = function(data){
		refreshGridData(data.criteriaId, data.perfCriteriaPolicyId)
	};
	
	initBtnEvent = function(){
		$('#alterCancel_edit').click(function(){
			$('#editPolicyWindow').jqxWindow('close');
		})
	};
	
	var closeWindow = function(){
		$('#editPolicyWindow').jqxWindow('close');
	};
	
	var updateKpiPolicy = function(data){
		$.ajax({
			type : 'POST',
			data : {
				'setup' : JSON.stringify(data),
				'perfCriteriaPolicyId' : perfCriteriaPolicyId
			},
			url : 'updateKpiPolicy',
			datatype : 'json',
			success : function(response){
				if(response.responseMessage == 'success'){
					var success = uiLabelMap.updateSuccessfully;
					$("#jqxNotificationsetupGrid_edit").jqxNotification('closeLast');
					$("#notificationContentsetupGrid_edit").text(success);
					$("#jqxNotificationsetupGrid_edit").jqxNotification('open');
					if(data.flag == 'save'){
						$('#setupKpiPolicyWindow').jqxWindow('close');
					}
					$('#jqxgrid').jqxGrid('updatebounddata');
					$('#setupGrid_edit').jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage, [{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}])
				}
			}
		})
	};
	
	var setId = function(criteriaIdTmp, perfCriteriaPolicyIdTmp){
		criteriaId = criteriaIdTmp;
		perfCriteriaPolicyId = perfCriteriaPolicyIdTmp;
	};
	
	
	return {
		init : init,
		validate : validate,
		setData : setData,
		setId : setId,
		updateKpiPolicy : updateKpiPolicy
	}
}());

$(document).ready(function(){
	createKpiPolicyWindow.init();
	contextMenuObject.init();
	setupPolicyGrid.init();
	editPolicyObject.init();
})