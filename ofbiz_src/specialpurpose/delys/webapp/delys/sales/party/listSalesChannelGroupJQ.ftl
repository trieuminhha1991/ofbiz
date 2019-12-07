<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqGridMinimumLib/>
<div class="row-fluid" id="container"></div>
<script type="text/javascript">

<#assign listTypes = delegator.findList("RoleType", null, null, null, null, false) >
	var typeData = new Array();
	<#list listTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['roleTypeId'] = '${item.roleTypeId}';
		row['description'] = '${description}';
		typeData[${item_index}] = row;
	</#list>

		var sourceRoleType = {
			localdata: typeData,
	        datatype: "array",
	        datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
	    };
	    
	    
</script>

<#assign dataField = "[{name: 'roleTypeGroupId', type: 'string'}, 
{name: 'description', type: 'string'},

]"/>


<script type="text/javascript">
	<#assign rowsDetails = "function (index, parentElement, gridElement, datarecord){
		var roleTypeGroupId = datarecord.roleTypeGroupId;
		var url = 'jqxGeneralServicer?sname=jqGetListSalesChannelGroupMember';
		var id = datarecord.uid.toString();
		var grid = $($(parentElement).children()[0]);
        $(grid).attr(\"id\",\"jqxgridDetail\" + \"_\");
        var jqxGridDetailsSource = {
        		datafields: [
        			{name: 'roleTypeGroupId', type: 'string'},
        			{name: 'roleTypeId', type: 'string'},
        			{name: 'sequenceNum', type: 'number'},
        			{name: 'fromDate', type: 'date', other: 'Timestamp'},
        			{name: 'thruDate', type: 'date', other: 'Timestamp'}
        		],
        		cache: false,
        		datatype: 'json',
				type: 'POST',
				data: {
					roleTypeGroupId: roleTypeGroupId,
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
		        url: url,
		        beforeprocessing: function (data) {
		        	jqxGridDetailsSource.totalrecords = data.TotalRows;
		        },
		        contentType: 'application/x-www-form-urlencoded',
		        root: 'results'
        };
        var nestedGridColums = [
			{text: '${StringUtil.wrapString(uiLabelMap.roleTypeId)}', datafield: 'roleTypeId'},
			{text: '${StringUtil.wrapString(uiLabelMap.sequenceNum)}', datafield: 'sequenceNum', width: '30%', cellsalign: 'center', filtertype: 'number'},
			{text: '${StringUtil.wrapString(uiLabelMap.fromDate)}', datafield: 'fromDate', width: '20%', cellsalign: 'center', cellsformat: 'dd/MM/yyyy'},
			{text: '${StringUtil.wrapString(uiLabelMap.thruDate)}', datafield: 'thruDate', width: '20%', cellsalign: 'center', cellsformat: 'dd/MM/yyyy'}
		];
        var nestedGridAdapter = new $.jqx.dataAdapter(jqxGridDetailsSource);
         
        if (grid != null) {
        	grid.jqxGrid({
        		source: nestedGridAdapter, 
        		width: '65%', 
        		height: 186,
        		autoheight: false,
        		pagesize: 5,
        		pagesizeoptions:['5', '10'],
        		virtualmode: true,
        		showtoolbar: false,
        		rendergridrows: function () {
    	            return nestedGridAdapter.records;
    	        },
    	        columnsResize: true,
    	        pageable: true,
    	        editable: false,
    	        columns: nestedGridColums,
    	        selectionmode: 'singlerow',
    	        theme: 'olbius'
        	});
        	
        	grid.on('contextmenu', function () {
                return false;
            });
            
            grid.on('rowclick', function (event) {
                if (event.args.rightclick) {
                    grid.jqxGrid('selectrow', event.args.rowindex);
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    contextMenuChil.jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
            
            contextMenuChil.on('itemclick', function (event) {
			var args = event.args;
		    var rowindex = grid.jqxGrid('getselectedrowindex');
		    var tmpKey = $.trim($(args).text());
		    if (tmpKey == '${StringUtil.wrapString(uiLabelMap.deleteMember)}') {
	    		var wtmp = window;
	    	   	var rowindex = grid.jqxGrid('getselectedrowindex');
	    	   	var data = grid.jqxGrid('getrowdata',rowindex);
	    	   	var tmpwidth = $('#popupDeleteMember').jqxWindow('width');
	    	   	windowDeleteMember.jqxWindow('open');
	    	}

		});  
            
        function deleteMemberr(){
		var row = grid.jqxGrid('getselectedrowindexes');
		var success = deleteSuccess;
		var cMemberr = new Array();
			var data2 = grid.jqxGrid('getrowdata', row);
			var map = {};
			map['roleTypeGroupId'] = data2.roleTypeGroupId;
			map['roleTypeId'] = data2.roleTypeId;
			map['sequenceNum'] = data2.sequenceNum;
			map['fromDate'] = data2.fromDate;
			if(!data2.thruDate){
				map['thruDate'] = data2.fromDate;
			}else{
				map['thruDate'] = data2.thruDate;
			}
			cMemberr = map;
		if (cMemberr.length <= 0){
			return false;
		} else {
			cMemberr = JSON.stringify(cMemberr);
			jQuery.ajax({
		        url: 'deleteMemberGroupJQ',
		        type: 'POST',
		        async: true,
		        data: {
		        		'cMemberr': cMemberr,
	        		},
		        success: function(res) {
		        	var message = '';
					var template = '';
					if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
						if(res._ERROR_MESSAGE_LIST_){
							message += res._ERROR_MESSAGE_LIST_;
						}
						if(res._ERROR_MESSAGE_){
							message += res._ERROR_MESSAGE_;
						}
						template = 'error';
					}else{
						message = success;
						template = 'success';
						grid.jqxGrid('updatebounddata');
		        		grid.jqxGrid('clearselection');
					}
					updateGridMessage('jqxgrid', template ,message);
		        },
		        error: function(e){
		        }
		    });
		}
	}
     
     $('#alterSave3').click(function () {
				var rowindex = grid.jqxGrid('getselectedrowindex');
			   	if (rowindex >= 0) {
				   	deleteMemberr();
		           	$('#popupDeleteMember').jqxWindow('hide');
		           	$('#popupDeleteMember').jqxWindow('close');
			   	}
		    });
     
            
        }
	}
	"/> 
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.salesChannelGroupId)}', dataField: 'roleTypeGroupId', width: '30%', editable:false,}, 
		{text: '${StringUtil.wrapString(uiLabelMap.salesChannelGroupName)}', dataField: 'description'},
	"/>
   function setWindowTitle(jqxWindowDiv, title){
	   jqxWindowDiv.jqxWindow('setTitle', title);
   }
   
   
</script>

<div id='contextMenu' style="display: none">
	<ul>
		<li>
			${StringUtil.wrapString(uiLabelMap.DARefresh)}
		</li>
		<li>
			${StringUtil.wrapString(uiLabelMap.createMember)}
		</li>
	</ul>
</div>

<div id='contextMenuChil' style="display: none">
	<ul>
		<li>
			${StringUtil.wrapString(uiLabelMap.deleteMember)}
		</li>
	</ul>
</div>
<div id="popupDeleteMember" style="display : none;">
	<div>
		${uiLabelMap.CommonDelete}
	</div>
	<div style="overflow: hidden;">
		<form id="DeleteMemberForm" class="form-horizontal">
			<input type="hidden" value="${parameters.roleTypeId?if_exists}" id="roleTypeId" name="roleTypeId">
			<label class="">${uiLabelMap.DAAreYouSureDelete}</label>
			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel3" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave3" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-pencil'></i> ${uiLabelMap.OK}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div id="popupCreateGMember" style="display : none;">
	<div>
		${uiLabelMap.CommonAdd}
	</div>
	<div style="overflow: hidden;">
		<form id="CreateMemberForm" class="form-horizontal">
			<input type="hidden" value="${parameters.roleTypeGroupId?if_exists}" id="roleTypeGroupId" name="roleTypeGroupId" />
			<div class="row-fluid no-left-margin">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.roleTypeId}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="roleTypeIdMember"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.sequenceNum}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="sequenceNumMember"></div>
					</div>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.fromDate}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="fromDateMember"></div>
					</div>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div class="row-fluid no-left-margin">
					<label class="span5 align-right asterisk">${uiLabelMap.thruDate}</label>
					<div class="span7" style="margin-bottom: 10px;">
						<div id="thruDateMember"></div>
					</div>
				</div>
			</div>

			<div class="form-action">
				<div class='row-fluid'>
					<div class="span12 margin-top10">
						<button type="button" id="alterCancel2" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.Cancel}
						</button>
						<button type="button" id="alterSave2" class='btn btn-primary form-action-button pull-right'>
							<i class='fa-pencil'></i> ${uiLabelMap.Save}
						</button>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>
