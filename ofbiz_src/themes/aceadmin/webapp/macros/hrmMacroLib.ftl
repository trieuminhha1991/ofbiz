<#macro renderComboxBox name id emplData container width=220 height=30 itemHeight=59 dropDownWidth=300 multiSelect="true" autoDropDownHeight="false">
    <script type="text/javascript">
        $(document).ready(function () {           
            // prepare the data
            var data = new Array();
            var firstNames = new Array();
            var lastNames = new Array();
            var middleName = new Array();
            var partyIds = new Array();
            var genders = new Array();
            var emplPosType = new Array();
            
            <#list emplData as empl>
            	
            	<#if empl?exists>
	            	firstNames[${empl_index?number}] = '${empl.firstName?if_exists}';
	            	lastNames[${empl_index?number}] = '${empl.lastName?if_exists}';
	            	partyIds[${empl_index?number}] = '${empl.partyId?if_exists}';
	            	middleName[${empl_index?number}] = '${empl.middleName?if_exists}';
	            	
	            	<#assign genderEmpl = delegator.findList("Gender", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("genderId", ((empl.gender)?if_exists)), null, null, null, false) />
	            	<#if genderEmpl?has_content>
	            		genders[${empl_index?number}] = '${(genderEmpl.get(0).get("description", locale))?if_exists}';
					<#else>genders[${empl_index?number}] = '';
					</#if>
					
					<#assign emplPosTypeList = Static["com.olbius.util.PartyUtil"].getCurrPositionTypeOfEmpl(delegator, empl.partyId)/>
					<#if emplPosTypeList?has_content>
						<#assign emplPosType = delegator.findOne("EmplPositionType", Static["org.ofbiz.base.util.UtilMisc"].toMap("emplPositionTypeId", emplPosTypeList.get(0).emplPositionTypeId), false)>
						emplPosType[${empl_index?number}] = '${(emplPosType.get("description", locale))?if_exists}';
					<#else>	
						emplPosType[${empl_index?number}] = '';
					</#if>
				</#if>
            </#list>
			
            var k = 0;
            for (var i = 0; i < ${emplData.size()}; i++) {
                var row = {};
                row["firstname"] = firstNames[k];
                row["lastname"] = lastNames[k];
                row["middleName"] = middleName[k];
                row["partyId"] = partyIds[k];
                row["gender"] = genders[k];
                row["emplPosType"] = emplPosType[k];
                row["displayEmpl"] = lastNames[k] + " " + middleName[k] + " " + firstNames[k];
                data[i] = row;
                k++;
            }
            var source =
            {
                localdata: data,
                datatype: "array"
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            $('#${container}').jqxComboBox({ 
            	autoDropDownHeight: ${autoDropDownHeight},  
            	multiSelect: ${multiSelect}, 
            	source: dataAdapter, 
            	displayMember: "displayEmpl",
            	valueMember: "partyId", 
            	height: ${height}, 
            	width: ${width},
            	itemHeight: ${itemHeight},
            	dropDownWidth: ${dropDownWidth},
                renderer: function (index, label, value) {
                    var datarecord = data[index];
					var img = '<img height="45" width="45" src="/aceadmin/assets/avatars/avatar.png"/>';
					var gender = "";
					if(!datarecord.emplPosType){
						datarecord.emplPosType = "${uiLabelMap.Title}: ${uiLabelMap.CommonNotDefined}";
					}
					if(datarecord.gender){
						gender = " - " + datarecord.gender;
					}
					var table = '<table style="min-width: 200px;">'+
								   '<tr>' + 
								   		'<td style="width: 55px;" rowspan="2">'+ img + '</td>' + 
								   		'<td>' + datarecord.lastname + " " + datarecord.middleName + " " + datarecord.firstname + gender + '</td>'+
								   '</tr>'+
								   '<tr>'+
								   		'<td>'+datarecord.emplPosType+'</td>'+'<td></td>' +								   		
								   	'</tr>' +
						   		 '</table>';
                    return table;
                }            
            });
             $('#${container}').on('select', function (event) {
       			var args = event.args;
       				if (args) {
          	 		// index represents the item's index.                          
           			var index = args.index;
           			var item = args.item;
           			if(!item) return;
           			// get item's label and value.
           			var value = item.value;
           			$('#${id}').append("<option selected='true' value=" + value + "></option>");
       			}
   			});
   		
   			$('#${container}').on('unselect', function (event) {
       			var args = event.args;
       				if (args) {
          	 		// index represents the item's index.                          
           			var index = args.index;
           			var item = args.item;
           			if(!item) return;
           			// get item's label and value.
           			var value = item.value;
           			var el = $('#${id}').children("option[value='"+value+"']");
           			el.remove();
       			}
   			});
     });
      
    </script>
    <div id="${container}" style="margin-bottom: 10px;">
    </div>
    <select <#if multiSelect == "true">multiple</#if> style="display: none;" name=${name} id="${id}">
    </select>
 </#macro>
 
 <#macro renderPayrollFormula name id payrollFormulaList divId height width>
 	<script type="text/javascript">
 		(function(){
 			$(document).ready(function() {
		     var data = new Array();
		     var  code= new Array();
		     var name= new Array();
		     var funct= new Array();
		     var container = $('#${divId}');
		     if(!container.is('[class*="jqx"]')){
		     	<#list payrollFormulaList as pf >
			  		code[${pf_index?number}]='${pf.code?if_exists}';
			  		name[${pf_index?number}]='${pf.name?if_exists}';
			  		funct[${pf_index?number}]='${pf.function?if_exists}';
			  	</#list>
			  	var k=0;
			  	for(var i=0; i<${payrollFormulaList.size()}; i++){
			  		var row={};
			  		row["code"]= code[k];
			  		row["name"]= name[k];
			  		row["funct"]= funct[k];
			  		data[i]=row;
			  		k++;
			  	}
			  	
			  	var source= {
			  		localdata: data,
			    	datatype: "array"
			  	}
			 	var dataAdapter = new $.jqx.dataAdapter(source);
			  	container.jqxListBox({ 
		            	multiple: true, 
		            	source: dataAdapter, 
		            	theme: 'energyblue',
		            	displayMember: "name",
		            	valueMember: "code", 
		            	height: "${height}", 
		            	width: "${width}",
		                renderer: function (index, label, value) {
		                    var datarecord = data[index];
		                    var table = '<table style="width: ${width};"><tr><td>' + '<b>'+datarecord.name+'</b>'+ '</br>'+'<i>'+ datarecord.funct+'</i>'+'</td></tr></table>';
		                    return table;
		                }            
		            });
		         container.on('select', function (event) {
	       			var args = event.args;
	       				if (args) {
	          	 		// index represents the item's index.                          
	           			var index = args.index;
	           			var item = args.item;
	           			// get item's label and value.
	           			var value = item.value;
	           			$('#${id}').append("<option selected='true' value=" + value + "></option>");
	       			}
	   			});
	   		
	   			container.on('unselect', function (event) {
	       			var args = event.args;
	       				if (args) {
	          	 		// index represents the item's index.                          
	           			var index = args.index;
	           			var item = args.item;
	           			// get item's label and value.
	           			var value = item.value;
	           			var el = $('#${id}').children("option[value='"+value+"']");
	           			el.remove();
	       			}
	   			});	
		     }
		  });
 		})();
 	</script>

  <div id="${divId}"> </div>
  <select multiple style="display: none" name=${name} id="${id}"> </select>
 </#macro>

<#macro loadingContainer id="default" lines=12 length=7 width=4 radius=10 corners=1 rotate=0 trail=60 speed=1 fixed="true" zIndex=9999>
    <style>
    	.loading-container{
			width: 100%;
			height: 100%;
			top: 0px;
			left: 0px;
			<#if fixed == "true">
				position: fixed;
			<#else>
				position: absolute;
			</#if>
			opacity: 0.7;
			background-color: #fff;
			z-index: ${zIndex};
			text-align: center;
		}
		.spinner-preview{
			margin-top: 0;
			position: absolute;
			left: 50%;
			top: 50%;
			margin-left: -50px;
			margin-top: -50px;
		}
    </style>
    <script src="/aceadmin/assets/js/spin.min.js"></script>
    <script>
    $.fn.spin = function(opts) {
	this.each(function() {
		var $this = $(this), data = $this.data();

		if (data.spinner) {
			data.spinner.stop();
			delete data.spinner;
		}
		if (opts !== false) {
			data.spinner = new Spinner($.extend({
				color : $this.css('color')
			}, opts)).spin(this);
		}
	});
	return this;
};
	var spinnerUpdate = function(opts, id) {
		$('#'+id).spin(opts);
	};
	var showLoading = function(id){
		$("#"+id).show();
	};
	var hideLoading = function(id){
		$("#"+id).hide();
	};
    $(document).ready(function(){
    	var id = "${loadingid?if_exists}";
		var opts = {
			"lines" : ${lines},
			"length" : ${length},
			"width" : ${width},
			"radius" : ${radius},
			"corners" : ${corners},
			"rotate" : ${rotate},
			"trail" : ${trail},
			"speed" : ${speed}
		};
		spinnerUpdate(opts, "spinner-preview${id}"); 	
		hideLoading("loading${id}");
    });

</script>
	<div class='loading-container' id="loading${id?if_exists}">
		<div class="spinner-preview" id="spinner-preview${id?if_exists}"></div>	
	</div>
 </#macro>
 <#global loadingContainer=loadingContainer />
 
<#macro getLocalization>
	var getLocalization = function () {
             var localizationobj = {};
             localizationobj.pagergotopagestring = "${StringUtil.wrapString(uiLabelMap.wgpagergotopagestring)}:";
             localizationobj.pagershowrowsstring = "${StringUtil.wrapString(uiLabelMap.wgpagershowrowsstring)}:";
             localizationobj.pagerrangestring = " ${StringUtil.wrapString(uiLabelMap.wgpagerrangestring)} ";
             localizationobj.pagernextbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagernextbuttonstring)}";
             localizationobj.pagerpreviousbuttonstring = "${StringUtil.wrapString(uiLabelMap.wgpagerpreviousbuttonstring)}";
             localizationobj.sortascendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortascendingstring)}";
             localizationobj.sortdescendingstring = "${StringUtil.wrapString(uiLabelMap.wgsortdescendingstring)}";
             localizationobj.sortremovestring = "${StringUtil.wrapString(uiLabelMap.wgsortremovestring)}";
             localizationobj.emptydatastring = "${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}";
             localizationobj.filterselectstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}";
             localizationobj.filterselectallstring = "${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}";
             localizationobj.filterchoosestring = "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}";
             localizationobj.groupsheaderstring = "${StringUtil.wrapString(uiLabelMap.wgdragDropToGroupColumn)}";
             localizationobj.firstDay = 1;
             localizationobj.percentsymbol = "%";
             localizationobj.currencysymbol = "đ";
             localizationobj.decimalseparator = ",";
             localizationobj.thousandsseparator = ".";
             <#if defaultOrganizationPartyCurrencyUomId?has_content>
                <#if defaultOrganizationPartyCurrencyUomId == "USD">
                    localizationobj.currencysymbol = "$";
                    localizationobj.decimalseparator = ".";
                    localizationobj.thousandsseparator = ",";
                <#elseif defaultOrganizationPartyCurrencyUomId == "EUR">
                    localizationobj.currencysymbol = "€";
                    localizationobj.decimalseparator = ".";
                    localizationobj.thousandsseparator = ",";
                </#if>
             </#if>
             localizationobj.currencysymbolposition = "after";
             
             var days = {
                 // full day names
                 names: ["${StringUtil.wrapString(uiLabelMap.wgmonday)}", "${StringUtil.wrapString(uiLabelMap.wgtuesday)}", "${StringUtil.wrapString(uiLabelMap.wgwednesday)}", "${StringUtil.wrapString(uiLabelMap.wgthursday)}", "${StringUtil.wrapString(uiLabelMap.wgfriday)}", "${StringUtil.wrapString(uiLabelMap.wgsaturday)}", "${StringUtil.wrapString(uiLabelMap.wgsunday)}"],
                 // abbreviated day names
                 namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgamonday)}", "${StringUtil.wrapString(uiLabelMap.wgatuesday)}", "${StringUtil.wrapString(uiLabelMap.wgawednesday)}", "${StringUtil.wrapString(uiLabelMap.wgathursday)}", "${StringUtil.wrapString(uiLabelMap.wgafriday)}", "${StringUtil.wrapString(uiLabelMap.wgasaturday)}", "${StringUtil.wrapString(uiLabelMap.wgasunday)}"],
                 // shortest day names
                 namesShort: ["${StringUtil.wrapString(uiLabelMap.wgsmonday)}", "${StringUtil.wrapString(uiLabelMap.wgstuesday)}", "${StringUtil.wrapString(uiLabelMap.wgswednesday)}", "${StringUtil.wrapString(uiLabelMap.wgsthursday)}", "${StringUtil.wrapString(uiLabelMap.wgsfriday)}", "${StringUtil.wrapString(uiLabelMap.wgssaturday)}", "${StringUtil.wrapString(uiLabelMap.wgssunday)}"],
             };
             localizationobj.days = days;
             var months = {
                 // full month names (13 months for lunar calendards -- 13th month should be "" if not lunar)
                 names: ["${StringUtil.wrapString(uiLabelMap.wgjanuary)}", "${StringUtil.wrapString(uiLabelMap.wgfebruary)}", "${StringUtil.wrapString(uiLabelMap.wgmarch)}", "${StringUtil.wrapString(uiLabelMap.wgapril)}", "${StringUtil.wrapString(uiLabelMap.wgmay)}", "${StringUtil.wrapString(uiLabelMap.wgjune)}", "${StringUtil.wrapString(uiLabelMap.wgjuly)}", "${StringUtil.wrapString(uiLabelMap.wgaugust)}", "${StringUtil.wrapString(uiLabelMap.wgseptember)}", "${StringUtil.wrapString(uiLabelMap.wgoctober)}", "${StringUtil.wrapString(uiLabelMap.wgnovember)}", "${StringUtil.wrapString(uiLabelMap.wgdecember)}", ""],
                 // abbreviated month names
                 namesAbbr: ["${StringUtil.wrapString(uiLabelMap.wgajanuary)}", "${StringUtil.wrapString(uiLabelMap.wgafebruary)}", "${StringUtil.wrapString(uiLabelMap.wgamarch)}", "${StringUtil.wrapString(uiLabelMap.wgaapril)}", "${StringUtil.wrapString(uiLabelMap.wgamay)}", "${StringUtil.wrapString(uiLabelMap.wgajune)}", "${StringUtil.wrapString(uiLabelMap.wgajuly)}", "${StringUtil.wrapString(uiLabelMap.wgaaugust)}", "${StringUtil.wrapString(uiLabelMap.wgaseptember)}", "${StringUtil.wrapString(uiLabelMap.wgaoctober)}", "${StringUtil.wrapString(uiLabelMap.wganovember)}", "${StringUtil.wrapString(uiLabelMap.wgadecember)}", ""],
             };
             var patterns = {
                d: "dd/MM/yyyy",
                D: "dd MMMM yyyy",
                f: "dd MMMM yyyy h:mm tt",
                F: "dd MMMM yyyy h:mm:ss tt",
                M: "dd MMMM",
                Y: "MMMM yyyy"
             }
             localizationobj.patterns = patterns;
             localizationobj.months = months;
             localizationobj.todaystring = "${StringUtil.wrapString(uiLabelMap.wgtodaystring)}";
             localizationobj.clearstring = "${StringUtil.wrapString(uiLabelMap.wgclearstring)}";
             return localizationobj;
         }
