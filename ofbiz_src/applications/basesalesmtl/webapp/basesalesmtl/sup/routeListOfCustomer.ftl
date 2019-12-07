<div id="AddGoogleMapForm" class='hide'>
	<div>
		${uiLabelMap.BSAddCustomerToRoute}
	</div>
	<div class="form-window-container" id="popup-manager-customer">
		<div id="jqxNotification-updateSuccess">C&#x1EAD;p nh&#x1EAD;t th&#xE0;nh c&#xF4;ng</div>
		<div id="jqxNotification-updateError">C&#x1EAD;p nh&#x1EAD;t th&#x1EA5;t b&#x1EA1;i</div>
		<div id="omnibox">
			<button class="icon-side-bar" id="side-bar-map" onClick="openSideBar()" title="menu">
				<span class="fa fa-bars"></span>
			</button>
			<input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
			<button class="search-box-button" onClick="searchAdress()" title="search">
				<span class="fa fa-search"></span>
			</button>
		</div>
		
		<div id="pick-view"> </div>
		<#include "sideBar.ftl"/>
		<div class='row-fluid'>
			<div class='map-container' id="googlemap">
			</div>
		</div>
	</div>
</div>
<style>
	#pac-input {
		width: 250px;
   		margin: 0;
   		border: none;
		outline: none;
		background: none;
		height: 100%;
    	padding: 0px 10px;
	}
	
	#wrappertable-store-no-location #contenttable-store-no-location, #wrappertable-visiting-routes-of-customer #contenttable-visiting-routes-of-customer{
		border: 1px #CCC solid!important;
	}
	
	#googlemap {
        border: 1px solid #d9d9d9;
	}
	
	 #note {
        border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0,0,0,.25)!important;
        margin-right: 5px;
		margin-top: 5px;
		padding: 10px;
      }
      
      #note h3 {
        margin-top: 0;
      }
      
      #note img, #content-route-change li img {
      	padding: 2px;
      	width: 20px;
        vertical-align: middle;
      }
      
      #item-selectbox img {
      	padding: 2px;
      	width: 20px;
        vertical-align: middle;
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
      
      .jqx-menu-popup-olbius {
      	z-index: 999999!important;
      }
      
      #omnibox button {
      	border: none;
		outline: none;
		background: none;
      }
      
      #table-store-no-location {
          width: 500px;
          margin: 10px 10px 0px 10px;
      }
      
      #table-visiting-routes-of-customer {
      	width: 600px;
       	margin: 10px 10px 0px 10px;
      }
      
      #view-store-no-location, #view-visiting-routes-of-customer{
      	border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0,0,0,.25)!important;
        margin-right: 5px;
		margin-top: 5px;
      }
      
      #view-store-no-location button, #view-visiting-routes-of-customer button {
		  margin: 10px;
      }
      
      .jqx-menu-wrapper{
		z-index: 99999!important;
      }
      
      .disable {
      	background: #e6e6e6!important;
      	color: #000!important;
      	border: none!important;
		outline: none!important;
      }
      
      .disable:hover {
      	background: #e6e6e6!important;
      	color: #000!important;
      	border: none!important;
		outline: none!important;
      }
      
      .row-fluid .notifyjs-wrapper::before {
      	top: 60px;
      }
      
</style>

