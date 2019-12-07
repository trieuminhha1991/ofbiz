
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<div id="jqxwindowStoresOnMap" style="display:none;">
	<div>${uiLabelMap.BSViewListRoutesAssigned}</div>
	<div>
		<div id='menu-change-router'>
	        <ul>
		        <li>Change Route
                    <ul id="content-route-change">
                    </ul>
                </li>
             </ul>
       </div>
		
		<div style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.Employee}:</div> <div class="salesmanInfo jqxwindowTitle" style="display: inline-block;"></div></div>
		<div id="omnibox">
			<button class="icon-side-bar" id="side-bar-map" onClick="openSideBar()" title="menu">
				<span class="fa fa-bars"></span>
			</button>
			<input id="pac-input-store" class="form-control" type="text" placeholder="Type city, zip or address here..">
			<button class="search-box-button" onClick="searchAdressStore()" title="search">
				<span class="fa fa-search"></span>
			</button>
		</div>
		
		<#include "sideBar.ftl"/>
		<div id="pick-view"> </div>
		<div id="note"></div>
		<div id="view-store-no-location" style="z-index:999!important; display: none">
			<div id="table-store-no-location">
			</div>
			<button onClick="closeTableStoreNoLocation()" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
		</div>
		<div id="map-stores" style="height: 405px;overflow-y: hidden;">
		</div>
		
		<div class="form-action row-fluid">
			<div class="span12">
				<button id="btnCloseStoresOnMap" class="btn btn-danger form-action-button pull-right"><i class="icon-remove"></i>${uiLabelMap.CommonClose}</button>
			</div>
		</div>
	</div>
</div>

<style>
	#pac-input-store {
		width: 250px;
   		margin: 0;
   		border: none;
		outline: none;
		background: none;
		height: 100%;
    	padding: 0px 10px;
	}
	
	#wrappertable-store-no-location #contenttable-store-no-location {
		border: 1px #CCC solid!important;
	}
	
	#map-stores {
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
      
      #view-store-no-location {
      	border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0,0,0,.25)!important;
        margin-right: 5px;
		margin-top: 5px;
      }
      
      #view-store-no-location button {
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
      
</style>

