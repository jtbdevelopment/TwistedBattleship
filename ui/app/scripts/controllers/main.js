'use strict';

//  Deals with the crud of initializing and various platform messages
angular.module('tbs.controllers').controller('MainCtrl',
    ['$window', '$rootScope', '$scope', '$timeout', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsVersionNotes',
        'tbsCircles', 'jtbGameFeatureService', 'tbsCellStates', 'tbsShips', 'jtbGamePhaseService', 'tbsAds', 'jtbPushNotifications',
        function ($window, $rootScope, $scope, $timeout, jtbPlayerService, jtbLiveGameFeed, $state, ENV, $document, tbsVersionNotes,
                  tbsCircles, jtbGameFeatureService, tbsCellStates, tbsShips, jtbGamePhaseService, tbsAds, jtbPushNotifications) {

            if (2 < 1) {
                console.log('have notifications' + jtbPushNotifications);
            }

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            $scope.showPlayer = function () {
                $state.go('app.playerDetails');
            };

            $scope.showAdminScreen = function () {
                $state.go('app.admin');
            };

            $scope.adminSwitchToStats = function () {
                $scope.adminShowStats = true;
                $scope.adminShowSwitch = false;
            };
            $scope.adminSwitchToSwitchPlayer = function () {
                $scope.adminShowStats = false;
                $scope.adminShowSwitch = true;
            };

            $scope.adminSwitchToStats();

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            //console.log('Have push' + jtbPushNotifications);
            $scope.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';
            $scope.player = jtbPlayerService.currentPlayer();
            $scope.showAdmin = angular.isDefined($scope.player) && $scope.player.adminUser;

            $scope.mobile = $window.location.href.indexOf('file') === 0;
            $scope.adImport = 'templates/ads/' + ($scope.mobile ? 'mobile' : 'non-mobile') + '.html';

            $scope.$on('playerUpdate', function (event, id, player) {
                if ($scope.player.id === id) {
                    $scope.player = player;
                    $scope.theme = player.gameSpecificPlayerAttributes.theme;
                    $scope.showAdmin = $scope.showAdmin || $scope.player.adminUser;  //  Once an admin always an admin for ui
                }
            });

            $scope.$on('playerLoaded', function () {
                $scope.player = jtbPlayerService.currentPlayer();
                $scope.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                $scope.showAdmin = $scope.showAdmin || $scope.player.adminUser;  //  Once an admin always an admin for ui

                //  TODO - preload ship images?
                tbsAds.initialize();
                //  Kick off some preemptive caching of info
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

            $scope.refreshGames = function () {
                $rootScope.$broadcast('refreshGames', '');
            };


        }
    ]
);