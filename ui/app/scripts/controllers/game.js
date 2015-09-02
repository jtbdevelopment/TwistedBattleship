'use strict';

var ALL = 'ALL';

//  TODO - need to disable play between click and response
angular.module('tbs.controllers').controller('GameCtrl',
    ['$rootScope', '$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', 'shipInfo', 'tbsShipGrid', '$ionicPopup', // 'twAds',
        function ($rootScope, $scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, shipInfo, tbsShipGrid, $ionicPopup /*, twAds*/) {
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.playerKeys = Object.keys($scope.game.players);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = {};
            $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);
            $scope.generalShipInfo = shipInfo;
            $scope.showing = ALL;
            $scope.showingSelf = false;

            $scope.shipHighlighted = false;

            $scope.showHelp = function () {
                $state.transitionTo('app.help');
            };

            tbsShipGrid.initialize($scope.game, [], [], function () {
                $scope.changePlayer($scope.showing);
                if ($scope.game.gamePhase === 'Playing') {
                    tbsShipGrid.activateHighlighting(highlightCallback);
                }
                $scope.switchView(false);
                $scope.$apply();
            });

            $scope.declineRematch = function () {
                //  TODO - winds up on weird location
                tbsActions.declineRematch($scope);
            };

            $scope.rematch = function () {
                //  TODO - winds up on weird location
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
                $scope.showing = md5;
            };

            $scope.switchView = function (showingSelf) {
                //  TODO - if switching from attack to defend, does not highlight ship if appropriate
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
                angular.forEach($scope.game.maskedPlayersState.shipStates, function (value, key) {
                    var shipInfo = $scope.generalShipInfo.find(function (info) {
                        return info.ship === key;
                    });
                    var horizontal = value.shipGridCells[0].row === value.shipGridCells[1].row;
                    var row = value.shipGridCells[0].row;
                    var column = value.shipGridCells[0].column;
                    shipLocations.push({horizontal: horizontal, row: row, column: column, shipInfo: shipInfo});
                });
                return shipLocations;
            }

            function highlightCallback() {
                $scope.shipHighlighted = (tbsShipGrid.selectedShip() !== null);
                $scope.$apply();
            }

            $scope.$on('$ionicView.leave', function () {
                tbsShipGrid.stop();
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
                        var alertPopup = $ionicPopup.alert({
                            title: 'Move Completed!',
                            template: message
                        });
                        alertPopup.then(function () {
                        });
                    }
                }
            });
        }
    ]
);