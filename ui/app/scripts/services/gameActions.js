'use strict';

angular.module('tbs.services').factory('tbsActions',
    ['$http', '$state', 'jtbGameCache', 'jtbPlayerService',
        function ($http, $state, jtbGameCache, jtbPlayerService) {
            function updateGame($scope, updatedGame) {
                var currentPhase = $scope.game.gamePhase;
                $scope.game = updatedGame;
                jtbGameCache.putUpdatedGame(updatedGame);
                if ($scope.game.gamePhase !== currentPhase) {
                    $state.go('app.' + $scope.game.gamePhase.toLowerCase(), {gameID: $scope.gameID});
                } else {
                    $state.go('app.games');
                }
            }

            function gameURL($scope) {
                return jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID;
            }

            return {
                accept: function ($scope) {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                    $http.put(gameURL($scope) + '/accept').success(function (data) {
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
//  TODO
//                var modal = showConfirmDialog();
                    //               modal.result.then(function () {
                    $http.put(gameURL($scope) + '/reject').success(function (data) {
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
//  TODO
//                var modal = showConfirmDialog();
                    //               modal.result.then(function () {
                    $http.put(gameURL($scope) + '/quit').success(function (data) {
                        updateGame($scope, data);
                        //twGameDisplay.processGameUpdateForScope($scope, data);
                    }).error(function (data, status, headers, config) {
//  TODO
                        //showMessage(data);
                        console.error(data + status + headers + config);
                    });
//                });
                },

                setup: function ($scope, positions) {
                    $http.put(gameURL($scope) + '/setup', positions).success(function (data) {
                        updateGame($scope, data);
                    }).error(function (data, status, headers, config) {
                        //  TODO
                        console.error(data + status + headers + config);
                    });
                }

            };
        }
    ]
);

