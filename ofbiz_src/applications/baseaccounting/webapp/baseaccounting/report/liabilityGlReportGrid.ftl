<div id="liabilityDetailWindow" style="display:none;">
    <div>${uiLabelMap.BACCSettingDetailLiabilityYear}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid'>
			<div id="liabilityDetailReportGrid">
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" id="liabilityReportGrid">
	$(document).ready(function(){
		bindingDataLiabilityReport();
	});
	
	var bindingDataLiabilityReport = function(){
		$('#liabilityDetailWindow').jqxWindow({
			width: 800, height: 150, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:'olbius'
		});
		
		var localData = [];
	    var columns = [];
	    var datafields = [];
	    var id = '';

		var config = {
                title: ' ${uiLabelMap.BACCReportLiabilityReport}',
                service: 'invoicePayment',
                columns: [
						{text: "", datafield:'currency', cellsalign: 'left', type: 'string', hidden: true},
						{text: "", datafield:'glAccountId', cellsalign: 'left', type: 'string', hidden: true},
                        {text: "${uiLabelMap.BACCLiabilityPartyId}", datafield:'partyId', cellsalign: 'left', type: 'string'},
                        {text: "${uiLabelMap.BACCLiabilityPartyName}", datafield:'partyName', cellsalign: 'left', type: 'string'},
                        {text: "${uiLabelMap.BACCLiabilityPaymentApplied}", datafield:'paymentApplied', cellsalign: 'right',width : '200',
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (data && data.paymentApplied){
           	     						return '<div style="text-align: right;">' + formatcurrency(data.paymentApplied, data.currency) + '</div>';
           	     					} else if(data !== undefined){
           	     						return '<div style="text-align: right;">' + formatcurrency(0, data.currency) + '</div>';
           	     					}else return '';
               					}   
                       	   	}
                        },                        
                        {text: "${uiLabelMap.BACCLiabilityTotalAmount}", datafield:'totalAmount', cellsalign: 'right',width : '200',
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (data && data.totalAmount){
           	     						return '<div style="text-align: right;">' + formatcurrency(data.totalAmount, data.currency) + '</div>';
	           	     				} else if(data !== undefined){
	       	     						return '<div style="text-align: right;">' + formatcurrency(0, data.currency) + '</div>';
	       	     					}else return '';
               					}   
                       	   	}
                        },
                        {text: "${uiLabelMap.BACCLiabilityAppliedAmount}", datafield:'appliedAmount', cellsalign: 'right',width : '200',
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (data && data.appliedAmount){
           	     						return '<div style="text-align: right;">' + formatcurrency(data.appliedAmount, data.currency) + '</div>';
	           	     				} else if(data !== undefined){
	       	     						return '<div style="text-align: right;">' + formatcurrency(0, data.currency) + '</div>';
	       	     					}else return '';
	               					}   
                       	   	} 
                        },
                        {text: "${uiLabelMap.BACCLibialityAmount}", datafield:'liabilityAmount', cellsalign: 'right',
                        	olapcellrenderer: function(olap){
                       			return function (row, column, value) {
                           		   	var data = olap.jqxGrid('getrowdata', row);
           	     					if (data && data.liabilityAmount){
           	     						return '<div style="text-align: right;">' + formatcurrency(data.liabilityAmount, data.currency) + '</div>';
	           	     				} else if(data !== undefined){
	       	     						return '<div style="text-align: right;">' + formatcurrency(0, data.currency) + '</div>';
	       	     					}else return '';
               					}   
                       	   	}  
                        }
                      ]
            };
		var dateCurrent = new Date();
		var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);				
        var configPopup = [
				{
				    action : 'addDropDownList',
				    params : [{
				        id : 'glAccountId',
				        label : '${StringUtil.wrapString(uiLabelMap.BACCGlAccountId)}',
				        data : glAccountData,
				        index: 0
				    }]
				},
				{
				    action : 'addDropDownList',
				    params : [{
				        id : 'partyId',
				        label : '${StringUtil.wrapString(uiLabelMap.BACCLiabilityPartyName)}',
				        data : supplierData,
				        index: 0
				    }]
				},
	            {
                    action : 'addDateTimeInput',
                    params : [{
                        id : 'from_date',
                        label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                        value: OLBIUS.dateToString(currentFirstDay)
                    }],
                    before: 'thru_date'
                },
                {
                    action : 'addDateTimeInput',
                    params : [{
                        id : 'thru_date',
                        label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
                        value: OLBIUS.dateToString(cur_date)
                    }],
                    after: 'from_date'
                }
        ];
        
        var liabilityGrid = OLBIUS.oLapGrid('liabilityReportGrid', config, configPopup, 'executeGetLiabilityGlReport', true);
        liabilityGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'type': ['GL_ACCOUNT', 'SUPPLIER', 'ORGANIZATION'],
                'GL_ACCOUNT': oLap.val('glAccountId'),
                'SUPPLIER': oLap.val('partyId'),
                'ORGANIZATION': '${organizationPartyId}',
            });
        });
        
        liabilityGrid.init(function () {
        	liabilityGrid.runAjax();
        });
        
        var grid = liabilityGrid.getGrid();
        var par = grid.parent();
        new LiabilityDetail().run(grid);
        
        par.bind('resize', function() {
        	
        }).trigger('resize');
        
        $("#sidebar-collapse").on('click', function(){
        	
        });
        
	}
	
	
	  var LiabilityDetail = function(grid){
      	source = {};
      	localData = [];
      	columns = [];
      	datafields = []; 
      };
      
      LiabilityDetail.prototype = {
      	initGrid : function(){
      		
      		function _setDataFields(datafields){
      			datafields = datafields;
      		}
      		
      		function getDataFields(){
      			return datafields;
      		}
      		
      		function _setColumns(column){
      			columns = column;
      		}
      		
      		function getColumns(){
      			return columns; 
      		}
      		
      		function getLocaldata(){
      			return localData;
      		}
      		
      		function setLocaldata(localData){
      			localData = localData;
      		}
      		
  			function _initColumns(){
      			var columns = [
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear0}", datafield:'liabilityYear0', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear0){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear0, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},   
			                   },					           
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear1}", datafield:'liabilityYear1', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear1){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear1, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},   
			                   },
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear2}", datafield:'liabilityYear2', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear2){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear2, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},  
			                   },
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear3}", datafield:'liabilityYear3', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear3){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear3, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},   
			                   },
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear4}", datafield:'liabilityYear4', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear4){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear4, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},   
			                   },
			                   {text: "${uiLabelMap.BACCSettingLiabilityYear5}", datafield:'liabilityYear5', cellsalign: 'right',
			                	   cellsrenderer: function (row, column, value) {
				     					var data = $('#liabilityDetailReportGrid').jqxGrid('getrowdata', row);
				     					if (data && data.liabilityYear5){
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(data.liabilityYear5, data.currency) + '</div>';
				     					} else {
				     						return '<div style="text-align: right; margin-top: 4px; margin-right: 5px">' + formatcurrency(0, data.currency) + '</div>';
				     					}
			    					},   
			                   },			                   
			               ]
      			_setColumns(columns);
      		}
  			
      		function init(){
      			_initColumns();
      			Grid.initGrid({width : '98%',autoheight: true,virutalmode : false,source : {localdata : getLocaldata()}}, datafields, columns, null, $("#liabilityDetailReportGrid"));
      			
      		}
      		
			return function(){
				init();
			}
      	},	
      	processAjax : function(data){
      		var parent = this;
      		var request = $.ajax({
					url : 'executeGetLiabilityDetailGlReport',
					data : data,
					type : 'post',
					async : false
				});
      		
      		request.done(function(data){
      			if(data._EVENT_MESSAGE_ || data._EVENT_MESSAGE_LIST_){
      				return;
      			}
      			var datafieldsTmp = [];	
      			if(data.hasOwnProperty('data')) localData = data['data'];
	                if(data.hasOwnProperty('datafields')) datafieldsTmp = data['datafields'];
	                if(data.hasOwnProperty('id')) id = data['id'];
	                
	                for (var i = 0; i < datafieldsTmp.length; i++){
	                	var field = new Object();
	                	field.name = datafieldsTmp[i].name;
	                	if (datafieldsTmp[i] == 'currency'){
	                		field.type = 'string';
	                	} else {
	                		field.type = 'number';
	                	}
	                	datafields.push(field);
	                }
	                source.localdata = localData;
	                source.datafields = datafields;
	                parent.updateSrc();
			    	$("#liabilityDetailWindow").jqxWindow('open');
      			
      		})
      	},
      	updateSrc  : function(){
      		$("#liabilityDetailReportGrid").jqxGrid('source')._source = source;
      	},
  		bindEvent : function(grid){
  			var parent = this;
  			grid.on('rowdoubleclick', function (event){
  	    		var args = event.args;
  	    		var boundIndex = args.rowindex;
  	    		var rowData = grid.jqxGrid('getrowdata', boundIndex);
  	    		var data = new Object();
  	    		data.SUPPLIER = rowData.partyId;
  	    		data.GL_ACCOUNT = rowData.glAccountId;
  	    		data.ORGANIZATION = '${organizationPartyId?if_exists}';
  	    		parent.processAjax(data);
  	        });
  		},
      	run : function(grid){
      		new this.initGrid()();
      		this.bindEvent(grid);
      	}	
      }
      
</script>