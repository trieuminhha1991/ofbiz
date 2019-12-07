<div id="AddAddressForm" class='hide'>
	<div>
		${uiLabelMap.UpdateAddress}
	</div>
	<div class="form-window-container">
		<div class='row-fluid margin-bottom10'>
			<div class='span6 map-container'>
				<@loading id="MapLoadingRoute" fixed="false" zIndex="9998" top="20%" option=7 background="rgba(255, 255, 255, 1)"/>
				<div  id="Address"></div>
			</div>
			<div class="span6">
				<#include "routeListAddress.ftl"/>
				<div class='row-fluid search-address-container'>
					<input id="searchRouteAddress" type="text" placeholder="${uiLabelMap.BSEnterAddress}" class="search-input input-container"/>
					<button class='btn btn-success form-action-button button-container' id='addAddress'>
						${uiLabelMap.BSApplyAddress}
					</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script>
	var RouteAddress = (function() {
		var self = {};
		self.popup, self.grid;
		self.stores = [];
		self.suggestedStores = [];
		self.currentPartyId = "";
		self.currentRouteId = "";
		self.isLoadingAddress = false;
		self.map = Map();
		self.initForm = function() {
			self.popup = $('#AddAddressForm');
			self.searchInput = $('#searchRouteAddress');
			self.popup.jqxWindow({
				width : 1000,
				maxWidth : 1000,
				height : 545,
				resizable: false,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				cancelButton : '#cancelCreateAddress',
				theme : theme,
				initContent : function() {
					initGridRouteListAddress();
					self.grid = $("#RouteListAddress");
					self.bindingGridLoaded();
					self.initMap();
				}
			});
		};
		self.initMap = function(){
			Loading.show('MapLoadingRoute');
			setTimeout(function(){
				if(typeof(Map) != "undefined" && self.map.checkMapExist()){
					self.map.initMap($('#Address'));
					self.map.initsearchBox(self.searchInput.attr('id'));
					self.searchInput.keyup(function(e){
						var val = self.searchInput.val();
						self.successAddress(val);
					});
					Loading.hide('MapLoadingRoute');
				}
			}, 100);
		};
		self.bindingGridLoaded = function(){
			if(!self.grid.data('init')){
				self.grid.data('init', true);
				setTimeout(function(){
					self.grid.on('bindingcomplete', function(){
						var data = self.grid.jqxGrid('getboundrows');
						var addrs = [];
						for(var x in data){
							if(data[x].address1 && data[x].latitude && data[x].longitude){
								var addr = data[x].address1;
								if(data[x].wardGeo){
									addr += " " + data[x].wardGeo;
								}
								if(data[x].districtGeo){
									addr += " " + data[x].districtGeo;
								}
								if(data[x].stateProvinceGeo){
									addr += " " + data[x].stateProvinceGeo;
								}
								// if(data[x].countryGeo){
									// addr += " " + data[x].countryGeo;
								// }
								addrs.push({
									address : addr,
									latitude: data[x].latitude,
									longitude: data[x].longitude
								});
							}
							if(addrs.length){
								self.map.setDirections(addrs);
							}
						}
					});
				}, 100);
			}
		};
		self.successAddress = function(val){
			var flag = false;
			if(!val) return;
			for(var x in val){
				if(!isNaN(val[x])){
					flag = true;
					break;
				}
			}
			if(!flag){
				var vl = "1 " + val;
				self.searchInput.val(vl);
			}
		};
		self.bindEvent = function(){
			$('#addAddress').click(self.addAddress);
		};
		self.addCustomer = function(){
			self.popup.jqxWindow('close');
			var gr = $('#ListRoute');
			var row = RouteForm.currentRow != -1 ? RouteForm.currentRow : gr.jqxGrid('getSelectedRowindex');
			if(CustomerRoute && CustomerRoute.popup  && !isNaN(row)){
				CustomerRoute.open(row);
			}
		};
		self.open = function(row) {
			var data = $('#ListRoute').jqxGrid('getrowdata', row);
			if (self.popup) {
				self.currentRouteId = data.partyId;
				var x = self.popup.data('id');
				if(x && self.currentRouteId && x != self.currentRouteId){
					self.map.clearAllMarker();
					self.map.clearDirection();
				}
				self.popup.data('id', self.currentRouteId);
				self.popup.jqxWindow('open');
				Popup.appendHeader(self.popup, "(" + data.partyCode + ") " + data.groupName);
				// self.getAddress(data.partyId);
				setTimeout(function(){
					self.changeSource(data.partyId);
				}, 500);
			}
		};
		self.changeSource = function(partyId){
			var grid = $("#RouteListAddress");
			var url = "jqxGeneralServicer?sname=JQGetRouteAddresses&partyId=" + partyId;
			var tmpS = grid.jqxGrid('source');
			tmpS._source.url = url;
			grid.jqxGrid('source', tmpS);
		};
		var success = function(res) {
			if (!res._ERROR_MESSAGE_ && !res._ERROR_MESSAGE_LIST_) {
				$("#contentRouteListAddress").notify("${StringUtil.wrapString(uiLabelMap.BSUpdateSuccessful)}", {
					position : "left",
					className : "success"
				});
			} else {
				$("#contentRouteListAddress").notify("${StringUtil.wrapString(uiLabelMap.BSUpdateSuccessful)}", {
					position : "left",
					className : "error"
				});
			}
		};
		self.addAddress = function(){
			if(self.map && self.map.currentPlace  && self.currentRouteId){
				var address = self.map.processAddress(self.map.currentPlace.address_components);
				var position = self.map.currentPlace.geometry.location;
				var data = {
					customerId : self.currentRouteId,
					latitude : position.lat(),
					longitude : position.lng(),
					address : address.address,
					stateProvinceGeoId : address.stateProvinceGeoId,
					districtGeoId : address.districtGeoId,
					countryGeoId : address.countryGeoId,
					isClearOld: false,
					roleTypeId: "ROUTE"
				};
				Request.post("updateLocationCustomer", data, function(res){
					self.grid.jqxGrid('updatebounddata');
					self.searchInput.val('');
					self.map.currentPlace = null;
					success(res);
				});
			}
		};
		self.deleteAddress = function(){
			var sel = self.grid.jqxGrid('getselectedrowindex');
			var row = self.grid.jqxGrid('getrowdata', sel);
			if(row.contactMechId && self.currentRouteId){
				var data = {
					partyId: self.currentRouteId,
					contactMechId : row.contactMechId
				};
				Request.post("deleteAllPartyContactMechPurpose", data, function(res){
					self.grid.jqxGrid('updatebounddata');
					success(res);
				});
			}
		};
		self.close = function() {
			self.popup.jqxWindow('close');
		};

		$(document).ready(function() {
			self.initForm();
			self.bindEvent();
		});
		return self;
	})();
</script>
