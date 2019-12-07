<div class="row-fluid">
	<div class="span12">
		<div id="jqxTabsInsuranceDetails">
			<ul>
                <li style="margin-left: 10px;">${uiLabelMap.ParticipateInsuranceStatus}</li>
                <li>${uiLabelMap.PartyInsuranceSalary}</li>
                <li>${uiLabelMap.PartyTimeInsurance}</li>
            </ul>
            <div>            	
            	<div id="jqxgridParticipateInsurance"></div>
            </div>
            <div>	
            	<div id="jqxgridPartyInsuranceSalary"></div>
            </div>
            <div>	
            	<div id="PartyTimeInsurance"></div>
            </div>
		</div>
	</div>
</div>
<#assign dataFieldTimeInsurance = "[{name: 'partyId', type: 'string'},
									{name: 'insuranceTypeId', type: 'string'},
			       		            {name: 'insuranceNumber', type: 'string'},
			       		            {name: 'partyHealthCareId', type: 'string'},
			       		            {name: 'fromDate', type: 'date'},
			       		            {name: 'thruDate', type: 'date'}]">
<#assign dataFieldInsuranceSalary = "[{name: 'partyId', type: 'string'},
									  {name: 'salaryInsurance', type: 'number'},
									  {name: 'fromDate', type: 'date', other:'Timestamp'},
									  {name: 'thruDate', type: 'date',  other:'Timestamp'}]">
