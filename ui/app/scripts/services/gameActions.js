'use strict';

angular.module('tbs.services').factory('tbsActions',
    ['$http', '$state', 'jtbGameCache', 'jtbPlayerService', '$ionicHistory', '$ionicActionSheet',
        function ($http, $state, jtbGameCache, jtbPlayerService, $ionicHistory, $ionicActionSheet) {
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

            function makeMove($scope, action, opponent, cell) {
                $http.put(gameURL($scope) + action, createTarget(opponent, cell)).success(function (data) {
                    updateGame($scope, data);
                }).error(function (data, status, headers, config) {
                    //  TODO
                    console.error(data + status + headers + config);
                });
            }

            return {
                accept: function ($scope) {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                    $http.put(gameURL($scope) + 'accept').success(function (data) {
                        updateGame($scope, data);
                        //twGameDisplay.processGameUpdateForScope($scope, data);
                    }).error(function (data, status, headers, config) {
                        //  TODO
                        //showMessage(data);
                        console.error(data + status + headers + config);
                    });
//                });
                },

                reject: function ($scope) {
                    $ionicActionSheet.show({
                        buttons: [],
                        destructiveText: 'Reject this game!',
                        titleText: 'Are you sure?',
                        cancelText: 'Cancel',
                        destructiveButtonClicked: function () {
                            $http.put(gameURL($scope) + 'reject').success(function (data) {
                                updateGame($scope, data);
                            }).error(function (data, status, headers, config) {
                                //  TODO
                                //showMessage(data);
                                console.error(data + status + headers + config);
                            });
                        }
                    });
                },

                declineRematch: function ($scope) {
                    $ionicActionSheet.show({
                        buttons: [],
                        destructiveText: 'Decline further rematches.',
                        titleText: 'Are you sure?',
                        cancelText: 'Cancel',
                        destructiveButtonClicked: function () {
                            $http.put(gameURL($scope) + 'endRematch').success(function (data) {
                                updateGame($scope, data);
                            }).error(function (data, status, headers, config) {
                                //  TODO
                                //showMessage(data);
                                console.error(data + status + headers + config);
                            });
                        }
                    });
                },

                rematch: function ($scope) {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                    $http.put(gameURL($scope) + 'rematch').success(function (data) {
                        updateGame($scope, data);
                        //twGameDisplay.processGameUpdateForScope($scope, data);
                    }).error(function (data, status, headers, config) {
                        //  TODO
                        //showMessage(data);
                        console.error(data + status + headers + config);
                    });
//                });
                },

                quit: function ($scope) {
                    $ionicActionSheet.show({
                        buttons: [],
                        destructiveText: 'Quit this game!',
                        titleText: 'Are you sure?',
                        cancelText: 'Cancel',
                        destructiveButtonClicked: function () {
                            $http.put(gameURL($scope) + 'quit').success(function (data) {
                                updateGame($scope, data);
                            }).error(function (data, status, headers, config) {
                                //  TODO
                                //showMessage(data);
                                console.error(data + status + headers + config);
                            });
                        }
                    });
                },

                setup: function ($scope, positions) {
                    $http.put(gameURL($scope) + 'setup', positions).success(function (data) {
                        updateGame($scope, data);
                    }).error(function (data, status, headers, config) {
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

