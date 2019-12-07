<div id="newPartyGroupWindow" style="display: none;">
	<div id="newPartyGroupWindowHeader">${uiLabelMap.HrolbiusAddOrganizationUnit}</div>
	<div style="overflow: hidden;">
		<div class='row-fluid form-window-content' style="overflow: hidden;">
			<div id="newPartyGroupForm">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							${uiLabelMap.OrgUnitId}
						</div>
						<div class='span7'>
							<input type="text" id="orgUnitIdCreateNew"/>
						</div>
					</div>
					
					<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.BelongOrgUnit}
		 				</div>
		 				<div class='span7'>
		 					<div id="belongOrgUnitButtonCreateNew">
		 						 <div style="border: none;" id='jqxTreeOrgUnitCreateNew'>
		 						 </div>
		 					</div>
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.CommonAddress}
		 				</div>
		 				<div class='span7'>
							<input type="text" id="addressOrgUnitCreateNew"/>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.CommonCountry}
		 				</div>
		 				<div class='span7'>
							<div id="countryPartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.PartyDistrictGeoId}
		 				</div>
		 				<div class='span7'>
							<div id="districtPartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
		 					${uiLabelMap.FunctionAndDuties}
		 				</div>
		 				<div class='span7'>
							<textarea id="commentsNewPartyGroup"></textarea>	 					
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.OrgUnitName}
						</div>
						<div class='span7'>
							<input type="text" id="orgUnitNameCreateNew"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right asterisk'>
							${uiLabelMap.CommonRole}
						</div>
						<div class='span7'>
							<div id="roleTypeList"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 align-right'>
							${uiLabelMap.CommonSymbol}
						</div>
						<div class='span7'>
							<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
								<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
								<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
								<div class="rowf-fluid">
									<div class="span12" style="margin-bottom: 0px !important; height: 0px !important">
							 			<input type="file" id="uploadedFile" name="uploadedFile"/>
							 		</div>
								</div>
						 	</form>
						</div>
					</div>
					
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.PartyState}
		 				</div>
		 				<div class='span7'>
							<div id="statePartyGroupNew"></div>	 					
						</div>
		 			</div>
		 			<div class='row-fluid margin-bottom10'>
		 				<div class='span5 align-right'>
		 					${uiLabelMap.PartyWardGeoId}
		 				</div>
		 				<div class='span7'>
							<div id="wardPartyGroupNew"></div>	 					
						</div>
		 			</div>
				</div>
			</div>
		</div>
		
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="partyGroupCreateNewCancel"><i class='fa-remove'></i>${uiLabelMap.Cancel}</button>
					<button class='btn btn-primary form-action-button pull-right' id="partyGroupCreateNewSave"><i class='fa-check'></i>${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>	

<script type="text/javascript">
	var actionCreateNewPartyGroup = (function(){
		$.jqx.theme = 'olbius';  
		theme = $.jqx.theme;
		var initInputEle = function(){
			$("#orgUnitIdCreateNew").jqxInput({theme: 'olbius', height: 23, width: '97%'});
			$("#orgUnitNameCreateNew").jqxInput({theme: 'olbius', height: 23, width: '97%'});
			$("#addressOrgUnitCreateNew").jqxInput({theme: 'olbius', height: 23, width: '97%'});
			$("#addressOrgUnitCreateNew").on('change', function(){
				if($(this).val()){
					<#if countryGeoIdDefault?exists>
						$("#countryPartyGroupNew").jqxDropDownList('selectItem', '${countryGeoIdDefault}');
					</#if>
				}else{
					$("#countryPartyGroupNew").jqxDropDownList('clearSelection');
					$("#statePartyGroupNew").jqxDropDownList('clearSelection');
					$("#districtPartyGroupNew").jqxDropDownList('clearSelection');
					$("#wardPartyGroupNew").jqxDropDownList('clearSelection');
				}
			});
		};
		var initJqxWindow = function(){
			$("#newPartyGroupWindow").jqxWindow({showCollapseButton: false, maxHeight: 500, maxWidth: 960, minHeight: 500, minWidth: 960, height: 500, width: 960,
				theme: 'olbius', isModal: true, autoOpen: false, 
	            initContent: function () {
	            	$('#commentsNewPartyGroup').jqxEditor({ 
	            		width: '750px',
	    	            theme: 'olbiuseditor',
	    	            tools: 'datetime | clear | backcolor | font | bold italic underline',
	    	            height: 200,
	    	        });	
	            	$('#commentsNewPartyGroup').val("");
	            }
			});
			
			$("#newPartyGroupWindow").on('close', function(event){
				clearWindowSelection();
			});
		}
		
		var initDropDownList = function(){
			$("#roleTypeList").jqxDropDownList({
				source: roleTypeData, valueMember: 'roleTypeId', displayMember:'description', theme: 'olbius', width: '98%',
				
			});
			/* 'var countryData, provinceData, districtData, wardData' is defind in OrganizationUnit.ftl*/ 
			//Create prWard
			var prWardData = new Array();		
			$("#wardPartyGroupNew").jqxDropDownList({source: prWardData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
			
			var prDistrictData = new Array();
			$("#districtPartyGroupNew").jqxDropDownList({source: prDistrictData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
			$('#districtPartyGroupNew').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(value == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#wardPartyGroupNew").jqxDropDownList({source: prWardData});
			    	$("#wardPartyGroupNew").jqxDropDownList({selectedIndex: 0});
			    	if(prWardData.length < 8){
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}else{
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: false});
			    	}
		    	} 
			});
			var prProvinceData = new Array();
			$("#statePartyGroupNew").jqxDropDownList({source: prProvinceData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
			$('#statePartyGroupNew').on('change', function (event){    
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create district
			    	var prDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(value == districtData[i].geoIdFrom){
			    			prDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#districtPartyGroupNew").jqxDropDownList({source: prDistrictData});
			    	$("#districtPartyGroupNew").jqxDropDownList({selectedIndex: 0});
			    	if(prDistrictData.length < 8){
			    		$("#districtPartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#wardPartyGroupNew").jqxDropDownList({source: prWardData});
			    	$("#wardPartyGroupNew").jqxDropDownList({selectedIndex: 0});
			    	if(prWardData.length < 8){
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}else{
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: false});
			    	}
		    	} 
			});
			
			$("#countryPartyGroupNew").jqxDropDownList({source: countryData, valueMember: 'geoId', displayMember:'description', theme: 'olbius', width: '98%'});
			$('#countryPartyGroupNew').on('change', function (event){   
			    var args = event.args;
			    if (args) {
			    	var item = args.item;
			    	var value = item.value;
			    	
			    	//Create Province
			    	var prProvinceData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < provinceData.length; i++){
			    		if(value == provinceData[i].geoIdFrom){
			    			prProvinceData[index] = provinceData[i];
			    			index++;
			    		}
			    	}			    	
			    	$("#statePartyGroupNew").jqxDropDownList({source: prProvinceData});
			    	$("#statePartyGroupNew").jqxDropDownList({selectedIndex: 0}); 
			    	if(prProvinceData.length < 8){
			    		$("#statePartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}else{
			    		$("#statePartyGroupNew").jqxDropDownList({autoDropDownHeight: false});
			    	}
			    	//Create district
			    	var prDistrictData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < districtData.length; i++){
			    		if(prProvinceData[0] && prProvinceData[0].geoId == districtData[i].geoIdFrom){
			    			prDistrictData[index] = districtData[i];
			    			index++;
			    		}
			    	}
			    	$("#districtPartyGroupNew").jqxDropDownList({source: prDistrictData});
			    	$("#districtPartyGroupNew").jqxDropDownList({selectedIndex: 0});
			    	if(prDistrictData.length < 8){
			    		$("#districtPartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}else{
			    		$("#districtPartyGroupNew").jqxDropDownList({autoDropDownHeight: false});
			    	}
			    	//Create ward
			    	var prWardData = new Array();
			    	var index = 0;
			    	for(var i = 0; i < wardData.length; i++){
			    		if(prDistrictData[0] && prDistrictData[0].geoId == wardData[i].geoIdFrom){
			    			prWardData[index] = wardData[i];
			    			index++;
			    		}
			    	}
			    	$("#wardPartyGroupNew").jqxDropDownList({source: prWardData});
			    	$("#wardPartyGroupNew").jqxDropDownList({selectedIndex: 0});
			    	if(prWardData.length < 8){
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: true});
			    	}else{
			    		$("#wardPartyGroupNew").jqxDropDownList({autoDropDownHeight: false});
			    	}
		    	} 
			});
			
		};
		
		var initTreeAndDropdownBtn = function(){
			var treePartyGroupNewArr = new Array();
			<#list treePartyGroup as tree>
				var row = {};
				row["id"] = "${tree.id}_partyGroupNewId";
				row["text"] = "${tree.text}";
				row["parentId"] = "${tree.parentId}_partyGroupNewId";
				row["value"] = "${tree.idValueEntity}"
					treePartyGroupNewArr[${tree_index}] = row;
			</#list>  
			var sourceTreePartyGroupNew =
			{
			    datatype: "json",
			    datafields: [
			        { name: 'id'},
			        { name: 'parentId'},
			        { name: 'text'} ,
			        { name: 'value'}
			    ],
			    id: 'id',
			    localdata: treePartyGroupNewArr
			};
			 
			var dataAdapterPartyTreeGroupNew = new $.jqx.dataAdapter(sourceTreePartyGroupNew);
			dataAdapterPartyTreeGroupNew.dataBind();
			 
			var recordPartyGroupNewTree = dataAdapterPartyTreeGroupNew.getRecordsHierarchy('id', 'parentId', 'items', [{ name: 'text', map: 'label'}]);
			$("#belongOrgUnitButtonCreateNew").jqxDropDownButton({ width: '99%', height: 25, theme: 'olbius', autoOpen: true});
	    	$('#jqxTreeOrgUnitCreateNew').jqxTree({source: recordPartyGroupNewTree,width: '100%', height: 220, theme: 'olbius'});
	    	$('#jqxTreeOrgUnitCreateNew').on('select', function(event){
	 			var id = event.args.element.id;
	 		    var item = $('#jqxTreeOrgUnitCreateNew').jqxTree('getItem', event.args.element);
	 	    	setDropdownContent(event.args.element, $('#jqxTreeOrgUnitCreateNew'), $("#belongOrgUnitButtonCreateNew"));
	 	     });
	    	<#if expandedList?has_content>
	    	 	<#list expandedList as expandId>
	    	 		$('#jqxTreeOrgUnitCreateNew').jqxTree('expandItem', $("#${expandId}_partyGroupNewId")[0]);
	    	 	</#list>
	    	 	$('#jqxTreeOrgUnitCreateNew').jqxTree('selectItem', $("#${expandedList.get(0)}_partyGroupNewId")[0]);
	    	 </#if>
	    	 
		};
		var btnEvent = function(){
			$("#partyGroupCreateNewSave").click(function(event){
				var valid = $("#newPartyGroupForm").jqxValidator('validate');
				if(!valid){
					return false;
				}
				$(this).attr("disabled", "disabled");
				actionCreateNewPartyGroup.createPartyGroup();
			});
			$("#partyGroupCreateNewCancel").click(function(event){
				actionCreateNewPartyGroup.cancelCreatePartyGroup();
			});
			
		};
		
		var initJqxValidator = function(){
			$("#newPartyGroupForm").jqxValidator({
				rules: [
					{input: '#jqxTreeOrgUnitCreateNew', message: '${uiLabelMap.CommonRequired}', action: 'blur', 
						rule:  function (input, commit) {
							var value = $("#jqxTreeOrgUnitCreateNew").jqxTree('getSelectedItem').value;
							if(!value){
								return false;
							}
							return true;
						}
					},
					{input: '#orgUnitNameCreateNew', message: '${uiLabelMap.CommonRequired}', action: 'blur', rule: 'required'},
					{input: '#roleTypeList', message: '${uiLabelMap.CommonRequired}', action: 'blur',
						 rule: function(input, commit){
							 var value = input.val();
							 if(!value){
								 return false;
							 };
							 return true;
						 }	
					},
					{input: '#countryPartyGroupNew', message: '${uiLabelMap.CommonRequired}', action: 'blur',
						rule: function(input, commit){
							var address = $("#addressOrgUnitCreateNew").val();
							if(address && !input.val()){
								return false;
							}
							return true;
						}	
					},
					{input: '#statePartyGroupNew', message: '${uiLabelMap.CommonRequired}', action: 'blur',
						rule: function(input, commit){
							var address = $("#addressOrgUnitCreateNew").val();
							if(address && !input.val()){
								return false;
							}
							return true;
						}	
					}
					
				]
			});
		};
		var createPartyGroup = function(){
			var form = jQuery("#upLoadFileForm");
			var file = form.find('input[type=file]').eq(0);
			if(file.data('ace_input_files')){
				var fileUpload = $('#uploadedFile')[0].files[0];
				jQuery("#_uploadedFile_fileName").val(fileUpload.name);
				jQuery("#_uploadedFile_contentType").val(fileUpload.type);
				var dataSubmit = new FormData(jQuery('#upLoadFileForm')[0]);	
			}else{
				var dataSubmit = new FormData();				
			}
			dataSubmit.append("partyId", $("#orgUnitIdCreateNew").val());
			dataSubmit.append("groupName", $("#orgUnitNameCreateNew").val());
			dataSubmit.append("comments", $("#commentsNewPartyGroup").val()); 
			dataSubmit.append("address1", $("#addressOrgUnitCreateNew").val()); 
			dataSubmit.append("parentPartyGroupId", $("#jqxTreeOrgUnitCreateNew").jqxTree('getSelectedItem').value);
			dataSubmit.append("countryGeoId", $("#countryPartyGroupNew").jqxDropDownList('val'));
			dataSubmit.append("stateProvinceGeoId", $("#statePartyGroupNew").jqxDropDownList('val'));
			dataSubmit.append("districtGeoId", $("#districtPartyGroupNew").jqxDropDownList('val'));
			dataSubmit.append("wardGeoId", $("#wardPartyGroupNew").jqxDropDownList('val'));
			dataSubmit.append("partyRoleTypeId", $("#roleTypeList").jqxDropDownList('val'));
			
			$.ajax({
				url: "createOrganizationalUnit",
				data: dataSubmit,
				type: 'POST',
				cache: false,			        
		        processData: false, // Don't process the files
		        contentType: false, // Set content type to false as jQuery will tell the server its a query string request
				success: function(data){
					if(data._EVENT_MESSAGE_){
						$('#jqxNotification').jqxNotification('closeLast');
						$('#jqxNotification').jqxNotification({ template: 'info'});
	                	$("#jqxNotification").text(data._EVENT_MESSAGE_);
	                	$("#jqxNotification").jqxNotification("open");	
						$("#treePartyGroupGrid").jqxTreeGrid('updateBoundData');
					}else{
						$('#jqxNotification').jqxNotification('closeLast');
						$('#jqxNotification').jqxNotification({template: 'error'});
	                	$("#jqxNotification").text(data._ERROR_MESSAGE_);
	                	$("#jqxNotification").jqxNotification("open");						
					}
				},
				complete: function(){
					$("#partyGroupCreateNewSave").removeAttr("disabled");
				}
			});
			$("#newPartyGroupWindow").jqxWindow('close');
		};
		var cancelCreatePartyGroup = function(){
			$("#newPartyGroupWindow").jqxWindow('close');
		};
		 return {
	    	init: function(){
	    		initInputEle();
	    		initDropDownList();
	    		initTreeAndDropdownBtn();
	    		initJqxWindow();
	    		btnEvent();
	    		initJqxValidator();
	    	},
	    	createPartyGroup: createPartyGroup,
	    	cancelCreatePartyGroup: cancelCreatePartyGroup
		 };
	}());
	$(document).ready(function(){
		actionCreateNewPartyGroup.init();
		$('#uploadedFile').ace_file_input({
	  		no_file:'No File ...',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}',
			droppable:false,
			onchange:null,
			thumbnail:false,	
			width: '100px',
			whitelist:'gif|png|jpg|jpeg',
			preview_error : function(filename, error_code) {
			}
	
		}).on('change', function(){
		});
	});
	function clearWindowSelection(){
		$("#orgUnitIdCreateNew").val("");
		$("#orgUnitNameCreateNew").val("");
		$("#addressOrgUnitCreateNew").val("");
		$("#countryPartyGroupNew").jqxDropDownList('clearSelection');
		$("#statePartyGroupNew").jqxDropDownList('clearSelection');
		$("#districtPartyGroupNew").jqxDropDownList('clearSelection');
		$("#wardPartyGroupNew").jqxDropDownList('clearSelection');
		$("#roleTypeList").jqxDropDownList('clearSelection');
		<#if expandedList?has_content>
		 	$('#jqxTreeOrgUnitCreateNew').jqxTree('selectItem', $("#${expandedList.get(0)}_partyGroupNewId")[0]);
		 </#if>
		 $('#commentsNewPartyGroup').val("");
	}
</script>