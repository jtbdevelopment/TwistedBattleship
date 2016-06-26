'use strict';

describe('Service: phaserGameFactory', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var Phaser = {
        AUTO: 'auto',
        Game: function (width, height, renderer, parent, state, transparent, antialias, physicsConfig) {
            this.width = width;
            this.height = height;
            this.renderer = renderer;
            this.parent = parent;
            this.state = state;
            this.transparent = transparent;
            this.antialias = antialias;
            this.physicsConfig = physicsConfig;
        }
    };
    Phaser.Game.prototype = {};

    beforeEach(module(function ($provide) {
        $provide.factory('Phaser', function () {
            return Phaser;
        });
    }));

    var service;
    beforeEach(inject(function ($injector) {
        service = $injector.get('tbsPhaserGameFactory');
    }));

    function updateCB() {

    }

    function createCB() {

    }

    it('test initializes a new pahser game', function () {
        var callbackMap = {update: updateCB, create: createCB};
        var game = service.newGame(120, 300, 'test', callbackMap);
        expect(game.width).to.equal(120);
        expect(game.height).to.equal(300);
        expect(game.renderer).to.equal(Phaser.AUTO);
        expect(game.parent).to.equal('test');
        expect(game.state).to.equal(callbackMap);
        expect(game.antialias).to.be.undefined;
        expect(game.transparent).to.be.undefined;
        expect(game.physicsConfig).to.be.undefined;
    })
});