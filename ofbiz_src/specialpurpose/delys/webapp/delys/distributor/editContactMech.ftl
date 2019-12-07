<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script type="text/javascript">
	<#assign countryGeo = delegator.findList("Geo",Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"),null,null,null,false) !>
	<#if countryGeo?exists>
	var listCountryGeo = [
	          <#list countryGeo as cg>
	          	{
	          		geoId : "${cg.geoId?if_exists}",
	          		geoName : "${StringUtil.wrapString(cg.get("geoName",locale))?if_exists}"
	          	},
	          </#list>
	  ];
		<#else> var listCountryGeo = [];
	</#if>
	var sourceCountryGeoId = {
			localdata : listCountryGeo,
			datatype : "array",
			datafield : [
	             {name : "geoId"},
	             {name : "geoName"}
	         ]
	};
	var dataAdapterCountryGeoId = new $.jqx.dataAdapter(sourceCountryGeoId);
	<#if mechMap.contactMechTypes?exists>
		var contactMechType = [
				<#list mechMap.contactMechTypes as contactMechType>
					{
						contactMechTypeId : "${contactMechType.contactMechTypeId?if_exists}",
						description : "${StringUtil.wrapString(contactMechType.get("description", locale))?if_exists}"
					},
				</#list>   
		   ];
		<#else> var contactMechType = [];
	</#if>
	var sourcecontactMechType = {
			localdata : contactMechType,
			datatype : "array",
			datafield : [
	             {name : "contactMechTypeId"},
	             {name : "description"}
             ]
	};
	var dataAdaptercontactMechType = new $.jqx.dataAdapter(sourcecontactMechType);
	<#if mechMap.purposeTypes?exists>
		var listcontactMechPurposeType = [
	                  <#list mechMap.purposeTypes as purpuseType>
	                  		{
	                  			contactMechPurposeTypeId : "${purpuseType.contactMechPurposeTypeId?if_exists}",
	                  			description : "${StringUtil.wrapString(purpuseType.get("description",locale))?if_exists}"
	                  		},
	                  </#list>
	          ];
		<#else> var listcontactMechPurposeType = [];
	</#if>
	var source = {
			localdata: listcontactMechPurposeType,
			dataType : "array",
			datafield : [
	             {name : "contactMechPurposeTypeId"},
	             {name : "description"}
	         ]
			
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
</script>
<body>
	<div id="container">
		<div id="notification" style="display:none;"></div>
	</div>
	<div id="body">
		<form id="createcontactmechform">
			<div class="row-fluid form-window-content">
				<div class="span12">
					<div class="row-fluid margin-bottom10">
						<div class='span6 align-right asterisk'>
							${uiLabelMap.PartySelectContactType}
				        </div>
						<div class="span6">
							<div id="preContactMechTypeId" name="preContactMechTypeId"></div>
						</div>
					</div>
				</div>
			</div>
		</form>
		<form id="editcontactmechform">
			<div id="CreateContent"></div>
		</form>
		<div class='row-fluid'>
			<div class="span12 margin-top10">
				<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='icon-arrow-left'></i> ${uiLabelMap.CommonGoBack}</button>
				<button id="alterSave" class='btn btn-primary form-action-button pull-right' style="display:inline-block;"><i class='icon-save'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterSave1" class='btn btn-primary form-action-button pull-right' style="display:none;"><i class='icon-save'></i>${uiLabelMap.DAFinish}</button>
			</div>
		</div>
	</div>
</body>
<script>
	$(document).ready(function() {
	   start();
	});
	function start(){
		$("#preContactMechTypeId").jqxComboBox({width: 220, height:25, source: dataAdaptercontactMechType, displayMember : "description", valueMember : "contactMechTypeId"});
		$("#createcontactmechform").jqxValidator({
			rules : [
			         {input : '#preContactMechTypeId', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action: 'keyup,blur,change', rule: function(){
			        	 if(!$('#preContactMechTypeId').val()){
			        		 return false;
			        	 }
			        	 return true;
			         }}
	         ]
			
		});
		$('#alterSave').click(function(){
			$("#createcontactmechform").jqxValidator('validate');
		});
		$("#createcontactmechform").on('validationSuccess', function(){
			var request = $.ajax({
				type: "POST",
				url : "CreateNewStoreContactMech",
				data : {
					contactMechTypeId : $('#preContactMechTypeId').val()
				},
				dataType : "html",
				success: function(data){
					jQuery('#alterSave1').css("display", "inline-block");
            		jQuery('#alterSave').css("display", "none");
            		$("#CreateContent").html(data);
            		return true;
				}
			});request.done(function(data){
				if($("#contactMechPurposeTypeId").val() != undefined){
					if($("#preContactMechTypeId").val() == "POSTAL_ADDRESS"){
						$("#editcontactmechform").jqxValidator({
							rules : [
							         {input : '#contactMechPurposeTypeId', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action: 'keyup,blur,change', rule : function(){
							        	 if(!$('#contactMechPurposeTypeId').val()){
							        		 return false;
							        	 }
							        	 return true;
							         }}
					         ]
						});
						$("#alterSave1").click(function(){
							$("#editcontactmechform").jqxValidator('validate');
						})
						$("#editcontactmechform").on('validationSuccess', function(){
							var row ={};
							row = {
									contactMechTypeId : $('#preContactMechTypeId').val(),
									contactMechPurposeTypeId : $('#contactMechPurposeTypeId').val(),
									toName : $('#toName').val(),
									attnName : $('#attnName').val(),
									address1 : $('#address1').val(),
									address2 : $('#address2').val(),
									city : $('#city').val(),
									countryGeoId : $('#editcontactmechform_countryGeoId').val(),
									postalCode : $('#postalCode').val(),
									stateProvinceGeoId : $('#stateProvinceGeoId').val(),
									facilityId : '${parameters.facilityId?if_exists}'
							};
							$.ajax({
								type : "POST",
								data : row,
								datatype : "array",
								url : "createNewPostalAddFacilityContactMech",
								success : function(){
									$("#notification").jqxNotification({opacity: 0.9,appendContainer: "#container",autoClose: true,template: "success"});
									$("#notification").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
									$("#notification").jqxNotification('open');
									
									$("#contactMechPurposeTypeId").jqxComboBox('selectIndex', null);
									$("#toName").jqxInput('val', null);
									$("#attnName").jqxInput('val', null);
									$("#address1").jqxInput('val', null);
									$("#address2").jqxInput('val', null);
									$("#postalCode").jqxInput('val', null);
									$("#countryGeoId").jqxComboBox('selectIndex', null);
									$("#stateProvinceGeoId").jqxComboBox('selectIndex', null);
								}
							});
						})
					}
					else if("TELECOM_NUMBER" == $("#preContactMechTypeId").val()){
						$("#editcontactmechform").jqxValidator({
							rules : [
									{input : '#contactMechPurposeTypeId', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action: 'keyup,blur,change', rule : function(){
										 if(!$('#contactMechPurposeTypeId').val()){
											 return false;
										 }
										 return true;
									 }},
							         {input : '#countryCode', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action : 'keyup,blur,change', rule :'required'},
							         {input : '#areaCode', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action : 'keyup,blur,change', rule : 'required'},
							         {input : '#contactNumber', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action : 'keyup,blur,change', rule : 'required'}
					         ]
							
						});
						$("#alterSave1").click(function(){
							$("#editcontactmechform").jqxValidator('validate');
						})
						$("#editcontactmechform").on('validationSuccess', function(){
							var row ={};
							row ={
									contactMechTypeId : $('#preContactMechTypeId').val(),
									contactMechPurposeTypeId : $('#contactMechPurposeTypeId').val(),
									countryCode : $('#countryCode').val(),
									areaCode : $('#areaCode').val(),
									contactNumber : $('#contactNumber').val() + $('#extension').val(),
									facilityId : '${parameters.facilityId?if_exists}'
							};
							var request = $.ajax({
								type : "POST",
								data : row,
								datatype : "array",
								url : "createNewTeleComFacilityContactMech",
								success : function(data){
									$("#notification").jqxNotification({opacity: 0.9,appendContainer: "#container",autoClose: true,template: "success"});
									$("#notification").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
									$("#notification").jqxNotification('open');
									
									$("#contactMechPurposeTypeId").jqxComboBox('selectIndex',null);
									$("#countryCode").jqxInput('val', null);
									$("#areaCode").jqxInput('val',null);
									$("#contactNumber").jqxInput('val',null);
									$("#extension").jqxInput('val',null);
								}
							});
							request.done(function(data){
							})
						})
					}
					else if("EMAIL_ADDRESS" ==$("#preContactMechTypeId").val()){
						$("#editcontactmechform").jqxValidator({
							rules : [
									{input : '#contactMechPurposeTypeId', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action: 'keyup,blur,change', rule : function(){
										 if(!$('#contactMechPurposeTypeId').val()){
											 return false;
										 }
										 return true;
									 }},
									 {input : '#emailAddress', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action : 'keyup,blur,change', rule :'required'}
					         ]
						});
						$("#alterSave1").click(function(){
							$("#editcontactmechform").jqxValidator('validate');
						})
						$("#editcontactmechform").on('validationSuccess', function(){
							var row = {};
							row = {
									contactMechTypeId : $('#preContactMechTypeId').val(),
									contactMechPurposeTypeId : $('#contactMechPurposeTypeId').val(),
									facilityId : '${parameters.facilityId?if_exists}',
									infoString : $('#emailAddress').val()
							};
							var request = $.ajax({
								type : "POST",
								data : row,
								datatype : "array",
								url : "createNewOtherTypeFacilityContactMech",
								success : function(data){
									$("#notification").jqxNotification({opacity: 0.9,appendContainer: "#container",autoClose: true,template: "success"});
									$("#notification").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
									$("#notification").jqxNotification('open');
									
									$("#contactMechPurposeTypeId").jqxComboBox('selectIndex',null);
									$("#emailAddress").jqxInput('val',null);
								}
							});
							request.done(function(data){
							})
							})
						}
						else{
							$("#editcontactmechform").jqxValidator({
								rules : [
										{input : '#contactMechPurposeTypeId', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action: 'keyup,blur,change', rule : function(){
											 if(!$('#contactMechPurposeTypeId').val()){
												 return false;
											 }
											 return true;
										 }},
										 {input : '#infoString', message : '${StringUtil.wrapString(uiLabelMap.DAThisFieldIsRequired)}', action : 'keyup,blur,change', rule :'required'}
						         ]
							});
							$("#alterSave1").click(function(){
								$("#editcontactmechform").jqxValidator('validate');
							})
							$("#editcontactmechform").on('validationSuccess', function(){
								var row = {};
								row = {
										contactMechTypeId : $('#preContactMechTypeId').val(),
										contactMechPurposeTypeId : $('#contactMechPurposeTypeId').val(),
										facilityId : '${parameters.facilityId?if_exists}',
										infoString : $('#infoString').val()
								};
								var request = $.ajax({
									type : "POST",
									data : row,
									datatype : "array",
									url : "createNewOtherTypeFacilityContactMech",
									success : function(data){
										$("#notification").jqxNotification({opacity: 0.9,appendContainer: "#container",autoClose: true,template: "success"});
										$("#notification").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
										$("#notification").jqxNotification('open');
										
										$("#contactMechPurposeTypeId").jqxComboBox('selectIndex',null);
										$("infoString").jqxInput('val',null);
									}
								});
								request.done(function(data){
								})
							})
						}
					}
				})
			})
			$("#alterCancel").click(function(){
				window.location.href = "<@ofbizUrl>authview/${donePage}?facilityId=${parameters.facilityId}</@ofbizUrl>";
			})
		}
		
		<#--$("#alterSave").click(function(){
			<#if "POSTAL_ADDRESS" = mechMap.contactMechTypeId?if_exists>
				var row ={};
				row = {
						contactMechTypeId : '${parameters.contactMechTypeId?if_exists}',
						contactMechPurposeTypeId : $('#contactMechPurposeTypeId').val(),
						toName : $('#toName').val(),
						attnName : $('#attnName').val(),
						address1 : $('#address1').val(),
						address2 : $('#address2').val(),
						city : $('#city').val(),
						countryGeoId : $('#editcontactmechform_countryGeoId').val(),
						postalCode : $('#postalCode').val(),
						stateProvinceGeoId : $('#stateProvinceGeoId').val(),
						facilityId : '${parameters.facilityId?if_exists}'
				};
				$.ajax({
					type : "POST",
					data : row,
					datatype : "array",
					url : "createNewPostalAddFacilityContactMech",
					success : function(){
						$("#notification").jqxNotification({opacity: 0.9,autoClose: true,template: "success"});
						$("#notification").text('${StringUtil.wrapString(uiLabelMap.DACreateSuccessful)}');
						$("#notification").jqxNotification('open');
						
					}
				});
			</#if>
		})-->
	
</script>