<script>
	var test;
	
	var CustomerRoute = (function() {
		
		var self = {};
		self.pickViewSource;
		self.popup, self.RouteGrid;
		var popupHeader = "${uiLabelMap.BSAddCustomerToRoute}";
		self.map;
		self.contextMenu;
		self.note;
		self.bounds;
		self.contextMenuId;
		self.stores = [];
		self.markers = [];
		self.data=[];
		self.dataNoLacation=[];
		self.storeSelected;
		self.positionMouse;
		self.days = sortByDay(days);
		
		self.initMap = function() {
			var map;
			self.bounds = new google.maps.LatLngBounds();
			var mapOptions = {
				label: "",
				zoom: 15,
				center: new google.maps.LatLng(21.0056183, 105.8433475),
				zoomControl: true,
				mapTypeControl: true,
				mapTypeControlOptions: {
				    style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
				    position: google.maps.ControlPosition.TOP_CENTER
				},
				scrollwheel: false,
				scaleControl: false,
				rotateControl: false,
				fullscreenControl: false,
				mapTypeId: google.maps.MapTypeId.ROADMAP
			};
			map = new google.maps.Map(document.getElementById('googlemap'), mapOptions);
	        
	        // Update position mouse
	        google.maps.event.addDomListener(map.getDiv(), 'mousemove', function(e){
				self.positionMouse = e;
			});
	        
	        google.maps.event.addListenerOnce(map, 'idle', function(){
	        	//Add omnibox
	        	var omnibox = document.getElementById('omnibox');
	        	map.controls[google.maps.ControlPosition.TOP_LEFT].push(omnibox);
	        	
	        	//Add side bar
	        	var sideBar = document.getElementById('menu-side-bar');
	        	map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(sideBar);
	        	
	        	//Add places search
	        	var search = document.getElementById('pac-input');
	        	var autocomplete = new google.maps.places.Autocomplete(search);
		        autocomplete.bindTo('bounds', map);
		        autocomplete.setTypes(["geocode"]);
		
		        autocomplete.addListener('place_changed', function() {
		        	 var place = autocomplete.getPlace();
		        	if(!!place.geometry) {
			            var lat = place.geometry.location.lat();
			            var lng = place.geometry.location.lng();
			           	map.setCenter(new google.maps.LatLng(lat, lng));
		        	}
		        });
	        })
	        
	        var source =
                {
                    datatype: "json",
                    datafields: [
                        { name: 'description' },
                        { name: 'value' }
                    ],
                    localdata: self.days,
                };
             var dataAdapter = new $.jqx.dataAdapter(source);
	        $("#pick-day").jqxComboBox({
		        source: dataAdapter,
		        displayMember: 'description',
		        valueMember: 'value',
		        theme:'energyblue',        
		        width: '170px',
		        height: '25px',
		        checkboxes:true
		    });
		    
		    $('#pick-day').on('close', function (event) { 
		    	var items = event.owner.listBox.items.filter(function(item) {return item.checked}).map(function(item) { return item.value });
		   		var route = self.data;
		   		var bin = {
					routeId: route.routeId,
					routeCode: route.routeCode,
					scheduleRoute: JSON.stringify(items),
					description: route.description,
					routeName: route.routeName,
					salesmanId: route.salesmanId,
				};
				Request.post("updateRoute", bin, function(res){
					$("#AddGoogleMapForm").notify("Cập nhật thành công",{position:"top right",className:"success"});
					
					route.scheduleRoute = items.map(function(item) {
						return {
							routeId: route.routeId,
							scheduleRoute: item
						}
					});
					$('#ListRoute').jqxGrid('updaterow', self.row, bin);
				});
		    });
		    
		    var contentInRouter = [
				{
					el: '${StringUtil.wrapString(uiLabelMap.BSRemoveInRoute)}',
					id:"remove-route", 
				},
				{
					el: '${StringUtil.wrapString(uiLabelMap.BSViewListRoutesOfACustomer)}',
					id:"view-visiting-route", 
				}
			];
			var actions = [{name: "itemclick", func: eventClickMenu}]
			
			createContextMenu("#menu-in-router", 150, contentInRouter, actions, "contextMenuInRoute");
		    
		    var contentOutRouter = [
				{
					el: '${StringUtil.wrapString(uiLabelMap.BSAddInRoute)}',
					id:"add-router", 
				},
				{
					el: '${StringUtil.wrapString(uiLabelMap.BSViewListRoutesOfACustomer)}', 
					id:"view-visiting-route", 
				}
			];
			
			google.maps.event.addListenerOnce(map, 'idle', function(){
	        	setTimeout(function(){
	        	var pickView =
	        	'<div class="gm-style-mtc" style="float: left; margin-left: -1px;">'+
					'<div class="dropdown-toggle" id="pick-view" data-toggle="dropdown" draggable="false" title="Show view map" style="direction: ltr; overflow: hidden; text-align: center; position: relative; color: rgb(0, 0, 0); font-family: Roboto, Arial, sans-serif; user-select: none; font-size: 11px; background-color: rgb(255, 255, 255); padding: 8px; border-bottom-right-radius: 2px; border-top-right-radius: 2px; -webkit-background-clip: padding-box; background-clip: padding-box; box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 4px -1px; border-left: 0px; min-width: 40px;">'+
						'View map'+
					'</div>'+
						createDropDown(self.pickViewSource,"pick-view")+
				'</div>';
	        
			    var pickView = $.parseHTML(pickView)[0];
	        	$(".gmnoprint")[4].appendChild(pickView);
				},1000);
			});
			
			createContextMenu("#menu-out-router", 150, contentOutRouter, actions, "contextMenuOutRoute");
			self.map = map;
		}
		
		//event click menu in right marker
		function eventClickMenu(evt) {
			var args = evt.args;
		    var itemId = $(args).attr('id');
	        switch (itemId) {
				case 'add-router':
					self.addCustomerAction();
					break;
				case 'remove-route':
					self.removeCustomerAction();
					break;
				case 'view-visiting-route':
					createTableVisitingRoutesOfACustomer();
					break;
			}
		
		}
		
		self.removeCustomerAction = function(){
			var bin = {
				routeId: self.currentRouteId,
				//parties: JSON.stringify([self.storeSelected.partyIdTo])
				parties: JSON.stringify([self.storeSelected.customerId])
			}
			Request.post("removeRouteStores", bin, function(res){
				if(!!res._ERROR_MESSAGE_) {
					$("#jqxNotification-updateError").jqxNotification("open");
				} else {
					$("#jqxNotification-updateSuccess").jqxNotification("open");
					var newMarker = self.updateStoreMarker(self.storeSelected, "ff0000", "storeOutRoute")
				
					var custionmerInRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-in-route";
					});
					var custionmerOutRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-out-route";
					});
					newMarker.customerId = self.storeSelected.customerId;
					var index = custionmerInRoute.markers.findIndex(function(mk){return mk.customerId==self.storeSelected.customerId})
					custionmerInRoute.markers[index].setMap(null);
					custionmerInRoute.markers.splice(index,1);
					custionmerOutRoute.markers.push(newMarker);
					
					
					self.updateNote();
				}
				
			});
		};
		
		self.addCustomerAction = function(){
			var bin = {
				routeId: self.currentRouteId,
				//parties: JSON.stringify([self.storeSelected.partyIdTo])
				parties: JSON.stringify([self.storeSelected.customerId])
			}
			Request.post("createRouteStores", bin, function(res){
				if(!!res._ERROR_MESSAGE_) {
					$("#jqxNotification-updateError").jqxNotification("open");
				} else {
					$("#jqxNotification-updateSuccess").jqxNotification("open");
					var newMarker = self.updateStoreMarker(self.storeSelected, "00bfff", "storeInRoute")
				
					var custionmerInRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-in-route";
					});
					var custionmerOutRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-out-route";
					});
					
					newMarker.customerId = self.storeSelected.customerId;
					var index = custionmerOutRoute.markers.findIndex(function(mk){return mk.customerId==self.storeSelected.customerId})
					custionmerOutRoute.markers[index].setMap(null);
					custionmerOutRoute.markers.splice(index,1);
					custionmerInRoute.markers.push(newMarker);
					
					self.updateNote();
				}
			});
		};
		
		self.createNote = function(els) {
			var note = document.createElement("div");
			note.id = "note";
			
			//Add title
			var h3 = document.createElement('h3');
			h3.innerHTML = "${uiLabelMap.BSNote}";
			note.appendChild(h3);
			
			els.forEach(function(el) {
				note.appendChild(el);
			})
			self.note = note;
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
		}
		
		function createContextMenu(id, width, contents, actions, name) {
			var contextMenu = document.createElement("div");
			self.contextMenuId = id;
			
			contextMenu.id = id.slice(1);
			document.getElementById("popup-manager-customer").appendChild(contextMenu);
			var html = createElsArray(contents);
			$(id).append(html);
			
			self[name] = $(id).jqxMenu({ width: width, autoOpenPopup: false, mode: 'popup'});
			if(!!actions) {
				actions.forEach(function(action, index) {
					self[name].on(action.name, function(event) {
						action.func(event);
					});
				})
			}
		}
		
		function createTableVisitingRoutesOfACustomer() {
			Request.post("getVisitingRoutesOfACustomer", {partyId : self.storeSelected.customerId}, function(res) {
				if (!!res.routes) {
					var tableVisitingRoutesOfCustomer = $.parseHTML('<div id="view-visiting-routes-of-customer">'+
												'<div id="table-visiting-routes-of-customer"></div>'+
												'<button onClick="CustomerRoute.closeTableVisitingRoutesOfCustomer()" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>'+
											'</div>')[0];
			
					var source = {
					    datatype: "json",
					    datafields: [ {name: "routeId"}, {name: "routeCode"}, {name: "routeName"}, {name: "description"}, {name: "scheduleRoute"}],
					    localdata: res.routes
					};
					var dataAdapter = new $.jqx.dataAdapter(source);
					
					self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
					self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(tableVisitingRoutesOfCustomer);
					self.tableVisitingRoutesOfCustomer = tableVisitingRoutesOfCustomer;
					cellsrenderer = function(row, column, value, a, b, data){
						var val;
						switch(column) {
						    case "scheduleRoute":
                                if(!!value) {
                                    var regexp = /\w+/gi;
                                    var matches = value.match(regexp);
                                    var dayWeekConverted = matches.map(function (v,i,a){
                                        return dayMap[v];
                                    });
                                    dayWeekConverted.sort();
                                    val = dayWeekConverted.join(", ");
                                } else {
                                    val = "";
                                }
                                break;
						    default:
						    	val = value;
						}
				        return '<div class="jqx-grid-cell-left-align" style="margin-top: 4px;" title="'+val+'">'+val+'</div>';
					}
				
					$("#table-visiting-routes-of-customer").jqxGrid(
			            {
			                width: 700,
			                pageable: true,
			                autoshowloadelement:true,
			    			autoheight: true,
			                source: dataAdapter,
			                columnsresize: true,
			                theme: "olbius",
			                columns: [
			                  { text: '${StringUtil.wrapString(uiLabelMap.BsRouteId)}', datafield: 'routeCode', width: 140, cellsrenderer: cellsrenderer },
			                  { text: '${StringUtil.wrapString(uiLabelMap.BSRouteName)}', datafield: 'routeName', width: 170, cellsrenderer: cellsrenderer},
			                  { text: '${StringUtil.wrapString(uiLabelMap.BSDescription)}', datafield: 'description', width: 160, cellsrenderer: cellsrenderer },
			                  { text: '${StringUtil.wrapString(uiLabelMap.BSScheduleDescription)}', datafield: 'scheduleRoute', width: 'auto', cellsrenderer: cellsrenderer }
			                ]
					});
					var localizestrings = getLocalization()
					$("#table-visiting-routes-of-customer").jqxGrid('localizestrings', localizestrings);
				}
			});
		}
		
		self.closeTableVisitingRoutesOfCustomer = function() {
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			
			if(!!self.note) {
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(self.note);
			}
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
		
		
		function createIcon(color) {
			return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.8';
		}
		
		self.open = function(row) {
			if(!self.map) {
				setTimeout(function(){self.initMap()}, 100);
			}
			
			self.tableStoreNoLocation = undefined;
			self.note = undefined;
			self.dataNoLacation = [];
			var grid = $('#ListRoute');
			var data = grid.jqxGrid('getrowdata', row);
			Popup.appendHeader(self.popup, "(" + data.routeCode + ") " + data.routeName);
			self.popup.jqxWindow('open');
			self.data = data;
			self.row = row;
			
			self.pickViewSource = [
	        	{
	        		name: "Show customer in route",
	        		selected: true,
	        		id: "show-customer-in-route",
	        		markers: []
	        	},
	        	{
	        		name: "Show customer out route",
	        		selected: true,
	        		id: "show-customer-out-route",
	        		markers: []
	        	},
	        	{
	        		name: "Show customer on another route",
	        		selected: true,
	        		id: "show-customer-on-another-route"
	        	}
	        ];
	        
	        //Reset view
	        self.pickViewSource.forEach(function(view) {
	        	$("#"+view.id).css("display", view.selected?"block":"none");
	        })
			
			if (!!self.popup) {
				setTimeout(function(){
					/*google.maps.event.addListenerOnce(self.map, 'idle', function(){
			        	self.currentRouteId = data.routeId;
						
						self.popup.jqxWindow('open');
						self.cleanStoreMarker();
						self.getAllCustomerInRoute(data.routeId);
						self.updateSchedule(data);
						//self.setFilterCondition(data.routeId);
						//self.RouteGrid.jqxGrid('updatebounddata');
						//self.RouteGrid.data('selected', []);
						//self.NoRouteGrid.data('selected', []);
						//self.updateRoute();
					});*/
                    self.currentRouteId = data.routeId;
                    self.popup.jqxWindow('open');
                    self.cleanStoreMarker();
                    self.getAllCustomerInRoute(data.routeId);
                    self.updateSchedule(data);
				}, 200);
				
				
			}
		};
		
		self.updateSchedule = function(data) {
			$("#pick-day").jqxComboBox('uncheckAll');
            //parse scheduleRoute
            var sR = [];
            var selectDate = [];
            if (OlbCore.isNotEmpty(data.scheduleRoute)) {
                sR = data.scheduleRoute.match(/\w+/gm);
                $.each(sR, function (i, v) {
                    selectDate.push(v);
                });
            }
            //
            selectDate.forEach(function(value, index){
				$("#pick-day").jqxComboBox('checkIndex', findIndexJqxCombobox(value));
			})
		};
		
		function findIndexJqxCombobox(schedule) {
			return days.findIndex(function(day, index) {
				return day.value==schedule
			})
		}
		
		self.getAllCustomerInRoute = function(routeId) {
			if (!routeId)
				return;
			Request.post("getAllCustomerInRouteWithLatLng", {routeId : routeId}, function(res) {
				if (res.results) {
					var stores = res.results;
					self.stores = res.results;

					if (!!self.map) {
						var storesId = res.results.map(function(store) {
							return store.customerId;
						})
						self.drawStoreMarker(res.results, "00bfff", "storeInRoute");
						if(self.data.salesmanId){
							Request.post("getCustAvailableBySalesman", {salesmanId : self.data.salesmanId, routeId : self.data.routeId}, function(res) {
								if (res.customers) {
									var storeDiff = res.customers.filter(function(store) {
										return storesId.indexOf(store.partyIdTo) == -1
									})
									self.drawStoreMarker(storeDiff, "ff0000", "storeOutRoute");
									self.updateNote();
									document.getElementById("store-no-location").innerHTML = "${uiLabelMap.BSViewStoreNoLocation}".replace("&#x24;1", self.dataNoLacation.length);
									
								}
							});
						}	
						
					}else{
						alert('khong co map');
					}
				}
			});
		};
		
		self.updateNote = function() {
			var contentsNote = [
				{
					content: "${uiLabelMap.BSInRoute} (" + self.pickViewSource[0].markers.length + ")",
					icon: createIcon("00bfff")
				},
				{
					content: "${uiLabelMap.BSOutRoute} (" + self.pickViewSource[1].markers.length + ")",
					icon: createIcon("ff0000")
				}
			]
			var els = [];
			contentsNote.forEach(function(c, index) {
				var div = document.createElement('div');
				var name = c.content;
				div.innerHTML = '<img src="' + c.icon + '"> ' + name;
				els.push(div);
			})
		
			
			self.createNote(els);
		
		}
		
		self.drawStoreMarker = function(stores, color, type) {
			if(stores.length==0) {
				return ;
			}
			
			var bounds = new google.maps.LatLngBounds();
			
			
			stores.forEach(function(store, i) {
				var latitude = store.latitude,
				longitude = store.longitude;
				if(!!latitude && typeof latitude == "number" && latitude > -360 && latitude < 360 &&
					!!longitude && typeof longitude == "number" && longitude > -360 && longitude < 360) {
					var marker = new google.maps.Marker({
						position: new google.maps.LatLng(store.latitude, store.longitude),
						draggable: false,
						icon: createIcon(color),
						map: self.map
				   	});
				   	marker.customerId = store.customerId;
				   	var name = !!store.groupName?store.groupName:store.partyName;
				   	var adress = !!store.address1?store.address1:store.postalAddressName;
				   	var html = "<i class='fa fa-tag'></i>&nbsp;" + store.customerId + "</br>"
				   		+"<i class='fa fa-home'></i>&nbsp;" + name + "</br>"
						+ "<i class='fa fa-map-marker'></i>&nbsp;" + adress;
					var title = name + " - " + adress;
					   	
				   	var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="'+title+'" style="width:250px!important;color:#000">' + html + '</div>'});
				   	
				   	marker.addListener('click', function(evt) {
				  		infoWindow.open(self.map, marker);
					});
					
					switch(type) {
					    case "storeInRoute":
					    	var inRoute = self.pickViewSource.find(function(item) {
								return item.id=="show-customer-in-route";
							})
							if(!!inRoute) {
								inRoute.markers.push(marker)							
							}
					        break;
					    case "storeOutRoute":
					    	var outRoute = self.pickViewSource.find(function(item) {
								return item.id=="show-customer-out-route";
							});
							if(!!outRoute) {
								outRoute.markers.push(marker)							
							}
					        break;
					}
				   	
				   	marker.addListener('rightclick', function(evt) {
				   		self.storeSelected = store;
				   		switch(type) {
						    case "storeInRoute":
						        self.contextMenuInRoute.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
						        break;
						    case "storeOutRoute":
						        self.contextMenuOutRoute.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
						        break;
						}
	                    
				  	});
				   	
				   	bounds.extend(marker.position)
				   	self.markers.push(marker);
				} else {
					self.dataNoLacation.push(store)
				}
			})
			self.map.fitBounds(bounds);
		}
		
		function createTableStoreNoLocation() {
			var tableStoreNoLocaton = $.parseHTML('<div id="view-store-no-location">'+
												'<div id="table-store-no-location"></div>'+
												'<button onClick="CustomerRoute.closeTableStoreNoLocation()" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>'+
											'</div>')[0];
			
			var source = {
			    datatype: "json",
			    datafields: [ {name: "customerCode"}, {name: "customerId"}, {name: "customerName"}, {name: "postalAddressName"}, {name: "sequenceNum"}, {name: "routeId"}, {name: "visitFrequencyTypeId"}],
			    localdata: self.dataNoLacation
			};
			var dataAdapter = new $.jqx.dataAdapter(source);
			
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(tableStoreNoLocaton);
			self.tableStoreNoLocation = tableStoreNoLocaton;
			cellsrenderer = function(row, column, value, a, b, data){
				var val;
				switch(column) {
                    case "customerCode":
						if (OlbCore.isNotEmpty(value)){
						    val = value;
						} else {
							val = self.dataNoLacation[row].partyCode;
						}
                        break;
					case "customerName":
                        if (OlbCore.isNotEmpty(value)){
                            val = value;
                        } else {
                            val = self.dataNoLacation[row].fullName;
                        }
                        break;
				    /*case "partyName":
				    	val = value||data.groupName;
				        break;
				    case "postalAddressName":
				    	val = value||[data.address1, data.city].join(", ");
				    	break;*/
				    default:
				    	val = value;
				}
		        return '<div class="jqx-grid-cell-left-align" style="margin-top: 4px;" title="'+val+'">'+val+'</div>';
			}
		
		$("#table-store-no-location").jqxGrid(
            {
                width: 520,
                pageable: true,
                autoshowloadelement:true,
    			autoheight: true,
                source: dataAdapter,
                columnsresize: true,
                theme: "olbius",
                columns: [
                  { text: '${uiLabelMap.BSCustomerId}', datafield: 'customerCode', width: 120, cellsrenderer: cellsrenderer },
                  { text: '${uiLabelMap.BSCustomerName}', datafield: 'customerName', width: 200, cellsrenderer: cellsrenderer},
                  { text: '${uiLabelMap.Address}', datafield: 'postalAddressName', width: 'auto', cellsrenderer: cellsrenderer }
                ]
            });
            var localizestrings = getLocalization()
			$("#table-store-no-location").jqxGrid('localizestrings', localizestrings);
		}
		
		self.closeTableStoreNoLocation = function() {
			self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			
			if(!!self.note) {
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(self.note);
			}
		}
		
		self.showStoreNoLocation = function() {
			if(!self.tableStoreNoLocation) {
				createTableStoreNoLocation()
			} else {
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
				self.map.controls[google.maps.ControlPosition.TOP_RIGHT].push(self.tableStoreNoLocation);
			}
		}
		
		self.updateStoreMarker = function(store, color, type) {
			var latitude = store.latitude,
				longitude = store.longitude;
			var marker;
			if(!!latitude && typeof latitude == "number" && latitude > -360 && latitude < 360 &&
				!!longitude && typeof longitude == "number" && longitude > -360 && longitude < 360) {
				var index = self.markers.findIndex(function(mk){return mk.customerId==store.customerId})
			
				if(index !== -1) {
					self.markers.splice(index,1);
				}
			
				marker = new google.maps.Marker({
					position: new google.maps.LatLng(store.latitude, store.longitude),
					draggable: false,
					icon: createIcon(color),
					map: self.map
			   	});
			   	
			   	var name = !!store.groupName?store.groupName:store.partyName;
			   	var adress = !!store.address1?store.address1:store.postalAddressName;
			   	var html = "<i class='fa fa-tag'></i>&nbsp;" + store.customerId + "</br>"
			   		+"<i class='fa fa-home'></i>&nbsp;" + name + "</br>"
					+ "<i class='fa fa-map-marker'></i>&nbsp;" + adress;
				var title = name + " - " + adress;
				   	
			   	var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="'+title+'" style="width:250px!important;color:#000">' + html + '</div>'});
			   	
			   	marker.addListener('click', function(evt) {
			  		infoWindow.open(self.map, marker);
				});
			   	
			   	marker.addListener('rightclick', function(evt) {
			   		self.storeSelected = store;
			   		switch(type) {
					    case "storeInRoute":
					        self.contextMenuInRoute.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
					        break;
					    case "storeOutRoute":
					        self.contextMenuOutRoute.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
					        break;
					}
                    
			  	});
			   	
			   	self.markers.push(marker);
			}
			return marker;
		}
		
		self.cleanStoreMarker = function() {
			if(!!self.markers&&self.markers.length>0) {
				self.markers.forEach(function(marker) {
					marker.setMap(null)
				})
				self.markers = [];
				self.bounds = new google.maps.LatLngBounds();
			}
		}
		
		self.initForm = function() {
			self.popup = $('#AddGoogleMapForm');
			self.popup.jqxWindow({
				width : 1100,
				maxWidth : 1100,
				resizable: false,
				height : 545,
				isModal : true,
				autoOpen : false,
				modalOpacity : 0.7,
				theme : theme,
				initContent : function() {
					//initGridListCustomerDistributor();
					//self.initElement();
				}
			});
			
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
			
			self.popup.on('open', function(){
				//document.body.style.overflowY = "hidden";
			});
			
			self.popup.on('close', function(){
				//document.body.style.overflowY = "scroll";
			});
		};
		
		
		self.changeView = function(id) {
			var view = self.pickViewSource.find(function(item) {
				return item.id==id;
			})
			
			view.selected = !view.selected;
			$("#"+view.id).css("display", view.selected?"block":"none");
			
			switch(view.id) {
			    case "show-customer-in-route":
			        if(view.selected) {
						view.markers.forEach(function(marker) {
							marker.setMap(self.map);
						}) 
					} else {
						view.markers.forEach(function(marker) {
							marker.setMap(null);
						}) 
					}
			        break;
			    case "show-customer-out-route":
			    	var viewOutNoneRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-on-another-route";
					});
			        if(view.selected) {
			        	if(!viewOutNoneRoute.selected) {
			        		if(!!self.customerNoneRoute) {
								view.markers.filter(function(marker) {
									return !!self.customerNoneRoute.find(function(customer){return customer.customerId === marker.customerId });
								}).forEach(function(marker) {
									marker.setMap(self.map);
								})
							}
			        	} else {
							view.markers.forEach(function(marker) {
								marker.setMap(self.map);
							})			        	
			        	}
					} else {
						view.markers.forEach(function(marker) {
							marker.setMap(null);
						})
					}
			        break;
			    case "show-customer-on-another-route":
			    	var viewOutRoute = self.pickViewSource.find(function(item) {
						return item.id=="show-customer-out-route";
					});
			    	if(view.selected) {
			    		if(viewOutRoute.selected) {
					    	viewOutRoute.markers.forEach(function(marker) {
					    		marker.setMap(self.map)
					    	})
			    		}
			    	} else {
			    		Request.post("getCustomerNoneRoute", {salesmanId : self.data.employeeId[0].partyCode}, function(res) {
							self.customerNoneRoute = res.customers;
							
							if(!!self.customerNoneRoute) {
								viewOutRoute.markers.filter(function(marker) {
									return !self.customerNoneRoute.find(function(customer){return customer.customerId === marker.customerId });
								}).forEach(function(marker) {
									marker.setMap(null);
								})
							}
						});
			    	}
			    	
			        
					
			        break;
			        
			}
			
		};

		return self;
	})();

    $(function() {
        // ensure that CustomerRoute init one time.
        if (typeof (CustomerRoute) != "undefined") {
            if (flagPopupLoadListOfCustomer) {
                CustomerRoute.initForm();
                flagPopupLoadListOfCustomer = false;
                setTimeout(function(){ flagPopupLoadListOfCustomer = true }, 300);
            }
        }
    });
	
		function createDropDown(items, id) {
			var el = items.map(function(item, index) {
				var checkbox = '<span role="checkbox" style="box-sizing: border-box; position: relative; line-height: 0; font-size: 0px; margin: 0px 5px 0px 0px; display: inline-block; background-color: rgb(255, 255, 255); border: 1px solid rgb(198, 198, 198); border-radius: 1px; width: 13px; height: 13px; vertical-align: middle;"><div id="'+item.id+'"style="position: absolute; left: 1px; top: -2px; width: 13px; height: 11px; overflow: hidden; '+(!!item.selected?null:"display: none;")+'"><img src="https://maps.gstatic.com/mapfiles/mv/imgs8.png" draggable="false" style="position: absolute; left: -52px; top: -44px; user-select: none; border: 0px; padding: 0px; margin: 0px; max-width: none; width: 68px; height: 67px;"></div></span>';
				var htmlItem = '<li role="presentation" onClick=CustomerRoute.changeView("'+item.id+'")><a class="checkbox" role="menuitem" tabindex="-1" href="#">'+
						   checkbox + item.name+
					  '</a></li>';
				return htmlItem;
			})
			
	        	var html =
					'<ul class="dropdown-menu" role="menu" aria-labelledby="'+id+'" style="left: auto; position: absolute; min-width: 40px; padding: 0px;">'+
				      el.join("")+
				    '</ul>'+
				'</div>';
	        
			    return html;
		}
	
</script>
