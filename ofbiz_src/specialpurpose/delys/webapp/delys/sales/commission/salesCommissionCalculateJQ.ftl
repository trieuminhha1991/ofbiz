<style type="text/css">
	.control-group-manual {
		
	}
	.control-group-manual .control-label-manual {
		display:inline-block;
		margin-bottom:0;
		padding-right:5px;
		line-height: 27px;
  		vertical-align: top;
	}
	.control-group-manual .controls-manual {
		display:inline-block;
	}
</style>
<#assign roleTypesCommission = Static["com.olbius.util.SalesPartyUtil"].getListRoleTypeByRoleTypeGroup(delegator, "SALES_COMMISSIO_ROLE")!/>
<script type="text/javascript">
	var roleTypeData = [
		<#list roleTypesCommission as roleTypeItem>
		{
			'roleTypeId' : '${roleTypeItem.roleTypeId}', 
			'description' : '${StringUtil.wrapString(roleTypeItem.get("description", locale))}'
		},
		</#list>
	];
</script>
<div>
	<div class="row-fluid">
		<form name="fromCommissionCalculate" id="fromCommissionCalculate" method="POST" action="storeCommissionCalculateJQ">
			<div class="span4">
				<div class="control-group-manual">
					<label class="control-label-manual">${uiLabelMap.DARoleTypeIdOfParty}:</label>
					<div class="controls-manual">
						<div id="roleTypeId"></div>
					</div>
				</div>
			</div>
			<div class="span3">
				<div class="control-group-manual">
					<label class="control-label-manual">${uiLabelMap.DAFromDate}:</label>
					<div class="controls-manual">
						<div id="fromDate"></div>
					</div>
				</div>
			</div>
			<div class="span3">
				<div class="control-group-manual">
					<label class="control-label-manual">${uiLabelMap.DAThruDate}:</label>
					<div class="controls-manual">
						<div id="thruDate"></div>
					</div>
				</div>
			</div>
			<div class="span2">
				<button type="button" id="alterRun" class="btn btn-mini btn-primary pull-right"><i class="icon-ok open-sans"></i>${uiLabelMap.DARun}</button>
			</div>
		</form>
	</div>
	<div style="position:relative">
		<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
			<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
				<div style="float: left;">
					<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
					<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
				</div>
			</div>
		</div>
	</div>
	<br />
	<h5 class="green">${uiLabelMap.DAListCommissionLogRun}</h5>
	<div class="row-fluid">
		<div class="span12">
			<#assign dataField="[{ name: 'commissionLogId', type: 'string'},
								 { name: 'roleTypeId', type: 'string'},
								 { name: 'fromDate', type: 'date', other:'Timestamp'},
								 { name: 'thruDate', type: 'date', other:'Timestamp'},
								 { name: 'createdDate', type: 'date', other:'Timestamp'},
								 { name: 'createdBy', type: 'string'},
								 { name: 'countRowAction', type: 'number'}
				 		 	]"/>
			<#assign columnlist="{text: '${uiLabelMap.DALogId}', dataField: 'commissionLogId', width: '14%'},
								 {text: '${uiLabelMap.DARoleTypeId}', dataField: 'roleTypeId'},
							 	 {text: '${uiLabelMap.DAFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', width: '12%'},
							 	 {text: '${uiLabelMap.DAThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', width: '12%'},
							 	 {text: '${uiLabelMap.DACreatedDate}', dataField: 'createdDate', cellsformat: 'dd/MM/yyyy', width: '12%'},
							 	 {text: '${uiLabelMap.DACountRowAction}', dataField: 'countRowAction', cellsformat: 'n', cellsalign: 'right', width: '10%'},
							 	 {text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy'},
							 "/>
			<@jqGrid id="jqxgridCommissionLogRun" url="jqxGeneralServicer?sname=JQGetListSalesCommissionLogRun" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
			 	showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="false" addrow="false" addType="popup" deleterow="false" editable="false" filtersimplemode="true" showstatusbar="false" 
			 	mouseRightMenu="true" contextMenuId="contextMenu" showtoolbar="false" 
			 />
			<#-- initrowdetailsDetail=initrowdetailsDetail initrowdetails="true" rowdetailstemplateAdvance=rowdetailstemplateAdvance rowdetailsheight="300" -->
		</div>
	</div>
</div>
<div id='contextMenu'>
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/delys/images/js/sales/salesCommon.js"></script>
<script type="text/javascript">
	jQuery(document).ready(function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgridCommissionLogRun").jqxGrid('getselectedrowindex');
	        var tmpKey = $.trim($(args).text());
	        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
	        	$("#jqxgridCommissionLogRun").jqxGrid('updatebounddata');
	        }
		});
		
		$("#fromDate").jqxDateTimeInput({theme: theme, width: '150px', height: '25px', allowNullDate: true, value: null, formatString: 'dd/MM/yyyy'});
		$("#thruDate").jqxDateTimeInput({theme: theme, width: '150px', height: '25px', allowNullDate: true, value: null, formatString: 'dd/MM/yyyy'});
		
		$("#alterRun").on("click", function(){
			if(!$('#fromCommissionCalculate').jqxValidator('validate')) return false;
			jQuery.ajax({
	            url: 'storeCommissionCalculate',
	            async: true,
	            type: 'POST',
	            data: {
	            	"roleTypeId": $("#roleTypeId").val(),
	            	"fromDate": $("#fromDate").jqxDateTimeInput('getDate').getTime(),
	            	"thruDate": $("#thruDate").jqxDateTimeInput('getDate').getTime(),
	            },
	            beforeSend: function () {
					$("#info_loader").show();
				},
	            success: function (data) {
	            	if (data.thisRequestUri == "json") {
	            		var errorMessage = "";
				        if (data._ERROR_MESSAGE_LIST_ != null) {
				        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
				        		errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
				        	}
				        }
				        if (data._ERROR_MESSAGE_ != null) {
				        	errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
				        }
				        if (errorMessage != "") {
				        	$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html(errorMessage);
				        	$("#jqxNotification").jqxNotification("open");
				        } else {
				        	$('#container').empty();
				        	$('#jqxNotification').jqxNotification({ template: 'info'});
				        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
				        	$("#jqxNotification").jqxNotification("open");
				        }
	            	} else {
	            		var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAHaveAnErrorOccurredInTheProcess}!</span>";
						bootbox.dialog(message0, [{
							"label" : "OK",
							"class" : "btn-mini btn-primary width60px",
							}]
						);
	            	}
	            	$("#jqxgridCommissionLogRun").jqxGrid("updatebounddata");
	            },
	            error: function (e) {
	            	//console.log(e);
	            },
	            complete: function() {
			        $("#info_loader").hide();
			    }
	        });
		});
		
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
	    $("#roleTypeId").jqxComboBox({source: dataAdapterRoleType, multiSelect: false, 
	    	width: 200, height: 25, 
	    	dropDownWidth: 280, 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.DAChooseARoleId)}", 
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
	    
	    $("#fromCommissionCalculate").jqxValidator({
	    	position: 'bottom',
        	rules: [
        		{input: '#roleTypeId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(!isNotEmptyComboBoxOne($(input))){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).jqxDateTimeInput('getDate');
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#thruDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						var value = $(input).jqxDateTimeInput('getDate');
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				}
        	]
        });
	});
</script>