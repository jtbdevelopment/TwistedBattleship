'use strict';

angular.module('tbs.controllers').controller('MainCtrl',
    ['$scope', '$timeout', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsVersionNotes', 'tbsCircles', 'jtbGameFeatureService', 'tbsCellStates', 'tbsShips', 'jtbGamePhaseService', 'tbsAds', 'jtbPushNotifications',
        function ($scope, $timeout, jtbPlayerService, jtbLiveGameFeed, $state, ENV, $document, tbsVersionNotes, tbsCircles, jtbGameFeatureService, tbsCellStates, tbsShips, jtbGamePhaseService, tbsAds, jtbPushNotifications) {

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            $scope.showPlayer = function () {
                $state.go('app.playerDetails');
            };

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            console.log('Have push' + jtbPushNotifications);
            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';
            $scope.player = jtbPlayerService.currentPlayer();


            $scope.$on('playerLoaded', function () {
                $scope.player = jtbPlayerService.currentPlayer();

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
            var pausePromise;
            $document.bind('pause', function () {
                console.warn('pause detected');
                ++pauseResumeStack;
                pausePromise = $timeout(function () {
                    if (pauseResumeStack > 0) {
                        console.info('pauseResumeStack still in pause - shutting down livefeed');
                        pauseResumeStack = 0;
                        pausePromise = undefined;
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
                    if (pauseResumeStack === 0 && angular.isDefined(pausePromise)) {
                        console.info('clearing pause promise');
                        $timeout.cancel(pausePromise);
                        pausePromise = undefined;
                    }
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
        }
    ]
);