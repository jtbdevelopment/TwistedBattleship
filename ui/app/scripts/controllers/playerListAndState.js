'use strict';

var yes = 'checkmark';
var no = 'close';

angular.module('tbs.controllers').controller('PlayerListAndStateCtrl',
    ['$scope', 'tbsGameDetails', 'jtbGameCache', 'jtbPlayerService', '$http', '$state', // 'twAds',
        function ($scope, tbsGameDetails, jtbGameCache, jtbPlayerService, $http, $state/*, twAds*/) {

            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = {};
            $scope.showActions = $scope.game.gamePhase === 'Challenged';
            $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);

            function updateGame(updatedGame) {
                var currentPhase = $scope.game.gamePhase;
                $scope.game = updatedGame;
                jtbGameCache.putUpdatedGame(updatedGame);
                if ($scope.game.gamePhase !== currentPhase) {
                    $state.go('app.' + $scope.game.gamePhase.toLowerCase(), {gameID: $scope.gameID});
                } else {
                    $state.go('app.games');
                }
            }

            $scope.statusColor = function (md5) {
                if (angular.isDefined(md5)) {
                    return $scope.game.playerStates[md5] === 'Pending' ? 'energized' :
                        $scope.game.playerStates[md5] === 'Accepted' ? 'balanced' :
                            'assertive';
                }
                return '';
            };

            $scope.statusIcon = function (md5) {
                if (angular.isDefined(md5)) {
                    return $scope.game.playerStates[md5] === 'Pending' ? 'help-circled' :
                        $scope.game.playerStates[md5] === 'Accepted' ? 'checkmark-circled' :
                            $scope.game.playerStates[md5] === 'Quit' ? 'flag' :
                                'close-circled';
                }
                return '';
            };

            $scope.accept = function () {
//  TODO
//                twAds.showAdPopup().result.then(function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/accept').success(function (data) {
                    updateGame(data);
                    //twGameDisplay.processGameUpdateForScope($scope, data);
                }).error(function (data, status, headers, config) {
                    //  TODO
                    //showMessage(data);
                    console.error(data + status + headers + config);
                });
//                });
            };

            $scope.reject = function () {
//  TODO
//                var modal = showConfirmDialog();
                //               modal.result.then(function () {
                $http.put(jtbPlayerService.currentPlayerBaseURL() + '/game/' + $scope.gameID + '/reject').success(function (data) {
                    updateGame(data);
                    //twGameDisplay.processGameUpdateForScope($scope, data);
                }).error(function (data, status, headers, config) {
//  TODO
                    //showMessage(data);
                    console.error(data + status + headers + config);
                });
//                });
            };

        }
    ]
);