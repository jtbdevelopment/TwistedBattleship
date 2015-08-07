'use strict';

var ALL = 'ALL';
var VIEWING_SELF = 'Yourself viewed by:';
var VIEWING_OPPONENT = 'Your opponent(s):';

angular.module('tbs.controllers').controller('PlayGameCtrl',
    ['$rootScope', '$scope', 'tbsGameDetails', 'tbsActions', 'jtbGameCache', 'jtbPlayerService', '$state', '$ionicSideMenuDelegate', 'tbsShips', 'tbsShipGrid', '$ionicModal', // 'twAds',
        function ($rootScope, $scope, tbsGameDetails, tbsActions, jtbGameCache, jtbPlayerService, $state, $ionicSideMenuDelegate, tbsShips, tbsShipGrid, $ionicModal /*, twAds*/) {
            $ionicSideMenuDelegate.canDragContent(false);
            $scope.theme = 'default';
            $scope.gameID = $state.params.gameID;
            $scope.game = jtbGameCache.getGameForID($scope.gameID);
            $scope.gameDetails = tbsGameDetails;
            $scope.player = {};
            $scope.player = angular.copy(jtbPlayerService.currentPlayer(), $scope.player);
            $scope.showing = ALL;
            $scope.showingSelf = true;
            $scope.switchViewText = VIEWING_SELF;

            $scope.generalShipInfo = [];
            $scope.shipHighlighted = false;

            $scope.modalScope = $rootScope.$new();
            $ionicModal.fromTemplateUrl('templates/messageBox.html', {
                scope: $scope.modalScope,
                animation: 'slide-in-up'
            }).then(function (modal) {
                $scope.modal = modal;
                $scope.modalScope.close = function () {
                    $scope.modal.hide();
                };
            });

            $scope.fire = function () {
                var cell = tbsShipGrid.selectedCell();
                if (cell.x === -1 || cell.y === -1) {
                    //  TODO
                }
                tbsActions.fire($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.move = function () {
                var cell = tbsShipGrid.selectedCell();
                if (cell.x === -1 || cell.y === -1) {
                    //  TODO
                }
                tbsActions.move($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.spy = function () {
                var cell = tbsShipGrid.selectedCell();
                if (cell.x === -1 || cell.y === -1) {
                    //  TODO
                }
                tbsActions.spy($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.repair = function () {
                var cell = tbsShipGrid.selectedCell();
                if (cell.x === -1 || cell.y === -1) {
                    //  TODO
                }
                tbsActions.repair($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.ecm = function () {
                var cell = tbsShipGrid.selectedCell();
                if (cell.x === -1 || cell.y === -1) {
                    //  TODO
                }
                tbsActions.ecm($scope, $scope.showingSelf ? $scope.player.md5 : $scope.showing, cell);
            };

            $scope.quit = function () {
                tbsActions.quit($scope);
            };

            $scope.changePlayer = function (md5) {
                if (md5 === ALL) {
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

            $scope.switchView = function () {
                $scope.showingSelf = !($scope.showingSelf);
                if (!$scope.showingSelf && $scope.showing === ALL) {
                    var keys = Object.keys($scope.game.players);
                    if (keys[0] === $scope.player.md5) {
                        $scope.showing = keys[1];
                    } else {
                        $scope.showing = keys[0];
                    }
                }
                $scope.changePlayer($scope.showing);
                $scope.switchViewText = $scope.showingSelf ? VIEWING_SELF : VIEWING_OPPONENT;
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

            tbsShips.ships().then(
                function (generalShipInfo) {
                    $scope.generalShipInfo = generalShipInfo;
                    tbsShipGrid.initialize($scope.theme, $scope.game, [], [], function () {
                        $scope.changePlayer($scope.showing);
                        tbsShipGrid.activateHighlighting(highlightCallback);
                    });
                },
                function () {
                    //  TODO
                }
            );

            $scope.$on('$ionicView.leave', function () {
                tbsShipGrid.stop();
            });
            $scope.$on('$destroy', function () {
                $scope.modal.remove();
            });

            $scope.$on('gameUpdated', function (event, oldGame, newGame) {
                if ($scope.gameID === newGame.id) {
                    $scope.game = newGame;
                    $scope.changePlayer($scope.showing);
                    var message = $scope.game.maskedPlayersState.lastActionMessage;
                    if (angular.isDefined(message) && message !== '') {
                        $scope.modalScope.lastMessage = message;
                        $scope.modal.show();
                    }
                }
            });
        }
    ]
);