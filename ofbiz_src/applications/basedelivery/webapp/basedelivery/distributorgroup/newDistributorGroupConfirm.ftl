<div style="position:relative">
    <div class="row-fluid">
        <div class="form-horizontal form-window-content-custom label-text-left content-description"
             style="margin:10px">
            <div class="row-fluid">
                <div class="span6">
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BDProductStoreGroupName}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strProductStoreGroupName"></span>
                        </div>
                    </div>
                </div><!--.span6-->
                <div class="span6">
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BSDescription}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strDescription"></span>
                        </div>
                    </div>
                </div><!--.span6-->
            </div>
        </div><!-- .form-horizontal -->
    </div><!--.row-fluid-->
    <div class="row-fluid">
        <div class="span12">
        <#assign columnlistConfirm = "

				{text: '${StringUtil.wrapString(uiLabelMap.BSId)}', datafield: 'partyCode', width: '10%',
					cellsrenderer: function(row, column, value, a, b, data){
				        var link = 'DistributorDetail?me=distributorDSA&sub=distributorList&partyId=' + data.partyId;
				        return '<div style=\"margin:4px\"><a href=\"' + link + '\">' + value + '</a></div>';
					}
				},
				{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', datafield: 'groupName', minwidth: '20%'},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsTelecom)}', datafield: 'contactNumber', width: '10%', cellsalign: 'right', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsAddress)}', datafield: 'address1', width: '20%', sortable: false},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsEmail)}', datafield: 'emailAddress', width: '8%', sortable: false},
				{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId',  filtertype: 'checkedlist', width: '13%',
					cellsrenderer: function(row, column, value){
						if (orderStatusData.length > 0) {
							for(var i = 0 ; i < orderStatusData.length; i++){
    							if (value == orderStatusData[i].statusId){
    								return '<span title =\"' + orderStatusData[i].description +'\">' + orderStatusData[i].description + '</span>';
    							}
    						}
						}
						return '<span title=' + value +'>' + value + '</span>';
				 	},
				 	createfilterwidget: function (column, columnElement, widget) {
				 		if (orderStatusData.length > 0) {
							var filterDataAdapter = new $.jqx.dataAdapter(orderStatusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
									if (orderStatusData.length > 0) {
										for(var i = 0; i < orderStatusData.length; i++){
											if(orderStatusData[i].statusId == value){
												return '<span>' + orderStatusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
						}
		   			}
				},
			"/>

            <div id="jqxgridOrderConfirm" style="width: 100%"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    $(function () {
        if (typeof(dataSelected) == "undefined") var dataSelected = [];
        OlbQuotationConfirm.init();
    });
    var OlbQuotationConfirm = (function () {
        var init = function () {
            initElementComplex();
        };
        var initElementComplex = function () {
            var configProductList = {
                datafields: ${dataField},
                columns: [${columnlistConfirm}],
                useUrl: false,
                pageable: true,
                pagesize: 15,
                showtoolbar: false,
                width: '100%',
                bindresize: true,
                groupable: false,
                localization: getLocalization(),
                showtoolbar: true,
                showdefaultloadelement: true,
                autoshowloadelement: true,
                virtualmode: false,
            };
            new OlbGrid($("#jqxgridOrderConfirm"), dataSelected, configProductList, []);
        };
        return {
            init: init
        };
    }());
</script>