</#macro> 
 
<#macro renderEmplWorkOverTime id="jqxgridEmplWorkOvertime" jqxWindowId="jqxWindowWorkOverTime" width="100%" height="97%" showtoolbar="true"
	jqxGridInWindow="true" updaterow="true" jqxNotifyId="jqxNotifyEmplWorkOvertime" updateFuntion="updateApprovalWorkOvertime" 
	autoheight="fasle" isShowTitleProperty="false" titleProperty="" sendRequestAppr="true" >
 	
	<script type="text/javascript">
	<@getLocalization/>
	var statusWorkOvertimeArr = new Array();
	<#if statusListWorkOvertime?exists>
		var row = {};
		<#list statusListWorkOvertime as status>
			row = {};
			row["statusId"]= "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusWorkOvertimeArr[${status_index}] = row;
		</#list> 
	</#if>
	
	var columnTr = [{text: '${uiLabelMap.CommonDate}', datafield: 'dateRegistration', 'width': 100, 'cellsalign': 'left', 'editable': false, columntype: 'template', cellsformat: 'dd/MM/yyyy', filterable : false},
	                {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', 'width': 100, 'cellsalign': 'left', 'editable': false, 'pinned': false},
	                {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 130, 'cellsalign': 'left', 'editable': false, 'pinned': false},
	                {text: '${uiLabelMap.HREmplFromPositionType}', datafield: 'workOvertimeRegisId', hidden: true},                
					{text: '${uiLabelMap.overTimeFromDate}', datafield: 'overTimeFromDate', width: '100px',cellsformat: 'HH:mm:ss', editable: false, filterType : 'range'},
					{text: '${uiLabelMap.overTimeThruDate}', datafield: 'overTimeThruDate', width: '100px', cellsformat: 'HH:mm:ss', editable: false, filterType : 'range'},
					{text: '${uiLabelMap.ActualStartOverTime}', datafield: 'actualStartTime', width: '100px', cellsformat: 'HH:mm:ss', columntype: 'datetimeinput',filterable : false,
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDateTimeInput({width: '100%', height: 28, formatString: 'HH:mm:ss', showCalendarButton: false});
					        editor.val(cellvalue);
					    },
					    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                        // return the old value, if the new value is empty.
	                        if (newvalue == "" || !newvalue) return oldvalue;
	                    }
					},
					{text: '${uiLabelMap.ActualEndOverTime}', datafield: 'actualEndTime', width: '110px', cellsformat: 'HH:mm:ss', columntype: 'datetimeinput',filterable : false,
						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					        editor.jqxDateTimeInput({width: '100%', height: 28, formatString: 'HH:mm:ss', showCalendarButton: false});
					        editor.val(cellvalue);
					    },
					    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                        // return the old value, if the new value is empty.
	                        if (newvalue == "") return oldvalue;
	                    }
					},
					{text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusWorkOvertimeArr.length; i++){
								if(statusWorkOvertimeArr[i].statusId == value){
									return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusWorkOvertimeArr[i].description + '</div>';		
								}
							}
						},
						<#if updaterow == "true">
							createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
								 var source =
					            {
					                localdata: statusWorkOvertimeArr,
					                datatype: "array"
					            };
								var dataAdapter = new $.jqx.dataAdapter(source);
								editor.jqxDropDownList({source: dataAdapter, autoDropDownHeight: true, displayMember: 'description', valueMember : 'statusId'});							 
						    },
						    cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
		                        // return the old value, if the new value is empty.
		                        if (newvalue == "") return oldvalue;
		                    }
					    </#if>
					}
					];
		var dtFieldTr = [{name: 'partyId', type: 'string'},
		                {name: "partyName", "type": "string"},
		                {name: 'workOvertimeRegisId', type: 'string'},
		                {name: 'dateRegistration', type: 'date'},
				      	{name: 'overTimeFromDate', type: 'date'},
				      	{name: 'overTimeThruDate', type: 'date'},
				      	{name: 'actualStartTime', type: 'date'},
				      	{name: 'actualEndTime', type: 'date'},
				      	{name: 'statusId', type: 'string'}];
		var sourceWorkoverTime = {
			dataType: "json",
			type: 'POST',
	        dataFields: dtFieldTr,
	        data: {},
//	        url: 'getWorkOvertimeInPeriod',
	        url : 'getWorkOvertimeInPeriod',
			root: 'listReturn',        
	        id: 'workOvertimeRegisId',
	        pagenum: 0,
	        pagesize: 15,
	        altrows: true,
	        rowsheight: 25,
	        editmode: 'selectedcell',
	        selectionmode: 'multiplerows',
	        <#if updaterow == "true">
		        updaterow: function(rowid, rowdata, commit){
					var dataEdit = {};
					dataEdit["workOvertimeRegisId"]= rowdata.workOvertimeRegisId;
					dataEdit["dateRegistration"] = rowdata.dateRegistration.getTime();
					if(rowdata.actualStartTime){
						dataEdit["actualStartTime"] = rowdata.actualStartTime.getTime();
					}
					if(rowdata.actualEndTime){
						dataEdit["actualEndTime"] = rowdata.actualEndTime.getTime();
					}
					dataEdit["statusId"] = rowdata.statusId;
					$('#${jqxNotifyId}').jqxNotification('closeLast');
					$.ajax({
						url: 'updateEmplWorkovertime',
						data: dataEdit,
						type: 'POST',
						success: function(data){
							var rowIndex = $("#${id}").jqxGrid('getrowboundindexbyid', rowid);
							$("#${id}").jqxGrid('selectrow', rowIndex);
							if(data._ERROR_MESSAGE_){
								commit(false);
								$('#${jqxNotifyId}').jqxNotification({ template: 'error'});
		                    	$("#${jqxNotifyId}").text(data._ERROR_MESSAGE_);
		                    	$("#${jqxNotifyId}").jqxNotification("open");
							}else{
								commit(true);
								$('#${jqxNotifyId}').jqxNotification({ template: 'info'});
								//$('#container').empty();
		                    	$("#${jqxNotifyId}").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		                    	$("#${jqxNotifyId}").jqxNotification("open");
							}						
						},
						error: function(){
							commit(false);
						}
					});
				}
	        </#if>
		};
		var dataAdapter = new $.jqx.dataAdapter(sourceWorkoverTime,{
			loadComplete: function (data) { 
				//$("#treeGridEmplWorkOvertime").jqxGrid('expandallgroups');
			}
		});
		$(document).ready(function () {
			$("#${id}").jqxGrid({
		         width: "${width}",
		         <#if height != "">
		         	height: '${height}',
		         </#if>
		         source: dataAdapter,
		         localization: getLocalization(),
		         pageSizeOptions: ['10', '20', '30', '50', '100'],
		         pagerMode: 'advanced',
		         columnsResize: true,
		         pageable: true,
		         editmode: 'dblclick',
		         selectionmode: 'multiplerows',
//		         filterable : true,
//		         showfilterrow : true,
		         <#if autoheight == "true">
		         	autoheight: true,
		         </#if>
		         <#if updaterow == "true">
		         	editable: true,
		         <#else>
		         	editable: false,
		         </#if>
		         <#if showtoolbar == "true">
			         showtoolbar: true,
			         rendertoolbar: function (toolbar) {
			        	 if($("#toolbarcontainer${id}").length < 1){
			        		 <#if isShowTitleProperty == "true" && titleProperty !="">
			        		 	var container = $("<div id='toolbarcontainer${id}' class='widget-header'><h4>${StringUtil.wrapString(uiLabelMap[titleProperty])}</h4></div>");
			        		 <#else>
			        		 	var container = $("<div id='toolbarcontainer${id}' style='text-align: left' class='widget-header'><h4>" + "</h4></div>");
			        		 </#if>	
				        	 toolbar.append(container);
				        	 
				        	 <#if sendRequestAppr == "true">
				        	 	container.append('<button id="sendRequestAppr${id}" style="margin-left:10px;"><i class="fa-paper-plane"></i>${uiLabelMap.SendRequestApprWorkOvertime}</button>');
					        	 var obj = $("#sendRequestAppr${id}");
					        	 obj.jqxButton();
					        	 obj.click(function(){
					        		
					        		/* var rowsSelected = $("#${id}").jqxGrid('getselectedrowindexes');
					        		var dataSubmit = new Array();
					        		for(var i = 0; i < rowsSelected.length; i++){
					        			var rowData = $('#${id}').jqxGrid('getrowdata', rowSelected[i]);
					        			dataSubmit.push({"workOvertimeRegisId": rowData.workOvertimeRegisId});
					        		} */
					        		var source = $("#${id}").jqxGrid('source');
					        		var totalRecord = source.totalrecords;
					        		if(totalRecord <= 0){
					        			bootbox.dialog("${uiLabelMap.NoEmployeeWorkingOvertimeToApproval}!", 
					        			[
					        			{
					        				 "label" : "${uiLabelMap.CommonSubmit}",
					        				 "class" : "btn-primary btn-small icon-ok",
					        				    
				        				}
					        			]);
					        			return;
					        		}
					        		$(this).attr("disabled", "disabled");
					        		var fromDate = source._source.data.fromDate;
					        		var thruDate = source._source.data.thruDate;				        		
					        		$('#${id}').jqxGrid('showloadelement');
					        		$('#${id}').jqxGrid({ disabled: true});
					        		$.ajax({
					        			url: "requestUpdateWorkOvertimeStt",
					        			type: "POST",
					        			data: {fromDate: fromDate, thruDate: thruDate},
					        			success: function(data){
					        				if(data._EVENT_MESSAGE_){
					        					$('#${jqxNotifyId}').jqxNotification({ template: 'info'});
						                    	$("#${jqxNotifyId}").text(data._EVENT_MESSAGE_);
						                    	$("#${jqxNotifyId}").jqxNotification("open");
					        				}else{
					        					$('#${jqxNotifyId}').jqxNotification({ template: 'error'});
						                    	$("#${jqxNotifyId}").text(data._ERROR_MESSAGE_);
						                    	$("#${jqxNotifyId}").jqxNotification("open");
					        				}
					        			}, 
					        			complete: function(){
					        				$('#${id}').jqxGrid('hideloadelement');
							        		$('#${id}').jqxGrid({ disabled: false});
							        		obj.removeAttr("disabled");
					        			}
					        		});
					        	 });
				        	 </#if>
			        	 }
			         },
		         </#if>
		         groupable: true,
	             groups: ['dateRegistration'],
	             groupsexpandedbydefault: true,
		         columns: columnTr,
	             theme: 'olbius',
	             
		    });
	
			$("#${id}").on('bindingcomplete', function (event) {
			});
			
			<#if jqxGridInWindow == "true">
				$("#${jqxWindowId}").jqxWindow({
					width: '1024px', minWidth: '1024px', height: 576,  maxHeight: 576, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
					
					initContent: function(){
						$("#${jqxNotifyId}").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyEmplWorkOvertimeContainer"});
					}
			    });
			</#if>
			
		});		
		function ${updateFuntion}(fromDate, thruDate){
			var source = $("#${id}").jqxGrid('source');
			source._source.data = {fromDate: fromDate.getTime(), thruDate: thruDate.getTime()};
			$("#${id}").jqxGrid('source', source);
			$("#${id}").jqxGrid('addgroup', 'dateRegistration');
			<#if jqxGridInWindow == "true">
				$("#${jqxWindowId}").jqxWindow("open");
			</#if>
		}
	</script>
		
	<#if jqxGridInWindow == "true">
		<div id="${jqxWindowId}" style="display: none;">
			<div>${uiLabelMap.TimeRegisters}</div>
			<div class="row-fluid">
			<div id="jqxNotifyEmplWorkOvertimeContainer">
				<div id="${jqxNotifyId}"></div>
			</div>
	</#if>
			<div id="${id}"></div>
	<#if jqxGridInWindow == "true">
			</div>
		</div>
	</#if>
 </#macro>
 
 <#macro renderEmplWorkingLate id="jqxGridEmplWorkingLate" width="100%" height="" jqxWindowId="jqxWindowWorkingLate" jqxGridInWindow="true" updaterow="true" 
 	jqxNotifyId="jqxNotifyEmplWorkingLate" renderFuntionJs="overallEmplWorkingLate" autoheight="fasle">
 	<#if jqxGridInWindow == "true">
	 	<div id="${jqxWindowId}" style="display: none;">
			<div>${uiLabelMap.HREmplWorkingLateList}</div>
			<div class="row-fluid">
				<div id="jqxNotifyEmplWorkingLateContainer">
					<div id="${jqxNotifyId}"></div>
				</div>
	</#if>		
			<div id="${id}"></div>
	<#if jqxGridInWindow == "true">		
			</div>
		</div>
	</#if>	
	<script type="text/javascript">
	var statusWorkingLateArr = new Array();
	<#if statusListWorkOvertime?exists>
	var row = {};
		<#list statusListWorkingLate as status>
			row = {};
			row["statusId"]= "${status.statusId}";
			row["description"] = "${StringUtil.wrapString(status.description)}";
			statusWorkingLateArr[${status_index}] = row;
		</#list>
	</#if>
	var dtFieldWL = [{name: 'partyId', type: 'string'},
	                 {name: 'partyName', type: 'string'},
	                 {name: 'emplWorkingLateId', type: 'string'},
	                 {name: 'dateWorkingLate', type: 'date'},
	                 {name: 'arrivalTime', type: 'date'},
	                 {name: 'delayTime', type: 'number'},
	                 {name: 'reason', type: 'string'},
	                 {name: 'statusId', type:'string'}];
	                 
	var columnlistWL = [{text: '${uiLabelMap.CommonDate}', datafield: 'dateWorkingLate', width: '12%',cellsformat: 'dd/MM/yyyy', editable: false, filtertype:'range', columntype: 'datetimeinput'},
	                    {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId', width: '12%',editable: false, hidden: false},
	                    {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: '12%',editable: false, hidden: false},
	                    {text: '${uiLabelMap.ArrivalTime}', datafield: 'arrivalTime', width: '12%', editable: false, cellsformat: 'HH:mm:ss', columntype: 'datetimeinput'},
	                    {text: '${uiLabelMap.HRDelayTime}', datafield: 'delayTime', editable: true, width: '13%', cellsalign: 'right', columntype: 'numberinput',
	                    	validation: function (cell, value) {
	                            if (value < 0) {
	                                return { result: false, message: "${uiLabelMap.ValueNotLessThanZero}" };
	                            }
	                            return true;
	                        },
	                    	createeditor: function (row, cellvalue, editor) {
	                            editor.jqxNumberInput({ decimalDigits: 0, digits: 3 });
	                        }	
	                    },
	                    {text: '${uiLabelMap.HRCommonReason}', datafield: 'reason', editable: true,
	                    	cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
	                            // return the old value, if the new value is empty.
	                            if (newvalue == "") return oldvalue;
	                        }	
	                    },
	                    {text: '${uiLabelMap.CommonStatus}', datafield: 'statusId', editabel: true, width: '15%', columntype: 'dropdownlist',
	                    	cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusWorkingLateArr.length; i++){
									if(statusWorkingLateArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusWorkingLateArr[i].description + '</div>';		
									}
								}
							},
							<#if updaterow == "true">
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									 var source =
						            {
						                localdata: statusWorkingLateArr,
						                datatype: "array"
						            };
									var dataAdapter = new $.jqx.dataAdapter(source);
									editor.jqxDropDownList({source: dataAdapter, autoDropDownHeight: true, displayMember: 'description', valueMember : 'statusId'});							 
							    },
		                    	cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
		                            // return the old value, if the new value is empty.
		                            if (newvalue == "") return oldvalue;
		                        }
						    </#if>
	                    },
	                    {text: '', datafield: 'emplWorkingLateId', hidden: true}];
		var emplWorkLateSource = {
			data:{},
			type: 'POST',
			root: 'listReturn',
			url: 'getEmplTimesheetWorkingLate',
	        datafields: dtFieldWL,
	        datatype: "json",
	        pagenum: 0,
	        pagesize: 20,
	        
	        beforeprocessing: function (data) {
	        	emplWorkLateSource.totalrecords = data.TotalRows;
	        },
	        id: 'emplWorkingLateId',
	        <#if updaterow == "true">
	        updaterow: function(rowid, rowdata, commit){
	        	var dataEdit = {};
				dataEdit["emplWorkingLateId"]= rowdata.emplWorkingLateId;
				if(rowdata.dateWorkingLate){
					dataEdit["dateWorkingLate"] = rowdata.dateWorkingLate.getTime();
				}
				dataEdit["delayTime"] = rowdata.delayTime;
				dataEdit["reason"] = rowdata.reason;
				dataEdit["statusId"] = rowdata.statusId;
				$.ajax({
					url: 'updateEmplWorkingLate',
					data: dataEdit,
					type: 'POST',
					success: function(data){
						if(data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_){
							commit(false);
							$('#${jqxNotifyId}').jqxNotification({ template: 'error'});
	                    	$("#${jqxNotifyId}").text(data._ERROR_MESSAGE_);
	                    	$("#${jqxNotifyId}").jqxNotification("open");
						}else{
							commit(true);
							$('#${jqxNotifyId}').jqxNotification({ template: 'info'});
	                    	$("#${jqxNotifyId}").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	                    	$("#${jqxNotifyId}").jqxNotification("open");
						}						
					},
					error: function(){
						commit(false);
					}
				});
	        }
	        </#if>
		};
		var emplWorkLateAdapter = new $.jqx.dataAdapter(emplWorkLateSource);
		$(document).ready(function () {
			<#if jqxGridInWindow=="true">
				$("#${jqxWindowId}").jqxWindow({
					width: '950px', minWidth: '950px', height: 600,  maxHeight: 600, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
					initContent: function(){
						$("#jqxNotifyEmplWorkingLate").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyEmplWorkingLateContainer"});
					}
			    });
			</#if>
			$("#${id}").jqxGrid({
				width:'${width}',
				<#if height != "">
		         	height: '${height}',
		         </#if>
				filterable: false,
				sortable:true,
				source: emplWorkLateAdapter,
				localization: getLocalization(),
				<#if updaterow == "true">
					editable: true,
				<#else>
					editable: false,
				</#if>
	         	<#if autoheight == "true">
	         		autoheight: true,
		         </#if>
				selectionmode: 'singlecell',
				editmode: 'click',
				pageable: true,
				columns: columnlistWL,
				groupable: true,
	            groups: ['dateWorkingLate'],
	            groupsexpandedbydefault: true,
	            theme: 'olbius'
			});
			
		});
		
	
		function ${renderFuntionJs}(emplTimesheetId){
			var source = $("#${id}").jqxGrid('source');
			source._source.data = {emplTimesheetId: emplTimesheetId};
			$("#${id}").jqxGrid('source', source);
			$("#${id}").jqxGrid({'groups': ['dateWorkingLate']});
			<#if jqxGridInWindow == "true">
				$("#${jqxWindowId}").jqxWindow("open");
			</#if>	
		}
	</script>
 </#macro>
 
 <#macro renderEmplTimesheetOverview width="1005px" height="476px" id="jqxEmplTimesheetGeneral" jqxWindowId="jqxWindowEmplTimesheetGeneral" 
 	jqxGridInWindow="true" updaterow="true" renderFuntionJs="overallEmplTimesheets" autoheight="false">
 	<#if jqxGridInWindow == "true">
 	<div id="${jqxWindowId}" style="display: none;">
		<div>${uiLabelMap.EmplTimekeepingReportTilte}</div>
		<div>
	</#if>
		<div class="row-fluid">
			<div class="span12">
				<div class="span7" style="text-align: right; margin-top: 4px">
					<b>${uiLabelMap.CommonDepartment}</b>
				</div>
				<div class="span5">
					<div id="jqxDropDownButtonGeneral">
						<div style="border: none;" id="jqxTreeGeneral">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">	
			<div id="${id}"></div>
		</div>	
	<#if jqxGridInWindow == "true">
		</div>		
	</div>
	</#if>
	<script type="text/javascript">
	var dataFieldTimesheetOverview = [{name: 'partyId', type:'string'},
	                                  {name: 'partyName', type:'string'},
	                                  {name: 'emplPositionTypeId', type:'string'},
	                                  {name: 'overtimeRegister', type: 'number'},
		      						  {name: 'overtimeActual', type: 'number'},
		      						  {name: 'totalWorkingLateHour', type:'number'},
		      						  {name: 'totalDayLeave', type: 'number'},
		      						  {name: 'totalDayLeavePaidApproved', type: 'number'},
		      						  {name: 'totalDayWork', type: 'number'}];
	var emplPosTypeTimeSheetOverview = new Array();
	<#list emplPosType as posType>
		var row = {};
		row["emplPositionTypeId"] = "${posType.emplPositionTypeId}";
		row["description"] = "${posType.description?if_exists}";
		emplPosTypeTimeSheetOverview[${posType_index}] = row;
	</#list>
		      						  
	var columnEmplTimesheetGeneral = [{text: '${uiLabelMap.EmployeeId}', datafield: 'partyId',  editable: false,  cellsalign: 'left', width: 110, pinned: true},
					{text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 120, pinned: true},
					{text: '${uiLabelMap.HREmplFromPositionType}', datafield: 'emplPositionTypeId',  editable: false, cellsalign: 'right', width: 150,
						cellsrenderer: function (row, column, value){
							for(var i = 0; i < emplPosTypeTimeSheetOverview.length; i++){
								if(emplPosTypeTimeSheetOverview[i].emplPositionTypeId == value){
									return '<div style=\"\">' + emplPosTypeTimeSheetOverview[i].description + '</div>';		
								}
							}
						}	
					},
					{text: '${uiLabelMap.OvertimeRegister}', datafield: 'overtimeRegister', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
					{text: '${uiLabelMap.OvertimeActual}', datafield: 'overtimeActual', editable: false, cellsalign: 'right', width: 130, filtertype: 'number'},
					{text: '${uiLabelMap.TotalWorkingLateHour}', datafield: 'totalWorkingLateHour' , editable: false, cellsalign: 'right', width: 130, filtertype: 'number'}, 
					{text: '${uiLabelMap.TotalDayLeave}', datafield: 'totalDayLeave', editable: false, cellsalign: 'right', width: 150, filtertype: 'number'},
					{text: '${uiLabelMap.TotalDayPaidLeave}', datafield: 'totalDayLeavePaidApproved', editable: false, cellsalign: 'right', width: 160, filtertype: 'number'},					
					{text: '${uiLabelMap.TotalTimeKeeping}', datafield: 'totalDayWork', editable: false, cellsalign: 'right', width: 170, filtertype: 'number'}];	      						  
	var sourceEmplTimesheetGeneral = {
			dataType: "json",
			type: 'POST',
	        dataFields: dataFieldTimesheetOverview,
	        data: {},
	        beforeprocessing: function (data) {
	        	sourceEmplTimesheetGeneral.totalrecords = data.TotalRows;
	        },
	        url: 'getEmplTimesheetGeneral',
			root: 'listReturn',        
	        id: 'partyId',
	        pagenum: 0,
	        pagesize: 15,
		};
	var dataEmplTimesheetGeneralAdapter = new $.jqx.dataAdapter(sourceEmplTimesheetGeneral,{
		loadComplete: function (data) { 
			//$("#treeGridEmplWorkOvertime").jqxGrid('expandallgroups');
		}
	});
	$(document).ready(function () {
		<#if jqxGridInWindow == "true">
			$("#${jqxWindowId}").jqxWindow({
				minWidth: '1024px', width: '1024px', height: 576,  maxHeight: 576, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
				initContent: function(){
				}
		    });
		</#if>
		$("#${id}").jqxGrid({
	        width: "${width}",
	        <#if height != "">
	        	height: '${height}',
	        </#if>
	        autoheight: ${autoheight},	
	        virtualmode: true,
	        rendergridrows: function () {
	            return dataEmplTimesheetGeneralAdapter.records;
	        },
	        source: dataEmplTimesheetGeneralAdapter,
	        pageSizeOptions: ['15', '30', '50', '100'],
	        pagerMode: 'advanced',
	        columnsResize: true,
	        pageable: true,
	        editable: false,
	        columns: columnEmplTimesheetGeneral,
	        selectionmode: 'singlerow',
	        theme: 'olbius',
	   });
		/*================jqxTree defind =================*/
		var dropdownButtonGeneral = $("#jqxDropDownButtonGeneral");
		var jqxTreeDivGeneral = $("#jqxTreeGeneral");
		var idSuffixGeneral = "timesheetsGeneral";
		dropdownButtonGeneral.jqxDropDownButton({ width: '330px', height: 25, theme: 'olbius'});
		var dataTree = new Array();
		<#list treePartyGroup as tree>
			var row = {};
			row["id"] = "${tree.id}_" + idSuffixGeneral;
			row["text"] = "${tree.text}";
			row["parentId"] = "${tree.parentId}_" + idSuffixGeneral;
			row["value"] = "${tree.idValueEntity}";
			dataTree[${tree_index}] = row;
		</#list>
		var sourceTree =
		{
		    datatype: "json",
		    datafields: [
		    	{ name: 'id'},
		        { name: 'parentId'},
		        { name: 'text'} ,
		        { name: 'value'}
		    ],
		    id: 'id',
		    localdata: dataTree
		};
		var dataAdapterTreeGeneral = new $.jqx.dataAdapter(sourceTree);
		// perform Data Binding.
		dataAdapterTreeGeneral.dataBind();
		// get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents 
		// the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter 
		// specifies the mapping between the 'text' and 'label' fields.  
		var recordsTreeGeneral = dataAdapterTreeGeneral.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
		jqxTreeDivGeneral.jqxTree({source: recordsTreeGeneral,width: "330px", height: "240px", theme: 'olbius'});
		<#if expandedList?has_content>
		 	<#list expandedList as expandId>
		 		jqxTreeDivGeneral.jqxTree('expandItem', $("#${expandId}_" + idSuffixGeneral)[0]);
		 	</#list>
		 </#if>    
		 <#if expandedList?has_content>
		 	<#assign defaultPartyId = expandedList.get(expandedList?size - 1)>
		 	var initElement = $("#${expandedList.get(0)}_" + idSuffixGeneral)[0];
		 	setDropdownContent(initElement, jqxTreeDivGeneral, dropdownButtonGeneral);
		 </#if>
		 jqxTreeDivGeneral.on('select', function(event){
	    	var id = event.args.element.id;
	    	var item = jqxTreeDivGeneral.jqxTree('getItem', args.element);
	    	setDropdownContent(item, jqxTreeDivGeneral, dropdownButtonGeneral);
	        
			var tmpS = $("#${id}").jqxGrid('source');
			var emplTimesheetId = tmpS._source.data.emplTimesheetId;
			var value = jqxTreeDivGeneral.jqxTree('getItem', $("#"+id)[0]).value;
			tmpS._source.data = {emplTimesheetId: emplTimesheetId, partyGroupId: value};			 
			$("#${id}").jqxGrid('source', tmpS);
	     });
		/*==========./end jqxTree defind =================*/
		
	});
	function ${renderFuntionJs}(emplTimesheetId){
		var source = $("#${id}").jqxGrid('source');
		source._source.data = {emplTimesheetId: emplTimesheetId};
		$("#${id}").jqxGrid('source', source);
		<#if jqxGridInWindow == "true">
			$("#${jqxWindowId}").jqxWindow("open");
		</#if>	
	}
	</script>
 </#macro>
 
<#macro renderJqxTreeDropDownBtn id treeDropDownSource dropdownBtnId="" isDropDown="true" jqxTreeSelectFunc="jqxTreeSelectFunc" 
	setDropdownContentJsFunc="setDropdownContent" expandTreeId="" width="300px" height="200px" expandAll="false">
	<script type="text/javascript">
		$(document).ready(function () {
			<#if isDropDown == "true">
				var dropdownBtn = $("#${dropdownBtnId}");
				dropdownBtn.jqxDropDownButton({width: 300, height: 25,  autoOpen: true, theme: 'olbius'});
			</#if>
			var treeGridEle = $("#${id}");
			
			var treeDropDownArr = new Array();
			<#list treeDropDownSource as tree>
		 		var row = {};
		 		row["id"] = "${tree.id}_${id}";
		 		row["text"] = "${tree.text}";
		 		row["parentId"] = "${tree.parentId}_${id}";
		 		row["value"] = "${tree.idValueEntity}"
		 		treeDropDownArr[${tree_index}] = row;
			</#list>
			var sourceTreeDropDown =
			{
			     datatype: "json",
			     datafields: [
			         { name: 'id'},
			         { name: 'parentId'},
			         { name: 'text'} ,
			         { name: 'value'}
			     ],
			     id: 'id',
			     localdata: treeDropDownArr
			};
			var dataAdapterTree = new $.jqx.dataAdapter(sourceTreeDropDown);
			dataAdapterTree.dataBind();
			var recordTree = dataAdapterTree.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
			treeGridEle.jqxTree({source: recordTree, width: "${width}", height: "${height}", theme: 'olbius'});
			<#if jqxTreeSelectFunc != "">
				treeGridEle.on("select", function(event){
					${jqxTreeSelectFunc}(event);
				});
			</#if>
			<#if expandTreeId?has_content>
				treeGridEle.jqxTree('expandItem', $("#${expandTreeId}_${id}")[0]);
				$('#${id}').jqxTree('selectItem', $("#${expandTreeId}_${id}")[0]);
			</#if>
			<#if expandAll == "true">
				treeGridEle.jqxTree('expandAll');
			</#if>
		});
	</script> 
</#macro>


<#macro renderSearchPartyInOrg inputId searchBtnId="searchEmpl" searchUrl="searchPartyId" disabled="false"
	windowSearchId="popupWindowEmplList" width="86%" placeHolder=StringUtil.wrapString(uiLabelMap.EnterEmployeeId)>
	<input type="text" id="${inputId}">
	<img alt="search" id="${searchBtnId}" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
						style="
						   border: #d5d5d5 1px solid;
						   padding: 4px;
						   border-bottom-right-radius: 3px;
						   border-top-right-radius: 3px;
						   margin-left: -3px;
						   background-color: #f0f0f0;
						   border-left: 0px;
						   cursor: pointer;
						"/>
						
	<script type="text/javascript">
	$(document).ready(function () {
		$("#${searchBtnId}").click(function(event){
			openJqxWindow(jQuery("#${windowSearchId}"));
		});
		var source = function(query, response){
			var dataApdapter = new $.jqx.dataAdapter(
				{
					datatype: "json",
					datafields:
	                [
	                    { name: 'partyId' },
	                    { name: 'partyName'},
	                ],
	                url: "${searchUrl}",
	                data:{
	                	maxRows: 12,
	                },
				},
				{
					autoBind: true,
					formatData: function (data) {
	                    data.partyId_startsWith = query;
	                    return data;
	                },
	                loadComplete: function (data) {
	                    if (data.listParty.length > 0) {
	                        response($.map(data.listParty, function (item) {
	                            return {
	                                label: item.partyName + ' [' + item.partyId + ']',
	                                value: item.partyId
	                            }
	                        }));
	                    }
	                }
				}
			);
		};
		$("#${inputId}").jqxInput({ placeHolder: "${placeHolder}", source: source,
			height: 20, width: '${width}', minLength: 1, theme: 'olbius', valueMember: 'partyId', 
			displayMember:'partyName', items: 12, disabled: ${disabled}});
	});
	</script>						
</#macro>



