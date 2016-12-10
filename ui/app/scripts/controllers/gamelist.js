'use strict';

angular.module('tbs.controllers').controller('MobileGameListCtrl',
    ['$scope', '$state', 'jtbGameCache', 'jtbGameClassifier',
        function ($scope, $state, jtbGameCache, jtbGameClassifier) {
            var controller = this;
            controller.games = {};
            controller.phasesInOrder = [];
            var icons = jtbGameClassifier.getIcons();
            angular.forEach(jtbGameClassifier.getClassifications(), function (classification) {
                controller.phasesInOrder.push(classification);
                controller.games[classification] = {};
                controller.games[classification].games = [];
                controller.games[classification].icon = icons[classification];
                controller.games[classification].hideGames = false;
                controller.games[classification].label = classification;
            });

            controller.createNew = function () {
                $state.go('app.create');
            };

            controller.switchHideGames = function (phase) {
                controller.games[phase].hideGames = !controller.games[phase].hideGames;
            };

            function reloadFromCaches() {
                angular.forEach(controller.games, function (phaseData, phase) {
                    phaseData.games = jtbGameCache.getGamesForPhase(phase);
                });
            }

            $scope.$on('gameCachesLoaded', function () {
                reloadFromCaches();
                $scope.$broadcast('scroll.refreshComplete');
                $state.go('app.games');
            });

            if (jtbGameCache.initialized()) {
                reloadFromCaches();
            }
        }
    ]);

