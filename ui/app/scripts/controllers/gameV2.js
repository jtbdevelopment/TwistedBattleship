'use strict';

var ALL = 'ALL';

angular.module('tbs.controllers').controller('GameV2Ctrl',
    ['$scope', '$http', 'jtbIonicGameActions', 'jtbGameCache', 'jtbPlayerService', '$state', 'tbsShipGridV2',
        '$ionicPopup', '$ionicLoading', '$timeout', 'jtbIonicAds',
        function ($scope, $http, jtbIonicGameActions, jtbGameCache, jtbPlayerService, $state, tbsShipGridV2,
                  $ionicPopup, $ionicLoading, $timeout, jtbIonicAds) {
            var controller = this;
            controller.gameID = $state.params.gameID;
            controller.game = jtbGameCache.getGameForID(controller.gameID);
            controller.playerKeys = Object.keys(controller.game.players);
            controller.player = jtbPlayerService.currentPlayer();
            controller.showing = ALL;
            controller.showingSelf = false;
            controller.actions = jtbIonicGameActions;

            controller.shipHighlighted = false;
            controller.selectedCell = undefined;

            controller.showActionLog = function () {
                $state.go('app.actionLog', {gameID: controller.gameID});
            };

            controller.showHelp = function () {
                $state.go('app.playhelp');
            };

            controller.showDetails = function () {
                $state.go('app.gameDetails', {gameID: controller.gameID});
            };

            function createTarget(opponent, cell) {
                return {player: opponent, coordinate: {row: cell.row, column: cell.column}};
            }

            function makeMove(action) {
                jtbIonicGameActions.wrapActionOnGame(
                    $http.put(jtbIonicGameActions.getGameURL(controller.game) + action,
                        createTarget(
                            controller.showingSelf ? controller.player.md5 : controller.showing,
                            controller.selectedCell))
                );
            }

            controller.fire = function () {
                makeMove('fire');
            };

            controller.move = function () {
                makeMove('move');
            };

            controller.spy = function () {
                makeMove('spy');
            };

            controller.missile = function () {
                makeMove('missile');
            };

            controller.repair = function () {
                makeMove('repair');
            };

            controller.ecm = function () {
                makeMove('ecm');
            };

            controller.changePlayer = function (md5) {
                controller.shipHighlighted = false;
                controller.selectedCell = undefined;
                controller.selectedShip = undefined;
                if (md5 === ALL) {
                    controller.showingSelf = true;
                    tbsShipGridV2.placeShips(controller.game.maskedPlayersState.shipStates);
                    tbsShipGridV2.placeCellMarkers(controller.game.maskedPlayersState.consolidatedOpponentView.table);
                } else {
                    if (controller.showingSelf) {
                        tbsShipGridV2.placeShips(controller.game.maskedPlayersState.shipStates);
                        tbsShipGridV2.placeCellMarkers(controller.game.maskedPlayersState.opponentViews[md5].table);
                    } else {
                        tbsShipGridV2.placeShips([]);
                        tbsShipGridV2.placeCellMarkers(controller.game.maskedPlayersState.opponentGrids[md5].table);
                    }
                }
                controller.showing = md5;
            };

            controller.switchView = function (showingSelf) {
                controller.showingSelf = showingSelf;
                if (!controller.showingSelf && controller.showing === ALL) {
                    if (controller.playerKeys[0] === controller.player.md5) {
                        controller.showing = controller.playerKeys[1];
                    } else {
                        controller.showing = controller.playerKeys[0];
                    }
                }
                controller.changePlayer(controller.showing);
            };

            function cellSelectionCB(cell, ship) {
                $timeout(function () {
                    controller.shipHighlighted = angular.isDefined(ship);
                    controller.selectedCell = cell;
                });
            }

            $scope.$on('$ionicView.leave', function () {
                tbsShipGridV2.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGridV2.initialize(controller.game, [], [], function () {
                    $timeout(function () {
                        controller.switchView(false);
                        if (controller.game.gamePhase === 'Playing') {
                            tbsShipGridV2.enableCellSelecting(cellSelectionCB);
                        } else {
                            if (controller.game.gamePhase === 'RoundOver') {
                                if (controller.game.winningPlayer === controller.player.md5) {
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
                if (controller.gameID === newGame.id) {
                    controller.game = newGame;
                    controller.changePlayer(controller.showing);
                    if (oldGame.gamePhase === newGame.gamePhase) {
                        if (oldGame.currentPlayer === controller.player.md5 && newGame.currentPlayer !== controller.player.md5) {
                            jtbIonicAds.showInterstitial();
                        }
                    }
                }
            });
        }
    ]
);