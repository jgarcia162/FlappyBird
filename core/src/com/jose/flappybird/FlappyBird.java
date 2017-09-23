package com.jose.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {

    //TODO add pause button
    //TODO change background
    //TODO change game over screen

    private Stage stage;
    private Skin skin;
    private Preferences preferences;
    private SpriteBatch batch;
    private Texture background;
    private Texture gameOver;
    private Texture topTube;
    private Texture bottomTube;
    private Texture[] birds;
    private TextButton pauseButton;
    private TextButton.TextButtonStyle buttonStyle;
    private TextureAtlas buttonAtlas;
    private Rectangle topTubeRectangle;
    private Rectangle bottomTubeRectangle;
    private ShapeRenderer shapeRenderer;
    private Circle birdCircle;
    private BitmapFont font;

    private int flapState = 0;
    private float birdY = 0;
    private float velocity = 0;
    private int score = 0;
    int scoringTube = 0;

    private int gameState = 0;
    private float gravity = 2;

    private float gap = 400;
    private float maxTubeOffset;
    private float tubeVelocity = 4;
    private float maxTubeVelocity = 40;
    private int numberOfTubes = 4;
    private float[] tubeX = new float[numberOfTubes];
    private float[] topTubeY = new float[numberOfTubes];
    private float[] bottomTubeY = new float[numberOfTubes];
    private float[] tubeOffset = new float[numberOfTubes];
    private float distanceBetweenTubes;

    private Random randomGenerator;


    @Override
    public void create() {
        stage = new Stage();
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        font = new BitmapFont();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons_pack.atlas"));
        skin.addRegions(buttonAtlas);
        font.setColor(Color.WHITE);
        font.getData().setScale(10f);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.up = skin.getDrawable("play");
        buttonStyle.down = skin.getDrawable("pause");
        pauseButton = new TextButton("Pause",buttonStyle);
        stage.addActor(pauseButton);
        preferences = Gdx.app.getPreferences("Flappy Bird Preferences");
        shapeRenderer = new ShapeRenderer();
        background = new Texture("bodega.jpg");
        gameOver = new Texture("gameover.png");
        birdCircle = new Circle();

        birds = new Texture[2];
        birds[0] = new Texture("wingsdownpigeon.png");
        birds[1] = new Texture("wingsuppigeon.png");


        topTube = new Texture("topbuilding.png");
        bottomTube = new Texture("bottombuilding.png");
        topTubeRectangle = new Rectangle();
        bottomTubeRectangle = new Rectangle();
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 1.5f;

        startGame();
    }

    private void startGame() {
        birdY = Gdx.graphics.getHeight() / 2 - birds[0].getHeight() / 2;

        for (int i = 0; i < numberOfTubes; i++) {

            tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + Gdx.graphics.getWidth() + i * distanceBetweenTubes;

        }
    }

    @Override
    public void render() {
        batch.begin();
        stage.draw();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {

                score++;

                if(score % 5 == 0 && tubeVelocity <= maxTubeVelocity){
                    tubeVelocity +=2;
                }

                if (scoringTube < numberOfTubes - 1) {

                    scoringTube++;

                } else {

                    scoringTube = 0;

                }
            }

            if (Gdx.input.justTouched()) {

                velocity = -25;
                batch.draw(birds[0], Gdx.graphics.getWidth() / 2 - birds[0].getWidth() / 2, birdY);
                birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[0].getHeight() / 2, birds[0].getWidth() / 2);

            }

            for (int i = 0; i < numberOfTubes; i++) {

                if (tubeX[i] < -topTube.getWidth()) {

                    tubeX[i] += numberOfTubes * distanceBetweenTubes;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                topTubeY[i] = Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i];

                bottomTubeY[i] = Gdx.graphics.getHeight() / 2 - gap / 2 - tubeOffset[i];

                batch.draw(topTube, tubeX[i], topTubeY[i], topTube.getWidth(), Gdx.graphics.getHeight() - topTubeY[i]);
                batch.draw(bottomTube, tubeX[i], 0, bottomTube.getWidth(), Gdx.graphics.getHeight() - bottomTubeY[i] - gap);

//                shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight(),topTube.getWidth(),topTube.getHeight());
//                shapeRenderer.rect(tubeX[i],0,bottomTube.getWidth(),bottomTube.getHeight());

                topTubeRectangle.set(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangle.set(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

                if (Intersector.overlaps(birdCircle, topTubeRectangle) || Intersector.overlaps(birdCircle, bottomTubeRectangle)) {

                    gameState = 2;
                    saveScore(score);
                    tubeVelocity = 4;
                }
            }

            if (birdY > 0) {

                velocity = velocity + gravity;
                birdY -= velocity;

            } else {
                gameState = 2;
                saveScore(score);
                tubeVelocity = 4;
            }

        } else if (gameState == 0) {

            if (Gdx.input.justTouched()) {

                gameState = 1;


            }

        } else if (gameState == 2) {

            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);
            saveScore(score);

            if (Gdx.input.justTouched()) {

                gameState = 1;
                startGame();
                score = 0;
                scoringTube = 0;
                velocity = 0;
                tubeVelocity = 4;

            }
        }

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[1], Gdx.graphics.getWidth() / 2 - birds[1].getWidth() / 2, birdY);
        font.draw(batch, String.valueOf(preferences.getInteger("highScore",0)), 100, Gdx.graphics.getHeight() - 250);
        font.draw(batch, String.valueOf(score), 100, 250);
        batch.end();
        shapeRenderer.end();
        //TODO slow down flap speed
        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[1].getHeight() / 2, birds[1].getWidth() / 2);
    }

    private void saveScore(int score) {
        if (preferences.getInteger("highScore", 0) < score) {
            preferences.putInteger("highScore", score);
            preferences.flush();
        }
    }
}
