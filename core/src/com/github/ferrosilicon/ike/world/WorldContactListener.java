package com.github.ferrosilicon.ike.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.github.ferrosilicon.ike.entity.Ike;

final class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(final Contact contact) {
        if (contact.getWorldManifold().getNormal().x != 0.0)
            contact.setFriction(0);

        final Ike ike = getIke(contact);
        if (contact.getWorldManifold().getNormal().y > 0 && ike != null)
            ike.grounded = true;
    }

    @Override
    public void endContact(final Contact contact) {
        final Ike ike = getIke(contact);
        if (contact.getWorldManifold().getNormal().y >= 0 && ike != null)
            ike.grounded = false;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }

    // If the one of the two fixtures in the contact event belong to Ike, return the Ike instance
    private static Ike getIke(final Contact contact) {
        final Object userDataA = contact.getFixtureA().getBody().getUserData();
        if (userDataA != null && userDataA instanceof Ike)
            return (Ike) userDataA;
        final Object userDataB = contact.getFixtureB().getBody().getUserData();
        return userDataB != null && userDataB instanceof Ike ? (Ike) userDataB : null;
    }
}
