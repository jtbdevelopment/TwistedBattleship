'use strict';

var ALL = 'ALL';

angular.module('tbs.controllers').controller('GameCtrl',
    ['$rootScope', '$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', 'shipInfo', 'tbsShipGrid', '$ionicPopup', '$ionicLoading', '$timeout', 'tbsAds',
        function ($rootScope, $scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, shipInfo, tbsShipGrid, $ionicPopup, $ionicLoading, $timeout, tbsAds) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.playerKeys = Object.keys($scope.game.players);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = jtbPlayerService.currentPlayer();
            $scope.showing = ALL;
            $scope.showingSelf = false;

            $scope.shipHighlighted = false;

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
                var cell = tbsShipGrid.selectedCell();
                tbsActions.fire($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.move = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.move($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.spy = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.spy($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.missile = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.missile($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.repair = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.repair($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.ecm = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.ecm($scope.game, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.quit = function () {
                tbsActions.quit($scope.game);
            };

            $scope.changePlayer = function (md5) {
                if (md5 === ALL) {
                    $scope.showingSelf = true;
                    tbsShipGrid.placeShips(computeShipLocations());
                    tbsShipGrid.placeCellMarkers($scope.game.maskedPlayersState.consolidatedOpponentView.table);
                } else {
                    if ($scope.showingSelf) {
                        tbsShipGrid.placeShips(computeShipLocations());
                        tbsShipGrid.placeCellMarkers($scope.game.maskedPlayersState.opponentViews[md5].table);
                    } else {
                        tbsShipGrid.placeShips([]);
                        tbsShipGrid.placeCellMarkers($scope.game.maskedPlayersState.opponentGrids[md5].table);
                    }
                }
                $scope.shipHighlighted = (tbsShipGrid.selectedShip() !== null);
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

            function computeShipLocations() {
                //  TODO - overlap with setup
                var shipLocations = [];
                angular.forEach($scope.game.maskedPlayersState.shipStates, function (shipState) {
                    var shipDetails = null;
                    angular.forEach(shipInfo, function (ship) {
                        if (ship.ship === shipState.ship) {
                            shipDetails = ship;
                        }
                    });
                    if (shipDetails !== null) {
                        var horizontal = shipState.shipGridCells[0].row === shipState.shipGridCells[1].row;
                        var row = shipState.shipGridCells[0].row;
                        var column = shipState.shipGridCells[0].column;
                        shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipDetails});
                    }
                });
                return shipLocations;
            }

            function highlightCallback() {
                $timeout(function () {
                    $scope.shipHighlighted = (tbsShipGrid.selectedShip() !== null);
                });
            }

            $scope.$on('$ionicView.leave', function () {
                tbsShipGrid.stop();
            });

            $scope.$on('$ionicView.enter', function () {
                $ionicLoading.show({
                    template: 'Loading...'
                });
                tbsShipGrid.initialize($scope.game, [], [], function () {
                    $timeout(function () {
                        $scope.switchView(false);
                        if ($scope.game.gamePhase === 'Playing') {
                            tbsShipGrid.activateHighlighting(highlightCallback);
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