<div style="position:relative">
    <div class="row-fluid">
        <div class="form-horizontal form-window-content-custom label-text-left content-description"
             style="margin:10px">
            <div class="row-fluid">
                <div class="span6">
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BSRequiredByDate}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strRequiredByDate"></span>
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BSRequirementStartDate}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strRequirementStartDate"></span>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BDContractorId}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strContractorId"></span>
                        </div>
                    </div>
                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BDVehicleId}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strVehicleName"></span>
                        </div>
                    </div>

                    <div class="row-fluid">
                        <div class="div-inline-block">
                            <label>${uiLabelMap.BDTotalWeight}:</label>
                        </div>
                        <div class="div-inline-block">
                            <span id="strTotalWeightId"></span>
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
				{ text: '${StringUtil.wrapString(uiLabelMap.BDDeliveryId)}', dataField: 'deliveryId', pinned: true, width: '13%'},
				{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdFrom)}', dataField: 'partyIdFrom', cellClassName: cellClass,  width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
	        	  },
				{ text: '${StringUtil.wrapString(uiLabelMap.BDPartyIdTo)}', dataField: 'partyIdTo', cellClassName: cellClass,  width: '20%',cellsrenderer: function(row, column, value){
					  var partyName = value;
					  $.ajax({
							url: 'getPartyName',
							type: 'POST',
							data: {partyId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									partyName = data.partyName;
								}
					        }
						});
					  return '<span title' + value + '>' + partyName + '</span>';}
	        	  },
				{ text: '${StringUtil.wrapString(uiLabelMap.BDDesContactMechId)}', dataField: 'destContactMechId', cellClassName: cellClass, width: '20%',
				    cellsrenderer: function(row, column, value) {
				    var address = value;
					  $.ajax({
							url: 'getContactMechName',
							type: 'POST',
							data: {contactMechId: value},
							dataType: 'json',
							async: false,
							success : function(data) {
								if(!data._ERROR_MESSAGE_){
									address = data.fullName;
								}
					        }
						});
					  return '<span title' + value + '>' + address + '</span>';}},
			    { text: '${StringUtil.wrapString(uiLabelMap.BDTotalWeight)} (kg)', dataField: 'totalWeight', cellsformat: 'd', width: '10%'},
	    		{ text: '${uiLabelMap.BDDeliveryDate}', dataField: 'deliveryDate', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',
					cellsrenderer: function(row, column, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
					}
				},
				{ text: '${uiLabelMap.BDCreateDate}', dataField: 'createDate', cellClassName: cellClass, cellsformat: 'dd/MM/yyyy', filtertype:'range', width: '13%',
					cellsrenderer: function(row, colum, value) {
						return \"<span>\" + jOlbUtil.dateTime.formatFullDate(value) + \"</span>\";
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
