 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
 <div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
    	<input type="hidden" name="statusIdAdd" id="statusIdAdd" value="AGREEMENT_CREATED"/>
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<form id="formAdd">
	    		<div class='span6'>
	    			<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAProductId}
	    				</div>
	    				<div class='span7' >
							<div id="productIdAdd">
			 					<div id="jqxProductGrid"></div>
			 				</div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAPartyFrom}
	    				</div>
	    				<div class='span7' >
	    					<div id="partyIdFromAdd" data-role="roleTypeIdFromAdd">
			 					<div id="jqxPartyFromGrid"></div>
			 				</div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAPartyTo}
	    				</div>
	    				<div class='span7' >
	    					<div id="partyIdToAdd" data-role="roleTypeIdToAdd">
	 							<div id="jqxPartyToGrid"></div>
	 						</div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DARoleTypeIdFrom}
	    				</div>
	    				<div class='span7'>
	    					<div id="roleTypeIdFromAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DARoleTypeIdTo}
	    				</div>
	    				<div class='span7'>
	    					<div id="roleTypeIdToAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAAgreementTypeId}
	    				</div>
	    				<div class='span7'>
	    					<div id="agreementTypeIdAdd"></div>
	    				</div>
					</div>
				</div>	
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAAgreementDate}
	    				</div>
	    				<div class='span7'>
	    					<div id="agreementDateAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right asterisk'>
	    					${uiLabelMap.DAFromDate}
	    				</div>
	    				<div class='span7'>
	    					<div id="fromDateAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right '>
	    					${uiLabelMap.DAThruDate}
	    				</div>
	    				<div class='span7'>
	    					<div id="thruDateAdd"></div>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right '>
	    					${uiLabelMap.DADescription}
	    				</div>
	    				<div class='span7'>
	    					<input style="position : absolute;" id="descriptionAdd"></input>
	    					<button type="button" class="custom-description" onclick="openTextArea()"><i style="padding : 8px;" class="icon-edit"></i></button>
	    				</div>
					</div>
					<div class='row-fluid margin-bottom10'>
	    				<div class='span5 align-right '>
	    					${uiLabelMap.textValue}
	    				</div>
	    				<div class='span7'>
	    					<input id="textDataAdd"></input>
	    				</div>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>	
    </div>
</div>

<!--window edit text area-->
<div class="hide" id="textWindow">
	<div>${uiLabelMap.EditTextAdd}</div>
	<div class="row-fluid">
		<div class="span12 form-window-content">
			<div id="editText"></div>		
		</div>
		<div class="form-action">
			<button id="cancelText"class='btn btn-danger form-action-button pull-right'><i class='fa-cancel'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="addText" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonAdd}</button>
		</div>
	</div>
</div>

