<#assign dataField="[{ name: 'partyId', type: 'string' },
					 { name: 'skillTypeId', type: 'string' },
					 { name: 'firstName', type: 'string' },
					 { name: 'middleName', type: 'string' },
					 { name: 'lastName', type: 'string' },
					 { name: 'description', type: 'string'},
					 { name: 'rating', type: 'string' },
					 { name: 'yearsExperience', type: 'string' },
					 { name: 'startedUsingDate', type: 'string' },
					 { name: 'skillLevel', type: 'string' }]
					"/>
<#assign columnlist="{ text: '${uiLabelMap.partyId}', datafield: 'partyId', width: 100, hidden: true},
 					 { text: '${uiLabelMap.skillTypeId}', datafield: 'skillTypeId', width: 100, hidden: true},
					 { text: '${uiLabelMap.lastName}', datafield: 'lastName', width: 100, editable:false},
					 { text: '${uiLabelMap.middleName}', datafield: 'middleName', hidden: 'true',editable:false},
					 { text: '${uiLabelMap.firstName}', datafield: 'firstName',editable:false},
                     { text: '${uiLabelMap.SkillType}', datafield: 'description', width: 300, editable:false},
                     { text: '${uiLabelMap.FormFieldTitle_conversionFactor}', datafield: 'rating', width: 200,  
                     	validation: function (cell, value) {
                          if (isNaN(value) || value < 0 || value > 100) {
                              return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.invalidRating?default(''))}\" };
                          }
                          return true;
                      	}
                     },
                     { text: '${uiLabelMap.FormFieldTitle_yearsExperience}', datafield: 'yearsExperience',width: 150},
                     { text: '${uiLabelMap.startedUsingDate}', datafield: 'startedUsingDate', columntype: 'number', width: 50, hidden: true },
                     { text: '${uiLabelMap.FormFieldTitle_skillLevel}', datafield: 'skillLevel', width: 150}
                	
                	
   "/>
					 
<@jqGrid url="jqxGeneralServicer?sname=JQGetPartySkill" dataField=dataField columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" 
	editrefresh ="true"
	editmode="click"
	showtoolbar = "true" deleterow="true"
	removeUrl="jqxGeneralServicer?sname=deletePartySkill&jqaction=D" deleteColumn="partyId;skillTypeId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createPartySkill" alternativeAddPopup="popupAddPartySkill" addrow="true" addType="popup" 
	addColumns="partyId;skillTypeId;yearsExperience;rating;skillLevel" addrefresh="true"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartySkill"  editColumns="partyId;skillTypeId;rating;yearsExperience;startedUsingDate;skillLevel"
	
/>
<!-- <button id="setDefault" style="float:left;  padding-bottom: 5px;"  onclick="setDefault();">${uiLabelMap.SetDefault}</button> -->

<!-- <button id="cancelRow" style="float:right; padding-bottom: 5px;"  onclick="cancelRow();">${uiLabelMap.Cancel}</button>
<button id="saveRow" style="float:right; padding-bottom: 5px;"  onclick="saveAccTarget();">${uiLabelMap.Save}</button>

<button id="addNewTarget" style="float:right; padding-bottom: 5px;" onclick="openPopupCreatePartySkill();">${uiLabelMap.AddNewRow}</button> -->
<div id="popupAddPartySkill" >
    <div>${uiLabelMap.ConstructFormular}</div>
    <div style="overflow: hidden;">
    	<form name="popupAddPartySkill" action="" class="form-horizontal">
    		<div class="control-group">
				<label class="control-label" >${uiLabelMap.HRolbiusEmployeeID}</label>
				<div class="controls">
					<div id="partyIdFromAdd">
	 					<div id="jqxPartyFromGrid"></div>
	 				</div>
					<!-- <input type="text" name="partyId"/> -->
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.FormFieldTitle_skillTypeId}</label>
				<div class="controls">
					<div id="skillTypeIdDropdownlist"></div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.FormFieldTitle_experienceYears}</label>
				<div class="controls">
					<input type="number" name="yearsExperience"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.FormFieldTitle_conversionFactor}</label>
				<div class="controls">
						<input type="number" name="rating" id="rating"/>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" >${uiLabelMap.FormFieldTitle_skillLevel}</label>
				<div class="controls">
					<input type="number" name="skillLevel"/>
				</div>
			</div>
			<div class="row-fluid wizard-actions pull-right">
				<button type="button" style="margin-right: 5px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
    	</form>
    </div>
