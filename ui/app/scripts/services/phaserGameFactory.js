'use strict';

//  Exists to make shipGridV2 testable
angular.module('tbs.services').factory('tbsPhaserGameFactory',
    ['Phaser', function (Phaser) {
        return {
            newGame: function (width, height, divName, callbackMap) {
                return new Phaser.Game(
                    width,
                    height,
                    Phaser.AUTO,
                    divName,
                    callbackMap);
            }
        }
    }]
); 
    
