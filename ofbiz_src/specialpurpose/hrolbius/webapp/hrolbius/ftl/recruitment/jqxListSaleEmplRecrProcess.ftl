<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.spinner.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script>
	theme = 'olbius';
	sequenceNum = 0;
	//Prepare data for Recruitment Type 
	<#assign listRecruitmentTypes = delegator.findList("RecruitmentType", null, null, null, null, false) >
	var recruitmentTypeData = new Array();
	<#list listRecruitmentTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['recruitmentTypeId'] = '${item.recruitmentTypeId}';
		row['description'] = '${description}';
		recruitmentTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Recruitment Form 
	<#assign listRecruitmentForms = delegator.findList("RecruitmentForm", null, null, null, null, false) >
	var recruitmentFormData = new Array();
	<#list listRecruitmentForms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['recruitmentFormId'] = '${item.recruitmentFormId}';
		row['description'] = '${description}';
		recruitmentFormData[${item_index}] = row;
	</#list>
	
	//Prepare data for Work Effort Type 
	<#assign listWorkEffortType = delegator.findList("WorkEffortType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "RECRUITMENT_ROUND"), null, null, null, false) >
	var workEffortTypeData = new Array();
	<#list listWorkEffortType as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['workEffortTypeId'] = '${item.workEffortTypeId}';
		row['description'] = '${description}';
		workEffortTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Empl Position Type 
	<#assign listEmplPositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false) >
	var emplPositionTypeData = new Array();
	<#list listEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		emplPositionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Party Name
	<#assign listPartyGroups = delegator.findList("PartyGroup", null, null, null, null, false) >
	var partyData = new Array();
	<#list listPartyGroups as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.groupName?if_exists) />
		row['partyId'] = '${item.partyId}';
		row['description'] = "${description}";
		partyData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'workEffortId', type: 'string' },
					 { name: 'workEffortName', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'workEffortId', width: 200,
						cellsrenderer: function(column, row, value){
		             		return '<span><a href=FindSaleApplRecruitmentProcess?workEffortId=' + value + '>' + value + '</a></span>';
		             	}
					 },
                     { text: '${uiLabelMap.workEffortName}', datafield: 'workEffortName'}
					 "/>
<#if Static['com.olbius.util.SecurityUtil'].hasRole("DELYS_SALESSUP_GT", userLogin.getString("partyId"), delegator)>
	<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListSaleRecruitmentProcess" alternativeAddPopup="alterpopupNewRecrProcess" dataField=dataField columnlist=columnlist
		 />
<#else>
	<@jqGrid addrow="false" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListSaleRecruitmentProcess" alternativeAddPopup="alterpopupNewRecrProcess" dataField=dataField columnlist=columnlist
		 />
</#if>

