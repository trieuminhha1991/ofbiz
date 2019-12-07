<div id="ViewImagePopup" class='hide'>
	<div>
		${uiLabelMap.BSLinkImage}
	</div>
	<div class="form-window-container">
		<@loading id="ImageLoading" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
		<div id="image-container" class='image-preview'></div>
	</div>
</div>
<div id="updatePopup" class='hide'>
	<div>
		${uiLabelMap.BSEditCustomer}
	</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid'>
				<div class='span6'>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label id="ChooseImage">${uiLabelMap.BSChooseImage}</label>
						</div>
						<div class="span7">
							<input type="file" accept="image/*" id="ava" class='file-upload'/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.DmsPartyGender}</label>
						</div>
						<div class="span7">
							<div id="Gender"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSCustomerName}</label>
						</div>
						<div class="span7">
							<input id="groupName"/>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.BSStoreName}</label>
						</div>
						<div class="span7">
							<input id="siteName"/>
						</div>
					</div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.BSPSProductStore}</label>
                        </div>
                        <div class="span7">
                            <div id="divDistributor">
                                <div id="jqxgridDistributor"></div>
                            </div>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSCustomerType}</label>
						</div>
						<div class="span7">
							<div id="partyTypeId"></div>
						</div>
					</div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.DASalesman}</label>
                        </div>
                        <div class="span7">
                            <div id="divSalesman">
                                <div id="jqxgridSalesman"></div>
                            </div>
                        </div>
                    </div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label >${uiLabelMap.BSRoute}</label>
                        </div>
                        <div class="span7">
                            <div id="divRoute">
                                <div id="jqxgridRoute"></div>
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
							<div id="CityProvince"></div>
						</div>
					</div>
                    <div class='row-fluid blue margin-bottom10' id="cityProvinceSuggestContainer">
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
							<div id="District"></div>
						</div>
					</div>
                    <div class='row-fluid blue margin-bottom10' id="districtSuggestContainer">
                        <div class='span5 text-algin-right'>
                            <label>${uiLabelMap.Suggest}</label>
                        </div>
                        <div class='span7 text-algin-left'>
                            <label id="districtSuggest"/>
                        </div>
                    </div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label class='asterisk'>${uiLabelMap.BSAddress}</label>
						</div>
						<div class="span7">
							<input id="Address"/>
						</div>
					</div>
                    <div class='row-fluid margin-bottom10'>
                        <div class='span5 text-algin-right'>
                            <label class='asterisk'>${uiLabelMap.PhoneNumber}</label>
                        </div>
                        <div class="span7">
                            <input id="PhoneNumber"/>
                        </div>
                    </div>
					<div class='row-fluid margin-bottom10'>
						<div class='span5 text-algin-right'>
							<label>${uiLabelMap.BSNote}</label>
						</div>
						<div class="span7">
							<textarea id="Note" class='textarea-standard' rows="2"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelCustomer" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="saveApproveCustomer" class='btn btn-success form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.BSSaveAndApprove}</button>
			<button id="saveCustomer" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script>
	var CustomerRegistration = (function(){
		var partyTypeIdDDL;
		
		var theme = 'olbius';
		var self = {};
		self.grid = $('#${id}');
		self.updatePopup = $('#updatePopup');
		self.popup = $('#ViewImagePopup');
		self.image = "";
		self.currentParty = null;
		self.path = "";
		self.currentRow = null;
		self.approveNow = null;
		self.initImageWindow = function() {
			self.popup.jqxWindow({
				width : 800,
				height : 600,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				theme : theme
			});
			self.popup.on('open', function(){
				self.loadImage(self.image);
			});
			self.popup.on('close', function(){
				$('#image-container').html('');
			});
		};
		self.initUpdateWindow = function() {
			self.updatePopup.jqxWindow({
				width : 820,
				height : 420,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton: '#cancelCustomer',
				initContent : function(){
					self.initUpdateElement();
					self.validateUpdateForm();
				},
				theme : theme
			});
			self.updatePopup.on('open', function(){
				self.initDataUpdate();
			});
			self.updatePopup.on('close', function(){
				Grid.clearForm(self.updatePopup);
                $("#jqxgridSalesman").jqxDropDownList('clearSelection');
                $("#jqxgridSalesman").jqxDropDownList('clear');
				self.currentRow = null;
				self.currentParty = null;
			});
			$('#saveApproveCustomer').click(function(){
				self.updateAction(true);
			});
			$('#saveCustomer').click(function(){
                self.updateAction(false);
			});
		};
		self.editCustomer = function(row){
			self.updatePopup.jqxWindow('open');
			self.currentRow = row;
			self.currentParty = self.getGridData(self.currentRow);
		};
		self.updateAction = function(callback){
			var update = function(obj, callback){
				obj.customerId = self.currentParty.customerId;
				if(callback){
					self.approveNow = self.currentRow;
					self.grid.data('approve', true);
				}else{
					self.grid.removeData('approve');
					self.approveNow = null;
				}
				self.grid.jqxGrid('updaterow', self.currentRow, obj);
                Loading.hide('loadingMacro');
                self.updatePopup.jqxWindow('close');
			};
			if(self.currentParty && self.updatePopup.jqxValidator('validate')){
                Loading.show('loadingMacro');
				var obj = self.getUpdateData();
                var data = $('#ava').prop('files')[0];
                if(data){
                    Request.uploadFile(data, function(res){
                        var path = res["path"];
                        if(path){
                            obj.url = path;
                            update(obj, callback);
                        }
                    });
                }else{
                    update(obj, callback);
                }
			}
		};
		self.getUpdateData = function(){
			var obj = {
				customerName : $('#groupName').val(),
				officeSiteName : $('#siteName').val(),
				address : $('#Address').val(),
				phone : $('#PhoneNumber').val(),
				url: self.currentParty.url,
				productStoreId : Grid.getDropDownValue($("#divDistributor")).trim(),
                salesmanId : Grid.getDropDownValue($("#divSalesman")).trim(),
                routeId : Grid.getDropDownValue($("#divRoute")).trim(),
				note: $('#Note').val()
			};
			var index = $("#CityProvince").jqxComboBox('getSelectedItem');
			var data = index ? index.value : "";
			obj.stateProvinceGeoId = data;
			index = $("#District").jqxComboBox('getSelectedItem');
			var data = index ? index.value : "";
			obj.districtGeoId = data;
			index = $("#Gender").jqxDropDownList('getSelectedItem');
			var data = index ? index.value : "";
			obj.gender = data;
			return obj;
		};
		self.initUpdateElement = function(){
			$('#ChooseImage').click(function(){
				self.viewExistImage();
			});
			self.grid.on('bindingcomplete', function(){
				if(self.grid.data('approve')){
					if(!isNaN(self.approveNow)){
						self.approveCustomer([self.approveNow]);
					}
				}
			});
			var listGender = [{value: 'M', label: "${StringUtil.wrapString(uiLabelMap.DmsMale)}"}, {value: 'F', label: "${StringUtil.wrapString(uiLabelMap.DmsFemale)}"}];
			var wi = 'calc(100% - 2px)';
			var wi2 = 'calc(100% - 7px)';
			$("#Gender").jqxDropDownList({
				theme: 'olbius', width: wi,
				height: 25, source: listGender,
				displayMember: "label",
				valueMember: "value",
				autoDropDownHeight: true
			});
			$("#groupName").jqxInput({width: wi2, height: '20px' , theme: theme});
			$("#siteName").jqxInput({width: wi2, height: '20px' , theme: theme});
			$("#Address").jqxInput({width: wi2, height: '20px' , theme: theme});
			$("#PhoneNumber").jqxInput({width: wi2, height: '20px' , theme: theme});
			initComboboxGeo('VNM', 'PROVINCE', 'CityProvince');
			$('#CityProvince').on('change', function(){
				var item = $(this).jqxComboBox('getSelectedItem');
				if(item){
					var value = item.value;
					initComboboxGeo(value, 'DISTRICT', 'District');
				}
			});
			$('#CityProvince').on('bindingComplete', function(){
				if(self.currentParty){
					$(this).jqxComboBox('val', self.currentParty.stateProvinceGeoId);
				}
			});
			initComboboxGeo('', 'DISTRICT', 'District');
			$('#District').on('bindingComplete', function(){
				if(self.currentParty){
					$(this).jqxComboBox('val', self.currentParty.districtGeoId);
				}
			});
			initDistributorDrDGrid($("#divDistributor"),$("#jqxgridDistributor"), 600);
			initSalesmanDrGrid($("#divSalesman"), $("#jqxgridSalesman"), 600, "JQGetListSalesmanByDistributor&distributorCode=" + self.currentParty.productStoreId);
			initRouteDrGrid($("#divRoute"), $("#jqxgridRoute"), 600, "JQGetListRouteBySalesman&salesmanId=" + self.currentParty.salesmanId);
            $('#jqxgridDistributor').on('rowselect', function() {

                Grid.cleanDropDownValue($("#divSalesman"));
                Grid.cleanDropDownValue($("#divRoute"));
                var distributorCode = Grid.getDropDownValue($("#divDistributor"));
                var source = "jqxGeneralServicer?sname=JQGetListSalesmanByDistributor&distributorCode=" + distributorCode;
                var tmpSource =  $("#jqxgridSalesman").jqxGrid('source');
                tmpSource._source.url = source;
                $("#jqxgridSalesman").jqxGrid('clearSelection');
                $("#jqxgridSalesman").jqxGrid('source', tmpSource);
            });
            $('#jqxgridSalesman').on('rowselect', function() {

                Grid.cleanDropDownValue($("#divRoute"));
                var salesmanId = Grid.getDropDownValue($("#divSalesman"));
                var source = "jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + salesmanId;
                var tmpSource =  $("#jqxgridRoute").jqxGrid('source');
                tmpSource._source.url = source;
                $("#jqxgridRoute").jqxGrid('clearSelection');
                $("#jqxgridRoute").jqxGrid('source', tmpSource);
            });
            
            // init dropdownlist for partyTypeId
            var sourcePartyTypeId = {
                datatype: "json",
                datafields: [
                    { name: 'partyTypeId' },
                    { name: 'description' }
                ],
                url: '',
                async: true
            };
            var dataAdapterPartyTypeId = new $.jqx.dataAdapter(sourcePartyTypeId);
            $("#partyTypeId").jqxDropDownList({
				theme: 'olbius', width: wi,
				height: 25, source: dataAdapterPartyTypeId,
				displayMember: "label",
				valueMember: "value",
				autoDropDownHeight: true
			});
        };
		self.initDataUpdate = function(){
			if(!isNaN(self.currentRow)){
				var data = self.currentParty;
				if(data.url){
					$('#ChooseImage').addClass('has-image');
				}else{
					$('#ChooseImage').removeClass('has-image');
				}
				$('#Gender').jqxDropDownList('val', data.gender);
				$('#groupName').val(data.customerName);
				$('#siteName').val(data.officeSiteName);
				$('#Address').val(data.address);
				$('#CityProvince').jqxComboBox('val', data.stateProvinceGeoId);
				$('#Note').val(data.note);
				$('#PhoneNumber').val(data.phone);
                Grid.setDropDownValue($("#divDistributor"), self.currentParty.productStoreId, self.currentParty.storeName);
                Grid.setDropDownValue($("#divSalesman"), self.currentParty.salesmanId, self.currentParty.salesmanName);
                //init jqx salesman by distributor
                var source = "jqxGeneralServicer?sname=JQGetListSalesmanByDistributor&distributorCode=" + self.currentParty.productStoreId;
                var tmpSource =  $("#jqxgridSalesman").jqxGrid('source');
                tmpSource._source.url = source;
                $("#jqxgridSalesman").jqxGrid('source', tmpSource);

                Grid.setDropDownValue($("#divRoute"), self.currentParty.routeId, self.currentParty.routeName);
                //init jqx route by Salesman
                var sourceRoute = "jqxGeneralServicer?sname=JQGetListRouteBySalesman&salesmanId=" + self.currentParty.salesmanId;
                var tmpSourceRoute =  $("#jqxgridRoute").jqxGrid('source');
                tmpSourceRoute._source.url = sourceRoute;
                $("#jqxgridRoute").jqxGrid('source', tmpSourceRoute);

                if(OlbCore.isNotEmpty(self.currentParty.stateProvinceGeoName)){
                    $('#cityProvinceSuggest').text(self.currentParty.stateProvinceGeoName);
                    $('#cityProvinceSuggestContainer').show();
                }else{
                    $('#cityProvinceSuggest').text("");
                    $('#cityProvinceSuggestContainer').hide();
                }

                if(OlbCore.isNotEmpty(self.currentParty.districtGeoName)){
                    $('#districtSuggest').text(self.currentParty.districtGeoName);
                    $('#districtSuggestContainer').show();
                }else{
                    $('#districtSuggest').text("");
                    $('#districtSuggestContainer').hide();
                }

                //update distributor by salesman;
                if (OlbCore.isEmpty(self.currentParty.productStoreId)) {
                    $.ajax({
                        type: 'POST',
                        url: 'getDistIdBySalesmanCode',
                        data: {
                            salesmanCode: self.currentParty.createdByUserLogin,
                        },
                        success: function(data){
                            if (OlbCore.isNotEmpty(data)) {
                                var distId = data.distributorId;
                                var distName = data.distributorName;
                                Grid.setDropDownValue($("#divDistributor"), distId, distName);
                            }
                        }
                    });
                }
            }
		};
		self.viewExistImage = function(){
			if(self.currentParty.url){
				self.image = self.currentParty.url;
				self.popup.jqxWindow('open');
			}
		};
		var initComboboxGeo = function(geoId, geoTypeId, element) {
			var wi = 'calc(100% - 2px)';
			var source = { datatype: "json",
					datafields: [{ name: "geoId" },
					             { name: "geoName" }]};
			if(geoId){
				source.url = "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
			}
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("#" + element).jqxComboBox({ source: dataAdapter, theme: "olbius", displayMember: "geoName", valueMember: "geoId",
				width: wi, height: 25, dropDownHeight: 150});
		};
		var initDistributorDrDGrid = function(dropdown, grid, width){
			var datafields = [{ name: "partyId", type: "string" },
								{ name: "partyCode", type: "string" },
			                  { name: "groupName", type: "string" }];
			var columns = [{text: "${uiLabelMap.DADistributorId}", datafield: "partyCode", width: 150},
			               {text: "${uiLabelMap.DADistributorName}", datafield: "groupName"}];
			Grid.initDropDownButton({
				url: "JQGetListDistributor&sD=N", autorowheight: true, filterable: true, showfilterrow: true,
				width: width ? width : 600, source: {id: "partyCode", pagesize: 5},
					handlekeyboardnavigation: function (event) {
						var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
						if (key == 70 && event.ctrlKey) {
							$("#jqxgridDistributor").jqxGrid("clearfilters");
							return true;
						}
					}, dropdown: {width: 'calc(100% - 2px)', height: 25}, clearOnClose: 'Y'
			}, datafields, columns, null, grid, dropdown, "partyCode", "groupName");
		};

        var initSalesmanDrGrid = function (dropdown, grid, width, source) {
            var datafields = [{ name: "partyId", type: "string" },
                { name: "partyCode", type: "string" },
                { name: "fullName", type: "string" }];
            var columns = [{text: "${uiLabelMap.BSSalesmanId}", datafield: "partyCode", width: 150},
                {text: "${uiLabelMap.BSSalesmanName}", datafield: "fullName"}];
            Grid.initDropDownButton({
                url: source, autorowheight: true, filterable: true, showfilterrow: true,
                width: width ? width : 600, source: {id: "partyId", pagesize: 5},
                handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    if (key == 70 && event.ctrlKey) {
                        $("#jqxgridSalesman").jqxGrid("clearfilters");
                        return true;
                    }
                }, dropdown: {width: 'calc(100% - 2px)', height: 25}, clearOnClose: 'Y'
            }, datafields, columns, null, grid, dropdown, "partyId", "fullName");
        };

        var initRouteDrGrid = function (dropdown, grid, width, source) {
            var datafields = [{ name: "routeId", type: "string" },
                { name: "routeCode", type: "string" },
                { name: "routeName", type: "string" }];
            var columns = [{text: "${uiLabelMap.BSRouteId}", datafield: "routeCode", width: 150},
                {text: "${uiLabelMap.BSRouteName}", datafield: "routeName"}];
            Grid.initDropDownButton({
                url: source, autorowheight: true, filterable: true, showfilterrow: true,
                width: width ? width : 600, source: {id: "routeId", pagesize: 5},
                handlekeyboardnavigation: function (event) {
                    var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                    if (key == 70 && event.ctrlKey) {
                        $("#jqxgridSalesman").jqxGrid("clearfilters");
                        return true;
                    }
                }, dropdown: {width: 'calc(100% - 2px)', height: 25}, clearOnClose: 'Y'
            }, datafields, columns, null, grid, dropdown, "routeId", "routeName");
        };

		self.validateUpdateForm = function(){
			self.updatePopup.jqxValidator({
				rules: [
					/*{input: '#ava', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur, change',
						rule: function(input, commit){
							if(self.currentParty.url){
								return true;
							}
							var data = $('#ava').prop('files')[0];
							if(data){
								return true;
							}
							return false;
						}
					},
                    {input: '#ava', message: '${uiLabelMap.BSFileTypeError}', action: 'keyup, blur, change',
                        rule: function(input, commit){
                            var data = $('#ava').prop('files')[0];
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
					{input: '#groupName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
					{input: '#PhoneNumber', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
					{input: '#PhoneNumber', message: '${uiLabelMap.PhoneNotValid}', action: 'keyup, blur',
						rule: function(input, commit){
							var val = input.val();
							return BasicUtils.validatePhone(val);
						}
					},
					{input: '#Address', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
					{input: '#CityProvince', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function(input, commit){
							var index = input.jqxComboBox('getSelectedIndex');
							return index != -1;
						}
					},
					{input: '#District', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur',
						rule: function(input, commit){
							var index = input.jqxComboBox('getSelectedIndex');
							return index != -1;
						}
					},
                    {
                        input: '#divDistributor',
                        message: '${uiLabelMap.FieldRequired}',
                        action: 'blur,change',
                        rule: function () {
                            var val = Grid.getDropDownValue($("#divDistributor"));
                            if (!val) return false;
                            return true;
                        }
                    },
                    /*{
                        input: '#divRoute',
                        message: '${uiLabelMap.FieldRequired}',
                        action: 'blur,change',
                        rule: function () {
                            var val = Grid.getDropDownValue($("#divRoute"));
                            if (!val) return false;
                            return true;
                        }
                    },*/
                    {
                        input: '#divSalesman',
                        message: '${uiLabelMap.FieldRequired}',
                        action: 'blur,change',
                        rule: function () {
                            var val = Grid.getDropDownValue($("#divSalesman"));
                            if (!val) return false;
                            return true;
                        }
                    }
				]
			});
		};
		self.approveCustomer = function(row){
			if(row && row.length){
				var arr = [];
				var flagApprove = true;
				for(var x in row){
					var i = row[x];
					var data = self.getGridData(i);
					arr.push(data.customerId);
					/*if(OlbCore.isEmpty(data.districtGeoId) || OlbCore.isEmpty(data.stateProvinceGeoId) || OlbCore.isEmpty(data.url)) {*/
					if(OlbCore.isEmpty(data.districtGeoId) || OlbCore.isEmpty(data.stateProvinceGeoId)) {
                        flagApprove = false;
                        bootbox.dialog("${uiLabelMap.BSUpdateDataBeforeApprove}",
                                [{
                                    "label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}",
                                    "icon": 'fa fa-remove',
                                    "class": 'btn  btn-danger form-action-button pull-right',
                                    "callback": function () {
                                        bootbox.hideAll();
                                    }
                                },
                                {
                                    "label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
                                    "icon": 'fa-check',
                                    "class": 'btn btn-primary form-action-button pull-right',
                                    "callback": function () {
                                        CustomerRegistration.editCustomer(i);
                                    }
                                }]);
					    break;
                    }
				}
				var obj = {
					customerId : JSON.stringify(arr)
				};
				if(flagApprove){
                    Request.post('approveRequestNewCustomer', obj, function(res){
                        if(res.partyId){
                            self.reloadGrid();
                            Grid.renderMessage('${id}', '${StringUtil.wrapString(uiLabelMap.HRApprovalSuccessfully)}', 'success');
                        }else{
                            Grid.renderMessage('${id}', res._ERROR_MESSAGE_, 'error');
                        }
                    });
                }
			}
		};

		self.viewImage = function(row){
			if(!isNaN(row)){
				var data = self.getGridData(row);
				self.image = data.url;
			}else{
				self.image = row;
			}
			if(self.popup && self.popup.length){
				self.popup.jqxWindow('open');
			}
		};
		self.getGridData = function(row){
			if(self.grid && self.grid.length){
				return self.grid.jqxGrid('getrowdata', row);
			}
		};
		self.reloadGrid = function(){
			self.grid.jqxGrid('clearselection');
			self.grid.jqxGrid('updatebounddata');
			self.grid.removeData('approve');
		};
		self.loadImage = function(image){
			if(image){
				var img = new Image();
				Loading.show('ImageLoading');
				img.onload = function() {
					var obj = $('#image-container');
					obj.html('');
					obj.append($(img));
					Loading.hide('ImageLoading');
				};
				img.src = image;
			}
		};
		$(document).ready(function(){
			self.initImageWindow();
			self.initUpdateWindow();
		});
		return self;
	})();
</script>