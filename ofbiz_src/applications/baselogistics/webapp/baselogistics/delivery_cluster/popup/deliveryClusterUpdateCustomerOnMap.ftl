<div id="BindCustomerToClusterForm" class='hide'>
    <div>
    ${uiLabelMap.BLAddCustomerToCluster}
    </div>
    <div class="form-window-container" id="popup-manager-customer">
        <div id="jqxNotification-updateSuccess">C&#x1EAD;p nh&#x1EAD;t th&#xE0;nh c&#xF4;ng</div>
        <div id="jqxNotification-updateError">C&#x1EAD;p nh&#x1EAD;t th&#x1EA5;t b&#x1EA1;i</div>
        <div class='row-fluid'>
            <div id="googlemapCluster" style="height: 500px;">
            </div>
        </div>
    </div>
</div>
<style>
    #wrappertable-store-no-location #contenttable-store-no-location, #wrappertable-visiting-routes-of-customer #contenttable-visiting-routes-of-customer {
        border: 1px #CCC solid !important;
    }

    #googlemapCluster {
        border: 1px solid #d9d9d9;
        position: inherit !important;
    }

    #note {
        border-radius: 2px;
        background-color: #fff;
        box-shadow: 0 1px 3px rgba(0, 0, 0, .25) !important;
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

    .jqx-menu-popup-olbius {
        z-index: 999999 !important;
    }

    .jqx-menu-wrapper {
        z-index: 99999 !important;
    }

    .disable {
        background: #e6e6e6 !important;
        color: #000 !important;
        border: none !important;
        outline: none !important;
    }

    .disable:hover {
        background: #e6e6e6 !important;
        color: #000 !important;
        border: none !important;
        outline: none !important;
    }

    .row-fluid .notifyjs-wrapper::before {
        top: 60px;
    }

</style>

