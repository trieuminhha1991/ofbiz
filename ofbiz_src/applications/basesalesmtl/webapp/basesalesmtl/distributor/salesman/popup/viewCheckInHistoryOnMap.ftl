<div id="jqxwindowCheckInHistoryOnMap" style="display:none;">
	<div>${uiLabelMap.BSCheckInHistory}</div>
	<div>
		<div style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.Employee}:</div> <div class="salesmanInfo jqxwindowTitle" style="display: inline-block;"></div></div>
		<#include "sideBarCheckInHistory.ftl"/>
		
		<div id="map-check-in-history" style="height: 405px;overflow-y: hidden;"></div>
		<div class="form-action row-fluid">
			<div class="span12">
				<button id="btnCloseCheckInHistoryOnMap" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<script>
	var colorInit=["#F7786B","#91A8D0","#034F84","#FAE03C","#98DDDE","#9896A4","#DD4132","#B18F6A","#79C753","#B93A32","#AD5D5D","#006E51","#B76BA3","#5C7148","#D13076"];
	var checkInHistoryOnMap = (function() {
		var self={
			key: "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs"
		};
		self.chooseDate = [];


		function initMap() {
			
			var sub = Array.from($("#jqxwindowCheckInHistoryOnMap .jqx-window-content").children()).reduce(function(acc, curr) {
					if(curr.id !== "menu-side-bar-check-in-history") {
						return acc + curr.offsetHeight
					} else {
						return acc
					}
				}, 0) - 10;
			
			$("#map-check-in-history").height(sub<405?405:sub);
			var mapOptions = {
				zoom: 5,
				minZoom: 5,
				center: new google.maps.LatLng(21.0056183, 105.8433475),
				mapTypeControlOptions: {
					style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
					position: google.maps.ControlPosition.TOP_CENTER
				},
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};

			self.map = new google.maps.Map(document.getElementById('map-check-in-history'), mapOptions);
			var map = self.map;
			var oms = new OverlappingMarkerSpiderfier(self.map, { 
				markersWontMove: true,   // we promise not to move any markers, allowing optimizations
				markersWontHide: true,   // we promise not to change visibility of any markers, allowing optimizations
				basicFormatEvents: true  // allow the library to skip calculating advanced formatting information
			});

			self.oms = oms;
			google.maps.event.addListenerOnce(map, 'idle', function(){
				//Add omnibox
				createOmnibox();
				
				//Add side bar
				var sideBar = document.getElementById('menu-side-bar-check-in-history');
				map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(sideBar);
				
				// Update position mouse
				google.maps.event.addDomListener(map.getDiv(), 'mousemove', function(e){
					self.positionMouse = e;
				});
				
			})
		}

		async function defaultInit() {
			var today = new Date();
			var from = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate());
			var thruDate = new Date(today.getFullYear(), today.getMonth() + 1, today.getDate() + 1, 0, 0, -1);
			createMenu();
			//get check in today
			updateMarkers(from, thruDate);
			
			updateNote();
			
		}

		function createNote (els) {
			var note = document.createElement("div");
			note.id = "note";
			note.style.overflowY = "auto";
			note.style.maxHeight = "300px";

			//Add title
			var h3 = document.createElement('h3');
			h3.innerHTML = "${uiLabelMap.BSNote}";
			note.appendChild(h3);

			els.forEach(function(el) {
				note.appendChild(el);
			})

			self.noteEl = note;

			if(!!self.map) {
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
			}
		}

		function updateNote() {
			var els = [];
			self.chooseDate.forEach(function(date, index) {
				var div = document.createElement('div');
				var name = date.name + " (" + date.markers.length + ")";
				div.innerHTML = '<img src="' + date.icon + '"> ' + name;
				els.push(div);
			})
			createNote(els);
		}

		function createOmnibox () {
			var omniboxEl =
				'<div id="omnibox-check-check-in" style="margin: 5px; display: flex; height: 30px; align-items: stretch; border-bottom: 1px solid #ddd; background: #fff; border-bottom: 1px solid #ddd; box-shadow: 0 3px 12px rgba(27,31,35,0.15);">'+
					'<button style="border: none; outline: none; background: none;" id="side-bar-history-check-in-map" onClick="openSideBarCheckInHistory()" title="menu">'+
						'<span class="fa fa-bars"></span>'+
					'</button>'+
					'<input id="input-search-check-in" style="width: 250px;margin: 0;border: none;outline: none;background: none;height: 100%;padding: 0px 10px;" class="form-control" type="text" placeholder="Type city, zip or address here..">'+
					'<button style="border: none; outline: none; background: none;" onClick="checkInHistoryOnMap.searchAddress()" title="search">'+
						'<span class="fa fa-search"></span>'+
					'</button>'+
				'</div>';
			self.omnibox = $.parseHTML(omniboxEl)[0];

			if(!!self.map) {
				self.map.controls[google.maps.ControlPosition.TOP_LEFT].push(self.omnibox);
				$('#input-search-check-in').keyup(function(e){
					if(e.keyCode == 13) {
						self.searchAddress();
					}
				});

				//Add places search
				var search = document.getElementById('input-search-check-in');
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

			}
		}

		self.searchAddress = function () {
			var address = $("#input-search-check-in").val();
			$.ajax({
				url: 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key='+self.key,
				type: "get",
				success: function(data){
					var location;
					if(data.status==="OK") {
				location = data.results[0].geometry.location;
			if(!!self.map) {
				self.map.setCenter(new google.maps.LatLng(location.lat, location.lng));
			}
					} else {
				alert("Not found location")
			}
			},
				error: function(err){console.log(err)}
			})
		}

		function createMenu() {
			var items = 
				'<div class="side-bar-item-container">'+
					'<label class="tree-toggle nav-header">'+
						'${uiLabelMap.timePeriod}'+
					'</label>'+
					'<ul class="nav-list tree" style="height: 130px;">'+
						'<li style="border:none; padding: 5px 5px 5px 10px; height: 25px;">'+
							'<div class="pick-date">'+
								'<div class="name-pick-date"> ${uiLabelMap.startDate} </div>'+
								'<div id="inputFromDate"></div>'+
							'</div>'+
							'<div class="pick-date">'+
								'<div class="name-pick-date"> ${uiLabelMap.endDate} </div>'+
								'<div id="inputThruDate"></div>'+
							'</div>'+
						'</li>'+
					'</ul>'+
					'<hr class="side-bar-separator">'+
				'</div>'+
				'<div class="side-bar-item-container">'+
						'<label class="tree-toggle nav-header">'+
							'${uiLabelMap.showForTime}'+
						'</label>'+
						'<ul class="nav-list tree" style="height: 130px;">'+
							'<li style="border:none; padding: 5px 5px 5px 10px; height: 25px;">'+
								'<div class="pick-date">'+
									'<div id="pick-date-show-check-in-history"></div>'+
								'</div>'+
							'</li>'+
						'</ul>'+
						'<hr class="side-bar-separator">'+
				'</div>';
			Array.from(document.getElementById("side-bar-selection-check-in-items").children).forEach(function(el) {
				el.remove()
			})
			$.parseHTML(items).forEach(function(item){
				document.getElementById("side-bar-selection-check-in-items").appendChild(item)
			})

			var maxDate = new Date();
			var minDate = new Date(maxDate.getFullYear() - 1, maxDate.getMonth() + 1, maxDate.getDate());
			// Add format html
			$('#inputFromDate').jqxDateTimeInput({ width: '170px', height: '25px', formatString: 'dd-MM-yyyy', min: minDate,max: new Date()});
			$('#inputThruDate').jqxDateTimeInput({ width: '170px', height: '25px', formatString: 'dd-MM-yyyy', min: minDate,max: new Date()});

			// Add event
			
			$('#inputFromDate').on('change', async function (event) {  
				var dateFrom = event.args.date;
				var dateTo = $("#inputThruDate").jqxDateTimeInput("getDate");
				dateTo = new Date(dateTo.getFullYear(), dateTo.getMonth(), dateTo.getDate() + 1, 0, 0, -1);
				updateMarkers(dateFrom, dateTo);
			});
			
			$('#inputThruDate').on('change', function (event) {
				var dateTo = event.args.date;
				var dateFrom = $("#inputFromDate").jqxDateTimeInput("getDate");
				dateFrom = new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + 1, 0, 0, -1);
				updateMarkers(dateFrom, dateTo);
			}); 

  
			var source = {
				datatype: "array",
			};

			var dataAdapter = new $.jqx.dataAdapter(source);
			$('#pick-date-show-check-in-history').jqxComboBox({
				source: dataAdapter,
				theme: 'olbius',
				width: '170px',
				height: '25px',
				displayMember: "name",
				valueMember: "name",
				checkboxes:true,
				renderer: function (index, label) {
					var date = self.chooseDate[index];
					var name = date.name + " (" + date.markers.length + ")";
					var item = '<div style="padding:2px;" title="'+name+'"><img style="width: 20px;padding-right: 3px;" src="' + date.icon + '">' + name + '</div>'
					return item;
				}
			});


			$("#pick-date-show-check-in-history").on('checkChange', function (event) {
				if (event.args) {
					var value = event.args.value;
					var checked = event.args.checked;

					var item = self.chooseDate.find(function(date){
						return date.name==value;
					})

					if(!!item){
						if(checked) {
							item.markers.forEach(function(marker) {
								marker['check-in'].setMap(self.map)
								marker['flightPath'].setMap(self.map)
								self.markerCluster.addMarker(marker['check-in'])
							})
						} else {
							item.markers.forEach(function(marker) {
								marker['check-in'].setMap(null)
								marker['flightPath'].setMap(null)
								self.markerCluster.removeMarker(marker['check-in'])
							})
						}
					}
				}
			});


		}

		async function updateMarkers(dateFrom, dateTo) {
			
			self.chooseDate.forEach(function(date) {
					
				date.markers.forEach(function(marker){
					marker['check-in'].setMap(null)
					marker['flightPath'].setMap(null)
					self.markerCluster.removeMarker(marker['check-in'])
				})
				date.markers=[];
			})
			if(!!self.stores) {
				self.stores.forEach(function(store) {
					store.marker.setMap(null)
				})
				self.stores = [];
			}
			if(!!self.markerCluster) {
				self.markerCluster.setMap(null);
				
			}
			self.markerCluster = new MarkerClusterer();
			
			<#--  if(!!self.stores) {
				self.stores.forEach(function(store) {
					store.setMap(null)
				})
			}  -->
			
			var diffDays = subDate(dateFrom, dateTo);

			listDate = [];
			if(diffDays > 15) {
				var totMonth = subMonth(dateFrom, dateTo);
				if(totMonth > 2) {
					listDate.push({
						dateFrom: dateFrom,
						dateTo: new Date(dateFrom.getFullYear(), dateFrom.getMonth() + 1, 1, 0, 0, -1)
					})

					for(var i = 2; i < totMonth; ++i) {
						listDate.push({
							dateFrom: new Date(dateFrom.getFullYear(), dateFrom.getMonth() + i - 1, 1, 0, 0, 0),
							dateTo: new Date(dateFrom.getFullYear(), dateFrom.getMonth() + i, 1, 0, 0, -1)
						})
					}

					listDate.push({
						dateFrom: new Date(dateTo.getFullYear(), dateTo.getMonth(), 2, 0, 0, -1),
						dateTo: dateTo
					})
				} else {
					var distance_day = Math.ceil(diffDays/10);
					var times = Math.floor(diffDays/distance_day);
					while(times > 15) {
						distance_day++;
						times = Math.floor(diffDays/distance_day);
					}
					
					for(var i = 0; i < times - 1; ++i) {
						listDate.push({
							dateFrom: new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + distance_day*i, 0, 0, 0),
							dateTo: new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + distance_day*(i+1) + 1, 0, 0, -1)
						})
					}

					listDate.push({
						dateFrom: new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + distance_day*(times - 1), 0, 0, 0),
						dateTo: dateTo
					})
				}

				listDate = listDate.map(function(distance){
					distance.name = distance.dateFrom.getDate() + "-" + distance.dateTo.getDate() + "/"+ (distance.dateFrom.getMonth() + 1) + "/" + distance.dateFrom.getFullYear();
					return distance
				})
			} else {
				for(var i = 0; i < diffDays; ++i) {
					listDate.push({
						dateFrom: new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + i, 0, 0, 0),
						dateTo: new Date(dateFrom.getFullYear(), dateFrom.getMonth(), dateFrom.getDate() + i + 1, 0, 0, -1)
					})
				}

				listDate = listDate.map(function(distance){
					distance.name = distance.dateFrom.getDate() + "/" + (distance.dateFrom.getMonth() + 1) + "/" + distance.dateFrom.getFullYear();
					return distance
				})
			}
			self.stores = []
			for(var i = 0; i < listDate.length; ++i ) {
				var distance = listDate[i];
				var historyCheckIn = await self.getCheckInHistory(distance.dateFrom, distance.dateTo, self.data.partyId);
				
				distance.icon = createIcon(colorInit[i], 2, historyCheckIn.length);
				
				var markers = [];
				historyCheckIn.forEach(function(cus, index){
					if(cus.latitude < -90 || cus.latitude > 90 || cus.longitude < -180 || cus.longitude > 180
					  ||cus.customerLatitude < -90 || cus.customerLatitude > 90 || cus.customerLongitude < -180 || cus.customerLongitude > 180) {
						  return ;
					  }

					var image = {
						url: createIcon(colorInit[i], 2, index+1),
						// This marker is 20 pixels wide by 32 pixels high.
						scaledSize: new google.maps.Size(28, 28), // scaled size
						origin: new google.maps.Point(0, 0),
						anchor: new google.maps.Point(14, 14)
					};
					var marker = new google.maps.Marker({
						position: new google.maps.LatLng(cus.latitude, cus.longitude),
						icon: image,
						labelClass: "labels",
						map: self.map
					});

					var distance = '';
					if(cus.distance > 1000) {
						distance = (cus.distance/1000).toFixed(2) + ' km'
					} else {
						distance = cus.distance + ' m'
					}
					var options = {
						year: 'numeric', month: 'numeric', day: 'numeric',
						hour: 'numeric', minute: 'numeric', second: 'numeric',
						hour12: false,
						timeZone: 'Asia/Ho_Chi_Minh' 
					}
					var html = "<i class='fa fa-tag'></i>&nbsp;" + cus.partyId + "</br>"
				   		+"<i class='fa fa-user'></i>&nbsp;" + cus.fullName + "</br>"
						+"<i class='fa fa-home'></i>&nbsp;" + cus.customerCode + "</br>"
						+"<i class='fa fa-home'></i>&nbsp;" + cus.customerName + "</br>"
						+"<i class='fa fa-calendar'></i>&nbsp;" + getDate(new Date(cus.checkInDate), options) + "</br>"
						+ "<i class='fa fa-map-marker'></i>&nbsp;" + cus.customerAddress + "</br>"
						+ "<i class='fa fa-expand'></i>&nbsp;" + distance;
					   	
				   	var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" style="width:250px!important;color:#000">' + html + '</div>'});
				   	
				   	<#--  marker.addListener('click', function(evt) {
				  		infoWindow.open(self.map, marker);
					});  -->

					

					self.oms.addMarker(marker);

					var checkStore = self.stores.find(function(store) {
						return cus.customerCode == store.customerCode
					})

					if(!checkStore) {
						var imageStore = {
							url: createIconStore('#4080ff', 2),
							// This marker is 20 pixels wide by 32 pixels high.
							scaledSize: new google.maps.Size(28, 28), // scaled size
							origin: new google.maps.Point(0, 0),
							anchor: new google.maps.Point(14, 14)
							
						};
						var store = new google.maps.Marker({
							position: new google.maps.LatLng(cus.customerLatitude, cus.customerLongitude),
							icon: imageStore,
							labelClass: "labels",
							map: self.map
						});

						var htmlStore = "<i class='fa fa-tag'></i>&nbsp;" + cus.customerCode + "</br>"
							+"<i class='fa fa-home'></i>&nbsp;" + cus.customerName + "</br>"
							+ "<i class='fa fa-map-marker'></i>&nbsp;" + cus.customerAddress + "</br>"
							
						var infoWindowStore = new google.maps.InfoWindow({content: '<div id="iw" style="width:250px!important;color:#000">' + htmlStore + '</div>'});
						
						<#--  store.addListener('click', function(evt) {
							infoWindowStore.open(self.map, store);
						});  -->

						google.maps.event.addListener(store, 'spider_click', function(e) {  // 'spider_click', not plain 'click'
							infoWindowStore.open(self.map, store);
						});

						checkStore = {
							customerCode: cus.customerCode,
							customerName: cus.customerName,
							customerAddress: cus.customerAddress,
							marker: store
						};
						self.stores.push(checkStore)

						self.oms.addMarker(store);
					}


					var flightPlanCoordinates = [
						marker.position,
						checkStore.marker.position
					];

					var flightPath = new google.maps.Polyline({
						path: flightPlanCoordinates,
						geodesic: true,
						map: self.map,
						strokeColor: '#FF0000',
						strokeOpacity: 1.0,
						strokeWeight: 2
					});
				  	

					google.maps.event.addListener(marker, 'spider_click', function(e) {  // 'spider_click', not plain 'click'
						infoWindow.open(self.map, marker);
						flightPath.setOptions({strokeColor: '#0095ff'});
					});

					google.maps.event.addListener(infoWindow,'closeclick',function(){
						flightPath.setOptions({strokeColor: '#FF0000'});
						// then, remove the infowindows name from the array
					});

					

					markers.push({
						'check-in': marker,
						flightPath: flightPath
					})
				})
				distance.markers = markers;
			}

			 var totalMarkers = []
			listDate.forEach(function(date) {
				date.markers.forEach(function(marker) {
					totalMarkers.push(marker['check-in'])
				})
			})

			self.markerCluster = new MarkerClusterer(self.map, totalMarkers,
			{
				imagePath: '/salesmtlresources/image/google_map/markerclusterer/m',
				maxZoom: 12
			});
			self.chooseDate = listDate;
			updateInputDateShow()
			updateNote()
		}

		function updateInputDateShow() {
			$('#pick-date-show-check-in-history').jqxComboBox('clear'); 
			self.chooseDate.forEach(function(date){
				$("#pick-date-show-check-in-history").jqxComboBox('addItem', date ); 
				$("#pick-date-show-check-in-history").jqxComboBox('checkItem', date );
			})
		}

		function createIcon(color, size, index) {
			return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-container-bg_4x.png,icons/onion/SHARED-mymaps-container_4x.png,icons/onion/1738-blank-sequence_4x.png&highlight=ff000000,'+color.substr(1)+',ff000000&scale='+size+'&color=ffffffff&psize=15&text='+index+'&font=fonts/Roboto-Medium.ttf';
			//return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-container_4x.png,icons/onion/1738-blank-sequence_4x.png&highlight='+color.substr(1)+',ff000000&scale='+size+'&color=ffffffff&psize=15&text='+index+'&font=fonts/Roboto-Medium.ttf';
		}

		function createIconStore(color, size) {
			return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-container-bg_4x.png,icons/onion/SHARED-mymaps-container_4x.png,icons/onion/1686-shop_4x.png&highlight=ff000000,'+color.substr(1)+',ff000000&scale='+size;
		}

		function subDate(dateFrom, dateTo) {
			var timeDiff = Math.abs(dateTo.getTime() - dateFrom.getTime());
			var diffDays = Math.ceil(timeDiff / (1000 * 3600 * 24)); 
			return diffDays
		}

		function subMonth(dateFrom, dateTo) {
			var months;
			months = (dateTo.getFullYear() - dateFrom.getFullYear()) * 12;
			months -= dateFrom.getMonth();
			months += dateTo.getMonth() + 1;
			return months <= 0 ? 0 : months;
		}

		function getDate(date, options) {
			return new Intl.DateTimeFormat('en-GB', options).format(date)
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

		self.getCheckInHistory = function(fromDate, thruDate, sailesmanId) {
			return new Promise(function(resolve, reject) {
				if(!!sailesmanId) {
					$.ajax({
						url: "getSalesmanCheckInHistory",
						type: "post",
						data: {
							partyId: sailesmanId,
							fromDate: fromDate.getTime(),
							thruDate: thruDate.getTime()
						},
						success: function(data) {
							if(!data._ERROR_MESSAGE_) {
								resolve(data.customers)
							} else {
								resolve();
								alert(data._ERROR_MESSAGE_)
							}
							return;
						}

					})
				} else {
					$.ajax({
						url: "getListSalesman",
						type: "get",
						success: function(data) {
							if(!data._ERROR_MESSAGE_) {
								Promise.all(data.listSalesman.map(async function(salesman) {
									salesman.checkStore = await self.getCheckInHistory(fromDate, thruDate, salesman.partyId);
									return salesman
								})).then(function(results) {
									resolve(results)
								})
							} else {
								resolve();
								alert(data._ERROR_MESSAGE_)
							}
						}
					})
				}
			})
		}

		self.open = function(row) {
			//openLoader()
			self.data = row;
			var wtmp = window;
	    	var tmpwidth = $("#jqxwindowCheckInHistoryOnMap").jqxWindow("width");
	        $("#jqxwindowCheckInHistoryOnMap").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	
			$("#jqxwindowCheckInHistoryOnMap").jqxWindow("open");

	    	if(!self.map) {
		    	initMap();
	    	}

			defaultInit();
		}

		self.initForm = function () {
			$("#jqxwindowCheckInHistoryOnMap").jqxWindow({
				theme: "olbius", maxWidth: 1000, width: 1000, height: 560, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#btnCloseCheckInHistoryOnMap"), modalOpacity: 0.7
			});
		}

		$(document).ready(function() {
			self.initForm();
		});


		return self;
	})();


</script>
<script src="/salesmtlresources/js/google_map/markerclusterer.js"></script>
<script src="/salesmtlresources/js/google_map/oms.min.js"></script>
