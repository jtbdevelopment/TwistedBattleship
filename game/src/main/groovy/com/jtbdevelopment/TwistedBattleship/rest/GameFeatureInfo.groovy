package com.jtbdevelopment.TwistedBattleship.rest

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import groovy.transform.CompileStatic

/**
 * Date: 4/28/15
 * Time: 6:54 PM
 */
@CompileStatic
class GameFeatureInfo {
    static class Detail {
        @SuppressWarnings("unused")
        Detail() {
        }

        Detail(final GameFeature feature) {
            this.feature = feature
            this.description = feature.description
        }

        boolean equals(final o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            final Detail detail = (Detail) o

            if (description != detail.description) return false
            if (feature != detail.feature) return false

            return true
        }

        int hashCode() {
            return feature.hashCode()
        }
        GameFeature feature
        String description
    }

    Detail feature
    List<Detail> options = []

    @SuppressWarnings("unused")
    GameFeatureInfo() {
    }

    public GameFeatureInfo(final GameFeature feature, final List<Detail> options) {
        this.feature = new Detail(feature)
        this.options = options
    }

    boolean equals(final o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        final GameFeatureInfo that = (GameFeatureInfo) o

        if (options != that.options) return false
        if (feature != that.feature) return false

        return true
    }

    int hashCode() {
        return feature.hashCode()
    }
}
