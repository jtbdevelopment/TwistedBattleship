'use strict';

var ACTION_LOG_CHUNK_SIZE = 20;
angular.module('tbs.controllers').controller('ActionLogCtrl',
    ['$scope', '$state', 'tbsGameDetails', 'jtbGameCache',
        function ($scope, $state, tbsGameDetails, jtbGameCache) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;

            $scope.shownEntries = [];
            $scope.totalEntries = $scope.game.maskedPlayersState.actionLog.length;

            function loadMore() {
                if ($scope.game.maskedPlayersState.actionLog.length === $scope.shownEntries.length) {
                    return;
                }
                var index = $scope.game.maskedPlayersState.actionLog.length - $scope.shownEntries.length - ACTION_LOG_CHUNK_SIZE;
                var chunk = ACTION_LOG_CHUNK_SIZE;
                if (index < 0) {
                    chunk += index;
                    index = 0;
                }
                if (chunk < 0) {
                    return;
                }
                var newItems = $scope.game.maskedPlayersState.actionLog.slice(
                    index, index + chunk
                );
                $scope.shownEntries = $scope.shownEntries.concat(newItems);
            }

            $scope.$on('$ionicView.enter', function () {
                $scope.shownEntries = [];
                $scope.game = jtbGameCache.getGameForID($scope.gameID);
                $scope.totalEntries = $scope.game.maskedPlayersState.actionLog.length;
                loadMore();
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                    if ($scope.game.maskedPlayersState.actionLog.length !== $scope.totalEntries) {
                        var newEntries = $scope.game.maskedPlayersState.actionLog.length - $scope.totalEntries;
                        $scope.totalEntries = $scope.game.maskedPlayersState.actionLog.length;
                        $scope.shownEntries.concat($scope.maskedPlayersState.actionLog.slice(0, newEntries));
                    }
                }
            });

            $scope.hasMoreEntries = function () {
                return $scope.shownEntries.length !== $scope.totalEntries;
            };

            $scope.loadMore = function () {
                loadMore();
                $scope.$broadcast('scroll.infiniteScrollComplete');
            };
        }
    ]
);