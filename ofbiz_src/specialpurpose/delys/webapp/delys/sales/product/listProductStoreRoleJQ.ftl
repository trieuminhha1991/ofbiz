<#assign listRoleType = Static["com.olbius.util.SalesPartyUtil"].getListRoleTypeProductStoreRole(delegator)/>
<script type="text/javascript">
	<#if listRoleType?exists>
		var roleTypeData = [
			<#list listRoleType as roleTypeItem>
				{
					roleTypeId: "${roleTypeItem.roleTypeId}",
					description: "${StringUtil.wrapString(roleTypeItem.get("description", locale))}"
				},
			</#list>
		];
	<#else>
		var roleTypeData = [];
	</#if>
</script>

<#assign dataField = "[{name: 'productStoreId', type: 'string'}, 
						{name: 'partyId', type: 'string'}, 
						{name: 'fullName', type: 'string'}, 
						{name: 'roleTypeId', type: 'string'}, 
						{name: 'fromDate', type: 'date', other: 'Timestamp'}, 
						{name: 'thruDate', type: 'date', other: 'Timestamp'}
						]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DAPartyId)}', dataField: 'partyId', width: '16%',}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAFullName)}', dataField: 'fullName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DARoleType)}', dataField: 'roleTypeId', width: '16%', filtertype: 'checkedlist',
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < roleTypeData.length; i++){
	    							if (value == roleTypeData[i].roleTypeId){
	    								return '<span title = ' + roleTypeData[i].description +'>' + roleTypeData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
							}, 
							createfilterwidget: function (column, columnElement, widget) {
				   				var filterBoxAdapter2 = new $.jqx.dataAdapter(roleTypeData,
				                {
				                    autoBind: true
				                });
				                var uniqueRecords2 = filterBoxAdapter2.records;
				   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
				   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'roleTypeId', renderer: function (index, label, value) 
								{
									for(i=0;i < roleTypeData.length; i++){
										if(roleTypeData[i].roleTypeId == value){
											return roleTypeData[i].description;
										}
									}
								    return value;
								}});
								widget.jqxDropDownList('checkAll');
				   			}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '14%', cellsformat: 'dd/MM/yyyy - HH:mm:ss'}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', dataField: 'status', width: '8%',
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
<#assign tmpEditUrl = "icon-edit open-sans@${uiLabelMap.DAEditRowSelected}@javascript:editProductStoreRole();"/>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" filtersimplemode="true" showstatusbar="false" addrow="true" addType="popup" addrefresh="true" 
		createUrl="jqxGeneralServicer?sname=createProductStoreRoleJQ&jqaction=C" addColumns="productStoreId;partyId;roleTypeId;fromDate;thruDate" 
		removeUrl="jqxGeneralServicer?sname=removeProductStoreRoleJQ&jqaction=C" deleteColumn="productStoreId;partyId;roleTypeId;fromDate(java.sql.Timestamp)" jqGridMinimumLibEnable="true" deleterow="true" 
		url="jqxGeneralServicer?sname=JQGetListProductStoreRole&productStoreId=${productStore.productStoreId?if_exists}" mouseRightMenu="true" contextMenuId="contextMenu" 
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

<form id="alterpopupWindowform" name="alterpopupWindowform" class="form-horizontal form-table-block" method="post" action="<@ofbizUrl>storeCreateRole</@ofbizUrl>">
	<div id="alterpopupWindow" style="display:none">
		<div>${uiLabelMap.DACreateNewProductStore}</div>
		<div class="form-horizontal form-table-block">
			<input type="hidden" value="${productStore.productStoreId?if_exists}"/>
	    	<div class="row-fluid form-window-content">
	    		<div class="span12">
	    			<div class="row-fluid margin-bottom10">
		    			<div class='span5 align-right asterisk'>
							${uiLabelMap.DAPartyId}
				        </div>
	    				<div class="span7">
    						<div id="partyId" name="partyId">
    							<div id="partyIdGrid"></div>
    						</div>
	    				</div>
	    			</div>
	    			<div class="row-fluid margin-bottom10">
		    			<div class='span5 align-right asterisk'>
							${uiLabelMap.DARoleTypeId}
				        </div>
	    				<div class="span7">
    						<div id="roleTypeId" name="roleTypeId"></div>
	    				</div>
	    			</div>
	    			<div class="row-fluid margin-bottom10">
		    			<div class='span5 align-right asterisk'>
							${uiLabelMap.DAFromDate}
				        </div>
	    				<div class="span7">
    						<div id="fromDate" name="fromDate"></div>
	    				</div>
	    			</div>
	    			<div class="row-fluid margin-bottom10">
		    			<div class='span5 align-right asterisk'>
							${uiLabelMap.DAThruDate}
				        </div>
	    				<div class="span7">
    						<div id="thruDate" name="thruDate"></div>
	    				</div>
	    			</div>
	    		</div>
	    	</div><!--.row-fluid-->
	    	<#--<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label"></label>
	    				<div class="controls">
    						<input type="button" id="alterSave4" value="${uiLabelMap.CommonSave}"/>
							<input type="button" id="alterCancel4" value="${uiLabelMap.CommonCancel}"/>
	    				</div>
	    			</div>
	    		</div>
	    	</div>-->
	    	<div class="form-action">
		    	<div class='row-fluid'>
		    		<div class="span12 margin-top10">
		    			<button id="alterCancel4" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
		    			<button id="alterSave4" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		    		</div>
		    	</div>
		    </div>
	    </div>
	</div>		
