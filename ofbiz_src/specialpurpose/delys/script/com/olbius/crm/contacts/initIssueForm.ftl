<div id="issueFormWindow" class='hide'>
	<div>
		${uiLabelMap.createIssue}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.directionComm}</label>
				</div>  
				<div class="span7">
					<div id="directionComm"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span5 text-algin-right'>
					<label class="asterisk">${uiLabelMap.CommunicationType}</label>
				</div>  
				<div class="span7">
					<div id="support"></div>
		   		</div>
		   	</div>
		   	<div class='row-fluid margin-bottom10'>
		   		<div class='span12'>
		   			<textarea id="issueContent" class='margin-left20'></textarea>
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
	//add issue for each customer
	var issueForm = $("#issueFormWindow");
	var issueObj = (function(){
		var initElementIssue = function(){
			$('#issueContent').jqxEditor({
		        width: '545px',
		        height: '210px',
		        theme: 'olbiuseditor'
		    });
		    $("#support").jqxDropDownList({ 
		    	theme: theme, 
		    	source: commType, 
		    	displayMember: "description", 
		    	valueMember: "communicationEventTypeId", 
		    	width: '200',
				height: '25'
			});
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
				height: '25'
			});
		};
		var initIssueWindow = function(popup) {
			popup.jqxWindow({
				width : 600,
				height : 400,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: '#cancelCreateIssue',
				theme : theme
			});
		};
		var bindEvent = function(){
			$("#createIssue").click(function() {
				if(!issueForm.jqxValidator('validate')){
					return;
				}
				var row = {
					support : $("#support").val(),
					content : $("#issueContent").jqxEditor('val'),
					type : $("#directionComm").val()
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
			$('#directionComm').jqxDropDownList('clearSelection');
			$('#support').jqxDropDownList('clearSelection');
			$("#issueContent").jqxEditor('val','');
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
					{input: '#support', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
		                    var index = input.jqxDropDownList('getSelectedIndex');
		                    return index != -1;
		                }
					},
					{input: '#issueContent', message: '${uiLabelMap.IssueContentLengthRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
		                    var index = input.jqxEditor('val');
		                    index = index.replace(/ /g, '');
		                    if(index.indexOf('<div></div>') == -1 && index.length > 20){
		                    	return true;	
		                    }
		                    return false;
		                }
					}
				]
			});
		};
		return {
			init: function(){
				initIssueWindow($("#issueFormWindow"));
				initElementIssue();	
				bindEvent();
				initIssueFormRule();
			}
		};
	})();
	
	$(document).ready(function(){
		issueObj.init();
	});
</script>