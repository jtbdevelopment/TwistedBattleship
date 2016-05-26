'use strict';

describe('Controller: MainCtrl', function () {
    // load the controller's module
    beforeEach(module('tbs.controllers'));

    var ctrl, stateSpy, rootScope, scope, timeout, window, q;

    var url = 'http://xtz.com';
    window = {
        location: {
            href: url
        }
    };

    var env = {
        apiEndpoint: 'some endpoint'
    };

    var currentPlayer;
    var mockPlayerService = {
        currentPlayer: function () {
            return currentPlayer;
        }
    };

    var mockDoc = {
        resumeFunction: undefined,
        pauseFunction: undefined,
        bind: function (event, fn) {
            if (event === 'pause') {
                this.pauseFunction = fn;
            }
            else {
                this.resumeFunction = fn;
            }
        }
    };
    //  Stuff that is pre-cache stuff
    var pushNotifications = {x: '334'};
    var features, circles, cells, ships, phases, ads, livefeed, version;
    beforeEach(inject(function ($rootScope, $controller, $timeout, $q) {
        currentPlayer = {source: 'MANUAL', gameSpecificPlayerAttributes: {theme: 'atheme'}};
        stateSpy = {go: sinon.spy()};
        rootScope = $rootScope;
        scope = rootScope.$new();
        q = $q;
        timeout = $timeout;
        features = {features: sinon.stub()};
        phases = {phases: sinon.stub()};
        ads = {initialize: sinon.spy()};
        cells = {cellStates: sinon.stub()};
        circles = {circles: sinon.stub()};
        ships = {ships: sinon.stub()};
        livefeed = {setEndPoint: sinon.spy()};
        version = {showReleaseNotes: sinon.spy()};

        ctrl = $controller('MainCtrl', {
            $scope: scope,
            $state: stateSpy,
            $document: mockDoc,
            $timeout: timeout,
            $window: window,
            tbsAds: ads,
            ENV: env,
            jtbPlayerService: mockPlayerService,
            jtbGameFeatureService: features,
            jtbGamePhaseService: phases,
            tbsCellStates: cells,
            tbsCircles: circles,
            tbsShips: ships,
            jtbLiveGameFeed: livefeed,
            tbsVersionNotes: version,
            jtbPushNotifications: pushNotifications
        });
    }));

    it('initializes non-mobile', function () {
        expect(mockDoc.pauseFunction).to.be.defined;
        expect(mockDoc.resumeFunction).to.be.defined;
        assert(livefeed.setEndPoint.calledWithMatch(env.apiEndpoint));
        expect(scope.theme).to.equal(currentPlayer.gameSpecificPlayerAttributes.theme);
        expect(scope.mobile).to.be.false;
        expect(scope.adImport).to.equal('templates/ads/non-mobile.html');
        expect(scope.player).to.equal(currentPlayer);
    });
});