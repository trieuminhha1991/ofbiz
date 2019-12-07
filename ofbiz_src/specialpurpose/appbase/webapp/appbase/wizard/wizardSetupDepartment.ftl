<style type="text/css">
	#toolbarcontainerjqxgridSubsidiary{
		padding-left:0;
	}
	#toolbarcontainerjqxgridSubsidiary h4{
		font-size: 11pt;
	    line-height: 20px;
	}
	#toolbarcontainerjqxgridSubsidiary .custom-control-toolbar{
		margin-top:0;
		margin-bottom:0;
		padding-top:0;
		padding-bottom:0;
	}
</style>
<div id="alterpopupWindowStep2" style="display:none">
	<div>${uiLabelMap.BSSetupSubsidiary}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="wn_step2_jqxgridSubsidiary"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-left form-window-content-custom">
	   			<div style="margin-left: 30px">
	   				<div id="wn_step2_noSubsidiary">${uiLabelMap.BSNoHaveSubsidiaryInCompany}</div>
	   			</div>
	   		</div>
	   		<div class="pull-right form-window-content-custom">
	   			<button id="wn_step2_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="wn_step2_alterSaveAndContinue" class='btn btn btn-success form-action-button'><i class='fa-check'></i> ${uiLabelMap.BSSaveAndContinue}</button>
				<button id="wn_step2_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#include "wizardSetupDepartmentSubsidiaryAdd.ftl"/>

