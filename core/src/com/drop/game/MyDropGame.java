package com.drop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyDropGame extends ApplicationAdapter {
    OrthographicCamera camera;

    World world;

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    Texture playingField;
    Texture dropImage;

    Texture bucketImage;

    Sound dropSound;
    Music rainMusic;
    Rectangle bucket;

    Vector3 touchPos;
    Array<MyRectangle> enemies;
    long lastEnemyAppearanceTime;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        createWorld();

        batch = new SpriteBatch();
        dropImage = new Texture("ic_drop.png");
        bucketImage = new Texture("ic_bucket.png");
        playingField = new Texture("level_1.png");


        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        shapeRenderer = new ShapeRenderer();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = bucketImage.getWidth();
        bucket.height = bucketImage.getHeight();

        touchPos = new Vector3();

        enemies = new Array<>();
        spawnEnemy();
    }

    private void createWorld(){
        world = new World(new Vector2(0, -10f), true);
        world.setContactListener(new MyContactListener());
    }

    private void spawnEnemy() {
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(dropImage.getWidth(), dropImage.getHeight());
        MyBodyDef boxBodyDef = new MyBodyDef(new Route(0,0));


        MyRectangle enemy = new MyRectangle();
        enemy.setRoute(new Route(0,0));
        enemy.x = 0;
//        enemy.y = MathUtils.random(0, 480);
        enemy.y = 50;
        enemy.width = dropImage.getWidth();
        enemy.height = dropImage.getHeight();
        enemy.setRotation(0f);
        enemies.add(enemy);
        lastEnemyAppearanceTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(playingField, 0,0);
        batch.draw(bucketImage, bucket.x, bucket.y);

        for (MyRectangle enemy : enemies) {
//            batch.draw(dropImage, enemy.x, enemy.y);
            batch.draw(dropImage,
                    enemy.x,
                    enemy.y,
                    dropImage.getWidth()/2,
                    dropImage.getHeight()/2,
                    dropImage.getWidth(),
                    dropImage.getHeight(),
                    1f, 1f,
                    enemy.getRotation(),
                    0, 0,
                    dropImage.getWidth(),
                    dropImage.getHeight(),
                    false, false);
        }

        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0f, 0f, 1f, 0.5f));

        shapeRenderer.circle(100, 100, 50);
        shapeRenderer.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            bucket.x = (int) (touchPos.x - 64 / 2);
        }

        checkKeyPressed();
        checkBucketPos();
        checkNewEnemy();
        moveEnemy();
    }

    private void checkKeyPressed() {
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) bucket.x -= 300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) bucket.x += 300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) bucket.y += 300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) bucket.y -= 300 * Gdx.graphics.getDeltaTime();
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) bucket.y -= 300 * Gdx.graphics.getDeltaTime();
    }

    private void checkBucketPos() {
        if (bucket.x < 0) bucket.x = 0;
        if (bucket.x > 800 - bucket.width) bucket.x = 800 - bucket.width;
        if (bucket.y < 0) bucket.y = 0;
        if (bucket.y > 480 - bucket.height) bucket.y = 480 - bucket.height;
    }

    private void checkNewEnemy() {
        if (TimeUtils.nanoTime() - lastEnemyAppearanceTime > 1000000000) {
            spawnEnemy();
        }
    }


    private void moveEnemy() {
        Iterator<MyRectangle> iter = enemies.iterator();
        while (iter.hasNext()) {
            MyRectangle enemy = iter.next();
//            enemy.x += 200 * Gdx.graphics.getDeltaTime();
            enemy.x = enemy.getRoute().x;
            enemy.y = enemy.getRoute().y;
            enemy.getRoute().update();
            enemy.setRotation(enemy.getRotation() + 2);
            if (enemy.x > 800) iter.remove();
            if(enemy.overlaps(bucket)){
                dropSound.play();
                iter.remove();
            }
        }
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

    @Override
    public void dispose() {
        batch.dispose();
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }



}
