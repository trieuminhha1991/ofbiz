<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<#assign partyRelationshipType = delegator.findList("PartyRelationshipType", null, null, null, null, false) />
<script>
	//Prepare for party data
	<#assign listParties = delegator.findList("PartyNameView", null, null, null, null, false) >
	var partyData = new Array();
	<#list listParties as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.firstName?if_exists) + " " + StringUtil.wrapString(item.middleName?if_exists) + " " + StringUtil.wrapString(item.lastName?if_exists) />
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyData[${item_index}] = row;
	</#list>
	</script><script>	
	
	var partyRelationshipType = [
	                            <#list partyRelationshipType as partyRelationship>
	                     		{
	                     			partyRelationshipTypeId : "${partyRelationship.partyRelationshipTypeId}",
	                     			partyRelationshipName : "${StringUtil.wrapString(partyRelationship.partyRelationshipName?default(''))}"
	                     		},
	                     	</#list>	
	                     	];
	
</script>
<style>
	#emergencyContactAdd > div {
		margin-left: 10px;
	}


</style>

<div class="tab-pane" id="familyMemberTab">
	<#assign dataField="[{ name: 'relPartyId', type: 'string' },
					 { name: 'personFamilyBackgroundId', type: 'string' },
					 { name: 'partyId', type: 'string'},
					 { name: 'partyFamilyId', type: 'string' },
					 { name: 'firstName', type: 'string' },
					 { name: 'middleName', type: 'string' },
					 { name: 'lastName', type: 'string' },
					 { name: 'partyRelationshipTypeId', type: 'string' },
					 { name: 'occupation', type: 'string' },
					 { name: 'birthDate', type: 'date', other:'Timestamp' },
					 { name: 'placeWork', type: 'string'},
					 { name: 'phoneNumber', type: 'number'},
					 { name: 'emergencyContact', type: 'string'}
					 ]"/>

	<#assign columnlist="
			{ text: '${uiLabelMap.FullName}', datafield: 'partyFamilyId', width: 200,
							cellsrenderer: function(column, row, value){
								for(var i = 0;  i < partyData.length; i++){
									if(partyData[i].partyId == value){
										return '<span title=' + value + '>' + partyData[i].description + '</span>'
									}
								}
								return '<span>' + value + '</span>'
							}
				, hidden: true},
				{ text: '${uiLabelMap.personFamilyBackgroundId}', datafield: 'personFamilyBackgroundId', width: '10%', editable: false, hidden: true},	
				{ text: '${uiLabelMap.partyId}', datafield: 'partyId', width: '10%',hidden: true, editable: false},	
				{ text: '${uiLabelMap.firstName}', datafield: 'firstName', width: '10%', filtertype: 'input'},	
				{ text: '${uiLabelMap.middleName}', datafield: 'middleName', width: '10%'},
				{ text: '${uiLabelMap.lastName}', datafield: 'lastName', width: '15%'},
				{ text: '${uiLabelMap.partyRelationshipType}', datafield: 'partyRelationshipTypeId', width: '11%', filtertype: 'list', columntype: 'dropdownlist', editable: true,
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < partyRelationshipType.length; i++){
							if(value == partyRelationshipType[i].partyRelationshipTypeId){
								return '<span title=' + value + '>' + partyRelationshipType[i].partyRelationshipName + '</span>';
							}
						}
						return '<span>' + value + '</span>';
					},
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        editor.jqxDropDownList({source: partyRelationshipType, valueMember: 'partyRelationshipTypeId', displayMember:'partyRelationshipName' });
				    },
				    createfilterwidget: function (column, htmlElement, editor) {
		                editor.jqxDropDownList({ source: fixSelectAll(partyRelationshipType), displayMember: 'partyRelationshipName', valueMember: 'partyRelationshipTypeId' ,
		                	renderer: function (index, label, value) {
		                		if (index == 0) {
		                			return value;
		                		}
		                        for(var i = 0; i < partyRelationshipType.length; i++){
		                        	if(value == partyRelationshipType[i].partyRelationshipTypeId){
		                        		return partyRelationshipType[i].partyRelationshipName; 
		                        	}
		                        }
		                    }});
		                editor.jqxDropDownList('checkAll');
		            }
				},
				{ text: '${uiLabelMap.BirthDate}', datafield: 'birthDate', width: '10%', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxDateTimeInput({width: '110px', height: '31px'});
					}
				},
				{ text: '${uiLabelMap.HROccupation}', datafield: 'occupation', width: '14%'},
				{ text: '${uiLabelMap.placeWork}', datafield: 'placeWork', width: '13%', filtertype: 'string'},
				{ text: '${uiLabelMap.phoneNumber}', datafield: 'phoneNumber', width: '12%', columntype: 'numberinput', filtertype: 'number',
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
						editor.jqxNumberInput({inputMode: 'simple', spinButtons: false});
					}
				},
				{ text: '${uiLabelMap.EmergencyContact}', datafield: 'emergencyContact', width: '5%',filtertype: 'list', columntype: 'dropdownlist', editable: true,
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        editor.jqxDropDownList({source: emergencyContactList, selectedIndex: 0, width: '50%',autoDropDownHeight: true });
				    },
				}
					 "/>

	<@jqGrid addrow="true" addType="popup" isShowTitleProperty="false" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 deleterow="true" filterable="true" addrefresh="true" alternativeAddPopup="alterpopupWindow" editable="true" dataField=dataField columnlist=columnlist
		 url="jqxGeneralServicer?sname=JQGetListEmplFamily&partyId=${parameters.partyId}"
		 createUrl="jqxGeneralServicer?sname=createPersonFamilyBackground&jqaction=C" addColumns="partyId;firstName;middleName;lastName;partyRelationshipTypeId;occupation;birthDate(java.sql.Date);placeWork;phoneNumber;emergencyContact"
		 removeUrl="jqxGeneralServicer?sname=deletePersonFamilyBackgroundSv&jqaction=D" deleteColumn="personFamilyBackgroundId"
		 updateUrl="jqxGeneralServicer?sname=editPersonFamilyBackgroundSv&jqaction=U" editColumns="personFamilyBackgroundId;firstName;middleName;lastName;partyRelationshipTypeId;occupation;birthDate(java.sql.Date);placeWork;phoneNumber;emergencyContact"
	/>
	
