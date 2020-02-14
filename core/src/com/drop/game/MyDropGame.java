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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.drop.game.model.AttackArea;
import com.drop.game.model.MyRectangle;
import com.drop.game.model.Tower;
import com.drop.game.routes.RouteFirstLvl;

import java.util.Iterator;

public class MyDropGame extends ApplicationAdapter {
    OrthographicCamera camera;

    SpriteBatch batch;
    ShapeRenderer shapeRenderer;

    Texture playingField;
    Texture dropImage;
    Texture test;

    Texture bucketImage;

    private Sprite mapSprite;

    Sound dropSound;
    Music rainMusic;
    Rectangle bucket;

    Vector3 touchPos;
    Array<MyRectangle> enemies;
    Array<Tower> towers;

    long lastEnemyAppearanceTime;

    private int radius = 50;

    private float w;
    private float h;

    @Override
    public void create() {
//        float wi= Gdx.graphics.getWidth()/Gdx.graphics.getHeight()/480;
//        System.out.println("WI = " + wi);
//        camera.setToOrtho(false, wi, 480);

         w = Gdx.graphics.getWidth();
         h = Gdx.graphics.getHeight();


        mapSprite = new Sprite(new Texture(Gdx.files.internal("level_1.png")));
        mapSprite.setPosition(0, 0);
        mapSprite.setSize(w, h * (h/w));


        System.out.println("w = " + w);
        System.out.println("h = " + h);

        camera = new OrthographicCamera(w, h * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

        batch = new SpriteBatch();
        dropImage = new Texture("luntik.png");
        bucketImage = new Texture("ic_bucket.png");
        playingField = new Texture("level_1.png");


        test = new Texture("test.png");


        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setColor(new Color(0f, 0f, 1f, 0.2f));

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = bucketImage.getWidth();
        bucket.height = bucketImage.getHeight();

        touchPos = new Vector3();

        enemies = new Array<>();
        towers = new Array<>();
        spawnEnemy();
    }

    private void spawnEnemy() {
        MyRectangle enemy = new MyRectangle();
        enemy.setRoute(new RouteFirstLvl());
        enemy.width = dropImage.getWidth();
        enemy.height = dropImage.getHeight();
        enemies.add(enemy);
        lastEnemyAppearanceTime = TimeUtils.nanoTime();
    }

    private void addTower(float x, float y) {
        Tower tower = new Tower(x, y);
        towers.add(tower);
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = w;
        camera.viewportHeight = h * height/width;
        camera.update();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        mapSprite.draw(batch);

//        batch.draw(playingField, 0, 0);
        batch.draw(bucketImage, bucket.x, bucket.y);

        for (MyRectangle enemy : enemies) {
            batch.draw(dropImage, enemy.x, enemy.y);
        }
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (Tower tower : towers) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.circle(tower.getAttackArea().getX(),
                    tower.getAttackArea().getY(),
                    tower.getAttackArea().getAttackRadius());
            //todo пока рисуем область, потом будет тавер и если нажать на тавер, то надо будет рисовать область обстрела
            shapeRenderer.end();
        }

//        if (Gdx.input.isTouched()) {
//            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
//            camera.unproject(touchPos);
//            bucket.x = (int) (touchPos.x - 64 / 2);
//        }

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

        if (Gdx.input.isTouched()) {

            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            addTower(touchPos.x, touchPos.y);
        }
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
            if (enemy.x > 800) iter.remove();
            if (enemy.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
            }

            if (!towers.isEmpty()) {
                Tower tower = towers.get(0);

                Vector2 vectorShape = new Vector2(tower.getX(), tower.getY());
                Vector2 vectorEnemy = enemy.getCenter(new Vector2());

                System.out.println("Distance: " + distance(vectorShape, vectorEnemy));
                if (distance(vectorShape, vectorEnemy) < radius) {
                    dropSound.play();
                    iter.remove();
                }
            }
        }
    }

    private double distance(Vector2 object1, Vector2 object2) {
        return Math.sqrt(Math.pow((object2.x - object1.x), 2) + Math.pow((object2.y - object1.y), 2));
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
