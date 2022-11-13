package com.lonerae.nightsintheoutskirts.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lonerae.nightsintheoutskirts.screens.visible.MenuScreen;

public class BaseScreen implements Screen {

    private final Game game;

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    private final TextureAtlas atlas;
    private final Skin skin;
    private static I18NBundle strings;

    private boolean isTraceable;

    //PUBLIC CONSTANTS
    public final static float WIDTH = (float) Gdx.graphics.getWidth();
    public final static float HEIGHT = (float) Gdx.graphics.getHeight();

    public final static float DEFAULT_ACTOR_WIDTH = (WIDTH * 3) / 4;
    public final static float DEFAULT_ACTOR_HEIGHT = 200;
    public final static float DEFAULT_ICON_SIZE = 250;
    public final static float DEFAULT_POPUP_SIZE = (WIDTH * 4) / 5;

    public final static float PAD_VERTICAL_BIG = HEIGHT / 10;
    public final static float PAD_VERTICAL_SMALL = HEIGHT / 20;
    public final static float PAD_HORIZONTAL_BIG = 80;
    public final static float PAD_HORIZONTAL_SMALL = 40;
    public final static float DIALOG_VERTICAL_PAD = 80;

    public BaseScreen(Game game) {
        this(game, true);
    }

    public BaseScreen(Game game, boolean isTraceable) {
        this.game = game;

        UIUtil uiUtilInstance = UIUtil.getInstance();
        atlas = uiUtilInstance.getAtlas();
        skin = uiUtilInstance.getSkin();
        skin.getFont("font").getData().setScale(2f);
        skin.getPatch("window").scale(1.5f, 1.5f);

        strings = uiUtilInstance.getStrings();
        this.isTraceable = isTraceable;
    }

    public Game getGame() {
        return game;
    }

    public Stage getStage() {
        return stage;
    }

    public Skin getSkin() {
        return skin;
    }

    public static I18NBundle getStrings() {
        return strings;
    }

    public void setTraceable(boolean traceable) {
        isTraceable = traceable;
    }

    @Override
    public void show() {
        SpriteBatch batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
        camera.update();

        stage = new Stage(viewport, batch);
        //HIDE KEYBOARD WHEN CLICKING OUTSIDE OF TEXT FIELD
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return false;
            }
        });

        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        if (isTraceable) {
            ScreenStack.getScreenStack().add(this);
        }

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(1, 1, 1, 1);

        stage.act();
        stage.draw();

        if (isTraceable && Gdx.input.isKeyJustPressed(Input.Keys.BACK)){
            this.game.setScreen(ScreenStack.goToPrevious());
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth/2, camera.viewportHeight/2, 0);
        camera.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        skin.dispose();
        atlas.dispose();
    }
}
