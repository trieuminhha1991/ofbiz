/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller:
 * - retrieves and persists the model via the todoStorage service
 * - exposes the model to the template and provides event handlers
 */
olbius.directive('jqUpload', ['$compile', 'UploadService',
    function($compile, UploadService) {
        function init(scope, element, attrs) {
            scope.loadingFiles = false;
            scope.$on('fileuploadadd', function(e, act) {
                act.submit();
            });
        }

        return {
            restrict: 'ACE',
            templateUrl: 'templates/item/fileupload.htm',
            link: init
        };
    }
]);