<script type="text/javascript">
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;  
	var form = $('#formAdd');
	var popup = $("#alterpopupWindow");
	var partyFromChange = "";
	var partyToChange = "";
	var cellclass = function (row, columnfield, value) {
 		var now = new Date();
		now.setHours(0,0,0,0);
 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
        if (data.thruDate != undefined && data.thruDate != null && Date.parseExact(data.thruDate,"dd/MM/yyyy HH:mm:ss") <= now) {
            return 'background-red';
        }
    };
    
    //this function open window edit text area
    var openTextArea = function(){
	    $('#textWindow').jqxWindow('open');
    }
    
	/*this function for init all grid use*/
	var popupAction = (function(){
		var initPartySelect = function(dropdown, grid, width){
			var datafields = [{ name: 'partyId', type: 'string' },
	    		{ name: 'partyTypeId', type: 'string' },
	        	{ name: 'firstName', type: 'string' },
	        	{ name: 'lastName', type: 'string' },
	        	{ name: 'groupName', type: 'string' }];
	        var columns = [{ text: '${uiLabelMap.PartyPartyId}', datafield: 'partyId', width: 200, pinned: true},
	       		{ text: '${uiLabelMap.PartyTypeId}', datafield: 'partyTypeId', width: 200, filtertype  :'checkedlist',
					cellsrenderer: function(row, columns, value){
						var group = "${uiLabelMap.PartyGroup}";
						var person = "${uiLabelMap.Person}";
						if(value == "PARTY_GROUP"){
							return "<div class='custom-cell-grid'>"+group+"</div>";
						}else if(value == "PERSON"){
							return "<div class='custom-cell-grid'>"+person+"</div>";
						}
						return value;
					},
					createfilterwidget : function(row,column,widget){
						var records;
						if(listPartyType && listPartyType.length > 0){
							var filter = new $.jqx.dataAdapter(listPartyType,{autoBind : true});
							records = filter.records;
							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
						}else records = [];
						widget.jqxDropDownList({source : records,displayMember : 'description',valueMember : 'partyTypeId'});
					}
				},
				{ text: '${uiLabelMap.PartyGroupName}', datafield: 'groupName', width: 200},
				{ text: '${uiLabelMap.PartyFirstName}', datafield: 'firstName', width: 200, 
					cellsrenderer: function(row, columns, value, defaulthtml, columnproperties, rowdata){
						var first = rowdata.firstName ? rowdata.firstName : "";
						var last = rowdata.lastName ? rowdata.lastName : "";
						return "<div class='custom-cell-grid'>"+ first + " " + last +"</div>";
					}
				}];
			dropdown.on("DOMSubtreeModified", function(){
				var val = $(this).val();
				if(val){
					var role = $(this).data("role");
					getRolesByParty($("#"+role), val, listRoleCondition);
				}
			});
	    	GridUtils.initDropDownButton({url: "getFromParty",source : {cache : false,pagesize : 5}, autorowheight: true, filterable: true, width: width ? width : 600},datafields,columns, null, grid, dropdown, "partyId");
		};
			
		var initProductGrid = function(){
			var datafields = [{name: 'productId', type: 'string' },{name: 'productName', type: 'string' }];
			var columns = [{text: '${uiLabelMap.accProductId}', datafield: 'productId'},{text: '${uiLabelMap.accProductName}', datafield: 'productName'}];
			var source = {cache: false,sortcolumn: 'productId',sortdirection: 'asc'};
			GridUtils.initDropDownButton({source : source,url : 'JQGetListProducts',filterable : true},datafields,columns,null,$("#jqxProductGrid"),$('#productIdAdd'),'productId');
		};
		
		var initDate = function(){
			return {
				min : $("#fromDateAdd").jqxDateTimeInput('getMinDate'),
				max : $("#fromDateAdd").jqxDateTimeInput('getMaxDate')
			};
		};
		var initElementGrid = function(){
		    $("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
		    $("#roleTypeIdFromAdd").jqxDropDownList({placeHolder: "", source: [], width: 200, displayMember:"description", valueMember: "roleTypeId", autoDropDownHeight: true});
        	$("#roleTypeIdToAdd").jqxDropDownList({placeHolder: "", source: [], width: 200, displayMember:"description", valueMember: "roleTypeId", autoDropDownHeight: true});
			$("#agreementTypeIdAdd").jqxDropDownList({source: agreementTypeData, width: 200, displayMember:"description", valueMember: "agreementTypeId",placeHolder : '${StringUtil.wrapString(uiLabelMap.ChooseAgreemmentType?default(''))}'});	
			$("#agreementDateAdd").jqxDateTimeInput({height: '25px', width: 200,  formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
			$("#fromDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss',allowNullDate: true, value: null});
			$("#thruDateAdd").jqxDateTimeInput({height: '25px',width: 200, formatString: 'dd-MM-yyyy : HH:mm:ss', allowNullDate: true, value: null});
			$("#descriptionAdd").jqxInput({height: 20, width: 195});
			$("#textDataAdd").jqxInput({height: 20, width: 195});
			$('#descriptionAdd').jqxTooltip({theme : theme,width : 350,height : 50,position : 'right',opacity : 1});
			initPartySelect($('#partyIdToAdd'),$('#jqxPartyToGrid'), 600);
			initPartySelect($('#partyIdFromAdd'),$('#jqxPartyFromGrid'), 600);
			var objDate = initDate();
			localStorage.setItem('objDate',JSON.stringify(objDate));
		};	
		var initWindow = function(){
			popup.jqxWindow({
		        width: 820,height  : 350,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#cancel"), modalOpacity: 0.7          
		    });
		    popup.on("close", function(){
		    	popupAction.clear();
		    });
		    $('#textWindow').jqxWindow({
		    	width : 600,height : 300,resizable : true,isModal : true,autoOpen : false,cancelButton : $('#cancelText'),modalOpacity : 0.8,initContent : function(){
		    		$('#editText').jqxEditor({width  : '100%',height : '90%',theme : 'olbiuseditor'});
		    	}
		    });
		};
		var initRules = function(){
			form.jqxValidator({
				rules : [
					{input : '#productIdAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
						rule : function(input,commit){
							var value = $('#productIdAdd').jqxDropDownButton('val');
							if(!value) return false;
							return true;
						}
					},
					{input : '#partyIdFromAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
						rule : function(input,commit){
							var value = $('#partyIdFromAdd').val();
							if(!value) return false;
							return true;
						}
					},
					{input : '#partyIdToAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
						rule : function(input,commit){
							var value = $('#partyIdToAdd').val();
							if(!value) return false;
							return true;
						}
					},
					{input : '#agreementDateAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
						rule : function(input,commit){
							var value = $('#agreementDateAdd').jqxDateTimeInput('val');
							if(!value) return false;
							return true;
						}
					},
					{input : '#fromDateAdd',message : '${StringUtil.wrapString(uiLabelMap.CommonRequired?default(''))}',action : 'change,close',
						rule : function(input,commit){
							var value = $('#fromDateAdd').jqxDateTimeInput('val');
							if(!value) return false;
							return true;
						}
					}
				]
			});
		};
		var bindEvent = function(){
		    $("#save").click(function () {
		    	if(!popupAction.save()){return;}
		        popup.jqxWindow('close');
		    });
		    $("#saveAndContinue").click(function () {
		    	popupAction.save();
		    	popupAction.clear();
		    });	
		    
		     $('#textWindow').on('close',function(){
		     	$('#editText').jqxEditor('val','');	
		     });
		     
		     $('#addText').click(function(){
		     	var text =  $('#editText').jqxEditor('val');
		     	$('#descriptionAdd').jqxInput('val',text);
		     	$('#textWindow').jqxWindow('close');
		     	 $('#descriptionAdd').jqxTooltip({content : $('#descriptionAdd').jqxInput('val'),disabled : false});
		     });
		     $('#descriptionAdd').on('change',function(){
	     		 $('#descriptionAdd').jqxTooltip({content : $('#descriptionAdd').jqxInput('val')});
		     })
		      $('#fromDateAdd').on('change',function(){
		      var dateTmp = $('#fromDateAdd').jqxDateTimeInput('getDate');
			      if(dateTmp && dateTmp != null && typeof(dateTmp) != 'undefined'){
			      		$('#thruDateAdd').jqxDateTimeInput('setMinDate',new Date(dateTmp.getYear() + 1900,dateTmp.getMonth(),dateTmp.getDate() + 1));
			      		$('#agreementDateAdd').jqxDateTimeInput('setMaxDate',new Date(dateTmp.getYear() + 1900,dateTmp.getMonth(),dateTmp.getDate()));
			      }
		      });
		      $('#thruDateAdd').on('change',function(){
		      	var dateTmp  = $('#thruDateAdd').jqxDateTimeInput('getDate');
		      	if(dateTmp && dateTmp != null && typeof(dateTmp) != 'undefined'){
    		  		$('#fromDateAdd').jqxDateTimeInput('setMaxDate',new Date(dateTmp.getYear() + 1900,dateTmp.getMonth(),dateTmp.getDate() - 1));
		      	}
		      });
		       $('#agreementDateAdd').on('change',function(){
		       var dateTmp = $('#agreementDateAdd').jqxDateTimeInput('getDate');
		       if(dateTmp && dateTmp != null && typeof(dateTmp) != 'undefined'){
		       		$('#fromDateAdd').jqxDateTimeInput('setMinDate',new Date(dateTmp.getYear() + 1900,dateTmp.getMonth(),dateTmp.getDate()));
		       }
		      });
		      popup.on('close',function(){
		     	clearForm(); 
		     	$('#descriptionAdd').jqxTooltip({disabled : true});
		      })
		};
		var saveAction = function(){
			if(!form.jqxValidator('validate')) {
				return false;
			}
	    	var agreementDateJS = "";
	    	if ($('#agreementDateAdd').jqxDateTimeInput('getDate') != undefined && $('#agreementDateAdd').jqxDateTimeInput('getDate') != null) {
	    		agreementDateJS = new Date($('#agreementDateAdd').jqxDateTimeInput('getDate').getTime());
	    	}
	    	var fromDateJS = "";
	    	if ($('#fromDateAdd').jqxDateTimeInput('getDate') != undefined && $('#fromDateAdd').jqxDateTimeInput('getDate') != null) {
	    		fromDateJS = new Date($('#fromDateAdd').jqxDateTimeInput('getDate').getTime());
	    	}
	    	var thruDateJS = "";
	    	if ($('#thruDateAdd').jqxDateTimeInput('getDate') != undefined && $('#thruDateAdd').jqxDateTimeInput('getDate') != null) {
	    		thruDateJS = new Date($('#thruDateAdd').jqxDateTimeInput('getDate').getTime());
	    	}
	        var row = { 
	        		productId:$('#productIdAdd').val(),
	        		partyIdFrom:$('#partyIdFromAdd').val(),
	        		partyIdTo:$('#partyIdToAdd').val(),
	        		roleTypeIdFrom:$('#roleTypeIdFromAdd').val(),
	        		roleTypeIdTo:$('#roleTypeIdToAdd').val(),
	        		agreementTypeId:$('#agreementTypeIdAdd').val(),
	        		description:$('#descriptionAdd').val(),
	        		textData:$('#textDataAdd').jqxInput('val'),
	        		agreementDate: agreementDateJS,
	        		fromDate: fromDateJS,
	        		thruDate: thruDateJS,
	        		statusId:$('#statusIdAdd').val()
	        	  };
		    $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        $("#jqxgrid").jqxGrid('clearSelection');                        
	        $("#jqxgrid").jqxGrid('selectRow', 0);  
	        return true;
		};
		var clearForm = function(){
			$("#productIdAdd").jqxDropDownButton("setContent","");
	        $("#agreementDateAdd").jqxDateTimeInput("val", null);
	        $("#partyIdFromAdd").jqxDropDownButton("setContent","");
	        $("#partyIdToAdd").jqxDropDownButton("setContent","");
	        $("#descriptionAdd").val("");
	        $("#textDataAdd").jqxInput('clear');
	        $("#thruDateAdd").jqxDateTimeInput("val", null);
	        $("#fromDateAdd").jqxDateTimeInput("val", null);
	        $("#jqxProductGrid").jqxGrid('clearSelection');
	        $("#jqxPartyFromGrid").jqxGrid('clearSelection');
	        $("#jqxPartyToGrid").jqxGrid('clearSelection');
	        $('#agreementTypeIdAdd').jqxDropDownList('clearSelection');
	        $('#roleTypeIdToAdd').jqxDropDownList('clearSelection');
	        $('#roleTypeIdFromAdd').jqxDropDownList('clearSelection');
	        form.jqxValidator("hide");
	        if(localStorage.getItem('objDate')){
	        	var objDate = $.parseJSON(localStorage.getItem('objDate'));
	        	if(objDate){
	        		setMinMaxDate($('#fromDateAdd'),objDate);
	        		setMinMaxDate($('#thruDateAdd'),objDate);
	        		setMinMaxDate($('#agreementDateAdd'),objDate);
	        	}
	        }
		};
		var setMinMaxDate = function(object,date){
			object.jqxDateTimeInput('setMinDate',date.min);
			object.jqxDateTimeInput('setMaxDate',date.max);
		}
		var getRolesByParty = function (role, partyId, listRole){
	        var jsc = Array();
	        $.ajax({
	            url: 'getListRoleBelongCondition',
	            type: 'POST',
	            dataType: 'json',
	            data: {partyId:partyId,listRoleCondition:listRole},
	            success: function (data) {
	                jsc = data.listRoleBelong;
	                renderRole(role, jsc);
	            }
	        });
	    };
	    var renderRole = function(role, data){
	    	role.jqxDropDownList("source", data);
	    };
		return {
			init : function(){
				initProductGrid();
				initElementGrid();
				initWindow();
				bindEvent();
				initRules();
			},
			save : saveAction,
			clear: clearForm,
			getRolesByParty : getRolesByParty
		};		
	}());
	
    $(document).ready(function(){
    	popupAction.init();
    });
</script>