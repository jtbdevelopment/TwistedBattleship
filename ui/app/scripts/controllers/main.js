'use strict';

//  Deals with the crud of initializing and various platform messages
var CURRENT_VERSION = '1.2';
var CURRENT_NOTES = 'Added new game play options - cruise missile attack and new ship options.  Also added a new pirate theme, see your profile in top bar.';
angular.module('tbs.controllers').controller('MainCtrl',
    ['$window', '$rootScope', '$ionicPopup', '$ionicLoading', '$scope', '$timeout', 'jtbPlayerService',
        'jtbLiveGameFeed', '$state', 'ENV', '$document', 'tbsCircles', 'jtbGameFeatureService',
        'tbsCellStates', 'tbsShips', 'jtbGamePhaseService', 'tbsAds', 'jtbPushNotifications', 'tbsGameDetails',
        'jtbIonicVersionNotesService',
        function ($window, $rootScope, $ionicPopup, $ionicLoading, $scope, $timeout, jtbPlayerService,
                  jtbLiveGameFeed, $state, ENV, $document, tbsCircles, jtbGameFeatureService,
                  tbsCellStates, tbsShips, jtbGamePhaseService, tbsAds, jtbPushNotifications, tbsGameDetails,
                  jtbIonicVersionNotesService) {

            var controller = this;
            controller.gameDetails = tbsGameDetails;

            if (2 < 1) {
                console.log('have notifications' + jtbPushNotifications);
            }

            function checkNetworkStatusAndLogin() {
                $state.go('network');
            }

            controller.showPlayer = function () {
                $state.go('app.playerDetails');
            };

            controller.showAdminScreen = function () {
                $state.go('app.admin');
            };

            controller.adminSwitchToStats = function () {
                controller.adminShowStats = true;
                controller.adminShowSwitch = false;
            };
            controller.adminSwitchToSwitchPlayer = function () {
                controller.adminShowStats = false;
                controller.adminShowSwitch = true;
            };

            controller.adminSwitchToStats();

            //  Set here to avoid causing circular dependency in app.js
            jtbLiveGameFeed.setEndPoint(ENV.apiEndpoint);

            //console.log('Have push' + jtbPushNotifications);
            controller.theme = angular.isDefined(jtbPlayerService.currentPlayer()) ?
                jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme : 'default-theme';
            controller.player = jtbPlayerService.currentPlayer();
            controller.showAdmin = angular.isDefined(controller.player) && controller.player.adminUser;

            controller.mobile = $window.location.href.indexOf('file') === 0;
            controller.adImport = 'templates/ads/' + (controller.mobile ? 'mobile' : 'non-mobile') + '.html';

            $scope.$on('playerUpdate', function (event, id, player) {
                if (angular.isUndefined(controller.player) || controller.player.id === id) {
                    controller.player = player;
                    controller.theme = player.gameSpecificPlayerAttributes.theme;
                    controller.showAdmin = controller.showAdmin || controller.player.adminUser;  //  Once an admin always an admin for ui
                }
            });

            $scope.$on('playerLoaded', function () {
                controller.player = jtbPlayerService.currentPlayer();
                controller.theme = jtbPlayerService.currentPlayer().gameSpecificPlayerAttributes.theme;
                controller.showAdmin = controller.showAdmin || controller.player.adminUser;  //  Once an admin always an admin for ui

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

                jtbIonicVersionNotesService.displayVersionNotesIfAppropriate(CURRENT_VERSION, CURRENT_NOTES);
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

            $scope.$on('GeneralError', function () {
                $ionicLoading.hide();
                $ionicPopup.alert({
                    title: 'There was a problem!',
                    template: 'Going to reconnect!'
                });
                checkNetworkStatusAndLogin();
            });

            $scope.$on('InvalidSession', function () {
                $ionicLoading.hide();
                if ($state.$current.name !== 'signin') {
                    checkNetworkStatusAndLogin();
                }
            });

            controller.refreshGames = function () {
                $rootScope.$broadcast('refreshGames', '');
            };


        }
    ]
);