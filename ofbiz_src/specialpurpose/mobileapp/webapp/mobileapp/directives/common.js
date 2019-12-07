olbius.directive('searchInput', function() {
	return {
		templateUrl : 'templates/item/search.htm'
	};
});
olbius.directive('loading', function($timeout) {
	return {
		restrict : 'E',
		templateUrl : 'templates/item/loading.htm',
		transclude : true,
		scope : {
			id : "=",
			elements : "=",
			parent : "="
		},
		loading : "=",
		link : function(scope, element, attrs) {
			scope.option = {
				isFixed : true
			};
			if (!scope.parent) {
				scope.parent = scope.$parent;
			}
			if (!scope.id) {
				scope.id = 'loading-' + makeid(10);
			}
			scope.loading = scope.id;
			$timeout(function() {
				scope.init();
			});
			if (!scope.elements) {
				scope.elements = new Array();
			}
			scope.parent[scope.id] = scope;
			scope.elements[scope.loading] = scope;
			scope.init = function() {
				var obj = $('#' + scope.loading);
				var height = obj.height();
				var half = height / 2;
				var child = obj.find('.loading-content');
				var ch = half < 50 ? half : 50;
				var rm = height - ch;
				child.height(ch);
				child.css({
					top : rm / 2 + "px"
				});
			};
			scope.show = function() {
				$('#' + scope.loading).show();
			};
			scope.showLoading = function() {
				$('#' + scope.loading).show();
			};
			scope.hide = function() {
				$('#' + scope.loading).hide();
			};
			scope.hideLoading = function() {
				$('#' + scope.loading).hide();
			};
			scope.setOption = function(options) {
				scope.option = options;
			};
		}
	};
});
olbius.directive('validateInteger', function() {
	return {
		restrict : 'A',
		require : '?ngModel',
		link : function(scope, element, attrs, modelCtrl) {
			modelCtrl.$parsers.push(function(inputValue) {
				if (inputValue == undefined)
					return '';
				var transformedInput = parseInt(inputValue);
				modelCtrl.$setViewValue(transformedInput);
				modelCtrl.$render();
				return transformedInput;
			});
		}
	};
});

