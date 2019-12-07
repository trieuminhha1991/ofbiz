<div id="emailForm" class='hide'>
    <div>${uiLabelMap.composeEmail}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class="row-fluid">
				<div id="emailList">
					<div id="emailChosenJqx"></div>
				</div>
				<div class='dropdown-action'>
					<button id="chooseAllEmail" class='btn btn-primary form-action-button '><i class='fa fa-check-square-o'></i>&nbsp;${uiLabelMap.CheckAll}</button>
					<button id="clearEmail" class='btn btn-danger form-action-button'><i class='fa fa-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>
				</div>
			</div>
			<div class="row-fluid margin-top">
				<input id="emailSubject" placeholder="${uiLabelMap.subject}" class="span12" style="padding-left: 5px; padding-right: 5px;"/>
			</div>
			<div class="row-fluid margin-top">
				<textarea id="mailContent" class="span12 resize-none"></textarea>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelSendEmail" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="sendEmailBt" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.send}</button>
		</div>
	</div>
</div>
<script>
	var listEmailSelected = [];
	var isSelectAllEmail = false;
	var ProcessEmailSelected = function(dropdown, grid){
		var selected = grid.jqxGrid('getselectedrowindexes');
        renderEmail(dropdown, grid, selected);
	};
	var renderEmail = function(dropdown, grid, selected){
		var total = 0;
		var str = "";
		var tmp;
		for(var x in selected){
    		if(typeof(selected[x]) == "object"){
    			tmp = selected[x];
    		}else{
    			tmp = grid.jqxGrid("getrowdata", selected[x]);	
    		}
    		if(tmp && tmp.email){
    			if(total != 0){
    				str += "; ";
    			}
				str += tmp.email;
				listEmailSelected.push(tmp.email);
    			total++;
    		}
    	}
    	var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">' + str + '</div>';
		dropdown.jqxDropDownButton('setContent', dropDownContent);
	};
</script>
<#if id1 == "personContacts">
	<#include "initDropdownContact.ftl"/>
<#else>
	<#include "initDropdownGroupContact.ftl"/>
</#if>
<script>
	function OpenPopupSendEmail(){
		$("#emailForm").jqxWindow("open");	
	}
	
	var emailFromObj = (function(){
		var form = $("#emailForm");
		var grid = $("#emailChosenJqx");
		var initElement = function(){
			initPartyGridContact();
			$('#mailContent').jqxEditor({
		        width: '780px',
		        height: '310px',
		        theme: 'olbiuseditor'
		    });
		    $('#emailSubject').jqxInput({width: 768});
		    form.on("close", function(){
		    	ClearForm();
		    });
		};
		function initWindowSendEmail() {
			var popup = $("#emailForm");
			popup.jqxWindow({
				width : 800,
				height : 500,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				theme : theme
			});
		};
		var bindEvent = function(){
			$("#chooseAllEmail").click(function(){
				isSelectAllEmail = true;
				var data = $('#emailChosenJqx').jqxGrid('getboundrows');
				renderEmail($('#emailList'), $('#emailChosenJqx'), data);
			});
			$("#clearEmail").click(function(){
				isSelectAllEmail = false;
				listEmailSelected = [];
				GridUtils.clearDropDownButton($("#emailList"), $("#emailChosenJqx"));
			});
			$("#sendEmailBt").click(function(){
				if(!form.jqxValidator('validate')){
					return false;
				}
				var ind = grid.jqxGrid("getselectedrowindexes");
				if(isSelectAllEmail){
					listEmailSelected = [];
					listEmailSelected.push("all@all");
				}
				if(listEmailSelected.length){
					var subject = $("#emailSubject").val();
					var content = $("#mailContent").jqxEditor('val');
					$.ajax({
						url: "sendEmailSupport",
						type: "POST",
						data: {
							listEmail : JSON.stringify(to),
							subject: subject,
							content: content
						},
						success: function(res){
							emailForm.modal("hide");
							ClearForm();
						}
					});
				}
			});
		};
		var ClearForm = function(){
			$("#emailSubject").jqxInput('val','');
			$("#mailContent").jqxEditor('val','');
			$('#emailList').jqxDropDownButton('close');
			isSelectAllEmail = false;
			listEmailSelected = [];
			setTimeout(function(){
				form.jqxValidator("hide");
			}, 100);
		};
		var initEmailFormRule = function(){
			form.jqxValidator({
				rules: [
					{input: '#emailList', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
							if(listEmailSelected.length){
								return true;	
							}
		                    return false;
		                }
					},
					{input: '#emailSubject', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function (input, commit) {
							var val = input.val();
							if(val != "" && val.length){
								return true;
							}
							return false;
		                }
					},
					{input: '#mailContent', message: '${uiLabelMap.IssueContentLengthRequired}', action: 'keyup, blur',
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
				initElement();
				initWindowSendEmail();
				bindEvent();
				initEmailFormRule();
			}
		};
	})();
	$(document).ready(function(){
		emailFromObj.init();
	});
	
</script>