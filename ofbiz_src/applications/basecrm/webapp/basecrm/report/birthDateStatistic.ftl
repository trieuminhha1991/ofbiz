<style>
	.olbiusChartContainer {
		margin-top: 50px!important;
	}
</style>
<script id="test">
$(function(){
	var regions = [];
    	var resources = [];
		var getRegions = function(){
			if(!regions.length){
				$.ajax({
					url: "autoCompleteGeoAjax?geoTypeId=SUBREGION&geoId=VNM",
					async: false,
					success: function(res){
						if(res.listGeo){
							regions = [];
							for(var x in res.listGeo){
								regions.push({
									value: res.listGeo[x].geoId,
									text: res.listGeo[x].geoName
								});
							}
						}
					}
				});
			}
			return regions;
		};
		var getResource = function(){
			if(!resources.length){
				$.ajax({
					url: "getDataResources",
					async: false,
					success: function(res){
						if(res.result){
							resources = [];
							for(var x in res.result){
								resources.push({
									value: res.result[x].dataSourceId,
									text: res.result[x].dataSourceId
								});
							}							
						}
					}
				});
			}
			return resources;
		};

        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BirthDateStatistic)}',
            service: 'dateOfBirth',
            columns: [
                { text: '${StringUtil.wrapString(uiLabelMap.BCRMRange1)}', datafield: 'Range1', type: 'number', width: '25%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BCRMRange2)}', datafield: 'Range2', type: 'number', width: '25%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BCRMRange3)}', datafield: 'Range3', type: 'number', width: '25%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BCRMRange4)}', datafield: 'Range4', type: 'number', width: '25%'},
            ]
        };

        var configPopup = [
        	{
                action : 'addDropDownList',
                params : [{
                    id : 'dataResourceId',
                    label : '${StringUtil.wrapString(uiLabelMap.ChooseResource)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(getResource()),
                    index: 0
                }]
            },
            {
                action : 'addDropDownList',
                params : [{
                    id : 'geoId',
                    label : '${StringUtil.wrapString(uiLabelMap.ChooseRegion)}',
                    data : [{'text': '${StringUtil.wrapString(uiLabelMap.olap_all)}', 'value': null}].concat(getRegions()),
                    index: 0
                }]
            }
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'getDateOfBirthReportGrid', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'dataSourceId': oLap.val('dataResourceId'),
                'geoId': oLap.val('geoId')
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        });
    });
</script>