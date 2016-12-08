'use strict';

var YES = 'checkmark';
var NO = 'close';
angular.module('tbs.controllers').controller('GameDetailsCtrl',
    ['$scope', '$state', 'tbsGameDetails', 'jtbGameCache', 'jtbGamePhaseService',
        function ($scope, $state, tbsGameDetails, jtbGameCache, jtbGamePhaseService) {
            var controller = this;
            controller.gameID = $state.params.gameID;
            controller.game = jtbGameCache.getGameForID(controller.gameID);
            controller.gameDetails = tbsGameDetails;

            $scope.$on('$ionicView.enter', function () {
                controller.game = jtbGameCache.getGameForID(controller.gameID);
                controller.ecmEnabled = controller.game.features.indexOf('ECMEnabled') >= 0 ? YES : NO;
                controller.spyingEnabled = controller.game.features.indexOf('SpyEnabled') >= 0 ? YES : NO;
                controller.cruiseMissileEnabled = controller.game.features.indexOf('CruiseMissileEnabled') >= 0 ? YES : NO;
                controller.repairsEnabled = controller.game.features.indexOf('EREnabled') >= 0 ? YES : NO;
                controller.moveEnabled = controller.game.features.indexOf('EMEnabled') >= 0 ? YES : NO;
                controller.gridSize = tbsGameDetails.shortenGridSize(controller.game);
                controller.intel = controller.game.features.indexOf('IsolatedIntel') >= 0 ? 'Isolated' : 'Shared';
                controller.moves = controller.game.features.indexOf('Single') >= 0 ? '1' : 'Per Ship';
                jtbGamePhaseService.phases().then(function (phases) {
                    angular.forEach(phases, function (values, phase) {
                        if (controller.game.gamePhase === phase) {
                            controller.phase = values[1];
                        }
                    });
                });
            });

        }
    ]
);