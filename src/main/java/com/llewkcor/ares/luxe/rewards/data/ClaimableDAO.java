package com.llewkcor.ares.luxe.rewards.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.llewkcor.ares.commons.connect.mongodb.MongoDB;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public final class ClaimableDAO {
    private static final String NAME = "ares";
    private static final String COLL_CRATE = "rewards";

    public static ImmutableList<Claimable> getRewards(MongoDB database, UUID ownerId) {
        final MongoCollection<Document> crateCollection = database.getCollection(NAME, COLL_CRATE);
        final MongoCursor<Document> crateCursor = crateCollection.find(Filters.eq("owner", ownerId)).iterator();
        final List<Claimable> result = Lists.newArrayList();

        while (crateCursor.hasNext()) {
            final Document document = crateCursor.next();
            final ClaimableCrate crate = new ClaimableCrate().fromDocument(document);
            result.add(crate);
        }

        // Create other loading methods here...

        return ImmutableList.copyOf(result);
    }

    public static void setRewards(MongoDB database, Collection<Claimable> rewards) {
        final MongoCollection<Document> crateCollection = database.getCollection(NAME, COLL_CRATE);

        for (Claimable claimableCrate : rewards.stream().filter(reward -> reward instanceof ClaimableCrate).collect(Collectors.toList())) {
            final ClaimableCrate crate = (ClaimableCrate)claimableCrate;
            final Document existing = crateCollection.find(Filters.eq("id", crate.getUniqueId())).first();

            if (existing != null) {
                crateCollection.replaceOne(existing, crate.toDocument());
            } else {
                crateCollection.insertOne(crate.toDocument());
            }
        }

        // Loop and save other claimable types here...
    }

    public static void deleteRewards(MongoDB database, Collection<Claimable> rewards) {
        final MongoCollection<Document> crateCollection = database.getCollection(NAME, COLL_CRATE);

        rewards.forEach(reward -> {
            if (reward instanceof ClaimableCrate) {
                final Document existing = crateCollection.find(Filters.eq("id", reward.getUniqueId())).first();

                if (existing != null) {
                    crateCollection.deleteOne(existing);
                }
            }

            // Delete other claimable types here...
        });
    }
}