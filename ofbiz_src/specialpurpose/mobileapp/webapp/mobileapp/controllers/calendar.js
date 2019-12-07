/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.controller('CalendarController', function($rootScope, $scope, $controller) {
    $.extend(this, $controller('BaseController', {
        $scope: $scope
    }));
});