<#assign columnlistInsuranceSalary = "{text: '${uiLabelMap.InsuranceSalary}', datafield: 'salaryInsurance', filterable: false,editable: false, cellsalign: 'right',
											cellsrenderer: function (row, column, value) {
								 		 		 var data = $('#jqxgridPartyInsuranceSalary').jqxGrid('getrowdata', row);
								 		 		 if (data && data.salaryInsurance){
								 		 		 	return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(data.salaryInsurance, data.salaryInsuranceUomId) + \"</div>\";
								 		 		 }
								 		 		 return value;
							 		 		 }
									  },
									  {text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', filterable: false, cellsalign: 'left', width: '30%', cellsformat: 'dd/MM/yyyy'},
									  {text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', filterable: false, cellsalign: 'left', width: '30%', cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
									  {text: '', datafield: 'partyId', hidden: true}" />
									  
<#assign dataFieldPariticipate = "[{name: 'insuranceTypeId', type:'string'},
									{name: 'statusDatetime', type: 'date'},
									{name: 'statusId', type: 'string'}]" />									  									        		            
<script type="text/javascript">
	var statusArr = new Array();
	<#list statusList as status>
		var row = {};
		row["statusId"] = "${status.statusId}";
		row["description"] = "${status.description?if_exists}";
		statusArr[${status_index}] = row;
	</#list>
	
	var insuranceTypeArr = new Array();
	<#list insuranceTypeList as insuranceType>
		var row = {};
		row["insuranceTypeId"] = "${insuranceType.insuranceTypeId}";
		row["description"] = "${insuranceType.description?if_exists}";
		insuranceTypeArr[${insuranceType_index}] = row;
	</#list>
	<#assign columlistTimeInsurance = "{text: '${uiLabelMap.InsuranceType}',  datafield: 'insuranceTypeId', filterable: false,editable: false, cellsalign: 'left',
											cellsrenderer: function (row, column, value){
												for(var i = 0; i < insuranceTypeArr.length; i++){
													if(insuranceTypeArr[i].insuranceTypeId == value){
														return '<div style=\"margin-top: 2px; margin-left: 3px\">' + insuranceTypeArr[i].description + '</div>';		
													}
												}
											}
										},
										{text: '${uiLabelMap.CommonInsuranceNumber}', datafield: 'insuranceNumber', filterable: false,editable: true, cellsalign: 'left'},
										{text: '${uiLabelMap.HealthCareProvider}', datafield: 'partyHealthCareId', filterable: false,editable: true, cellsalign: 'left'},
										{text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', filterable: false, cellsalign: 'left', width: 140, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
								        {text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', filterable: false, cellsalign: 'left', width: 140, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
								        {text: '', datafield: 'partyId', hidden: true}"
											/>
							        
	<#assign columnlistparticipate = "{text: '${uiLabelMap.InsuranceType}',  datafield: 'insuranceTypeId', filterable: false,editable: false, cellsalign: 'left',
											cellsrenderer: function (row, column, value){
												for(var i = 0; i < insuranceTypeArr.length; i++){
													if(insuranceTypeArr[i].insuranceTypeId == value){
														return '<div style=\"margin-top: 2px; margin-left: 3px\">' + insuranceTypeArr[i].description + '</div>';		
													}
												}
											}
										},
										{text: '${uiLabelMap.HRCommonDateModify}', datafield: 'statusDatetime', filterable: false, cellsalign: 'left', width: 150, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
										{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', filterable: false,editable: false, cellsalign: 'left',
											cellsrenderer: function (row, column, value){
												for(var i = 0; i < statusArr.length; i++){
													if(statusArr[i].statusId == value){
														return '<div style=\"margin-top: 2px; margin-left: 3px\">' + statusArr[i].description + '</div>';		
													}
												}
												return value;
											}	
										}"/>
	
	$(document).ready(function () {
		$('#jqxTabsInsuranceDetails').jqxTabs({width: '100%', height: '500px'});
		
		jQuery("#popupInsuranceSalary").jqxWindow({showCollapseButton: false, autoOpen: false,
			height: 250, width: 580, isModal: true, theme:theme});
		jQuery("#popupInsuranceSalary").on('open', function(event){
			$("#thruDateSalaryInsurance").val(null);
			$('#salaryInsurance').val(0);
		});
		
		
		var sourceInsuranceType =
        {
            localdata: insuranceTypeArr,
            datatype: "array"
        };
		
        var dataAdapterInsuranceType = new $.jqx.dataAdapter(sourceInsuranceType);
        $("#insuranceTypeListSetting").jqxDropDownList({ selectedIndex: 0,  source: dataAdapterInsuranceType, displayMember: "description", valueMember: "insuranceTypeId",
        		itemHeight: 25, height: 25, width: 250, dropDownHeight: 180,
        		renderer: function (index, label, value) {
					for(i=0; i < insuranceTypeArr.length; i++){
						if(insuranceTypeArr[i].insuranceTypeId == value){
							return insuranceTypeArr[i].description;
						}
					}
				    return value;
				}
            });
		jQuery("#popupInsuranceTime").jqxWindow({showCollapseButton: false, autoOpen: false,
			height: 280, width: 580, isModal: true, theme:theme});
		
		jQuery("#popupInsuranceTime").on('open', function(event){
			$("#insuranceTypeThruDate").val(null);
		});
		
		jQuery("#salaryInsurance").jqxNumberInput({ width: '250px', height: '25px', spinButtons: false, decimalDigits: 2, digits: 9, max: 999999999, theme: 'olbius', min: 0});
		jQuery("#fromDateSalaryInsurance").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		jQuery("#thruDateSalaryInsurance").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		jQuery("#insuranceTypeFromDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		jQuery("#insuranceTypeThruDate").jqxDateTimeInput({width: '250px', height: '25px', formatString: 'dd/MM/yyyy', theme: 'olbius'});
		
		$("#insuranceNumber").jqxInput({height: 25, width: 250 });
		$("#alterSalaryCancel").jqxButton({theme:theme});
	    $("#alterSalarySave").jqxButton({theme:theme});
	    
	    $("#alterInsuranceTimeSave").jqxButton({theme:theme});
	    $("#alterInsuranceTimeCancel").jqxButton({theme:theme});
	    
	    $("#alterSalarySave").click(function(){
	    	$("#popupInsuranceSalary").jqxWindow('close');
	    	var row = {
        		salaryInsurance: $('#salaryInsurance').val(),
        		partyId: "${parameters.partyId}",
        		fromDate: $("#fromDateSalaryInsurance").jqxDateTimeInput('getDate'),
        		thruDate: $("#thruDateSalaryInsurance").jqxDateTimeInput('getDate')
        	};
        	$("#jqxgridPartyInsuranceSalary").jqxGrid('addRow', null, row, "first");
	    });
	    $("#alterInsuranceTimeSave").click(function(){
	    	$("#popupInsuranceTime").jqxWindow('close');
	    	var row = {
	    			partyId: "${parameters.partyId}",
	    			insuranceTypeId: $("#insuranceTypeListSetting").val(),
	    			fromDate: $("#insuranceTypeFromDate").jqxDateTimeInput('getDate'),
	    			thruDate: $("#insuranceTypeThruDate").jqxDateTimeInput('getDate'),
	    			insuranceNumber: $("#insuranceNumber").val()
	    	};
	    	$("#PartyTimeInsurance").jqxGrid('addRow', null, row, "first");
	    });
	    $("#alterInsuranceTimeCancel").click(function(event){
	    	$("#popupInsuranceTime").jqxWindow("close");
	    });
	});
	
</script>

       		            
<@jqGrid filtersimplemode="true"  dataField=dataFieldPariticipate columnlist=columnlistparticipate clearfilteringbutton="false" showtoolbar="false" 
		 filterable="false" deleterow="false" editable="false" addrow="false" bindresize="false" width="'100%'"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQPartyParticipateInsurance&partyId=${parameters.partyId}" id="jqxgridParticipateInsurance" 
		 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" jqGridMinimumLibEnable="false"/>

<@jqGrid filtersimplemode="true" addType="popup" alternativeAddPopup="popupInsuranceSalary" dataField=dataFieldInsuranceSalary columnlist=columnlistInsuranceSalary 
		 clearfilteringbutton="false" showtoolbar="true" addrefresh="true"
		 filterable="false" deleterow="false" editable="false" addrow="true" bindresize="false" width="'100%'"
		 url="jqxGeneralServicer?hasrequest=Y&sname=JQPartyInsuranceSalary&partyId=${parameters.partyId}" id="jqxgridPartyInsuranceSalary" 
		 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" 
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyInsuranceSalary" functionAfterAddRow="refreshGrid()"
		 addColumns="partyId;salaryInsurance(java.math.BigDecimal);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)" jqGridMinimumLibEnable="false"/>
		 		 
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFieldTimeInsurance columnlist=columlistTimeInsurance clearfilteringbutton="false" showtoolbar="false" 
		 filterable="false" deleterow="true" editable="false" addrow="true" alternativeAddPopup="popupInsuranceTime" bindresize="false" 
		 width="'100%'" showtoolbar="true" url="jqxGeneralServicer?hasrequest=Y&sname=JQPartyTimeInsurance&partyId=${parameters.partyId}" id="PartyTimeInsurance"
		 createUrl="jqxGeneralServicer?jqaction=C&sname=createPartyInsurance" 
		 addColumns="partyId;insuranceTypeId;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp);insuranceNumber"  
		 removeUrl="jqxGeneralServicer?jqaction=D&sname=deletePartyInsurance" 
		 deleteColumn="partyId;insuranceTypeId;fromDate(java.sql.Timestamp)" 
		 updateUrl="" editColumns="" selectionmode="singlerow" jqGridMinimumLibEnable="false"/>
<div class="row-fluid">
	<div id="popupInsuranceSalary">
		<div id="popupInsuranceSalaryHeader">
			 ${uiLabelMap.SettingInsuranceSalary}
		</div>
		<div style="overflow: hidden;" id="popupParticipateWindowContent">
			<form class="form-horizontal">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonAmount}</label>
					<div class="controls">
						<div id="salaryInsurance"></div>
					</div>
				</div> 
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonFromDate}</label>
					<div class="controls">
						<div id="fromDateSalaryInsurance"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					<div class="controls">
						<div id="thruDateSalaryInsurance"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<input style="margin-right: 5px;" type="button" id="alterSalarySave" value="${uiLabelMap.CommonSubmit}" />
						<input id="alterSalaryCancel" type="button" value="${uiLabelMap.CommonCancel}" />
					</div>
				</div>
			</form>
		</div>
	</div>
</div>		

<div class="row-fluid">
	<div id="popupInsuranceTime">
		<div id="popupInsuranceTimeHeader">
			 ${uiLabelMap.SettingInsuranceTypeTime}
		</div>
		<div style="overflow: hidden;" id="popupInsuranceTimeContent">
			<form class="form-horizontal">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.InsuranceType}</label>
					<div class="controls">
						<div id="insuranceTypeListSetting"></div>
					</div>
				</div> 
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonInsuranceNumber}</label>
					<div class="controls">
						<input type="text" id="insuranceNumber"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonFromDate}</label>
					<div class="controls">
						<div id="insuranceTypeFromDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					<div class="controls">
						<div id="insuranceTypeThruDate"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<input style="margin-right: 5px;" type="button" id="alterInsuranceTimeSave" value="${uiLabelMap.CommonSubmit}" />
						<input id="alterInsuranceTimeCancel" type="button" value="${uiLabelMap.CommonCancel}" />
					</div>
				</div>
			</form>
		</div>
	</div>
</div>		 		 		 