<script type="text/javascript">
	if (typeof(Grid) == "undefined") {
		var Grid = (function(){
			var createCustomControlButton = function(grid, container, value){
				var tmpStr = value.split("@");
				var id = grid.attr('id');
				var str = '';
				var group = $('.custom-control-toolbar').length + 1;
	            if (tmpStr.length == 4) {
	            	if(tmpStr[1] == '_last_update_'){
	            		str = '<div class="custom-control-toolbar">' + '&nbsp;<span id=' + tmpStr[1] +'></span>&nbsp;'
	    					+'<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">'
	    					+'<i class="' + tmpStr[0] +'"></i></a><span id="_status_update_" style="float: right;margin-right: 4px; color: #4383b4;display:none;"><image src="/images/ajax-loader.gif"></span></div>';
	    			}else{
	    				str = '<div class="custom-control-toolbar">'
	    					+'<a id="customcontrol' + id + group + '" style="color:#438eb9;" href="' + tmpStr[2] +'" onclick="' + tmpStr[3] + '">'
	    					+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
	    			}
	                container.append(str);
	            } else {
					var tmp = tmpStr[2];
					var link = tmpStr[2];
					var target = "";
					if(tmp.indexOf("$") != "-1"){
						var arr = tmp.split("$");
						link = arr.shift();
						target = arr.pop();
					}
					str = '<div class="custom-control-toolbar"><a id="customcontrol' + id + group +'" style="color:#438eb9;" href="' + link +'" '+target+'>'
						+'<i class="' + tmpStr[0] +'"></i>&nbsp;<span>' + tmpStr[1] +'</span></a></div>';
	                container.append(str);
	            }
			};
			return {
				createCustomControlButton: createCustomControlButton
			}
		}());
	}
	var OlbWizardStep2 = (function(){
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementComplex();
			initEvent();
			initValidateForm();
			
			OlbWizardStep2Sub.init();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#alterpopupWindowStep2"), {maxWidth: 1020, width: 1020, height: 460, cancelButton: $("#wn_step2_alterCancel")});
			jOlbUtil.checkBox.create($("#wn_step2_noSubsidiary"));
		};
		
		var initElementComplex = function(){
			var configGridSubsidiary = {
				datafields: [
					{name: 'partyId', type: 'string'},
					{name: 'partyCode', type: 'string'},
					{name: 'groupName', type: 'string'},
					{name: 'phoneNumber', type: 'string'},
					{name: 'emailAddress', type: 'string'},
					{name: 'officeSiteName', type: 'string'},
					{name: 'taxAuthInfos', type: 'string'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'countryGeoId', type: 'string'},
					{name: 'stateProvinceGeoId', type: 'string'},
					{name: 'districtGeoId', type: 'string'},
					{name: 'wardGeoId', type: 'string'},
					{name: 'address1', type: 'string'},
				],
				columns: [
					{text: '${uiLabelMap.BSSubsidiaryId}', dataField: 'partyCode', width: 100}, 
					{text: '${uiLabelMap.BSSubsidiaryName}', dataField: 'groupName', minWidth: 160}, 
					{text: '${uiLabelMap.BSPhone}', dataField: 'phoneNumber', width: 120}, 
					{text: '${uiLabelMap.BSEmail}', dataField: 'emailAddress', width: 100}, 
					{text: '${uiLabelMap.BSOfficeSiteName}', dataField: 'officeSiteName', width: 100}, 
					{text: '${uiLabelMap.BSTaxCode}', dataField: 'taxAuthInfos', width: 100}, 
					{text: '${uiLabelMap.BSCurrencyUomId}', dataField: 'currencyUomId', width: 100}, 
					{text: '${uiLabelMap.BSAddress1}', dataField: 'address1', width: 100}, 
					{text: '${uiLabelMap.BSWard}', dataField: 'wardGeoId', width: 100}, 
					{text: '${uiLabelMap.BSCounty}', dataField: 'districtGeoId', width: 100}, 
					{text: '${uiLabelMap.BSStateProvince}', dataField: 'stateProvinceGeoId', width: 100}, 
					{text: '${uiLabelMap.BSCountry}', dataField: 'countryGeoId', width: 100} 
				],
				width: '100%',
				height: 200,
				editable: true,
				editmode: 'dblclick',
				sortable: false,
				filterable: false,
				pageable: true,
				pagesize: 10,
				showfilterrow: false,
				useUtilFunc: false,
				<#-- <#if updateMode>
				useUrl: true,
				url: 'jqxGeneralServicer?sname=JQGetListProdConfigItemProduct',
				</#if> -->
				useUrl: false,
				url: '',
				groupable: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				selectionmode:'singlerow',
				virtualmode: false,
				//toolbarheight: 22,
				showtoolbar:true,
				rendertoolbar: function(toolbar){
					<#assign customcontrol1 = "fa fa-plus@@javascript: void(0);@OlbWizardStep2.addSubsidiary()">
					<#assign customcontrol2 = "fa fa-minus@@javascript: void(0);@OlbWizardStep2.removeSubsidiary()">
					<@renderToolbar id="wn_step2_jqxgridSubsidiary" isShowTitleProperty="false" customTitleProperties="" isCollapse="false" showlist="false" 
						customControlAdvance="" filterbutton="" clearfilteringbutton="false" 
						addrow="false" addType="popup" alternativeAddPopup="" 
						deleterow="false" deleteConditionFunction="" deleteConditionMessage="" 
						virtualmode="false" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
						updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
						customcontrol1=customcontrol1 customcontrol2=customcontrol2 customcontrol3="" customtoolbaraction=""/>
				},
			};
			new OlbGrid($("#wn_step2_jqxgridSubsidiary"), null, configGridSubsidiary, []);
		};
		var initEvent = function(){
			$("#step2").on("click", function(){
				$("#alterpopupWindowStep2").jqxWindow("open");
			});
			
			$('#wn_step2_noSubsidiary').on('change', function(event){
				var checked = event.args.checked;
				if (checked) {
					$("#wn_step2_jqxgridSubsidiary").jqxGrid({disabled: true});
				} else {
					$("#wn_step2_jqxgridSubsidiary").jqxGrid({disabled: false});
				}
			});
			
			$('#wn_step2_alterSaveAndContinue').on("click", function(){
				createFinishStep2(true);
			});
			
			$('#wn_step2_alterSave').on('click', function(){
				createFinishStep2();
			});
		};
		var createFinishStep2 = function(isContinue){
			//if(!validatorVAL.validate()) return false;
			
			var noHaveSubsidiary = $('#wn_step2_noSubsidiary').val();
			var dataMap = {"noHaveSubsidiary": noHaveSubsidiary};
			if (!noHaveSubsidiary) {
				var rowsData = getValueSubsidiary();
				if (rowsData == null || rowsData.length <= 0) {
					OlbCore.alert.error("${uiLabelMap.BSYouHaveNotYetEnteredSubsidiary}!");
					return false;
				}
				dataMap.partySubsidiary = JSON.stringify(rowsData);
			}
			
			$.ajax({
                type: "POST",
                url: "wizardSetupSubsidiary",
                data: dataMap,
                beforeSend: function(){
                    $("#loader_page_common").show();
                    var step2 = $("#step2");
		        	step2.removeClass("alert-error");
		        	step2.removeClass("alert-success");
	        		var itemI = step2.find("i");
	        		if (itemI) itemI.remove();
                }, 
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
			        	
			        	var step2 = $("#step2");
			        	step2.addClass("alert-error");
		        		step2.prepend($('<i class="icon-remove"></i>'));
		        		
			        	return false;
					}, function(data){
                    	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	//if (data.partyId) {
			        		var step2 = $("#step2");
			        		step2.addClass("alert-success");
			        		step2.prepend($('<i class="icon-ok green"></i>'));
			        		
			        		$('#alterpopupWindowStep2').jqxWindow("close");
			        		
			        		if (isContinue) {
			        			OlbWizardStep3.openWindow();
			        			OlbWizardStep3.refreshWindow();
			        		}
			        	//}
                    });
                },
                error: function(){
                    alert("Send to server is false!");
                },
                complete: function(){
                	$("#loader_page_common").hide();
                }
            });
		};
		var initValidateForm = function(){
			
		};
		var addSubsidiary = function(){
			$("#alterpopupWindowStep2AddSub").jqxWindow("open");
			OlbWizardStep2Sub.initContent();
		};
		var removeSubsidiary = function(){
			var rowIndex = $("#wn_step2_jqxgridSubsidiary").jqxGrid('getselectedrowindex');
			if (rowIndex == null || rowIndex < 0) {
				jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
			} else {
				var rowData = $("#wn_step2_jqxgridSubsidiary").jqxGrid('getrowdata', rowIndex);
				if (rowData) {
					$("#wn_step2_jqxgridSubsidiary").jqxGrid('deleterow', rowData.uid);
				}
			}
		};
		var getValueSubsidiary = function() {
			var data = $("#wn_step2_jqxgridSubsidiary").jqxGrid('getboundrows');
			if (data) {
				//for (var x in data) {
				//	if (data[x].groupName) {
				//		data[x].groupName = "";
				//	}
				//}
				return data;
			}
			return [];
		};
		var setValue = function(data){
			if (data.noHaveSubsidiary) $("#wn_step2_noSubsidiary").jqxCheckBox("checked", true);
		};
		var openWindow = function(){
			$("#alterpopupWindowStep2").jqxWindow("open");
		};
		return {
			init: init,
			addSubsidiary: addSubsidiary,
			removeSubsidiary: removeSubsidiary,
			openWindow: openWindow,
			setValue: setValue
		}
	}());
</script>