</div>	 
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<input type="hidden" value="${parameters.partyId}" id="partyId" name="partyId" />
			
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.LastName}</div>
				<div class="span8" style="margin-bottom: 10px;">
					<input type="text" id="lastNameFamily" name="lastNameFamily"/>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.MiddleName}</div>
				<div class="span8" style="margin-bottom: 10px;">
					<input type="text" id="middleNameFamily" name="middleNameFamily"/>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.FirstName}</div>
				<div class="span8" style="margin-bottom: 10px;">
					<input type="text" id="firstNameFamily" name="firstNameFamily"/>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<label class="span4">${uiLabelMap.HRRelationship}</label>
				<div class="span8" style="margin-bottom: 10px;">
					<div id="relationshipAdd"></div>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<label class="span4">${uiLabelMap.BirthDate}</label>
				<div class="span8" style="margin-bottom: 10px;">
	        		<div id="familyBirthDateAdd"></div>
	        	</div>
	        </div>
	        		
	        <div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.HROccupation}</div>
				<div class="span8" style="margin-bottom: 10px;">
					<input type="text" id="occupationAdd" name="occupationAdd"/>
				</div>
			</div>
				
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.HRPlaceWork}</div>
				<div class="span8" style="margin-bottom: 10px;">
					<input type="text" id="placeWorkAdd" name="placeWorkAdd"/>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<label class="span4 asterisk">${uiLabelMap.PhoneNumber}</label>
				<div class="span8" style="margin-bottom: 10px;">
					<div id="phoneNumberAdd"></div>
				</div>
			</div>
					
			<div class="row-fluid no-left-margin">
				<label class="span4">${uiLabelMap.EmergencyContact}</label>
				<div class="span8" style="margin-bottom: 10px;">
					<div id="emergencyContactAdd"><span>Y</span></div>
				</div>
			</div>
					
			<div class="control-group no-left-margin" style="float:right">
				<div class="" style="width:166px;margin:0 auto;">
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius btn-danger" style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
					
		</form>
	</div>
