'use strict';

//  TODO - test
angular.module('tbs.directives').directive('playerGameList', function () {
    return {
        restrict: 'AE',
        templateUrl: 'templates/gamelist/playerGameList.html',
        replace: true
    };
});
