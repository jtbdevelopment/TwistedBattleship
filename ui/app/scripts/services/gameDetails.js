'use strict';

//  TODO - review each is used
angular.module('tbs.services').factory('tbsGameDetails',
    function () {
        var iconMap = {
            'SpyEnabled': 'eye',
            'ECMEnabled': 'eye-disabled',
            'CriticalEnabled': 'alert',
            'SharedIntel': 'images',
            'IsolatedIntel': 'image',
            'EREnabled': 'wrench',
            'EMEnabled': 'shuffle',
            'Single': 'chatbox',
            'PerShip': 'chatboxes',
            'Grid10x10': 'crop',
            'Grid15x15': 'crop',
            'Grid20x20': 'crop'
        };

        function checkParams(game, md5) {
            return !(angular.isUndefined(game) || angular.isUndefined(md5) || md5.trim() === '');
        }

        return {
            iconForFeature: function (feature) {
                return iconMap[feature];
            },

            ecmPossible: function (game, md5) {
                if (!this.playerCanPlay(game, md5)) {
                    return false;
                }
                return game.maskedPlayersState.ecmsRemaining > 0 && game.remainingMoves >= game.movesForSpecials;
            },

            spyPossible: function (game, md5) {
                if (!this.playerCanPlay(game, md5)) {
                    return false;
                }
                return game.maskedPlayersState.spysRemaining > 0 && game.remainingMoves >= game.movesForSpecials;
            },

            repairPossible: function (game, md5) {
                if (!this.playerCanPlay(game, md5)) {
                    return false;
                }
                return game.maskedPlayersState.emergencyRepairsRemaining > 0 && game.remainingMoves >= game.movesForSpecials;
            },

            evasiveMovePossible: function (game, md5) {
                if (!this.playerCanPlay(game, md5)) {
                    return false;
                }
                return game.maskedPlayersState.evasiveManeuversRemaining > 0 && game.remainingMoves >= game.movesForSpecials;
            },

            playerRematchPossible: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return false;
                }

                return game.gamePhase === 'RoundOver';
            },

            playerChallengeResponseNeeded: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return false;
                }

                if (game.gamePhase !== 'Challenged') {
                    return false;
                }

                return game.playerStates[md5] === 'Pending';
            },

            playerCanPlay: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return false;
                }

                if (game.gamePhase !== 'Playing') {
                    return false;
                }

                return md5 === game.currentPlayer;
            },

            playerSetupEntryRequired: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return false;
                }

                if (game.gamePhase !== 'Setup') {
                    return false;
                }

                return game.playersSetup[md5] === false;
            },

            gameEndForPlayer: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return '';
                }

                if (game.gamePhase !== 'Playing' && game.gamePhase !== 'RoundOver' && game.gamePhase !== 'NextRoundStarted') {
                    return '';
                }

                if (game.playersAlive[md5] === false) {
                    return 'Defeated!';
                } else {
                    if (game.gamePhase === 'Playing') {
                        return 'Still Playing.';
                    } else {
                        return 'Winner!';
                    }
                }
            },

            gameScoreForPlayer: function (game, md5) {
                if (!checkParams(game, md5)) {
                    return '';
                }

                return game.playersScore[md5];
            },

            profileForPlayer: function (game, md5) {
                if (checkParams(game, md5)) {
                    if (angular.isDefined(game.playerProfiles[md5])) {
                        return game.playerProfiles[md5];
                    }
                }
                return '';
            },

            imageForPlayer: function (game, md5) {
                if (checkParams(game, md5)) {
                    if (angular.isDefined(game.playerImages[md5]) && game.playerImages[md5] !== null) {
                        return game.playerImages[md5];
                    }
                }
                //  TODO
                return 'images/ionic.png';
            },

            stateIconForPlayer: function (game, md5) {
                if (checkParams(game, md5)) {
                    switch (game.playerStates[md5]) {
                        case 'Quit':
                            return 'flag';
                        case 'Pending':
                            return 'speakerphone';
                        case 'Accepted':
                            return 'thumbsup';
                        case 'Rejected':
                            return 'thumbsdown';
                    }
                }
                return 'help';
            },

            gameEndIconForPlayer: function (game, md5) {
                if (checkParams(game, md5)) {
                    switch (this.gameEndForPlayer(game, md5)) {
                        case 'Winner!':
                            return 'ribbon-a';
                        case 'Defeated!':
                            return 'sad-outline';
                        case 'Still Playing.':
                            return 'load-a';
                    }
                }
                return 'help';
            },

            shortenGridSize: function (game) {
                var val = '';
                if (checkParams(game, 'DUMMY')) {
                    return game.gridSize + 'x' + game.gridSize;
                }
                return val;
            },

            shortGameDescription: function (game, md5) {
                if (checkParams(game, md5)) {
                    var result = {
                        sizeText: '',
                        actionsText: '',
                        icons: [],
                        playerAction: false
                    };
                    result.sizeText = this.shortenGridSize(game);
                    angular.forEach(game.features.sort(), function (feature) {
                        switch (feature) {
                            case 'PerShip':
                                result.actionsText = 'Multiple';
                                break;
                            case 'Single':
                                result.actionsText = 'Single';
                                break;
                            default:
                                if (angular.isDefined(iconMap[feature])) {
                                    result.icons.push(iconMap[feature]);
                                }
                                break;
                        }
                    });
                    result.playerAction = this.playerChallengeResponseNeeded(game, md5) === true ||
                        this.playerSetupEntryRequired(game, md5) === true ||
                        this.playerCanPlay(game, md5) === true ||
                        this.playerRematchPossible(game, md5) == true;
                    return result;
                }
                return 'Game details missing!';
            }
        };
    }
);

