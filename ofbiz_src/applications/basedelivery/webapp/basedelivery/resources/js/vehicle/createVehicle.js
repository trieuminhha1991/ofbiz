var productStoreDDL;

var CreateVehicle = (function () {
    var handleEvents = function () {
        $("#btnUpload").click(function () {
            Loading.show('loadingMacro');
            setTimeout(function () {
                var dataMap =
                    {
                        vehicleTypeId: $("#vehicleTypeId").val(),
                        loading: document.getElementById("loading").value,
                        volume: document.getElementById("volume").value,
                        reqNo: document.getElementById("reqNo").value,
                        licensePlate: document.getElementById("licensePlate").value,
                        description: document.getElementById("description").value,
                        width: document.getElementById("width").value,
                        height: document.getElementById("height").value,
                        longitude: document.getElementById("longitude").value,
                    }
                ;
                $.ajax({
                    type: 'POST',
                    url: 'createVehicle',
                    data: dataMap
                }).done(function () {
                    document.location.href = "getAllVehicle";
                });
            }, 300);
        });
    };


    var initElementComplex = function() {
        var configProductStore = {
            width: '30%',
            placeHolder: uiLabelMap.BDVehicleType,
            useUrl: true,
            url: 'jqxGeneralServicer?sname=JQGetListVehicleType',
            key: 'vehicleTypeId',
            value: 'name',
            autoDropDownHeight: true
        };
        productStoreDDL = new OlbDropDownList($("#vehicleTypeId"), null, configProductStore, []);
    };
    return {
        init: function () {
            initElementComplex();
            handleEvents();
        }
    };
})();