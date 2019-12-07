<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqGridMinimumLib />
<#include "../loader/loader.ftl"/>
<#include "./popup/sideBarViewRetailOutletMap.ftl"/>
<div id="jqxNotification-updateSuccess">C&#x1EAD;p nh&#x1EAD;t th&#xE0;nh c&#xF4;ng</div>
<div id="jqxNotification-updateError">C&#x1EAD;p nh&#x1EAD;t th&#x1EA5;t b&#x1EA1;i</div>
<div id="jqxNotification-NoStore">Ch&#432;a c&#243; &#273;&#417;n h&#224;ng</div>

<div id="map-reqDeliveryOrderNewOnMap" style="width:100%;"></div>
<div id="omnibox">
	<button class="icon-side-bar" id="side-bar-map" onClick="openSideBar()" title="menu">
		<span class="fa fa-bars"></span>
	</button>
	<input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
	<button class="search-box-button" onClick="reqDeliveryOrderNewOnMap.searchAdress()" title="search">
		<span class="fa fa-search"></span>
	</button>
</div>
<div id="popup-manager-contex-menu" />
<style>
	#note {
        border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0,0,0,.25)!important;
        margin-right: 5px;
		margin-top: 5px;
		padding: 10px;
		max-height: 400px;
		overflow-y: auto;
      }

	  .side-bar {
		      z-index: 10000002!important;
	  }
      
      #note h3 {
        margin-top: 0;
      }
      
      #note img, #item-selectbox img, .jqx-item img {
      	padding: 2px;
      	width: 20px;
        vertical-align: middle;
      }
      
      #view-orderchoose {
      	border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0,0,0,.25)!important;
        margin-right: 5px;
		margin-top: 5px;
		z-index: 1000001!important;
      }
      
      #table-orders-choose {
          margin: 10px 10px 0px 10px;
      }
      
      #view-orderchoose button {
      	 margin: 10px;
      }
      
      .jqx-menu-popup-olbius {
      	z-index: 999999!important;
      }
      
      .jqx-item {
      	font-size: 12px;
      }
      
      #omnibox button {
      	border: none;
		outline: none;
		background: none;
      }
      
      #omnibox {
      	margin: 5px;
      	display: flex;
      	height: 30px;
      	align-items: stretch;
      	border-bottom: 1px solid #ddd;
		background: #fff;
		border-bottom: 1px solid #ddd;
		box-shadow: 0 3px 12px rgba(27,31,35,0.15);
      }
      
      #pac-input {
		width: 250px;
   		margin: 0;
   		border: none;
		outline: none;
		background: none;
		height: 100%;
    	padding: 0px 10px;
	}
	
	.pick-date {
    	padding: 5px;
    	display: inline-flex;	
	}
	
	.name-pick-date {
		width: 200px;
		font-size: 14px;
		padding: 5px;
	}
	
	#general-information {
	    background: #fff;
	    padding: 10px;
	    z-index: 1000001!important;
	    left: 0px!important;
	    bottom: 0px;
	    margin: 3px;
	}
	
	#general-information div {
	    font-size: 13px;
	    margin-bottom: 5px;
	}
	
	#general-information div i {
		padding: 5px;
	}

</style>

