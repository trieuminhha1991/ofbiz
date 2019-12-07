<div id="partyowner-tab" class="tab-pane<#if activeTab?exists && activeTab == "partyowner-tab"> active</#if>">
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<#assign dataField="[{ name: 'partyIdFrom', type: 'string'},
					 { name: 'partyIdTo', type: 'string'},
					 { name: 'fullName', type: 'string'}]"/>
<#assign columnlist = "{text: '${uiLabelMap.DmsSequenceId}', datafield: '', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false, width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (row + 1) + '</div>';
						    }
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.CustomerID)}', datafield: 'partyIdTo', width: 150,
							cellsrenderer: function(row, column, value, a, b, data){
						        var link = 'MTCustomerDetail?partyId=' + data.partyIdTo;
						        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
							}
						},
						{ text: '${StringUtil.wrapString(uiLabelMap.BSCustomerName)}', datafield: 'fullName', minWidth: 200},"
						/>
<#if modernTradeInfo.statusId == "PARTY_ENABLED" && security.hasEntityPermission("CUSTOMER_MT", "_APPROVE", session)>
<@jqGrid addrow="true" addType="popup"
         url='jqxGeneralServicer?sname=JQGetListMTCustomerRepresentative&partyId=${parameters.partyId?if_exists}' dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" sortdirection="desc"
         alternativeAddPopup="alterpopupWindow"/>
<#else >
<@jqGrid url='jqxGeneralServicer?sname=JQGetListMTCustomerRepresentative&partyId=${parameters.partyId?if_exists}' dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" filtersimplemode="true" sortdirection="desc"/>
</#if>
<#if modernTradeInfo.statusId == "PARTY_ENABLED" && security.hasEntityPermission("CUSTOMER_MT", "_APPROVE", session)>
<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span4 align-right asterisk'>
    					${uiLabelMap.BSCustomerId}
    				</div>
    				<div class='span8'>
    				    <div id="partyIdTo">
    					    <div id="partyIdToGrid"></div>
    					</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>

</div>

<script type="text/javascript">
    $.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var action = (function(){
	    var partyDDL;
		var initElement = function(){
            var configCustomer = {
                useUrl: true,
                root: 'results',
                widthButton: '80%',
                showdefaultloadelement: false,
                autoshowloadelement: false,
                datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
                columns: [
                    {text: "${uiLabelMap.BSCustomerId}", datafield: 'partyCode', width: '40%'},
                    {text: "${uiLabelMap.BSFullName}", datafield: 'fullName', width: '60%'},
                ],
                url: "getMTCustomerNotRepresentatives",
                useUtilFunc: true,
                key: 'partyId',
                keyCode: 'partyCode',
                description: ['fullName'],
                autoCloseDropDown: true,
                filterable: true,
                sortable: true,
            };
            partyDDL = new OlbDropDownButton($("#partyIdTo"), $("#partyIdToGrid"), null, configCustomer, []);
            initjqxWindow();
		}
		var initjqxWindow = function(){
			$("#alterpopupWindow").jqxWindow({
		        width: 530,height :150,  resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme: theme
		    });
		}

		var initRules = function(){
			$('#formAdd').jqxValidator({
				rules : [
                    {input : '#partyIdTo',message : '${StringUtil.wrapString(uiLabelMap.BSFieldRequired?default(''))}',action : 'change,close',rule : function(input){
                    	var val = partyDDL.getValue();
                    	if(!val) return false;
                    	return true;
                    }}
				]
			})
		}
		var save = function(){
            var partyIdTo = partyDDL.getValue();
			if(!$('#formAdd').jqxValidator('validate')){return;};
                $.ajax({
                    type: 'POST',
                    url: "createNewMemberOffice",
                    data: {
                        partyIdFrom:"${parameters.partyId?if_exists}",
                        partyIdTo:partyIdTo,
                    },
                    beforeSend: function(){
                        $("#loader_page_common").show();
                    },
                    success: function(data){
                        $("#jqxgrid").jqxGrid("updatebounddata");
                    },
                    error: function(data){
                        alert("Send request is error");
                    },
                    complete: function(data){
                        $("#loader_page_common").hide();
                    },
                });
                return true;
			}

		var clear = function(){
			partyDDL.clearAll();
		};

		var bindEvent = function(){
			$("#save").click(function () {
		    	if(save())  $("#alterpopupWindow").jqxWindow('close');
		    });
		    $("#alterpopupWindow").on('open',function(){
                partyDDL.getGrid().updateBoundData()
            });
		    $("#alterpopupWindow").on('close',function(){
		    	clear();
		    });
		}

		return {
			init : function(){
				initElement();
				bindEvent();
				initRules();
			}
		}
	}())

	$(document).ready(function(){
		action.init();
	});

</script>
</#if>
