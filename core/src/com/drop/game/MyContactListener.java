package com.drop.game;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class MyContactListener implements ContactListener {

    @Override
    public void endContact(Contact contact) {
        System.out.println("endContact");
    }

    @Override
    public void beginContact(Contact contact) {
        System.out.println("beginContact");

    }

    @Override
    public void preSolve (Contact contact, Manifold oldManifold){
        System.out.println("preSolve");
    }

    @Override
    public void postSolve (Contact contact, ContactImpulse impulse){
        System.out.println("postSolve");
    }

}