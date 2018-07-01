package com.jtbdevelopment.TwistedBattleship.rest;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.GameFeatureGroupType;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;

import java.util.ArrayList;
import java.util.List;

/**
 * Date: 4/28/15
 * Time: 6:54 PM
 */
@SuppressWarnings("unused")
public class GameFeatureInfo {
    private Detail feature;
    private List<Detail> options = new ArrayList<Detail>();

    public GameFeatureInfo() {
    }

    public GameFeatureInfo(final GameFeature feature, final List<Detail> options) {
        this.feature = new Detail(feature);
        this.options = options;
    }

    public boolean equals(final Object o) {
        if (DefaultGroovyMethods.is(this, o)) return true;
        if (!getClass().equals(o.getClass())) return false;

        final GameFeatureInfo that = (GameFeatureInfo) o;

        if (!options.equals(that.getOptions())) return false;
        return feature.equals(that.getFeature());
    }

    public int hashCode() {
        return feature.hashCode();
    }

    public Detail getFeature() {
        return feature;
    }

    public void setFeature(Detail feature) {
        this.feature = feature;
    }

    public List<Detail> getOptions() {
        return options;
    }

    public void setOptions(List<Detail> options) {
        this.options = options;
    }

    public static class Detail {
        private GameFeatureGroupType groupType;
        private GameFeature feature;
        private String label;
        private String description;

        public Detail() {
        }

        public Detail(final GameFeature feature) {
            this.feature = feature;
            this.description = feature.getDescription();
            this.label = feature.getLabel();
            this.groupType = feature.getGroupType();
        }

        public boolean equals(final Object o) {
            if (DefaultGroovyMethods.is(this, o)) return true;
            if (!getClass().equals(o.getClass())) return false;

            final Detail detail = (Detail) o;

            if (!description.equals(detail.getDescription())) return false;
            if (!feature.equals(detail.getFeature())) return false;

            return true;
        }

        public int hashCode() {
            return feature.hashCode();
        }

        public GameFeatureGroupType getGroupType() {
            return groupType;
        }

        public void setGroupType(GameFeatureGroupType groupType) {
            this.groupType = groupType;
        }

        public GameFeature getFeature() {
            return feature;
        }

        public void setFeature(GameFeature feature) {
            this.feature = feature;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
