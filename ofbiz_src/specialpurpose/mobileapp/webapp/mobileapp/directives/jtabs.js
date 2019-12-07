/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.directive('jtabs', ['$compile',
    function($compile) {
        function init(scope, element, attrs) {
            var jqueryElm = $(element[0]);
            $(jqueryElm).tabs();

        }

        return {
            restrict: 'ACE',
            link: init
        };
    }
]);
