<@jqOlbCoreLib hasGrid=true hasComboBox=true/>
<script type="text/javascript">
	<#assign contactMechPurposeTypeList = delegator.findByAnd("ContactMechPurposeType", null, null, false)!/>
	var contactMechPurposeData = [
	<#if contactMechPurposeTypeList?exists>
		<#list contactMechPurposeTypeList as item>
		{	contactMechPurposeTypeId: "${item.contactMechPurposeTypeId}",
			description: "${StringUtil.wrapString(item.get("description", locale))}"
		},
		</#list>
	</#if>
	];
	var postalAddress = {
		toName: "${StringUtil.wrapString(postalAddress.toName?if_exists)}",
		attnName: "${StringUtil.wrapString(postalAddress.attnName?if_exists)}",
		address1: "${StringUtil.wrapString(postalAddress.address1?if_exists)}",
		countryGeoId: "${StringUtil.wrapString(postalAddress.countryGeoId?if_exists)}",
		postalCode: "${StringUtil.wrapString(postalAddress.postalCode?if_exists)}",
		stateProvinceGeoId: "${StringUtil.wrapString(postalAddress.stateProvinceGeoId?if_exists)}",
		districtGeoId: "${StringUtil.wrapString(postalAddress.districtGeoId?if_exists)}",
		wardGeoId: "${StringUtil.wrapString(postalAddress.wardGeoId?if_exists)}",
		allowSolicitation: "${StringUtil.wrapString(postalAddress.allowSolicitation?if_exists)}",
	};
	var localDataContactMech = {
		partyId: "${parameters.partyId?if_exists}",
		contactMechId: "${contactMech.contactMechId}"
	};
	if (typeof(OlbContactMechEditOnly) == 'undefined') {
		var OlbContactMechEditOnly = (function(){
			var countryGeoCBB;
			var stateProvinceGeoCBB;
			var districtGeoCBB;
			var wardGeoCBB;
			var allowSolicitationDDL;
			var validatorCMEOVAL;
			
			var init = function(){
				initOther();
				initEventOnClick();
			};
			var initOther = function(){
				initElement();
				initComplexElement();
				initValidateForm();
				initEvent();
				initTabSecond();
				initCustomControlAdvance();
			}
			var initElement = function(){
				$("#we_contactMechId").jqxInput({height: 25, theme: theme, maxLength: 100, disabled: true});
				$("#we_toName").jqxInput({height: 25, theme: theme, maxLength: 100});
				$("#we_attnName").jqxInput({height: 25, theme: theme, maxLength: 100});
				$("#we_address1").jqxInput({height: 25, theme: theme, maxLength: 255});
				$("#we_postalCode").jqxInput({height: 25, theme: theme, maxLength: 60});
				
				$("#we_toName").jqxInput("val", postalAddress.toName);
				$("#we_attnName").jqxInput("val", postalAddress.attnName);
				$("#we_address1").jqxInput("val", postalAddress.address1);
				$("#we_postalCode").jqxInput("val", postalAddress.postalCode);
			};
			var initComplexElement = function(){
				var configCountry = {
		    		width: "99%",
		    		height: 25,
		    		key: "geoId",
		    		value: "geoName",
		    		displayDetail: true,
		    		placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
		    		useUrl: true,
		    		url: "getListCountryGeo",
		    		datafields: [
			            {name: 'geoId'},
			            {name: 'geoName'}
			        ],
			        selectedIndex: 0,
			        autoComplete: true,
					searchMode: 'containsignorecase',
			        renderer: null,
			        renderSelectedItem: null,
		    	};
			    countryGeoCBB = new OlbComboBox($("#we_countryGeoId"), null, configCountry, [postalAddress.countryGeoId]);
			    
			    var initDropDownGeoState = function(elementObj, sname, selectArr){
			    	var url = "jqxGeneralServicer?sname=" + sname;
			    	var configGeo = {
			    		width: "99%",
			    		height: 25,
			    		key: "geoId",
			    		value: "geoName",
			    		displayDetail: true,
			    		placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
			    		useUrl: true,
			    		url: url,
			    		datafields: [
				            {name: 'geoId'},
				            {name: 'geoName'}
				        ],
				        selectedIndex: 0,
				        autoComplete: true,
						searchMode: 'containsignorecase',
				        renderer: null,
				        renderSelectedItem: null,
			    	};
			    	return new OlbComboBox(elementObj, null, configGeo, selectArr);
			    };
			    var url_stateProvinceGeoId = 'JQGetAssociatedStateListGeo&pagesize=0&pagenum=1';
			    if (postalAddress.countryGeoId != null) url_stateProvinceGeoId += '&geoId=' + postalAddress.countryGeoId;
			    var url_countyGeoId = 'JQGetAssociatedStateOtherListGeo&pagesize=0';
			    if (postalAddress.stateProvinceGeoId != null) url_countyGeoId += '&geoId=' + postalAddress.stateProvinceGeoId;
			    var url_wardGeoId = 'JQGetAssociatedStateOtherListGeo&pagesize=0&includeNA=Y';
			    if (postalAddress.districtGeoId != null) url_wardGeoId += '&geoId=' + postalAddress.districtGeoId;
			    
			    stateProvinceGeoCBB = initDropDownGeoState($("#we_stateProvinceGeoId"), url_stateProvinceGeoId, [postalAddress.stateProvinceGeoId]);
			    districtGeoCBB = initDropDownGeoState($("#we_countyGeoId"), url_countyGeoId, [postalAddress.districtGeoId]);
			    wardGeoCBB = initDropDownGeoState($("#we_wardGeoId"), url_wardGeoId, [postalAddress.wardGeoId]);
			    
			    var localYN = [{id: 'Y', description: '${StringUtil.wrapString(uiLabelMap.BSYes)}'}, {id: 'N', description: '${StringUtil.wrapString(uiLabelMap.BSNo)}'}];
				var configYN = {
					width: '99%',
					placeHolder: '${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}',
					key: 'id',
					value: 'description',
					autoDropDownHeight: true,
					selectedIndex: 0,
					displayDetail: false,
					dropDownHorizontalAlignment: 'right',
				};
				allowSolicitationDDL = new OlbDropDownList($("#we_allowSolicitation"), localYN, configYN, [postalAddress.allowSolicitation]);
			};
			var initEvent = function(){
				$('#we_countryGeoId').on('change', function (event){
					getAssociatedState($('#we_stateProvinceGeoId'), event);
					var args = event.args;
				    if (args) {
					    var item = args.item;
					    if (item) {
					    	var geoId = item.value;
					    	if ("VNM" == geoId) {
					    		$("#we_stateProvinceGeoId").jqxComboBox('selectItem', <#if organizationCurrent?exists && "MN" == organizationCurrent>'VNM-HCM'<#else>'VNM-HN2'</#if>);
							}
						}
					}
				});
				$('#we_stateProvinceGeoId').on('change', function (event){
				    getAssociatedState($('#we_countyGeoId'), event);
				});
				$('#we_countyGeoId').on('change', function (event){
				    getAssociatedState($('#we_wardGeoId'), event, true);
				});
				
				var getAssociatedState = function(comboBoxObj, event, includeNA) {
					var args = event.args;
				    if (args) {
					    var item = args.item;
					    if (item) {
					    	var geoId = item.value;
					    	if (geoId) {
					    		var tmpSource = $(comboBoxObj).jqxComboBox('source');
								if(typeof(tmpSource) != 'undefined'){
									var url_new = "jqxGeneralServicer?sname=JQGetAssociatedStateOtherListGeo&geoId=" + geoId + "&pagesize=0&pagenum=1";
									if (includeNA) url_new += "&includeNA=Y";
									tmpSource._source.url = url_new;
									$(comboBoxObj).jqxComboBox('clearSelection');
									$(comboBoxObj).jqxComboBox('source', tmpSource);
									$(comboBoxObj).jqxComboBox("selectIndex", 0);
								}
					    	}
					    }
					}
				};
			};
			var initEventOnClick = function(){
				$('#we_alterSave').on('click', function(){
					if(!validatorCMEOVAL.validate()) return false;
					
					jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToCreate}", 
						function() {
			            	var m_countryGeoId = countryGeoCBB.getValue();
			            	var m_stateProvinceGeoId = stateProvinceGeoCBB.getValue();
			            	var m_countyGeoId = districtGeoCBB.getValue();
			            	var m_wardGeoId = wardGeoCBB.getValue();
			            	var data = {
								partyId : typeof($('#we_partyId').val()) != 'undefined' ? $('#we_partyId').val() : null,
								contactMechId : typeof($('#we_contactMechId').val()) != 'undefined' ? $('#we_contactMechId').val() : null,
								contactMechTypeId : typeof($('#we_contactMechTypeId').val()) != 'undefined' ? $('#we_contactMechTypeId').val() : null,
								contactMechPurposeTypeId: typeof($('#we_contactMechPurposeTypeId').val()) != 'undefined' ? $('#we_contactMechPurposeTypeId').val() : null,
								toName: typeof($('#we_toName').val()) != 'undefined' ? $('#we_toName').val() : null,
								attnName: typeof($('#we_attnName').val()) != 'undefined' ? $('#we_attnName').val() : null,
								countryGeoId: typeof(m_countryGeoId) != 'undefined' ? m_countryGeoId : null,
								stateProvinceGeoId: typeof(m_stateProvinceGeoId) != 'undefined' ? m_stateProvinceGeoId : null,
								countyGeoId: typeof(m_countyGeoId) != 'undefined' ? m_countyGeoId : null,
								wardGeoId: typeof(m_wardGeoId) != 'undefined' ? m_wardGeoId : null,
								address1: typeof($('#we_address1').val()) != 'undefined' ? $('#we_address1').val() : null,
								postalCode: typeof($('#we_postalCode').val()) != 'undefined' ? $('#we_postalCode').val() : null,
								allowSolicitation: typeof($('#we_allowSolicitation').val()) != 'undefined' ? $('#we_allowSolicitation').val() : null,
							};
							$.ajax({
	                            type: "POST",
	                            url: "updatePostalAddressShippingAjax",
	                            data: data,
	                            beforeSend: function(){
	                                $("#loader_page_common").show();
	                            }, 
	                            success: function(data){
	                            	jOlbUtil.processResultDataAjax(data, 
											function(data, errorMessage){
									        	$('#container').empty();
									        	$('#jqxNotification').jqxNotification({ template: 'error'});
									        	$("#jqxNotification").html(errorMessage);
									        	$("#jqxNotification").jqxNotification("open");
									        	
									        	return false;
											}, function(){
												$('#container').empty();
									        	$('#jqxNotification').jqxNotification({ template: 'info'});
									        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
									        	$("#jqxNotification").jqxNotification("open");
									        	
									        	if (typeof(data.contactMechId) != 'undefined') {
				                                	$('body').trigger('createContactmechComplete', [data.contactMechId]);
				                                }
									        	return true;
											}
										);
	                            },
	                            error: function(){
	                                alert("Send to server is false!");
	                            },
	                            complete: function(){
	                            	$("#loader_page_common").hide();
	                            }
	                        });
			            }, 
			            "${StringUtil.wrapString(uiLabelMap.wgcancel)}", "${StringUtil.wrapString(uiLabelMap.wgok)}"
					);
					
				});
				
			};
			var initValidateForm = function(){
				var extendRules = [
					<#-- 	{input: '#we_postalCode', message: '${uiLabelMap.BSRequired}', action: 'blur', 
								rule: function(input, commit){
									return OlbValidatorUtil.validElement(input, commit, 'validInputNotNull');
								}
							},
			        		{input: '#we_postalCode', message: '${uiLabelMap.BSThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
			        			function (input, commit) {
			        				return OlbValidatorUtil.validElement(input, commit, 'validCannotSpecialCharactor');
								}
							}, -->
				];
				var mapRules = [
					{input: "#we_countryGeoId", type: "validObjectNotNull", objType: "comboBox"},
					{input: "#we_stateProvinceGeoId", type: "validObjectNotNull", objType: "comboBox"},
					{input: "#we_countyGeoId", type: "validObjectNotNull", objType: "comboBox"},
					{input: "#we_address1", type: "validInputNotNull"},
				];
				validatorCMEOVAL = new OlbValidator($('#alterpopupWindowContactMechNew'), mapRules, extendRules, {position: "bottom"});
			};
			var initTabSecond = function(){
				$("#jqxNotificationjqxPartyContactMechPurpose").jqxNotification({ 
		        	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
		        	appendContainer: "#" + $("#containerjqxPartyContactMechPurpose").attr("id"), 
		        	opacity: 1, autoClose: true, template: "success" 
		        });
				
				var datafields = [
					{ name: 'partyId', type: 'string'},
					{ name: 'contactMechId', type: 'string'},
					{ name: 'contactMechPurposeTypeId', type: 'string'},
					{ name: 'fromDate', type: 'date', other: 'Timestamp'},
					{ name: 'thruDate', type: 'date', other: 'Timestamp'},
				];
				var columns = [
					{ text: '${uiLabelMap.BSContactMechPurposeTypeId}', dataField: 'contactMechPurposeTypeId',
						cellsrenderer: function(row, column, value){
							if (contactMechPurposeData.length > 0) {
								for(var i = 0 ; i < contactMechPurposeData.length; i++){
	    							if (value == contactMechPurposeData[i].contactMechPurposeTypeId){
	    								return '<span title =\"' + contactMechPurposeData[i].description +'\">' + contactMechPurposeData[i].description + '</span>';
	    							}
	    						}
							}
							return '<span title=' + value +'>' + value + '</span>';
					 	}, 
					},
					{ text: '${uiLabelMap.BSFromDate}', dataField: 'fromDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '20%'},
					{ text: '${uiLabelMap.BSThruDate}', dataField: 'thruDate', cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '20%'},
				];
				var urlPurpose = 'JQGetListPartyContactMechPurpose&partyId=' + localDataContactMech.partyId + '&contactMechId=' + localDataContactMech.contactMechId;
				var configPartyContactMechPurpose = {
					showdefaultloadelement: false,
					autoshowloadelement: false,
					dropDownHorizontalAlignment: 'right',
					datafields: datafields,
					columns: columns,
					useUrl: true,
					root: 'results',
					url: urlPurpose,
					useUtilFunc: true,
					clearfilteringbutton: false,
					editable: false,
					alternativeAddPopup: 'alterpopupWindow',
					pagesize: 15,
					showtoolbar: true,
					editmode: 'click',
					width: '100%',
					bindresize: false,
					rendertoolbar: function(toolbar){
						<#assign customControlAdvance = "<div id='jqxContactMechPurposeTypeAdd' style='display:inline-block'></div>"/>
						<#assign customControlAdvance = customControlAdvance + "<a href='javascript: void(0);' onclick='OlbContactMechEditOnly.addNewRow()' style='display:inline-block; vertical-align: top; margin-top:0' class='btn btn-primary btn-small'><span>${uiLabelMap.BSAdd}</span></a>">
						<@renderToolbar id="jqxPartyContactMechPurpose" isShowTitleProperty="false" customTitleProperties="" isCollapse="false" showlist="false" 
							customControlAdvance=customControlAdvance filterbutton="" clearfilteringbutton="false" 
							addrow="false" addType="popup" alternativeAddPopup="alterpopupWindowContactMechPurposeNew" 
							deleterow="true" deleteConditionFunction="" deleteConditionMessage="" 
							virtualmode="true" addinitvalue="" primaryColumn="ID" addmultiplerows="false" 
							updaterow="" updatemultiplerows="" excelExport="false" toPrint="false" 
							customcontrol1="" customcontrol2="" customcontrol3="" customtoolbaraction=""/>
	                },
	                removeUrl: "deletePartyContactMechPurposeCustom",
	                deleteColumns: "partyId;contactMechId;contactMechPurposeTypeId;fromDate(java.sql.Timestamp)" 
				};
				new OlbGrid($("#jqxPartyContactMechPurpose"), null, configPartyContactMechPurpose, []);
			};
			var initCustomControlAdvance = function(){
				$("#jqxPartyContactMechPurpose").on('loadCustomControlAdvance', function(){
					var configContactMechPurpose = {
						width: 220,
			    		height: 25,
			    		key: "contactMechPurposeTypeId",
			    		value: "description",
			    		displayDetail: true,
			    		placeHolder: "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}",
			    		useUrl: true,
			    		url: 'jqxGeneralServicer?sname=JQGetListContactMechPurposeType&contactMechTypeId=POSTAL_ADDRESS',
			    		datafields: [
				            {name: 'contactMechPurposeTypeId'},
				            {name: 'description'}
				        ],
				        autoComplete: true,
						searchMode: 'containsignorecase',
				        renderer: null,
				        renderSelectedItem: null,
					}
					new OlbComboBox($("#jqxContactMechPurposeTypeAdd"), null, configContactMechPurpose, []);
				});
			};
			var addNewRow = function(){
				var contactMechPurposeTypeId = $("#jqxContactMechPurposeTypeAdd").jqxComboBox('getSelectedItem').value;
				if (!OlbElementUtil.isNotEmpty(contactMechPurposeTypeId)) {
					jOlbUtil.alert.error("${uiLabelMap.BSYouNotYetChooseRow}");
					return false;
				}
				jOlbUtil.confirm.dialog("${uiLabelMap.BSAreYouSureYouWantToCreate}", 
					function(){
						var dataMap = {
							partyId : localDataContactMech.partyId,
							contactMechId : localDataContactMech.contactMechId,
							contactMechPurposeTypeId: contactMechPurposeTypeId,
						};
						$.ajax({
                            type: "POST",
                            url: "createPartyContactMechPurposeAjax",
                            data: dataMap,
                            beforeSend: function(){
                                $("#loader_page_common").show();
                            }, 
                            success: function(data){
                            	jOlbUtil.processResultDataAjax(data, 
									function(data, errorMessage){
							        	$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'error'});
							        	$("#jqxNotification").html(errorMessage);
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	return false;
									}, function(){
										$('#container').empty();
							        	$('#jqxNotification').jqxNotification({ template: 'info'});
							        	$("#jqxNotification").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotification").jqxNotification("open");
							        	
							        	$("#jqxPartyContactMechPurpose").jqxGrid("updatebounddata");
									}
								);
                            },
                            error: function(){
                                alert("Send to server is false!");
                            },
                            complete: function(){
                            	$("#loader_page_common").hide();
                            }
                        });
					}
				);
			};
			return {
				init: init,
				initOther: initOther,
				addNewRow: addNewRow
			};
		}());
		OlbContactMechEditOnly.init();
	} else {
		OlbContactMechEditOnly.initOther();
	}
</script>