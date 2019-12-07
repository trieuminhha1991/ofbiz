<div id="alterpopupWindow" class='hide'>
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='form-window-content'>
		   		<#if typePayment?exists>
			   		<#if typePayment == "AP">
			   			<div class='row-fluid margin-bottom10'>
			   				<div class='span5 text-algin-right'>
				   				<label class="asterisk">${uiLabelMap.accAccountingFromParty}</label>
							</div> 
							<div class="span7">
								<div id="jqxdropdownbuttonToParty" data-grid="jqxgridToParty">
									<div id="jqxgridToParty"></div>
								</div>
			   				</div>
		   				</div>	
		   				<div class='row-fluid margin-bottom10'>
					   		<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.accAccountingToParty}</label>
							</div>  
							<div class="span7">
								<div id="organizationPartyId"></div>
					   		</div>
					   	</div>
			   		</#if>
			   		<#if typePayment == "AR">
			   			<div class='row-fluid margin-bottom10'>
			   				<div class='span5 text-algin-right'>
				   				<label class="asterisk">${uiLabelMap.accAccountingFromParty}</label>
							</div> 
							<div class="span7">
								<div id="organizationPartyId"></div>
			   				</div>
		   				</div>	
		   				<div class='row-fluid margin-bottom10'>
					   		<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.accAccountingToParty}</label>
							</div>  
							<div class="span7">
								<div id="jqxdropdownbuttonToParty" data-grid="jqxgridToParty">
									<div id="jqxgridToParty"></div>
								</div>
					   		</div>
					   	</div>
			   		</#if>
		   		</#if>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.AccountingPaymentType}</label>
				</div>  
				<div class="span7">
					<div id="paymentTypeId"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class='asterisk'>${uiLabelMap.AccountingPaymentMethodId}</label>
				</div>  
				<div class="span7">
					<div id="paymentMethodId"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label>${uiLabelMap.paymentRefNum}</label>
				</div>  
				<div class="span7">
					<div id="paymentRefNum"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label>${uiLabelMap.overrideGlAccountId}</label>
				</div>  
				<div class="span7">
					<div id="jqxdropdownbuttonoverrideGlAccountId">
						<div id="jqxgridoverrideGlAccountId"></div>
					</div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.apAmount}</label>
				</div>  
				<div class="span7">
					<div id="amount"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label>${uiLabelMap.finAccountId}</label>
				</div>  
				<div class="span7">
					<div id="finAccountId"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label>${uiLabelMap.description}</label>
				</div>  
				<div class="span7">
					<textarea id="comments" class="text-popup"></textarea>
		   		</div>
		   	</div>
    	</div>
    	<div class="form-action">
			<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>		
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript">
	<#assign listGlType = delegator.findByAnd("GlAccountType",null,null,false) !>
	<#assign listGlClass = delegator.findByAnd("GlAccountClass",null,null,false) !>
	var listGlType = [
		<#list listGlType as type>
		{
			'glAccountTypeId' : '${type.glAccountTypeId?if_exists}',
			'description' : "${StringUtil.wrapString(type.get('description',locale))}"
		},
		</#list>
	];
	
	var listGlClass = [
		<#list listGlClass as type>
		{
			'glAccountClassId' : '${type.glAccountClassId?if_exists}',
			'description' : "${StringUtil.wrapString(type.get('description',locale))}"
		},
		</#list>
	];
	var action = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;  
		var width = 280;
	    var initElement = function(){
	    	$("#organizationPartyId").jqxDropDownList({ theme: theme,  width:width,autoDropDownHeight : true, dropDownWidth: 500, source: dataPAGA, displayMember: "groupName", valueMember: "partyId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#paymentTypeId").jqxDropDownList({ theme: theme, width:width, source: dataPTE, displayMember: "description", valueMember: "paymentTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#paymentMethodId").jqxDropDownList({ theme: theme, width:width,source: dataPM, displayMember: "description", valueMember: "paymentMethodId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			$("#finAccountId").jqxDropDownList({ theme: theme, width:width, dropDownWidth: 500, source: dataFA, displayMember: "description", valueMember: "finAccountId",placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
		    $("#paymentRefNum").jqxNumberInput({ width:  width, decimalDigits: 0, spinButtons: false, min: 0});
		    $("#amount").jqxNumberInput({ width: width,  max : 999999999999999999, digits: 18, decimalDigits:0, spinButtons: false, min: 0});
		    $("#comments").width(width - 10);
		    initPartyDropDown();
		    initAccountDropDown();
	    };
	    var initPartyDropDown = function(){
	    	var datafields = [{ name: 'partyId', type: 'string' },
        		{ name: 'partyTypeId', type: 'string' },
            	{ name: 'firstName', type: 'string' },
            	{ name: 'lastName', type: 'string' },
            	{ name: 'groupName', type: 'string' }];
            var columns = [{ text: '${uiLabelMap.accApInvoice_partyId}', datafield: 'partyId', width: 200, pinned: true},
				{ text: '${uiLabelMap.accAccountingFromParty}', datafield: 'groupName', width: 200},
				{ text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width: 200, 
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
						var first = rowdata.firstName ? rowdata.firstName : "";
						var last = rowdata.lastName ? rowdata.lastName : "";
						return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
					}
				},
				{ text: '${uiLabelMap.accApInvoice_partyTypeId}', datafield: 'partyTypeId', width: 200, 
					cellsrenderer: function(row, columns, value){
						var group = "${uiLabelMap.PartyGroup}";
						var person = "${uiLabelMap.Person}";
						if(value == "PARTY_GROUP"){
							return "<div class='custom-cell-grid'>"+group+"</div>";
						}else if(value == "PERSON"){
							return "<div class='custom-cell-grid'>"+person+"</div>";
						}
						return value;
					}
				}];
			GridUtils.initDropDownButton({url: "getFromParty", autorowheight: true, filterable: true, dropdown:{width: 280}, source:{pagesize: 5}},
										datafields,columns, null, $("#jqxgridToParty"), $("#jqxdropdownbuttonToParty"), "partyId");
	    };
	    var initAccountDropDown = function(){
	    	var datafields = [{ name: 'glAccountId', type: 'string'},
	            { name: 'accountName', type: 'string' },
	            { name: 'glAccountTypeId', type: 'string' },
	            { name: 'glAccountClassId', type: 'string' }];
            var columns = [{ text: '${uiLabelMap.FormFieldTitle_glAccountId}', datafield: 'glAccountId', width: 100},
	            { text: '${uiLabelMap.FormFieldTitle_accountName}', datafield: 'accountName', width: 150},
	            { text: '${uiLabelMap.FormFieldTitle_glAccountTypeId}', datafield: 'glAccountTypeId', width: 200,filtertype : 'checkedlist',cellsrenderer : function(row){
	            	var data = $("#jqxgridoverrideGlAccountId").jqxGrid('getrowdata',row);
	            	for(var key in listGlType){
	            		if(listGlType[key].glAccountTypeId == data.glAccountTypeId){
	            			return '<span>'+ listGlType[key].description  +'</span>';
	            		}
	            	}
	            	return '<span>'+data.glAccountTypeId  +'</span>';
	            },createfilterwidget: function (column, columnElement, widget) {
			   				var filterBoxAdapterSI = new $.jqx.dataAdapter(listGlType,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsSI = filterBoxAdapterSI.records;
			   				uniqueRecordsSI.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecordsSI,dropDownHeight : 200, displayMember: 'description', valueMember : 'glAccountTypeId'});
			   			}},
	            { text: '${uiLabelMap.FormFieldTitle_glAccountClassId}', datafield: 'glAccountClassId', filtertype : 'checkedlist',width: 150,cellsrenderer : function(row){
	            	var data = $("#jqxgridoverrideGlAccountId").jqxGrid('getrowdata',row);
	            	for(var key in listGlClass){
	            		if(listGlClass[key].glAccountClassId == data.glAccountClassId){
	            			return '<span>'+ listGlClass[key].description  +'</span>';
	            		}
	            	}
	            	return '<span>'+data.glAccountClassId  +'</span>';
	            },createfilterwidget: function (column, columnElement, widget) {
			   				var filterBoxAdapterSI = new $.jqx.dataAdapter(listGlClass,
			                {
			                    autoBind: true
			                });
			                var uniqueRecordsSI = filterBoxAdapterSI.records;
			   				uniqueRecordsSI.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
			   				widget.jqxDropDownList({ source: uniqueRecordsSI,dropDownHeight : 200, displayMember: 'description', valueMember : 'glAccountClassId'});
			   			}}];
	            
	        GridUtils.initDropDownButton({url: "JQGetListGLAccounts", autorowheight: true, filterable: true, dropdown:{width: 280}, source:{pagesize: 5}},datafields,columns, null, $("#jqxgridoverrideGlAccountId"),$("#jqxdropdownbuttonoverrideGlAccountId"), "glAccountId");
	    };
	    var initWindow = function(){
	    	$("#alterpopupWindow").jqxWindow({
		        width: 700, height: 520, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
		    });
	    };
	    var initDropDownButton = function(config, datafields, columns, formatData, sortcolumn, grid, dropdown, key){
		    var sourceG = initSource(config.url, datafields, sortcolumn, grid);
		    var dataAdapterG = initDataAdapter(sourceG, formatData);
		    dropdown.jqxDropDownButton({theme: theme,  width: width});
		    initGrid(config, datafields, columns, formatData, sortcolumn, grid);
		    grid.on('rowselect', function (event) {
		        var args = event.args;
		        var row = grid.jqxGrid('getrowdata', args.rowindex);
		        if(row) $('#alterpopupWindow').jqxValidator('hideHint','#jqxdropdownbuttonToParty');
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row[key] +'</div>';
		        dropdown.jqxDropDownButton('setContent', dropDownContent);
		        dropdown.jqxDropDownButton("close");
		    });
	    };
	    var initGrid = function(config, datafields, columns, formatData, sortcolumn, grid){
	    	var sourceConfig = config.source ? config.source : {};
		    var sourceG = initSource(sourceConfig, config.url, datafields, sortcolumn, grid);
		    var dataAdapterG = initDataAdapter(sourceG, formatData);
		    grid.jqxGrid({
		    	width: config.width ? config.width : 600,
		        source: dataAdapterG,
		        showfilterrow: true,
		        filterable: config.filterable ? config.filterable : false,
		        virtualmode: config.virtualmode ? config.virtualmode : true, 
		        autorowheight: config.autorowheight ? config.autorowheight : false,
		        sortable: config.sortable ? config.sortable : true,
		        editable: config.editable ? config.editable : false,
		        theme: theme,
		        autoheight:true,
		        pageable: true,
		        rendergridrows: function(obj)
				{
					return obj.data;
				},
		        columns: columns
		    });
	    };
	    var initSource = function(config, url, datafields, sorcolumn, grid){
	    	var sourceG = {
		        datafields: datafields,
		        cache: config.cache ? config.cache : false,
		        root: 'results',
		        datatype: "json",
		        updaterow: function (rowid, rowdata) {
		            // synchronize with the server - send update command   
		        },
		        beforeprocessing: function (data) {
		            sourceG.totalrecords = data.TotalRows;
		        },
		        filter: function () {
		            // update the grid and send a request to the server.
		            grid.jqxGrid('updatebounddata');
		        },
		        pager: function (pagenum, pagesize, oldpagenum) {
		            // callback called when a page or page size is changed.
		        },
		        sort: function () {
		            grid.jqxGrid('updatebounddata');
		        },
		        sortcolumn: sorcolumn,
				sortdirection: 'asc',
		        type: 'POST',
		        data: {
			        noConditionFind: 'Y',
			        conditionsFind: 'N',
			    },
			    pagesize: config.pagesize ? config.pagesize : 5,
		        contentType: 'application/x-www-form-urlencoded',
		        url: 'jqxGeneralServicer?sname=' + url,
		    };
		    return sourceG;
	    };
	    var initDataAdapter = function(sourceG, formatData){
	    	var defaultFormat = function (data) {
	    		if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
       		};
	    	var format = typeof(formatData) == "function" ? formatData : defaultFormat;
	    	var dataAdapterG = new $.jqx.dataAdapter(sourceG,
		    {
		    	formatData: format,
		        loadError: function (xhr, status, error) {
		            alert(error);
		        },
		        downloadComplete: function (data, status, xhr) {
		                if (!sourceG.totalRecords) {
		                    sourceG.totalRecords = parseInt(data["odata.count"]);
		                }
		        }, 
		        beforeLoadComplete: function (records) {
		        	for (var i = 0; i < records.length; i++) {
		        		if(typeof(records[i])=="object"){
		        			for(var key in records[i]) {
		        				var value = records[i][key];
		        				if(value != null && typeof(value) == "object" && typeof(value) != null){
		        					var date = new Date(records[i][key]["time"]);
		        					records[i][key] = date;
		        				}
		        			}
		        		}
		        	}
		        }
		    });
		    return dataAdapterG;
	    };
	    
	    var initRule = function(){
	    	$('#alterpopupWindow').jqxValidator({
		        rules: [{
		        	input: "#organizationPartyId", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#jqxdropdownbuttonToParty", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = $("#jqxgridToParty").jqxGrid('getselectedrowindex');
		                return index != -1;
		            }
		   		},{
		        	input: "#paymentTypeId", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#paymentMethodId", message: "${uiLabelMap.CommonRequired}", action: 'change,blur', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{ 
		   			input: '#amount', message: '${uiLabelMap.CommonRequired}', action: 'keyup, blur', 
		   			rule: function (input, commit) {
		                var value = input.jqxNumberInput('val');
		                if(!isNaN(value) && value){
		                	return true; 
		                }
		                return false;
		            } 
		   		}]
		    });
	    };  
	    var bindEvent = function(){
	    	$("#save").click(function () {
			    if(!action.save()){
			    	return;
			    }
		        $("#alterpopupWindow").jqxWindow('close');
		    });
		    $("#saveAndContinue").click(function () {
			    action.save()
		    });	
		    $("#cancel").click(function(){
				$('#alterpopupWindow').jqxValidator('hide');
			});
			
			$('#alterpopupWindow').on('close',function(){
				GridUtils.clearForm($('#alterpopupWindow'));
				$('#alterpopupWindow').jqxValidator('hide');
			})
	    };
	    var save = function(){
	    	if(!$('#alterpopupWindow').jqxValidator("validate")){
	    		return false;
	    	}
	    	var row = { 
		    	<#if typePayment?exists && typePayment="AP">
		    		partyIdFrom:$('#organizationPartyId').val(),
		    		partyIdTo:$('#jqxdropdownbuttonToParty').val(),
		    	</#if>
		    	<#if typePayment?exists && typePayment="AR">
		    		partyIdFrom:$('#jqxdropdownbuttonToParty').val(),
		    		partyIdTo:$('#organizationPartyId').val(),
		    	</#if>
				paymentTypeId:$('#paymentTypeId').val(),
				paymentMethodId:$('#paymentMethodId').val(),
				paymentRefNum:$('#paymentRefNum').val(),
				overrideGlAccountId:$('#jqxdropdownbuttonoverrideGlAccountId').val(),
				amount:$('#amount').val(),
				comments: $('#comments').val(),            
		  	};
		  	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        return true;
	    };
	    
	    return {
	    	init: function(){
	    		initElement();
	    		initWindow();
	    		initRule();
	    		bindEvent();
	    	},
	    	save: save,
	    	initGrid: initGrid
	    };
	}());
	$(document).ready(function(){
		action.init();
	});
</script>	