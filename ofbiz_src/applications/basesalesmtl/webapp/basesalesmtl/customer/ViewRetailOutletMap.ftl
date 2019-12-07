<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/salesmtlresources/js/util/color-slicer.js"></script>
<@jqGridMinimumLib />
<#include "../loader/loader.ftl"/>
<#include "./popup/sideBarViewRetailOutletMap.ftl"/>
<script type="text/javascript">
	openLoader();
</script>
<div id="jqxNotification-updateSuccess">C&#x1EAD;p nh&#x1EAD;t th&#xE0;nh c&#xF4;ng</div>
<div id="jqxNotification-updateError">C&#x1EAD;p nh&#x1EAD;t th&#x1EA5;t b&#x1EA1;i</div>
	
<div id="map" style="width:100%;"></div>
<div id="omnibox">
	<button class="icon-side-bar" id="side-bar-map" onClick="openSideBar()" title="menu">
		<span class="fa fa-bars"></span>
	</button>
	<input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
	<button class="search-box-button" onClick="searchAdress()" title="search">
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
      
      #note h3 {
        margin-top: 0;
      }
      
      #note img, #item-selectbox img, .jqx-item img {
      	padding: 2px;
      	width: 20px;
        vertical-align: middle;
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

</style>

<script type="text/javascript">
	$(window).load(function() {
		closeLoader()
	});

	$(document).ready(function() {
		initMap();
	})

	var listSalesman;

	var markers = [], map, salesmans = [], salesmansForUpdateCustomer, noteEl, contextMenuChangeSalesMan, storeSelected, positionMouse, salesmanac;
	var color = [
		"ff0000",
		"ff8000",
		"ffff00",
		"bfff00",
		"00ffff",
		"0040ff",
		"8000ff",
		"ff00ff",
		"F0E68C",
		"008000",
		"20B2AA",
		"4682B4",
		"FFE4C4",
		"8B4513"
	]; //default
	
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
	
	function updateSalesman(newSalesman) {
		var salesman = salesmans.find(function(s) {
			return s.salesmanId == newSalesman.salesmanId;
		})
		if(!!salesman) {
			salesman.markers.push(newSalesman.marker);
		} else {
			salesmans.push({
				salesmanId: newSalesman.salesmanId,
				salesmanName: newSalesman.salesmanName,
				salesmanCode: newSalesman.salesmanCode,
				distributorId: newSalesman.distributorId,
				markers: [newSalesman.marker]
			})
		}
	}

	var hex = function(int) {
		var str = int.toString(16);
		return "00".substring(0, 2 - str.length) + str;
	}
	
	function initMap() {
		
		var height_map = window.innerHeight - Array.from($("#main-content").children()).reduce(function(acc, curr) {return acc + curr.offsetHeight}, 0) - 50;
		$("#map").height(height_map);
		var mapOptions = {
			//label: "",
			zoom: 8,
			//minZoom: 5, 
			//maxZoom: 16,
			center: new google.maps.LatLng(21.0056183, 105.8433475),
			
			//center: {lat: -34.397, lng: 150.644},
			//mapTypeControlOptions: {
			 //   style: google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
			 //   position: google.maps.ControlPosition.TOP_CENTER
			//},
			mapTypeId: google.maps.MapTypeId.ROADMAP
		};
				
		map = new google.maps.Map(document.getElementById('map'), mapOptions);
		
		google.maps.event.addListenerOnce(map, 'idle', function(){
        	//Add omnibox
        	var omnibox = document.getElementById('omnibox');
        	map.controls[google.maps.ControlPosition.TOP_LEFT].push(omnibox);
        	
        	//Add side bar
        	var sideBar = document.getElementById('menu-side-bar');
        	map.controls[google.maps.ControlPosition.BOTTOM_LEFT].push(sideBar);
        	
        	// Update position mouse
	        google.maps.event.addDomListener(map.getDiv(), 'mousemove', function(e){
				positionMouse = e;
			});
        	
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
        
        //set key enter on input search
        $('#pac-input').keyup(function(e){
		    if(e.keyCode == 13)
		    {
		    	searchAdress();
		    }
		});
		
		
		var marker;
		var bounds = new google.maps.LatLngBounds();
		//alert(bounds);
		
		<#assign index=0>
		<#list agents.results as agent>
			<#assign index=index+1>
			<#if agent.geoPointId??>
				marker${index} = new google.maps.Marker({
					position: new google.maps.LatLng(${agent.latitude?c}, ${agent.longitude?c}),
					map: map
				});
				var html${index} = '<i class="fa fa-tag"></i>&nbsp;${agent.partyId}</br>'
				+'<i class="fa fa-home"></i>&nbsp;<#if agent.fullName?exists>${agent.fullName}</#if></br>'
				+ '<i class="fa fa-map-marker"></i>&nbsp;<#if agent.postalAddressName?exists>${agent.postalAddressName}</#if>';
				marker${index}.partyId = "${agent.partyId}";
				marker${index}.partyName = "<#if agent.fullName?exists>${agent.fullName}</#if>";
				marker${index}.postalAddressName = "<#if agent.postalAddressName?exists>${agent.postalAddressName}</#if>";
				marker${index}.distributorId = "${agent.distributorId}";
				var infoWindow${index} = new google.maps.InfoWindow({content: "<div id='iw' style='width:250px!important;color:#000'>" + html${index} + "</div>"});

				marker${index}.addListener('click', function(evt) {
					infoWindow${index}.open(map, marker${index});
				});
			  	<#if Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_GT", userLogin, true)
				||Static["com.olbius.salesmtl.util.MTLUtil"].hasSecurityGroupPermission(delegator, "SALESSUP_MT", userLogin, true)>
				
				marker${index}.addListener('rightclick', function(evt) {
					storeSelected = marker${index};
					if(!!contextMenuChangeSalesMan) {
						contextMenuChangeSalesMan.jqxMenu('close');
					}
                    var contenSalesman = salesmans.filter(function(salesman){
                        return salesman.distributorId == storeSelected.distributorId;
                    }).map(function(salesman){
                        return {
                            el: '<img src="' + salesman.icon + '"> ' + salesman.salesmanName + " [" + salesman.salesmanCode + "]",
                            id: ['SALES',salesman.salesmanId].join("-")
                        }
                    });

                    var contentChangeSalesman = [
                        {
                            el: '${uiLabelMap.BSChangeSalesman}',
                            id:"change-salesman",
                            children: contenSalesman
                        }
                    ];
                    var actions = [{name: "itemclick", func: eventClickMenu}]

                    contextMenuChangeSalesMan = createContextMenu("#menu-change-salesman", 150, contentChangeSalesman, actions);

                    contextMenuChangeSalesMan.jqxMenu('open', parseInt(positionMouse.pageX), parseInt(positionMouse.pageY));
						



				});
				</#if>
				bounds.extend(marker${index}.position);
			   	markers.push(marker${index})

			   	var salesman${index} = {
					salesmanId: <#if agent.salesmanId??>"${agent.salesmanId}"<#else>undefined</#if>,
					salesmanName: <#if agent.salesmanName??>"${StringUtil.wrapString(agent.salesmanName)}"<#else>'${StringUtil.wrapString(uiLabelMap.BSNoneSalesmans)}'</#if>,
					salesmanCode: <#if agent.salesmanCode??>"${agent.salesmanCode}"<#else>'000000'</#if>,
					own: undefined,
                    distributorId: <#if agent.distributorId??>"${agent.distributorId}"<#else>'undefined'</#if>,
					marker: marker${index}
				}
				updateSalesman(salesman${index});
			</#if>
		</#list>
		//map.fitBounds(bounds);
		color=colorSlicer.getLchColors(salesmans.length + 1, 32, {l: 0, bright: false, unsafe: true}).map(function(lch) { var rgb = colorSlicer.lchToRgb(lch); return hex(rgb[0]) + hex(rgb[1]) + hex(rgb[2])});
		
		$.ajax({
			//url: 'getListSalesman',
			//url: 'getListSalesmanOfSupervisor',
			url: 'getListSalesmanManagedByUserLogin',
			type: "post",
			success: function(data){
				salesmanac = data.listSalesman;
				listSalesman = data.listSalesman;
				
				salesmanac.forEach(function(s) {
					var saleman = salesmans.find(function(v) { return v.salesmanId == s.partyId })
					if(!!saleman) {
						saleman.own = true;
					} else {
						salesmans.push({
							icon: "",
							salesmanId: s.partyId,
							salesmanCode: s.partyCode,
							salesmanName: s.fullName,
							distributorId: s.distributorId,
							own: true,
							markers: []
						})
					}
				})
				var els = [];
				
				salesmans.forEach(function(salesman, index) {
					salesman.icon = createIcon(color[index])
					var div = document.createElement('div');
					var name = salesman.salesmanName + " [" + salesman.salesmanCode + "] (" + salesman.markers.length + ") "+  (!!salesman.salesmanId?(!!salesman.own?"":"?"):"");
					div.innerHTML = '<img src="' + salesman.icon + '"> ' + name;
					els.push(div);
					salesman.markers.forEach(function(marker) {
						marker.setIcon(salesman.icon);
					})
				})
			
				createNote(els);
				
				 var source =
		        {
		            datatype: "array",
		            localdata: salesmans,
		        };
		        var dataAdapter = new $.jqx.dataAdapter(source);
		        $('#pick-salesman').jqxComboBox({
		        	source: dataAdapter,
			        theme:'energyblue',        
			        width: '220px',
			        height: '25px',
			        displayMember: "salesmanName",
			        valueMember: "salesmanCode",
			        checkboxes:true,
		            renderer: function (index, label) {
		            	var value = salesmans[index]
						var name = value.salesmanName + " [" + value.salesmanCode + "] (" + value.markers.length + ") " + (!!value.salesmanId?(!!value.own?"":"?"):"");
						var item = '<div id="item-selectbox" title="'+name+'"><img src="' + value.icon + '">' + name + '</div>'
		                return item;
		            }
		        });
		        
		        salesmans.forEach(function(salesman, index) {
			        $("#pick-salesman").jqxComboBox('checkIndex', index);
		        })
		        
		        $("#pick-salesman").on('checkChange', function (event) {
		            if (event.args) {
		                var item = event.args.item;
		                var salesman = item.originalItem;
		                if(item.checked) {
		                	salesman.markers.forEach(function(marker) {
		                		marker.setMap(map)
		                	})
		                } else {
		                	salesman.markers.forEach(function(marker) {
		                		marker.setMap(null)
		                	})
		                }
		            }
		        });
			},
			error: function(err){$("#jqxNotification-updateError").jqxNotification("open");}
		})
		
		
		
		
		
		
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
		map.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
		map.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
	}
	
	function createIcon(color) {
		return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.75';
	}
	
	function searchAdress() {
		var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";
		var address = $("#pac-input").val();
		//alert('address = ' + address);
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
	var test
	function eventClickMenu(evt) {
		test=evt;
		var args = evt.args;
	    var itemId = $(args).attr('id');
	    var [category, salesmanId] = itemId.split("-")
        switch (category) {
			case 'SALES':
				changeSalesman(salesmanId)
				break;
		}
	}
	
	function changeSalesman(salesmanId) {
		$.ajax({
			url: 'updateSalesmanProvideAgent',
			data: {partyIdTo: storeSelected.partyId,
					partyIdFrom: salesmanId},
			type: "post",
			success: function(data){
				$("#jqxNotification-updateSuccess").jqxNotification("open");
				var salesmanNew = salesmans.find(function(salesman){return salesman.salesmanId == salesmanId })
				for(var i = 0, len = salesmans.length; i < len; ++i) {
					var salesman = salesmans[i];
					var storeIndexOld = salesman.markers.findIndex(function(marker){ return marker.partyId ==  storeSelected.partyId})
					if(storeIndexOld !== -1) {
						var [storeOld] = salesman.markers.splice(storeIndexOld, 1);
						storeOld.setIcon(salesmanNew.icon);
						salesmanNew.markers.push(storeOld);
						updateNote();
						break;
					}
				}
			},
			error: function(err){$("#jqxNotification-updateError").jqxNotification("open");}
		})
	}
	
	function updateNote() {
		var els = [];
		salesmans.forEach(function(salesman, index) {
			if(!salesman.icon) {
			 salesman.icon = createIcon(color[index])
			}
			var div = document.createElement('div');
			var name = salesman.salesmanName + " [" + salesman.salesmanCode + "] (" + salesman.markers.length + ") " + (!!salesman.salesmanId?(!!salesman.own?"":"?"):"");
			div.innerHTML = '<img src="' + salesman.icon + '"> ' + name;
			els.push(div);
			salesman.markers.forEach(function(marker) {
				marker.setIcon(salesman.icon);
			})
		})
	
		createNote(els);
	}
	
</script>