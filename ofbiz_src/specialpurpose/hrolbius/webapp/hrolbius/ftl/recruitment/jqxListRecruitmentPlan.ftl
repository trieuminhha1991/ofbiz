<#--IMPORT LIB-->
<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<#--/IMPORT LIB-->
<#--============================================== PREPARE DATA=========================================-->
<script>
	//Prepare for DeptPositionTypeDetail data
	<#assign listEmplPositionTypes = delegator.findByAnd("DeptPositionTypeDetail", {"deptId" : '${parameters.partyId}'}, null, false)>
	emplPositionTypeData = [
	              <#list listEmplPositionTypes as item>
					<#assign description = StringUtil.wrapString(item.description?if_exists) />
					{'emplPositionTypeId': '${item.emplPositionTypeId}', 'description': '${description}'},
				  </#list>
				];
</script>
<#--============================================== /PREPARE DATA=========================================-->
<#--==============================================Create Plan Detail====================================-->
<div class="row-fluid" >
	<div class="span12">
		<div id="jqxNotification">
		    <div id="notificationContent">
		    </div>
		</div>
		<div id="notificationContainer"></div>
		<div id="jqxgridPlanDetail"></div>
	</div>
</div>
<style>
	.cell-green-color {
	    color: black !important;
	    background-color: #33CCFF !important;
	}
