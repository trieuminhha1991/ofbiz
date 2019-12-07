var _olbius = function(){};

/**
 *@Notice : this method like interface use to build common behavior each other object 
 * 			scope of controller use process logic apps
 *@param  The scope current controller used
 *@param The Object config include all of event and method for scope object
 *@author Namdn
 *
 * */
_olbius.init = function(self,config){
	
	var $dependency  = {};
	
	$dependency = {
			$controller : _.has(config,'$inject') ? (_.has(config.$inject,'$controller') ? config.$inject.$controller : null ): null
	}
	
	if(_.isNull($dependency.$controller))
	{
		throw new Error('this api can"t not missing injector dependency : "controller" ');
		return;
	}	
	
	if(self === undefined) 
	{
		throw new Error('use olbius object apps need pass params self');
		return ;
	}
	if(config === undefined)
	{
		throw new Error('use olbius object apps need pass params config behavior of self');
		return ;
	}
		
	return (function(self,_config){
		
		this.prototype = {
				_extendBaseCtrl : function(){
					angular.extend(this,$dependency.$controller('CommonController',{$scope : self}));
				},
				addAttribute : function(_config){
					if(!_.has(_config,'atr'))
						return ;

					for(var k in _config['atr']){
						self[k] = _config['atr'][k];
					}
				},
				addEvent : function(e){
					
					e = !_.isArray(e) ? [e] : e;
					
					angular.forEach(e,function($obj,index){
						if(!_.has($obj,'type'))
						{
							if($obj._name.indexOf('$') != -1)
								self.$on($obj._name,$obj._func);
							else
								self.$on('$ionicView.' + $obj._name,$obj._func);
						}else 
						{
							if($obj['type'] == 'watch')
								self.$watch($obj._name,$obj._func);
							else
								self.$on($obj._name,$obj._func);
						}
					})
				},
				addBehavior : function(b){
					
					b = !_.isArray(b) ? [b] : b;
					
					if(_.isEmpty(b))
						return;
					
					angular.forEach(b,function($o,i){
						self[$o._fname] = $o._fbody;
					})
					
				},
				_apply : function(_config){
					if(_.has(_config,'event'))
						this.addEvent.call(this,_config.event);
					if(_.has(_config,'behavior'))
						this.addBehavior.call(this,_config.behavior);
				},
				run : function(_config){
					/*this.addAttribute.call(this,_config);*/
					this._extendBaseCtrl.call(this);
					this._apply.call(this,_config);
				}
		}
		
		var thread = new this();
		
		thread.run(_config);
		
	}.call(_olbius,self,config));
	
}
