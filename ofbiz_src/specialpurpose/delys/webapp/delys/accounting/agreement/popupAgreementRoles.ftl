<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	var action = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		var roleType;
		var initElement = function(){
			roleType.jqxDropDownList({theme: theme, source: rtData, displayMember: "description",autoDropDownHeight : true, valueMember: "roleTypeId", width: 215, height: 25,placeHolder : '${StringUtil.wrapString(uiLabelMap.PleaseChooseAcc?default(''))}'});
			initPartyDropDown();
		};
		var initSource = function(){
			var sourcePG = { datafields: [
						      { name: 'partyId', type: 'string' },
						      { name: 'partyTypeId', type: 'string' },
						      { name: 'lastName', type: 'string' },
						      { name: 'firstName', type: 'string' },
						      { name: 'groupName', type: 'string' }
						    ],
				cache: false,
				root: 'results',
				datatype: "json",
				updaterow: function (rowid, rowdata) {
					// synchronize with the server - send update command   
				},
				beforeprocessing: function (data) {
				    sourcePG.totalrecords = data.TotalRows;
				},
				filter: function () {
				   // update the grid and send a request to the server.
				   $("#jqxPartyGrid").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  // callback called when a page or page size is changed.
				},
				sort: function () {
				  $("#jqxPartyGrid").jqxGrid('updatebounddata');
				},
				sortcolumn: 'partyId',
			   	sortdirection: 'asc',
				type: 'POST',
				data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
					roleTypes : JSON.stringify(roleTypeUnique)
				},
				pagesize:5,
				contentType: 'application/x-www-form-urlencoded',
				url: 'jqxGeneralServicer?sname=JQGetListPartiesRoleTypes',
			};
			return sourcePG;
		};
		
		var initDataAdapter = function(){
			var sourcePG = initSource();
			var dataAdapterPG = new $.jqx.dataAdapter(sourcePG,
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
			                if (!sourcePG.totalRecords) {
			                    sourcePG.totalRecords = parseInt(data['odata.count']);
			                }
			        }
			    });	
			return dataAdapterPG;
		};
		var initPartyDropDown = function(){
			var dataAdapterPG = initDataAdapter();
			$("#partyIdAdd").jqxDropDownButton({ width: 215, height: 25,dropDownHorizontalAlignment : 'right'});
			$("#jqxPartyGrid").jqxGrid({
			width:600,
			source: dataAdapterPG,
			filterable: true,
			virtualmode: true, 
			showfilterrow: true,
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			rendergridrows: function(obj)
			{	
				return obj.data;
			},
			columns: 
			[
				{ text: '${uiLabelMap.accPartyId}', datafield: 'partyId',width : '20%'},
				{ text: '${uiLabelMap.accPartyTypeId}', datafield: 'partyTypeId', width:'20%'},
				{ text: '${uiLabelMap.accFirstName}', datafield: 'lastName', width:'10%'},
				{ text: '${uiLabelMap.accLastName}', datafield: 'firstName', width:'10%'},
				{ text: '${uiLabelMap.accGroupName}', datafield: 'groupName', width:'40%'}
			]
			});
			$("#jqxPartyGrid").on('rowselect', function (event) {
                var args = event.args;
                var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
                var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyId'] + '</div>';
                $("#partyIdAdd").jqxDropDownButton('setContent', dropDownContent);
                $("#partyIdAdd").jqxDropDownButton("close");
                getPartyRole(row['partyId']);
            });
		};
		var initWindow = function(){
			$("#accTransaction").jqxWindow({
		        width: 600, height: 180, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
		    $("#accTransaction").on("close", function(){
		    	clearForm();
		    });
		};
		var initRule = function(popup){
			popup.jqxValidator({
	       	   	rules: [{
	                   input: "#roleTypeIdAdd", 
	                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
	                   action: 'change,close', 
	                   rule: function (input, commit) {	
	                       var val = input.jqxDropDownList('val');
	                       return val && val != null && typeof(val) != 'undefined' ;
	                   }
	               },{
	                   input: "#partyIdAdd", 
	                   message: "${StringUtil.wrapString(uiLabelMap.FieldRequiredAccounting?default(''))}", 
	                   action: 'change,close', 
	                   rule: function (input, commit) {
	                   	var val = input.jqxDropDownButton('val');
                       return val && val != null && typeof(val) != 'undefined' ;
	                   }
	               }]
	       	 });
		};
		var bindingEvent = function(){
			$("#save").click(function () {
		        if(!action.save()){
		        	return;
		        }
		        $("#accTransaction").jqxWindow('close');
		    });
		    $("#saveAndContinue").click(function () {
		        action.save();
		        action.clear();
		    });    
		};
		var getPartyRole = function(partyId){
			$.ajax({
				url : "getPartyRole",
				type : "POST",
				data: {
					partyId : partyId
				},
				success: function(res){
					if(res.listRoleTypes){
						changeDropDownSource(res.listRoleTypes);	
					}
				}
			});
		};
		var changeDropDownSource = function(data){
			var tmp = [];
			for(var y in rtData){
				for(var x in data){
					if(data[x].roleTypeId == rtData[y].roleTypeId){
						tmp.push(rtData[y]);
					}	
				}
			}
			roleType.jqxDropDownList("source", tmp);
		};
		
		var save = function(){
			if(!$("#accTransaction").jqxValidator("validate")){
				return false;
			}
			var row = { 
	    		partyId:$('#partyIdAdd').val(), 
	    		roleTypeId:$('#roleTypeIdAdd').val()
	    	};
		    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        return true;  
		};
		var clearForm = function(){
			roleType.jqxDropDownList("selectedIndex", -1);
			$("#partyIdAdd").jqxDropDownButton("setContent", "");
			$("#jqxPartyGrid").jqxGrid("clearselection");
		};
		return {
			init: function(){
				roleType = $("#roleTypeIdAdd");
				initElement();
				initWindow();
				initRule($("#accTransaction"));
				bindingEvent();
			},
			save: save,
			clear: clearForm
		};
	}());
	$(document).ready(function(){
		action.init();
	});
</script>
<div id="accTransaction" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='form-window-content'>
    		<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.accAgreementPartyId}</label>
				</div>  
				<div class="span7">
					<div id="partyIdAdd">
						<div id="jqxPartyGrid"></div>	 				
	 				</div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.roleTypeId}</label>
				</div>  
				<div class="span7">
					<div id="roleTypeIdAdd"></div>	 				
		   		</div>
		   	</div>
    	</div>
        <div class="form-action">
			<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div>