<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewRecrProcess" style="display: none">
			<div id="windowHeaderNewRecrProcess">
	            <span>
	               ${uiLabelMap.NewRecrProcess}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewRecrProcess">
	        	<div class="basic-form form-horizontal" style="margin-top: 10px">
	    			<form name="createNewRecrProcess" id="createNewRecrProcess">	
			            <div class="row-fluid" >
							<div class="span12">
								<div class="span6">
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.workEffortName}:</label>  
										<div class="controls">
											<input id="workEffortName"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.estimatedStartDate}:</label>  
										<div class="controls">
											<div id="estimatedStartDate"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.estimatedCompletionDate}:</label>  
										<div class="controls">
											<div id="estimatedCompletionDate"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.CommonDescription}:</label>  
										<div class="controls">
											<input id="description"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.Position}:</label>  
										<div class="controls">
											<div id="emplPositionTypeId">
												<div id="jqxPosGrid"></div>
											</div>
										</div>
									</div>
								</div>
								<div class="span6">
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.Department}:</label>  
										<div class="controls">
											<div id="partyIdLabel"></div>
											<input id="partyId" type="hidden">
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.workLocation}:</label>
										<div class="controls">
											<input id="workLocation"></input>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.ResourceNumber}:</label>  
										<div class="controls">
											<div id="resourceNumber"></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.RecruitmentType}:</label>
										<div class="controls">
											<div id="recruitmentTypeId" ></div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label">${uiLabelMap.RecruitmentForm}:</label>
										<div class="controls">
											<div id="recruitmentFormId" ></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
							<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
						</div>
					</div>
				</div>
	        </div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function (){
		
		//Init Window Popup for creation
		$("#alterpopupNewRecrProcess").jqxWindow({
		    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "90%", height: 450, minWidth: '40%', width: '80%', isModal: true,
		    theme:theme, collapsed:false,modalZIndex: 10000, cancelButton: '#alterCancel',
		    initContent: function () {
		    	//Create workEffortName
            	$("#workEffortName").jqxInput({width: 195});
            	
            	//Create estimatedStartDate
            	$("#estimatedStartDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
            	
            	//Create estimatedCompletionDate
            	$("#estimatedCompletionDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
            	
            	//Create description
            	$("#description").jqxInput({width: 195});
            	
            	//Create resourceNumber
            	$("#resourceNumber").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
            	
            	//Create workLocation
            	$("#workLocation").jqxInput({width: 195});
            	
            	//Create recruitmentTypeId
            	$("#recruitmentTypeId").jqxDropDownList({source: recruitmentTypeData, valueMember: 'recruitmentTypeId', displayMember: 'description'});
            	
            	//Create recruitmentFormId
            	$("#recruitmentFormId").jqxDropDownList({source: recruitmentFormData, valueMember: 'recruitmentFormId', displayMember: 'description'});
            	
            	//Create partyId
        		<#assign partyId = Static["com.olbius.util.PartyUtil"].getOrgByManager(userLogin, delegator) >
        		<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
        		jQuery("#partyIdLabel").text('${StringUtil.wrapString(partyName)}');
        		$("#partyId").val('${partyId}');
            	
            	//Position Grid
            	var sourcePos =
            	{
            			datafields:
            				[
            				 { name: 'emplPositionTypeId', type: 'string' },
            				 { name: 'partyId', type: 'string' },
            				 { name: 'description', type: 'string' },
            				 { name: 'groupName', type: 'string' },
            				],
            			cache: false,
            			root: 'results',
            			datatype: "json",
            			updaterow: function (rowid, rowdata) {
            				// synchronize with the server - send update command   
            			},
            			beforeprocessing: function (data) {
            				sourcePos.totalrecords = data.TotalRows;
            			},
            			filter: function () {
            				// update the grid and send a request to the server.
            				$("#jqxPosGrid").jqxGrid('updatebounddata');
            			},
            			pager: function (pagenum, pagesize, oldpagenum) {
            				// callback called when a page or page size is changed.
            			},
            			sort: function () {
            				$("#jqxPosGrid").jqxGrid('updatebounddata');
            			},
            			sortcolumn: 'emplPositionTypeId',
            			sortdirection: 'asc',
            			type: 'POST',
            			data: {
            				noConditionFind: 'Y',
            				conditionsFind: 'N',
            				partyId: '${partyId}',
            			},
            			pagesize:15,
            			contentType: 'application/x-www-form-urlencoded',
            			url: 'jqxGeneralServicer?sname=getListSaleEmplPositionTypes',
            	};
            	var dataAdapterParty = new $.jqx.dataAdapter(sourcePos);
            	$("#emplPositionTypeId").jqxDropDownButton({ width: 200, height: 25});
            	$("#jqxPosGrid").jqxGrid({
            		source: dataAdapterParty,
            		filterable: true,
            		showfilterrow: true,
            		virtualmode: true, 
            		sortable:true,
            		theme: theme,
            		editable: false,
            		autoheight:true,
            		pageable: true,
            		width: 800,
            		rendergridrows: function(obj)
            		{
            			return obj.data;
            		},
            	columns: [
            	  { text: '${uiLabelMap.Position}', datafield: 'description', filtertype: 'input'},
            	]
            	});
            	$("#jqxPosGrid").on('rowselect', function (event) {
            		var args = event.args;
            		var row = $("#jqxPosGrid").jqxGrid('getrowdata', args.rowindex);
            		selectedEmplPositionTypeId = row['emplPositionTypeId'];
            		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['description'] +'</div>';
            		$('#emplPositionTypeId').jqxDropDownButton('setContent', dropDownContent);
            		$('#emplPositionTypeId').jqxDropDownButton('close');
            	});
		    }
		});
		
		//Validate createNewRecrProcess
		$("#createNewRecrProcess").jqxValidator({
			rules: [
				{input: '#workEffortName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#estimatedStartDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') > $("#estimatedCompletionDate").jqxDateTimeInput('getDate') && $("#estimatedCompletionDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#estimatedCompletionDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, close', 
					rule: function (input, commit) {
	                    if(input.val()){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#estimatedCompletionDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') < $("#estimatedStartDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#resourceNumber', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				}
			]
		});
		
		//Handle click submit
		$("#alterSave").click(function(){
			var newRRData = {};
			var rrData = new Array();
			row = {
	        		workEffortName:"Vòng trúng tuyển",
	        		sequenceNum:"10000",
	        		workEffortTypeId:"ROUND_SELECTED",
	        		description:"Vòng mặc định",
			  };
			rrData[0] = row;
			newRRData['workEffortName'] = $("#workEffortName").val();
			newRRData['estimatedStartDate'] = $("#estimatedStartDate").jqxDateTimeInput('getDate').getTime();
			newRRData['estimatedCompletionDate'] = $("#estimatedCompletionDate").jqxDateTimeInput('getDate').getTime();
			newRRData['description'] = $("#description").val();
			newRRData['resourceNumber'] = $("#resourceNumber").val();
			newRRData['workLocation'] = $("#workLocation").val();
			newRRData['recruitmentTypeId'] = $("#recruitmentTypeId").val();
			newRRData['recruitmentFormId'] = $("#recruitmentFormId").val();
			newRRData['emplPositionTypeId'] = selectedEmplPositionTypeId;
			newRRData['partyId'] = $("#partyId").val();
			newRRData['listRounds'] = JSON.stringify(rrData);
			//Sent request create applicant
			$.ajax({
				url: 'createRecruitmentProcess',
				type: "POST",
				data: newRRData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(data.responseMessage == 'error'){
						bootbox.confirm("Tạo mới đợt tuyển dụng không thành công", function(result) {
							return;
						});
					}else{
						$("#alterpopupNewRecrProcess").jqxWindow('close');
						$("#jqxgrid").jqxGrid('updatebounddata');
					}
				}
			});
		});
	});
</script>