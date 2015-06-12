/*global $:false */
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

angular.module('tbs').controller('MobileGameListCtrl',
    ['$scope', '$location', '$animate', '$timeout', '$state', 'jtbGamePhaseService', 'jtbGameCache', 'tbsGameDetails',
        function ($scope, $location, $animate, $timeout, $state, jtbGamePhaseService, jtbGameCache, tbsGameDetails) {
            $scope.games = {};
            $scope.phasesInOrder = [];
            $scope.gameDetails = tbsGameDetails;
            angular.forEach(phasesAndIcons, function (icon, phase) {
                $scope.phasesInOrder.push(phase);
                $scope.games[phase] = {};
                $scope.games[phase].games = [];
                $scope.games[phase].icon = icon;
                $scope.games[phase].style = phase.toLowerCase() + 'Button';
                //  TODO - needed?
                $scope.games[phase].hideGames = false;
                $scope.games[phase].label = '';
            });

            $scope.createNew = function () {
                $state.go('app.create');
            };

            $scope.switchHideGames = function (phase) {
                $scope.games[phase].hideGames = !$scope.games[phase].hideGames;
            };

            jtbGamePhaseService.phases().then(function (phases) {
                angular.forEach(phases, function (values, phase) {
                    $scope.games[phase].label = values[1];
                });
            }, function () {
                //  TODO
                $location.path('/error');
            });

            function loadGames() {
                angular.forEach($scope.games, function (phaseData, phase) {
                    phaseData.games = jtbGameCache.getGamesForPhase(phase);
                });
            }

            $scope.$on('gameCachesLoaded', function () {
                loadGames();
            });

            $scope.$on('phaseChangeAlert', function (event, game) {
                //  TODO
                /*
                 //  Brief timeout to allow event to fully propagate so data is current
                 $timeout(function () {
                 if (game.gamePhase === phase) {
                 var buttonId = '#' + game.id;
                 var prop = angular.element(buttonId);
                 if (angular.isDefined(prop)) {
                 $animate.addClass(prop, 'animated shake').then(function () {
                 var prop = angular.element(buttonId);
                 //  TODO - not sure why but angular remove not working - jquery for now
                 $(buttonId).removeClass('animated');
                 $(buttonId).removeClass('shake');
                 $animate.removeClass(prop, 'animated shake');
                 });
                 }
                 }
                 }, 1);
                 */
            });
        }
    ]);

