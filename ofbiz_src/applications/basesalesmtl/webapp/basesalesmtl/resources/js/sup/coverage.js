var Coverage = (function() {
	var baseUrl = "https://elas.olbius.com";
	var imageUrl = "/aceadmin/assets/images/";
	var default_image = imageUrl + "default.jpg";
	var delayBoundDrawDrag = 2000;
	var warning = {
		url : imageUrl + "red_pin.png",
		width : 25,
		height : 40
	};
	var success = {
		url : imageUrl + "blue_marker.png",
		width : 25,
		height : 33
	};
	var success_notice = {
		url : imageUrl + "blue_marker_warning.png",
		width : 25,
		height : 33
	};
	var self = {};
	var dzoom = 12;
	var isDrawableBoundary = false;
	self.markers = [];
	self.mappingCustomerMarker = {};
	self.mappingCustomerLocation = {};
	self.mappingPotentialLocation = {};
	self.mappingProductLocation = {};
	self.mappingPotentialMarker = {};
	self.potentialMarkers = [];
	self.productMarkers = [];
	self.pendingRequest = {};
	self.delay = [];
	self.init = function() {
		self.gmap = Map();
		self.gmap.initMap($('#googlemap'), {
			zoom : dzoom
		});
		if (defaultCity)
			self.gmap.placeFocus(defaultCity);
		self.resize();
		setTimeout(function() {
			self.bindMapZoom();
			self.getCustomerDataZoom(dzoom);
			self.drawBoundary();
		}, 1000);
		$(window).resize(self.resize);
		$('#refreshMap').click(function() {
			$('input[name="customertype"]').prop('checked', true);
			self.getCustomerDataZoom(dzoom);
			self.gmap.map.setZoom(dzoom);
			self.drawBoundary(true);
		});
		$('input[name="customertype"]').change(self.changeCluster);
		$('input[name="boundarydrawing"]').change(function(){
			if($(this).is(':checked')){
				isDrawableBoundary = true;
			}else{
				isDrawableBoundary = false;
				self.clearBoundary();
			}
			if(self.delayBoundary){
				clearTimeout(self.delayBoundary)
			};
			self.delayBoundary = setTimeout(self.drawBoundary, 1000);
		});
		// self.getListDelayCustomer();
		$('#ordertimeconditions').change(self.getListDelayCustomer);
		$('input[name="product"]').change(function() {
			if ($('input[name="product"]').prop('checked')) {
				$('input[name="customertype"]').prop('checked', false);
				self.getProductData();
				self.removeCustomer();
				self.removePotential();
				$("#txtProduct").removeClass("hide");
			} else {
				self.removeProduct();
				$('input[name="customertype"]').prop('checked', true);
			}
		});
		
		var initProductCoverageDrDGrid = function(dropdown, grid, width){
			var datafields = [{ name: "productId", type: "string" },
			                  { name: "productCode", type: "string" },
			                  { name: "productName", type: "string" }];
			var columns = [{text: label.ProductProductId, datafield: "productCode", width: 200},
			               {text: label.ProductProductName, datafield: "productName"}];
			GridUtils.initDropDownButton({url: "JQListProductCoverage", filterable: true, showfilterrow: true, width: width ? width : 600, source: {id: "productId", pagesize: 5},
					handlekeyboardnavigation: function (event) {
		                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
		                if (key == 70 && event.ctrlKey) {
		                	$("#jqxgridProduct").jqxGrid("clearfilters");
		                	return true;
		                }
				 	}, clearOnClose: 'Y', dropdown: {dropDownHorizontalAlignment: "left"}
			}, datafields, columns, null, grid, dropdown, "productId", "productName");
		};
		initProductCoverageDrDGrid($("#txtProduct"),$("#jqxgridProduct"), 600);
		
		$("#txtProduct").on("close", function (event) {
			var partyId = Grid.getDropDownValue($("#txtProduct")).toString();
			if (partyId.trim()) {
				self.getProductData();
			}
		});
	};
	self.resize = function() {
		if (!self.gmap)
			return;
		var he = $(window).height() - $('#page-content').offset().top - 30;
		$('#googlemap').height(he);
		self.gmap.resize();
	};
	self.changeCluster = function() {
		var obj = $(this);
		var val = obj.val();
		var isChecked = obj.is(':checked');
		
		if (isChecked) {
			$('input[name="product"]').prop('checked', false);
		}
		
		var data = obj.data('value');
		var allobj = $('input[name="customertype"][value="all"]');
		var cusObj = $('input[name="customertype"][value="customer"]');
		var conObj = $('input[name="customertype"][value="contact"]');
		var cus = cusObj.is(':checked');
		var con = conObj.is(':checked');
		if (val == 'customer') {
			if (isChecked) {
				setTimeout(function() {
					self.getCustomerData();
					self.removeProduct();
				}, 100);
				if (con)
					allobj.prop('checked', true);
				$('input[name="customertype"][data-value="ROUTE"]').prop('checked', true);
			} else {
				self.removeCustomer();
				$('input[name="customertype"][data-value="ROUTE"]').prop('checked', false);
				allobj.prop('checked', false);
			}
		} else if (val == 'contact') {
			if (isChecked) {
				self.getPotentialData();
				self.removeProduct();
				if (cus)
					allobj.prop('checked', true);
			} else {
				self.removePotential();
				allobj.prop('checked', false);
			};
		} else if (val == 'all') {
			if (isChecked) {
				setTimeout(function() {
					self.getCustomerData();
				}, 100);
				self.getPotentialData();
				$('input[name="customertype"]').prop('checked', true);
			} else {
				self.removeCustomer();
				self.removePotential();
				$('input[name="customertype"]').prop('checked', false);
			}
		} else if (data == 'ROUTE') {
			var sl = self.getRouteSelected();
			self.getCustomerData();
			if (isChecked) {
				if (sl.length == routes.length) {
					cusObj.prop('checked', true);
				} else {
				}
			} else {
				allobj.prop('checked', false);
				cusObj.prop('checked', false);
			}
			if (self.checkAllSelected()) {
				allobj.prop('checked', true);
			}
		}
	};
	self.getRouteSelected = function() {
		var selected = $('input[data-value="ROUTE"]:checked');
		var arr = [];
		for (var x = 0; x < selected.length; x++) {
			arr.push($(selected[x]).val());
		}
		return arr;
	};
	self.checkAllSelected = function() {
		var selected = $('input[name="customertype"]');
		for (var x = 0; x < selected.length; x++) {
			var obj = $(selected[x]);
			if (obj.val() == 'all')
				continue;
			if (!obj.is(':checked'))
				return false;
		}
		return true;
	};
	self.getContentInfoWindow = function(input) {
		var str = "";
		if(input.agentInfo){
			var data = input.agentInfo;
			var addr = data.address1 ? data.address1 : "";
			var dis = data.districtGeoName ? data.districtGeoName : "";
			var city = data.cityGeoName ? data.cityGeoName : "";
			var fulladdr = addr + " " + " " + dis + " " + city;
			fulladdr = fulladdr.trim();
			str += "<div class='item item-thumb-left'>";
			if(data.logoImageUrl){
				str += "<img src='" + data.logoImageUrl + "'/>"
			}else{
				str += "<img src='" + default_image + "'/>"
			}
			str += "<div><a href='AgentDetail?partyId=" + data.partyId + "' target='_blank''>" + data.groupName + "</a></div>";
			if(fulladdr){
				str += "<i class='fa fa-map-marker'></i> " + fulladdr
			}
			str += "</div>";
			if(routes && data.routes){
				var obj, tp;
				for(var x in data.routes){
					obj = _.findWhere(routes, {partyId : data.routes[x]});
					tp = obj && obj.groupName ? obj.groupName : data.routes[x];
					str += "<i class='fa fa-road'></i> " + tp + "<br/>";
				}
			}
		}
		if(input.stores){
			var stores = "";
			var length = input.stores.length;
			var i = 0;
			for(var x in input.stores){
				i++;
				stores += input.stores[x].storeName;
				if(i != length){
					stores += ", ";
				}
			}
			if(stores) str += "<i class='fa fa-home'></i> " + stores + "<br/>";
		}
		if(input.salesman){
			str += "<i class='fa fa-user'></i> " + input.salesman + "<br/>";
		}
		if(input.fromDate){
			str += "<i class='fa fa-calendar'></i> " + BasicUtils.getFullDate(input.orderDate.time)  + "<br/>";
		}
		if(input.orderDate) {
			var date = BasicUtils.formatDateDMY(input.orderDate.time);
			str += "<i class='fa fa-shopping-cart'></i> " + date + "<br/>";
		}
		if(input.promotions){
			var promotions = "<hr class='margin-top10 margin-bottom10'/><i class='fa fa-tag'></i> " + label.BSSpecialPromotion + "<br/>";
			promotions += "<table class='table table-striped table-bordered promotion'>"
			var header = "<thead><tr>"
						+ "<td>" + label.BSProgramName+ "</td>"
						+ "<td>" + label.BSRule+ "</td>"
						+ "<td>" + label.BSStatus+ "</td>"
						+ "<td>" + label.BSResult+ "</td>"
						+ "</tr><thead>";
			promotions += header + "<tbody>";
			for(var x in input.promotions){
				var pp = input.promotions[x];
				promotions += "<tr>"
							+ "<td>"+pp.promoName+"</td>"
							+ "<td>"+(pp.ruleText ? pp.ruleText : "") +"</td>"
							+ "<td>"+pp.statusDescription+"</td>"
							+ "<td>"+(pp.resultEnumDescription ? pp.resultEnumDescription : "")+"</td>"
							+ "</tr>"
			}
			promotions += "</tbody></table>";
			str += promotions;
		}
		return str;
	};
	self.getCustomerQuery = function(factor, filters, tags) {
		
		/*var query = {
			"query" : {
				"constant_score" : {
					"filter" : filters
				}
			},
			"facets" : {
				"places" : {
					"geohash" : {
						"field" : "location",
						"factor" : factor
					}
				},
				"tags" : tags
			}
		};*/
		
		var query = {
				  "size": 0,
				  "query": {
					  "match_all": {}
				  },
				  "filter": {
				    "geo_bounding_box": {
				      "location": {
				        "top_left": {
				          "lat": 21.175903198621292,
				          "lon": 105.21095322925544
				        },
				        "bottom_right": {
				          "lat": 20.914667518458977,
				          "lon": 106.74903916675544
				        }
				      }
				    }
				  },
				  "facets": {
				    "places": {
				      "geohash": {
				        "field": "location",
				        "factor": factor
				      }
				    }
				  }
				};
		return query;
	};
	self.getBoundQuery = function(ne, sw) {
		var obj = {};
		if (ne && sw) {
			obj = {
				"geo_bounding_box" : {
					"location" : {
						"top_left" : {
							"lat" : ne.latitude,
							"lon" : sw.longitude
						},
						"bottom_right" : {
							"lat" : sw.latitude,
							"lon" : ne.longitude
						}
					},

				}
			};
		}
		return obj;
	};
	self.getProductData = function(factor, zoom) {
		if (!zoom)
			zoom = self.gmap.getZoom();
		if (!factor)
			factor = self.gmap.getFactory(zoom);
		var partys = org.length ? org : sups;
		var routesSelected = self.getRouteSelected();
		var filters = {};
		var bound = self.gmap.getBoundPoint();
		var boundQuery = self.getBoundQuery(bound.ne, bound.sw);
		filters = {
				"and" : [{
					"terms" : {
						"productId": new Array(Grid.getDropDownValue($("#txtProduct")).toString())
					}
				}, boundQuery]
		};
		var tags = {
			"terms" : {
				"script_field" : "_source.id + _source.location",
				"size" : 50000
			}
		};
		var query = self.getCustomerQuery(factor, filters, tags);
		var success = function(data) {
			if(data.facets && data.facets.tags && data.facets.tags.terms){
				self.mappingProductLocation = {};
				self.processMapping(self.mappingProductLocation, data.facets.tags.terms);		
			}
			var arr = self.processData('product', data, zoom);
			self.removeProduct();
			self.setProduct(arr);
			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
		}
		self.requestData('product', query, success);
	};
	
	self.getCustomerData = function(factor, zoom) {
		if (!zoom)
			zoom = self.gmap.getZoom();
		if (!factor)
			factor = self.gmap.getFactory(zoom);
		var partys = org.length ? org : sups;
		var routesSelected = self.getRouteSelected();
		var filters = {};
		var bound = self.gmap.getBoundPoint();
		var boundQuery = self.getBoundQuery(bound.ne, bound.sw);
		if (routesSelected.length != routes.length) {
			var and = [{
				"terms" : {
					"sup_party_id" : partys
				}
			}, {
				"terms" : {
					"route_party_id" : routesSelected
				}
			}, boundQuery];
			filters = {
					"and" : and
			}
		} else {
			filters = {
					"and" : [{
						"terms" : {
							"sup_party_id" : partys
						}
					}, boundQuery]
			};
		}
		if (isDistributor) {
			filters.and.push({
				"terms" : {
					"party_id" : agents
				}
			});
		}
		var tags = {
				"terms" : {
					"script_field" : "_source.party_id + _source.location",
					"size" : 50000
				}
		};
		var query = self.getCustomerQuery(factor, filters, tags);
		var success = function(data) {
			if(data.facets && data.facets.tags && data.facets.tags.terms){
				self.mappingCustomerLocation = {};
				self.processMapping(self.mappingCustomerLocation, data.facets.tags.terms);		
			}
			var arr = self.processData('customer', data, zoom);
			self.removeCustomer();
			self.setCustomer(arr);
			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
		}
		self.requestData('customer', query, success);
	};
	
	self.getPotentialData = function(factor, zoom) {
		if (!zoom)
			zoom = self.gmap.getZoom();
		if (!factor)
			factor = self.gmap.getFactory(zoom);
		var partys = sups.length ? sups : [currentSup];
		var obj = uniqAddresses;
		var bound = self.gmap.getBoundPoint();
		var boundQuery = self.getBoundQuery(bound.ne, bound.sw);
		var filters = {
			"and" : [{
				"terms" : {
					"state_province_geo_id" : obj
				}
			}, boundQuery]
		}
		var tags = {
			"terms" : {
				"script_field" : "_source.customer_id + _source.location",
				"size" : 50000
			}
		};
		var query = self.getCustomerQuery(factor, filters, tags);
		var success = function(data) {
			if(data.facets && data.facets.tags && data.facets.tags.terms){
				self.mappingPotentialLocation = {};
				self.processMapping(self.mappingPotentialLocation, data.facets.tags.terms);		
			}
			var arr = self.processData('potentialcustomer', data, zoom);
			self.removePotential();
			self.setPotential(arr);
			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
		}
		self.requestData('potentialcustomer', query, success);
	}
	/*
	 * From term pattern partyId[lon: longitudeValue, lat: latitudeValue]
	 * Split to create mapping with key is lon vs lat & value is partyid. 
	 * This purpose is to easily look up what partyId correlate with this lat long
	 */
	self.processMapping = function(map, data){
		var key = "";
		var tmp = [];
		var partyId = "";
		for(var x in data){
			tmp = data[x].term.split("[");
			partyId = tmp[0];
			key = tmp[1];
			map[key] =  partyId;
		}
	};
	self.getContactBoundary = function(force){
		if(!notSup) {
			self.getBoundaryData('getContactBoundary', function(polygon){
				if(self.contactPolygon){
					self.contactPolygon.setMap(null);
				}
				self.contactPolygon = polygon;
			}, "", '#E64413', '#DA0404', force);
		}else{
			var obj = uniqAddresses;
			for(var x in obj){
				(function(x){
					setTimeout(function(){
						self.getBoundaryData('getContactBoundary', function(polygon){
							if(!self.contactPolygon){self.contactPolygon = []}
							if(self.contactPolygon[obj[x]]){
								self.contactPolygon[obj[x]].setMap(null);
							}
							self.contactPolygon[obj[x]] = polygon;
						}, obj[x], '#E64413', '#DA0404', force);
					}, x * 100);
				})(x);
			}
		}
	};
	self.getCustomerBoundary = function(force){
		if(!notSup) {
			self.getBoundaryData('getCustomerBoundary', function(polygon){
				if(self.polygon){
					self.polygon.setMap(null);
				}
				self.polygon = polygon;
			}, "", "#8ECF5F", "#4AAA36", force);
		}else{
			var obj = uniqAddresses;
			for(var x in obj){
				(function(){
					self.getBoundaryData('getCustomerBoundary', function(polygon){
						if(!self.polygon){self.polygon = []}
						if(self.polygon[obj[x]]){
							self.polygon[obj[x]].setMap(null);
						}
						self.polygon[obj[x]] = polygon;
					}, obj[x], "#8ECF5F", "#4AAA36", force);
				})(x);
			}
		}
	};
	self.getBoundaryData = function(url, callback, stateProvinceGeoId, fillColor, strokeColor, force){
		if(isDrawableBoundary){
			var bound = self.gmap.getBoundPoint();
			var obj = {
				// neLat : BasicUtils.processNumberLocale(bound.ne.latitude),
				// neLong : BasicUtils.processNumberLocale(bound.ne.longitude),
				// swLat : BasicUtils.processNumberLocale(bound.sw.latitude),
				// swLong : BasicUtils.processNumberLocale(bound.sw.longitude),
				stateProvinceGeoId: stateProvinceGeoId,
			};
			var action = function(datadraw, callback){
				if(isDrawableBoundary && datadraw.length){
					var polygon = self.gmap.drawPolygon({paths: datadraw, zIndex : 1000, fillColor: fillColor, strokeColor: strokeColor, fillOpacity: 0.1});
					callback(polygon);
				}
			};
			var key = url + '-' + userLoginId;
			if(stateProvinceGeoId) key += '-' + stateProvinceGeoId;
			if(localStorage[key] && !force){
				try{
					var data = $.parseJSON(localStorage.getItem(key));
					action(data, callback);
				}catch(e){

				}
			}else{
				Loading.showLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
				Request.post(url, obj, function(res){
					var tmp = [];
					var data = self.reOrderBoundaryPoint(res.left, 'latitude', 'DESC');
					tmp = self.reOrderBoundaryPoint(res.left, 'latitude', 'DESC');
					data = _.union(data, tmp);
					tmp = self.reOrderBoundaryPoint(res.bottom, 'longitude', 'ASC');
					data = _.union(data, tmp);
					tmp = self.reOrderBoundaryPoint(res.right, 'latitude', 'ASC');
					data = _.union(data, tmp);
					tmp = self.reOrderBoundaryPoint(res.top, 'longitude', 'DESC');
					data = _.union(data, tmp);
					var datadraw = [];
					for(var x in data){
						datadraw.push({
							lat: data[x].latitude,
							lng: data[x].longitude
						})
					}
					localStorage.setItem(key, JSON.stringify(datadraw));
					action(datadraw, callback);
					Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
				});
			}
		}
	};
	self.reOrderBoundaryPoint = function(data, key, dir){
		var arr = [];
		var flag = false;
		for(var x in data){
			if(!arr.length){
				arr.push(data[x])
			}else{
				var obj = data[x];
				flag = false;
				for(var y in arr){
					var last = arr[y];
					if(dir == 'ASC'){
						if(obj[key] <= last[key]){
							arr.splice(y, 0, obj);
							flag = true;
							break;
						}
					}else{
						if(obj[key] >= last[key]){
							arr.splice(y, 0, obj);
							flag = true;
							break;
						}
					}

				}
				if(!flag) arr.push(obj);
			}
		}
		return arr;
	};
	self.removeCustomer = function() {
		var ar = self.markers;
		self.markers = [];
		self.gmap.removeMarkers(ar);
	};
	self.removePotential = function() {
		var ar = self.potentialMarkers;
		self.potentialMarkers = [];
		self.gmap.removeMarkers(ar);
	};
	self.removeProduct = function() {
		var ar = self.productMarkers;
		self.productMarkers = [];
		self.gmap.removeMarkers(ar);
		if (!$('input[name="product"]').prop('checked')) {
			if (!$("#txtProduct").hasClass("hide")) {
				$("#txtProduct").addClass("hide");
			}
		}
	};
	self.requestData = function(type, query, success) {
		if (self.pendingRequest[type]) {
			clearTimeout(self.pendingRequest[type]);
			self.pendingRequest[type] = 0;
		}
		self.pendingRequest[type] = setTimeout(function() {
//			Request.post(baseUrl + '/' + type + '/_search', JSON.stringify(query), success, null, null, {
//				crossDomain : true,
//				contentType : 'application/json',
//				dataType : 'json',
//			});
			$.ajax({
		        url: "elasticSearchCustomer",
		        type: "POST",
		        data: {query: JSON.stringify(query), channel: type},
				dataType: "json",
		    }).done(function(res) {
		    	if (res) {
		    		success(res.data);
				}
			});
			clearTimeout(self.pendingRequest[type]);
			self.pendingRequest[type] = 0;
		}, 500);
	};
	/*
	 * When data is loaded completely, processing is to create marker & render it to map
	 */
	self.processData = function(type, data, zoom) {
		var bound = self.gmap.getBoundPoint();
		var arr = [];
		var tmpRes = data.facets.places.clusters;
		var key = "";
		var partyId = "";
		var icon = "";
		for ( i = 0; i < tmpRes.length; i++) {
			if (self.checkBoundValid(bound.ne, bound.sw, tmpRes[i].center.lat, tmpRes[i].center.lon)) {
				var latlng = self.gmap.createLatLng({
					latitude : tmpRes[i].center.lat,
					longitude : tmpRes[i].center.lon
				});
				key = "lon:" + tmpRes[i].center.lon + ", " + "lat:" + tmpRes[i].center.lat + "]";
				if(type == 'customer'){
					/*
					 * create mapping with key is lat long vs value is partyId;
					 * purpose is when a marker is clicked. We can only get it location -> find a correlative marker 
					 */
					partyId = self.mappingCustomerLocation[key];
					if(self.checkCustomerInNotice(partyId)) icon = self.getIcon(type, zoom, tmpRes[i].total, true);  
					else icon = self.getIcon(type, zoom, tmpRes[i].total);
				} else if (type == 'product') {
					partyId = self.mappingProductLocation[key];
					icon = self.getIcon(type, zoom, tmpRes[i].total);
				} else {
					partyId = self.mappingPotentialLocation[key];
					icon = self.getIcon(type, zoom, tmpRes[i].total);
				}
				var options = {
					icon : icon,
					position : latlng,
					labelContent : tmpRes[i].total > 1 ? tmpRes[i].total.toString() : null,
					labelClass : "marker-label",
					labelStyle : 'opacity: 0.1'
				};
				var marker = self.gmap.makeMakerLabel(options);
				(function(marker, partyId){
					marker.addListener('click', function() {
						if (marker.infowindow) {
							marker.infowindow.open(self.gmap.map, marker);
						} else if (partyId) {
							self.getAgentInfo(marker, partyId);
						}
					});	
				})(marker, partyId);
				arr.push(marker);
				if(type == 'customer' && partyId){
					self.mappingCustomerMarker[partyId] = marker;
				}
			}
		}
		return arr;
	};
	self.checkBoundValid = function(ne, sw, latitude, longitude) {
		if (latitude >= sw.latitude && latitude <= ne.latitude && longitude >= sw.longitude && longitude <= ne.longitude)
			return true;
	};
	self.setCustomer = function(arr) {
		self.markers = arr;
	};
	self.setPotential = function(arr) {
		self.potentialMarkers = arr;
	};
	self.setProduct = function(arr) {
		self.productMarkers = arr;
	};
	self.getCustomerDataZoom = function(zoom) {
		var factor = self.gmap.getFactory(zoom);
		var cusObj = $('input[name="customertype"][value="customer"]');
		var conObj = $('input[name="customertype"][value="contact"]');
		var cus = cusObj.is(':checked');
		var con = conObj.is(':checked');
		var sl = self.getRouteSelected();
		if (cus || sl.length) {
			self.getCustomerData(factor, zoom);
		}
		if (con) {
			self.getPotentialData(factor, zoom);
		}
		if ($('input[name="product"]').prop('checked')) {
			self.getProductData(factor, zoom);
		}
	};
	self.getListDelayCustomer = function() {
		if (!sups.length && distributor.length) {
			var dis = distributor[0];
			var val = $('#ordertimeconditions').val();
			if (val) {
				var bd = $('body');
				Loading.showLoadingCursor(bd);
				Request.post('getListDelayCustomer', {
					days : val,
					distributorId : dis
				}, function(res) {
					Loading.hideLoadingCursor(bd);
					self.delay = res.listCustomer;
					setTimeout(function(){
						$('#orderResult').html(self.delay.length)
						$('#orderResult').parent().show();	
					}, 100)
					self.changeCustomerMarkerStyle();
				});
			}else{
				self.delay = [];
				self.changeCustomerMarkerStyle();
				$('#orderResult').html('')
				$('#orderResult').parent().hide();	
			}
		}
	};
	self.changeCustomerMarkerStyle = function() {
		var marker;
		for(var x in self.markers){
			self.markers[x].setIcon(success.url);
		}
		if(self.delay.length){
			for(var x in self.delay){
				marker = self.mappingCustomerMarker[self.delay[x]];
				if(marker){
					marker.setIcon(success_notice.url);
				}
			}	
		}
		
	};
	self.checkCustomerInNotice = function(partyId) {
		return _.contains(self.delay, partyId);
	};
	self.getAgentInfo = function(marker, partyId) {
		Loading.showLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
		Request.post('loadAgentHistoryDetail', {
			partyId : partyId
		}, function(res) {
			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
			if (res.result) {
				var content = self.getContentInfoWindow(res.result);
				var infowindow = new google.maps.InfoWindow({
					content : content
				});
				marker.infowindow = infowindow;
				infowindow.open(self.gmap.map, marker);
			}
		});
	};
	
	self.bindMapZoom = function() {
		if (self.gmap.map) {
			var action = function(){
				Loading.showLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
				var zoom = self.gmap.getZoom();
				self.getCustomerDataZoom(zoom);
				// if(isDrawableBoundary){
					// if(self.delayBoundary){
						// clearTimeout(self.delayBoundary);
					// }
					// self.delayBoundary = setTimeout(self.drawBoundary, delayBoundDrawDrag);
				// }
			};
			self.gmap.map.addListener("dragend", action);
			self.gmap.map.addListener("zoom_changed", action);
		}
	};
	self.drawBoundary = function(force){
		if(isDrawableBoundary){
			self.getContactBoundary(force);
			self.getCustomerBoundary(force);
		}
	};
	self.clearBoundary = function(){
		if(self.polygon.length){
			for(var x in self.polygon){
				self.polygon[x].setMap(null);
			}
		}else if(self.polygon  && self.polygon.setMap){self.polygon.setMap(null);}
		if (self.contactPolygon.length){
			for(var x in self.contactPolygon){
				self.contactPolygon[x].setMap(null);
			}
		}else if(self.contactPolygon && self.contactPolygon.setMap){self.contactPolygon.setMap(null);}
	};
	self.getIcon = function(type, zoom, length, delay) {
		var url = self.gmap.createIcon(imageUrl + 'store30.png', 30, 30);
		if (type == 'customer') {
			if (length <= 1) {
				if(!delay)
					url = success.url;
				else url = success_notice.url;
			} else {
				if (zoom < 12) {
					url = self.gmap.createIcon(imageUrl + 'store30.png', 50, 50);
				} else if (zoom >= 12 && zoom <= 14)
					url = self.gmap.createIcon(imageUrl + 'store30.png', 40, 40);
				else if (zoom > 14)
					url = self.gmap.createIcon(imageUrl + 'store30.png', 30, 30);
			}
		} else if (type == 'product') {
			if (length <= 1) {
				url = warning.url;
			} else {
				if (zoom < 12) {
					url = self.gmap.createIcon(imageUrl + 'pstore50.png', 50, 50);
				} else if (zoom >= 12 && zoom <= 14)
					url = self.gmap.createIcon(imageUrl + 'pstore40.png', 40, 40);
				else if (zoom > 14)
					url = self.gmap.createIcon(imageUrl + 'pstore30.png', 30, 30);
			}
		} else {
			if (length <= 1) {
				url = warning.url;
			} else {
				if (zoom < 12) {
					url = self.gmap.createIcon(imageUrl + 'target_50x50.png', 50, 50);
				} else if (zoom >= 12 && zoom <= 14)
					url = self.gmap.createIcon(imageUrl + 'target_40x40.png', 40, 40);
				else if (zoom > 14)
					url = self.gmap.createIcon(imageUrl + 'target_30x30.png', 30, 30);
			}
		}
		return url;
	};
	$(document).ready(self.init);
	return self;
})();