olbius.directive('horizonScrollable', function() {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			element.on('touchstart mousedown', function(e) {
				var obj = $(this);
				var objWidth = obj.width();
				var childWidth = obj.children().outerWidth();
				if (childWidth > objWidth && !obj.hasClass('horizon-scrollable-active')) {
					obj.addClass('horizon-scrollable-active');
				} else if (childWidth <= objWidth) {
					obj.removeClass('horizon-scrollable-active');
				}
			});
			element.on('touchmove mousemove mousewheel', function(e) {
				e.stopPropagation();
			});
		}
	};
});
olbius.directive('autoHeightScrollable', function() {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			var time = setTimeout(function() {
				var ct = $('#container');
				var ctHeight = ct.height();
				var par = $('.page-content');
				var sibs = element.siblings();
				var header = element.children('.exclude');
				var body = element.children('.effect');
				var parentPadding = getPadding(par);
				for (var x = 0; x < sibs.length; x++) {
					var height = $(sibs[x]).outerHeight(true);
					ctHeight -= height;
				}
				ctHeight -= (parentPadding.top + parentPadding.bottom) - header.outerHeight(true);
				ctHeight = ctHeight < 400 ? 400 : ctHeight;
				element.height(ctHeight);
				element.addClass('scrollable-auto-height-active');
				element.on('touchstart mousedown', function(e) {
					var obj = $(this);
					var objWidth = obj.width();
					var childWidth = obj.children().outerWidth();
					if (childWidth > objWidth && !obj.hasClass('horizon-scrollable-active')) {
						obj.addClass('horizon-scrollable-active');
					} else if (childWidth <= objWidth) {
						obj.removeClass('horizon-scrollable-active');
					}
				});
				element.on('touchmove mousemove mousewheel', function(e) {
					e.stopPropagation();
				});
				clearTimeout(time);
			}, 100);
		}
	};
});
olbius.directive('autoPageable', function($timeout, $compile) {
	return {
		restrict : 'A',
		scope : {
			data : "=autoPageableData",
			callback : "=autoPageableCallback",
			renderer : "=autoPageableRenderer",
			itemHeight : "=autoPageableItemHeight"
		},
		link : function(scope, element, attrs) {
			scope.index = 0;
			scope.end = 0;
			scope.container, scope.header;
			scope.isShowed = false;
			$timeout(function() {
				scope.container = $('#container');
				scope.header = $('#header');
				scope.init();
				element.on('touchmove mousemove mousewheel', function(e) {
					// e.stopPropagation();
					var offset = element.offset();
					var top = offset.top;
					var end = element.height() + top;
					var ctHeight = scope.container.height();
					var sctop = scope.container.scrollTop();
					var off = sctop + ctHeight + 50 - end;
					if (off >= 0) {
						scope.appendData();
					}
				});
			});
			scope.$watch('data', function() {
				try{
					if (scope.data.length) {
						scope.init();
					}
				}catch(e){
					console.log(e);
				}
			});
			scope.init = function() {
				if (scope.container && scope.header && scope.data.length) {
					var offset = element.offset();
					var top = offset.top;
					var ctHeight = scope.container.height();
					var hdHeight = scope.header.height();
					var re = ctHeight - hdHeight - top;
					scope.initData(re);
				}
			};
			scope.initData = function(height) {
				var chHeight = scope.itemHeight;
				var item = Math.ceil(height / chHeight);
				var start = scope.index * item;
				scope.end = start + item;
				if (scope.data && scope.data.length) {
					scope.renderer = scope.data.slice(start, scope.end);
					if (scope.callback && typeof (scope.callback) == "function") {
						scope.callback();
					}
				}
			};
			scope.appendData = function() {
				if (scope.data && scope.data[scope.end]) {
					scope.renderer.push(scope.data[scope.end]);
					if (scope.callback && typeof (scope.callback) == "function") {
						scope.callback();
					}
					scope.end++;
				}
			};
		}
	};
});
olbius.directive('fixedTableHeader', function($compile, $timeout) {
	return {
		restrict : 'A',
		link : function(scope, element, attrs) {
			// var self = scope;
			// self.resize = function(columns, parent) {
				// var h = parent.find('div');
				// for (var x = 0; x < columns.length; x++) {
					// var obj = $(columns[x]);
					// var width = obj.innerWidth();
					// var bt = obj.outerWidth() - obj.innerWidth();
					// var off = obj.position();
					// var h1 = $(h[x]);
					// h1.css({
						// width : (width + bt) + "px",
						// top : off.top + 'px',
						// left : off.left + 'px'
					// });
				// }
			// };
			// var table, head, columns;
			// $timeout(function() {
				// head = element.find('thead');
				// columns = element.find('thead th');
				// var classes = element.attr('class');
				// table = $('<div class="' + classes + " " + 'fixed-header-table"></div>');
				// var bordT = element.outerWidth() - element.innerWidth();
				// var bordH = head.outerHeight() - head.innerHeight();
				// var offset = element.offset();
				// var left = offset.left;
				// var height = head.height();
				// table.css({
					// display : 'none',
					// left : left + 'px',
					// top : offset.top + 'px',
					// width : element.width() + 'px',
					// height : height + bordH + 'px'
				// });
				// for (var x = 0; x < columns.length; x++) {
					// var obj = $(columns[x]);
					// var bt = obj.outerWidth() - obj.innerWidth();
					// var width = obj.innerWidth();
					// var padding = obj.css('padding');
					// var h = $('<div></div>');
					// var off = obj.position();
					// h.text(obj.text());
					// h.css({
						// position : 'absolute',
						// padding : padding,
						// width : (width + bt) + "px",
						// top : off.top + 'px',
						// left : (off.left + bt) + 'px',
						// height : height
					// });
					// table.append(h);
				// }
				// $compile(table.contents())(scope);
				// $('.page-content').append(table);
			// });
			// element.on('touchstart mousedown mousewheel', function() {
				// self.resize(columns, table);
			// });
			// element.on('touchmove mousewheel', function() {
				// var par = element.parents('.scrollable-auto-height-active');
				// var scrollTop = par.scrollTop();
				// if (scrollTop > 0 && table) {
					// table.show();
				// }
			// });
			// element.on('touchend mouseup mousewheel', function() {
				// var par = element.parents('.scrollable-auto-height-active');
				// var scrollTop = par.scrollTop();
				// if (scrollTop == 0 && table) {
					// table.hide();
				// }
			// });

		}
	};
});
olbius.directive('imageReader', function($q) {
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
					else
						ngModel.$setViewValue(values.length ? values[0] : null);
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
olbius.directive("scroll", function() {
	return {
		restrict : 'A',
		scope : {
			callback : "&scrollCallBack",
			offset : "="
		},
		link : function(scope, element, attrs) {
			$(window).bind('touchmove mousewheel mousemove', function(e) {
				scope.bindScrollAction(e.offsetY);
			});
			scope.bindScrollAction = function(y) {
				var offset = element.offset();
				var height = element.height();
				var top = y;
				if (!y) {
					top = $('#header').outerHeight();
				}
				if (top >= offset.top && top <= height && scope.callback && typeof (scope.callback) == "function") {
					scope.callback();
				}
			};
			scope.bindScrollAction();
		}
	};
});
olbius.directive('preventDefault', function() {
	return {
		restrict : 'E',
		link : function(scope, elem, attrs) {
			if (attrs.ngClick || attrs.href === '' || attrs.href === '#') {
				elem.on('click', function(e) {
					e.stopProparation();
				});
			}
		}
	};
});
olbius.directive('formValidation', function($parse) {
	return {
		restrict : 'A',
		scope : {
			parent : "=formValidationParent"
		},
		require : '^form',
		link : function(scope, element, attrs, form) {
			console.log(form);
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
			scope.checkRule = function(rule, inp, val){
				if(scope.parent[rule] && typeof(scope.parent[rule]) == 'function'){
					return scope.parent[rule](inp, val);
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
					var field = form[attributes.name.value];
					var inp = angular.element(input);
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
							scope.$watch(function() {
								return field.$viewValue + '_' + field.$valid;
							}, function() {
								var inp = angular.element(input);
								if(!field.$viewValue && ! field.blur){
									return;
								}
								field.blur = true;
								var val = inp.val();
								var valid = field.$valid;
								if(valid && rule){
									valid = scope.checkRule(rule.value, inp, val);
								}
								field.realValid = valid;
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
					var field = form[attributes.name.value];
					var inp = angular.element(input);
					scope.setStyleInput(inp, true);
					field.blur = false;
					field.realValid = null;
				};
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
