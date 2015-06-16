'use strict';

describe('Service: gameDetails', function () {
    // load the controller's module
    beforeEach(module('tbs.services'));

    var service;

    // Initialize the controller and a mock scope
    beforeEach(inject(function ($injector) {
        service = $injector.get('tbsGameDetails');
    }));

    it('test player response needed - bad parameters', function () {
        expect(service.playerChallengeResponseNeeded()).to.equal(false);
        expect(service.playerChallengeResponseNeeded({})).to.equal(false);
        expect(service.playerChallengeResponseNeeded({}, '  ')).to.equal(false);
    });

    it('test player can play - bad parameters', function () {
        expect(service.playerCanPlay()).to.equal(false);
        expect(service.playerCanPlay({})).to.equal(false);
        expect(service.playerCanPlay({}, '  ')).to.equal(false);
    });

    it('test player setup required - bad parameters', function () {
        expect(service.playerSetupEntryRequired()).to.equal(false);
        expect(service.playerSetupEntryRequired({})).to.equal(false);
        expect(service.playerSetupEntryRequired({}, '  ')).to.equal(false);
    });

    it('test player end - bad parameters', function () {
        expect(service.gameEndForPlayer()).to.equal('');
        expect(service.gameEndForPlayer({})).to.equal('');
        expect(service.gameEndForPlayer({}, '  ')).to.equal('');
    });

    it('test player end icon - bad parameters', function () {
        expect(service.gameEndIconForPlayer()).to.equal('help');
        expect(service.gameEndIconForPlayer({})).to.equal('help');
        expect(service.gameEndIconForPlayer({}, '  ')).to.equal('help');
    });

    it('test player score - bad parameters', function () {
        expect(service.gameScoreForPlayer()).to.equal('');
        expect(service.gameScoreForPlayer({})).to.equal('');
        expect(service.gameScoreForPlayer({}, '  ')).to.equal('');
    });

    it('test player profile - bad parameters', function () {
        expect(service.profileForPlayer()).to.equal('');
        expect(service.profileForPlayer({})).to.equal('');
        expect(service.profileForPlayer({}, '  ')).to.equal('');
    });

    it('test player image - bad parameters', function () {
        expect(service.imageForPlayer()).to.equal(null);
        expect(service.imageForPlayer({})).to.equal(null);
        expect(service.imageForPlayer({}, '  ')).to.equal(null);
    });

    it('test state icon - bad parameters', function () {
        expect(service.stateIconForPlayer()).to.equal('help');
        expect(service.stateIconForPlayer({})).to.equal('help');
        expect(service.stateIconForPlayer({}, '  ')).to.equal('help');
    });

    it('test game description bad game', function () {
        expect(service.gameDescription()).to.equal('Game details missing!');
    });

    it('test short game description bad game', function () {
        expect(service.shortGameDescription()).to.equal('Game details missing!');
    });
});


