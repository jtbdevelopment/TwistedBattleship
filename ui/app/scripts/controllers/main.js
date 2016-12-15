'use strict';

//  Deals with the crud of initializing and various platform messages
var CURRENT_VERSION = '1.3';
var CURRENT_NOTES = 'Minor bug fixes.';
angular.module('tbs.controllers').controller('MainCtrl',
    ['$window', '$rootScope', '$scope', 'jtbPlayerService', 'jtbLiveGameFeed', '$state', 'ENV', 'tbsCircles',
        'jtbGameFeatureService', 'tbsCellStates', 'tbsShips', 'jtbGamePhaseService', 'jtbIonicAds', 'jtbPushNotifications',
        'tbsGameDetails', 'jtbIonicVersionNotesService',
        function ($window, $rootScope, $scope, jtbPlayerService, jtbLiveGameFeed, $state, ENV, tbsCircles,
                  jtbGameFeatureService, tbsCellStates, tbsShips, jtbGamePhaseService, jtbIonicAds, jtbPushNotifications,
                  tbsGameDetails, jtbIonicVersionNotesService) {

            var controller = this;
            controller.gameDetails = tbsGameDetails;

            if (2 < 1) {
                console.log('have notifications' + jtbPushNotifications);
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
                jtbIonicAds.initialize({
                    ios: {
                        banner: 'ca-app-pub-8812482609918940/2316719315',
                        interstitial: 'ca-app-pub-8812482609918940/9839986116'
                    },
                    android: {
                        banner: 'ca-app-pub-8812482609918940/3876007710',
                        interstitial: 'ca-app-pub-8812482609918940/5352740910'
                    }
                });
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

            controller.refreshGames = function () {
                $rootScope.$broadcast('refreshGames', '');
            };
        }
    ]
);