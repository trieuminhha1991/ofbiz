if(app !== undefined)
	app.directive('search',function($ionicActionSheet,popupApp,Util){
		return {
			restrict : 'ACE',
			templateUrl:'templates/common/search.htm',
			scope : {
				attribute : "=?",
				parent : "=",
				callBack : "=?",
				getlist : "=?",
				visible : "=?",
				filterByDay : "=?"
			},
			link:function(scope,element,attrs){
				if(!scope.parent)
					scope.parent = scope;
				
				if(!scope.visible)
					scope.visible = false;
				
				scope.parent._search = scope;
				
				scope.datafilter = {pagesize : config.pagesize,pagenum :( scope.parent.currentPage ? scope.parent.currentPage : 0)}
				
				var inputElement = element.find('input');

				var num = 0,current = 0;
				var _interval;
				
				$(inputElement).keypress(function(){
					num++;
				})
				
				$(inputElement).keyup(function(){
					num++;
				})
				
				$(inputElement).focus(function(){
					try {
						_interval = scope.bindInterval();
					} catch (e) {
						throw e;
					}
				})
				
				$(inputElement).blur(function(){
					try {
						clearInterval(_interval);
					} catch (e) {
						throw e;
					}
				});
				
				var count = 0;
				scope.bindInterval = function(){
					var interval =  setInterval(function(){
						if(count != 0)
						{
							if(current == num)
							{
								clearInterval(interval);
								if(angular.isFunction(scope.callBack))
									scope.callBack(scope.parent.search);
							}else
							{
								current = num;
							}	
						}else	
						{
							if(num != 0)
							{
								current = num;
								count++;
							}	
						}	
					},config.timoutSearch);
					
					return interval;
				}
				
				scope.refresh = function(){
					scope.parent.search = "";
					num=current=count = 0;
					if(angular.isFunction(scope.parent.refresh))
						scope.parent.refresh();
				}
				
				scope.day = (function(){
					
					var day = [];
					
					for(var i = 2;i < 8;i++)
						day.push({val : i,label  :'Thứ ' + i,isChecked : false})
					
						return day;
				}())
				
				scope.openFilter = function(){
					var checkbox = '<div ng-repeat="n in day"><ion-checkbox ng-model="n.isChecked">{{n.label}}</ion-checkbox></div>'
			    		var template = "<div class='setting'>";
			    		template += "<div class='row'><div class='col-10'></div><div class='col'>" + checkbox +"</div></div>";
			    		template += "</div>";
			    		
			    		popupApp.showConfirm(template,scope.parent.getLabel('filter'),function(res){
			    			if(res === true)
		    				{
			    				var arr = [];
			    				
			    				for(var k in scope.day){
			    					if(scope.day[k].isChecked === true)
			    						arr.push(scope.day[k].val)
			    				}
			    				scope.getlist(null,_.extend(scope.datafilter,{options : JSON.stringify(arr)}))
		    				}
			    		},scope);
				}
				
				scope.sheet = function(){
					
					return {
						setOptions : function(config){
							this.cf = {
									
								     buttons: [
										       { text: '<i class="ion-ios-lightbulb"></i> <b>' + uiLabelMap["toDay"]['vi'] + '</b>' },
										       { text : '<i class="ion-ios-calendar"></i> <b>Xem theo ngày</b>'},
										       { text: '<i class="ion-android-done-all"></i> <b>' + uiLabelMap["All"]['vi'] +'</b>' }
										     ],
										     cancelText: uiLabelMap["Cancel"]['vi'],
										     cancel: function() {},
										     buttonClicked: function(index) {
										    	if(index == 0)
										    	{
										    		 scope.parent.searchOptions = 'today'
									    			 if(typeof scope.getlist === 'function')
									    				 scope.getlist(null,_.extend(scope.datafilter,{options : JSON.stringify([new Date().getDay() + 1])}))
										    	}else if(index == 1)
										    		scope.openFilter();
										    	else{
										    		scope.parent.searchOptions = 'all'
									    			 if(typeof scope.getlist === 'function')
										    			 scope.getlist('init');
										    	}	 
										    	 
										       return true;
										     }
								   };
							 
							this.cf = Util.isValid(config) ? config : this.cf;
						},
						getOptions : function(){
							return this.cf;
						},
						run : function(config){
							if(scope.filterByDay)
								$ionicActionSheet.show(this.getOptions(this.setOptions(config)));
						}
					}
				};
				scope.runSheet = new scope.sheet();
				
				if(!_.has(scope.parent,'refresh'))
					scope.refresh = function(){
						if(typeof scope.getlist === 'function')
							scope.getlist('init');
					}
			}
		}
	})
	