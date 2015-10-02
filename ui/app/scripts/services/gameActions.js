'use strict';

angular.module('tbs.services').factory('tbsActions',
    ['$http', '$state', 'jtbGameCache', 'jtbPlayerService', '$ionicActionSheet', '$ionicLoading', '$ionicPopup',
        function ($http, $state, jtbGameCache, jtbPlayerService, $ionicActionSheet, $ionicLoading, $ionicPopup) {
            function updateGame(updatedGame) {
                jtbGameCache.putUpdatedGame(updatedGame);
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
                    updateGame(data);
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
                    updateGame(data);
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
                updateCurrentView: function (oldGame, newGame) {
                    if (newGame.gamePhase !== oldGame.gamePhase) {
                        if (newGame.gamePhase !== 'Challenged' &&
                            newGame.gamePhase !== 'NextRoundStarted' &&
                            newGame.gamePhase !== 'Declined') {
                            $state.go('app.' + newGame.gamePhase.toLowerCase(), {gameID: newGame.id});
                        } else {
                            $state.go('app.games');
                        }
                    }
                },

                accept: function (game) {
                    takeAction(game, 'accept');
                },

                reject: function (game) {
                    confirmableAction('Reject this game!', 'reject', game);
                },

                declineRematch: function (game) {
                    confirmableAction('Decline further rematches.', 'endRematch', game);
                    $state.go('app.games');
                },

                rematch: function (game) {
                    takeAction(game, 'rematch');
                    $state.go('app.games');
                },

                quit: function (game) {
                    confirmableAction('Quit this game!', 'quit', game);
                },

                setup: function (game, positions) {
                    showSending();
                    $http.put(gameURL(game) + 'setup', positions).success(function (data) {
                        $ionicLoading.hide();
                        updateGame(data);
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

