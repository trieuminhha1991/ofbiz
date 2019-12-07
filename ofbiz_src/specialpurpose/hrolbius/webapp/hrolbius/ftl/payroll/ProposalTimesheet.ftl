<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxdatatable.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxnotification.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script src="/aceadmin/assets/js/spin.min.js" type="text/javascript"></script>
<script src="/hrolbius/js/bootbox.js" type="text/javascript"></script>
<#include "/hrolbius/webapp/hrolbius/ftl/js/commonUtil.ftl"/>
<script type="text/javascript">
	var emplTimekeepingSignArr = new Array();
	<#if listEmplTimekeepingSign?has_content>
		<#list listEmplTimekeepingSign as emplTimekeepingSign>
			var row = {};
			row["emplTimekeepingSignId"] = "${emplTimekeepingSign.emplTimekeepingSignId}";
			row["description"] = "${StringUtil.wrapString(emplTimekeepingSign.description)}";
			row["sign"] = "${StringUtil.wrapString(emplTimekeepingSign.sign)}";
			emplTimekeepingSignArr[${emplTimekeepingSign_index}] = row;
		</#list>
	</#if>
	var statusArr = new Array();
	<#if statusList?has_content>
		<#list statusList as status>
			var row = {};
			row["statusId"] = "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusArr[${status_index}] = row;
		</#list>
	</#if>
	var dataFieldTimesheet = [{'name': 'partyId', 'type': 'string'},
		                     {'name': 'emplTimesheetId', 'type': 'string'},
		                     {'name': 'partyName', 'type': 'string'}];
	var columnsTimesheet =[{'datafield': 'emplTimesheetId', 'width': 100, 'cellsalign': 'left', 'hidden': true},
					    {'text': '${uiLabelMap.EmployeeId}', 'datafield': 'partyId', 'width': 100, 'cellsalign': 'left', 'editable': false, 'pinned': true},
					    {'text': '${uiLabelMap.EmployeeName}', 'datafield': 'partyName', 'cellsalign': 'left', 'editable': false, 'pinned': true}
					    ]
	var fromDate = new Date(${emplTimesheet.fromDate.getTime()});		 
	var thruDate = new Date(${emplTimesheet.thruDate.getTime()});
	while(fromDate.getTime() < thruDate.getTime()){
		var date = fromDate.getDate() + "/" + fromDate.getMonth() + "/" + fromDate.getFullYear();
		var textDate = fromDate.getDate() + "/" + (fromDate.getMonth() + 1) + " - " + weekday[fromDate.getDay()];
		dataFieldTimesheet.push({"name": date, "type": "string"});
		dataFieldTimesheet.push({"name": date + "_hours", "type": "string"});
		columnsTimesheet.push({datafield: date + "_hours", hidden: true});
		columnsTimesheet.push({"text": textDate, datafield: date, width: 85, cellsalign: 'center',
									cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
										var valueRet = "";
										//"value" is object type and have format like that {emplTimekeepingSignId: hours}, so "key" in "value" is contain "emplTimekeepingSignId" 
										if(value){
											var keys = Object.keys(value);													
											for(var i = 0; i < keys.length; i++){
												for(var j = 0; j < emplTimekeepingSignArr.length; j++){
													if(emplTimekeepingSignArr[j].emplTimekeepingSignId == keys[i]){
														valueRet += emplTimekeepingSignArr[j].sign;			
													}
												}
												if(i < keys.length - 1){
													valueRet += ", ";
												}
											}
											return '<div style="text-align:center">' +valueRet + '</div>';
										}else{
											return value;
										}
									}
							  });
		fromDate.setDate(fromDate.getDate() + 1); 
	}
	var sourceTimesheet = {
		datatype: "json",
		type: 'POST',
		data: {emplTimesheetId: ${emplTimesheet.emplTimesheetId}},
		datafields: dataFieldTimesheet,
		url: 'getEmplTimesheetAttendance',
		root: 'listIterator',
		beforeprocessing: function (data) {
			sourceTimesheet.totalrecords = data.TotalRows;
	      },
	      id: 'partyId',
	      pagenum: 0,
	      pagesize: 20,
	      pager: function (pagenum, pagesize, oldpagenum) {
	          // callback called when a page or page size is changed.
	      }
	};
	var dataAdapterTimesheet = new $.jqx.dataAdapter(sourceTimesheet);
	$(document).ready(function(){
		$("#jqxTimesheetAtt").jqxGrid({
			width: '100%', 
			autoheight: true,
	        source: dataAdapterTimesheet,
	        columnsheight: 30,
	        pageSizeOptions: ['20', '30', '50', '100', '200'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        columns: columnsTimesheet,
	        selectionmode: 'singlecell',
	        virtualmode: true,
	        rendergridrows: function () {
	            return dataAdapterTimesheet.records;
	        },
	        theme: 'olbius'
		});
	});
	
</script>
<div class="row-fluid">
	<div id="ntfContainer">
		<div id="jqxNotification"></div>
	</div>
</div>
<div class="row-fluid">
	<div class="row-fluid">
		<div class="span12">
			<div class="clearfix" id="updateApprovalTimesheet">
				<div class="pull-left alert inline no-margin">
					<b>${uiLabelMap.EmplTimesheetList}: </b>${emplTimesheet.emplTimesheetName?if_exists}&nbsp;- 
					<b>${uiLabelMap.CommonStatus}:</b>&nbsp;
					<#assign statusCurr = delegator.findOne("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusId", emplTimesheet.statusId), false)>
					${statusCurr.description?if_exists}
				</div>
				<#if emplTimesheet.statusId == "EMPL_TS_PROPOSAL">
					<div class="pull-right">
						<span class="widget-toolbar none-content">
							<a title="${uiLabelMap.HrCommonApproval}" class="actionApproval" href="javascript:void(0)" id="acceptTimesheet"><i class="icon-ok"></i></a>
							<a title="${uiLabelMap.HrCommonNotApproval}" class="actionApproval" href="javascript:void(0)" id="rejectTimesheet"><i class="icon-remove"></i></a>
						</span>						
					</div>
				</#if>
			</div>
		</div>
	</div>
	<div class="hr dotted"></div>
	<div class="row-fluid">
		<div class="tabbable">
			<ul class="nav nav-tabs padding-18">
				<li class="active">
					<a data-toggle="tab" href="#EmplTimesheetOverview">
						<i class=""></i>
						${uiLabelMap.EmplTimekeepingReportTilte}
					</a>
				</li>
	
				<li>
					<a data-toggle="tab" href="#EmplTimesheetDetail">
						<i class=""></i>
						${uiLabelMap.EmplTimekeepingDetailReport}
					</a>
				</li>
	
				<li>
					<a data-toggle="tab" href="#EmplWorkOvertime">
						<i class=""></i>
						${uiLabelMap.EmplWorkingOvertime}
					</a>
				</li>
	
				<li>
					<a data-toggle="tab" href="#EmplWorkingLate">
						<i class=""></i>
						${uiLabelMap.EmplWorkingLateOverView}
					</a>
				</li>
			</ul>
			<div class="tab-content no-border">
				<div id="EmplTimesheetOverview" class="tab-pane in active">
					<div class="row-fluid">
						<div class="span12">
							<@htmlTemplate.renderEmplTimesheetOverview id="jqxEmplTimesheetGeneral" updaterow="false" jqxGridInWindow="false" width="100%" 
								autoheight="true"/>
						</div> 
					</div>
				</div>
				<div id="EmplTimesheetDetail" class="tab-pane">
					<div class="row-fluid">
						<div id="jqxTimesheetAtt"></div>
					</div>
				</div>
				<div id="EmplWorkOvertime" class="tab-pane">
					<div class="row-fluid">
						<@htmlTemplate.renderEmplWorkOverTime  id="jqxGridEmplWorkOvertime" updaterow="false" jqxNotifyId="jqxNotifyEmplWorkOvertime" jqxGridInWindow="false" showtoolbar="false"/> 
					</div>
				</div>
				<div id="EmplWorkingLate" class="tab-pane">
					<div class="row-fluid">
						<@htmlTemplate.renderEmplWorkingLate id="jqxGridEmplWorkingLate" updaterow="false" jqxNotifyId="jqxNotifyEmplWorkingLate" jqxGridInWindow="false"/> 
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(document).ready(function () {
		var fromDate = new Date(${emplTimesheet.fromDate.getTime()});
		var thruDate = new Date(${emplTimesheet.thruDate.getTime()});
		updateApprovalWorkOvertime(fromDate, thruDate);
		overallEmplTimesheets("${parameters.emplTimesheetId}");
		overallEmplWorkingLate("${parameters.emplTimesheetId}");
		$('[data-toggle="tab"]').on('click', function(e){
			var target = $(e.target);
			var which = parseInt($.trim(target.text()));
			
		});
		$.fn.spin = function(opts) {
			this.each(function() {
			  var $this = $(this),
				  data = $this.data();
	
			  if (data.spinner) {
				data.spinner.stop();
				delete data.spinner;
			  }
			  if (opts !== false) {
				data.spinner = new Spinner($.extend({color: $this.css('color')}, opts)).spin(this);
			  }
			});
			return this;
		};
		$("#acceptTimesheet").click(function(event){
			if(!$("#acceptTimesheet").attr("disabled")!="disabled"){
				disableAndSpinDiv($("#updateApprovalTimesheet"));
				$("#acceptTimesheet").attr("disabled","disabled");
				$.ajax({
					url: "approvalTimesheet",
					type: 'POST',
					data:{emplTimesheetId: ${emplTimesheet.emplTimesheetId}, statusId: "EMPL_TS_APPR", ntfId: "${parameters.ntfId?if_exists}"},
					success: function(data){
						if(data._EVENT_MESSAGE_){
							$(".actionApproval").remove();
							$("#jqxNotification").text(data._EVENT_MESSAGE_);
							$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#ntfContainer", opacity: 0.9, autoClose: false, template: "info" });
							$("#jqxNotification").jqxNotification("open");
						}else{
							$("#acceptTimesheet").removeAttr("disabled");
							$("#jqxNotification").text(data._ERROR_MESSAGE_);
							$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#ntfContainer", opacity: 0.9, autoClose: false, template: "error" });
							$("#jqxNotification").jqxNotification("open");
						}
					},
					complete: function(){
						disableSpin($("#updateApprovalTimesheet").parent());
						$("#updateApprovalTimesheet").css("opacity", 1);
					}
				});
			}
		});
		$("#rejectTimesheet").click(function(event){
			if($("#rejectTimesheet").attr("disabled")!="disabled"){
				disableAndSpinDiv($("#updateApprovalTimesheet"));
				$("#rejectTimesheet").attr("disabled","disabled");
				$.ajax({
					url: "approvalTimesheet",
					type: 'POST',
					data:{emplTimesheetId: ${emplTimesheet.emplTimesheetId}, statusId: "EMPL_TS_NOT_APPR", ntfId: "${parameters.ntfId?if_exists}"},
					success: function(data){
						if(data._EVENT_MESSAGE_){
							$(".actionApproval").remove();
							$("#jqxNotification").text(data._EVENT_MESSAGE_);
							$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#ntfContainer", opacity: 0.9, autoClose: false, template: "info" });
							$("#jqxNotification").jqxNotification("open");
						}else{
							$("#rejectTimesheet").removeAttr("disabled");
							$("#jqxNotification").text(data._ERROR_MESSAGE_);
							$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#ntfContainer", opacity: 0.9, autoClose: false, template: "error" });
							$("#jqxNotification").jqxNotification("open");
						}
					},
					complete: function(){
						disableSpin($("#updateApprovalTimesheet").parent());
						$("#updateApprovalTimesheet").css("opacity", 1);
					}
				});
			}
		});
	});
	function disableAndSpinDiv(divEle){
		divEle.parent().spin();
		divEle.css("opacity", "0.4");
	}
	function disableSpin(ele){
		ele.spin(false);
	}
	function setDropdownContent(element, jqxTree, dropdownBtn){
		var item = jqxTree.jqxTree('getItem', element);
		var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + item.label + '</div>';
		dropdownBtn.jqxDropDownButton('setContent', dropDownContent);
	}
</script>