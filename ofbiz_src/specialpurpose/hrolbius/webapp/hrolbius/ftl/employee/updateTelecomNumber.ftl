<script>
	var listTelecomNumber = [<#if listTelecomNumber?exists><#list listTelecomNumber as pt>{contactMechPurposeTypeId : "${pt.contactMechPurposeTypeId}",description: "${StringUtil.wrapString(pt.description?default(''))}"},</#list></#if>];
</script>
<#assign dataField="[{ name: 'contactMechId', type: 'string' },
					 { name: 'partyId', type: 'string' },
					 { name: 'fromDate', type: 'date' },
					 { name: 'areaCode', type: 'string' },
					 { name: 'countryCode', type: 'string' },
					 { name: 'contactNumber', type: 'string' },
					 { name: 'contactMechPurposeTypeId', type: 'string' }]
					"/>				

<#assign columnlist="{ text: '${uiLabelMap.contactMechId}', datafield: 'contactMechId', hidden: true},
					 { text: '${uiLabelMap.partyId}', datafield: 'partyId', hidden: true},
					 { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', hidden: true},
 					 { text: '${uiLabelMap.CommonCountryCode}', datafield: 'countryCode'},
 					 { text: '${uiLabelMap.PartyAreaCode}', datafield: 'areaCode'},
 					 { text: '${uiLabelMap.PartyPhoneNumber}', datafield: 'contactNumber', },
                     { text: '${uiLabelMap.ContactMechType}', datafield: 'contactMechPurposeTypeId', width: 350, filtertype: 'checkedlist',columntype: 'dropdownlist',
                  		createfilterwidget: function(column, columnElement, widget){
             				var filterBoxAdapter = new $.jqx.dataAdapter(listTelecomNumber, {autoBind: true});
                 			var dataSoureList = filterBoxAdapter.records;
						    dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
						    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', autoDropDownHeight: false,valueMember : 'contactMechPurposeTypeId', filterable:true, searchMode:'containsignorecase', filterable: true, searchMode: 'containsignorecase'});
						},
						cellsrenderer : function(row, column, value){
							var val = $('#partyGroupTelecom').jqxGrid('getrowdata', row);
							for(var x in listTelecomNumber){
								if(listTelecomNumber[x].contactMechPurposeTypeId  
									&& val.contactMechPurposeTypeId 
									&& listTelecomNumber[x].contactMechPurposeTypeId == val.contactMechPurposeTypeId){
									return '<div style=\"margin-top: 6px; margin-left: 4px;\">'+listTelecomNumber[x].description+'</div>';		
								}
							}
						},
						createeditor: function (row, column, editor) {
                            var sourceGlat =
					            {
					                localdata: listTelecomNumber,
					                datatype: \"array\"
					            };
					            var dataAdapterGlat = new $.jqx.dataAdapter(sourceGlat);
	                            editor.jqxDropDownList({source: dataAdapterGlat, dropDownHeight:300,  displayMember: 'description', valueMember : 'contactMechPurposeTypeId'}); 
							 }
                     }"/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartyGroupTelecom&partyId=${party.partyId}" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	id="partyGroupTelecom"
	showtoolbar = "true" deleterow="true"
	width="780"
	bindresize="false"
	autorowheight="true"
	customLoadFunction="true"
	jqGridMinimumLibEnable="false"
	removeUrl="jqxGeneralServicer?sname=deletePartyContact&jqaction=D" deleteColumn="partyId;contactMechId;contactMechPurposeTypeId;fromDate(java.sql.Timestamp)"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyTelecomNumber" alternativeAddPopup="popupAddRowTelecom" addrow="true" addType="popup" 
	addColumns="partyId;contactMechPurposeTypeId;countryCode;areaCode;contactNumber" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateTelecomNumber"  editColumns="partyId;contactMechId;countryCode;areaCode;contactNumber"
/>
<div id="popupAddRowTelecom">
	<div>${uiLabelMap.ContactInfomation}</div>
    <div style="overflow: hidden;">
    	<form id="EditContactMech" class='basic-form form-horizontal'>
			<div class="control-group no-left-margin ">
				<label for="statusId" id="statusId_title">${uiLabelMap.PhoneMobile}</label>
				<div class="controls">
					<input type="text" name="phone_mobile" id="phone_mobile">
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="asterisk">
					${uiLabelMap.ContactMechType}
				</label>
				<div class="controls">
					<div id="ContactMechTypeTelecom"></div>
				</div>
			</div>	
			<input type="hidden" value="<#if party.partyId?exists>${party.partyId}</#if>" id="partyIdNumber"/>
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" class='btn btn-primary' style="margin-right: 5px; margin-top: 10px; padding: 0 10px!important;" id="alterSaveTelecom"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>
<script>
	if(!loadFormTelecom){
		loadFormTelecom = true;
		function findGeo(list, par){
			var tmp = new Array();
			for(var x in list){
				if(list[x].geoIdFrom == par){
					tmp.push(list[x]);
				}
			}
			return tmp;
		}
		var countrySelected = -1;
		var stateSelected = -1;
		var creatingPA = false;
		var popupTelecom = $("#popupAddRowTelecom");
		popupTelecom.jqxWindow({
	        width: 600, height: 200, resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.01, theme: 'olbius'           
	    });
	    popupTelecom.on('close', function (event) { 
	    	popupTelecom.jqxValidator('hide');
	    }); 
	    $("#phone_mobile").jqxMaskedInput({ width: '208px', height: '30px', mask: '(+##)-###-##########' });
		var ctmDd = $('#ContactMechTypeTelecom');
		ctmDd.jqxDropDownList({
			theme: 'olbius',
			source: listTelecomNumber,
			width: 218,
			filterable: true,
			displayMember: "description",
			valueMember : 'contactMechPurposeTypeId'
		});
		popupTelecom.jqxValidator({
		   	rules: [{
	            input: "#ContactMechTypeTelecom", 
	            message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}", 
	            action: 'blur', 
	            rule: function (input, commit) {
	                var index = input.jqxDropDownList('getSelectedIndex');
	                return index != -1;
	            }
	        },{
				input: '#phone_mobile',
				message: '${StringUtil.wrapString(uiLabelMap.NumberRequired?default(''))}',
				action: 'blur',
				rule: function(input, commit){
					var value = getPhoneNumber(input);
					if(!value || isNaN(value)){
						return false;
					}
					return true;
				}
				
			}]
		 });
		 var skillJqx = $("#partyGroupTelecom");
		 $("#alterSaveTelecom").click(function () {
			if(!popupTelecom.jqxValidator('validate')){
				return;
			}
			var index = ctmDd.jqxDropDownList("getSelectedItem");
			var contactMechPurposeTypeId = index ? index.value : "";
			var number = getPhoneDetail($("#phone_mobile"));
	    	var row = { 
	    		partyId : $("#partyIdNumber").val(),
	    		areaCode : number.areaCode,
	    		countryCode : number.countryCode,
	    		contactNumber: number.contactNumber,
	    		contactMechPurposeTypeId: contactMechPurposeTypeId
	    	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popupTelecom.jqxWindow('close');
	    });
	}
	function getPhoneNumber(input){
		var value = input.jqxMaskedInput('val');
		var val = value.match(/\d/g);
		value = val.join("");
		return value;
	}
	function getPhoneDetail(input){
		var value = input.jqxMaskedInput('val');
		var arr = value.split(")-");
		if(!arr.length){return;}
		var country = arr[0].match(/\d/g).join("");
		var arr2 = arr[1].split("-");
		var areaCode = arr2[0].match(/\d/g).join("");
		var number = arr2[1].match(/\d/g).join("");
		return {
			countryCode : country,
			areaCode : areaCode,
			contactNumber: number
		};
	}
</script>