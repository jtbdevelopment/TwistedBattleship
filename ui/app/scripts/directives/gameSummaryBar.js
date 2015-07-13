'use strict';

var yes = 'checkmark';
var no = 'close';

angular.module('tbs.directives', []).directive('gameSummaryBar', [
        'jtbGameCache', 'tbsGameDetails',
        function (jtbGameCache, tbsGameDetails) {
            return {
                controller: ['$scope', function ($scope) {
                    this.$scope = $scope;
                }],
                scope: true,
                templateUrl: 'templates/gameSummaryBar.html',
                restrict: 'AE',
                compile: function () {
                    return function link($scope) {
                        $scope.ecmEnabled = $scope.game.features.indexOf('ECMEnabled') >= 0 ? yes : no;
                        $scope.spyingEnabled = $scope.game.features.indexOf('SpyEnabled') >= 0 ? yes : no;
                        $scope.repairsEnabled = $scope.game.features.indexOf('EREnabled') >= 0 ? yes : no;
                        $scope.moveEnabled = $scope.game.features.indexOf('EMEnabled') >= 0 ? yes : no;
                        $scope.criticalsEnabled = $scope.game.features.indexOf('CriticalEnabled') >= 0 ? yes : no;
                        $scope.gridSize = tbsGameDetails.shortenGridSize($scope.game);
                        $scope.intel = $scope.game.features.indexOf('IsolatedIntel') >= 0 ? 'Isolated' : 'Shared';
                        $scope.moves = $scope.game.features.indexOf('Single') >= 0 ? '1' : 'Per Ship';
                    };
                }
            };
        }
    ]
);