<div id="notifyMissInfoId" style="display: none;">
	<div>
		${uiLabelMap.approveCancel1}
	</div>
</div>
<@jqGrid id="jqxgrid" addrow="false" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup" mouseRightMenu="true" contextMenuId="contextMenu"
url="jqxGeneralServicer?sname=JQGetListSalesChannelGroup&hasrequest=Y" editable="false"  initrowdetailsDetail=rowsDetails  rowdetailsheight="200"  initrowdetails="true"
/> 
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign dayStart = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<script>
	$("#notifyMissInfoId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#container", autoOpen: false, animationOpenDelay: 800, autoClose: true, autoCloseDelay: 5000 });
	$("#contextMenu").jqxMenu({ width: 230, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
	    var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    var tmpKey = $.trim($(args).text());
	    if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	    	$("#jqxgrid").jqxGrid('updatebounddata');
	    }  else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.createMember)}") {
	    		var wtmp = window;
	    	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
	    	   	var tmpwidth = $('#popupCreateGMember').jqxWindow('width');
	    	   	$('#popupCreateGMember').jqxWindow({ width: 500, height : 250,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
	    	   	$("#popupCreateGMember").jqxWindow('open');
	    	   	$('#sequenceNumMember').jqxNumberInput({width : '200px',height : '25px', spinButtons: false, inputMode: 'simple', decimalDigits: 0});
	    		$("#fromDateMember").jqxDateTimeInput({ width: '200px', height: '25px' });
	    	    $("#thruDateMember").jqxDateTimeInput({ width: '200px', height: '25px' });
	    	    $("#sequenceNumMember").val(null);
	    	    $("#fromDateMember").val(null); 
	    	    $("#thruDateMember").val(null);
    	}
	});
	$(document).ready(function(){
		$("#alterSave2").click(function () {
	    		   $('#CreateMemberForm').jqxValidator('validate');
	    		   if(!$('#CreateMemberForm').jqxValidator('validate')) return false;
	    			   var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	    			   if (rowindex >= 0) {
	    				   abc();
				           $('#CreateMemberForm').jqxValidator('hide');
				           $("#popupCreateGMember").jqxWindow('hide');
				           $("#popupCreateGMember").jqxWindow('close');
	    			   }
				//});
		    });
			$("#alterCancel2").click(function(){
				$("#sequenceNumMember").val(null);
				$("#fromDateMember").val(null);
				$("#thruDateMember").val(null);
				$('#CreateMemberForm').jqxValidator('hide');
				$("#popupCreateGMember").jqxWindow('hide');
				$("#popupCreateGMember").jqxWindow('close');
			});
			   
			$('#popupCreateGMember').on('close', function (event) { 
				$('#CreateMemberForm').jqxValidator('hide');
			});
			    
	     	$('#CreateMemberForm').jqxValidator({
			  	rules : [
			   			{input: '#roleTypeIdMember', message: '${StringUtil.wrapString(uiLabelMap.DAChooseARoleTypeApply)}', action: 'blur', rule: 
			    			function (input, commit) {
			    				var value = $(input).val();
								if(/^\s*$/.test(value)){
									return false;
								}
								return true;
							}
						},
						{input: '#sequenceNumMember', message: '${StringUtil.wrapString(uiLabelMap.DASequenceNumberMustNotbeEmpty)}', action: 'blur', rule: 
			    			function (input, commit) {
			    				var value = $(input).val();
								if(/^\s*$/.test(value) || $("#sequenceNumMember").jqxNumberInput('getDecimal') == null ){
									return false;
								}
								return true;
							}
						},
						{input: '#fromDateMember', message: '${uiLabelMap.DAFromDateMustNotBeEmpty}', action: 'blur', rule:
							function (input, commit) {
								if($('#fromDateMember').jqxDateTimeInput('getDate') == null || $('#fromDateMember').jqxDateTimeInput('getDate') == ''){
									return false;
								}
								return true;
							}
						},
						{input: '#thruDateMember', message: '${uiLabelMap.DAThruDateMustNotBeEmpty}', action: 'blur', rule:
							function (input, commit) {
								if($('#fromDateMember').jqxDateTimeInput('getDate') == null || $('#thruDateMember').jqxDateTimeInput('getDate') == ''){
									return false;
								}
								return true;
							}
						},
						{input: '#fromDateMember', message: '${uiLabelMap.fromDateValidate}', action: 'blur', rule: 
							function (input, commit) {
								if($('#fromDateMember').jqxDateTimeInput('val', 'date').getTime() < ${dayStart.getTime()} || $('#fromDateMember').jqxDateTimeInput('val', 'date').getTime() > $('#thruDateMember').jqxDateTimeInput('val', 'date').getTime()) {
									return false;
								}
								return true;
							}
						},
						{input: '#thruDateMember', message: '${uiLabelMap.thruDateValidate}', action: 'blur', rule: 
							function (input, commit) {
								if($('#thruDateMember').jqxDateTimeInput('val', 'date').getTime() < ${dayStart.getTime()}) {
									return false;
								}
								return true;
							}
						},
						
				]
			});
			
			
	});
	
	var contextMenuChil = $("#contextMenuChil").jqxMenu({ width: 230, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
    var windowDeleteMember =  $('#popupDeleteMember').jqxWindow({ width: 320, height : 120,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel3"), modalOpacity: 0.7 });
	var deleteSuccess = "${StringUtil.wrapString(uiLabelMap.DADeleteSuccess)}";
	
	function abc(){
		var row = $('#jqxgrid').jqxGrid('getselectedrowindexes');
		var success = "${StringUtil.wrapString(uiLabelMap.DAAddSuccess)}";
		var cGroupChannel = new Array();
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			var map = {};
			map['roleTypeGroupId'] = data.roleTypeGroupId;
			map['roleTypeId'] = $('#roleTypeIdMember').val();
			map['sequenceNum'] = $('#sequenceNumMember').val();
			map['fromDate'] = $("#fromDateMember").jqxDateTimeInput('val', 'date').getTime();
			map['thruDate'] = $("#thruDateMember").jqxDateTimeInput('val', 'date').getTime();
			cGroupChannel = map;
		if (cGroupChannel.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			cGroupChannel = JSON.stringify(cGroupChannel);
			jQuery.ajax({
		        url: "createMemberGroupJQ",
		        type: "POST",
		        async: true,
		        data: {
		        		'cGroupChannel': cGroupChannel,
	        		},
		        success: function(res) {
		        	var message = "";
					var template = "";
					if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
						if(res._ERROR_MESSAGE_LIST_){
							message += res._ERROR_MESSAGE_LIST_;
						}
						if(res._ERROR_MESSAGE_){
							message += res._ERROR_MESSAGE_;
						}
						template = "error";
					}else{
						message = success;
						template = "success";
						$('#jqxgrid').jqxGrid('updatebounddata');
		        		$('#jqxgrid').jqxGrid('clearselection');
		        		$("#roleTypeIdMember").jqxComboBox('clearSelection'); 
		        		$('#sequenceNumMember').jqxNumberInput('clear');
						// $("#wdwApprove").jqxWindow('close');
						// $('#jqxgrid').jqxGrid('updatebounddata');
					}
					updateGridMessage('jqxgrid', template ,message);
		        },
		        error: function(e){
		        }
		    });
		}
	}
	var dataAdapterRoleType = new $.jqx.dataAdapter(sourceRoleType, {
	        	formatData: function (data) {
	                if ($("#roleTypeIdMember").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#roleTypeIdMember").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#roleTypeIdMember").jqxComboBox({source: dataAdapterRoleType, multiSelect: false, width: '200px', height: 25,
	    	dropDownWidth: 'auto', 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}", 
	    	displayMember: "description", 
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
	    
	
</script>>