<#include "script/trainingViewEmplRegisterTrainingScript.ftl"/>
<#assign datafield = "[{name: 'statusIdRegister', type: 'string'},
						{name: 'trainingCourseId', type: 'string'},
						{name: 'trainingCourseCode', type: 'string'},
						{name: 'trainingCourseName', type: 'string'},
						{name: 'fromDate', type: 'date', other : 'Timestamp'},
						{name: 'thruDate', type: 'date', other : 'Timestamp'},
						{name: 'registerThruDate', type: 'date', other : 'Timestamp'},
						{name: 'registerFromDate', type: 'date', other : 'Timestamp'},
						{name: 'location', type: 'string'},
						{name: 'estimatedEmplPaid', type: 'number'},
						{name: 'certificate', type: 'string'},
						]"/>
						
<script type="text/javascript">
<#assign columnlist = "
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=margin:4px;>' + (value + 1) + '</div>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.StatusRegisterTraining)}', datafield: 'statusIdRegister', cellClassName: cellClass,
							width: '17%', filtertype: 'checkedlist', sortable: false, filterable: false, editable: false,
							cellsrenderer: function (row, column, value){
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
										return 	'<div style=\"margin: left\">' + globalVar.statusArr[i].description + '</div>';		
									}
								}
								return 	'<div style=\"margin: left\">${uiLabelMap.NotRegistered}</div>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var sourceStatusItem = {
							        localdata: globalVar.statusArr,
							        datatype: 'array'
							    };		
								var filterBoxAdapter = new $.jqx.dataAdapter(sourceStatusItem, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.TrainingCourseId)}', datafield: 'trainingCourseCode', width: '16%', filtertype: 'input', cellClassName: cellClass, editable: false,},
						{text: '${StringUtil.wrapString(uiLabelMap.TrainingCourseName)}', datafield: 'trainingCourseName', filtertype: 'input', width: '17%', cellClassName: cellClass, editable: false,},
						{text: '${StringUtil.wrapString(uiLabelMap.HREstimatedFromDate)}', datafield: 'fromDate',  filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellClassName: cellClass, editable: false, width: '15%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HREstimatedThruDate)}', datafield: 'thruDate',  filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellClassName: cellClass, editable: false, width: '15%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HRDeadlineRegister)}', datafield: 'registerThruDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellClassName: cellClass, editable: false, width: '15%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonLocation)}', datafield: 'location', editable: false, cellClassName: cellClass, width: '13%'},
						{text: '${StringUtil.wrapString(uiLabelMap.AmountEstimatedEmplPaid)}', datafield: 'estimatedEmplPaid', cellClassName: cellClass, width: '12%', 
							columntype: 'numberinput', cellsalign: 'right', editable: false,
							cellsrenderer: function (row, column, value){
								if(typeof(value) != 'undefined'){
									return '<span>'+ formatcurrency(value) + '</span>';
								}
								return '<span>'+ value + '</span>';
							},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonCertificate)}', datafield: 'certificate', width: '17%', cellClassName: cellClass, editable: false,}
						"/>
</script>						

<@jqGrid filtersimplemode="true" addType="popup" dataField=datafield columnlist=columnlist clearfilteringbutton="true" showtoolbar="true"  
		 filterable="true" alternativeAddPopup="newTrainingWindow" deleterow="false" editable="true" addrow="false"
		 url="jqxGeneralServicer?sname=JQGetListTrainingForEmplRegister" id="jqxgrid" removeUrl="" deleteColumn="" showlist="false"
		 updateUrl="" editColumns="" jqGridMinimumLibEnable="false" mouseRightMenu="true" contextMenuId="TrainingMenu" 
	/>

