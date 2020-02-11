package com.drop.game;

import com.badlogic.gdx.math.Rectangle;

public class MyRectangle extends Rectangle {

    private float rotation;
    private Route route;

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }
}
