'use strict';

angular.module('tbs.services').factory('tbsActions',
    ['$http', '$state', 'jtbGameCache', 'jtbPlayerService', '$ionicHistory', '$ionicActionSheet', '$ionicLoading',
        function ($http, $state, jtbGameCache, jtbPlayerService, $ionicHistory, $ionicActionSheet, $ionicLoading) {
            function updateGame($scope, updatedGame) {
                var currentPhase = $scope.game.gamePhase;
                $scope.game = updatedGame;
                jtbGameCache.putUpdatedGame(updatedGame);
                //  TODO - review this concept
                if ($scope.game.gamePhase !== currentPhase) {
                    $ionicHistory.nextViewOptions({
                        disableBack: true
                    });
                    $state.go('app.' + $scope.game.gamePhase.toLowerCase(), {gameID: $scope.gameID});
                } else {
                    if ($scope.game.gamePhase !== 'Playing') {
                        $ionicHistory.nextViewOptions({
                            disableBack: true
                        });
                        $state.go('app.games');
                    }
                }
            }

            function gameURL($scope) {
                return jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/';
            }

            function createTarget(opponent, cell) {
                return {player: opponent, coordinate: {row: cell.y, column: cell.x}};
            }

            function showSending() {
                $ionicLoading.show({
                    template: 'Sending...'
                });
            }

            function takeAction($scope, action) {
                showSending();
                $http.put(gameURL($scope) + action).success(function (data) {
                    $ionicLoading.hide();
                    updateGame($scope, data);
                }).error(function (data, status, headers, config) {
                    $ionicLoading.hide();
                    //  TODO
                    console.error(data + status + headers + config);
                });
            }

            function makeMove($scope, action, opponent, cell) {
                showSending();
                $http.put(gameURL($scope) + action, createTarget(opponent, cell)).success(function (data) {
                    $ionicLoading.hide();
                    updateGame($scope, data);
                }).error(function (data, status, headers, config) {
                    $ionicLoading.hide();
                    //  TODO
                    console.error(data + status + headers + config);
                });
            }

            function confirmableAction(destructiveText, action, $scope) {
                $ionicActionSheet.show({
                    buttons: [],
                    destructiveText: destructiveText,
                    titleText: 'Are you sure?',
                    cancelText: 'Cancel',
                    destructiveButtonClicked: function () {
                        takeAction($scope, action);
                    }
                });
            }

            return {
                accept: function ($scope) {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                    takeAction($scope, 'accept');
//                });
                },

                reject: function ($scope) {
                    confirmableAction('Reject this game!', 'reject', $scope);
                },

                declineRematch: function ($scope) {
                    confirmableAction('Decline further rematches.', 'endRematch', $scope);
                },

                rematch: function ($scope) {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                    takeAction($scope, 'rematch');
//                });
                },

                quit: function ($scope) {
                    confirmableAction('Quit this game!', 'quit', $scope);
                },

                setup: function ($scope, positions) {
                    showSending();
                    $http.put(gameURL($scope) + 'setup', positions).success(function (data) {
                        $ionicLoading.hide();
                        updateGame($scope, data);
                    }).error(function (data, status, headers, config) {
                        $ionicLoading.hide();
                        //  TODO
                        console.error(data + status + headers + config);
                    });
                },

                fire: function ($scope, opponent, cell) {
                    makeMove($scope, 'fire', opponent, cell);
                },

                spy: function ($scope, opponent, cell) {
                    makeMove($scope, 'spy', opponent, cell);
                },

                repair: function ($scope, opponent, cell) {
                    makeMove($scope, 'repair', opponent, cell);
                },

                move: function ($scope, opponent, cell) {
                    makeMove($scope, 'move', opponent, cell);
                },

                ecm: function ($scope, opponent, cell) {
                    makeMove($scope, 'ecm', opponent, cell);
                }
            };
        }
    ]
);

