<div id="jqxgridFamily"></div>
<div id = "createNewFamilyWindow" style="display: none">
	<div id="windowHeaderNewFamilyMember">
		<span>
		   ${uiLabelMap.NewFamilyMember}
		</span>
	</div>
	<div class="basic-form form-horizontal" style="margin-top: 10px">
		<form name="createNewFamily" id="createNewFamily">	
			<div class="row-fluid" >
				<div class="span12">
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.LastName}:</label>
						<div class="controls">
							<input id="fmLastName">
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.MiddleName}:</label>  
						<div class="controls">
							<input id="fmMiddleName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label asterisk">${uiLabelMap.FirstName}:</label>
						<div class="controls">
							<input id="fmFirstName">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HRRelationship}:</label>  
						<div class="controls">
							<div  id="fmRelationshipTypeId"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.BirthDate}:</label>   
						<div class="controls">
							<div id="fmBirthDate"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label no-left-margin">${uiLabelMap.HROccupation}:</label>   
						<div class="controls">
							<input id="fmOccupation">
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">${uiLabelMap.HRPlaceWork}:</label>   
						<div class="controls">
							<input id="fmPlaceWork">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.PhoneNumber}:</label>   
						<div class="controls">
							<input id="fmPhoneNumber">
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">${uiLabelMap.EmergencyContact}:</label>   
						<div class="controls">
							<div id="fmEmergencyContact" style="margin-left: -3px !important;"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<button type="button" class="btn btn-mini btn-primary" id="alterSaveFamily"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
							<button type="button" class="btn btn-mini btn-danger" id="alterCancelFamily"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}&nbsp;</button>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

<script type="text/javascript">
<#assign listPartyRelationshipTypes = delegator.findList("PartyRelationshipType", 
		Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["FAMILY"]), null, null, null, false)>
var partyRelaTypeData = new Array();
<#list listPartyRelationshipTypes as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.partyRelationshipName?if_exists) />
	row['partyRelationshipTypeId'] = '${item.partyRelationshipTypeId?if_exists}';
	row['description'] = '${description}';
	partyRelaTypeData[${item_index}] = row;
</#list>
//Prepare data for friendType
<#assign listFriendTypes = delegator.findList("PartyRelationshipType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["FRIEND"]), null, null, null, false)>
var friendTypeData = new Array();
<#list listFriendTypes as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.partyRelationshipName?if_exists) />
	row['partyRelationshipTypeId'] = '${item.partyRelationshipTypeId?if_exists}';
	row['description'] = '${description}';
	friendTypeData[${item_index}] = row;
</#list>


