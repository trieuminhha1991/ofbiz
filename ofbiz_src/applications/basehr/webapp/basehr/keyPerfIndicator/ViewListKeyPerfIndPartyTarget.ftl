<#include "script/ViewListKeyPerfIndPartyTargetScript.ftl"/>
<#assign datafield = "[{name: 'partyTargetId', type: 'string'},
					   {name: 'partyTargetName', type: 'string'},
					   {name: 'description', type: 'string'},	
					   {name: 'statusId', type: 'string'},	
					   {name: 'partyId', type: 'string'},	
					   {name: 'groupName', type: 'string'},	
					   {name: 'periodTypeId', type: 'string'},	
					   {name: 'fromDate', type: 'date'},	
					   {name: 'thruDate', type: 'date'},	
					   {name: 'description', type: 'string'},	
					   ]"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRCommonUnit)}', datafield: 'groupName', width: '17%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.KeyPerfIndTargetName)}', datafield: 'partyTargetName', width: '20%'},
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonDuration)}', datafield: 'periodTypeId', width: '13%', columntype: 'dropdownlist',
							filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value){
					 		   for(var i = 0; i < globalVar.periodTypeArr.length; i++){
					 			   if(value == globalVar.periodTypeArr[i].periodTypeId){
					 				   return '<span>' + globalVar.periodTypeArr[i].description + '</span>';
					 			   }
					 		   }
					 		   return '<span>' + value + '</span>';
					 	    },
					 	    createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.periodTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'periodTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							},
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: '12%', editable: false, 
						   cellsformat: 'dd/MM/yyyy', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: '12%', editable: false, 
						   cellsformat: 'dd/MM/yyyy', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield: 'description', width: '26%'}, 
					   "/>
</script>					   
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
	clearfilteringbutton="true" showlist="true" 
	addType="popup" alternativeAddPopup="AddNewKeyPerfIndicatorWindow" addrow="true" 
	editable="false" deleterow="false" 
	url="jqxGeneralServicer?sname=JQGetListKeyPerfIndPartyTarget" autorowheight="true" jqGridMinimumLibEnable="false"
	mouseRightMenu="true" contextMenuId="contextMenu"
 	updateUrl=""
	editColumns=""/>

<div id="contextMenu" class="hide">
	<ul>
		<li action="viewDetail">
			<i class="fa-eye"></i>${uiLabelMap.ViewDetails}
        </li>
	</ul>
</div>	
	
<div id="AddNewKeyPerfIndicatorWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class="row-fluid" >
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
			        <ul class="wizard-steps wizard-steps-square">
		                <li data-target="#generalInfo" class="active">
		                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
		                </li>
		                <li data-target="#settingTarget">
		                    <span class="step">2. ${uiLabelMap.SettingTarget}</span>
		                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="step-container">
			    	<div class="step-pane active" id="generalInfo">
			    		<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.KeyPerfIndTargetName}</label>
							</div>
							<div class='span8'>
								<input type="text" id="partyTargetNameAdd">
							</div>
						</div>
			    		<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.HRCommonUnit}</label>
							</div>
							<div class='span8'>
								<div id="partyIdAdd">
									<div id="jqxTree"></div>
								</div>
							</div>
						</div>
			    		<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.HRCommonDuration}</label>
							</div>
							<div class='span8'>
								<div id="periodTypeAdd"></div>
							</div>
						</div>
			    		<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.CommonFromDate}</label>
							</div>
							<div class='span8'>
								<div class="row-fluid">
									<div class="span4">
										<div id="fromDateAdd"></div>
									</div>
									<div class="span8">
										<div class='row-fluid'>
											<div class='span4 align-right'>
												<label class="asterisk">${uiLabelMap.CommonThruDate}</label>
											</div>
											<div class='span8'>
												<div id="thruDateAdd"></div>
											</div>
										</div>
									</div>		
								</div>
							</div>
						</div>
			    		<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.HRNotes}</label>
							</div>
							<div class='span8'>
								<textarea id="descriptionAdd"></textarea>
							</div>
						</div>
			    	</div>
			    	<div class="step-pane" id="settingTarget">
			    		<div id="keyPerfIndItemGrid"></div>
			    	</div>
			    </div>
			    <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.HRCommonCreateNew)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingAddNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAddNew"></div>
					</div>
				</div>
			</div>
		</div>
	</div>	
</div>	
<div id="addNewKeyPerfIndicator" class=hide>
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container' >
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.KeyPerfIndicator}</label>
				</div>
				<div class='span8'>
					<div id="keyPerfIndicatorList"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.KPIWeigth}</label>
				</div>
				<div class='span8'>
					<div id="itemWeight"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRTarget}</label>
				</div>
				<div class='span8'>
					<div id="itemTarget"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.HRCommonMeasure}</label>
				</div>
				<div class='span8'>
					<div id="itemUom"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancelAddNewTarget" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAddNewTarget" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewListKeyPerfIndPartyTarget.js"></script>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/AddKeyPerfIndPartyTarget.js"></script>