<script>
    var OlbDeliveryClusterCustomerOnMapObj = (function () {
        var self = {};
        self.pickViewSource;
        self.popup, self.RouteGrid;
        self.mapCluster;
        self.contextMenu;
        self.note;
        self.bounds;
        self.contextMenuId;
        self.stores = [];
        self.markers = [];
        self.data = [];
        self.storeSelected;
        self.positionMouse;
        self.contextMenuInCluster;
        self.contextMenuOutCluster;
        self.maxLat = -1000, self.minLat = 1000, self.maxLng = -1000, self.minLng = 1000;
        var eventCount = 0;
        var numberOfCustomerInsideCluster = 0;
        var numberOfCustomerOutsideCluster = 0;

        self.initMap = function () {
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
            map = new google.maps.Map(document.getElementById('googlemapCluster'), mapOptions);

            // Update position mouse
            google.maps.event.addDomListener(map.getDiv(), 'mousemove', function (e) {
                self.positionMouse = e;
            });

            // Event load
            google.maps.event.addListener(map, 'idle', function (e) {
                eventCount++;
                if (eventCount > 1) {
                    //filter customer by bounda map
                    updateBoundariesLatLng();
                    //load outside cluster
                    self.getCustomersAvailableInDeliveryCluster();
                }
            });

            var contentInCluster = [
                {
                    el: '${StringUtil.wrapString(uiLabelMap.BLRemoveCustomerFromCluster)}',
                    id: "remove-from-cluster",
                }
            ];
            var actions = [{name: "itemclick", func: eventClickMenu}];
            createContextMenu("#menu-in-cluster", 150, contentInCluster, actions, "contextMenuInCluster");
            var contentOutCluster = [
                {
                    el: '${StringUtil.wrapString(uiLabelMap.BLAddCustomerFromCluster)}',
                    id: "add-to-cluster",
                }
            ];
            createContextMenu("#menu-out-cluster", 150, contentOutCluster, actions, "contextMenuOutCluster");
            self.mapCluster = map;
        };

        //event click menu in right marker
        function eventClickMenu(evt) {
            var args = evt.args;
            var itemId = $(args).attr('id');
            switch (itemId) {
                case 'add-to-cluster':
                    self.addCustomerAction();
                    break;
                case 'remove-from-cluster':
                    self.removeCustomerAction();
                    break;
            }

        }

        self.removeCustomerAction = function () {
            var customerId = self.storeSelected.partyId ? self.storeSelected.partyId : self.storeSelected.customerId;
            var bin = {
                deliveryClusterId: self.currentDeliveryClusterId,
                parties: JSON.stringify([customerId])
            };
            Request.post("removeDeliveryClusterCustomers", bin, function (res) {
                if (!!res._ERROR_MESSAGE_) {
                    $("#jqxNotification-updateError").jqxNotification("open");
                } else {
                    $("#jqxNotification-updateSuccess").jqxNotification("open");
                    self.getCustomersInDeliveryCluster(self.currentDeliveryClusterId, false);
                }

            });
        };

        self.addCustomerAction = function () {
            var customerId = self.storeSelected.partyId ? self.storeSelected.partyId : self.storeSelected.customerId;
            var bin = {
                deliveryClusterId: self.currentDeliveryClusterId,
                parties: JSON.stringify([customerId])
            };
            Request.post("createDeliveryClusterCustomers", bin, function (res) {
                if (!!res._ERROR_MESSAGE_) {
                    $("#jqxNotification-updateError").jqxNotification("open");
                } else {
                    $("#jqxNotification-updateSuccess").jqxNotification("open");
                    self.getCustomersInDeliveryCluster(self.currentDeliveryClusterId, false);
                }
            });
        };

        self.createNote = function (els) {
            var note = document.createElement("div");
            note.id = "note";
            //Add title
            var h3 = document.createElement('h3');
            h3.innerHTML = "${uiLabelMap.BSNote}";
            note.appendChild(h3);

            els.forEach(function (el) {
                note.appendChild(el);
            })
            self.note = note;
            self.mapCluster.controls[google.maps.ControlPosition.TOP_RIGHT].clear();
            self.mapCluster.controls[google.maps.ControlPosition.TOP_RIGHT].push(note);
        }

        function createContextMenu(id, width, contents, actions, name) {
            var contextMenu = document.createElement("div");
            self.contextMenuId = id;

            contextMenu.id = id.slice(1);
            document.getElementById("popup-manager-customer").appendChild(contextMenu);
            var html = createElsArray(contents);
            $(id).append(html);

            self[name] = $(id).jqxMenu({width: width, autoOpenPopup: false, mode: 'popup'});
            if (!!actions) {
                actions.forEach(function (action, index) {
                    self[name].on(action.name, function (event) {
                        action.func(event);
                    });
                })
            }
        };

        function createElsArray(array) {
            var els = [];
            els.push("<ul>");
            array.forEach(function (value, index) {
                if (Object.prototype.toString.call(value) == "[object Array]") {
                    els.push(createElsArray(value));
                } else {
                    if (Object.prototype.toString.call(value) == "[object Object]") {
                        els.push(createElsObject(value));
                    }
                }
            });
            els.push("</ul>");
            return els.join("");
        }

        function createElsObject(object) {
            var children = "";
            if (!!object.children && Object.prototype.toString.call(object.children) == "[object Array]") {
                children = createElsArray(object.children);
            }

            var el = '<li ' + (!!object.id ? 'id="' + object.id + '"' : "") + '>' +
                    object.el +
                    children +
                    '</li>';

            return el;
        }


        function createIcon(color) {
            return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.8';
        }


        self.open = function (row) {
            if (!self.mapCluster) {
                setTimeout(function () {
                    self.initMap()
                }, 100);
            }
            self.note = undefined;
            var grid = $('#jqxgridDeliveryCluster');
            var data = grid.jqxGrid('getrowdata', row);
            Popup.appendHeader(self.popup, "(" + data.deliveryClusterCode + ") " + data.deliveryClusterName);
            self.popup.jqxWindow('open');
            self.data = data;
            self.row = row;

            self.pickViewSource = [
                {
                    name: "Show customer in cluster",
                    selected: true,
                    id: "show-customer-inside-cluster",
                    markers: []
                },
                {
                    name: "Show customer out cluster",
                    selected: true,
                    id: "show-customer-outside-cluster",
                    markers: []
                },
                {
                    name: "Show customer on another cluster",
                    selected: true,
                    id: "show-customer-on-another-route"
                }
            ];
            //Reset view
            self.pickViewSource.forEach(function (view) {
                $("#" + view.id).css("display", view.selected ? "block" : "none");
            });

            if (!!self.popup) {
                setTimeout(function () {
                    self.currentDeliveryClusterId = data.deliveryClusterId;
                    self.popup.jqxWindow('open');
                    self.cleanStoreMarker();
                    self.getCustomersInDeliveryCluster(data.deliveryClusterId, true);
                }, 300);
            }
        };

        self.getCustomersInDeliveryCluster = function (deliveryClusterId, isFirstTimeOpen) {
            self.cleanStoreMarker();
            if (!deliveryClusterId)
                return;
            Request.post("getCustomersInDeliveryCluster", {deliveryClusterId: deliveryClusterId}, function (res) {
                numberOfCustomerInsideCluster = 0;
                if (OlbCore.isNotEmpty(res.customers)) {
                    numberOfCustomerInsideCluster = res.customers.length;
                    if (!!self.mapCluster) {
                        self.drawCustomersMarker(res.customers, "00bfff", "customerInsideCluster", isFirstTimeOpen);
                    } else {
                        alert('Map error');
                    }
                    //filter customer by bounda map
                    updateBoundariesLatLng();
                    //in first time. this is loaded by map event
                    if (OlbCore.isNotEmpty(isFirstTimeOpen) && isFirstTimeOpen == true) {
                    } else {
                        self.getCustomersAvailableInDeliveryCluster();
                    }
                } else {
                    self.getCustomersAvailableInDeliveryCluster();
                }
            });
        };

        var updateBoundariesLatLng = function () {
            var bounds = self.mapCluster.getBounds();
            var ne = bounds.getNorthEast(); // LatLng of the north-east corner
            var sw = bounds.getSouthWest();
            self.minLat = sw.lat();
            self.maxLat = ne.lat();
            self.minLng = sw.lng();
            self.maxLng = ne.lng();
        };

        self.getCustomersAvailableInDeliveryCluster = function () {
            var mapInput = {};
            mapInput["deliveryClusterId"] = self.currentDeliveryClusterId;
            if (self.minLng <= self.maxLng && self.minLat <= self.maxLat) {
                mapInput["latLngBound"] = self.minLat + "_" + self.maxLat + "_" + self.minLng + "_" + self.maxLng;
            }
            Request.post("getCustomersAvailableInDeliveryCluster", mapInput, function (res) {
                numberOfCustomerOutsideCluster = 0;
                if (OlbCore.isNotEmpty(res.customers)) {
                    numberOfCustomerOutsideCluster = res.customers.length;
                    if (!!self.mapCluster) {
                        self.drawCustomersMarker(res.customers, "ff0000", "customerOutsideCluster", false);
                        self.updateNote();
                    } else {
                        alert('Map error');
                    }
                }
            });
        };

        self.updateNote = function () {
            var numberOutside = 0;

            numberOutside = numberOfCustomerOutsideCluster>99?"100+":numberOfCustomerOutsideCluster;
            var contentsNote = [
                {
                    content: "${uiLabelMap.BLInsideCluster} (" + numberOfCustomerInsideCluster + ")",
                    icon: OlbDeliveryClusterUtil.createIcon("00bfff"),
                    color: "00bfff"
                },
                {
                    content: "${uiLabelMap.BLOutsideCluster} (" + numberOutside + ")",
                    icon: OlbDeliveryClusterUtil.createIcon("ff0000"),
                    color: "ff0000"
                }
            ];
            var els = [];
            contentsNote.forEach(function (c, index) {
                var div = document.createElement('div');
                var name = c.content;
                innerHtml = "<div>" +
                        "<div style='height: 20px; width: 20px; display: inline-block'>" +
                        "<svg viewBox='0 0 360 360' ><path fill='#" + c.color + "' d='" + OlbDeliveryClusterUtil.getSvgIcon() + "'/></svg>" +
                        "</div>" +
                        "<span style='display: inline-block; padding-left: 5px'>" +
                        name + "" +
                        "</span>" +
                        "</div>"
                div.innerHTML = innerHtml;
                els.push(div);
            });
            self.createNote(els);
        };

        self.drawCustomersMarker = function (customers, color, type, flagBounds) {
            if (customers.length == 0) {
                return;
            }
            self.bounds = new google.maps.LatLngBounds();
            customers.forEach(function (customer, i) {
                var latitude = customer.latitude,
                        longitude = customer.longitude;
                if (!!latitude && typeof latitude == "number" && latitude > -360 && latitude < 360 &&
                        !!longitude && typeof longitude == "number" && longitude > -360 && longitude < 360) {
                    var marker = new google.maps.Marker({
                        position: new google.maps.LatLng(customer.latitude, customer.longitude),
                        draggable: false,
                        icon: OlbDeliveryClusterUtil.createIcon(color),
                        map: self.mapCluster
                    });
                    var name = customer.fullName;
                    var adress = customer.postalAddressName;
                    var code = customer.customerCode ? customer.customerCode : customer.partyCode;
                    var html = "<i class='fa fa-tag'></i>&nbsp;" + code + "</br>"
                            + "<i class='fa fa-home'></i>&nbsp;" + name + "</br>"
                            + "<i class='fa fa-map-marker'></i>&nbsp;" + adress;
                    var title = name + " - " + adress;

                    var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="' + title + '" style="width:250px!important;color:#000">' + html + '</div>'});

                    marker.addListener('click', function (evt) {
                        infoWindow.open(self.mapCluster, marker);
                    });

                    switch (type) {
                        case "customerInsideCluster":
                            var inCluster = self.pickViewSource.find(function (item) {
                                return item.id == "show-customer-inside-cluster";
                            })
                            if (!!inCluster) {
                                inCluster.markers.push(marker)
                            }
                            break;
                        case "customerOutsideCluster":
                            var outCluster = self.pickViewSource.find(function (item) {
                                return item.id == "show-customer-outside-cluster";
                            });
                            if (!!outCluster) {
                                outCluster.markers.push(marker)
                            }
                            break;
                    }

                    marker.addListener('rightclick', function (evt) {
                        self.storeSelected = customer;
                        switch (type) {
                            case "customerInsideCluster":
                                self.contextMenuInCluster.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
                                break;
                            case "customerOutsideCluster":
                                self.contextMenuOutCluster.jqxMenu('open', parseInt(self.positionMouse.pageX), parseInt(self.positionMouse.pageY));
                                break;
                        }

                    });
                    if (flagBounds) {
                        self.bounds.extend(marker.position);
                    }
                    self.markers.push(marker);
                }
            });
            if (flagBounds) {
                self.mapCluster.fitBounds(self.bounds);
            }
        };

        self.cleanStoreMarker = function () {
            if (!!self.markers && self.markers.length > 0) {
                self.markers.forEach(function (marker) {
                    marker.setMap(null)
                })
                self.markers = [];
                self.bounds = new google.maps.LatLngBounds();
            }
        };

        self.initForm = function () {
            self.popup = $('#BindCustomerToClusterForm');
            self.popup.jqxWindow({
                width: 1100,
                maxWidth: 1100,
                resizable: false,
                height: 545,
                isModal: true,
                autoOpen: false,
                modalOpacity: 0.7,
                theme: theme,
                initContent: function () {
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
        };

        return self;
    })();

    $(function () {
        if (typeof (OlbDeliveryClusterCustomerOnMapObj) != "undefined") {
            OlbDeliveryClusterCustomerOnMapObj.initForm();
        }
    });
</script>