$(document).ready(function () {
	var theme = "olbius";	
	var fmData = new Array();
	var fmIndex = 0;
	var source =
    {
        localdata: fmData,
        datatype: "array",
        datafields:
        [
        	{ name: 'firstName', type: 'string' },
			{ name: 'middleName', type: 'string' },
			{ name: 'lastName', type: 'string' },
			{ name: 'partyRelationshipTypeId', type: 'string' },
			{ name: 'birthDate', type: 'date' },
			{ name: 'occupation', type: 'string' },
			{ name: 'placeWork', type: 'string'},
			{ name: 'phoneNumber', type: 'string'},
			{ name: 'emergencyContact', type: 'string'}
        ]
    };
	var dataAdapter = new $.jqx.dataAdapter(source);
	
	$("#jqxgridFamily").jqxGrid(
    {
        width: "99%",
        source: dataAdapter,
        columnsresize: true,
        pageable: true,
        autoheight: true,
        showtoolbar: true,
        theme: 'olbius',
        rendertoolbar: function (toolbar) {
            var container = $("<div id='toolbarcontainer' class='widget-header'>");
            toolbar.append(container);
            container.append('<h4></h4>');
            container.append('<button id="fmAddrowbutton" class="grid-action-button"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
            container.append('<button id="fmDelrowbutton" class="grid-action-button"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
         
            // create new row.
            $("#fmAddrowbutton").on('click', function () {
            	$("#createNewFamilyWindow").jqxWindow('open');
            });
            
            // create new row.
            $("#fmDelrowbutton").on('click', function () {
            	var selectedrowindex = $('#jqxgridFamily').jqxGrid('selectedrowindex'); 
            	fmData.splice(selectedrowindex, 1);
            	$('#jqxgridFamily').jqxGrid('updatebounddata'); 
            	
            });
     },
        columns: [
      	  { text: '${uiLabelMap.fullName}',
      		 cellsrenderer: function(column, row, value){
      			 var rowData = $("#jqxgridFamily").jqxGrid('getrowdata', row);
      			 return '<span>' + rowData['lastName'] + ' ' + rowData['middleName'] + ' ' + rowData['firstName'] + '</span>'
      		 }
      	  },
          { text: '${uiLabelMap.HRRelationship}', datafield: 'partyRelationshipTypeId',
      		cellsrenderer: function(column, row, value){
     			 for(var i = 0; i < partyRelaTypeData.length; i++){
     				 if(value == partyRelaTypeData[i].partyRelationshipTypeId){
     					 return '<span>' + partyRelaTypeData[i].description + '<span>'
     				 }
     			 }
     			 return '<span>' + value + '<span>';
     		 }
          },
          { text: '${uiLabelMap.BirthDate}', datafield: 'birthDate', width: 150, cellsformat: 'dd/MM/yyyy'},
          { text: '${uiLabelMap.HROccupation}', datafield: 'occupation', width: 150},
          { text: '${uiLabelMap.HRPlaceWork}', datafield: 'placeWork', width: 150},
          { text: '${uiLabelMap.PhoneNumber}', datafield: 'phoneNumber', width: 150},
          { text: '${uiLabelMap.EmergencyContact}', datafield: 'emergencyContact', width: 150,
        	  cellsrenderer: function(column, row, value){
        		  if(value == 'Y') return '<span>' + '${uiLabelMap.CommonYes}' + '</span>'
        		  else return '<span>' + '${uiLabelMap.CommonNo}' + '</span>';
        	  }
          }
        ]
    });
	
	/******************************************************Edit Family**************************************************************************/
	//Handle alterSaveFamily
	$("#createNewFamily").jqxValidator({
		rules: [
			{input: '#fmLastName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
			{input: '#fmFirstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'}
		]
	});
	$("#alterSaveFamily").click(function () {
		$("#createNewFamily").jqxValidator('validate');
	});
	
	$("#createNewFamily").on('validationSuccess', function (event) {
		var row;
	        row = {
	        		lastName:$('#fmLastName').val(),
	        		middleName:$("#fmMiddleName").val(),
	        		firstName:$("#fmFirstName").val(),
	        		partyRelationshipTypeId:$("#fmRelationshipTypeId").val(),
	        		birthDate:$("#fmBirthDate").jqxDateTimeInput('getDate'),
	        		occupation:$("#fmOccupation").val(),
	        		placeWork:$("#fmPlaceWork").val(),
	        		phoneNumber:$("#fmPhoneNumber").val(),
	        		emergencyContact:$("#fmEmergencyContact").val() == true ? 'Y' : 'N'
			};
	        //fmIndex = fmData.length - 1;
	        //fmData[++fmIndex] = row;
	        $("#jqxgridFamily").jqxGrid('addrow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgridFamily").jqxGrid('clearSelection');
	        $("#jqxgridFamily").jqxGrid('selectRow', 0);
	        $("#createNewFamilyWindow").jqxWindow('close');
	    });
	//Create new family window
	$("#createNewFamilyWindow").jqxWindow({
        showCollapseButton: false, maxHeight: 1000, autoOpen: false, height: 550, minWidth: '640px', width: "640px", isModal: true,
        theme:'olbius', collapsed:false
    });
	
	//Create fmLastName
	$("#fmLastName").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmMiddleName
	$("#fmMiddleName").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmFirstName
	$("#fmFirstName").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmRelationshipTypeId
	$("#fmRelationshipTypeId").jqxDropDownList({selectedIndex: 0, source: partyRelaTypeData, valueMember: 'partyRelationshipTypeId', displayMember:'description', theme: theme});
	<#if (listPartyRelationshipTypes?size < 8)>
		$("#fmRelationshipTypeId").jqxDropDownList({autoDropDownHeight: true});
	</#if>
	//Create fmBirthDate
	$("#fmBirthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', theme: theme});
	
	//Create fmOccupation
	$("#fmOccupation").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmPlaceWork
	$("#fmPlaceWork").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmPhoneNumber
	$("#fmPhoneNumber").jqxInput({width: 195, height: 25, theme: theme});
	
	//Create fmEmergencyContact
	$("#fmEmergencyContact").jqxCheckBox({ width: 120, height: 25, height: 25, theme: theme});
	/******************************************************End Edit Family**********************************************************************/
});
function getEmployeeFamily(){	
	var rows = $("#jqxgridFamily").jqxGrid('getrows');
	return rows;
}
</script>