package com.drop.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class MyDropGame extends ApplicationAdapter {
    OrthographicCamera camera;

    SpriteBatch batch;
    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    Rectangle bucket;

    Vector3 touchPos;
    Array<Rectangle> enemies;
    long lastEnemyAppearanceTime;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        batch = new SpriteBatch();
        dropImage = new Texture("ic_drop.png");
        bucketImage = new Texture("ic_bucket.png");


        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        rainMusic.setLooping(true);
        rainMusic.play();

        bucket = new Rectangle();
        bucket.x = 800 / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = bucketImage.getWidth();
        bucket.height = bucketImage.getHeight();

        touchPos = new Vector3();

        enemies = new Array<>();
        spawnEnemy();
    }

    @Override
    public void render() {

        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle enemy : enemies) {
            batch.draw(dropImage, enemy.x, enemy.y);
        }
        batch.end();

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

    private void spawnEnemy() {
        Rectangle enemy = new Rectangle();
        enemy.x = 0;
        enemy.y = MathUtils.random(0, 480);
        enemy.width = dropImage.getWidth();
        enemy.height = dropImage.getHeight();

        enemies.add(enemy);
        lastEnemyAppearanceTime = TimeUtils.nanoTime();
    }

    private void moveEnemy() {
        Iterator<Rectangle> iter = enemies.iterator();
        while (iter.hasNext()) {
            Rectangle enemy = iter.next();
            enemy.x += 200 * Gdx.graphics.getDeltaTime();
            if (enemy.x > 800) iter.remove();
            if(enemy.overlaps(bucket)){
                dropSound.play();
                iter.remove();
            }
        }
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
