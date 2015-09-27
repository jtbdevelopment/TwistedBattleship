'use strict';

angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsVersionNotes', 'tbsCircles', 'jtbGameFeatureService', 'tbsCellStates', 'tbsShips', 'jtbGamePhaseService',
        function ($scope, jtbPlayerService, jtbLiveGameFeed, $state, ENV, $document, tbsVersionNotes, tbsCircles, jtbGameFeatureService, tbsCellStates, tbsShips, jtbGamePhaseService) {

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                //  Kick off some precaching of info
                tbsCircles.circles().then(function () {
                });
                jtbGameFeatureService.features().then(function () {
                });
                tbsCellStates.cellStates().then(function () {
                });
                tbsShips.ships().then(function () {
                });
                jtbGamePhaseService.phases().then(function () {
                });
                tbsVersionNotes.showReleaseNotes();
            });

            $document.bind('pause', function () {
                jtbLiveGameFeed.suspendFeed();
            });

            $document.bind('resume', function () {
                checkNetworkStatusAndLogin();
            });

            $scope.$on('$cordovaNetwork:offline', function () {
                checkNetworkStatusAndLogin();
            });

            $scope.$on('InvalidSession', function () {
                if ($state.$current.name !== 'signin') {
                    checkNetworkStatusAndLogin();
                }
            });

            $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState) {
                console.log('change start from ' + fromState + ' to state ' + toState);
            });
            $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
                console.log('change success from ' + fromState + ' to state ' + toState);
            });
            $scope.$on('$stateChangeError', function (event, toState, toParams, fromState) {
                console.log('change error from ' + fromState + ' to state ' + toState);
            });
        }
    ]
);