<div id='TrainingMenu' style="display:none;">
	<ul>
	    <li><i class="fa fa-unlock"></i>${StringUtil.wrapString(uiLabelMap.Registered)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<div id="registerTrainingWindow" class="hide">
	<div>${uiLabelMap.EmplRegisterTrainingCourse}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.TrainingCourseIdShort}</label>
							</div>  
							<div class="span7">
								<input type="text" id="trainingCourseId">
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonFromDate}</label>
							</div>  
							<div class="span7">
								<div id="fromDate"></div>
					   		</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRRegisterStartDateShort}</label>
							</div>  
							<div class="span7">
								<div id="registerFromDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.CommonLocation}</label>
							</div>  
							<div class="span7">
								<div id="geoId"></div>
					   		</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
	  						<div class='span5 text-algin-right'>
	  							<label class="">${uiLabelMap.AmountEstimatedEmplPaid}</label>
	  						</div>
	  						<div class="span7">
	  							<div id="amountEmplPaid"></div>
	  						</div>
	  					</div>	
	  					<div class='row-fluid margin-bottom10'>
		  					<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.TrainingProvider}</label>
							</div>  
							<div class="span7">
								<div id="trainingProvider"></div>
					   		</div>
		  				</div>	
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.TrainingCourseNameShort}</label>
							</div>  
							<div class="span7">
								<input type="text" id="trainingCourseName">
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRCommonThruDate}</label>
							</div>  
							<div class="span7">
								<div id="thruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRRegisterEndDateShort}</label>
							</div>  
							<div class="span7">
								<div id="registerThruDate"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.TrainingFormTypeId}</label>
							</div>  
							<div class="span7">
								<div id="trainingFormTypeId"></div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
	  						<div class='span5 text-algin-right'>
	  							<label class="">${uiLabelMap.AmountCompanySupport}</label>
	  						</div>
	  						<div class="span7">
	  							<div id="amountCompanyPaid"></div>
	  						</div>
	  					</div>
	  					<div class='row-fluid margin-bottom10'>
		  					<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.HRAddrContactShort}</label>
							</div>
							 <div class="span7">
							 	<input type="text" id="providerContact">
							 </div>
	  					</div>	
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12">
    				<div class='row-fluid margin-bottom10'>
						<div class='span2 text-algin-right' style="margin-left: 29px">
							<label class="">${uiLabelMap.CommonPurpose}</label>
						</div>  
						<div class="span9">
							<input id="trainingPurposeTypeId" type="text">
				   		</div>
					</div>
    			</div>
			</div>
			<div class="row-fluid">
    			<div class="span12">
    				<div class='row-fluid margin-bottom10'>
    					<div class='span2 text-algin-right' style="margin-left: 29px">
		    				<label class="">${uiLabelMap.CommonCertificate}</label>
		    			</div>
		    			<div class="span9">
							<input type="text" id="certificate">
				   		</div>
    				</div>
    			</div>
    		</div>
    		<div class="row-fluid">
				<div class="span12">
					<div class='row-fluid margin-bottom10'>
						<div class='span2 text-algin-right' style="margin-left: 29px">
		    				<label class="">${uiLabelMap.Description}</label>
		    			</div>
		    			<div class="span9">
							<div id="description"></div>
				   		</div>
					</div>
				</div>
			</div>
    		<div class="row-fluid">
				<div class="span12">
					<div class='span2 text-algin-right' style="margin-left: 29px">
	    				<label class="">${uiLabelMap.HRNotes}</label>
	    			</div>
	    			<div class="span9">
						<div><label id="registerWarning"></label></div>
			   		</div>
				</div>
			</div>
    		<div class="row-fluid margin-bottom10">
				<div class="span12">
					<div class="span2 text-algin-right" style="margin-left: 29px">
						<label>${uiLabelMap.CommonStatus}</label>
					</div>
					<div class="span9">
						<input type="text" id="statusId">
					</div>
				</div>
			</div>
    		<div class="row-fluid">
    			<div class="span12">
    				<div class='span2 text-algin-right' style="margin-left: 29px">
	    				<label class=""></label>
	    			</div>
	    			<div class="span9">
	    				<div class="row-fluid hide" id="registerArea">
		    				<div class="span6">
			    				<div id="registerAttTrainingCoure" style="margin-left: -3px !important">
									<span style="font-size: 14px"><b>${uiLabelMap.RegisterTrainingCourse}</b></span>
								</div>
		    				</div>
		    				<div class="span6">
		    					<div id="cancelRegisterAttTrainingCourse" style="margin-left : 0 !important" class="hide">
		    						<span style="font-size: 14px"><b>${uiLabelMap.TrainingCancelRegister}</b></span>
	    						</div>
		    				</div>
	    				</div>
	    				<div class="row-fluid hide" id="registerOutOfTimeArea">
	    					<span style="font-size: 14px"><b>${uiLabelMap.TrainingRegisterOutOfTime}</b></span>
	    				</div>
			   		</div>
    			</div>
    		</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
		<div class="row-fluid no-left-margin">
			<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
				<div class="loader-page-common-custom" id="spinner-ajax"></div>
			</div>
		</div>
	</div>
</div>			
<script type="text/javascript" src="/hrresources/js/training/trainingViewEmplRegisterTraining.js"></script>

	