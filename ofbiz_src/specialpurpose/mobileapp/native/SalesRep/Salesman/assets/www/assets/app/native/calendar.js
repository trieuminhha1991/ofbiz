app.directive('calendar', ['$compile', '$timeout', '$cordovaDatePicker', 'CalendarFactory',
// app.directive('calendar', ['$rootScope', '$compile', '$timeout', 'CalendarFactory',
    function($compile, $timeout, datepicker, calendar) {
        function init(scope, element, attrs) {
            scope.self = scope;
            scope.parent = scope.parent ? scope.parent : scope.$parent;
            scope.option = scope.option ? scope.option : {
                date: new Date(),
                mode: "date",
                minDate: new Date()
            }
            scope.formatDateYMD = calendar.formatDateYMD;
            scope.formatDateDMY = calendar.formatDateDMY;
            scope.isPhonegap = (typeof(datepicker) != 'undefined');
            element.ready(function(){
                var obj = $(element[0]).find('.calendar-picker');
                if(!scope.isPhonegap){
                    scope.input = $('<input type="date" ng-model="date"/>');
                    obj.prepend(scope.input);
                    $compile(obj.contents())(scope);
                }
            });

            scope.pick = function() {
                if(scope.isPhonegap){
                    if(scope.option && scope.date) scope.option.date = scope.date;
                    datepicker.show(scope.option).then(function(date){
                        scope.date = date;
                    });
                }else if(scope.input){
                    scope.input.click();
                }
            };

        }

        return {
            restrict: 'ACE',
            templateUrl: 'templates/item/calendar.htm',
            scope: {
                self: "=",
                parent: "=",
                date: "=",
                type: "=",
                option: "="
            },
            // transclude : true,
            link: init
        };
    }
]);
