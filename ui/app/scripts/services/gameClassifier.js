'use strict';

var YOUR_TURN = 'Your move.';
var THEIR_TURN = 'Their move.';
var OLDER = 'Older games.';
angular.module('tbs.services').factory('jtbGameClassifier',
    ['tbsGameDetails', 'jtbPlayerService',
        function (tbsGameDetails, jtbPlayerService) {
            var icons = {};
            icons[YOUR_TURN] = 'play';
            icons[THEIR_TURN] = 'pause';
            icons[OLDER] = 'stop';
            return {
                getClassifications: function () {
                    return [YOUR_TURN, THEIR_TURN, OLDER];
                },

                getClassification: function (game) {
                    var md5 = jtbPlayerService.currentPlayer().md5;
                    var action = tbsGameDetails.playerCanPlay(game, md5) ||
                        tbsGameDetails.playerChallengeResponseNeeded(game, md5) ||
                        tbsGameDetails.playerRematchPossible(game, md5) ||
                        tbsGameDetails.playerSetupEntryRequired(game, md5);
                    if (action) {
                        return YOUR_TURN;
                    }

                    if (game.gamePhase === 'Declined' || game.gamePhase === 'Quit' || game.gamePhase === 'NextRoundStarted') {
                        return OLDER;
                    }

                    return THEIR_TURN;
                },

                getIcons: function () {
                    return icons;
                }
            };
        }
    ]
);