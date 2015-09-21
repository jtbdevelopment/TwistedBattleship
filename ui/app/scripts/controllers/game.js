'use strict';

var ALL = 'ALL';

angular.module('tbs.controllers').controller('GameCtrl',
    ['$rootScope', '$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', 'shipInfo', 'tbsShipGrid', '$ionicPopup', '$ionicLoading', '$timeout', // 'twAds',
        function ($rootScope, $scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, shipInfo, tbsShipGrid, $ionicPopup, $ionicLoading, $timeout /*, twAds*/) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.playerKeys = Object.keys($scope.game.players);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = jtbPlayerService.currentPlayer();
            $scope.generalShipInfo = shipInfo;
            $scope.showing = ALL;
            $scope.showingSelf = false;

            $scope.shipHighlighted = false;

            $scope.showHelp = function () {
                $state.go('app.playhelp');
            };

            $scope.declineRematch = function () {
                tbsActions.declineRematch($scope);
            };

            $scope.rematch = function () {
                tbsActions.rematch($scope);
            };

            $scope.fire = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.fire($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.move = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.move($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.spy = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.spy($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.repair = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.repair($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.ecm = function () {
                var cell = tbsShipGrid.selectedCell();
                tbsActions.ecm($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.quit = function () {
                tbsActions.quit($scope);
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
                    var shipInfo = null;
                    angular.forEach($scope.generalShipInfo, function (ship) {
                        if (ship.ship === shipState.ship) {
                            shipInfo = ship;
                        }
                    });
                    if (shipInfo !== null) {
                        var horizontal = shipState.shipGridCells[0].row === shipState.shipGridCells[1].row;
                        var row = shipState.shipGridCells[0].row;
                        var column = shipState.shipGridCells[0].column;
                        shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipInfo});
                    }
                });
                return shipLocations;
            }

            function highlightCallback() {
                $timeout(function() {
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
                    $timeout(function() {
                        if ($scope.game.gamePhase === 'Playing') {
                            tbsShipGrid.activateHighlighting(highlightCallback);
                        }
                        $scope.switchView(false);
                        $ionicLoading.hide();
                    });
                });
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    //  TODO - show ad if turn over
                    $scope.game = newGame;
                    $scope.changePlayer($scope.showing);
                    var message = $scope.game.maskedPlayersState.lastActionMessage;
                    if ($scope.game.gamePhase !== 'Playing') {
                        tbsShipGrid.deactivateHighlighting();
                    }
                    if (angular.isDefined(message) && message !== '') {
                        $ionicPopup.alert({
                            title: 'Move Completed!',
                            template: message
                        }).then(function () {
                        });
                    }
                }
            });
        }
    ]
);