</form>
<#--<div class="form-action">
	<div class='row-fluid'>
		<div class="span12 margin-top10">
			<button id="alterCancel4" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button id="alterSave4" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
		</div>
	</div>
</div>-->

<form id="alterpopupWindowEditform" name="alterpopupWindowEditform" class="form-horizontal form-table-block" method="post" action="<@ofbizUrl>storeCreateRole</@ofbizUrl>">
	<div id="alterpopupWindowEdit" style="display:none">
		<div>${uiLabelMap.DAEditProductStore}</div>
		<div class="form-horizontal form-table-block">
			<input type="hidden" value="${productStore.productStoreId?if_exists}"/>
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label required" for="partyId">${uiLabelMap.DAPartyId}</label>
	    				<div class="controls">
    						<input id="e_partyId" name="partyId" value=""/>
	    				</div>
	    			</div>
	    			<div class="control-group">
	    				<label class="control-label required" for="roleTypeId">${uiLabelMap.DARoleTypeId}</label>
	    				<div class="controls">
    						<input id="e_roleTypeId" name="roleTypeId" value=""/>
	    				</div>
	    			</div>
	    			<div class="control-group">
	    				<label class="control-label required" for="fromDate">${uiLabelMap.DAFromDate}</label>
	    				<div class="controls">
    						<div id="e_fromDate" name="fromDate"></div>
	    				</div>
	    			</div>
	    			<div class="control-group">
	    				<label class="control-label" for="thruDate">${uiLabelMap.DAThruDate}</label>
	    				<div class="controls">
    						<div id="e_thruDate" name="thruDate"></div>
	    				</div>
	    			</div>
	    		</div>
	    	</div><!--.row-fluid-->
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label"></label>
	    				<div class="controls">
    						<input type="button" id="alterSave5" value="${uiLabelMap.CommonSave}"/>
							<input type="button" id="alterCancel5" value="${uiLabelMap.CommonCancel}"/>
	    				</div>
	    			</div>
	    		</div>
	    	</div>
	    	<div class="control-group" style="margin:0 !important; font-size:9pt;height: 25px;">
				<span style="color:#F00" id="e_status"></span>
			</div>
	    </div>
	</div>		
