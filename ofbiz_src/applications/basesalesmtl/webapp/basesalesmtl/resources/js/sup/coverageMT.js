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
		var cusObj = $('input[name="customertype"][value="mtcustomer"]');
		var conObj = $('input[name="customertype"][value="contact"]');
		var cus = cusObj.is(':checked');
		var con = conObj.is(':checked');
		if (val == 'mtcustomer') {
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
		self.getCustomerData(factor, zoom);
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
			self.markers = self.processData('mtcustomer', data, zoom);

			Loading.hideLoadingCursor($(self.gmap.element.find('.gm-style div').first()));
		}
		self.requestData('mtcustomer', query, success);
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
						case "mtcustomer":
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
					case "mtcustomer":
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
			case "mtcustomer":
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
})();