<script type="text/javascript">

	var reqDeliveryOrderNewOnMap = function() {
		var self = {

		}

		var checkAddChosseMarkers = false, pick_day, rectangles = [], noteEl, contextMenu, positionMouse, hold, tableOrdersChoose, generalInformation;
		//var map, days = {}, noteEl, contextMenu, orderSelected, positionMouse, rectangles = [], chooseMarkers = [], hold, checkAddChosseMarkers, tableOrdersChoose, generalInformation;
		
	
		function getDate(date) {
			return new Intl.DateTimeFormat('en-GB').format(date)
		}
		
		function getDateTime(date) {
			var options = {
				weekday: "long", year: "numeric", month: "numeric",  
				day: "numeric", hour: "2-digit", minute: "2-digit"  
			};
			return date.toLocaleTimeString("en-GB", options)
		}
		
		
		self.initMap = function() {
			openLoader();
			var height_map = window.innerHeight - Array.from($("#main-content").children()).reduce(function(acc, curr) {return acc + curr.offsetHeight}, 0) - 50;
			$("#map-reqDeliveryOrderNewOnMap").height(height_map);
			self.height_map = height_map;
			var mapOptions = {
				label: "",
				zoom: 12,
				minZoom: 3,
				maxZoom: 16,
				center: new google.maps.LatLng(21.0056183, 105.8433475),
				mapTypeControlOptions: {
					style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
					position: google.maps.ControlPosition.TOP_CENTER
				},
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};

			self.map = new google.maps.Map(document.getElementById('map-reqDeliveryOrderNewOnMap'), mapOptions);
			
			google.maps.event.addListenerOnce(self.map, 'idle', function(){

				var drawingManager = new google.maps.drawing.DrawingManager({
					drawingControl: true,
					drawingControlOptions: {
						position: google.maps.ControlPosition.BOTTOM_CENTER,
						drawingModes: [google.maps.drawing.OverlayType.RECTANGLE]
					},
			
					rectangleOptions: {
						strokeColor: '#FF0000',
						strokeOpacity: 0.8,
						strokeWeight: 1,
						fillColor: '#FF0000',
						fillOpacity: 0.1,
						clickable: true,
						editable: false
					}
				});
			
				drawingManager.setMap(self.map);

				self.drawingManager = drawingManager;

				self.oms = new OverlappingMarkerSpiderfier(self.map, { 
					markersWontMove: true,   // we promise not to move any markers, allowing optimizations
					markersWontHide: true,   // we promise not to change visibility of any markers, allowing optimizations
					basicFormatEvents: true  // allow the library to skip calculating advanced formatting information
				});

				google.maps.event.addListener(drawingManager, 'rectanglecomplete', function (event) {
			
					// Get circle center and radius
					var ne = event.getBounds().getNorthEast();
					var sw = event.getBounds().getSouthWest();
					//console.log(ne, sw)
					// Remove overlay from map
					event.setMap(null);
					//drawingManager.setDrawingMode(null);
			
					//remove old rectangle
					if(!checkAddChosseMarkers) {
						rectangles.forEach(function(rectangle) {
							rectangle.setMap(null)
						})
						rectangles = [];
					}
			
					// Create circle
					self.createRectangle(ne, sw);
				});
				


				$.ajax({
					url: 'getListOrderNeedDelivery',
					type: "get",
					success: function(data){
						self.data = data.listIterator;
						self.dataNoLocation = [];
						var days = {};
						var bounds = new google.maps.LatLngBounds();
						self.data.forEach(function(reqDeliveryOrder) {
							if(!!reqDeliveryOrder.geoPointId) {
								var date = getDate(new Date(reqDeliveryOrder.beginDeliveryDate));

								var marker = new google.maps.Marker({
									position: new google.maps.LatLng(reqDeliveryOrder.latitude, reqDeliveryOrder.longitude),
									map: self.map
								});

								var html = '<i class="fa fa-tag"></i>&nbsp'+reqDeliveryOrder.customerCode+'</br>'
								+'<i class="fa fa-home"></i>&nbsp;'+reqDeliveryOrder.customerFullName+'</br>'
								+ '<i class="fa fa-map-marker"></i>&nbsp;'+reqDeliveryOrder.address;
								var infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width:250px!important;color:#000'>" + html + "</div>"});

								<#--  marker.addListener('click', function(evt) {
									infoWindow.open(self.map, marker);
								});  -->

								google.maps.event.addListener(marker, 'spider_click', function(e) {  // 'spider_click', not plain 'click'
									infoWindow.open(self.map, marker);
								});
								
								marker.address = reqDeliveryOrder.address;
								marker.beginDeliveryDate = reqDeliveryOrder.beginDeliveryDate;
								marker.createdBy = reqDeliveryOrder.createdBy;
								marker.customerFullName = reqDeliveryOrder.customerFullName;
								marker.customerCode = reqDeliveryOrder.customerCode;
								marker.orderDate = reqDeliveryOrder.orderDate;
								marker.orderId = reqDeliveryOrder.orderId;
								marker.orderTypeId = reqDeliveryOrder.orderTypeId;
								marker.originFacilityId = reqDeliveryOrder.originFacilityId;
								marker.priority = reqDeliveryOrder.priority;
								marker.productId = reqDeliveryOrder.productId;
								marker.productStoreId = reqDeliveryOrder.productStoreId;
								marker.salesChannelEnumId = reqDeliveryOrder.salesChannelEnumId;
								marker.sellerId = reqDeliveryOrder.sellerId;
								marker.sellerCode = reqDeliveryOrder.sellerCode;
								marker.remainingSubTotal = reqDeliveryOrder.remainingSubTotal;
								marker.totalWeight = reqDeliveryOrder.totalWeight;
								marker.currencyUom = reqDeliveryOrder.currencyUom;
								marker.grandTotal = reqDeliveryOrder.grandTotal;

								if(!days.hasOwnProperty(date)) {
									days[date] = {
										date: date,
										show: true,
										markers: []
									}
								}
								self.oms.addMarker(marker);
								bounds.extend(marker.position);
								days[date].markers.push(marker)
							} else {
								self.dataNoLocation.push(reqDeliveryOrder)
							}
						})
						self.days = days;
						self.map.fitBounds(bounds);

						self.colors = colorSlicer.getLchColors(Object.keys(days).length + 1, 32, {l: 0, bright: false, unsafe: true}).map(function(lch) { var rgb = colorSlicer.lchToRgb(lch); return hex(rgb[0]) + hex(rgb[1]) + hex(rgb[2])});
						
						var els = [];
						Object.keys(self.days).forEach(function(key, index) {
							var day = self.days[key];
							day.icon = createIcon(self.colors[index]);
							var div = document.createElement('div');
							var name = day.date + " (" + day.markers.length + ")";
							div.innerHTML = '<img src="' + day.icon + '"> ' + name;
							els.push(div);
							day.markers.forEach(function(marker) {
								marker.setIcon(day.icon);
							})
						})
						
						createNote(els);
						pick_day = [{date: "${StringUtil.wrapString(uiLabelMap.All)}"}]
						pick_day = pick_day.concat(Object.keys(self.days).map(function(key) { return self.days[key] }));
						var source =
						{
							datatype: "array",
							localdata: pick_day,
						};
						var dataAdapter = new $.jqx.dataAdapter(source);
						$('#pick-delivery-date').jqxComboBox({
							source: dataAdapter,
							theme: 'olbius',
							width: '220px',
							height: '25px',
							displayMember: "date",
							valueMember: "date",
							checkboxes:true,
							renderer: function (index, label) {
								var item;
								if(label=="${StringUtil.wrapString(uiLabelMap.All)}") {
									item = '<div id="item-selectbox" title="'+label+'"><img src="' + label + '">' + label + '</div>'
								} else {
									var day = self.days[label];
									var name = day.date + " (" + day.markers.length + ")";
									item = '<div id="item-selectbox" title="'+name+'"><img src="' + day.icon + '">' + name + '</div>'
								}
								return item;
							}
						});
						
						pick_day.forEach(function(salesman, index) {
							$("#pick-delivery-date").jqxComboBox('checkIndex', index);
						})
						
						$("#pick-delivery-date").on('checkChange', function (event) {
							if (event.args) {
								var item = event.args.item;
								var pick_day_order = pick_day.find(function(day) {
									return day.date==item.value
								})
								if(!pick_day_order) {
									return;
								}
								
								pick_day_order.show = item.checked;
								if(pick_day_order.date=="${StringUtil.wrapString(uiLabelMap.All)}") {
									if(item.checked) {
										pick_day.forEach(function(day, index) {
											if(day.date!=="${StringUtil.wrapString(uiLabelMap.All)}") {
												$("#pick-delivery-date").jqxComboBox('checkIndex', index);
											}
										})
									} else {
										pick_day.forEach(function(day, index) {
											if(day.date!=="${StringUtil.wrapString(uiLabelMap.All)}") {
												$("#pick-delivery-date").jqxComboBox('uncheckIndex', index);
											}
										})
									}
									return;
								}

								Object.keys(self.days).forEach(function(key){
									if(self.days[key].markers===pick_day_order.markers){
										self.days[key].show = item.checked;
									}
								})
								if(item.checked) {
									pick_day_order.markers.forEach(function(marker) {
										marker.setMap(self.map)
									})
								} else {
									pick_day_order.markers.forEach(function(marker) {
										marker.setMap(null)
									})
								}
							}
						});

						closeLoader();
					},
					error: function(err){console.log(err)}
				})

				//Add omnibox
				var omnibox = document.getElementById('omnibox');
				self.map.controls[google.maps.ControlPosition.TOP_LEFT].push(omnibox);
				
				//Add side bar
				var sideBar = document.getElementById('menu-side-bar');
				self.map.controls[google.maps.ControlPosition.LEFT_CENTER].push(sideBar);
				
				// Update position mouse
				google.maps.event.addDomListener(self.map.getDiv(), 'mousemove', function(e){
					positionMouse = e;
				});

				//Add places search
				var search = document.getElementById('pac-input');
				var autocomplete = new google.maps.places.Autocomplete(search);
				autocomplete.bindTo('bounds', self.map);
				autocomplete.setTypes(["geocode"]);
		
				autocomplete.addListener('place_changed', function() {
					var place = autocomplete.getPlace();
					if(!!place.geometry) {
						var lat = place.geometry.location.lat();
						var lng = place.geometry.location.lng();
						self.map.setCenter(new google.maps.LatLng(lat, lng));
					}
				});
			
				$('#pac-input').keyup(function(e){
					if(e.keyCode == 13)
					{
						self.searchAdress();
					}
				});
				

			})
					
			$(document).keyup(function(e){
				//Key control
				if(e.keyCode == 17) {
					checkAddChosseMarkers = false;
				}
			});
			
			$(document).keydown(function(e){
				//Key control
				if(e.keyCode == 17) {
					if(!checkAddChosseMarkers) {
						checkAddChosseMarkers = true;
					}
				}
			});
			
			var contentReqDelivery = [
				{
					el: '<i class="fa-file-text-o"></i>&nbsp;&nbsp;${uiLabelMap.BSReqDelivery}',
					id: ['DELIVERY','reqDelivery'].join("-")
				}
			];
			
			var actions = [{name: "itemclick", func: eventClickMenu}]
			contextMenu = createContextMenu("#menu-req-delivery", 200, contentReqDelivery, actions);
		}


		self.createRectangle = function(ne, sw) {
			
			if(!checkAddChosseMarkers) {
				chooseMarkers = [];
			}
			
			var newChooseMarker = []
			var bounds = new google.maps.LatLngBounds(sw, ne);
			Object.keys(self.days).forEach(function(key, index) {
				var day = self.days[key];
				if(!!day.show) {
					day.markers.forEach(function(marker, i) {
						if (bounds.contains(marker.position)) {
							newChooseMarker.push(marker);
						}
					})			
				}
			})
			
			newChooseMarker.forEach(function(marker) {
				check = chooseMarkers.find(function(m) {
					return m.orderId == marker.orderId
				})
				
				if(!check) {
					chooseMarkers.push(marker)
				}
			})
			
			
			// Construct the polygon, including both paths.
			rectangle = new google.maps.Rectangle({
			strokeColor: '#FF0000',
				strokeOpacity: 0.8,
				strokeWeight: 1,
				fillColor: '#FF0000',
				fillOpacity: 0.1,
				bounds: new google.maps.LatLngBounds(sw, ne)
			});
			rectangle.setMap(self.map);
			
			//set event to reactangle
			google.maps.event.addListener(rectangle, "rightclick", function(event) {  
				contextMenu.jqxMenu('open', parseInt(positionMouse.pageX), parseInt(positionMouse.pageY));
			});
			
			rectangles.push(rectangle)
			
			updateGeneralInformation();
			
			//createBermudaTriangle(newChooseMarker);
			
		}

		function updateGeneralInformation() {

			// Update information reqDeliveryOrder
			generalInformation = document.createElement("div");
			generalInformation.id = "general-information";
			
			//Add title
			var h3 = document.createElement('h3');
			h3.innerHTML = "${uiLabelMap.BSGeneralInformation}";
			generalInformation.appendChild(h3);
			
			var totalWeight = chooseMarkers.reduce(function(acc, curr) {
				return acc +  parseFloat(curr.totalWeight);
			}, 0)
			
			var totalBudget = [];
			
			chooseMarkers.forEach(function(marker) {
				var u = totalBudget.find(function(u) {
					return u.uom == marker.currencyUom;
				})
				
				if(!!u) {
					u.total +=  parseFloat(marker.grandTotal)
				} else {
					totalBudget.push({
						uom: marker.currencyUom,
						total: parseFloat(marker.grandTotal)
					})
				}
			
			})
			
			var numOrder = chooseMarkers.length;
			
			generalInformation.innerHTML = '<h3>${uiLabelMap.BSGeneralInformation}</h3><div>'+
				'<i class="fa-file-text-o"></i> ${uiLabelMap.TotalOrder}: '+numOrder+
				'</div>'+
				'<div>'+
				'<i class="fa-truck"></i> ${uiLabelMap.ShipmentTotalWeight}: '+totalWeight.toFixed(3) +
				'</div>'+
				'<div>'+
				'<i class="fa-money"></i> ${uiLabelMap.DABudgetTotal}: '+
					totalBudget.map(function(b) {
						return formatcurrency(b.total, b.uom);
					}).join(" + ")
				
				+ '</div>';
			
			self.map.controls[google.maps.ControlPosition.BOTTOM_LEFT].clear();
			self.map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(generalInformation);
			
		}

		function createBermudaTriangle(chooseMarkers) {
			// Define the LatLng coordinates for the polygon's  outer path.
			var outerCoords = getPointsBoundary(chooseMarkers).map(function(marker) {
				return marker.position;
			})

			// Define the LatLng coordinates for the polygon's inner path.
			// Note that the points forming the inner path are wound in the
			// opposite direction to those in the outer path, to form the hole.
			var innerCoords = [
			];

			// Construct the polygon, including both paths.
			rectangle = new google.maps.Polygon({
			paths: [outerCoords, innerCoords],
			strokeColor: '#FF0000',
				strokeOpacity: 0.8,
				strokeWeight: 1,
				fillColor: '#FF0000',
				fillOpacity: 0.1,
			});
			rectangle.setMap(map);
			
			//set event to reactangle
			google.maps.event.addListener(rectangle, "rightclick", function(event) {  
				contextMenu.jqxMenu('open', parseInt(positionMouse.pageX), parseInt(positionMouse.pageY));
			});
			
		rectangles.push(rectangle)
		}
		
		<#--  function getPointsBoundary(chooseMarkers) {
			if(chooseMarkers.length < 4) {
				return chooseMarkers;
			}
			
			var markers = [].concat(chooseMarkers);
			
			var startIndex = markers.findIndex(function(marker) {
				var A = marker.position, check = true;
				for(var i = 0; i < chooseMarkers.length; ++i) {
					var B = chooseMarkers[i].position;
					
					var vectoNomal = [B.lng()-A.lng(), - (B.lat()-A.lat())]
					if(vectoNomal[0] == 0 && vectoNomal[1] == 0) {
						continue;
					}
					var num = 0, check = true;
					for(var k = 0; k < chooseMarkers.length; ++k) {
						var point = chooseMarkers[k].position;
						var temp = vectoNomal[0]*(point.lat()-A.lat()) + vectoNomal[1]*(point.lng()-A.lng());
						
						if(num != 0) {
							if(num*temp < 0) {
								check = false;
								break;
							}
						} else {
							num = temp;
						}
					}
					if(check) {
						//chooseMarkers[i].setIcon("https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=ff8000,fff&scale=0.75")
						console.log("true")
						return true;
					}
				}
				
				return false;
			})
			
			if(startIndex !== -1) {
				var result = [];
				var start = markers.splice(startIndex, 1);
				//start[0].setIcon("https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=ff8000,fff&scale=0.75")
				result = result.concat(start);
				var end;
				do {
					var A = result[result.length -1].position;
					end = undefined;
					for(var i = 0; i < markers.length; ++i) {
						var B = markers[i].position;
						var vectoNomal = [B.lng()-A.lng(), - (B.lat()-A.lat())]
						
						if(vectoNomal[0] == 0 && vectoNomal[1] == 0) {
							continue;
						}
						
						var num = 0, check = true;
						for(var k = 0; k < chooseMarkers.length; ++k) {
							var point = chooseMarkers[k].position;
							var temp = vectoNomal[0]*(point.lat()-A.lat()) + vectoNomal[1]*(point.lng()-A.lng());
							
							if(num != 0) {
								
								if(num*temp < 0) {
									check = false;
									break;
								}
							} else {
								num = temp;
							}
						}
						if(check) {
							end = markers.splice(i, 1);
							
							result = result.concat(end);
							//console.log(result)
							break;
						}
					}
					
				} while (end !== undefined);
			
				return result;
			
			} else {
				alert("No find point start")
				return chooseMarkers;
			}
		}  -->
		function hex(int) {
			var str = int.toString(16);
			return "00".substring(0, 2 - str.length) + str;
		}
		function createNote(els) {
			var note = document.createElement("div");
			note.id = "note";
			
			//Add title
			var h3 = document.createElement('h3');
			h3.innerHTML = "${uiLabelMap.BSNote}";
			note.appendChild(h3);
			
			els.forEach(function(el) {
				note.appendChild(el);
			})
			noteEl = note;
			self.noteEl = note
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
		}
		
		function createIcon(color) {
			return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.75';
		}
		
		self.searchAdress = function() {
			var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";
			var address = $("#pac-input").val();
			$.ajax({
				url: 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key='+key,
				type: "get",
				success: function(data){
					var location;
					if(data.status==="OK") {
						location =  data.results[0].geometry.location;
						self.map.setCenter(new google.maps.LatLng(location.lat, location.lng));
					} else {
						alert("Not found location")
					}
				},
				error: function(err){console.log(err)}
			})
		}
		
		function createContextMenu(id, width, contents, actions) {
			var contextMenu = document.createElement("div");
			contextMenu.id = id.slice(1);
			
			document.getElementById("popup-manager-contex-menu").appendChild(contextMenu);
			var html = createElsArray(contents);
			$(id).append(html);
			
			var contex = $(id).jqxMenu({ width: width, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
			if(!!actions) {
				actions.forEach(function(action, index) {
					contex.on(action.name, function(event) {
						action.func(event);
					});
				})
			}
			return contex;
		}
		
		function createElsArray(array) {
			var els = [];
			els.push("<ul>");
			array.forEach(function(value, index) {
				if(Object.prototype.toString.call(value) == "[object Array]") {
					els.push(createElsArray(value));
				} else {
					if(Object.prototype.toString.call(value) == "[object Object]") {
						els.push(createElsObject(value));
					}
				}
			});
			els.push("</ul>");
			return els.join("");
		}
		
		function createElsObject(object) {
			var children = "";
			if(!!object.children && Object.prototype.toString.call(object.children) == "[object Array]") {
				children = createElsArray(object.children);
			}
			
			var el = '<li ' + (!!object.id?'id="'+object.id+'"':"") + '>' +
							object.el +
						children +
					'</li>';
			
			return el;
		}

		function eventClickMenu(evt) {
			var args = evt.args;
			var itemId = $(args).attr('id');
			var [category, id] = itemId.split("-")

			switch (category) {
				case 'DELIVERY':
					if(id == 'reqDelivery') {
						createTableOrderChoose();
					}
					break;
			}
		}
		
		createTableOrderChoose = function() {
			var orders = [];
			chooseMarkers.forEach(function(marker) {
				if(!!marker.orderId) {
					var check = orders.find(function(order){
						return order.orderId == marker.orderId
					})
					
					if(!check) {
						orders.push(marker)
					}
				}	
			})
			tableOrdersChoose = document.createElement("div");
			tableOrdersChoose.id = "view-orderchoose";
			
			var html = 
						'<div style="margin-top: 20px;width: 95%;">'+
							'<div class="pick-date">'+
								'<div class="name-pick-date"> ${uiLabelMap.BSRequiredByDate} </div>'+
								'<div id="inputrequiredByDate"></div>'+
							'</div>'+
							'<div class="pick-date">'+
								'<div class="name-pick-date"> ${uiLabelMap.BSRequirementStartDate} </div>'+
								'<div id="inputrequirementStartDate"></div>'+
							'</div>'+
						'</div>'+
						'<div style="padding: 0px 10px; font-size: 14px;">'+
							'<div>'+
								'<p>${uiLabelMap.BSDescription}</p>'+
							'</div>'+
							'<div style="width:95%;">'+
								'<textarea id="description" style="width:100%;"></textarea>'+
							'</div>'+
						'</div>'+
						'<div id="table-orders-choose"></div>'+
						'<div id="menu-table-orders-choose">'+
							'<ul>'+
								'<li id="delete-order"><i class="fa-trash"></i>&nbsp;&nbsp;${uiLabelMap.BSRemove}</li>'+
							'</ul>'+
						'</div>'+
						'<button onClick="reqDeliveryOrderNewOnMap.closeTableOrdersChoose()" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>'+
						'<button onClick="reqDeliveryOrderNewOnMap.createReqDeliveryOrder()" class="btn btn-primary form-action-button pull-right" style="cursor: default;"><i class="icon-ok"></i>${uiLabelMap.BSSave}</button>';
			
			tableOrdersChoose.insertAdjacentHTML('beforeend', html);
			
			var source = {
				datatype: "json",
				localdata: orders
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			$("body").append(tableOrdersChoose);
			/*
			$("#view-orderchoose").jqxWindow({
				position: getPosition(noteEl)
			});
			*/
			
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(tableOrdersChoose);
			tableOrders = tableOrdersChoose;
			cellsrenderer = function(row, column, value, a, b, data){
				var order = orders[row]
				var val = value;
				switch(column) {
					case "orderDate":
						val = getDate(new Date(value))
						break;
					case "beginDeliveryDate":
						val = getDate(new Date(value))
						break;
					case "grandTotal":
						val = formatcurrency(order.grandTotal, order.currencyUom)
						break;
				}
				return '<div class="jqx-grid-cell-left-align" style="margin-top: 4px;" title="'+val+'">'+val+'</div>';
			}
			
			
			
			
			$('#inputrequiredByDate').jqxDateTimeInput({ width: '200px', height: '25px', formatString: 'dd-MM-yyyy', disabled: true});
			$('#inputrequirementStartDate').jqxDateTimeInput({ width: '200px', height: '25px', formatString: 'dd-MM-yyyy', min: new Date()});
		
			var grid = $("#table-orders-choose").jqxGrid(
				{
					width: '95%',
					height: '200px',
					pageable: true,
					autoshowloadelement:true,
					source: dataAdapter,
					columnsresize: true,
					theme: "olbius",
					columns: [
					{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', width: 150, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.createDate}', datafield: 'orderDate', width: 100, cellsrenderer: cellsrenderer},
					{ text: '${uiLabelMap.deliveryDate}', datafield: 'beginDeliveryDate', width: 100, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.BSCustomerId}', datafield: 'customerCode', width: 100, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.BSCustomerName}', datafield: 'customerFullName', width: 100, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.BSAddress}', datafield: 'address', width: 200, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.productStore}', datafield: 'productStoreId', width: 100, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.BSMTurnover}', datafield: 'grandTotal', width: 150, cellsrenderer: cellsrenderer },
					{ text: '${uiLabelMap.BSTotalWeight}', datafield: 'totalWeight', minwidth: 150, width: 'auto', cellsrenderer: cellsrenderer },
					]
			});
			
			var localizestrings = getLocalization()
			$("#table-orders-choose").jqxGrid('localizestrings', localizestrings);
			

			// create context menu
            //var contextMenu = $("#menu-table-orders-choose").jqxMenu({ width: 200, height: 30, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
			jOlbUtil.contextMenu.create($("#menu-table-orders-choose"));
			Grid.createContextMenu(grid, $("#menu-table-orders-choose"), "false");
			<#--  $("#table-orders-choose").on('contextmenu', function () {
                return false;
            });  -->

            // handle context menu clicks.
            $("#menu-table-orders-choose").on('itemclick', function (event) {
                var args = event.args;
				var itemId = $(args).attr('id');
                var rowindex = $("#table-orders-choose").jqxGrid('getselectedrowindex');
				var rowdata = $("#table-orders-choose").jqxGrid('getrowdata', rowindex);

				
				switch(itemId) {
					case 'delete-order':
					var id = $("#table-orders-choose").jqxGrid('getrowid', rowindex);
					 $("#table-orders-choose").jqxGrid('deleterow', id);
					var markerIndex = chooseMarkers.findIndex(function(marker) {
						return marker.orderId == rowdata.orderId
					})

					if (markerIndex > -1) {
						chooseMarkers.splice(markerIndex, 1);
					}
					updateGeneralInformation();
					break;
				}
            });
		}
		
		function getPosition(el) {
		var xPos = 0;
		var yPos = 0;
		
		while (el) {
			if (el.tagName == "BODY") {
			// deal with browser quirks with body/window/document and page scroll
			var xScroll = el.scrollLeft || document.documentElement.scrollLeft;
			var yScroll = el.scrollTop || document.documentElement.scrollTop;
		
			xPos += (el.offsetLeft - xScroll + el.clientLeft);
			yPos += (el.offsetTop - yScroll + el.clientTop);
			} else {
			// for all other non-BODY elements
			xPos += (el.offsetLeft - el.scrollLeft + el.clientLeft);
			yPos += (el.offsetTop - el.scrollTop + el.clientTop);
			}
		
			el = el.offsetParent;
		}
		return {
			x: xPos,
			y: yPos
		};
		}
		
		function updateNote() {
			var els = [];
			Object.keys(self.days).forEach(function(key, index) {
				var day = self.days[key];
				day.icon = createIcon(self.colors[index]);
				var div = document.createElement('div');
				var name = day.date + " (" + day.markers.length + ")";
				div.innerHTML = '<img src="' + day.icon + '"> ' + name;
				els.push(div);
				day.markers.forEach(function(marker) {
					marker.setIcon(day.icon);
				})
			})
			
			createNote(els);
		}
		
		self.closeTableOrdersChoose = function() {
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			if(!!self.noteEl) {
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(self.noteEl);
			}
		}
		
		self.createReqDeliveryOrder = function() {
			orderList = []
			chooseMarkers.forEach(function(marker) {
				if(!!marker.orderId) {
					if(orderList.indexOf(marker.orderId) == -1) {
						orderList.push(marker.orderId)
					}
				}
			})

			if(orderList.length == 0) {
				$("#jqxNotification-NoStore").jqxNotification("open");
				return;
			}

			$.ajax({
				url: 'createReqDeliveryOrderAjax',
				type: 'POST',
				data: {
					requirementStartDate: $('#inputrequirementStartDate').jqxDateTimeInput('getDate').getTime(),
					description: $("#description").val()||"",
					orderList: JSON.stringify(orderList)
				},
				success: function(data){
					if(!!data._ERROR_MESSAGE_) {
						$("#jqxNotification-updateError").jqxNotification("open");
					} else {
						$("#jqxNotification-updateSuccess").jqxNotification("open");
						self.map.controls[google.maps.ControlPosition.BOTTOM_LEFT].clear();
						
						rectangles.forEach(function(rectangle) { rectangle.setMap(null) });
						
						// remove markers Require Delivery Order
						Object.keys(self.days).forEach(function(key){
							var day = self.days[key];
							newMarkers = [];
							day.markers.forEach(function(marker) {
								if(orderList.indexOf(marker.orderId) == -1) {
									newMarkers.push(marker)
								} else {
									marker.setMap(null)
								}
							})
							day.markers = newMarkers
						})
						self.closeTableOrdersChoose();
						rectangles = [];
						updateNote();
					}
				}
			})
		}

		return self;
	}();
	$(document).ready(function(){
		reqDeliveryOrderNewOnMap.initMap();
	})
	// TODO
	
	$("#jqxNotification-updateError").jqxNotification({
	    width: "auto",
	    position: "top-right",
	    opacity: 0.9,
	    autoOpen: false,
	    autoClose: true,
	    template: "info"
	});
	
	$("#jqxNotification-updateSuccess").jqxNotification({
	    width: "auto",
	    position: "top-right",
	    opacity: 0.9,
	    autoOpen: false,
	    autoClose: true,
	    template: "info"
	});

	$("#jqxNotification-NoStore").jqxNotification({
	    width: "auto",
	    position: "top-right",
	    opacity: 0.9,
	    autoOpen: false,
	    autoClose: true,
	    template: "info"
	});
	
	
	
	
</script>