</style>
<#--==============================================/Create Plan Detail====================================-->
<#--==============================================Create Approve Window=====================================-->
<#include "jqxApprove.ftl" />
<#--==============================================/Create Approve Window====================================-->
<script>
	var JQXAction = function(theme){
		this.theme = theme;
	};
	JQXAction.bindData = function(){
		var recuitmentPlanData = new Array();
		var dataSource = new Array();
		$.ajax({
            url: "getRecruitmentPlan",
            type: "POST",
            cache: false,
            datatype: 'json',
            async: false,
            data: {partyId : '${parameters.partyId}', year: '${parameters.year}'},
            success: function (data, status, xhr) {
            	if(!data._ERROR_MESSAGE_){
            		recuitmentPlanData = data.listGenericValue;
            	}
            }
        });
		var firstMonth = 0;
		var secondMonth = 0;
		var thirdMonth = 0;
		var fourthMonth = 0;
		var fifthMonth = 0;
		var sixthMonth = 0;
		var seventhMonth = 0;
		var eighthMonth = 0;
		var ninthMonth = 0;
		var tenthMonth = 0;
		var eleventhMonth = 0;
		var twelfthMonth = 0;
		
		var index = 0;
		if(recuitmentPlanData.length > 0){
			for(var i = 0; i < recuitmentPlanData.length; i++){
				var row = {};
				row['emplPositionTypeId'] = recuitmentPlanData[i].emplPositionTypeId;
				row['type'] = 'POS';
				row['level'] = '1';
				row['firstMonth']  = recuitmentPlanData[i].firstMonth;
				row['secondMonth']  = recuitmentPlanData[i].secondMonth;
				row['thirdMonth']  = recuitmentPlanData[i].thirdMonth;
				row['fourthMonth']  = recuitmentPlanData[i].fourthMonth;
				row['fifthMonth']  = recuitmentPlanData[i].fifthMonth;
				row['sixthMonth']  = recuitmentPlanData[i].sixthMonth;
				row['seventhMonth']  = recuitmentPlanData[i].seventhMonth;
				row['eighthMonth']  = recuitmentPlanData[i].eighthMonth;
				row['ninthMonth']  = recuitmentPlanData[i].ninthMonth;
				row['tenthMonth']  = recuitmentPlanData[i].tenthMonth;
				row['eleventhMonth']  = recuitmentPlanData[i].eleventhMonth;
				row['twelfthMonth']  = recuitmentPlanData[i].twelfthMonth;
				firstMonth += recuitmentPlanData[i].firstMonth;
				secondMonth += recuitmentPlanData[i].secondMonth;
				thirdMonth += recuitmentPlanData[i].thirdMonth;
				fourthMonth += recuitmentPlanData[i].fourthMonth;
				fifthMonth += recuitmentPlanData[i].fifthMonth;
				sixthMonth += recuitmentPlanData[i].sixthMonth;
				seventhMonth += recuitmentPlanData[i].seventhMonth;
				eighthMonth += recuitmentPlanData[i].eighthMonth;
				ninthMonth += recuitmentPlanData[i].ninthMonth;
				tenthMonth += recuitmentPlanData[i].tenthMonth;
				eleventhMonth += recuitmentPlanData[i].eleventhMonth;
				twelfthMonth += recuitmentPlanData[i].twelfthMonth;
				dataSource[index++] = row;
			}
		}else{
			//Plan is not created
			for(var i = 0; i < emplPositionTypeData.length; i++){
				var row = {};
				row['emplPositionTypeId'] = emplPositionTypeData[i].emplPositionTypeId;
				row['type'] = 'POS';
				row['level'] = 1;
				row['firstMonth']  = 0;
				row['secondMonth']  = 0;
				row['thirdMonth']  = 0;
				row['fourthMonth']  = 0;
				row['fifthMonth']  = 0;
				row['sixthMonth']  = 0;
				row['seventhMonth']  = 0;
				row['eighthMonth']  = 0;
				row['ninthMonth']  = 0;
				row['tenthMonth']  = 0;
				row['eleventhMonth']  = 0;
				row['twelfthMonth']  = 0;
				dataSource[index++] = row;
			}
		}
		//Summary
		var row = {};
		row['emplPositionTypeId'] = '${parameters.partyId}';
		row['type'] = 'DEPT';
		row['level'] = 1;
		row['firstMonth']  = firstMonth;
		row['secondMonth']  = secondMonth;
		row['thirdMonth']  = thirdMonth;
		row['fourthMonth']  = fourthMonth;
		row['fifthMonth']  = fifthMonth;
		row['sixthMonth']  = sixthMonth;
		row['seventhMonth']  = seventhMonth;
		row['eighthMonth']  = eighthMonth;
		row['ninthMonth']  = ninthMonth;
		row['tenthMonth']  = tenthMonth;
		row['eleventhMonth']  = eleventhMonth;
		row['twelfthMonth']  = twelfthMonth;
		dataSource[index++] = row
		return dataSource;
	};
	JQXAction.prototype.createPlanDetailGrid = function(){
		<#assign rph = delegator.findOne("RecruitmentPlanHeader", {"partyId" : parameters.partyId, "year" : parameters.year}, false)>
        var editable = true;
        <#if rph.statusId == "RPH_ACCEPTED" || userLogin.partyId != rph.creatorPartyId>
			editable = false;
        </#if>
		var source =
        {
            localdata: JQXAction.bindData(),
            datatype: "array",
            datafields:
            [
                { name: 'emplPositionTypeId', type: 'string'},
                { name: 'level', type: 'number' },
                { name: 'type', type: 'string' },
                { name: 'firstMonth', type: 'number' },
                { name: 'secondMonth', type: 'number' },
                { name: 'thirdMonth', type: 'number' },
                { name: 'fourthMonth', type: 'number' },
                { name: 'fifthMonth', type: 'number' },
                { name: 'sixthMonth', type: 'number' },
                { name: 'seventhMonth', type: 'number' },
                { name: 'eighthMonth', type: 'number' },
                { name: 'ninthMonth', type: 'number' },
                { name: 'tenthMonth', type: 'number' },
                { name: 'eleventhMonth', type: 'number' },
                { name: 'twelfthMonth', type: 'number' }
            ],
            updaterow: function (rowid, newdata, commit) {
            	$("#jqxgridPlanDetail").jqxGrid('updatebounddata');
            }
        };
        var dataAdapter = new $.jqx.dataAdapter(source);
   
        $("#jqxgridPlanDetail").jqxGrid(
        {
            source: dataAdapter,
            columnsresize: true,
            theme: this.theme,
            autoheight: true,
            pageable: true,
            width: '100%',
            editable: editable,
            pagesize: 100,
            showtoolbar: true,
            rendertoolbar: function (toolbar) {
            	var container = $("<div id='toolbarcontainer' class='widget-header'>");
                toolbar.append(container);
                container.append('<h4></h4>');
                <#if rph.statusId != "RPH_ACCEPTED" && userLogin.partyId == rph.creatorPartyId>
	                container.append('<button id="btnAccept" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-ok"></i>${uiLabelMap.CommonUpdate}</button>');
	                $("#btnAccept").jqxButton({theme: this.theme});
                </#if>
                <#if Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) == "MANAGER" && rph.statusId != "RPH_ACCEPTED">
                	container.append('<button id="btnPropose" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="fa fa-hand-o-right"></i>${uiLabelMap.HRCommonProposal}</button>');
                	$("#btnPropose").jqxButton({theme: this.theme});
                </#if>
            	<#if (userLogin.partyId != rph.creatorPartyId && Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) == "HRMADMIN") || Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) == "PHOTONGGIAMDOC">
	                container.append('<button id="btnApprove" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="fa fa-thumbs-o-up"></i>${uiLabelMap.HRApprove}</button>');
	                $("#btnApprove").jqxButton({theme: this.theme});
                </#if>
                JQXAction.bindEvent(source);
            },
            columns: [
              { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', width: '16%',pinned: true, editable: false,
            	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                      for(var i = 0; i < emplPositionTypeData.length; i++){
                      	if(value == emplPositionTypeData[i].emplPositionTypeId){
                      		return '<span title=' + value + '>' + emplPositionTypeData[i].description + '</span>';
                      	}
                      }
                      return '<span title=' + value + '>' + value + '</span>';
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.FirstMonth}', datafield: 'firstMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SecondMonth}', datafield: 'secondMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.ThirdMonth}', datafield: 'thirdMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.FourthMonth}', datafield: 'fourthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.FifthMonth}', datafield: 'fifthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SixthMonth}', datafield: 'sixthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.SeventhMonth}', datafield: 'seventhMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.EighthMonth}', datafield: 'eighthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.NinthMonth}', datafield: 'ninthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.TenthMonth}', datafield: 'tenthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.EleventhMonth}', datafield: 'eleventhMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              },
              { text: '${uiLabelMap.TwelfthMonth}', datafield: 'twelfthMonth', width: '7%',
            	  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellClassName: function (row, columnfield, value) {
                      var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                      if(rowData['type'] ==  'DEPT'){
                    	  return 'cell-green-color';
                      }
                  },
                  cellbeginedit: function (row, datafield, columntype) {
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'DEPT'){
                		  return false;
                	  }
                  },
                  cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                	  var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
                	  var rowData = $("#jqxgridPlanDetail").jqxGrid('getrowdata', row);
                	  if(rowData['type'] == 'POS' && rowData['level'] == 1){
                		  for(var i = 0; i < rows.length; i++){
                			  if(rows[i].type == 'DEPT' && rows[i].level == 1 && rows[i].partyId == rowData['partyId']){
                				  rows[i][datafield] += newvalue - oldvalue;
                			  }
                		  }
                	  }
                	  source.localdata = rows;
                  }
              }
            ]
        });
	};
	
	JQXAction.bindEvent = function(source){
		var wgdeletesuccess = "${StringUtil.wrapString(uiLabelMap.wgdeletesuccess)}";
        var wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
        var wgproposesuccess = "${StringUtil.wrapString(uiLabelMap.wgproposesuccess)}";
        
        $("#btnApprove").on('click', function(){
        	$("#wdwApprove").jqxWindow('open');
        });
        
        $("#alterSaveAppr").click(function(){
        	var submitData = {};
        	submitData['reason'] = $("#apprComment").val();
        	submitData['statusId'] = $("#jqxAccepted").jqxRadioButton('val') ? "RPH_ACCEPTED" : "RPH_REJECTED";
        	submitData['partyId'] = '${parameters.partyId}';
        	submitData['year'] = '${parameters.year}';
        	//Send Request
        	$.ajax({
        		url: 'updateRecruitmentPlanHeader',
        		type: "POST",
        		data: submitData,
        		dataType: 'json',
        		async: false,
        		success : function(data) {
        			if(!data._ERROR_MESSAGE_){
        				$("#wdwApprove").jqxWindow('close');
        			}else{
        				bootbox.confirm(data._ERROR_MESSAGE_, function(result) {
        					return;
        				});
        			}
                }
        	});
        });
        
        $("#btnPropose").on('click', function(){
        	var submitData = {};
        	submitData['partyId'] = '${parameters.partyId}';
        	submitData['year'] = '${parameters.year}';
        	submitData['statusId'] = 'RPH_PROPOSED';
        	var index = 0;
        	$.ajax({
                url: "updateRecruitmentPlanHeader",
                type: "POST",
                cache: false,
                datatype: 'json',
                data: submitData,
                async: false,
                success: function (data, status, xhr) {
                	if(data._ERROR_MESSAGE_){
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'error' });
                		$("#notificationContent").html(data._ERROR_MESSAGE_);
                        $("#jqxNotification").jqxNotification("open");
                	}else{
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'success' });
                		$("#notificationContent").html(wgproposesuccess);
                        $("#jqxNotification").jqxNotification("open");
                	}
                }
            });
        });
        
		$("#btnAccept").on('click', function () {
    		var rows = $('#jqxgridPlanDetail').jqxGrid('getboundrows');
        	var submitData = {};
        	var index = 0;
        	for(var i = 0; i < rows.length; i++){
        		if(rows[i].type == 'POS'){
        			var rowData = rows[i];
            		submitData['partyId_o_' + index] = '${parameters.partyId}';
            		submitData['emplPositionTypeId_o_' + index] = rowData['emplPositionTypeId'];
            		submitData['year_o_' + index] = '${parameters.year}';
            		submitData['firstMonth_o_' + index] = rowData['firstMonth'];
            		submitData['secondMonth_o_' + index] = rowData['secondMonth'];
            		submitData['thirdMonth_o_' + index] = rowData['thirdMonth'];
            		submitData['fourthMonth_o_' + index] = rowData['fourthMonth'];
            		submitData['fifthMonth_o_' + index] = rowData['fifthMonth'];
            		submitData['sixthMonth_o_' + index] = rowData['sixthMonth'];
            		submitData['seventhMonth_o_' + index] = rowData['seventhMonth'];
            		submitData['eighthMonth_o_' + index] = rowData['eighthMonth'];
            		submitData['ninthMonth_o_' + index] = rowData['ninthMonth'];
            		submitData['tenthMonth_o_' + index] = rowData['tenthMonth'];
            		submitData['eleventhMonth_o_' + index] = rowData['eleventhMonth'];
            		submitData['twelfthMonth_o_' + index] = rowData['twelfthMonth'];
            		index++;
        		}
        	}
        	$.ajax({
                url: "createOrUpdateRecruitmentPlan",
                type: "POST",
                cache: false,
                datatype: 'json',
                data: submitData,
                async: false,
                success: function (data, status, xhr) {
                	if(data._ERROR_MESSAGE_){
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'error' });
                		$("#notificationContent").html(data._ERROR_MESSAGE_);
                        $("#jqxNotification").jqxNotification("open");
                        source.localdata = JQXAction.bindData();
                        $("#jqxgridPlanDetail").jqxGrid('updatebounddata');
                	}else{
                		//Create jqxNotification
                		$("#jqxNotification").jqxNotification({ width: "100%", appendContainer: "#notificationContainer", opacity: 0.9, autoClose: true, template: 'success' });
                		$("#notificationContent").html(wgupdatesuccess);
                        $("#jqxNotification").jqxNotification("open");
                        source.localdata = JQXAction.bindData();
                        $("#jqxgridPlanDetail").jqxGrid('updatebounddata');
                	}
                }
            });
        });
	}
	$(document).ready(function() {
		<#include "jsApprove.ftl" />
		var jqxAction = new JQXAction('olbius');
		jqxAction.createPlanDetailGrid();
	});
</script>