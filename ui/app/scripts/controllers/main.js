'use strict';

angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', '$timeout', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsVersionNotes', 'tbsCircles', 'jtbGameFeatureService', 'tbsCellStates', 'tbsShips', 'jtbGamePhaseService', 'tbsAds',
        function ($scope, $timeout, jtbPlayerService, jtbLiveGameFeed, $state, ENV, $document, tbsVersionNotes, tbsCircles, jtbGameFeatureService, tbsCellStates, tbsShips, jtbGamePhaseService, tbsAds) {

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';

            $scope.$on('playerLoaded', function () {
                //  TODO - preload ship images?
                tbsAds.initialize();
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

            var pauseResumeStack = 0;
            $document.bind('pause', function () {
                console.warn('pause detected');
                ++pauseResumeStack;
                $timeout(function () {
                    if (pauseResumeStack > 0) {
                        console.info('pauseResumeStack still in pause - shutting down livefeed');
                        pauseResumeStack = 0;
                        jtbLiveGameFeed.suspendFeed();
                    } else {
                        console.info('ignoring pauseResume, stack back to 0');
                    }
                }, 3 * 60 * 1000); //  delay between checks should not match delay between interstitials
            });

            $document.bind('resume', function () {
                console.warn('resume detected');
                if (pauseResumeStack > 0) {
                    console.info('pauseresume stack reduced');
                    --pauseResumeStack;
                } else {
                    console.info('pauseresumestack empty - full reconnect');
                    checkNetworkStatusAndLogin();
                }
            });

            $scope.$on('$cordovaNetwork:offline', function () {
                console.warn('offline');
                checkNetworkStatusAndLogin();
            });

            $scope.$on('InvalidSession', function () {
                if ($state.$current.name !== 'signin') {
                    checkNetworkStatusAndLogin();
                }
            });

            /*
             $scope.$on('$stateChangeStart', function (event, toState, toParams, fromState) {
             console.log('change start from ' + JSON.stringify(fromState) + ' to state ' + JSON.stringify(toState));
             });
             $scope.$on('$stateChangeSuccess', function (event, toState, toParams, fromState) {
             console.log('change success from ' + JSON.stringify(fromState) + ' to state ' + JSON.stringify(toState));
             });
             $scope.$on('$stateChangeError', function (event, toState, toParams, fromState) {
             console.log('change error from ' + JSON.stringify(fromState) + ' to state ' + JSON.stringify(toState));
             });
             */
        }
    ]
);