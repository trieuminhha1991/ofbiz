<#assign dataField = "[{name: 'productStoreId', type: 'string'}, 
						{name: 'prodCatalogId', type: 'string'}, 
						{name: 'catalogName', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}
						]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DACatalogId)}', dataField: 'prodCatalogId', width: '16%'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DACatalogName)}', dataField: 'catalogName'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '16%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '16%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', dataField: 'status', width: '12%',
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if (data != null && data.thruDate != null && data.thruDate != undefined) {
									var thruDate = new Date(data.thruDate);
									var nowDate = new Date('${nowTimestamp}');
									if (thruDate < nowDate) {
										return '<span title=\"${uiLabelMap.DAExpired}\">${uiLabelMap.DAExpired}</span>';
									}
								}
	    						return '<span></span>';
							}, 
						}, 
						"/>
<#assign tmpEditUrl = "icon-edit open-sans@${uiLabelMap.DAEditRowSelected}@javascript:editProductStoreCatalog();"/>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addrow="true" addType="popup" addrefresh="true" 
		createUrl="jqxGeneralServicer?sname=createProductStoreCatalogJQ&jqaction=C" addColumns="productStoreId;prodCatalogId;fromDate;thruDate" 
		removeUrl="jqxGeneralServicer?sname=deleteProductStoreCatalogJQ&jqaction=C" deleteColumn="productStoreId;prodCatalogId;fromDate(java.sql.Timestamp)" jqGridMinimumLibEnable="true" deleterow="true" 
		url="jqxGeneralServicer?sname=JQGetListProductStoreCatalog&productStoreId=${productStore.productStoreId?if_exists}" mouseRightMenu="true" contextMenuId="contextMenu" 
		customcontrol1=tmpEditUrl/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>

<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.DAAddProductCatalogIntoProductStore}</div>
	<div style="overflow: hidden;">
		<input type="hidden" value="${productStore.productStoreId?if_exists}"/>
		<div class='form-window-content'>
    		<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="required" for="prodCatalogId">${uiLabelMap.DACatalogId}</label>
				</div>  
				<div class="span7">
					<div id="prodCatalogId">
			       	 	<div id="jqxgridProductCatalog"></div>
			       	</div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="required" for="fromDate">${uiLabelMap.DAFromDate}</label>
				</div>  
				<div class="span7">
					<div id="fromDate" name="fromDate"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label for="thruDate">${uiLabelMap.DAThruDate}</label>
				</div>  
				<div class="span7">
					<div id="thruDate" name="thruDate"></div>
		   		</div>
		   	</div>
		</div>
		<div class="form-action">
			<button id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div id="alterpopupWindowEdit" class="hide">
	<div>${uiLabelMap.DAEditProductStore}</div>
	<div style="overflow: hidden;">
		<input type="hidden" value="${productStore.productStoreId?if_exists}"/>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="required" for="prodCatalogId">${uiLabelMap.DACatalogId}</label>
				</div>  
				<div class="span7">
					<input id="e_prodCatalogId" name="prodCatalogId" value=""/>
		   		</div>
		   	</div>
			<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="required" for="fromDate">${uiLabelMap.DAFromDate}</label>
				</div>  
				<div class="span7">
					<div id="e_fromDate" name="fromDate"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label for="thruDate">${uiLabelMap.DAThruDate}</label>
				</div>  
				<div class="span7">
					<div id="e_thruDate" name="thruDate"></div>
		   		</div>
		   	</div>
	   	</div>
		<div class="form-action">
			<button id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
    	<div class="control-group" style="margin:0 !important; font-size:9pt;height: 25px;">
			<span style="color:#F00" id="e_status"></span>
		</div>
    </div>
</div>

