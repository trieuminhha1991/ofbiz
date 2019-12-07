<div id="issueFormWindow" class='hide'>
	<div>
		${uiLabelMap.createIssue}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.CommunicationDirection}</label>
						</div>
						<div class="span7">
							<div id="directionComm"></div>
						</div>
					</div>
					<!-- <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.CommunicationType}</label>
						</div>
						<div class="span7">
							<div id="support"></div>
						</div>
					</div> -->
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.ProductDiscussing}</label>
						</div>
						<div class="span7">
							<textarea id="productDiscussing" row="3" class='no-resize no-top-bottom-margin' style='width: 188px;'></textarea>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.CurrentBrandUsing}</label>
						</div>
						<div class="span7">
							<div id="CurrentBrandUsing"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.CurrentProductUsing}</label>
						</div>
						<div class="span7">
							<div id="CurrentProductUsing"></div>
						</div>
					</div>
				</div>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="">${uiLabelMap.Subject}</label>
						</div>
						<div class="span7">
							<!-- <input id="subject"/> -->
							<div id="subject"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class="asterisk">${uiLabelMap.ResultEnumId}</label>
						</div>
						<div class="span7">
							<div id="reasonTypeEnumId"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.ReasonEnumId}</label>
						</div>
						<div class="span7">
							<div id="reasonEnumId"></div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
								<label>${uiLabelMap.Content}</label>
						</div>
						<div class='span7 relative'>
							<div id="issueContent" class='margin-left20'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCreateIssue" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="createIssue" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script>
	var reasonType = [<#if reasonEnums?exists><#list reasonEnums as reason>{enumTypeId: '${reason.enumTypeId}', sequenceId: '${reason.sequenceId?if_exists}', description: "${StringUtil.wrapString(reason.description)?default("")}"},</#list></#if>];
	var listRCTypes = [<#if listRCType?exists><#list listRCType as rCType>{enumId: '${rCType.enumId}', description: "${StringUtil.wrapString(rCType.description)?default("")}"},</#list></#if>];
	var subjects = [<#if subjects?exists><#list subjects as rCType>{enumId: '${rCType.enumId}', sequenceId: '${rCType.sequenceId?if_exists}', description: "${StringUtil.wrapString(rCType.description)?default("")}"},</#list></#if>];
	var brands = [<#if brands?exists><#list brands as rCType>{partyId: '${rCType.partyId}', groupName: "${StringUtil.wrapString(rCType.groupName)?default("")}"},</#list></#if>];
	var genderData = [{
		gender: 'M',
		description : "${uiLabelMap.Male}"
	},{
		gender : 'F',
		description : "${uiLabelMap.Female}"
	}];

	$('#reasonEnumId').jqxDropDownList({width: 198, height: 23, autoDropDownHeight: true});

	//add issue for each customer
	var issueObj = (function(){
		var issueForm;
		var theme = 'olbius';
		var initElementIssue = function(){
			$('#issueContent').jqxEditor({
		        width: '554px',
		        height: '240px',
		        theme: 'olbiuseditor'
		    });
		    // $("#support").jqxDropDownList({
			// theme: 'olbius',
			// source: commType,
			// displayMember: "description",
			// valueMember: "communicationEventTypeId",
			// width: '200',
				// height: '25'
			// });
			var tmp = [{
				value : 'send',
				description: '${uiLabelMap.send}'
			},{
				value : 'receive',
				description: '${uiLabelMap.receive}'
			}];
			$("#directionComm").jqxDropDownList({
			theme: theme,
			source: tmp,
			displayMember: "description",
			valueMember: "value",
				autoDropDownHeight: true,
			width: '200',
				height: '25',
				selectedIndex: 0
			});
			$("#CurrentBrandUsing").jqxDropDownList({
			theme: theme,
			source: brands,
			displayMember: "groupName",
			valueMember: "partyId",
				dropDownHeight: 250,
			width: '200',
				height: '25',
				selectedIndex: 0
			});
			$("#CurrentProductUsing").jqxDropDownList({
			theme: theme,
			source: [],
			displayMember: "productName",
			valueMember: "productId",
				dropDownHeight: 250,
			width: '200',
				height: '25',
				selectedIndex: 0
			});
		    $("#reasonTypeEnumId").jqxDropDownList({
				theme: 'olbius',
			source: reasonType,
			displayMember: "description",
			valueMember: "enumTypeId",
			width: '200',
				dropDownWidth: 400,
				height: '25',
				renderer: function (index, label, value) {
					var da = reasonType[index];
					var seq = da.enumTypeId ? "[" + da.enumTypeId + "] " : '';
		            var valueStr = seq + label;
		            return valueStr;
		        },
			});
			if(reasonType.length >= 8){
				$("#reasonTypeEnumId").jqxDropDownList({dropDownHeight: 250});
			}else{
				$("#reasonTypeEnumId").jqxDropDownList({autoDropDownHeight: true});
			}
			// $("#subject").jqxInput({
				// height: 21,
				// width: 195,
			// });
			$("#subject").jqxDropDownList({ selectedIndex: 0,  source: subjects,
				valueMember: 'enumId', displayMember: 'description', autoDropDownHeight: true
			});
			$("#reasonEnumId").jqxDropDownList({ selectedIndex: 0,  source: [],
				valueMember: 'enumId', displayMember: 'description', autoDropDownHeight: true
			});
		};
		var initIssueWindow = function(popup) {
			popup.jqxWindow({
				width : 750,
				height : 470,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: '#cancelCreateIssue',
				theme : theme,
				initContent: function(){
					initElementIssue();
				}
			});
		};
		var bindEvent = function(){
			$("#createIssue").click(function() {
				var currentGrid = $("#communicationHistory");
				if(!issueForm.jqxValidator('validate')){
					return;
				}
				var row = {
					//$("#support").val()
					support : "PHONE_COMMUNICATION",
					content : $("#issueContent").jqxEditor('val'),
					subjectEnumId: $("#subject").val(),
					subject: $('#productDiscussing').val(),
					resultEnumTypeId : $("#reasonTypeEnumId").val(),
					resultEnumId : $("#reasonEnumId").val(),
					type : $("#directionComm").val(),
					partyId : CookieLayer.getCurrentParty().partyId
				};
				if (!currentGrid) {
					issueForm.jqxWindow('close');
					clearForm();
					return;
				}
				currentGrid.jqxGrid('addRow', null, row, "first");
				currentGrid.jqxGrid('clearSelection');
				currentGrid.jqxGrid('selectRow', 0);
				issueForm.jqxWindow('close');
				clearForm();
			});
		};
		var clearForm = function(){
			Grid.clearForm(issueForm);
			$("#issueContent").jqxEditor('val','');
			$('#subject').val(null);
		};
		var initIssueFormRule = function(){
			issueForm.jqxValidator({
				rules: [
					{input: '#directionComm', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
							var index = input.jqxDropDownList('getSelectedIndex');
		                    return index != -1;
		                }
					},
					{input: '#subject', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
		                    var index = input.jqxDropDownList('getSelectedIndex');
		                    return index != -1;
		                }
					},
					{input: '#reasonTypeEnumId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
		                    var index = input.jqxDropDownList('getSelectedIndex');
		                    return index != -1;
		                }
					},
					{input: '#issueContent', message: '${uiLabelMap.FieldRequired}', action: 'blur',
						rule: function (input, commit) {
		                    var index = input.jqxEditor('val');
		                    index = index.replace(/ /g, '');
		                    if(index.indexOf('<div></div>') == -1 && (index.indexOf('<div>&#8203;</div>')) == -1){
					return true;
		                    }
		                    input.css({
					position: 'absolute',
					top: 0,
					width: '100%',
							height: '1px',
							margin: '0'
		                    });
		                    input.show();
		                    return false;
		                }
					}
				]
			});
		};
		return {
			init: function(){
				issueForm = $("#issueFormWindow");
				initIssueWindow($("#issueFormWindow"));
				bindEvent();
				initIssueFormRule();
			}
		};
	})();

	$(document).ready(function(){
		issueObj.init();
	});
	var tmpReason = [];
	$("#reasonTypeEnumId").on('change', function (event) {
	    if (event.args) {
	        var item = event.args.item;
	    }
	    var resultEnumId = item.value;
	    var request = $.ajax({
			  url: "loadReasonListByReasonTypeId",
			  type: "POST",
			  data: {resultEnumId : resultEnumId},
			  dataType: "json",
			  success: function(data) {
				  var listReasonClaimm = data["listReasonClaim"];
				  tmpReason = listReasonClaimm;
				  if (!listReasonClaimm.length){
					  $("#reasonEnumId").jqxDropDownList({source: [], dropDownWidth : 200,});
				  } else {
						var source = {
						localdata: listReasonClaimm,
					datatype: "array"
				};
				var dataAdapter = new $.jqx.dataAdapter(source);
					  $("#reasonEnumId").jqxDropDownList({selectedIndex: 0,  source: dataAdapter, dropDownWidth : 400,
						renderer: function (index, label, value) {
							var da = tmpReason[index];
							var seq = da.sequenceId ? "[" + da.sequenceId + "] " : '';
				            var valueStr = seq + label;
				            return valueStr;
				        },
					  });
				  }
			  }
		});
	});
	$("#CurrentBrandUsing").on('change', function (event) {
	    if (event.args) {
	        var item = event.args.item;
	    }
	    var partyId = item.value;
	    var request = $.ajax({
			  url: "getProductBySupplier",
			  type: "POST",
			  data: {partyId : partyId},
			  dataType: "json",
			  success: function(data) {
				  var results = data["results"];
				  var obj = $("#CurrentProductUsing");
				  if (!results.length){
					  obj.jqxDropDownList({source: []});
				  } else {
					var source = {
						localdata: results,
						datatype: "array"
					};
					var dataAdapter = new $.jqx.dataAdapter(source);
					obj.jqxDropDownList({selectedIndex: 0,  source: dataAdapter, dropDownWidth : 400});
				  }
			  }
		});
	});
</script>
