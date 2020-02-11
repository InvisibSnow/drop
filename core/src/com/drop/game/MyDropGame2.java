package com.drop.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class MyDropGame2 extends com.badlogic.gdx.Game {

    private static final float SCALING = 0.1f;
    public static final float PPM = 100f;

    private Box2DDebugRenderer debugRenderer;
    private OrthographicCamera camera;
    private World world;

    private Body boxBody;
    Sprite sprite;
    Texture bucketImage;

    SpriteBatch sb;

    @Override
    public void create() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        sb =  new SpriteBatch();
        sprite = new Sprite(new Texture("ic_drop.png"));
        bucketImage = new Texture("ic_bucket.png");
        createWorld();
        createCollisionListener();
        createGround();
        createBox();
    }

    private void createWorld() {
        Vector2 gravity = new Vector2(0, -10);
        world = new World(gravity, true);
    }

    private void createCollisionListener() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("beginContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void endContact(Contact contact) {
                Fixture fixtureA = contact.getFixtureA();
                Fixture fixtureB = contact.getFixtureB();
                Gdx.app.log("endContact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }

        });
    }

    private void createGround() {
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(500, 1);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyType.StaticBody;
        groundBodyDef.position.set(0, 0);

        Body groundBody = world.createBody(groundBodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundShape;

        groundBody.createFixture(fixtureDef);
        groundShape.dispose();
    }

    private void createBox() {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(5, 5);
        BodyDef boxBodyDef = new BodyDef();
        boxBodyDef.position.set(0, 0);
        boxBodyDef.angle = 40;

//        boxBodyDef.type = BodyType.DynamicBody;
        boxBodyDef.type = BodyType.DynamicBody;

        boxBodyDef.fixedRotation = false;
        boxBody = world.createBody(boxBodyDef);

        boxBody.setUserData(sprite);

        FixtureDef boxFixtureDef = new FixtureDef();
        boxFixtureDef.shape = boxShape;
        boxFixtureDef.restitution = 0.5f;
        boxFixtureDef.density = 2.0f;
        boxBody.createFixture(boxFixtureDef);
        boxShape.dispose();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
//        float cameraWidth = Gdx.graphics.getWidth();
//        float cameraHeight = Gdx.graphics.getHeight();
//        camera.setToOrtho(false, cameraWidth, cameraHeight);
//        camera.position.set(0, 0, 0);
    }

    @Override
    public void render() {
        super.render();

        world.step(Gdx.graphics.getDeltaTime(), 8, 3);

//        int numContacts = world.getContactCount();
//        if (numContacts > 0) {
//            Gdx.app.log("contact", "start of contact list");
//            for (Contact contact : world.getContactList()) {
//                Fixture fixtureA = contact.getFixtureA();
//                Fixture fixtureB = contact.getFixtureB();
//                Gdx.app.log("contact", "between " + fixtureA.toString() + " and " + fixtureB.toString());
//            }
//            Gdx.app.log("contact", "end of contact list");
//        }
//
//        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//        camera.update();
//        debugRenderer.render(world, camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        sb.setProjectionMatrix(camera.combined);
        sb.begin();
        sb.draw(bucketImage, boxBody.getPosition().x, boxBody.getPosition().y);
        sb.end();



//        float posX = boxBody.getPosition().x;
//        float posY = boxBody.getPosition().y;
//        float rotation = (float) Math.toDegrees(boxBody.getAngle());
//        sprite.setPosition(posX, posY);
//        sprite.setRotation(rotation);

    }


}
