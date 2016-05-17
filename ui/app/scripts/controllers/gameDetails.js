'use strict';

var YES = 'checkmark';
var NO = 'close';
angular.module('tbs.controllers').controller('GameDetailsCtrl',
    ['$scope', '$state', 'tbsGameDetails', 'jtbGameCache', 'jtbGamePhaseService',
        function ($scope, $state, tbsGameDetails, jtbGameCache, jtbGamePhaseService) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.$on('$ionicView.enter', function () {
                $scope.game = jtbGameCache.getGameForID($scope.gameID);
                $scope.ecmEnabled = $scope.game.features.indexOf('ECMEnabled') >= 0 ? YES : NO;
                $scope.spyingEnabled = $scope.game.features.indexOf('SpyEnabled') >= 0 ? YES : NO;
                $scope.cruiseMissileEnabled = $scope.game.features.indexOf('CruiseMissileEnabled') >= 0 ? YES : NO;
                $scope.repairsEnabled = $scope.game.features.indexOf('EREnabled') >= 0 ? YES : NO;
                $scope.moveEnabled = $scope.game.features.indexOf('EMEnabled') >= 0 ? YES : NO;
                $scope.gridSize = tbsGameDetails.shortenGridSize($scope.game);
                $scope.intel = $scope.game.features.indexOf('IsolatedIntel') >= 0 ? 'Isolated' : 'Shared';
                $scope.moves = $scope.game.features.indexOf('Single') >= 0 ? '1' : 'Per Ship';
                jtbGamePhaseService.phases().then(function (phases) {
                    angular.forEach(phases, function (values, phase) {
                        if ($scope.game.gamePhase === phase) {
                            $scope.phase = values[1];
                        }
                    });
                });
            });

        }
    ]
);