<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	function editProductStoreCatalog() {
		var indexSeleted = $("#jqxgrid").jqxGrid('getselectedrowindex');
		if (indexSeleted == null || indexSeleted == undefined || indexSeleted < 0) {
			var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseRow}!</span>";
			bootbox.dialog(message0, [{
				"label" : "OK",
				"class" : "btn-mini btn-primary width60px",
				}]
			);
			return false;
		}
		
		var data = $("#jqxgrid").jqxGrid("getrowdata", indexSeleted);
		if (data != null) {
			if (data.prodCatalogId != null) $("#e_prodCatalogId").val(data.prodCatalogId);
			if (data.fromDate != null) $("#e_fromDate").jqxDateTimeInput("setDate", data.fromDate);
			if (data.thruDate != null) {
				$("#e_thruDate").jqxDateTimeInput("setDate", data.thruDate);
				var thruDate0 = new Date($("#e_thruDate").jqxDateTimeInput("getDate"));
				var nowDate0 = new Date("${nowTimestamp}");
				if (thruDate0 < nowDate0) {
					$("#e_thruDate").jqxDateTimeInput({disabled: true});
					$("#alterCancel2").jqxButton({disabled: true});
	    			$("#alterSave2").jqxButton({disabled: true});
	    			$("#e_status").html("<i>(${uiLabelMap.DAThisRecordWasExpired})</i>");
				}
			}
			$("#alterpopupWindowEdit").jqxWindow("open");
		}
	}
	
	function resetContentWindowEdit() {
		$('#e_prodCatalogId').val("");
		$('#e_fromDate').val("");
		$("#e_thruDate").val("");
		$("#e_thruDate").jqxDateTimeInput({disabled: false});
		$("#alterCancel2").jqxButton({disabled: false});
		$("#alterSave2").jqxButton({disabled: false});
		$("#e_status").html("");
	}
	
	$(function(){
	    $("#fromDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss'});
	    $("#fromDate").jqxDateTimeInput('setDate', '${nowTimestamp}');
	    $("#thruDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null});
	    $("#alterpopupWindow").jqxWindow({width: 600, height: 240, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, theme:theme});
	    $("#alterpopupWindowEdit").jqxWindow({width: 600, height: 240, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, theme:theme});
	    $("#alterCancel2").jqxButton({theme: theme});
	    $("#alterSave2").jqxButton({theme: theme});
	    $("#e_fromDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null, disabled: true});
	    $("#e_thruDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null});
	    $("#e_prodCatalogId").jqxInput({width:212, height: 25, disabled: true});
	    
	    $('#alterpopupWindowEdit').on('close', function (event) {
	    	resetContentWindowEdit();
	    });
	    
	    $("#alterSave1").on("click", function(){
	    	if($('#alterpopupWindow').jqxValidator('validate')){
		    	var row;
		    	var fromDate;
		    	var fromDateStr = $("#fromDate").jqxDateTimeInput('getDate');
		    	if (fromDateStr != null) {
		    		fromDate = fromDateStr.getTime();
		    	}
		    	var thruDate;
		    	var thruDateStr = $("#thruDate").jqxDateTimeInput('getDate');
		    	if (thruDateStr != null) {
		    		thruDate = thruDateStr.getTime();
		    	}
		        row = { productStoreId: '${productStore.productStoreId?if_exists}',
		        		prodCatalogId: $('#prodCatalogId').val(),
		        		fromDate: fromDate,
		        		thruDate: thruDate
		        	  };
			   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');          
		        $("#jqxgrid").jqxGrid('selectRow', 0);
		        $("#alterpopupWindow").jqxWindow('close');
		        
		        // reset value on window
				$('#prodCatalogId').val("");
				$('#fromDate').val("");
				$("#thruDate").val("");
	        }else{
	        	return;
	        }
	    });
	    
	    $("#alterSave2").on("click", function(){
	    	if($('#alterpopupWindowEdit').jqxValidator('validate')){
		    	var row;
		    	var fromDate;
		    	var fromDateStr = $("#e_fromDate").jqxDateTimeInput('getDate');
		    	if (fromDateStr != null) {
		    		fromDate = fromDateStr.getTime();
		    	}
		    	var thruDate;
		    	var thruDateStr = $("#e_thruDate").jqxDateTimeInput('getDate');
		    	if (thruDateStr != null) {
		    		thruDate = thruDateStr.getTime();
		    	}
		        row = { productStoreId: '${productStore.productStoreId?if_exists}',
		        		prodCatalogId: $('#e_prodCatalogId').val(),
		        		fromDate: fromDate,
		        		thruDate: thruDate
		        	  };
		       	
		       	$.ajax({
	                type: "POST",                        
	                url: "updateProductStoreCatalogAjax",
	                data: row,
	                success: function (data, status, xhr) {
	                    // update command is executed.
	                    if(data.responseMessage == "error"){
	                        //if(commit){commit(false)}
                            $('#jqxgrid').jqxGrid('updatebounddata');
	                        $('#container').empty();
	                        $('#jqxNotification').jqxNotification({ template: 'error'});
	                        $("#notificationContent").text(data.errorMessage);
	                        $("#jqxNotification").jqxNotification("open");
	                    }else{
	                        //if(commit){commit(true)}
	                        $('#container').empty();
	                        $('#jqxNotification').jqxNotification({ template: 'success'});
	                        $("#notificationContent").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	                        $("#jqxNotification").jqxNotification("open");
                            $('#jqxgrid').jqxGrid('updatebounddata');
	                    }
	                },
	                error: function () {
	                    //if(commit){commit(false)}
	                }
	            });
		        $("#alterpopupWindowEdit").jqxWindow('close');
		        
		        // reset value on window
				resetContentWindowEdit();
	        }else{
	        	return;
	        }
	    });
	    
	    // list product catalog id =======================================================================
	    // Product catalog JQX Dropdown
	    var sourceP2 =
	    {
	        datafields:[{name: 'prodCatalogId', type: 'string'},
	            		{name: 'catalogName', type: 'string'}
        			],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	            // synchronize with the server - send update command   
	        },
	        beforeprocessing: function (data) {
	            sourceP2.totalrecords = data.TotalRows;
	        },
	        filter: function () {
	            // update the grid and send a request to the server.
	            $("#jqxgridProductCatalog").jqxGrid('updatebounddata');
	        },
	        pager: function (pagenum, pagesize, oldpagenum) {
	            // callback called when a page or page size is changed.
	        },
	        sort: function () {
	            $("#jqxgridProductCatalog").jqxGrid('updatebounddata');
	        },
	        sortcolumn: 'prodCatalogId',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQGetListProductCatalog',
	    };
	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
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
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourceP2.totalRecords) {
	                    sourceP2.totalRecords = parseInt(data["odata.count"]);
	                }
	        }, 
	        beforeLoadComplete: function (records) {
	        	for (var i = 0; i < records.length; i++) {
	        		if(typeof(records[i])=="object"){
	        			for(var key in records[i]) {
	        				var value = records[i][key];
	        				if(value != null && typeof(value) == "object" && typeof(value) != null){
	        					//var date = new Date(records[i][key]["time"]);
	        					//records[i][key] = date;
	        				}
	        			}
	        		}
	        	}
	        }
	    });
	    $("#prodCatalogId").jqxDropDownButton({ theme: theme, width: 218, height: 25});
	    $("#jqxgridProductCatalog").jqxGrid({
	    	width:450,
	        source: dataAdapterP2,
	        filterable: true,
	        columnsresize: true, 
	        showfilterrow: true,
	        virtualmode: true, 
	        sortable:true,
	        theme: theme,
	        editable: false,
	        autoheight:true,
	        pageable: true,
	        rendergridrows: function(obj){
				return obj.data;
			},
	        columns: [{text: '${uiLabelMap.DACatalogId}', datafield: 'prodCatalogId', width:'42%'},
	          			{text: '${uiLabelMap.DACatalogName}', datafield: 'catalogName'}
	        		]
	    });
	    $("#jqxgridProductCatalog").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxgridProductCatalog").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['prodCatalogId'] +'</div>';
	        $('#prodCatalogId').jqxDropDownButton('setContent', dropDownContent);
	    });
	    
	    // Validate =======================================================================
	    $('#alterpopupWindow').jqxValidator({
	        rules: [
	        	{input: '#prodCatalogId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($(input).jqxDateTimeInput('getDate') == null || $(input).jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						now.setHours(0,0,0,0);
		        		if($(input).jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				},
				{input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).jqxDateTimeInput('getDate');
						if(value != null && !(/^\s*$/.test(value))){
							var now = new Date();
							now.setHours(0,0,0,0);
			        		if (value < now) return false;
		        		}
		        		return true;
		    		}
				},]
	    });
	    
	    $('#alterpopupWindowEdit').jqxValidator({
	        rules: [
	        	{input: '#e_prodCatalogId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#e_fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($(input).jqxDateTimeInput('getDate') == null || $(input).jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#e_thruDate', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).jqxDateTimeInput('getDate');
						if(value != null && !(/^\s*$/.test(value))){
							var now = new Date();
							now.setHours(0,0,0,0);
			        		if (value < now) return false;
		        		}
		        		return true;
		    		}
				},]
	    });
	});
</script>

<#--
{input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}', action: 'blur', rule: 
	function (input, commit) {
		var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
		if(input.jqxDateTimeInput('getDate') != null && input.jqxDateTimeInput('getDate') != undefined && (input.jqxDateTimeInput('getDate') < fromDate)){
			return false;
		}
		return true;
	}
}
-->