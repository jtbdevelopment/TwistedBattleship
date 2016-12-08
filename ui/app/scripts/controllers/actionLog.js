'use strict';

var ACTION_LOG_CHUNK_SIZE = 20;
angular.module('tbs.controllers').controller('ActionLogCtrl',
    ['$scope', '$state', 'tbsGameDetails', 'jtbGameCache',
        function ($scope, $state, tbsGameDetails, jtbGameCache) {
            var controller = this;
            controller.gameID = $state.params.gameID;
            controller.gameDetails = tbsGameDetails;

            function initialize() {
                controller.game = jtbGameCache.getGameForID(controller.gameID);
                controller.shownEntries = [];
                controller.totalEntries = controller.game.maskedPlayersState.actionLog.length;
            }

            initialize();

            function loadMore() {
                if (controller.game.maskedPlayersState.actionLog.length === controller.shownEntries.length) {
                    return;
                }
                var index = controller.game.maskedPlayersState.actionLog.length - controller.shownEntries.length - ACTION_LOG_CHUNK_SIZE;
                var chunk = ACTION_LOG_CHUNK_SIZE;
                if (index < 0) {
                    chunk += index;
                    index = 0;
                }
                if (chunk < 0) {
                    return;
                }
                var newItems = controller.game.maskedPlayersState.actionLog.slice(
                    index, index + chunk
                );
                controller.shownEntries = controller.shownEntries.concat(newItems);
            }

            $scope.$on('$ionicView.enter', function () {
                initialize();
                loadMore();
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if (controller.gameID === newGame.id) {
                    controller.game = newGame;
                    if (controller.game.maskedPlayersState.actionLog.length !== controller.totalEntries) {
                        var newEntries = controller.game.maskedPlayersState.actionLog.length - controller.totalEntries;
                        controller.totalEntries = controller.game.maskedPlayersState.actionLog.length;
                        controller.shownEntries = controller.shownEntries.concat(controller.game.maskedPlayersState.actionLog.slice(0, newEntries));
                    }
                }
            });

            controller.hasMoreEntries = function () {
                return controller.shownEntries.length !== controller.totalEntries;
            };

            controller.loadMore = function () {
                loadMore();
                $scope.$broadcast('scroll.infiniteScrollComplete');
            };
        }
    ]
);