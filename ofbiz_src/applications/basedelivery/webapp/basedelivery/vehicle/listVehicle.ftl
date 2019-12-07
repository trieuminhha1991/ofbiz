<#assign localeStr = "VI" />

<#if locale = "en">
    <#assign localeStr = "EN" />
<#elseif locale= "en_US">
    <#assign localeStr = "EN" />
</#if>
<script>

    var locale = '${locale}';
    $(document).ready(function () {
        locale == "vi_VN" ? locale = "vi" : locale = locale;
    });

</script>
<#assign customcontrol2 = "fa fa-file-excel-o@@javascript: void(0);@exportExcel()">
<#--<#assign customcontrol3 = "fa fa-file-pdf-o@@javascript: void(0);@printVehicle()">-->


<script type="text/javascript">
    var exportExcel = function () {
        ;
        var form = document.createElement("form");
        form.setAttribute("method", "POST");
        form.setAttribute("action", "exportExcelVehicle");
        document.body.appendChild(form);
        form.submit();
    };

    var printVehicle = function () {
        var url = 'VehiclelList.pdf';
        var win = window.open(url, '_blank');
        win.focus();
    };
</script>

<div id="detailItems">
<#assign dataFieldVehicle="[
					{ name: 'vehicleId', type: 'string'},
					{ name: 'vehicleTypeId', type: 'string' },
					{ name: 'description', type: 'string'},
					{ name: 'licensePlate', type: 'string'},
					{ name: 'loading', type: 'string'},
					{ name: 'volume', type: 'String'},
					{ name: 'reqNo', type: 'String'},
					{ name: 'width', type: 'String'},
					{ name: 'height', type: 'String'},
					{ name: 'longitude', type: 'String'},
				]"/>
	<#assign columnlistVehicle="
					{
					    text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{
					    text: '${uiLabelMap.BDVehicleId}', datafield: 'vehicleId', align: 'left',
					        width: 150, pinned: true,width: '13%',
                        cellsrenderer: function(row, colum, value) {
                            return \"<span><a href='editVehicle?vehicleId=\" + value + \"'>\" + value +\"</a></span>\";}
                    },
					{   text: '${uiLabelMap.BDLoading} (${uiLabelMap.Ton})', datafield: 'loading', align:'left', width: '13%'},
					{   text: '${uiLabelMap.BDVolume} (${uiLabelMap.M3})', datafield: 'volume', align: 'left', width: '13%'},
					{   text: '${uiLabelMap.BDReqNo}', datafield: 'reqNo', align: 'left', width: '13%'},
					{   text: '${uiLabelMap.BDLicensePlate}', datafield: 'licensePlate', width: '13%', align:
					        'left'},
					{   text: '${uiLabelMap.BDVehicleType}', datafield: 'vehicleTypeId', align: 'left', width: '13%', cellsrenderer: function(row, column, value){
					    if(!value) return '<span> _NA_ </span>';
							  var name = value;
				    		  $.ajax({
				    				url: 'getVehicleTypeName',
				    				type: 'POST',
				    				data: {vehicleTypeId: value},
				    				dataType: 'json',
				    				async: false,
				    				success : function(data) {
				    					if(!data._ERROR_MESSAGE_){
				    						name = data.name;
				    					}
				    		        }
				    			});
				    		  return '<span title' + value + '>' + name + '</span>';
						}},
					{   text: '${uiLabelMap.BDDescription}', datafield: 'description', align: 'left', width: '13%'},
					{   text: '${uiLabelMap.BDWidth} (m)', datafield: 'width', align: 'left', width: '13%'},
					{   text: '${uiLabelMap.BDHeight} (m)', datafield: 'height', align: 'left', width: '13%'},
					{   text: '${uiLabelMap.BDLongitude} (m)', datafield: 'longitude', align: 'left', width: '13%'},
				"/>

	<@jqGrid filtersimplemode="true" filterable="true" dataField=dataFieldVehicle columnlist=columnlistVehicle editable="false" showtoolbar="true"
url="jqxGeneralServicer?sname=JQGetVehicle" customTitleProperties="BDListVehicle" groupable="true"
id="jqxgridVehicle" customcontrol1="icon-plus open-sans@${uiLabelMap
.AddNew}@javascript:ListVehicleObj.prepareCreate('prepareCreateNewVehicle');"
customcontrol2=customcontrol2
<#--customcontrol3=customcontrol3-->
/>
</div>
<script type="text/javascript" src="/deliresources/js/vehicle/vehicle.js"></script>