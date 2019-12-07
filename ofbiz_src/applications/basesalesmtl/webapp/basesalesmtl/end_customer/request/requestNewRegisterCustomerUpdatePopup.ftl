<div id="updatePopup" class='hide'>
	<div>${uiLabelMap.BSEditCustomer}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label id="ChooseImage">${uiLabelMap.BSChooseImage}</label>
						</div>
						<div class="span7">
							<input type="file" accept="image/*" id="customerAvatar" class='file-upload'/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.DmsPartyGender}</label>
						</div>
						<div class="span7">
							<div id="we_genderId"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSCustomerName}</label>
						</div>
						<div class="span7">
							<input id="we_groupName"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSStoreName}</label>
						</div>
						<div class="span7">
							<input id="we_siteName"/>
						</div>
					</div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.BSPSProductStore}</label>
                        </div>
                        <div class="span7">
                            <div id="we_productStoreId">
                                <div id="we_productStoreGrid"></div>
                            </div>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSCustomerType}</label>
						</div>
						<div class="span7">
							<div id="we_partyTypeId"></div>
						</div>
					</div>
                    <div class='row-fluid'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.DASalesman}</label>
                        </div>
                        <div class="span7">
                            <div id="we_salesmanId">
                                <div id="we_salesmanGrid"></div>
                            </div>
                        </div>
                    </div>
                    <div class='row-fluid blue' id="salesmanSuggestContainer">
                        <div class='span5 text-algin-right'>
                            <label>${uiLabelMap.Suggest}</label>
                        </div>
                        <div class='span7 text-algin-left'>
                            <label id="salesmanSuggest"/>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label >${uiLabelMap.BSRoute}</label>
                        </div>
                        <div class="span7">
                            <div id="we_route">
                                <div id="we_routeGrid"></div>
                            </div>
                        </div>
                    </div>
				</div>
				<div class='span6'>
					<div class='row-fluid'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.CityProvince}</label>
						</div>
						<div class="span7">
							<div id="we_stateProvinceGeoId"></div>
						</div>
					</div>
                    <div class='row-fluid blue' id="cityProvinceSuggestContainer">
                        <div class='span5 text-algin-right'>
                            <label>${uiLabelMap.Suggest}</label>
                        </div>
                        <div class='span7 text-algin-left'>
                            <label id="cityProvinceSuggest"/>
                        </div>
                    </div>
					<div class='row-fluid'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.District}</label>
						</div>
						<div class="span7">
							<div id="we_districtGeoId"></div>
						</div>
					</div>
                    <div class='row-fluid blue' id="districtSuggestContainer">
                        <div class='span5 text-algin-right'>
                            <label>${uiLabelMap.Suggest}</label>
                        </div>
                        <div class='span7 text-algin-left'>
                            <label id="districtSuggest"/>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.BSWard}</label>
						</div>
						<div class="span7">
							<div id="we_wardGeoId"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSAddress}</label>
						</div>
						<div class="span7">
							<input id="we_address"/>
						</div>
					</div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.PhoneNumber}</label>
                        </div>
                        <div class="span7">
                            <input id="we_phoneNumber"/>
                        </div>
                    </div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.BSNote}</label>
						</div>
						<div class="span7">
							<textarea id="we_note" class='textarea-standard' rows="2"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCustomer" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveApproveCustomer" class='btn btn-success form-action-button pull-right hidden'><i class='fa-check'></i>${uiLabelMap.BSSaveAndApprove}</button>
			<button id="saveCustomer" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script>
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSId = "${StringUtil.wrapString(uiLabelMap.BSId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
	uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
	uiLabelMap.BSOwner = "${StringUtil.wrapString(uiLabelMap.Owner)}";
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	
	$(function(){
		RequestNewCust.init();
	});
	var RequestNewCust = (function(){
		var approveNow = null;
		var currentRow = null;
		var currentParty = {};
		var okIsApproveNow = false;
		var listGenderData = [
			{value: 'M', label: "${StringUtil.wrapString(uiLabelMap.DmsMale)}"}, 
			{value: 'F', label: "${StringUtil.wrapString(uiLabelMap.DmsFemale)}"}
		];
		var widthSize = 'calc(100% - 2px)';
		var widthSize2 = 'calc(100% - 7px)';
		
		var genderDDL;
		var stateProvinceGeoCBB;
		var districtGeoCBB;
		var wardGeoCBB;
		var productStoreDDB;
		var salesmanDDB;
		var routeDDB;
		var partyTypeDDL;
		var validatorVAL;
		
		var init = function(){
			initElement();
			initElementConplex();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.windowPopup.create($("#updatePopup"), {width: 820, height: 460, cancelButton: $("#cancelCustomer")});
			
			jOlbUtil.input.create($("#we_groupName"), {width: widthSize2});
			jOlbUtil.input.create($("#we_siteName"), {width: widthSize2});
			jOlbUtil.input.create($("#we_address"), {width: widthSize2});
			jOlbUtil.input.create($("#we_phoneNumber"), {width: widthSize2});
		};
		var initElementConplex = function(){
			var configGender = {
				width: widthSize,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				key: 'value',
				value: 'label',
				autoDropDownHeight: true
			}
			genderDDL = new OlbDropDownList($("#we_genderId"), listGenderData, configGender, []);
			
			stateProvinceGeoCBB = initComboBoxGeo('VNM', 'PROVINCE', 'we_stateProvinceGeoId');
			districtGeoCBB = initComboBoxGeo('', 'DISTRICT', 'we_districtGeoId');
			wardGeoCBB = initComboBoxGeo('', 'WARD', 'we_wardGeoId');
			
			var configProductStore = {
				useUrl: true,
				root: 'results',
				widthButton: widthSize,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'productStoreId', type: 'string'}, {name: 'storeName', type: 'string'}, {name: 'payToPartyId', type: 'string'}, {name: 'salesMethodChannelEnumId', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSProductStoreId, datafield: 'productStoreId', width: '25%'},
					{text: uiLabelMap.BSStoreName, datafield: 'storeName', width: '25%'},
					{text: uiLabelMap.BSOwner, datafield: 'payToPartyId', width: '25%'},
					{text: uiLabelMap.BSSalesChannelEnumId, datafield: 'salesMethodChannelEnumId', width: '24%'},
				],
				url: 'JQGetProductStoreForRequestNewCustomer',
				useUtilFunc: true,
				
				key: 'productStoreId',
				//keyCode: 'productStoreId',
				description: ['storeName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			productStoreDDB = new OlbDropDownButton($("#we_productStoreId"), $("#we_productStoreGrid"), null, configProductStore, []);
			
			var configPartyType = {
				width: widthSize,
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: '',
				key: 'partyTypeId',
				value: 'description',
				autoDropDownHeight: true
			}
			partyTypeDDL = new OlbDropDownList($("#we_partyTypeId"), null, configPartyType, []);
			
			var configSalesman = {
				useUrl: true,
				root: 'results',
				widthButton: widthSize,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'partyId', type: 'string'}, {name: 'partyCode', type: 'string'}, {name: 'fullName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSId, datafield: 'partyCode', width: '30%'},
					{text: uiLabelMap.BSFullName, datafield: 'fullName', width: '69%'},
				],
				url: '', //'JQGetListSalesmanByDistributor&distributorCode=' + currentParty.productStoreId,
				useUtilFunc: true,
				
				key: 'partyId',
				keyCode: 'partyCode',
				description: ['fullName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			salesmanDDB = new OlbDropDownButton($("#we_salesmanId"), $("#we_salesmanGrid"), null, configSalesman, []);
			
			var configRoute = {
				useUrl: true,
				root: 'results',
				widthButton: widthSize,
				showdefaultloadelement: false,
				autoshowloadelement: false,
				datafields: [{name: 'routeId', type: 'string'}, {name: 'routeCode', type: 'string'}, {name: 'routeName', type: 'string'}],
				columns: [
					{text: uiLabelMap.BSId, datafield: 'routeCode', width: '30%'},
					{text: uiLabelMap.BSFullName, datafield: 'routeName', width: '69%'},
				],
				url: '', //'JQGetListRouteBySalesman&salesmanId=' + currentParty.salesmanId,
				useUtilFunc: true,
				
				key: 'routeId',
				keyCode: 'routeCode',
				description: ['routeName'],
				autoCloseDropDown: true,
				filterable: true,
				sortable: true,
			};
			routeDDB = new OlbDropDownButton($("#we_route"), $("#we_routeGrid"), null, configRoute, []);
			
		};
		
		var initEvent = function(){
			productStoreDDB.getGrid().rowSelectListener(function(rowData){
				partyTypeDDL.updateSource("jqxGeneralServicer?sname=JQGetPartyTypeByChannel&salesMethodChannelEnumId=" + rowData.salesMethodChannelEnumId + "&pagesize=0");
				salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetSalesmanByProductStore&productStoreId=" + rowData.productStoreId);
				routeDDB.clearAll();
            });
            salesmanDDB.getGrid().rowSelectListener(function(rowData){
				routeDDB.updateSource("jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + rowData.partyId);
            });
			stateProvinceGeoCBB.selectListener(function(itemData, index){
				if(itemData){
					updateComboBoxGeo(itemData.value, 'DISTRICT', districtGeoCBB, currentParty.districtGeoId);
				}
			});
			districtGeoCBB.selectListener(function(itemData, index){
				if(itemData){
					updateComboBoxGeo(itemData.value, 'WARD', wardGeoCBB, currentParty.wardGeoId);
				}
			});
            $('#saveApproveCustomer').click(function(){
				updateAction(true);
			});
			$('#saveCustomer').click(function(){
                updateAction(false);
			});
			$('#ChooseImage').click(function(){
				if (currentParty.url) ContexMenuCustomer.viewImageByUrl(currentParty.url);
			});
			
			$("#updatePopup").jqxWindow("close", function(){
				resetWindowData();
			});
			
            /*
			$("#updatePopup").jqxWindow("open", function(){
				updateWindowContent();
			});
			$("#CustomerRegistration").on('bindingcomplete', function(){
				if($("#CustomerRegistration").data('approve')){
					if(!isNaN(self.approveNow)){
						self.approveCustomer([self.approveNow]);
					}
				}
			});
			self.reloadGrid = function(){
				$("#CustomerRegistration").jqxGrid('clearselection');
				$("#CustomerRegistration").jqxGrid('updatebounddata');
				$("#CustomerRegistration").removeData('approve');
			};
			*/
		};
		
		var resetWindowData = function(){
			currentRow = null;
			currentParty = null;
			okIsApproveNow = false;
			$('#image-container').html('');
			$('#ChooseImage').removeClass('has-image');
			
			genderDDL.clearAll();
			stateProvinceGeoCBB.clearAll();
			districtGeoCBB.clearAll();
			wardGeoCBB.clearAll();
			productStoreDDB.clearAll();
			salesmanDDB.clearAll();
			routeDDB.clearAll();
			partyTypeDDL.clearAll();
			
			$("#we_groupName").jqxInput("val", "");
			$("#we_siteName").jqxInput("val", "");
			$("#we_address").jqxInput("val", "");
			$("#we_phoneNumber").jqxInput("val", "");
			$('#we_note').val("");
		};
		
		var editCustomer = function(rowData, rowIndex, okIsApproveNow) {
			$("#updatePopup").jqxWindow('open');
			updateWindowContent(rowData, rowIndex, okIsApproveNow);
		};
		var initComboBoxGeo = function(geoId, geoTypeId, elementObj){
			var url = "";
			if (geoTypeId) url += "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			
			var configGeo = {
				width: widthSize,
				dropDownHeight: '150px',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: true,
				url: url,
				key: 'geoId',
				value: 'geoName',
				root: 'listGeo',
				autoDropDownHeight: false,
				datafields: [{name: "geoId"}, {name: "geoName"}],
			}
			return new OlbComboBox($("#" + elementObj), null, configGeo, []);
		};
		var updateComboBoxGeo = function(geoId, geoTypeId, comboBoxOLB, defaultValue){
			comboBoxOLB.updateSource('autoCompleteGeoAjax?geoTypeId=' + geoTypeId + "&geoId=" + geoId, null, function(){
				if (defaultValue) {
					comboBoxOLB.selectItem([defaultValue]);
				}
			});
		};
		
		var updateWindowContent = function(rowData, rowIndex, okIsApproveNow) {
			ContexMenuCustomer.loadImage(rowData.url);
			setDataWindow(rowData, rowIndex, okIsApproveNow);
		};
		var setDataWindow = function(rowData, rowIndex, okIsApproveNow) {
			currentRow = rowIndex;
			currentParty = rowData;
			okIsApproveNow = okIsApproveNow;
			if (OlbCore.isNotEmpty(rowData)) {
				if (rowData.url) {
					$('#ChooseImage').addClass('has-image');
				} else {
					$('#ChooseImage').removeClass('has-image');
				}
				
				$('#we_groupName').jqxInput('val', rowData.customerName);
				$('#we_siteName').val(rowData.officeSiteName);
				$('#we_address').val(rowData.address);
				$('#we_note').val(rowData.note);
				$('#we_phoneNumber').val(rowData.phone);
				genderDDL.selectItem([rowData.gender]);
				productStoreDDB.selectItem([rowData.productStoreId]);
				
				partyTypeDDL.updateSource("jqxGeneralServicer?sname=JQGetPartyTypeByChannel&salesMethodChannelEnumId=" + rowData.salesMethodChannelEnumId + "&pagesize=0", null, function(){
					partyTypeDDL.selectItem([rowData.partyTypeId]);
				});
				salesmanDDB.updateSource("jqxGeneralServicer?sname=JQGetSalesmanByProductStore&productStoreId=" + rowData.productStoreId, null, function(){
					salesmanDDB.selectItem([rowData.salesmanId]);
				});
				routeDDB.updateSource("jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + rowData.salesmanId, null, function(){
					routeDDB.selectItem([rowData.routeId]);
				});
                
                stateProvinceGeoCBB.selectItem([rowData.stateProvinceGeoId]);
                
                // update suggest value
                if (OlbCore.isNotEmpty(rowData.salesmanName)) {
                    $('#salesmanSuggest').text(rowData.salesmanName + " (" + rowData.salesmanId + ")");
                    $('#salesmanSuggestContainer').show();
                } else {
                    $('#salesmanSuggest').text("");
                    $('#salesmanSuggestContainer').hide();
                }
                if (OlbCore.isNotEmpty(rowData.stateProvinceGeoName)) {
                    $('#cityProvinceSuggest').text(rowData.stateProvinceGeoName);
                    $('#cityProvinceSuggestContainer').show();
                } else {
                    $('#cityProvinceSuggest').text("");
                    $('#cityProvinceSuggestContainer').hide();
                }
                if (OlbCore.isNotEmpty(rowData.districtGeoName)) {
                    $('#districtSuggest').text(rowData.districtGeoName);
                    $('#districtSuggestContainer').show();
                } else {
                    $('#districtSuggest').text("");
                    $('#districtSuggestContainer').hide();
                }
                
                /*
                // update distributor by salesman;
                if (OlbCore.isEmpty(currentParty.productStoreId)) {
                    $.ajax({
                        type: 'POST',
                        url: 'getDistIdBySalesmanCode',
                        rowData: {
                            salesmanCode: currentParty.createdByUserLogin,
                        },
                        success: function(rowData){
                            if (OlbCore.isNotEmpty(rowData)) {
                                var distId = rowData.distributorId;
                                var distName = rowData.distributorName;
                                Grid.setDropDownValue($("#we_productStoreId"), distId, distName);
                            }
                        }
                    });
                }
                */
            }
		}
		var updateAction = function(callback){
			var updateTempParty = function(obj, callback){
				obj.customerId = currentParty.customerId;
				if (callback) {
					approveNow = currentRow;
					$("#CustomerRegistration").data('approve', true);
				} else {
					$("#CustomerRegistration").removeData('approve');
					approveNow = null;
				}
				$("#CustomerRegistration").jqxGrid('updaterow', currentRow, obj);
                Loading.hide('loadingMacro');
                $("#updatePopup").jqxWindow('close');
			};
			if (validatorVAL.validate()) {
                Loading.show('loadingMacro');
				var newData = getUpdateData();
                var data = $('#customerAvatar').prop('files')[0];
                if (data) {
                    Request.uploadFile(data, function(res){
                        var path = res["path"];
                        if (path) {
                            newData.url = path;
                            updateTempParty(newData, callback);
                        }
                    });
                } else {
                    updateTempParty(newData, callback);
                }
			}
		};
		var getUpdateData = function(){
			var newData = {
				url: currentParty.url,
				gender : genderDDL.getValue(),
				customerName : $('#we_groupName').val(),
				officeSiteName : $('#we_siteName').val(),
				productStoreId : productStoreDDB.getValue(),
				partyTypeId : partyTypeDDL.getValue(),
				salesmanId : salesmanDDB.getValue(),
				routeId : routeDDB.getValue(),
				
				stateProvinceGeoId : stateProvinceGeoCBB.getValue(),
				districtGeoId : districtGeoCBB.getValue(),
				wardGeoId : wardGeoCBB.getValue(),
				address : $('#we_address').val(),
				phone : $('#we_phoneNumber').val(),
                
				note: $('#we_note').val()
			};
			return newData;
		};
		
		var initValidateForm = function(){
			var extendRules = [
					/*{input: '#customerAvatar', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur, change',
						rule: function(input, commit){
							if(currentParty.url){
								return true;
							}
							var data = $('#customerAvatar').prop('files')[0];
							if(data){
								return true;
							}
							return false;
						}
					},
                    {input: '#customerAvatar', message: '${uiLabelMap.BSFileTypeError}', action: 'keyup, blur, change',
                        rule: function(input, commit){
                            var data = $('#customerAvatar').prop('files')[0];
                            if(data){
                                if((data.type=="image/png" || data.type=="image/jpeg")){
                                    return true;
                                }else{
                                    return false;
                                }
                            }
                            return true;
                        }
                    },*/
	           ];
			var mapRules = [
		            {input: '#we_groupName', type: 'validInputNotNull'},
		            {input: '#we_phoneNumber', type: 'validInputNotNull'},
		            {input: '#we_phoneNumber', type: 'validPhoneNumber'},
		            {input: '#we_address', type: 'validInputNotNull'},
		            
					{input: '#we_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
					{input: '#we_districtGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
					{input: '#we_productStoreId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					{input: '#we_partyTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
					{input: '#we_salesmanId', type: 'validObjectNotNull', objType: 'dropDownButton'},
					//{input: '#we_route', type: 'validObjectNotNull', objType: 'dropDownButton'},
	            ];
			validatorVAL = new OlbValidator($('#updatePopup'), mapRules, extendRules, {position: 'bottom', scroll: true});
		};
		
		return {
			init: init,
			editCustomer: editCustomer,
		};
	}());
</script>