'use strict';

describe('Controller: MainCtrl', function () {
    beforeEach(module('tbs.controllers'));

    var ctrl, stateSpy, rootScope, scope, timeout, window, q;
    var $ionicLoading, $ionicPopup;

    var url;
    window = {
        location: {
            href: ''
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
        url = 'http://xtz.com';
        window.location.href = url;
        currentPlayer = undefined;
        stateSpy = {go: sinon.spy()};
        rootScope = $rootScope;
        scope = rootScope.$new();
        q = $q;
        $ionicLoading = {show: sinon.spy(), hide: sinon.spy()};
        $ionicPopup = {alert: sinon.spy()};
        timeout = $timeout;
        features = {features: sinon.stub()};
        phases = {phases: sinon.stub()};
        ads = {initialize: sinon.spy()};
        cells = {cellStates: sinon.stub()};
        circles = {circles: sinon.stub()};
        ships = {ships: sinon.stub()};
        livefeed = {setEndPoint: sinon.spy(), suspendFeed: sinon.spy()};
        version = {showReleaseNotes: sinon.spy()};

        ctrl = $controller('MainCtrl', {
            $scope: scope,
            $state: stateSpy,
            $document: mockDoc,
            $timeout: timeout,
            $window: window,
            tbsAds: ads,
            ENV: env,
            $ionicLoading: $ionicLoading,
            $ionicPopup: $ionicPopup,
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
        expect(scope.theme).to.equal('default-theme');
        expect(scope.mobile).to.be.false;
        expect(scope.adImport).to.equal('templates/ads/non-mobile.html');
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showAdmin).to.be.false;

        expect(scope.adminShowStats).to.be.true;
        expect(scope.adminShowSwitch).to.be.false;
    });

    it('switching admin to switch player', function () {
        scope.adminSwitchToSwitchPlayer();
        expect(scope.adminShowStats).to.be.false;
        expect(scope.adminShowSwitch).to.be.true;
    });

    it('switching admin to stats', function () {
        scope.adminSwitchToSwitchPlayer();
        scope.adminSwitchToStats();
        expect(scope.adminShowStats).to.be.true;
        expect(scope.adminShowSwitch).to.be.false;
    });

    describe('initialize with player and mobile', function () {
        beforeEach(inject(function ($controller) {
            currentPlayer = {id: 'initial', gameSpecificPlayerAttributes: {theme: 'initial'}, adminUser: false};
            window.location.href = 'file://';
            $ionicLoading = {show: sinon.spy(), hide: sinon.spy()};
            $ionicPopup = {alert: sinon.spy()};
            ctrl = $controller('MainCtrl', {
                $scope: scope,
                $state: stateSpy,
                $document: mockDoc,
                $timeout: timeout,
                $window: window,
                tbsAds: ads,
                ENV: env,
                $ionicLoading: $ionicLoading,
                $ionicPopup: $ionicPopup,
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

        it('initializes', function () {
            expect(mockDoc.pauseFunction).to.be.defined;
            expect(mockDoc.resumeFunction).to.be.defined;
            assert(livefeed.setEndPoint.calledWithMatch(env.apiEndpoint));
            expect(scope.theme).to.equal('initial');
            expect(scope.mobile).to.be.true;
            expect(scope.adImport).to.equal('templates/ads/mobile.html');
            expect(scope.player).to.equal(currentPlayer);
            expect(scope.showAdmin).to.be.false;
        });

        it('ignores player updates if id doesnt match', function () {
            var updatedPlayer = {
                id: currentPlayer.id + 'X',
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'initialX'}
            };
            rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(scope.player).to.equal(currentPlayer);
            expect(scope.showAdmin).to.be.false;
        });

        it('takes in player updates if id matches', function () {
            expect(scope.showAdmin).to.be.false;
            var updatedPlayer = {
                id: currentPlayer.id,
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(scope.player).to.equal(updatedPlayer);
            expect(scope.theme).to.equal(updatedPlayer.gameSpecificPlayerAttributes.theme);
            expect(scope.showAdmin).to.be.true;
        });

        it('player retains admin once has it', function () {
            expect(scope.showAdmin).to.be.false;
            var updatedPlayer = {
                id: currentPlayer.id,
                adminUser: true,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(scope.showAdmin).to.be.true;
            updatedPlayer = {
                id: currentPlayer.id,
                adminUser: false,
                gameSpecificPlayerAttributes: {theme: 'new-theme'}
            };
            rootScope.$broadcast('playerUpdate', updatedPlayer.id, updatedPlayer);
            expect(scope.showAdmin).to.be.true;
        });
    });

    it('initializes on player loaded', function () {
        var circlePromise = q.defer(), featurePromise = q.defer(),
            cellPromise = q.defer(), shipPromise = q.defer(), phasePromise = q.defer();

        currentPlayer = {id: 'replaced', adminUser: false, gameSpecificPlayerAttributes: {theme: 'replacedtheme'}};
        features.features.returns(featurePromise.promise);
        circles.circles.returns(circlePromise.promise);
        cells.cellStates.returns(cellPromise.promise);
        ships.ships.returns(shipPromise.promise);
        phases.phases.returns(phasePromise.promise);
        rootScope.$broadcast('playerLoaded');

        featurePromise.resolve();
        phasePromise.resolve();
        cellPromise.resolve();
        circlePromise.resolve();
        shipPromise.resolve();
        rootScope.$apply();
        expect(scope.theme).to.equal(currentPlayer.gameSpecificPlayerAttributes.theme);
        expect(scope.player).to.equal(currentPlayer);
        assert(version.showReleaseNotes.calledWithMatch());
        assert(ads.initialize.calledWithMatch());
        expect(scope.showAdmin).to.be.false;
    });

    it('initial load is admin, updated load is not like simulating person', function () {
        var circlePromise = q.defer(), featurePromise = q.defer(),
            cellPromise = q.defer(), shipPromise = q.defer(), phasePromise = q.defer();

        features.features.returns(featurePromise.promise);
        circles.circles.returns(circlePromise.promise);
        cells.cellStates.returns(cellPromise.promise);
        ships.ships.returns(shipPromise.promise);
        phases.phases.returns(phasePromise.promise);
        currentPlayer = {id: 'replaced', adminUser: true, gameSpecificPlayerAttributes: {theme: 'replacedtheme'}};

        rootScope.$broadcast('playerLoaded');
        rootScope.$apply();
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showAdmin).to.be.true;

        currentPlayer = {id: 'new', adminUser: false, gameSpecificPlayerAttributes: {theme: 'update'}};
        expect(scope.player).not.to.equal(currentPlayer);
        rootScope.$broadcast('playerLoaded');
        rootScope.$apply();
        expect(scope.player).to.equal(currentPlayer);
        expect(scope.showAdmin).to.be.true;
    });

    it('show player', function () {
        scope.showPlayer();
        assert(stateSpy.go.calledWithMatch('app.playerDetails'));
    });

    it('show admin', function () {
        scope.showAdminScreen();
        assert(stateSpy.go.calledWithMatch('app.admin'));
    });

    it('handles offline', function () {
        rootScope.$broadcast('$cordovaNetwork:offline');
        assert(stateSpy.go.calledWith('network'));
    });

    it('handles invalid session when current state is signin', function () {
        stateSpy.$current = {
            name: 'signin'
        };
        rootScope.$broadcast('InvalidSession');
        assert(!stateSpy.go.calledWith('network'));
    });

    it('handles invalid session when current state is not signin', function () {
        stateSpy.$current = {
            name: 'other'
        };
        rootScope.$broadcast('InvalidSession');
        assert(stateSpy.go.calledWith('network'));
    });

    it('handles general error', function () {
        stateSpy.$current = {
            name: 'other'
        };
        rootScope.$broadcast('GeneralError');
        assert($ionicLoading.hide.calledWithMatch());
        $ionicPopup.alert.calledWithMatch({
            title: 'There was a problem!',
            template: 'Going to reconnect!'
        });
        assert(stateSpy.go.calledWith('network'));
    });

    it('if resume is called after no pauses, goes to network reconnect', function () {
        mockDoc.resumeFunction();
        assert(stateSpy.go.calledWith('network'));
    });

    it('receiving pause sets up timeout that goes to network after timeout', function () {
        mockDoc.pauseFunction();
        timeout.flush();
        assert(livefeed.suspendFeed.calledWithMatch());
    });

    it('receiving pause and then resume cancels timeout', function () {
        mockDoc.pauseFunction();
        mockDoc.resumeFunction();
        var exception = false;
        try {
            timeout.flush();
        } catch (ex) {
            exception = true;
        }
        expect(exception).to.be.true;
        assert(!livefeed.suspendFeed.calledWithMatch());
    });

    it('receiving multiple pauses requires multiple resumes or timeout fires', function () {
        mockDoc.pauseFunction();
        mockDoc.pauseFunction();
        mockDoc.resumeFunction();
        timeout.flush();
        assert(livefeed.suspendFeed.calledWithMatch());
    });

    it('refresh broadcasts to game cache', function () {
        rootScope.$broadcast = sinon.spy();
        scope.refreshGames();
        expect(rootScope.$broadcast.callCount).to.equal(1);
        assert(rootScope.$broadcast.calledWithMatch('refreshGames', ''));
    });

});