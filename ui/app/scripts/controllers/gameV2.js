'use strict';

var ALL = 'ALL';

angular.module('tbs.controllers').controller('GameV2Ctrl',
    ['$rootScope', '$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', 'tbsShipGridV2', '$ionicPopup', '$ionicLoading', '$timeout', 'tbsAds',
        function ($rootScope, $scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, tbsShipGridV2, $ionicPopup, $ionicLoading, $timeout, tbsAds) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.playerKeys = Object.keys($scope.game.players);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = jtbPlayerService.currentPlayer();
            $scope.showing = ALL;
            $scope.showingSelf = false;

            $scope.shipHighlighted = false;
            $scope.selectedCell = undefined;

            $scope.showActionLog = function () {
                $state.go('app.actionLog', {gameID: $scope.gameID});
            };

            $scope.showHelp = function () {
                $state.go('app.playhelp');
            };

            $scope.showDetails = function () {
                $state.go('app.gameDetails', {gameID: $scope.gameID});
            };

            $scope.declineRematch = function () {
                tbsActions.declineRematch($scope.game);
            };

            $scope.rematch = function () {
                tbsActions.rematch($scope.game);
            };

            $scope.fire = function () {
                tbsActions.fire($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.move = function () {
                tbsActions.move($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.spy = function () {
                tbsActions.spy($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.missile = function () {
                tbsActions.missile($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.repair = function () {
                tbsActions.repair($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.ecm = function () {
                tbsActions.ecm($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, $scope.selectedCell);
            };

            $scope.quit = function () {
                tbsActions.quit($scope.game);
            };

            $scope.changePlayer = function (md5) {
                $scope.shipHighlighted = false;
                $scope.selectedCell = undefined;
                $scope.selectedShip = undefined;
                if (md5 === ALL) {
                    $scope.showingSelf = true;
                    tbsShipGridV2.placeShips($scope.game.maskedPlayersState.shipStates);
                    tbsShipGridV2.placeCellMarkers($scope.game.maskedPlayersState.consolidatedOpponentView.table);
                } else {
                    if ($scope.showingSelf) {
                        tbsShipGridV2.placeShips($scope.game.maskedPlayersState.shipStates);
                        tbsShipGridV2.placeCellMarkers($scope.game.maskedPlayersState.opponentViews[md5].table);
                    } else {
                        tbsShipGridV2.placeShips([]);
                        tbsShipGridV2.placeCellMarkers($scope.game.maskedPlayersState.opponentGrids[md5].table);
                    }
                }
                $scope.showing = md5;
            };

            $scope.switchView = function (showingSelf) {
                $scope.showingSelf = showingSelf;
                if (!$scope.showingSelf && $scope.showing === ALL) {
                    if ($scope.playerKeys[0] === $scope.player.md5) {
                        $scope.showing = $scope.playerKeys[1];
                    } else {
                        $scope.showing = $scope.playerKeys[0];
                    }
                }
                $scope.changePlayer($scope.showing);
            };

            function cellSelectionCB(cell, ship) {
                $timeout(function () {
                    $scope.shipHighlighted = angular.isDefined(ship);
                    $scope.selectedCell = cell;
                });
            }

            $scope.$on('$ionicView.leave', function () {
                tbsShipGridV2.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGridV2.initialize($scope.game, [], [], function () {
                    $timeout(function () {
                        $scope.switchView(false);
                        if ($scope.game.gamePhase === 'Playing') {
                            tbsShipGridV2.enableCellSelecting(cellSelectionCB);
                        } else {
                            if ($scope.game.gamePhase === 'RoundOver') {
                                if ($scope.game.winningPlayer === $scope.player.md5) {
                                    $ionicPopup.alert({
                                        title: 'Game is over!',
                                        template: 'Congratulations Winner!'
                                    });
                                } else {
                                    $ionicPopup.alert({
                                        title: 'Game is over!',
                                        template: 'Better luck next time...'
                                    });
                                }
                            }
                        }
                        $ionicLoading.hide();
                    });
                });
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                    $scope.changePlayer($scope.showing);
                    if (oldGame.gamePhase === newGame.gamePhase) {
                        if (oldGame.currentPlayer === $scope.player.md5 && newGame.currentPlayer !== $scope.player.md5) {
                            tbsAds.showInterstitial();
                        }
                    } else {
                        tbsActions.updateCurrentView(oldGame, newGame);
                    }
                }
            });
        }
    ]
);