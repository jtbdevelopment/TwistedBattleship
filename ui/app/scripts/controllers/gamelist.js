'use strict';

var phasesAndIcons = {
    Playing: 'play',
    Setup: 'hammer',
    Challenged: 'chatboxes',
    RoundOver: 'refresh',
    Declined: 'thumbsdown',
    NextRoundStarted: 'checkmark',
    Quit: 'flag'
};

angular.module('tbs.controllers').controller('MobileGameListCtrl',
    ['$rootScope', '$scope', '$state', 'jtbPlayerService', 'jtbGameCache', 'tbsGameDetails', 'phases',
        function ($rootScope, $scope, $state, jtbPlayerService, jtbGameCache, tbsGameDetails, phases) {
            $scope.games = {};
            $scope.phasesInOrder = [];
            $scope.gameDetails = tbsGameDetails;
            $scope.md5 = '';
            angular.forEach(phasesAndIcons, function (icon, phase) {
                $scope.phasesInOrder.push(phase);
                $scope.games[phase] = {};
                $scope.games[phase].games = [];
                $scope.games[phase].icon = icon;
                $scope.games[phase].style = phase.toLowerCase() + 'Button';
                $scope.games[phase].hideGames = false;
                $scope.games[phase].label = '';
            });

            angular.forEach(phases, function (values, phase) {
                $scope.games[phase].label = values[1];
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
            });

            if(jtbGameCache.initialized()) {
                reloadFromCaches();
            }

            $scope.$on('phaseChangeAlert', function (/*event, game*/) {
                //  TODO
            });
        }
    ]);

