<div class="row-fluid">
<script>
	var listEmailType = [<#if listEmailType?exists><#list listEmailType as pt>{contactMechPurposeTypeId : "${pt.contactMechPurposeTypeId}",description: "${StringUtil.wrapString(pt.description?default(''))}"},</#list></#if>];
</script>
<#assign dataField="[{ name: 'contactMechId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'fromDate', type: 'date' },
					 { name: 'infoString', type: 'string' },
					 { name: 'contactMechTypeId', type: 'string' },
					 { name: 'contactMechPurposeTypeId', type: 'string' }]
					"/>				

<#assign columnlist="{ text: '${uiLabelMap.contactMechId}', datafield: 'contactMechId', hidden: true},
					 { text: '${uiLabelMap.partyId}', datafield: 'partyId', hidden: true},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', hidden: true},
 					 { text: '${uiLabelMap.Email}', datafield: 'infoString'},
 					 { text: '${uiLabelMap.contactMechTypeId}', datafield: 'contactMechTypeId', hidden: true},
                     { text: '${uiLabelMap.ContactMechType}', datafield: 'contactMechPurposeTypeId', width: 300, filtertype: 'checkedlist',columntype: 'dropdownlist',
                  		createfilterwidget: function(column, columnElement, widget){
             				var filterBoxAdapter = new $.jqx.dataAdapter(listEmailType, {autoBind: true});
                 			var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'contactMechPurposeTypeId', filterable:true, searchMode:'containsignorecase', filterable: true, searchMode: 'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupEmail').jqxGrid('getrowdata', row);
							for(var x in listEmailType){
								if(listEmailType[x].contactMechPurposeTypeId  
									&& val.contactMechPurposeTypeId 
									&& listEmailType[x].contactMechPurposeTypeId == val.contactMechPurposeTypeId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listEmailType[x].description+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
                            var sourceGlat =
					            {
					                localdata: listEmailType,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300,  displayMember: 'description', valueMember : 'contactMechPurposeTypeId'}); 
							 }
                     }"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartyGroupEmail&partyId=${party.partyId}" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	id="partyGroupEmail"
	showtoolbar = "true" deleterow="true"
	width="780"
	bindresize="false"
	autorowheight="true"
	customLoadFunction="true"
	jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?sname=deletePartyContact&jqaction=D" deleteColumn="partyId;contactMechId;contactMechPurposeTypeId;fromDate(java.sql.Timestamp)"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyContactMech" alternativeAddPopup="popupAddRowEmail" addrow="true" addType="popup" 
	addColumns="partyId;infoString;contactMechPurposeTypeId;contactMechTypeId" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyContactMech"  editColumns="contactMechId;partyId;infoString;contactMechPurposeTypeId;contactMechTypeId"
/>
<div id="popupAddRowEmail">
	<div>${uiLabelMap.DAAddNewAddress}</div>
    <div style="overflow: hidden;">
    	<form class="form-horizontal">
    		<input type="hidden" value="${party.partyId?if_exists}" id="partyCreated"/>
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.PartyAddressLine}
					</label>
					<div class="controls">
						<input type="text" id="emailAdd"/>
					</div>
				</div>		
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">
						${uiLabelMap.ContactMechType}
					</label>
					<div class="controls">
						<div id="ContactMechTypeEmail"></div>
					</div>
				</div>				
		   	</div>	
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary' style="margin-right: 5px; margin-top: 10px; padding: 0 10px!important;" id="alterSaveEmail"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>
<script>
	if(!loadFormEmail){
		loadFormEmail = true;
		$(function(){
			var popupEmail = $("#popupAddRowEmail");
			popupEmail.jqxWindow({
		        width: 600, height: 200, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01, theme: 'olbius'           
		    });
		    popupEmail.on('close', function (event) { 
		    	popupEmail.jqxValidator('hide');
		    }); 
		  
			var ctmDd = $('#ContactMechTypeEmail');
			ctmDd.jqxDropDownList({
				theme: 'olbius',
				source: listEmailType,
				width: 218,
				filterable: true,
				displayMember: "description",
				valueMember : 'contactMechPurposeTypeId'
			});
			popupEmail.jqxValidator({
			   	rules: [{
		            input: "#emailAdd", 
		            message: "${StringUtil.wrapString(uiLabelMap.EmailRequired?default(''))}", 
		            action: 'blur', 
		            rule: function (input, commit) {
		                var index = input.val();
		                var re = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    					return re.test(index);
		            }
			 	},{
		            input: "#ContactMechTypeEmail", 
		            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
		            action: 'blur', 
		            rule: function (input, commit) {
		                var index = input.jqxDropDownList('getSelectedIndex');
		                return index != -1;
		            }
		        }]
			 });
			 var skillJqx = $("#partyGroupEmail");
			 $("#alterSaveEmail").click(function () {
				if(!$('#popupAddRowEmail').jqxValidator('validate')){
					return;
				}
				var index = ctmDd.jqxDropDownList("getSelectedItem");
				var contactMechPurposeTypeId = index ? index.value : "";
		    	var row = { 
		    		partyId : $("#partyCreated").val(),
		    		infoString: $("#emailAdd").val(),
		    		contactMechPurposeTypeId: contactMechPurposeTypeId,
		    		contactMechTypeId: "EMAIL_ADDRESS"
		    	  };
			    skillJqx.jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        skillJqx.jqxGrid('clearSelection');                        
		        skillJqx.jqxGrid('selectRow', 0);  
		        popupEmail.jqxWindow('close');
		    });
		});
	}
</script>
</div>