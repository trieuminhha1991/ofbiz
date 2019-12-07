<div id="addInvoicePopup${id}">
	<div>${addInvoiceTitle}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
	            <div class="control-group no-left-margin ">
	                <label class="">
	                    <label for="NewPurchaseInvoice_invoiceTypeIdShow" id="NewPurchaseInvoice_invoiceTypeIdShow_title">${uiLabelMap.FormFieldTitle_invoiceTypeId}</label>  </label>
	                <div class="controls">
                    	<div id="invoiceTypeIdShow${id}"></div>
	                </div>
	            </div>
	            <div class="control-group no-left-margin ">
	                <label class="">
	                    <label for="NewPurchaseInvoice_organizationPartyIdShow">${uiLabelMap.FormFieldTitle_organizationPartyId}</label>  </label>
	                <div class="controls">
                        <div id="organizationPartyIdShow${id}"></div>
	                </div>
	            </div>
	            <div class="control-group no-left-margin ">
	                <label class="">
	                    <label for="NewPurchaseInvoice_partyIdFromdShow" id="NewPurchaseInvoice_partyIdFromdShow_title">${uiLabelMap.Supplier}</label>  </label>
	                <div class="controls">
                        <div id="partyIdFromdShow${id}">
                        	<div id="jqxParty${id}"></div>
                        </div>
	                </div>
	            </div>
	        </div>
		</div>
		<div class="form-action">
			<button id="cancel{id}" class="btn btn-small btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinue{id}" class="btn btn-small btn-primary"><i class="icon-ok"></i>${uiLabelMap.SaveAndContinue}</button>
			<button id="save{id}" class="btn btn-small btn-primary"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript">
	var grid = $("#${id}");
	<#assign organList= delegator.findList("PartyAcctgPrefAndGroup",null,null,null,null,false)/>
    var organSource=new [<#if organList?has_content><#list organList as orItem>{partyId : "${orItem.partyId}",groupName : "${orItem.groupName}"},</#list></#if>];
	var acc = (function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		var outFilterCondition = "";
		var organizationPartyId = "${parameters.organizationPartyId?if_exists}";
		var form = $('#addInvoicePopup${id}');
		var initWindow = function(){
			$("#addInvoicePopup${id}").jqxWindow({
				width: 1000, height: 620, maxWidth: 1000, minWidth: 900, minHeight: 600, maxHeight: 1000, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7, theme:theme           
			});
			$("#addInvoicePopup${id}").on("close", function(){
				clearForm();
				form.jqxValidator("hide");
			});
		};
		var initPartySelect = function(dropdown, grid, width){
			var datafields = [{ name: 'partyId', type: 'string' },
	    		{ name: 'partyTypeId', type: 'string' },
	        	{ name: 'firstName', type: 'string' },
	        	{ name: 'lastName', type: 'string' },
	        	{ name: 'groupName', type: 'string' }];
	        var columns = [{ text: '${uiLabelMap.accApInvoice_partyId}', datafield: 'partyId', width: 200, pinned: true},
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
				},
				{ text: '${uiLabelMap.accAccountingFromParty}', datafield: 'groupName', width: 200},
				{ text: '${uiLabelMap.FormFieldTitle_firstName}', datafield: 'firstName', width: 200, 
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
						var first = rowdata.firstName ? rowdata.firstName : "";
						var last = rowdata.lastName ? rowdata.lastName : "";
						return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
					}
				}];
	    	GridUtils.initDropDownButton({url: "getFromParty", autorowheight: true, filterable: true, width: width ? width : 600},datafields,columns, null, grid, dropdown, "partyId");
		};
		var initElement = function(){
			$("#invoiceTypeIdShow${id}").jqxDropDownList({ theme: theme, source: dataInvoiceType, displayMember: "description", valueMember: "invoiceTypeId", width: '200', height: '25'});
			$("#organizationPartyIdShow${id}").jqxDropDownList({ theme: theme, source: organSource, displayMember: "groupName", valueMember: "partyId", width: '200', height: '25'});
			setTimeout(function(){
				initPartySelect($("#partyIdFromdShow${id}"), $("#jqxParty${id}"));
			}, 1000);
		};
		var bindEvent = function(){
			$("#commSave${id}").click(function () {
				if(!acc.save()){
					return;
				}
		        form.jqxWindow('close');
			}); 
			$("#saveAndContinue${id}").click(function () {
				if(!acc.save()){
					return;
				}
				clearForm();
			}); 
		};
		var save = function(){
			if(!validateForm()){
				return;
			}
			var row = getFormData();
	        grid.jqxGrid('addRow', null, row, "first");
	        grid.jqxGrid('clearSelection');                        
	        grid.jqxGrid('selectRow', 0); 
	        return true;
		};
		var validateForm = function(){
			return form.jqxValidator("validate");
		};
		var getFormData = function(){
			return { 
				statusId : "INVOICE_IN_PROCESS",
				currencyUomId : "${defaultOrganizationPartyCurrencyUomId}",
				invoiceTypeId : invoiceTypeId,
				partyId : partyId,
				partyIdFrom : partyIdFrom
		    };
		};
		var clearForm = function(){
			$("#invoiceTypeIdShow${id}").jqxDropDownList('selectedIndex', -1);
			$("#organizationPartyIdShow${id}").jqxDropDownList('selectedIndex', -1);
		};
		var initRule = function(){
			form.jqxValidator({
		        	rules: [{
		        	input: "#invoiceTypeIdShow${id}", message: "${uiLabelMap.CommonRequired}", action: 'blur change', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#organizationPartyIdShow${id}", message: "${uiLabelMap.CommonRequired}", action: 'blur change', 
		        	rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		   		},{
		        	input: "#partyIdFromdShow${id}", message: "${uiLabelMap.CommonRequired}", action: 'click', 
		        	rule: function (input, commit) {
		                var index = $("#jqxParty${id}").jqxGrid('getselectedrowindex');
		                return index != -1;
		            }
		   		}]
		    });
		};
		return {
			init : function(){
				initWindow();
				initElement();
				initRule();
				bindEvent();	
			},
			save : save
		};
	}());
	$(document).ready(function(){
		acc.init();
	});
</script>