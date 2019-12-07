<style>
	.buttonRt{
		background : rgba(240, 248, 255, 0) !important;
		color : #438eb9 !important;
		border: aqua;
		float : right;
		margin-left : 10px;
	}
</style>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign monthStart = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign monthEnd = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(prevMonthStart, timeZone, locale)/>
<#assign dataField = "[
			{name: 'criteriaId', type : 'string'},
			{name : 'criteriaName', type : 'string'},
			{name : 'parentTypeId', type : 'string'},
			{name : 'fromDate', type : 'date', other : 'Timestamp'},
			{name : 'thruDate', type : 'date', other: 'Timestamp'}
	]"/>

<#assign columnlist = "
	{text : '${StringUtil.wrapString(uiLabelMap.CriteriaId)}', width : '10%', dataField : 'criteriaId'},
	{text : '${StringUtil.wrapString(uiLabelMap.CriteriaName)}', dataField : 'criteriaName'},
	{text : '${StringUtil.wrapString(uiLabelMap.parentCriteriaId)}', width : '10%', dataField : 'parentTypeId'},
	{text : '${StringUtil.wrapString(uiLabelMap.fromDate)}', width : '20%', dataField : 'fromDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'},
	{text : '${StringUtil.wrapString(uiLabelMap.thruDate)}', width : '20%', dataField : 'thruDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'}
"/>
<div class="widget-box transparent no-bottom-border">
		<div class="widget-header">
			<h4>${uiLabelMap.GeneralStandardRating}</h4>
			<div class="widget-toolbar none-content">
			</div>
		</div>
		<div class="widget-body">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span2' style="text-align: center;">
								<b>${uiLabelMap.Time}</b>
							</div>
							<div class="span7">
								<div id="dateTimeInput"></div>						
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
			<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" addType="popup" alternativeAddPopup="alterpopupWindowParent" addrow="true"
				 url="jqxGeneralServicer?sname=GetListCriteriaAndDetail&fromDatefilter=null&thruDatefilter=null" initrowdetails="true" initrowdetailsDetail="initrowdetailsDetail" contextMenuId="contextMenu" mouseRightMenu="true"
			 	createUrl="jqxGeneralServicer?sname=CreateParentCriteria&jqaction=C" addColumns="criteriaName;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
				removeUrl="jqxGeneralServicer?sname=DeleteCriteria&jqaction=C" deleteColumn="criteriaId" deleterow="true"
				/>		
			</div>
		</div>
</div>
<div id="contextMenu" style="display:none;">
	<ul>
		<li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.ViewChildCriteria)}</li>
	</ul>
</div>
<div id="alterpopupWindowParent" style="display:none;">
	<div>${uiLabelMap.CreateNewCriteria}</div>
	<div style="over-flow:hidden">
		<form id="alterpopupWindowParentForm" class="form-horizontal">
			<div class='row-fluid form-window-content'>
				<div class='span12'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.CriteriaName}
						</div>
						<div class='span7'>
							<input id="CriteriaNameParent" type="text"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.fromDate}
						</div>
						<div class='span7'>
							<div id="fromDateParent"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.thruDate}
						</div>
						<div class='span7'>
							<div id="thruDateParent"></div>
						</div>
					</div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel1" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave1" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<div id="alterpopupWindow" style="display:none;">
	<div>${uiLabelMap.CreateNewChildCriteria}</div>
	<div style="over-flow:hidden">
		<form id="alterpopupWindowForm" class="form-horizontal"> 
			<div class='row-fluid form-window-content'>
				<div class='span12'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.CriteriaName}
						</div>
						<div class='span7'>
							<input id="CriteriaName" type="text"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.fromDate}
						</div>
						<div class='span7'>
							<div id="fromDate"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.thruDate}
						</div>
						<div class='span7'>
							<div id="thruDate"></div>
						</div>
					</div>
				</div>
			</div>
		</form>
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
<div id="alterpopupViewDetail" style="display:none;">
	<div>${uiLabelMap.CriteriaChild}</div>
	<div style="over-flow:hidden">
		<form id="alterpopupWindowDetail" class="form-horizontal">
			<div class='row-fluid form-window-content'>
				<div class="span12">
					<div id="gridDetail"></div>
				</div>
			</div>
		</form>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Exit}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	$.jqx.theme = 'olbius';
	theme  = $.jqx.theme;
	var criteriaIdParent = null;
//	dateTimeinput
	$("#dateTimeInput").jqxDateTimeInput({width: 250, height: 25,  selectionMode: 'range', theme: 'olbius'});
	var fromDate = new Date(${monthStart.getTime()});
	var thruDate = new Date(${monthEnd.getTime()});
	$("#dateTimeInput").on('change', function(event){
		var selection = $("#dateTimeInput").jqxDateTimeInput('getRange');
		var fromDate = selection.from.getTime();
	    var thruDate = selection.to.getTime();
	    refreshGridData(fromDate, thruDate);
	});
	function refreshGridData(fromDate,thruDate){
		var tmpS = $("#jqxgrid").jqxGrid('source');
		tmpS._source.url = "jqxGeneralServicer?sname=GetListCriteriaAndDetail&fromDatefilter=" + fromDate + "&thruDatefilter=" + thruDate;
		$("#jqxgrid").jqxGrid('updatebounddata');
	}
