if(typeof app !== undefined)
	app.controller('DashboardController',function($scope,$controller,$ionicPopup,Util,DashboardEvent,popupApp){
		
		var self = $scope;
		
		self.options = {
				ok : 0,
				cancel : 0,
				move : 0,
				turnover : 0
		}
		
		self.turnOvertChart = {
				yearr : (new Date()).getYear() + 1900,
				monthh : (new Date()).getMonth() + 1,
				topSalesman :10,
				topCustomer : 10,
				service : 'salesOrder'
		}
		
		self.isFullLoaded = {};
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			self.initDashBoard();
				           			self.show();
				           		}
				           	}
			           ],
			    'behavior' : [{
			    		_fname : 'initturnOverRankSaler',
			    		_fbody : function(act){
			    			
			    			DashboardEvent.getTurnOverChart(self.turnOvertChart).then(function(res){
				    			if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
				    				 void 0
			    				else
		    					{
			    					if(!_.isUndefined(_chart))
			    						if(_.has(res.data,'xAxis') && _.has(res.data,'yAxis'))
			    						{
			    							if(act == 'refresh')
			    								self.redrawChart('turnover',{xAxis : res.data.xAxis,yAxis : res.data.yAxis});
			    							else
			    							{
			    								self.chartTurnOver = _chart.run(self.getConfig('turnover',{xAxis : res.data.xAxis,yAxis : res.data.yAxis}))
			    								self.loadedTurnOvert = true;
						    					self.checkLoaded();
			    							}	
			    						}
			    					
		    					}
				    		});
			    			
					    }      
			    },
			    {
			    	_fname : 'initCustomerRatingChart',
		    		_fbody : function(act){
		    			
		    			DashboardEvent.getCustomerRatingChart(self.turnOvertChart).then(function(res){
			    			if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
			    				 void 0
		    				else
	    					{
		    					if(!_.isUndefined(_chart))
		    						if(_.has(res.data,'xAxis') && _.has(res.data,'yAxis'))
		    					{
		    							if(act == 'refresh')
		    								self.redrawChart('customerRating',{xAxis : res.data.xAxis,yAxis : res.data.yAxis});
		    							else
		    							{
		    								self.customerRatingChart = _chart.run(self.getConfig('customerRating',{xAxis : res.data.xAxis,yAxis : res.data.yAxis}))
		    								self.loadedCustomerRating = true;
					    					self.checkLoaded();
		    							}
		    					}		
	    					}
			    		});
		    			
				    }     
			    },
			    {
			    	_fname : 'redrawChart',
			    	_fbody : function(chart,data){
			    		if(Util.isValid(self.chartTurnOver) && chart == 'turnover')
			    			_chart.prepareData(self.chartTurnOver,data);
			    		
			    		if(Util.isValid(self.customerRatingChart) && chart == 'customerRating')
			    			_chart.prepareData(self.customerRatingChart,data);
			    		
			    	}
			    }
			    ,{
			    	_fname : 'initDashBoard',
			    	_fbody : function(){
			    		self.initFilter();
			    		self.getInfo();
			    		self.initturnOverRankSaler();
			    		self.initCustomerRatingChart();
			    	}
			    },
			    {
			    	_fname : 'initFilter',
			    	_fbody : function(){
			    		
			    		_.extend(self.options,{
			    			monthOfYear : [],
			    			year : [],
			    			number : [] 
			    		});
			    		
			    		angular.forEach(uiLabelMap['month_of_year'],function(val,i){
			    			self.options.monthOfYear.push({val : i,label : val['vi']})
			    		})
			    		
			    		var _thisYear = (new Date()).getYear();
			    		
			    		for(var i = _thisYear - 2;i < _thisYear + 2;i++){
			    			self.options.year.push({val : (1900 + i),label : 'Năm ' + (1900 + i)})
			    		}
			    		
			    		self.options.number = [{val : 5,label : 5},{val : 10,label : 10},{val : 15,label : 15}];
			    		
			    	}
			    },
			    {
			    	_fname : 'checkLoaded',
			    	_fbody : function(){
			    		self.isFullLoaded = self.loadedTurnOvert || self.loadedCustomerRating ? true : false;
			    		if(self.isFullLoaded === true)
			    			self.hide();
			    	}
			    },
			    {
			    	_fname : 'openFilter',
			    	_fbody : function(chart){
			    		
			    		var template = "<div class='setting'>";
			    		template += "<div class='row'><div class='col-50'>Tháng</div><div class='col'><select ng-model='turnOvertChart.monthh' ng-options='m.val as m.label for m in options.monthOfYear track by m.val'></select></div></div>";
			    		template += "<div class='row'><div class='col-50'>Năm</div><div class='col'><select ng-model='turnOvertChart.yearr' ng-options='y.val as y.label for y in options.year track by y.val'></select></div></div>";
			    		if(chart == 'turnover')
			    			template += "<div class='row'><div class='col-50'>Top</div><div class='col'><select ng-model='turnOvertChart.topSalesman' ng-options='top.val as top.label for top in options.number track by top.val'></select></div></div>";
			    		else 
			    			template += "<div class='row'><div class='col-50'>Top</div><div class='col'><select ng-model='turnOvertChart.topCustomer' ng-options='top.val as top.label for top in options.number track by top.val'></select></div></div>";
			    		
			    		template += "</div>";
			    		
			    		popupApp.showConfirm(template,self.getLabel('filter'),function(res){
			    			if(res === true)
			    				if(chart == 'turnover')
			    					self.initturnOverRankSaler('refresh');
			    				else if(chart == 'customerRating')
			    					self.initCustomerRatingChart('refresh');
			    		},self);
			    		
			    	}
			    },
			    {
			    	_fname : 'getInfo',
			    	_fbody : function(){
			    		DashboardEvent.getOrderComplete().then(function(res){
			    			if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
			    				 void 0
		    				else
		    					self.options.ok = _.has(res.data,'listOrderCompleted') ?  (res.data.listOrderCompleted[0] != null ? res.data.listOrderCompleted[0] : 0) : 0;
			    		});
			    		
						DashboardEvent.getOrderCancel().then(function(res){
							if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
			    				 void 0
		    				else
		    					self.options.cancel = _.has(res.data,'listOrderCancelled') ? (res.data.listOrderCancelled[0] != null ? res.data.listOrderCancelled[0] : 0) : 0;		
			    		});
						
						DashboardEvent.getOrderIntransit().then(function(res){
							if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
			    				 void 0
		    				else
		    					self.options.move = _.has(res.data,'listOrderIntransit') ? (res.data.listOrderIntransit[0] != null ? res.data.listOrderIntransit[0] : 0) : 0;		
			    		});
						
						DashboardEvent.getTurnOver().then(function(res){
							if(!_.has(res,'data') || _.has(res,config['msg']['erm']) || _.has(res,config['msg']['erml']))
			    				 void 0
		    				else
		    					self.options.turnover = _.has(res.data,'listValue') ? (res.data.listValue[0] != null ? res.data.listValue[0] : 0) : 0;		
			    		});
			    	}
			    },
			    {
			    	_fname : 'getConfig',
			    	_fbody : function(name,data){
			    		switch(name)	
			    		{
				    		case 'turnover' : 
				    			return {
				    				object : $('#turnOverRankBySaler'),
				    				title  : {
				    					text : self.getLabel('ChartEmplByTurnOver')
				    				},
				    				data : Util.isValid(data) ? data : {}
				    			}
				    		case 'customerRating' : 
				    			return {
				    				object : $('#customerRankchart'),
				    				title  : {
				    					text : self.getLabel('customerRankchart')
				    				},
				    				data : Util.isValid(data) ? data : {}
				    			}	
				    			
				    		default  :
				    			break;
			    		}
			    		
			    	}
			    }]
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})
	