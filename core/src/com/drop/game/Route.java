package com.drop.game;

public class Route {

    public int x;
    public int y;

    public Route(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (x > 100 && x < 200 && y < 350) {
            y += 2;
        } else if (x > 300 && x < 400 && y < 360 && y > 200) {
            y -= 2;
        } else {
            x += 2;
        }
    }
}
