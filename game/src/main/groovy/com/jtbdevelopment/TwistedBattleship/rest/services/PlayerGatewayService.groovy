package com.jtbdevelopment.TwistedBattleship.rest.services

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.games.rest.services.AbstractPlayerGatewayService
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * Date: 11/14/14
 * Time: 6:36 AM
 */
@Path("/")
@Component
@CompileStatic
class PlayerGatewayService extends AbstractPlayerGatewayService<ObjectId> {
    static class GameFeatureGroupDetails {
        String description
        List<GameFeature> options
        List<String> optionDescriptions

        boolean equals(final o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false

            final GameFeatureGroupDetails that = (GameFeatureGroupDetails) o

            if (description != that.description) return false
            if (optionDescriptions != that.optionDescriptions) return false
            if (options != that.options) return false

            return true
        }

        int hashCode() {
            int result
            result = description.hashCode()
            result = 31 * result + options.hashCode()
            result = 31 * result + optionDescriptions.hashCode()
            return result
        }

        @Override
        public String toString() {
            return "GameFeatureGroupDetails{" +
                    "description='" + description + '\'' +
                    ", options=" + options +
                    ", optionDescriptions=" + optionDescriptions +
                    '}';
        }
    }

    @GET
    @Path("features")
    @Produces(MediaType.APPLICATION_JSON)
    @SuppressWarnings("GrMethodMayBeStatic")
    public Map featuresAndDescriptions() {
        GameFeature.groupedFeatures.collectEntries {
            GameFeature group, List<GameFeature> options ->
                [(group): new GameFeatureGroupDetails(
                        description: group.description,
                        options: options.collect { GameFeature option -> option },
                        optionDescriptions: options.collect { GameFeature option -> option.description }
                )]
        }
    }

}
