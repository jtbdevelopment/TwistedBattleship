'use strict';

/*
var phasesAndIcons = {
    Playing: 'play',
    Setup: 'hammer',
    Challenged: 'chatboxes',
    RoundOver: 'refresh',
    Declined: 'thumbsdown',
    NextRoundStarted: 'checkmark',
    Quit: 'flag'
};
 */

angular.module('tbs.controllers').controller('MobileGameListCtrl',
    ['$rootScope', '$scope', '$state', 'jtbPlayerService', 'jtbGameCache', 'tbsGameDetails', 'jtbGamePhaseService', 'jtbGameClassifier',
        function ($rootScope, $scope, $state, jtbPlayerService, jtbGameCache, tbsGameDetails, jtbGamePhaseService, jtbGameClassifier) {
            $scope.games = {};
            $scope.phasesInOrder = [];
            $scope.gameDetails = tbsGameDetails;
            $scope.md5 = '';
            var icons = jtbGameClassifier.getIcons();
            angular.forEach(jtbGameClassifier.getClassifications(), function (classification) {
                $scope.phasesInOrder.push(classification);
                $scope.games[classification] = {};
                $scope.games[classification].games = [];
                $scope.games[classification].icon = icons[classification];
                $scope.games[classification].hideGames = false;
                $scope.games[classification].label = classification;
            });

            $scope.createNew = function () {
                $state.go('app.create');
            };

            $scope.switchHideGames = function (phase) {
                $scope.games[phase].hideGames = !$scope.games[phase].hideGames;
            };

            $scope.refreshGames = function () {
                $rootScope.$broadcast('refreshGames', '');
            };

            function reloadFromCaches() {
                $scope.md5 = jtbPlayerService.currentPlayer().md5;
                angular.forEach($scope.games, function (phaseData, phase) {
                    phaseData.games = jtbGameCache.getGamesForPhase(phase);
                });
            }

            $scope.$on('gameCachesLoaded', function () {
                reloadFromCaches();
                $scope.$broadcast('scroll.refreshComplete');
                $state.go('app.games');
            });

            if(jtbGameCache.initialized()) {
                reloadFromCaches();
            }

            $scope.$on('phaseChangeAlert', function (/*event, game*/) {
                //  TODO
            });
        }
    ]);