//	end
	var initrowdetailsDetail = function(index, parentElement, gridElement, datarecord){
		var tabsdiv = null;
		tabsdiv = $(parentElement);
		var tabsdivtoolbar = $($(parentElement).children()[0]);
		if(tabsdiv){
			var photocolumncustom = $('<div id=\"tmp\" style=\"margin : 3px 10px 18px 10px; width: auto;\"></div>');
			tabsdivtoolbar.append($(photocolumncustom));
			criteriaIdParent = datarecord.criteriaId;
			var dataField = [
	             {name : 'criteriaId', type : 'string'},
	             {name : 'criteriaName', type : 'string'},
	             {name : 'fromDate', type : 'date', other : 'Timestamp'},
	             {name : 'thruDate', type : 'date', other: 'Timestamp'}
	         ];
			var column = [
	          	{text : '${StringUtil.wrapString(uiLabelMap.CriteriaId)}', width : '20%', datafield : 'criteriaId'},
	          	{text : '${StringUtil.wrapString(uiLabelMap.CriteriaName)}', datafield : 'criteriaName'},
	          	{text : '${StringUtil.wrapString(uiLabelMap.fromDate)}', width : '20%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'},
	          	{text : '${StringUtil.wrapString(uiLabelMap.thruDate)}', width : '20%', datafield : 'thruDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'}
	         ];
			var grid = $('<div ></div>');
			$(grid).attr('id','jqxgridDetail'+index);
			tabsdiv.append($(grid));
			GridUtils.initGrid({url:'GetListCriteriaChild&criteriaIdParent=' + criteriaIdParent,width :'100%',height: '92%',localization: getLocalization()},dataField,column,null,grid);
			var buttoncustom = $('<div><button id="displayPopup" class="buttonRt"  onclick="displayPopup(\'' + datarecord.criteriaId + '\',\'' + index + '\')"><i class="icon-plus-sign"></i>&nbsp;${uiLabelMap.CreateNew}</button></div>');
			photocolumncustom.append(buttoncustom);
		}
	}
//	alterpopupDetail
	$("#alterpopupViewDetail").jqxWindow({width:640,height:250, theme:theme,cancelButton : $("#alterCancel2"),modalOpacity: 0.7,isModal: true, autoOpen: false,resizable: true});
	var dataFieldDetail = [
	                       {name : 'criteriaId', type : 'string'},
	                       {name : 'criteriaName', type : 'string'},
	                       {name : 'fromDate', type : 'date', other : 'Timestamp'},
	                       {name : 'thruDate', type : 'date', other: 'Timestamp'}
	               ];
 	var columnDetail = [
             {text : '${StringUtil.wrapString(uiLabelMap.CriteriaId)}', width : '20%', datafield : 'criteriaId'},
             {text : '${StringUtil.wrapString(uiLabelMap.CriteriaName)}', datafield : 'criteriaName'},
           	 {text : '${StringUtil.wrapString(uiLabelMap.fromDate)}', width : '20%', datafield : 'fromDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'},
        	 {text : '${StringUtil.wrapString(uiLabelMap.thruDate)}', width : '20%', datafield : 'thruDate', cellsformat : 'dd/MM/yyyy HH:mm:ss'}
     ];
 	GridUtils.initGrid({url:'GetListCriteriaChild&criteriaIdParent=' + criteriaIdParent,width :'100%',localization: getLocalization()},dataFieldDetail,columnDetail,null,$('#gridDetail'));
	//	end
