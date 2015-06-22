'use strict';

var yes = 'checkmark';
var no = 'close';

angular.module('tbs.controllers').controller('PlayerListAndStateCtrl',
    ['$scope', 'tbsGameDetails', 'jtbGameCache', 'jtbPlayerService', '$http', '$state', // 'twAds',
        function ($scope, tbsGameDetails, jtbGameCache, jtbPlayerService, $http, $state/*, twAds*/) {

            $scope.gameID = $state.params.gameId;
            $scope.gameDetails = tbsGameDetails;
            $scope.player = {};
            $scope.showActions = false;

            function initialize() {
                $scope.game = jtbGameCache.getGameForID($scope.gameID);
                $scope.showActions = $scope.game.gamePhase === 'Challenged';
                $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);
                $scope.ecmEnabled = $scope.game.features.indexOf('ECMEnabled') >= 0 ? yes : no;
                $scope.spyingEnabled = $scope.game.features.indexOf('SpyEnabled') >= 0 ? yes : no;
                $scope.repairsEnabled = $scope.game.features.indexOf('EREnabled') >= 0 ? yes : no;
                $scope.moveEnabled = $scope.game.features.indexOf('EMEnabled') >= 0 ? yes : no;
                $scope.criticalsEnabled = $scope.game.features.indexOf('CriticalEnabled') >= 0 ? yes : no;
                $scope.gridSize = tbsGameDetails.shortenGridSize($scope.game);
                $scope.intel = $scope.game.features.indexOf('IsolatedIntel') >= 0 ? 'Isolated' : 'Shared';
                $scope.moves = $scope.game.features.indexOf('Single') >= 0 ? '1' : 'Per Ship';
            }

            function updateGame(updatedGame) {
                var currentPhase = $scope.game.gamePhase;
                $scope.game = updatedGame;
                jtbGameCache.putUpdatedGame(updatedGame);
                if ($scope.game !== currentPhase) {
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

            initialize();
        }
    ]
);