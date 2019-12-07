<!-- <table>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="userLoginId">${uiLabelMap.UserLoginID}</label> <span style="color: red">(*)</span></td>
		<td><input type="text" id="userLoginId" name="userLoginId" /></td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="currentPassword">${uiLabelMap.CurrentPassword}</label> <span style="color: red">(*)</span></td>
		<td><input type="password" id="currentPassword" name="currentPassword" /></td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="currentPasswordVerify">${uiLabelMap.CurrentPasswordVerify}</label> <span style="color: red">(*)</span></td>
		<td><input type="password" id="currentPasswordVerify" name="currentPasswordVerify" /></td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="passwordHint">${uiLabelMap.PasswordHint}</label></td>
		<td><input type="text" id="passwordHint" name="passwordHint" /></td>
	</tr>
	<tr>
		<td><label class="padding-bottom5 padding-right15" for="requirePasswordChange">${uiLabelMap.RequirePasswordChange}</label></td>
		<td>
			<select name="requirePasswordChange" id="requirePasswordChange">
				<option value="N" select="selected">${uiLabelMap.CommonN}</option>
				<option value="Y">${uiLabelMap.CommonY}</option>
			</select>
		</td>
	</tr>
</table> -->
<div class="row-fluid" style="margin-top: 20px">
	<div class="span12">
		<div class="row-fluid form-window-content">
			<div class='span6'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						${uiLabelMap.UserLoginID}
					</div>
					<div class='span7'>
						<input type="text" id="userLoginId">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						${uiLabelMap.CurrentPassword}
					</div>
					<div class='span7'>
						<input type="password" id="currentPassword"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						${uiLabelMap.CurrentPasswordVerify}
					</div>
					<div class='span7'>
						<input type="password" id="currentPasswordVerify"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						${uiLabelMap.RequirePasswordChange}
					</div>
					<div class='span7'>
						<div id="requirePasswordChange"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
	var requiredPwChangeArr = [
	    {value: "Y", description: "${StringUtil.wrapString(uiLabelMap.CommonYes)}"},
	    {value: "N", description: "${StringUtil.wrapString(uiLabelMap.CommonNo)}"}
	];
	var sourceChangePw = {
			localdata: requiredPwChangeArr,
            datatype: "array"
	};
	var dataAdapterChangePW = new $.jqx.dataAdapter(sourceChangePw);
	$(document).ready(function () {
		$("#userLoginId").jqxInput({  width: '195px', height: '25px', theme: 'olbius'});
		$("#currentPassword").jqxPasswordInput({  width: '195px', height: '25px', theme: 'olbius'});
		$("#currentPasswordVerify").jqxPasswordInput({  width: '195px', height: '25px', theme: 'olbius'});
		
		$("#requirePasswordChange").jqxDropDownList({
			source: dataAdapterChangePW,  displayMember: "description", valueMember: "value", theme: 'olbius', selectedIndex: 0,
    		height: 25, width: 195, theme: 'olbius', autoDropDownHeight: true,
    		renderer: function (index, label, value) {
				for(i=0; i < requiredPwChangeArr.length; i++){
					if(requiredPwChangeArr[i].value == value){
						return requiredPwChangeArr[i].description;
					}
				}
			    return value;
			}
		});
	});
	function getEmployeeUserLoginInfo(){
		var userLoginArr = new Array();
		userLoginArr.push({"userLoginId": $("#userLoginId").val()});
		userLoginArr.push({"currentPassword": $("#currentPassword").jqxPasswordInput('val')});
		userLoginArr.push({"currentPasswordVerify": $("#currentPasswordVerify").jqxPasswordInput('val')});
		userLoginArr.push({"requirePasswordChange": $("#requirePasswordChange").val()});
		return userLoginArr; 
	}
</script>