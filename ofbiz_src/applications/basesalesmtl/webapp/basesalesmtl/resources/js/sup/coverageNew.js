var Coverage = (function () {

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


	self.init = function () {
		self.gmap = Map();
		self.gmap.initMap($('#googlemap'), {
			zoom: dzoom
		});



		google.maps.event.addListenerOnce(self.gmap.map, 'idle', function () {
			if (defaultCity) {
				self.gmap.placeFocus(defaultCity);
			}

			self.resize();

			self.bindMapZoom();
			self.getCustomerDataZoom(dzoom);
			self.drawBoundary();

			$(window).resize(self.resize);

			//process
			// $('#refreshMap').click(function () {
			// 	$('input[name="customertype"]').prop('checked', true);
			// 	self.getCustomerDataZoom(dzoom);
			// 	self.gmap.map.setZoom(dzoom);
			// 	self.drawBoundary(true);
			// });

			// $('input[name="customertype"]').change(self.changeCluster);
			// $('input[name="boundarydrawing"]').change(function () {
			// 	if ($(this).is(':checked')) {
			// 		isDrawableBoundary = true;
			// 	} else {
			// 		isDrawableBoundary = false;
			// 		self.clearBoundary();
			// 	}
			// 	if (self.delayBoundary) {
			// 		clearTimeout(self.delayBoundary)
			// 	};
			// 	self.delayBoundary = setTimeout(self.drawBoundary, 1000);
			// });
			// self.getListDelayCustomer();
			// $('#ordertimeconditions').change(self.getListDelayCustomer);
			// $('input[name="product"]').change(function () {
			// 	if ($('input[name="product"]').prop('checked')) {
			// 		$('input[name="customertype"]').prop('checked', false);
			// 		self.getProductData();
			// 		self.removeCustomer();
			// 		self.removePotential();
			// 		$("#txtProduct").removeClass("hide");
			// 	} else {
			// 		self.removeProduct();
			// 		$('input[name="customertype"]').prop('checked', true);
			// 	}
			// });
			// var initProductCoverageDrDGrid = function (dropdown, grid, width) {
			// 	var datafields = [{ name: "productId", type: "string" },
			// 	{ name: "productCode", type: "string" },
			// 	{ name: "productName", type: "string" }];
			// 	var columns = [{ text: label.ProductProductId, datafield: "productCode", width: 200 },
			// 	{ text: label.ProductProductName, datafield: "productName" }];
			// 	GridUtils.initDropDownButton({
			// 		url: "JQListProductCoverage", filterable: true, showfilterrow: true, width: width ? width : 600, source: { id: "productId", pagesize: 5 },
			// 		handlekeyboardnavigation: function (event) {
			// 			var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
			// 			if (key == 70 && event.ctrlKey) {
			// 				$("#jqxgridProduct").jqxGrid("clearfilters");
			// 				return true;
			// 			}
			// 		}, clearOnClose: 'Y', dropdown: { dropDownHorizontalAlignment: "left" }
			// 	}, datafields, columns, null, grid, dropdown, "productId", "productName");
			// };
			// initProductCoverageDrDGrid($("#txtProduct"), $("#jqxgridProduct"), 600);

			// $("#txtProduct").on("close", function (event) {
			// 	var partyId = Grid.getDropDownValue($("#txtProduct")).toString();
			// 	if (partyId.trim()) {
			// 		self.getProductData();
			// 	}
			// });
			
		})
	};
	self.resize = function () {
		if (!self.gmap)
			return;
		var he = $(window).height() - $('#page-content').offset().top - 30;
		$('#googlemap').height(he);
		self.gmap.resize();
	};
	self.changeCluster = function () {
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
				setTimeout(function () {
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
				setTimeout(function () {
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

	//Check Ok
	self.getCustomerDataZoom = function (zoom) {
		var factor = self.gmap.getFactory(zoom);
		// var cusObj = $('input[name="customertype"][value="customer"]');
		// var conObj = $('input[name="customertype"][value="contact"]');
		// var cus = cusObj.is(':checked');
		// var con = conObj.is(':checked');
		// var sl = self.getRouteSelected();

		// if (cus || sl.length) {
		// 	self.getCustomerData(factor, zoom);	
		// }
		self.getCustomerData(factor, zoom);

		// if (con) {
		// 	self.getPotentialData(factor, zoom);
		// }

		// TODO
		// if ($('input[name="product"]').prop('checked')) {
		// 	self.getProductData(factor, zoom);
		// }
	};

	//Check OK
	self.getRouteSelected = function () {
		var selected = $('input[data-value="ROUTE"]:checked');
		var arr = [];
		for (var x = 0; x < selected.length; x++) {
			arr.push($(selected[x]).val());
		}
		return arr;
	};

	//TODO
	// self.getPotentialData = function (factor, zoom) {
	// 	if (!zoom)
	// 		zoom = self.gmap.getZoom();

	// 	if (!factor)
	// 		factor = self.gmap.getFactory(zoom);

	// 	var partys = sups.length ? sups : [currentSup];

	// 	var obj = uniqAddresses;

	// 	var bound = self.gmap.getBoundPoint();
	// 	var geo_bounding_box = self.getBoundQuery(bound.ne, bound.sw);
	// 	var filtered = {
	// 		"query": {
	// 			"match_all": {}
	// 		},
	// 		"filter": {
	// 			geo_bounding_box
	// 		}
	// 	}

	// 	var query = self.getCustomerQuery(factor, filtered);
	// 	var success = function (data) {
	// 		// if (data.facets && data.facets.tags && data.facets.tags.terms) {
	// 		// 	self.mappingPotentialLocation = {};
	// 		// 	self.processMapping(self.mappingPotentialLocation, data.facets.tags.terms);
	// 		// }
	// 		var arr = self.processData('potentialcustomer', data, zoom);
	// 		self.removePotential();
	// 		self.setPotential(arr);
	// 		Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
	// 	}
	// 	self.requestData('potentialcustomer', query, success);
	// }

	self.getCustomerData = function (factor, zoom) {
		if (!zoom)
			zoom = self.gmap.getZoom();

		if (!factor)
			factor = self.gmap.getFactory(zoom);

		//TODO
		var partys = org.length ? org : sups;
		var routesSelected = self.getRouteSelected();


		var bound = self.gmap.getBoundPoint();
		var geo_bounding_box = self.getBoundQuery(bound.ne, bound.sw);

		var filtered = {
			"query": {
				"match_all": {}
			},
			"filter": {
				geo_bounding_box
			}
		}

		var query = self.getCustomerQuery(factor, filtered);

		var success = function (data) {
			self.removeCustomer();
			self.markers = self.processData('customer', data, zoom);

			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
		}
		self.requestData('customer', query, success);
	};

	self.getCustomerQuery = function (factor, filtered) {

		var query = {
			"size": "0",
			"query": {
				"filtered": filtered
			},
			"facets": {
				"places": {
					"geohash": {
						"field": "location",
						"factor": factor,
						"show_doc_id": true
					}
				}
			}
		};
		if (factor < 0.1) {
			// Remove cluster
			query = {
				"query": {
					"filtered": filtered
				}
			};
		}

		// var query = {
		// 	"size": 0,
		// 	"query": {
		// 		"filtered": {
		// 			"query": {
		// 				"match_all": {}
		// 			},
		// 			"filter": {
		// 				"geo_bounding_box": {
		// 					"location": {
		// 						"top_left": {
		// 							"lat": 21.029595,
		// 							"lon": 102.834284
		// 						},
		// 						"bottom_right": {
		// 							"lat": 21.028586,
		// 							"lon": 105.837267
		// 						}
		// 					}
		// 				}
		// 			}
		// 		}
		// 	},
		// 	"facets": {
		// 		"places": {
		// 			"geohash": {
		// 				"field": "location",
		// 				"factor": 0.5,
		// 				"show_doc_id": true
		// 			}
		// 		}
		// 	}
		// };
		return query;
	};

	self.getBoundQuery = function (ne, sw) {
		var obj = {};
		if (ne && sw) {
			obj = {
				"location": {
					"top_left": {
						"lat": ne.latitude,
						"lon": sw.longitude
					},
					"bottom_right": {
						"lat": sw.latitude,
						"lon": ne.longitude
					}
				},
			};
		}
		return obj;
	};

	self.requestData = function (type, query, success) {
		if (self.pendingRequest[type]) {
			clearTimeout(self.pendingRequest[type]);
			self.pendingRequest[type] = 0;
		}
		self.pendingRequest[type] = setTimeout(function () {
			//			Request.post(baseUrl + '/' + type + '/_search', JSON.stringify(query), success, null, null, {
			//				crossDomain : true,
			//				contentType : 'application/json',
			//				dataType : 'json',
			//			});
			$.ajax({
				url: "elasticSearchCustomer",
				type: "POST",
				data: { query: JSON.stringify(query), channel: type },
				dataType: "json",
			}).done(function (res) {
				if (res) {
					success(res.data);
				}
			});
			clearTimeout(self.pendingRequest[type]);
			self.pendingRequest[type] = 0;
		}, 500);
	};


	self.processData = function (type, data, zoom) {
		var bound = self.gmap.getBoundPoint();
		var markers = [];
		
		if (data.hits.hits.length != 0) {
			var stores = data.hits.hits;
			stores.forEach(function (store) {
				if (!!store._source.location) {
					switch (type) {
						case "customer":
							var icon = self.getIcon(type, zoom, 1);
							var source = store._source; 
							var position = self.gmap.createLatLng({
								latitude: source.location.lat,
								longitude: source.location.lon
							});

							var options = {
								icon: icon,
								position: position
							};
							var marker = self.gmap.makeMakerLabel(options);

							marker.addListener('click', function () {
								$.ajax({
									url: "get_agent_detail",
									type: "POST",
									data: {
										partyId: source.partyId
									},
									dataType: "json",
								}).done(function (res) {
									var agent = res.agentInfo;
									var html = "<i class='fa fa-tag'></i>&nbsp;" + source.partyId + "</br>"
										+"<i class='fa fa-user'></i>&nbsp;" + agent.groupName + "</br>"
										+"<i class='fa fa-map-marker'></i>&nbsp;" + agent.address1 + "</br>"
										+"<i class='fa fa-phone'></i>&nbsp;" + agent.contactNumber + "</br>";

									var infoWindow = new google.maps.InfoWindow({ content: '<div id="iw" style="width:250px!important;color:#000">' + html + '</div>' });
	
									infoWindow.open(self.gmap.map, marker);
								});
							});

							markers.push(marker)
							break;
					}
				}
			});

			return markers;

		}
		var clusters = data.facets.places.clusters;
		var key = "";
		var partyId = "";

		clusters.forEach(function (cluster) {
			if (!!cluster.center) {
				switch (type) {
					case "customer":
						var icon = self.getIcon(type, zoom, cluster.total);
						var size = icon.size;
						var position = self.gmap.createLatLng({
							latitude: cluster.center.lat,
							longitude: cluster.center.lon
						});
						var marker;

						if (cluster.total == 1) {
							var options = {
								icon: icon,
								position: position
							};
							marker = self.gmap.makeMakerLabel(options);

							marker.addListener('click', function () {
								$.ajax({
									url: "get_agent_detail",
									type: "POST",
									data: {
										partyId: cluster.doc_id
									},
									dataType: "json",
								}).done(function (res) {
									var agent = res.agentInfo;
									var html = "<i class='fa fa-tag'></i>&nbsp;" + cluster.doc_id + "</br>"
										+"<i class='fa fa-user'></i>&nbsp;" + agent.groupName + "</br>"
										+"<i class='fa fa-map-marker'></i>&nbsp;" + agent.address1 + "</br>"
										+"<i class='fa fa-phone'></i>&nbsp;" + agent.contactNumber + "</br>";
									var infoWindow = new google.maps.InfoWindow({ content: '<div id="iw" style="width:250px!important;color:#000">' + html + '</div>' });
	
									infoWindow.open(self.gmap.map, marker);
								});

								
							});

						} else {
							
							var url = icon.url;
							marker = new google.maps.Marker({
								position: position,
								map: self.gmap.map,
								icon: {
									url: url,
									size: new google.maps.Size(size.width, size.height),
									origin: new google.maps.Point(0, 0),
									anchor: new google.maps.Point(size.width / 2, size.height / 2),
									scaledSize: new google.maps.Size(size.width, size.height),
									labelOrigin: new google.maps.Point(size.width / 2, size.height / 2 + 1)
								},
								label: {
									text: cluster.total + "",
									fontWeight: 'bold',
									fontSize: '11px',
									color: "#000"
								}
							});

							//marker = self.gmap.makeMakerLabel(options);

							marker.addListener('click', function () {
								self.gmap.map.setCenter({ lat: cluster.center.lat, lng: cluster.center.lon })
								self.gmap.map.setZoom(zoom + 1);
							});
						}
						markers.push(marker)
						break;
				}
			}
		});

		return markers;
	};

	self.getIcon = function (type, zoom, length) {

		var icon;
		var sizes = [53, 56, 66, 78, 90];
		var counts = [10, 30, 70, 150, 300];

		switch (type) {
			case "customer":
				if (length == 1) {
					var iconStore = createIconStore("#4080ff", 2);
					icon = self.gmap.createIcon(iconStore, 30, 30);
				} else {
					var iconCluster = getImageCluster();
					var size = sizes[counts.findIndex(function (count, index) {
						return (index == sizes.length - 1 || length < count)
					})]

					icon = self.gmap.createIcon(iconCluster, size, size);
				}
				break;
		}

		return icon;
	};

	function getImageCluster() {
	    /*
		var min = 1;
		var max = 5;
		var index = Math.floor(Math.random() * (max - min + 1)) + min;
		return "/salesmtlresources/image/google_map/markerclusterer/m" + index + ".png";
		*/
        return "/salesmtlresources/image/google_map/markerclusterer/m1.png";
	}

	function createIconStore(color, size) {
		return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-container-bg_4x.png,icons/onion/SHARED-mymaps-container_4x.png,icons/onion/1686-shop_4x.png&highlight=ff000000,' + color.substr(1) + ',ff000000&scale=' + size;
	}

	self.removeCustomer = function () {
		var ar = self.markers;
		self.markers = [];
		self.gmap.removeMarkers(ar);
	};

	self.bindMapZoom = function () {
		if (self.gmap.map) {
			var action = function () {
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

	self.drawBoundary = function (force) {
		if (isDrawableBoundary) {
			self.getContactBoundary(force);
			self.getCustomerBoundary(force);
		}
	};

	$(document).ready(self.init);
	return self;

	// var baseUrl = "https://elas.olbius.com";
	// var imageUrl = "/aceadmin/assets/images/";
	// var default_image = imageUrl + "default.jpg";
	// var delayBoundDrawDrag = 2000;
	// var warning = {
	// 	url: imageUrl + "red_pin.png",
	// 	width: 25,
	// 	height: 40
	// };
	// var success = {
	// 	url: imageUrl + "blue_marker.png",
	// 	width: 25,
	// 	height: 33
	// };
	// var success_notice = {
	// 	url: imageUrl + "blue_marker_warning.png",
	// 	width: 25,
	// 	height: 33
	// };
	// var self = {};
	// var dzoom = 12;
	// var isDrawableBoundary = false;
	// self.markers = [];
	// self.mappingCustomerMarker = {};
	// self.mappingCustomerLocation = {};
	// self.mappingPotentialLocation = {};
	// self.mappingProductLocation = {};
	// self.mappingPotentialMarker = {};
	// self.potentialMarkers = [];
	// self.productMarkers = [];
	// self.pendingRequest = {};
	// self.delay = [];
	// self.init = function () {
	// 	self.gmap = Map();
	// 	self.gmap.initMap($('#googlemap'), {
	// 		zoom: dzoom
	// 	});
	// 	if (defaultCity)
	// 		self.gmap.placeFocus(defaultCity);
	// 	self.resize();
	// 	setTimeout(function () {
	// 		self.bindMapZoom();
	// 		self.getCustomerDataZoom(dzoom);
	// 		self.drawBoundary();
	// 	}, 1000);
	// 	$(window).resize(self.resize);
	// 	$('#refreshMap').click(function () {
	// 		$('input[name="customertype"]').prop('checked', true);
	// 		self.getCustomerDataZoom(dzoom);
	// 		self.gmap.map.setZoom(dzoom);
	// 		self.drawBoundary(true);
	// 	});
	// 	$('input[name="customertype"]').change(self.changeCluster);
	// 	$('input[name="boundarydrawing"]').change(function () {
	// 		if ($(this).is(':checked')) {
	// 			isDrawableBoundary = true;
	// 		} else {
	// 			isDrawableBoundary = false;
	// 			self.clearBoundary();
	// 		}
	// 		if (self.delayBoundary) {
	// 			clearTimeout(self.delayBoundary)
	// 		};
	// 		self.delayBoundary = setTimeout(self.drawBoundary, 1000);
	// 	});
	// 	// self.getListDelayCustomer();
	// 	$('#ordertimeconditions').change(self.getListDelayCustomer);
	// 	$('input[name="product"]').change(function () {
	// 		if ($('input[name="product"]').prop('checked')) {
	// 			$('input[name="customertype"]').prop('checked', false);
	// 			self.getProductData();
	// 			self.removeCustomer();
	// 			self.removePotential();
	// 			$("#txtProduct").removeClass("hide");
	// 		} else {
	// 			self.removeProduct();
	// 			$('input[name="customertype"]').prop('checked', true);
	// 		}
	// 	});

	// 	var initProductCoverageDrDGrid = function (dropdown, grid, width) {
	// 		var datafields = [{ name: "productId", type: "string" },
	// 		{ name: "productCode", type: "string" },
	// 		{ name: "productName", type: "string" }];
	// 		var columns = [{ text: label.ProductProductId, datafield: "productCode", width: 200 },
	// 		{ text: label.ProductProductName, datafield: "productName" }];
	// 		GridUtils.initDropDownButton({
	// 			url: "JQListProductCoverage", filterable: true, showfilterrow: true, width: width ? width : 600, source: { id: "productId", pagesize: 5 },
	// 			handlekeyboardnavigation: function (event) {
	// 				var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
	// 				if (key == 70 && event.ctrlKey) {
	// 					$("#jqxgridProduct").jqxGrid("clearfilters");
	// 					return true;
	// 				}
	// 			}, clearOnClose: 'Y', dropdown: { dropDownHorizontalAlignment: "left" }
	// 		}, datafields, columns, null, grid, dropdown, "productId", "productName");
	// 	};
	// 	initProductCoverageDrDGrid($("#txtProduct"), $("#jqxgridProduct"), 600);

	// 	$("#txtProduct").on("close", function (event) {
	// 		var partyId = Grid.getDropDownValue($("#txtProduct")).toString();
	// 		if (partyId.trim()) {
	// 			self.getProductData();
	// 		}
	// 	});
	// };
	// self.resize = function () {
	// 	if (!self.gmap)
	// 		return;
	// 	var he = $(window).height() - $('#page-content').offset().top - 30;
	// 	$('#googlemap').height(he);
	// 	self.gmap.resize();
	// };
	// self.changeCluster = function () {
	// 	var obj = $(this);
	// 	var val = obj.val();
	// 	var isChecked = obj.is(':checked');

	// 	if (isChecked) {
	// 		$('input[name="product"]').prop('checked', false);
	// 	}

	// 	var data = obj.data('value');
	// 	var allobj = $('input[name="customertype"][value="all"]');
	// 	var cusObj = $('input[name="customertype"][value="customer"]');
	// 	var conObj = $('input[name="customertype"][value="contact"]');
	// 	var cus = cusObj.is(':checked');
	// 	var con = conObj.is(':checked');
	// 	if (val == 'customer') {
	// 		if (isChecked) {
	// 			setTimeout(function () {
	// 				self.getCustomerData();
	// 				self.removeProduct();
	// 			}, 100);
	// 			if (con)
	// 				allobj.prop('checked', true);
	// 			$('input[name="customertype"][data-value="ROUTE"]').prop('checked', true);
	// 		} else {
	// 			self.removeCustomer();
	// 			$('input[name="customertype"][data-value="ROUTE"]').prop('checked', false);
	// 			allobj.prop('checked', false);
	// 		}
	// 	} else if (val == 'contact') {
	// 		if (isChecked) {
	// 			self.getPotentialData();
	// 			self.removeProduct();
	// 			if (cus)
	// 				allobj.prop('checked', true);
	// 		} else {
	// 			self.removePotential();
	// 			allobj.prop('checked', false);
	// 		};
	// 	} else if (val == 'all') {
	// 		if (isChecked) {
	// 			setTimeout(function () {
	// 				self.getCustomerData();
	// 			}, 100);
	// 			self.getPotentialData();
	// 			$('input[name="customertype"]').prop('checked', true);
	// 		} else {
	// 			self.removeCustomer();
	// 			self.removePotential();
	// 			$('input[name="customertype"]').prop('checked', false);
	// 		}
	// 	} else if (data == 'ROUTE') {
	// 		var sl = self.getRouteSelected();
	// 		self.getCustomerData();
	// 		if (isChecked) {
	// 			if (sl.length == routes.length) {
	// 				cusObj.prop('checked', true);
	// 			} else {
	// 			}
	// 		} else {
	// 			allobj.prop('checked', false);
	// 			cusObj.prop('checked', false);
	// 		}
	// 		if (self.checkAllSelected()) {
	// 			allobj.prop('checked', true);
	// 		}
	// 	}
	// };
	// 
	// self.checkAllSelected = function () {
	// 	var selected = $('input[name="customertype"]');
	// 	for (var x = 0; x < selected.length; x++) {
	// 		var obj = $(selected[x]);
	// 		if (obj.val() == 'all')
	// 			continue;
	// 		if (!obj.is(':checked'))
	// 			return false;
	// 	}
	// 	return true;
	// };
	// self.getContentInfoWindow = function (input) {
	// 	var str = "";
	// 	if (input.agentInfo) {
	// 		var data = input.agentInfo;
	// 		var addr = data.address1 ? data.address1 : "";
	// 		var dis = data.districtGeoName ? data.districtGeoName : "";
	// 		var city = data.cityGeoName ? data.cityGeoName : "";
	// 		var fulladdr = addr + " " + " " + dis + " " + city;
	// 		fulladdr = fulladdr.trim();
	// 		str += "<div class='item item-thumb-left'>";
	// 		if (data.logoImageUrl) {
	// 			str += "<img src='" + data.logoImageUrl + "'/>"
	// 		} else {
	// 			str += "<img src='" + default_image + "'/>"
	// 		}
	// 		str += "<div><a href='AgentDetail?partyId=" + data.partyId + "' target='_blank''>" + data.groupName + "</a></div>";
	// 		if (fulladdr) {
	// 			str += "<i class='fa fa-map-marker'></i> " + fulladdr
	// 		}
	// 		str += "</div>";
	// 		if (routes && data.routes) {
	// 			var obj, tp;
	// 			for (var x in data.routes) {
	// 				obj = _.findWhere(routes, { partyId: data.routes[x] });
	// 				tp = obj && obj.groupName ? obj.groupName : data.routes[x];
	// 				str += "<i class='fa fa-road'></i> " + tp + "<br/>";
	// 			}
	// 		}
	// 	}
	// 	if (input.stores) {
	// 		var stores = "";
	// 		var length = input.stores.length;
	// 		var i = 0;
	// 		for (var x in input.stores) {
	// 			i++;
	// 			stores += input.stores[x].storeName;
	// 			if (i != length) {
	// 				stores += ", ";
	// 			}
	// 		}
	// 		if (stores) str += "<i class='fa fa-home'></i> " + stores + "<br/>";
	// 	}
	// 	if (input.salesman) {
	// 		str += "<i class='fa fa-user'></i> " + input.salesman + "<br/>";
	// 	}
	// 	if (input.fromDate) {
	// 		str += "<i class='fa fa-calendar'></i> " + BasicUtils.getFullDate(input.orderDate.time) + "<br/>";
	// 	}
	// 	if (input.orderDate) {
	// 		var date = BasicUtils.formatDateDMY(input.orderDate.time);
	// 		str += "<i class='fa fa-shopping-cart'></i> " + date + "<br/>";
	// 	}
	// 	if (input.promotions) {
	// 		var promotions = "<hr class='margin-top10 margin-bottom10'/><i class='fa fa-tag'></i> " + label.BSSpecialPromotion + "<br/>";
	// 		promotions += "<table class='table table-striped table-bordered promotion'>"
	// 		var header = "<thead><tr>"
	// 			+ "<td>" + label.BSProgramName + "</td>"
	// 			+ "<td>" + label.BSRule + "</td>"
	// 			+ "<td>" + label.BSStatus + "</td>"
	// 			+ "<td>" + label.BSResult + "</td>"
	// 			+ "</tr><thead>";
	// 		promotions += header + "<tbody>";
	// 		for (var x in input.promotions) {
	// 			var pp = input.promotions[x];
	// 			promotions += "<tr>"
	// 				+ "<td>" + pp.promoName + "</td>"
	// 				+ "<td>" + (pp.ruleText ? pp.ruleText : "") + "</td>"
	// 				+ "<td>" + pp.statusDescription + "</td>"
	// 				+ "<td>" + (pp.resultEnumDescription ? pp.resultEnumDescription : "") + "</td>"
	// 				+ "</tr>"
	// 		}
	// 		promotions += "</tbody></table>";
	// 		str += promotions;
	// 	}
	// 	return str;
	// };
	// 
	// 
	// self.getProductData = function (factor, zoom) {
	// 	if (!zoom)
	// 		zoom = self.gmap.getZoom();
	// 	if (!factor)
	// 		factor = self.gmap.getFactory(zoom);
	// 	var partys = org.length ? org : sups;
	// 	var routesSelected = self.getRouteSelected();
	// 	var filters = {};
	// 	var bound = self.gmap.getBoundPoint();
	// 	var boundQuery = self.getBoundQuery(bound.ne, bound.sw);
	// 	filters = {
	// 		"and": [{
	// 			"terms": {
	// 				"productId": new Array(Grid.getDropDownValue($("#txtProduct")).toString())
	// 			}
	// 		}, boundQuery]
	// 	};
	// 	var tags = {
	// 		"terms": {
	// 			"script_field": "_source.id + _source.location",
	// 			"size": 50000
	// 		}
	// 	};
	// 	var query = self.getCustomerQuery(factor, filters, tags);
	// 	var success = function (data) {
	// 		if (data.facets && data.facets.tags && data.facets.tags.terms) {
	// 			self.mappingProductLocation = {};
	// 			self.processMapping(self.mappingProductLocation, data.facets.tags.terms);
	// 		}
	// 		var arr = self.processData('product', data, zoom);
	// 		self.removeProduct();
	// 		self.setProduct(arr);
	// 		Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
	// 	}
	// 	self.requestData('product', query, success);
	// };

	// 

	// self.getPotentialData = function (factor, zoom) {
	// 	if (!zoom)
	// 		zoom = self.gmap.getZoom();
	// 	if (!factor)
	// 		factor = self.gmap.getFactory(zoom);
	// 	var partys = sups.length ? sups : [currentSup];
	// 	var obj = uniqAddresses;
	// 	var bound = self.gmap.getBoundPoint();
	// 	var boundQuery = self.getBoundQuery(bound.ne, bound.sw);
	// 	var filters = {
	// 		"and": [{
	// 			"terms": {
	// 				"state_province_geo_id": obj
	// 			}
	// 		}, boundQuery]
	// 	}
	// 	var tags = {
	// 		"terms": {
	// 			"script_field": "_source.customer_id + _source.location",
	// 			"size": 50000
	// 		}
	// 	};
	// 	var query = self.getCustomerQuery(factor, filters, tags);
	// 	var success = function (data) {
	// 		if (data.facets && data.facets.tags && data.facets.tags.terms) {
	// 			self.mappingPotentialLocation = {};
	// 			self.processMapping(self.mappingPotentialLocation, data.facets.tags.terms);
	// 		}
	// 		var arr = self.processData('potentialcustomer', data, zoom);
	// 		self.removePotential();
	// 		self.setPotential(arr);
	// 		Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
	// 	}
	// 	self.requestData('potentialcustomer', query, success);
	// }
	// /*
	//  * From term pattern partyId[lon: longitudeValue, lat: latitudeValue]
	//  * Split to create mapping with key is lon vs lat & value is partyid. 
	//  * This purpose is to easily look up what partyId correlate with this lat long
	//  */
	// self.processMapping = function (map, data) {
	// 	var key = "";
	// 	var tmp = [];
	// 	var partyId = "";
	// 	for (var x in data) {
	// 		tmp = data[x].term.split("[");
	// 		partyId = tmp[0];
	// 		key = tmp[1];
	// 		map[key] = partyId;
	// 	}
	// };
	// self.getContactBoundary = function (force) {
	// 	if (!notSup) {
	// 		self.getBoundaryData('getContactBoundary', function (polygon) {
	// 			if (self.contactPolygon) {
	// 				self.contactPolygon.setMap(null);
	// 			}
	// 			self.contactPolygon = polygon;
	// 		}, "", '#E64413', '#DA0404', force);
	// 	} else {
	// 		var obj = uniqAddresses;
	// 		for (var x in obj) {
	// 			(function (x) {
	// 				setTimeout(function () {
	// 					self.getBoundaryData('getContactBoundary', function (polygon) {
	// 						if (!self.contactPolygon) { self.contactPolygon = [] }
	// 						if (self.contactPolygon[obj[x]]) {
	// 							self.contactPolygon[obj[x]].setMap(null);
	// 						}
	// 						self.contactPolygon[obj[x]] = polygon;
	// 					}, obj[x], '#E64413', '#DA0404', force);
	// 				}, x * 100);
	// 			})(x);
	// 		}
	// 	}
	// };
	// self.getCustomerBoundary = function (force) {
	// 	if (!notSup) {
	// 		self.getBoundaryData('getCustomerBoundary', function (polygon) {
	// 			if (self.polygon) {
	// 				self.polygon.setMap(null);
	// 			}
	// 			self.polygon = polygon;
	// 		}, "", "#8ECF5F", "#4AAA36", force);
	// 	} else {
	// 		var obj = uniqAddresses;
	// 		for (var x in obj) {
	// 			(function () {
	// 				self.getBoundaryData('getCustomerBoundary', function (polygon) {
	// 					if (!self.polygon) { self.polygon = [] }
	// 					if (self.polygon[obj[x]]) {
	// 						self.polygon[obj[x]].setMap(null);
	// 					}
	// 					self.polygon[obj[x]] = polygon;
	// 				}, obj[x], "#8ECF5F", "#4AAA36", force);
	// 			})(x);
	// 		}
	// 	}
	// };
	// self.getBoundaryData = function (url, callback, stateProvinceGeoId, fillColor, strokeColor, force) {
	// 	if (isDrawableBoundary) {
	// 		var bound = self.gmap.getBoundPoint();
	// 		var obj = {
	// 			// neLat : BasicUtils.processNumberLocale(bound.ne.latitude),
	// 			// neLong : BasicUtils.processNumberLocale(bound.ne.longitude),
	// 			// swLat : BasicUtils.processNumberLocale(bound.sw.latitude),
	// 			// swLong : BasicUtils.processNumberLocale(bound.sw.longitude),
	// 			stateProvinceGeoId: stateProvinceGeoId,
	// 		};
	// 		var action = function (datadraw, callback) {
	// 			if (isDrawableBoundary && datadraw.length) {
	// 				var polygon = self.gmap.drawPolygon({ paths: datadraw, zIndex: 1000, fillColor: fillColor, strokeColor: strokeColor, fillOpacity: 0.1 });
	// 				callback(polygon);
	// 			}
	// 		};
	// 		var key = url + '-' + userLoginId;
	// 		if (stateProvinceGeoId) key += '-' + stateProvinceGeoId;
	// 		if (localStorage[key] && !force) {
	// 			try {
	// 				var data = $.parseJSON(localStorage.getItem(key));
	// 				action(data, callback);
	// 			} catch (e) {

	// 			}
	// 		} else {
	// 			Loading.showLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
	// 			Request.post(url, obj, function (res) {
	// 				var tmp = [];
	// 				var data = self.reOrderBoundaryPoint(res.left, 'latitude', 'DESC');
	// 				tmp = self.reOrderBoundaryPoint(res.left, 'latitude', 'DESC');
	// 				data = _.union(data, tmp);
	// 				tmp = self.reOrderBoundaryPoint(res.bottom, 'longitude', 'ASC');
	// 				data = _.union(data, tmp);
	// 				tmp = self.reOrderBoundaryPoint(res.right, 'latitude', 'ASC');
	// 				data = _.union(data, tmp);
	// 				tmp = self.reOrderBoundaryPoint(res.top, 'longitude', 'DESC');
	// 				data = _.union(data, tmp);
	// 				var datadraw = [];
	// 				for (var x in data) {
	// 					datadraw.push({
	// 						lat: data[x].latitude,
	// 						lng: data[x].longitude
	// 					})
	// 				}
	// 				localStorage.setItem(key, JSON.stringify(datadraw));
	// 				action(datadraw, callback);
	// 				Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
	// 			});
	// 		}
	// 	}
	// };
	// self.reOrderBoundaryPoint = function (data, key, dir) {
	// 	var arr = [];
	// 	var flag = false;
	// 	for (var x in data) {
	// 		if (!arr.length) {
	// 			arr.push(data[x])
	// 		} else {
	// 			var obj = data[x];
	// 			flag = false;
	// 			for (var y in arr) {
	// 				var last = arr[y];
	// 				if (dir == 'ASC') {
	// 					if (obj[key] <= last[key]) {
	// 						arr.splice(y, 0, obj);
	// 						flag = true;
	// 						break;
	// 					}
	// 				} else {
	// 					if (obj[key] >= last[key]) {
	// 						arr.splice(y, 0, obj);
	// 						flag = true;
	// 						break;
	// 					}
	// 				}

	// 			}
	// 			if (!flag) arr.push(obj);
	// 		}
	// 	}
	// 	return arr;
	// };
	// 
	// self.removePotential = function () {
	// 	var ar = self.potentialMarkers;
	// 	self.potentialMarkers = [];
	// 	self.gmap.removeMarkers(ar);
	// };
	// self.removeProduct = function () {
	// 	var ar = self.productMarkers;
	// 	self.productMarkers = [];
	// 	self.gmap.removeMarkers(ar);
	// 	if (!$('input[name="product"]').prop('checked')) {
	// 		if (!$("#txtProduct").hasClass("hide")) {
	// 			$("#txtProduct").addClass("hide");
	// 		}
	// 	}
	// };
	// 
	// /*
	//  * When data is loaded completely, processing is to create marker & render it to map
	//  */
	// self.processData = function (type, data, zoom) {
	// 	var bound = self.gmap.getBoundPoint();
	// 	var arr = [];
	// 	var tmpRes = data.facets.places.clusters;
	// 	var key = "";
	// 	var partyId = "";
	// 	var icon = "";
	// 	for (i = 0; i < tmpRes.length; i++) {
	// 		if (self.checkBoundValid(bound.ne, bound.sw, tmpRes[i].center.lat, tmpRes[i].center.lon)) {
	// 			var latlng = self.gmap.createLatLng({
	// 				latitude: tmpRes[i].center.lat,
	// 				longitude: tmpRes[i].center.lon
	// 			});
	// 			key = "lon:" + tmpRes[i].center.lon + ", " + "lat:" + tmpRes[i].center.lat + "]";
	// 			if (type == 'customer') {
	// 				/*
	// 				 * create mapping with key is lat long vs value is partyId;
	// 				 * purpose is when a marker is clicked. We can only get it location -> find a correlative marker 
	// 				 */
	// 				partyId = self.mappingCustomerLocation[key];
	// 				if (self.checkCustomerInNotice(partyId)) icon = self.getIcon(type, zoom, tmpRes[i].total, true);
	// 				else icon = self.getIcon(type, zoom, tmpRes[i].total);
	// 			} else if (type == 'product') {
	// 				partyId = self.mappingProductLocation[key];
	// 				icon = self.getIcon(type, zoom, tmpRes[i].total);
	// 			} else {
	// 				partyId = self.mappingPotentialLocation[key];
	// 				icon = self.getIcon(type, zoom, tmpRes[i].total);
	// 			}
	// 			var options = {
	// 				icon: icon,
	// 				position: latlng,
	// 				labelContent: tmpRes[i].total > 1 ? tmpRes[i].total.toString() : null,
	// 				labelClass: "marker-label",
	// 				labelStyle: 'opacity: 0.1'
	// 			};
	// 			var marker = self.gmap.makeMakerLabel(options);
	// 			(function (marker, partyId) {
	// 				marker.addListener('click', function () {
	// 					if (marker.infowindow) {
	// 						marker.infowindow.open(self.gmap.map, marker);
	// 					} else if (partyId) {
	// 						self.getAgentInfo(marker, partyId);
	// 					}
	// 				});
	// 			})(marker, partyId);
	// 			arr.push(marker);
	// 			if (type == 'customer' && partyId) {
	// 				self.mappingCustomerMarker[partyId] = marker;
	// 			}
	// 		}
	// 	}
	// 	return arr;
	// };
	// 
	// self.setCustomer = function (arr) {
	// 	self.markers = arr;
	// };
	// self.setPotential = function (arr) {
	// 	self.potentialMarkers = arr;
	// };
	// self.setProduct = function (arr) {
	// 	self.productMarkers = arr;
	// };
	// 
	// self.getListDelayCustomer = function () {
	// 	if (!sups.length && distributor.length) {
	// 		var dis = distributor[0];
	// 		var val = $('#ordertimeconditions').val();
	// 		if (val) {
	// 			var bd = $('body');
	// 			Loading.showLoadingCursor(bd);
	// 			Request.post('getListDelayCustomer', {
	// 				days: val,
	// 				distributorId: dis
	// 			}, function (res) {
	// 				Loading.hideLoadingCursor(bd);
	// 				self.delay = res.listCustomer;
	// 				setTimeout(function () {
	// 					$('#orderResult').html(self.delay.length)
	// 					$('#orderResult').parent().show();
	// 				}, 100)
	// 				self.changeCustomerMarkerStyle();
	// 			});
	// 		} else {
	// 			self.delay = [];
	// 			self.changeCustomerMarkerStyle();
	// 			$('#orderResult').html('')
	// 			$('#orderResult').parent().hide();
	// 		}
	// 	}
	// };
	// self.changeCustomerMarkerStyle = function () {
	// 	var marker;
	// 	for (var x in self.markers) {
	// 		self.markers[x].setIcon(success.url);
	// 	}
	// 	if (self.delay.length) {
	// 		for (var x in self.delay) {
	// 			marker = self.mappingCustomerMarker[self.delay[x]];
	// 			if (marker) {
	// 				marker.setIcon(success_notice.url);
	// 			}
	// 		}
	// 	}

	// };
	// self.checkCustomerInNotice = function (partyId) {
	// 	return _.contains(self.delay, partyId);
	// };
	// self.getAgentInfo = function (marker, partyId) {
	// 	Loading.showLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
	// 	Request.post('loadAgentHistoryDetail', {
	// 		partyId: partyId
	// 	}, function (res) {
	// 		Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()[0]));
	// 		if (res.result) {
	// 			var content = self.getContentInfoWindow(res.result);
	// 			var infowindow = new google.maps.InfoWindow({
	// 				content: content
	// 			});
	// 			marker.infowindow = infowindow;
	// 			infowindow.open(self.gmap.map, marker);
	// 		}
	// 	});
	// };

	//
	// 
	// self.clearBoundary = function () {
	// 	if (self.polygon.length) {
	// 		for (var x in self.polygon) {
	// 			self.polygon[x].setMap(null);
	// 		}
	// 	} else if (self.polygon && self.polygon.setMap) { self.polygon.setMap(null); }
	// 	if (self.contactPolygon.length) {
	// 		for (var x in self.contactPolygon) {
	// 			self.contactPolygon[x].setMap(null);
	// 		}
	// 	} else if (self.contactPolygon && self.contactPolygon.setMap) { self.contactPolygon.setMap(null); }
	// };
	// 
	// $(document).ready(self.init);
	// return self;
})();