</form>
<script src="/delys/images/js/generalUtils.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript">
	function editProductStoreRole() {
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
			if (data.partyId != null) $("#e_partyId").val(data.partyId);
			if (data.roleTypeId != null) $("#e_roleTypeId").val(data.roleTypeId);
			if (data.fromDate != null) $("#e_fromDate").jqxDateTimeInput("setDate", data.fromDate);
			if (data.thruDate != null) {
				$("#e_thruDate").jqxDateTimeInput("setDate", data.thruDate);
				var thruDate0 = new Date($("#e_thruDate").jqxDateTimeInput("getDate"));
				var nowDate0 = new Date("${nowTimestamp}");
				if (thruDate0 < nowDate0) {
					$("#e_thruDate").jqxDateTimeInput({disabled: true});
					$("#alterCancel5").jqxButton({disabled: true});
	    			$("#alterSave5").jqxButton({disabled: true});
	    			$("#e_status").html("<i>(${uiLabelMap.DAThisRecordWasExpired})</i>");
				}
			}
			$("#alterpopupWindowEdit").jqxWindow("open");
		}
	}
	
	function resetContentWindowEdit() {
		$('#e_partyId').val("");
		$('#e_roleTypeId').val("");
		$('#e_fromDate').val("");
		$("#e_thruDate").val("");
		$("#e_thruDate").jqxDateTimeInput({disabled: false});
		$("#alterCancel5").jqxButton({disabled: false});
		$("#alterSave5").jqxButton({disabled: false});
		$("#e_status").html("");
	}
	
	$(function(){
		<#--$("#alterCancel4").jqxButton({theme: theme});
	    $("#alterSave4").jqxButton({theme: theme});-->
	    $("#partyId").jqxDropDownButton({width : 218, height : 25});
	    var dataField = [
             {name : "partyId", type: "string"},
             {name : "partyTypeId", type: "string"},
             {name : "description", type: "string"}
         ];
	    var column = [
              {text : "${StringUtil.wrapString(uiLabelMap.DAPartyId)}", datafield : "partyId", width :"20%"},
              {text : "${StringUtil.wrapString(uiLabelMap.DAPartyTypeId)}", datafield : "partyTypeId", width :"20%"},
              {text: "${StringUtil.wrapString(uiLabelMap.DADescription)}", datafield : "description"}
          ];
	    GridUtils.initGrid({url : 'JQGetListStoreParty',width : '100%',showfilterrow : true,editable: false,pageable : true,columnsresize: true,localization: getLocalization(),autoHeight : true},dataField, column, null, $('#partyIdGrid'));
	    $("#partyIdGrid").on('rowselect', function(event){
	    	var args = event.args;
	    	var rowindex = args.rowindex;
	    	var data = $('#partyIdGrid').jqxGrid('getrowdata', rowindex);
	    	var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + data.partyId +'</div>';
	    	$('#partyId').jqxDropDownButton('setContent',dropDownContent);
	    })
	    $("#fromDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss'});
	    $("#fromDate").jqxDateTimeInput('setDate', '${nowTimestamp}');
	    $("#thruDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null});
	    $("#alterpopupWindow").jqxWindow({width: 600,height: 320, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel4"), modalOpacity: 0.7, theme:theme});
	    $("#alterpopupWindowEdit").jqxWindow({width: 600, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel5"), modalOpacity: 0.7, theme:theme});
	    $("#alterCancel5").jqxButton({theme: theme});
	    $("#alterSave5").jqxButton({theme: theme});
	    $("#e_fromDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null, disabled: true});
	    $("#e_thruDate").jqxDateTimeInput({height: '25px',width: 218, formatString: 'dd/MM/yyyy HH:mm:ss', allowNullDate: true, value: null});
	    $("#e_partyId").jqxInput({height: 25, disabled: true});
	    $("#e_roleTypeId").jqxInput({height: 25, disabled: true});
	    
	    $('#alterpopupWindowEdit').on('close', function (event) {
	    	resetContentWindowEdit();
	    });
	    
	    $("#alterSave4").on("click", function(){
	    	if($('#alterpopupWindowform').jqxValidator('validate')){
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
		        		partyId: $('#partyId').val(),
		        		roleTypeId: $('#roleTypeId').val(),
		        		fromDate: fromDate,
		        		thruDate: thruDate
		        	  };
			   	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgrid").jqxGrid('clearSelection');          
		        $("#jqxgrid").jqxGrid('selectRow', 0);
		        $("#alterpopupWindow").jqxWindow('close');
		        
		        // reset value on window
				$('#partyId').val("");
				$('#roleTypeId').val("");
				$('#fromDate').val("");
				$("#thruDate").val("");
	        }else{
	        	return;
	        }
	    });
	    
	    $("#alterSave5").on("click", function(){
	    	if($('#alterpopupWindowEditform').jqxValidator('validate')){
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
		        		partyId: $('#e_partyId').val(),
		        		roleTypeId: $('#e_roleTypeId').val(),
		        		fromDate: fromDate,
		        		thruDate: thruDate
		        	  };
		       	
		       	$.ajax({
	                type: "POST",                        
	                url: "updateProductStoreRoleAjax",
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
	    
	    // list roleTypeId =======================================================================
		var sourceRoleType = {
			localdata: roleTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterRoleType = new $.jqx.dataAdapter(sourceRoleType, {
	        	formatData: function (data) {
	                if ($("#roleTypeId").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#roleTypeId").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#roleTypeId").jqxComboBox({source: dataAdapterRoleType, multiSelect: false, width: '218', height: 25,
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
	    	dropDownWidth: 300, 
	    	autoDropDownHeight: true, 
	    	valueMember: "roleTypeId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterRoleType.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterRoleType.dataBind();
	        }
	    });
	    
	    // list listParty =======================================================================
	    var sourcePartyIdsApply = {
			datatype: "json",
	        datafields: [
	            { name: 'partyId' },
                { name: 'firstName' },
                { name: 'middleName' },
                { name: 'lastName' },
                { name: 'groupName' }
	        ],
	        data: {},
	        type: "POST",
	        root: "listParty",
	        contentType: 'application/x-www-form-urlencoded',
	        url: "getPartiesJson"
	    };
	    var dataAdapterPartyIdsApply = new $.jqx.dataAdapter(sourcePartyIdsApply, {
	            formatData: function (data) {
	                if ($("#partyId").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#partyId").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    <#--$("#partyId").jqxComboBox({
	        width: 218,
	        placeHolder: " ${StringUtil.wrapString(uiLabelMap.DAChoosePartyIdApply)}",
	        dropDownWidth: 218,
	        height: 25,
	        source: dataAdapterPartyIdsApply,
	        remoteAutoComplete: true,
	        autoDropDownHeight: true,               
	        displayMember: "partyId",
	        valueMember: "partyId",
	        renderer: function (index, label, value) {
	            var item = dataAdapterPartyIdsApply.records[index];
	            if (item != null) {
	                var label = item.partyId;
	                return label;
	            }
	            return "";
	        },
	        renderSelectedItem: function(index, item)
	        {
	            var item = dataAdapterPartyIdsApply.records[index];
	            if (item != null) {
	                var label = item.partyId;
	                return label;
	            }
	            return "";
	        },
	        search: function (searchString) {
	            dataAdapterPartyIdsApply.dataBind();
	        }
	    });-->
	    
	    $('#alterpopupWindowform').jqxValidator({
	        rules: [
	        	{input: '#partyId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
	           	{input: '#roleTypeId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
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
	    
	    $('#alterpopupWindowEditform').jqxValidator({
	        rules: [
	        	{input: '#e_partyId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
	           	{input: '#e_roleTypeId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
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