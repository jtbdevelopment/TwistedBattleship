'use strict';

angular.module('tbs.services').factory('tbsActions',
    ['$http', '$state', 'jtbGameCache', 'jtbPlayerService', '$ionicActionSheet', '$ionicLoading', '$ionicPopup',
        function ($http, $state, jtbGameCache, jtbPlayerService, $ionicActionSheet, $ionicLoading, $ionicPopup) {
            function updateGame(game, updatedGame) {
                var previousPhase = game.gamePhase;
                jtbGameCache.putUpdatedGame(updatedGame);
                if (updatedGame.gamePhase !== previousPhase &&
                    updatedGame.gamePhase !== 'Challenged' &&
                    updatedGame.gamePhase !== 'NextRoundStarted' &&
                    updatedGame.gamePhase !== 'Declined') {
                    $state.go('app.' + updatedGame.gamePhase.toLowerCase(), {gameID: game.id});
                } else {
                    if (game.gamePhase !== 'Playing') {
                        $state.go('app.games');
                    }
                }
            }

            function gameURL(game) {
                return jtbPlayerService.currentPlayerBaseURL() + '/game/' + game.id + '/';
            }

            function createTarget(opponent, cell) {
                return {player: opponent, coordinate: {row: cell.y, column: cell.x}};
            }

            function showSending() {
                $ionicLoading.show({
                    template: 'Sending...'
                });
            }

            function takeAction(game, action) {
                showSending();
                $http.put(gameURL(game) + action).success(function (data) {
                    $ionicLoading.hide();
                    updateGame(game, data);
                }).error(function (data, status, headers, config) {
                    $ionicLoading.hide();
                    $ionicPopup.alert({
                        title: 'Error updating game!',
                        template: data
                    }).then(function () {
                    });
                    console.error(data + status + headers + config);
                });
            }

            function makeMove(game, action, opponent, cell) {
                showSending();
                $http.put(gameURL(game) + action, createTarget(opponent, cell)).success(function (data) {
                    $ionicLoading.hide();
                    updateGame(game, data);
                }).error(function (data, status, headers, config) {
                    $ionicLoading.hide();
                    $ionicPopup.alert({
                        title: 'Error updating game!',
                        template: data
                    }).then(function () {
                    });
                    console.error(data + status + headers + config);
                });
            }

            function confirmableAction(destructiveText, action, game) {
                $ionicActionSheet.show({
                    buttons: [],
                    destructiveText: destructiveText,
                    titleText: 'Are you sure?',
                    cancelText: 'Cancel',
                    destructiveButtonClicked: function () {
                        takeAction(game, action);
                    }
                });
            }

            return {
                accept: function (game) {
                    takeAction(game, 'accept');
                },

                reject: function (game) {
                    confirmableAction('Reject this game!', 'reject', game);
                },

                declineRematch: function (game) {
                    confirmableAction('Decline further rematches.', 'endRematch', game);
                },

                rematch: function (game) {
                    takeAction(game, 'rematch');
                },

                quit: function (game) {
                    confirmableAction('Quit this game!', 'quit', game);
                },

                setup: function (game, positions) {
                    showSending();
                    $http.put(gameURL(game) + 'setup', positions).success(function (data) {
                        $ionicLoading.hide();
                        updateGame(game, data);
                    }).error(function (data, status, headers, config) {
                        $ionicLoading.hide();
                        $ionicPopup.alert({
                            title: 'Error updating game!',
                            template: data
                        }).then(function () {
                        });
                        console.error(data + status + headers + config);
                    });
                },

                fire: function (game, opponent, cell) {
                    makeMove(game, 'fire', opponent, cell);
                },

                spy: function (game, opponent, cell) {
                    makeMove(game, 'spy', opponent, cell);
                },

                repair: function (game, opponent, cell) {
                    makeMove(game, 'repair', opponent, cell);
                },

                move: function (game, opponent, cell) {
                    makeMove(game, 'move', opponent, cell);
                },

                ecm: function (game, opponent, cell) {
                    makeMove(game, 'ecm', opponent, cell);
                }
            };
        }
    ]
);