</div>		 
	 
<#assign partyRelationshipType = delegator.findByAnd("PartyRelationshipType", Static["org.ofbiz.base.util.UtilMisc"].toMap("parentTypeId", "FAMILY"), null, false)>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	var theme = theme;

	var partyRelationshipType = [
	    <#list partyRelationshipType as relationshipT>
	    {
	    	partyRelationshipTypeId : "${relationshipT.partyRelationshipTypeId}",
	    	partyRelationshipName : "${StringUtil.wrapString(relationshipT.partyRelationshipName)}"
	    },
	    </#list>	
	];
	var emergencyContactList = [
	                            "Y","N"
	                            ];
	
	$("#relationshipAdd").jqxDropDownList({autoDropDownHeight: true});
	$('#alterpopupWindow').jqxWindow({ width: 480, height : 470,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });
	$('#lastNameFamily').jqxInput({width : '243px',height : '25px'});
	$('#middleNameFamily').jqxInput({width : '243px',height : '25px'});
	$('#firstNameFamily').jqxInput({width : '243px',height : '25px'});
	$("#relationshipAdd").jqxDropDownList({ source: partyRelationshipType, width: '248px', height: '25px', selectedIndex: 0, displayMember: "partyRelationshipName", valueMember : "partyRelationshipTypeId"});
	$("#familyBirthDateAdd").jqxDateTimeInput({width: '248px', height: '25px'});
	$('#occupationAdd').jqxInput({width : '243px',height : '25px'});
	$('#placeWorkAdd').jqxInput({width : '243px',height : '25px'});
	$("#phoneNumberAdd").jqxNumberInput({ width: '248px', height: '25px', inputMode: 'simple', digits: 11, decimalDigits: 0 });
	$("#emergencyContactAdd").jqxCheckBox({ width: '120px', height: '25px', checked: true});

	$('#formAdd').jqxValidator({
		rules : [
			{input : '#lastNameFamily',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
			{input : '#middleNameFamily',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
			{input : '#firstNameFamily',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
			{input : '#occupationAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
			{input : '#placeWorkAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		]
	});
	
    $("#emergencyContactAdd").on('change', function (event) {
        var checked = event.args.checked;
        if (checked) {
            $("#emergencyContactAdd").find('span')[1].innerHTML = 'Y';
        }
        else {
            $("#emergencyContactAdd").find('span')[1].innerHTML = 'N';
        }
    });
	
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	
	$('#formAdd').on('validationSuccess',function(){
		var row = {};
		row = {
			partyId: $('#partyId').val(),
			firstName : $('#firstNameFamily').val(),
			middleName : $('#middleNameFamily').val(),
			lastName : $('#lastNameFamily').val(),
			partyRelationshipTypeId : $('#relationshipAdd').val(),
			occupation : $('#occupationAdd').val(),
			birthDate : convertDate($('#familyBirthDateAdd').val()),
			placeWork : $("#placeWorkAdd").val(),
			phoneNumber : $('#phoneNumberAdd').val(),
			emergencyContact : $('#emergencyContactAdd').val()
		};
		
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
		$("#jqxgrid").jqxGrid('clearSelection');                        
		$("#jqxgrid").jqxGrid('selectRow', 0);  
		$("#alterpopupWindow").jqxWindow('close');
	});
	
	$('#alterpopupWindow').on('close',function(){
		$('#formAdd').trigger('reset');
	});
	
	function convertDate(date) {
	 	if (!date) {
			return null;
		}
		var dateArray = date.split("/");
		var newDate = new Date(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
		return newDate.getTime();
	}
</script>	