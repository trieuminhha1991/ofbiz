app.directive('ngEnter', function() {
	return function(scope, element, attrs) {
		element.bind("keydown keypress", function(event) {
			if (event.which === 13) {
				scope.$apply(function() {
					scope.$eval(attrs.ngEnter);
				});
				event.preventDefault();
			}
		});
	};
});
app.directive('searchInput', function($rootScope) {
	function init(scope, element, attrs) {
		scope.getLabel = $rootScope.getLabel;
	}

	return {
		templateUrl : 'templates/item/search.htm',
		scope : {
			search : "=",
		},
		link : init
	};
});
app.directive('imageReader', function($q) {
	var slice = Array.prototype.slice;
	return {
		restrict : 'A',
		require : '?ngModel',
		link : function(scope, element, attrs, ngModel) {
			if (!ngModel)
				return;

			ngModel.$render = function() {
			};

			element.bind('change', function(e) {
				var element = e.target;

				$q.all(slice.call(element.files, 0).map(readFile)).then(function(values) {
					if (element.multiple)
						ngModel.$setViewValue(values);
					else{
						ngModel.$setViewValue(values.length ? values[0] : null);
					}

				});

				function readFile(file) {
					var deferred = $q.defer();

					var reader = new FileReader();
					reader.onload = function(e) {
						deferred.resolve(e.target.result);
					};
					reader.onerror = function(e) {
						deferred.reject(e);
					};
					reader.readAsDataURL(file);

					return deferred.promise;
				}

			});
			//change

		} //link
	};
	//return
});
app.directive('hideTabBar', function($rootScope) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs, ngModel) {
			$rootScope.hideTabBar = true;
			scope.$on('$destroy', function() {
                $rootScope.hideTabBar = false;
            });
		} //link
	};
	//return
});
app.directive('formValidation', function($parse, $timeout) {
	return {
		restrict : 'A',
		scope : {
			parent : "=formValidationParent"
		},
		require : '^form',
		link : function(scope, element, attrs, form) {
			if(!scope.parent){
				scope.parent = scope.$parent;
			}
			var key = element.attr('id');
			if(!key){
				key = element.attr('name');

			}
			if(key){
				scope.parent[key] = scope;
			}
			scope.children = [];
			scope.checkRule = function(rule, field){
				var val = field.$viewValue;
				if(scope.parent[rule] && typeof(scope.parent[rule]) == 'function'){
					return scope.parent[rule](field);
				}else if(rule){
					switch(rule){
						case "phone" :
							return validatePhone(val);
						case "email" :
							return validateEmail(val);
						case "number" :
							return !isNaN(val);
					}
				} else if(val){
					return true;
				}
				return false;
			};
			scope.validate = function(){
				var flag = true;
				for (var i = 0; i < scope.children.length; i++) {
					var input = scope.children[i];
					var attributes = input.attributes;
					var rule = attributes.rule;
					var field = form[attributes.name.value];
					var inp = angular.element(input);
					var display = attributes.display;
					if(display){
						inp = $('#' + display.value);
					}
					scope.setValid(field, rule, display);
					scope.setStyleInput(inp, field.realValid);
					if(!field.realValid){
						flag = field.realValid;
					}
				};
				return flag;
			};
			scope.getChildren = function(){
				var inputs = element.find("*");
				for (var i = 0; i < inputs.length; i++) {
					var attributes = inputs[i].attributes;
					if (attributes.getNamedItem('ng-model') && attributes.name && attributes.name.value && (attributes.required || attributes.rule)) {
						scope.children.push(inputs[i]);
					}
				}
			};
			scope.init = function(){
				for (var i = 0; i < scope.children.length; i++) {
					(function(input) {
						try {
							var attributes = input.attributes;
							var field = form[attributes.name.value];
							var rule = attributes.rule;
							var display = attributes.display;
							scope.$watch(function() {
								return field.$viewValue + '_' + field.$valid;
							}, function() {
								var inp = angular.element(input);
								if(!field.$viewValue && ! field.blur){
									return;
								}
								field.blur = true;
								scope.setValid(field, rule, display);
								var valid = field.realValid;
								scope.setStyleInput(inp, valid);
							});
						} catch(e) {
							console.log(e);
						}
					})(scope.children[i]);
				}
			};
			scope.reset = function(){
				for (var i = 0; i < scope.children.length; i++) {
					var input = scope.children[i];
					var attributes = input.attributes;
					var rule = attributes.rule;
					var field = form[attributes.name.value];
					var inp = angular.element(input);
					var display = attributes.display;
					if(display){
						scope.setStyleInput($('#' + display.value), true);
					}else{
						scope.setStyleInput(inp, true);
					}
					field.blur = false;
					(function(field, rule, display){
						$timeout(function(){
							scope.setValid(field, rule, display);
							console.log(field);
						}, 100)
					})(field, rule, display);
				};
			};
			scope.setValid = function(field, rule, display){
				var valid = field.$valid;
				if(valid && rule){
					valid = scope.checkRule(rule.value, field);
				}
				if(display){
					inp = $('#' + display.value);
				}
				field.realValid = valid;
			};
			scope.setStyleInput = function(inp, valid){
				if (!valid) {
					inp.removeClass('has-success');
					inp.addClass('has-error');
				} else {
					inp.removeClass('has-error').addClass('has-success');
				}
			};
			scope.getChildren();
			scope.init();
		}
	};
});
