package com.llewkcor.ares.luxe.rewards.data;

import com.llewkcor.ares.commons.connect.mongodb.MongoDocument;
import lombok.Getter;
import org.bson.Document;

import java.util.UUID;

public final class ClaimableCrate implements Claimable, MongoDocument<ClaimableCrate> {
    @Getter public UUID uniqueId;
    @Getter public UUID ownerId;
    @Getter public String description;
    @Getter public int amount;
    @Getter public long expire;
    @Getter public String crateName;

    public ClaimableCrate() {
        this.uniqueId = null;
        this.ownerId = null;
        this.description = null;
        this.amount = 0;
        this.expire = 0L;
        this.crateName = null;
    }

    public ClaimableCrate(UUID ownerId, String description, int amount, String crateName, long expire) {
        this.uniqueId = UUID.randomUUID();
        this.ownerId = ownerId;
        this.description = description;
        this.amount = amount;
        this.crateName = crateName;
        this.expire = expire;
    }

    @Override
    public ClaimableCrate fromDocument(Document document) {
        this.uniqueId = (UUID)document.get("id");
        this.description = document.getString("description");
        this.amount = document.getInteger("amount");
        this.crateName = document.getString("crate");
        this.expire = document.getLong("expire");

        return this;
    }

    @Override
    public Document toDocument() {
        return new Document()
                .append("id", uniqueId)
                .append("description", description)
                .append("amount", amount)
                .append("crate", crateName)
                .append("expire", expire);
    }
}