//	contextMenu
	$("#contextMenu").jqxMenu({width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function(event){
		var args = event.args;
		var tmpkey = $.trim($(args).text());
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		criteriaIdParent = data.criteriaId;
		var source = $('#gridDetail').jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=GetListCriteriaChild&criteriaIdParent=" + criteriaIdParent;
		$('#gridDetail').jqxGrid('updatebounddata');
     	if(tmpkey == '${StringUtil.wrapString(uiLabelMap.ViewChildCriteria)}'){
         	$("#alterpopupViewDetail").jqxWindow('open');
		}
	})
//	end
//	alterpopupparent
	$("#alterpopupWindowParent").jqxWindow({width:480,height:250, theme:theme,cancelButton : $("#alterCancel1"),modalOpacity: 0.7,isModal: true, autoOpen: false,resizable: true});
	$("#CriteriaNameParent").jqxInput({width:220, height:18});
	$("#fromDateParent").jqxDateTimeInput({width:225, height: 22, allowNullDate: true, value : null, formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#thruDateParent").jqxDateTimeInput({width:225, height: 22, allowNullDate: true, value : null, formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#alterpopupWindowParentForm").jqxValidator({
		rules : [
		         {input : '#CriteriaNameParent', message : '${StringUtil.wrapString(uiLabelMap.ThisFieldIsNotBeEmpty)}', action : 'blur,keyup', rule : 'required'},
		         {input : '#fromDateParent', message : '${StringUtil.wrapString(uiLabelMap.ThisFieldIsNotBeEmpty)}', action : 'blur,keyup' , rule : function(input,commit){
		        	 if($('#fromDateParent').jqxDateTimeInput('getDate') == null || $('#fromDateParent').jqxDateTimeInput('getDate') == ""){
		        		 return false;
		        	 }
		        	 return true;
		         }},
		         {input : '#fromDateParent', message : '${StringUtil.wrapString(uiLabelMap.RequireMustBeBetterThanNow)}', action : 'blur', rule : function(){
		        	 var now = new Date();
		        	 if($('#fromDateParent').jqxDateTimeInput('getDate').getTime() <= now){
		        		 return false;
		        	 }
		        	 return true;
		         }}
         ]
	});
	$('#fromDateParent').on('change', function(){
		$('#alterpopupWindowParentForm').jqxValidator('validateInput', '#fromDateParent');
	})
	$("#alterSave1").click(function(){
		$('#alterpopupWindowParentForm').jqxValidator('validate');
	})
	$("#alterpopupWindowParentForm").on('validationSuccess', function(){
		var row ={};
		if($('#thruDateParent').jqxDateTimeInput('getDate')){
			row = {
					criteriaName : $('#CriteriaNameParent').val(),
					fromDate : $('#fromDateParent').jqxDateTimeInput('getDate'),
					thruDate : $('#thruDateParent').jqxDateTimeInput('getDate')
			}
		}else{
			row = {
					criteriaName : $('#CriteriaNameParent').val(),
					fromDate : $('#fromDateParent').jqxDateTimeInput('getDate'),
					thruDate : null
			};
		}
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        // select the first row and clear the selection.
        $("#jqxgrid").jqxGrid('clearSelection');                        
        $("#jqxgrid").jqxGrid('selectRow', 0);  
		$('#alterpopupWindowParent').jqxWindow('close');
	})
//	end
//	alterpopup
	$("#alterpopupWindow").jqxWindow({width:480,height:250, theme:theme,cancelButton : $("#alterCancel"),modalOpacity: 0.7,isModal: true, autoOpen: false,resizable: true});
	$("#CriteriaName").jqxInput({width:220, height:18});
	$("#fromDate").jqxDateTimeInput({width:225, height: 22, allowNullDate: true, value : null, formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#thruDate").jqxDateTimeInput({width:225, height: 22, allowNullDate: true, value : null, formatString : 'dd/MM/yyyy HH:mm:ss'});
	$("#alterpopupWindowForm").jqxValidator({
		rules : [
		         {input : '#CriteriaName', message : '${StringUtil.wrapString(uiLabelMap.ThisFieldIsNotBeEmpty)}', action : 'blur,keyup', rule : 'required'},
		         {input : '#fromDate', message : '${StringUtil.wrapString(uiLabelMap.ThisFieldIsNotBeEmpty)}', action : 'blur,keyup' , rule : function(input,commit){
		        	 if($('#fromDate').jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == ""){
		        		 return false;
		        	 }
		        	 return true;
		         }},
		         {input : '#fromDate', message : '${StringUtil.wrapString(uiLabelMap.RequireMustBeBetterThanNow)}', action : 'blur', rule : function(){
		        	 var now = new Date();
		        	 if($('#fromDate').jqxDateTimeInput('getDate').getTime() <= now){
		        		 return false;
		        	 }
		        	 return true;
		         }}
         ]
	});
	function displayPopup(criteriaId,index){
		$("#alterpopupWindow").jqxWindow('open');
		$('#fromDate').on('change', function(){
			$('#alterpopupWindowForm').jqxValidator('validateInput', '#fromDate');
		})
		$('#alterSave').click(function(){
			$("#alterpopupWindowForm").jqxValidator('validate');
		})
		$('#alterpopupWindowForm').on('validationSuccess', function(){
			var fromDate = formatDate($('#fromDate').jqxDateTimeInput('getDate').getTime());
			var thruDate = formatDate($('#thruDate').jqxDateTimeInput('getDate'));
			var data = {
					criteriaName : $('#CriteriaName').val(),
					fromDate : fromDate,
					thruDate : thruDate,
					parentTypeId : criteriaId,
			};
			$.ajax({
				type : 'POST',
				data : data,
				url : 'CreateNewChildCriteria',
				datatype : 'array',
				success : function(data){
					$('#alterpopupWindow').jqxWindow('close');
					$('#jqxgridDetail' + index).jqxGrid('updatebounddata');
				}
			})
		})
	}
//	end
	var formatDate = function(val){
		   var date = new Date(val);
		   var newFormat;
		   if(date){
		    newFormat = date.format('yyyy-mm-dd HH:MM:ss');
		   }
		   return newFormat;
		  }
</script>