<script>
	var test = []
	var contextMenu
	$(document).ready(function() {
		ListStoresOnMap.init();
		$('.tree-toggle').click(function () {
			$(this).parent().children('.tree').toggle(200);
		});
		
		$(function(){
			$('.tree-toggle').parent().children('.tree').toggle(200); 
		})
	});
	
	var days= {
		"MONDAY": {
			color: "ff00ff",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
		"TUESDAY": {
			color: "40ff00",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
		"WEDNESDAY": {
			color: "00bfff",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
		"THURSDAY": {
			color: "0000ff",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
		"FRIDAY": {
			color: "ff8000",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
		"SATURDAY": {
			color: "ff0000",
			markers: [],
			show: "hidden",
			flightPlanCoordinates: [],
			flightPath: undefined
		},
        "SUNDAY": {
            color: "efff00",
            markers: [],
            show: "hidden",
            flightPlanCoordinates: [],
            flightPath: undefined
        }
	};
	var storeNoLocation;
	var infoWindow, positionMouse;
	var mapStore, storeSelected;
	var routes, note, tableStoreNoLocation;
	var pickViewSource = [
	        	{
	        		name: "Show line",
	        		id: "show-line"
	        	},
	        	{
	        		name: "Hidden line",
	        		selected: true,
	        		id: "hidden-line"
	        	},
	        	{
	        		name: "Show line road",
	        		id: "show-line-road"
	        	}
	        ];
	function initMapStore() {
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
			scaleControl: false,
			rotateControl: false,
			fullscreenControl: false,
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
		var map = new google.maps.Map(document.getElementById('map-stores'), mapOptions);
		mapStore = map;
        var search = document.getElementById('pac-input-store');
        var omnibox = document.getElementById('omnibox');
        
        var sideBar = document.getElementById('menu-side-bar');
        
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(omnibox);
        map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(sideBar);
		note = document.getElementById('note');
	    map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
		
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
        source = [];
        $("#pick-day").jqxComboBox({checkboxes: true, source: source, width: '170px', height: '25px'});
        
        
        google.maps.event.addListenerOnce(map, 'idle', function(){
        	var pickView =
        	'<div class="gm-style-mtc" style="float: left; margin-left: -1px;">'+
				'<div class="dropdown-toggle" id="pick-view" data-toggle="dropdown" draggable="false" title="Show view map" style="direction: ltr; overflow: hidden; text-align: center; position: relative; color: rgb(0, 0, 0); font-family: Roboto, Arial, sans-serif; user-select: none; font-size: 11px; background-color: rgb(255, 255, 255); padding: 8px; border-bottom-right-radius: 2px; border-top-right-radius: 2px; -webkit-background-clip: padding-box; background-clip: padding-box; box-shadow: rgba(0, 0, 0, 0.3) 0px 1px 4px -1px; border-left: 0px; min-width: 40px;">'+
					'View map'+
				'</div>'+
					createDropDown(pickViewSource,"pick-view")+
			'</div>';
        
		    var pickView = $.parseHTML(pickView)[0];
        	$(".gmnoprint")[4].appendChild(pickView);
		});
        
        var changeRouteEl = document.getElementById("content-route-change");
  		var keys = Object.keys(days);
  		keys.forEach(function(k, index) {
  			var icon = createIcon(days[k].color);
  			
  			var li = document.createElement('li');
  			li.id = k;
  			li.innerHTML = '<img src="' + icon + '"> ' + k;
  			
  			changeRouteEl.appendChild(li);
  		})
        contextMenu = $("#menu-change-router").jqxMenu({ width: 150, autoOpenPopup: false, mode: 'popup'});
		
		$("#menu-change-router").on('itemclick', function (event) {
            var args = event.args;
        	var itemId = $(args).attr("id");
        	var keys = Object.keys(days);
        	for(var i = 0, len = keys.length; i < len; ++i) {
        		var key = keys[i];
        		if(key == itemId) {
        			changeRoute(storeSelected, itemId);
        			break;
        		}
        	}
        });
	}
	
	var ListStoresOnMap = (function() {
		var map = mapStore;
		var initJqxElements = function() {
			$("#jqxwindowStoresOnMap").jqxWindow({
				theme: "olbius", maxWidth: 1000, width: 1000, height: 560, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#btnCloseStoresOnMap"), modalOpacity: 0.7
			});
		};
		var open = function(salesman) {
			openLoader()
			var wtmp = window;
	    	var tmpwidth = $("#jqxwindowStoresOnMap").jqxWindow("width");
	        $("#jqxwindowStoresOnMap").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	$("#jqxwindowStoresOnMap").jqxWindow("open");
	    	if(!mapStore) {
		    	initMapStore();
	    	}
	    	
	    	storeNoLocation = [];
	    	var map = mapStore;
	    	$.ajax({
	    		url: "getRouteDaySalesman",
	    		type: "post",
	    		data: {
	    			salesmanId: salesman.partyId
	    		},
	    		"content-type": "application/json",
	    		success: function(data) {
	    			closeLoader();
	    			routes = data.routes;
	    			var keys = Object.keys(days);
	    			var bounds = new google.maps.LatLngBounds();
					$.each(routes, function(key, r) {
	    				clearMarkers(key);
	    				var color = days[key].color;
	    				var markers = days[key].markers;
	    				days[key].show = "show";

	    				var flightPlanCoordinates = days[key].flightPlanCoordinates;
	    				r.forEach(function(ro, i) {
	    					if(!!ro.geoPointId) {
		    					var marker = new google.maps.Marker({
									position: new google.maps.LatLng(ro.latitude, ro.longitude),
									draggable: false,
									icon: createIcon(color),
									map: map
							   	});
							   	flightPlanCoordinates.push({lat: ro.latitude, lng: ro.longitude})
							   	
							   	var html = '';
								html += '<b>Store:</b> ' + ro.customerId;
								html += '<br><b>Address:</b> ' + ro.postalAddressName;
								// html += '<br><b>Phone:</b> ' + ro.telecomName;
								html += '<br><small>' + '<i class="ti ti-location-pin"></i> Latitude: ' + ro.latitude.toString().substr(0, 10) + ' &nbsp; Longitude: ' + ro.longitude.toString().substr(0, 10) + '</small><br>';
								
								var infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width:250px!important;color:#000'>" + html + "</div>"});
								
								marker.addListener('click', function(evt) {
							  		infoWindow.open(map, marker);
							  	});
								/**
							  	marker.addListener('rightclick', function(evt) {
							  		Array.from($("#content-route-change").children()).forEach(function(el, index) {
							  			el.classList.remove("disable");
							  			if(key === el.id) {
							  				el.classList.add("disable");
										}
										storeSelected = ro;	
										storeSelected.partyId = salesman.partyId;		  			
							  		})
				                    contextMenu.jqxMenu('open', parseInt(positionMouse.pageX), parseInt(positionMouse.pageY));
							  	});
							  	**/
							  	google.maps.event.addDomListener(map.getDiv(), 'mousemove', function(e){
									positionMouse = e;
								});
								
						        
							  	markers.push(marker);
							   	bounds.extend(marker.position);
	    					} else {
	    						var store = storeNoLocation.find(function(s) { return s.customerId == ro.customerId })
	    						if(!store) {
	    							storeNoLocation.push(ro);
	    						}
	    					}
	    				})
	    				days[key].flightPath = new google.maps.Polyline({
				          path: flightPlanCoordinates,
				          geodesic: true,
				          strokeColor: '#'+ color,
				          strokeOpacity: 1.0,
				          strokeWeight: 2
				        });
	    			})
	    			map.fitBounds(bounds);
	    			
	    			document.getElementById("store-no-location").innerHTML = "${uiLabelMap.BSViewStoreNoLocation}".replace("&#x24;1", storeNoLocation.length);
	    			
	    			$("#pick-day").jqxComboBox('clear');
	    			var source = [];
	    			if(map.controls[google.maps.ControlPosition.TOP_RIGHT].b.length !== 0 
	    				&& map.controls[google.maps.ControlPosition.TOP_RIGHT].b[0].id!=="note") {
	    				
	    				tableStoreNoLocation = map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
	    				if(!note) {
	    					note = document.getElementById('note');
	    				}
	    				if(!!tableStoreNoLocation) {
	    					tableStoreNoLocation.style.display = "none";
	    				} else {
	    					$("#view-store-no-location").css("display", "none");
	    				}
	    				note.style.display = "block";
	    				map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
	    			}
	    			if(!note) {
	    				note = document.getElementById('note');
	    			}
	    			Array.from(note.children).forEach(function(el) {
	    				el.remove()
	    			})
					
					keys = Object.keys(days);
					
					var h3 = document.createElement('h3');
					h3.innerHTML = "${uiLabelMap.BSNote}";
					note.appendChild(h3);
					keys.forEach(function(key, index) {
						var day = days[key];
						var div = document.createElement('div');
						var icon = createIcon(day.color);
						var name = key + ' (' + day.markers.length + ')';
						div.innerHTML = '<img src="' + icon + '"> ' + name;
						note.appendChild(div);
						var html = '<div id="item-selectbox"><img src="' + icon + '">' + name + '</div>';
						var title = name;
						$("#pick-day").jqxComboBox('addItem', {html: html, title: title, value: name});
					})
					
					//select all
					var keys = Object.keys(days);
					keys.forEach(function(key, index) {
						$("#pick-day").jqxComboBox('checkIndex', index);
					})
					
					$("#pick-day").on('checkChange', function (event) {
			            if (event.args) {
			            	var viewType = pickViewSource.find(function(s){return !!s.selected});
			                var item = event.args.item;
			                var value = item.value.replace(/\s*\(\w*\)/g,"");
			                var markers = days[value].markers;
			                if(!item.checked) {
			                	markers.forEach(function(marker, index) {
		                			marker.setMap(null);
			                	})
			                	if(!!days[value].flightPath) {
			                		days[value].flightPath.setMap(null)
			                	}
			                	days[value].show == "hidden"
			                } else {
			                	markers.forEach(function(marker, index) {
		                			marker.setMap(map);
			                	})
			                	if(!!days[value].flightPath && viewType.id == "show-line") {
			                		days[value].flightPath.setMap(map)
			                	}
			                	days[value].show == "show"
			                }
			            }
			        });
	    		}, 
	    		error: function(err) {
	    			closeLoader();
	    			console.log(err)
	    		}
	    	})
		}
		return {
			init: function() {
				initJqxElements();
			},
			open: open
		};
	})();
	
	function createIcon(color) {
		return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.8';
	}
	
	function searchAdressStore() {
		var map = mapStore;
		var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";
		var address = $("#pac-input-store").val();
		$.ajax({
			url: 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key='+key,
			type: "get",
			success: function(data){
				var location;
				if(data.status==="OK") {
					location =  data.results[0].geometry.location;
					map.setCenter(new google.maps.LatLng(location.lat, location.lng));
				} else {
					alert("Not found location")
				}
			},
			error: function(err){console.log(err)}
		})
	}
	
	$('#pac-input-store').keyup(function(e){
	    if(e.keyCode == 13)
	    {
	    	searchAdressStore();
	    }
	});
	
	function showStoreNoLocation() {
		var map = mapStore;
		var keys = Object.keys(routes);
		
		var source = {
		    datatype: "json",
		    datafields: [
		        { name: "partyIdTo" },
		        { name: "partyName" },
		        { name: "postalAddressName" },
		        { name: "telecomName" }
		    ],
		    localdata: storeNoLocation
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		
		if(map.controls[google.maps.ControlPosition.TOP_RIGHT].b.length !== 0 
			&& map.controls[google.maps.ControlPosition.TOP_RIGHT].b[0].id!=="view-store-no-location") {
			
			note = map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			if(!tableStoreNoLocation) {
				tableStoreNoLocation = document.getElementById('view-store-no-location');
			}
			if(!!note) {
				note.style.display = "none";
			} else {
				$("#note").css("display", "none");
			}
			tableStoreNoLocation.style.display = "block";
			map.controls[google.maps.ControlPosition.TOP_RIGHT].push(tableStoreNoLocation);
		}
		
		cellsrenderer = function(row, column, value, a, b, data){
	        return '<div class="jqx-grid-cell-left-align" style="margin-top: 4px;" title="'+value+'">'+value+'</div>';
		}
		
		$("#table-store-no-location").jqxGrid(
            {
                width: 500,
                pageable: true,
                autoshowloadelement:true,
    			autoheight: true,
                source: dataAdapter,
                columnsresize: true,
                theme: "olbius",
                columns: [
                  { text: '${uiLabelMap.BLProductStoreId}', datafield: 'partyIdTo', width: 100, cellsrenderer: cellsrenderer },
                  { text: '${uiLabelMap.routeName}', datafield: 'partyName', width: 200, cellsrenderer: cellsrenderer},
                  { text: '${uiLabelMap.Address}', datafield: 'postalAddressName', width: 100, cellsrenderer: cellsrenderer },
                  { text: '${uiLabelMap.fromContactMechPhone}', datafield: 'telecomName', width: 100, cellsalign: 'right', width: 'auto', cellsrenderer: cellsrenderer }
                ]
            });
            
		var localizestrings = getLocalization()
		$("#table-store-no-location").jqxGrid('localizestrings', localizestrings);
	}
	
	function closeTableStoreNoLocation() {
		var map = mapStore;
		if(map.controls[google.maps.ControlPosition.TOP_RIGHT].b.length !== 0 
			&& map.controls[google.maps.ControlPosition.TOP_RIGHT].b[0].id!=="note") {
			
			tableStoreNoLocation = map.controls[google.maps.ControlPosition.TOP_RIGHT].pop();
			if(!note) {
				note = document.getElementById('note');
			}
			if(!!tableStoreNoLocation) {
				tableStoreNoLocation.style.display = "none";
			} else {
				$("#view-store-no-location").css("display", "none");
			}
			note.style.display = "block";
			map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
		}
	}
	
	function offset(el) {
	    var rect = el.getBoundingClientRect(),
	    scrollLeft = window.pageXOffset || document.documentElement.scrollLeft,
	    scrollTop = window.pageYOffset || document.documentElement.scrollTop;
	    return { top: rect.top + scrollTop, left: rect.left + scrollLeft }
	}
	
	function clearMarkers(key) {
		var map = mapStore;
		if(!!days[key].flightPath) {
			days[key].flightPath.setMap(null)
		}
		days[key].flightPlanCoordinates = [];
		var markers = days[key].markers;
	    for (var i = 0; i < markers.length; i++) {
	      markers[i].setMap(null);
	    }
	    days[key].markers = [];
	  }
	  
	function changeRoute(store, route) {
		alert("Chức năng này sẽ hoàn thiện trong tương lai \n Bạn vui lòng vào Danh sách tuyến đường để điều chỉnh");
	}
	
	function createDropDown(items, id) {
		var map = mapStore;
		var el = items.map(function(item, index) {
			var checkbox = '<span role="checkbox" style="box-sizing: border-box; position: relative; line-height: 0; font-size: 0px; margin: 0px 5px 0px 0px; display: inline-block; background-color: rgb(255, 255, 255); border: 1px solid rgb(198, 198, 198); border-radius: 1px; width: 13px; height: 13px; vertical-align: middle;"><div id="'+item.id+'"style="position: absolute; left: 1px; top: -2px; width: 13px; height: 11px; overflow: hidden; '+(!!item.selected?null:"display: none;")+'"><img src="https://maps.gstatic.com/mapfiles/mv/imgs8.png" draggable="false" style="position: absolute; left: -52px; top: -44px; user-select: none; border: 0px; padding: 0px; margin: 0px; max-width: none; width: 68px; height: 67px;"></div></span>';
			var htmlItem = '<li role="presentation" onClick=changeView("'+item.id+'")><a class="checkbox" role="menuitem" tabindex="-1" href="#">'+
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
	
	function changeView(id) {
		var map = mapStore;
		pickViewSource.forEach(function(item, index) {
			if(item.id!==id) {
				$("#"+item.id).css("display", "none");
				item.selected = false;
			} else {
				item.selected=true;
				$("#"+item.id).css("display", "block");
			}
		})
		
		switch(id) {
		    case "show-line":
		    	$.each( days, function( key, day ) {
				  if(!!day.flightPath && day.show=="show") {
				  	day.flightPath.setMap(map);
				  }
				});
		        break;
		    case "hidden-line":
		    	$.each( days, function( key, day ) {
				  if(!!day.flightPath) {
				  	day.flightPath.setMap(null);
				  }
				});
				break;
		    case "show-line-road":
		        break;
		}
	}
</script>