</div>  
<script type="text/javascript">
	var skills = [
		<#if skillTypes?exists>
		<#list skillTypes as skill>
		{
			skillTypeId : "${skill.skillTypeId}",
			description : "${skill.description}"
		},
		</#list>
		</#if>
	];
	
	$(document).ready(function(){
	   	// $("#setDefault").jqxButton({ template: "primary" });
		// $("#addNewTarget").jqxButton({ template: "primary" });
		// $("#alterSave").jqxButton({ template: "primary" });
		// $("#saveRow").jqxButton({ template: "primary" });
		// $("#cancelRow").jqxButton({ template: "primary" });	
		var skillJqx = $("#jqxgrid");
		var skillDd = $('#skillTypeIdDropdownlist');
		skillDd.jqxDropDownList({
			theme: 'olbius',
			source: skills,
			width: 218,
			displayMember: "description",
			valueMember: "skillTypeId",
			dropDownHeight: 200,
			// dropDownWidth: 400,
			filterable: true,
			incrementalSearch:true,
			searchMode: "containsignorecase",
			// renderer: function (index, label, value) {
			// var datarecord = province[index];
			// var table = "<div style='padding-top:5px'>"+datarecord.geoName+"</div>"
			// + "<div style='padding'><b>${uiLabelMap.Country}</b>:&nbsp;" + datarecord.geoNameFrom + "</div>";
			// return table;
			// }
		});
		var ptDropdown = $('#partyIdFromAdd'); 
		ptDropdown.jqxDropDownButton({ width: 218, height: 25});
			// create Party From
		var sourcePartyFrom = {
				datafields:[{name: 'partyId', type: 'string'},
					   		{name: 'firstName', type: 'string'},
					      	{name: 'lastName', type: 'string'},
					      	{name: 'middleName', type: 'string'},
					      	{name: 'groupName', type: 'string'},
			    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePartyFrom.totalrecords = data.TotalRows;
				},
				filter: function () {
				   	// update the grid and send a request to the server.
				   	$("#jqxPartyFromGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  	// callback called when a page or page size is changed.
				},
				sort: function () {
				  	$("#jqxPartyFromGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'partyId',
               	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListParties',
		};
	
	    var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom,
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
	                if (!sourcePartyFrom.totalRecords) {
	                    sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
	                }
	        }
	    });	
		$('#partyIdFromAdd').jqxDropDownButton({ width: 200, height: 25});
		$("#jqxPartyFromGrid").jqxGrid({
			width:600,
			source: dataAdapterPF,
			filterable: true,
			virtualmode: true, 
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			showfilterrow: true,
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns:[{text: '${uiLabelMap.DAPartyId}', datafield: 'partyId', width: '20%'},
						{text: '${uiLabelMap.DAFirstName}', datafield: 'firstName', width: '20%'},
						{text: '${uiLabelMap.DAMiddleName}', datafield: 'middleName', width: '20%'},
						{text: '${uiLabelMap.DALastName}', datafield: 'lastName', width: '20%'},
						{text: '${uiLabelMap.DAGroupName}', datafield: 'groupName'},
					]
		});
		$("#jqxPartyFromGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxPartyFromGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
	        $("#partyIdFromAdd").jqxDropDownButton('setContent', dropDownContent);
	    });
		var popup = $("#popupAddPartySkill");
		popup.jqxWindow({
	        width: 610, height: 350, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#Cancel"), modalOpacity: 0.01           
	    });
		$("#alterSave").click(function () {
			var i = skillDd.jqxDropDownList('getSelectedItem');
			var skillTypeId = i ? i.value : ""; 
	    	var row = { 
        		partyId: $("input[name='partyId']").val(),
        		skillTypeId: skillTypeId,
        		rating: $("input[name='rating']").val(),
        		skillLevel: $("input[name='skillLevel']").val(),
        		yearsExperience: $("input[name='yearsExperience']").val()
        	  };
		    skillJqx.jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        skillJqx.jqxGrid('clearSelection');                        
	        skillJqx.jqxGrid('selectRow', 0);  
	        popup.jqxWindow('close');
	        // 	skillJqx.jqxGrid("updatebounddata");
	    });
	});
    function openPopupCreatePartySkill(){
    	$("#popupAddPartySkill").jqxWindow('open');
    }
</script>