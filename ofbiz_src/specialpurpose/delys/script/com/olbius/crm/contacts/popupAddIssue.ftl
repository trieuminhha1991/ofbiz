<div id="alterpopupWindow" class="hide">
    <div>${uiLabelMap.accCreateNew}</div>
    <div class='form-window-container'>
    	<div id='jqxTabs' style="position: relative;">
            <ul>
				<!-- <li>${uiLabelMap.issue}</li> -->
                <li>${uiLabelMap.generalInfo}</li>
                <li>${uiLabelMap.contactInfo}</li>
            </ul>
            <!-- <div id="newIssueContact">
	     		<#include "jqxNewIssue.ftl" />
	     	</div> -->
            <div id="newGeneralInfo">
	    		<#include "jqxEditGeneralInfo.ftl" />
	    	</div>
	    	
	    	<div id="newContactInfo">
	     		<#include "jqxEditContact.ftl" />
	     	</div>
        </div>
        <div class="form-action">
			<button id="cancelIssue" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveIssueAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
			<button id="saveIssue" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div>
<script>
	function enableEditor(){
		$('#issueGeneral').jqxEditor({
	        height: "250px",
	        width: '938px',
	        theme: 'olbiuseditor'
	    });
	}
	function getIssue(){
		return {
			type: $("#directionCommGeneral").val(),
			support: $("#supportGeneral").val(),
			content: CKEDITOR.instances.issueGeneral.getData()
		};
	}
</script>
<script>
	var customerForm = (function(){
		var popup = $("#alterpopupWindow");
		var initElementInfo = function(){
			$("#lastName").jqxInput({width: 195});
			$("#middleName").jqxInput({width: 195});
			$("#firstName").jqxInput({width: 195});
			$("#height").jqxNumberInput({spinButtons: false, decimalDigits: 0, min: 0, inputMode: 'simple'});
			$("#weight").jqxNumberInput({spinButtons: false, decimalDigits: 0, min: 0, inputMode: 'simple'});
			$("#idNumber").jqxMaskedInput({
				 width: 195,
				 height: 21,
			     mask:'999999999999'
			 });
			$("#idIssuePlace").jqxInput({width: 195});
			$("#gender").jqxDropDownList({source: genderData, valueMember: "gender", displayMember: "description", autoDropDownHeight: true});
			$("#birthDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#birthDate").jqxDateTimeInput('val', null);
			$("#birthPlace").jqxInput({width: 195});
			$("#idIssueDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', clearString: '${uiLabelMap.Clear}', todayString: '${uiLabelMap.Today}',showFooter:true});
			$("#idIssueDate").jqxDateTimeInput('val', null);
			$("#maritalStatus").jqxDropDownList({source: maritalStatusData, valueMember: 'maritalStatusId', displayMember: 'description', autoDropDownHeight: true});
			$("#numberChildren").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
			$("#ethnicOrigin").jqxDropDownList({source: ethnicOriginData, valueMember: 'ethnicOriginId', displayMember: 'description', dropDownHeight: 200, filterable: true});
			$("#religion").jqxDropDownList({source: religionData, valueMember: 'religionId', displayMember: 'description', autoDropDownHeight: true});
			$("#sourceTypeId").jqxDropDownList({source: emplAppSourceTypeData, valueMember: 'employmentAppSourceTypeId', displayMember: 'description', autoDropDownHeight: true});
			$("#nativeLand").jqxInput({width: 195});
			// $("#homeTel").jqxMaskedInput({ width: 195, mask: '(+####)###-###-####'});
			// $("#diffTel").jqxMaskedInput({ width: 195, mask: '(+####)###-###-####'});
			$("#mobile").jqxMaskedInput({ width: 195, mask: '(+####)###-###-###-##'});
			$("#email").jqxInput({width: 195});
			$("#prAddress").jqxInput({width: 195});
		};
		var initContactInfo = function(country, province, district, ward, address){
			var prCountryIndex = 0;
			for(var i = 0; i < countryData.length; i++){
				if('VNM' == countryData[i].geoId){
					prCountryIndex = i;
					getGeoData('getProvinceByCountry', 'VNM', province);
					break;
				}
			}
			address.jqxInput({width: 195});
			country.jqxDropDownList({selectedIndex: prCountryIndex, source: countryData, valueMember: 'geoId', displayMember:'geoName', filterable: true});
			province.jqxDropDownList({selectedIndex: 0, source: [], valueMember: 'geoId', displayMember:'geoName', filterable: true});
			district.jqxDropDownList({selectedIndex: 0 ,source: [], valueMember: 'geoId', displayMember:'geoName', filterable: true});
			ward.jqxDropDownList({selectedIndex: 0,source: [], valueMember: 'geoId', displayMember:'geoName', filterable: true});
			OnChangeGeo(country, province, district, ward);
		};
		var OnChangeGeo = function(country, province, district, ward){
			country.on('change', function(e){
				var obj = e.args.item;
				var val = obj.value;
				getGeoData('getProvinceByCountry', val, province);
			});
			province.on('change', function(e){
				var obj = e.args.item;
				var val = obj.value;
				getGeoData('getDistrictByProvince', val, district);
			});
			district.on('change', function(e){
				var obj = e.args.item;
				var val = obj.value;
				getGeoData('getWardByDistrict', val, ward);
			});
		};
		//Handle on change prDistrict
		var updateChildrenDropdown = function(dropdown, children, url){
			dropdown.on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	getGeoData(url, value, children);
		    	} 
			});
		};
		var getGeoData = function(url, geoId, children){
			$.ajax({
				url : url,
				data: {
					geoId: geoId
				},
				cache: true,
				type: "POST",
				success: function(res){
					var data = res.results;
					if(data){
						children.jqxDropDownList({source: data});
					}
				}
			});
		};
		var initRuleGeneralInfo = function(){
			$("#createNewApplicant").jqxValidator({
				rules: [
					{input: '#firstName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
					{input: '#lastName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				]
			});	
		};	
		function initWindow() {
			popup.jqxWindow({
				width : 980,
				maxWidth : 1000,
				height : 560,
				resizable : false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				theme : theme,
				initContent : function() {
					$('#jqxTabs').jqxTabs({
						width : '957',
						height : 460,
						position : 'top'
					});
					$('#jqxTabs').on('change', function(){
						popup.jqxValidator('hide');
					});
					$('#jqxTabs').on('selected', function (event) {
					    var selectedTab = event.args.item;
					    if(selectedTab == 1){
					    	popup.jqxValidator('hideHint','#firstName');
					    	popup.jqxValidator('hideHint','#lastName');
					    }else{
					    	popup.jqxValidator('hideHint','#mobile');
					    };
				    });
					// enableEditor();
				}
			});
		};
		var bindIssueFormEvent = function(){
			$("#saveIssue").click(function() {
				if(!SaveIssueAction()){
					return;
				}
				popup.jqxWindow('close');
			});
			$("#saveIssueAndContinue").click(function(){
				SaveIssueAction();
			});
		};
		var SaveIssueAction = function(){
			if(!$("#alterpopupWindow").jqxValidator('validate')){
				return false;
			}
			var ck = CKEDITOR.instances;
			var gr = $("#personContacts");
			var row = GetIssueFormData();
			gr.jqxGrid('addRow', null, row, "first");
			gr.jqxGrid('clearSelection');
			gr.jqxGrid('selectRow', 0);
			return true;
		};
		var GetIssueFormData = function(){
			var row = {
				lastName : $("#lastName").val(),
				middleName : $("#middleName").val(),
				firstName : $("#firstName").val(),
				gender : $("#gender").val(),
				birthPlace : $("#birthPlace").val(),
				birthDate : $("#birthDate").jqxDateTimeInput('getDate') ? $("#birthDate").jqxDateTimeInput('getDate').getTime() : "",
				height : $("#height").val(),
				weight : $("#weight").val(),
				referredByPartyId : $('#referredByPartyId').val(),
				idNumber : $("#idNumber").val(),
				idIssuePlace : $("#idIssuePlace").val(),
				idIssueDate : $("#idIssueDate").val(),
				maritalStatus : $("#maritalStatus").val(),
				numberChildren : $("#numberChildren").val(),
				ethnicOrigin : $("#ethnicOrigin").val(),
				religion : $("#religion").val(),
				nativeLand : $("#nativeLand").val(),
				sourceTypeId : $("#sourceTypeId").val(),
				// homeTel : $("#homeTel").jqxMaskedInput('valNoPrompt'),
				// diffTel : $("#diffTel").jqxMaskedInput('valNoPrompt'),
				mobile : $("#mobile").jqxMaskedInput('valNoPrompt'),
				email : $("#email").val(),
				prAddress : $("#prAddress").val(),
				prCountry : $("#prCountry").val(),
				prProvince : $("#prProvince").val(),
				prDistrict : $("#prDistrict").val(),
				prWard : $("#prWard").val(),
				crAddress : $("#crAddress").val(),
				crCountry : $("#crCountry").val(),
				crProvince : $("#crProvince").val(),
				crDistrict : $("#crDistrict").val(),
				crWard : $("#crWard").val()
			};
			return row;
		};
		var ClearForm = function(){
			$("#lastName").val('');
			$("#middleName").val('');
			$("#firstName").val(''),
			$("#gender").jqxDropDownList('clearSelection');
			$("#birthPlace").val('');
			$("#birthDate").jqxDateTimeInput('val', null);
			$("#birthDate").jqxDateTimeInput('close');
			$("#height").val('');
			$("#weight").val(''),
			$("#idNumber").jqxMaskedInput('clear');
			$("#idIssuePlace").val('');
			$("#idIssueDate").jqxDateTimeInput('val', null);
			$("#idIssueDate").jqxDateTimeInput('close');
			$("#maritalStatus").jqxDropDownList('clearSelection');
			$("#numberChildren").val('');
			$("#ethnicOrigin").jqxDropDownList('clearSelection');
			$("#religion").jqxDropDownList('clearSelection');
			$("#nativeLand").val('');
			$("#sourceTypeId").jqxDropDownList('clearSelection');
			// $("#homeTel").jqxMaskedInput('clearSelection'),
			// $("#diffTel").jqxMaskedInput('clearSelection'),
			$("#mobile").jqxMaskedInput('clearSelection'),
			$("#email").val('');
			$("#prAddress").val();
			$("#prCountry").val();
			$("#prProvince").val();
			$("#prDistrict").val();
			$("#prWard").val();
			$("#crAddress").val();
			$("#crCountry").val();
			$("#crProvince").val();
			$("#crDistrict").val();
			$("#crWard").val();
		};
		
		var initContactRule = function(){
			$("#alterpopupWindow").jqxValidator({
				rules: [
					{input: '#firstName', message: '${uiLabelMap.FieldRequired}', action: 'change', rule:'required'},
					{input: '#lastName', message: '${uiLabelMap.FieldRequired}', action: 'change', rule:'required'},
					{input: '#mobile', message: '${uiLabelMap.NumberFieldRequired}', action: 'change',
						rule: function (input, commit) {
							var val = input.jqxMaskedInput('valNoPrompt');
		                    if (val && isFinite(val)) {
		                        return true;
		                    }
		                    return false;
		              } 	
					},
					// {input: '#homeTel', message: '${uiLabelMap.NumberFieldRequired}', action: 'change',
						// rule: function (input, commit) {
		                    // if (isFinite(input.val())) {
		                        // return true;
		                    // }
		                    // return false;
		                // }
					// },
					// {input: '#diffTel', message: '${uiLabelMap.NumberFieldRequired}', action: 'change',
						// rule: function (input, commit) {
		                    // if (isFinite(input.val())) {
		                        // return true;
		                    // }
		                    // return false;
		                // }
					// },
					{input: '#email', message: '${uiLabelMap.EmailFieldRequired}', action: 'keyup, blur', rule: 'email'}
				]
			});
		};
		return {
			init : function(){
				initWindow();
				initElementInfo();
				bindIssueFormEvent();
				initContactInfo($("#crCountry"), $("#crProvince"), $("#crDistrict"), $("#crWard"), $("#crAddress"));
				initContactInfo($("#prCountry"), $("#prProvince"), $("#prDistrict"), $("#prWard"), $("#prAddress"));
				// initRuleGeneralInfo();
				initContactRule();
			}
		};
	})();
	$(document).ready(function(){
		customerForm.init();
	});
</script>