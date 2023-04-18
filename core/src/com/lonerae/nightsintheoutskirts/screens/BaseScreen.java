package com.lonerae.nightsintheoutskirts.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.I18NBundle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.EndGameScreen;

public class BaseScreen implements Screen {

    //PUBLIC CONSTANTS
    public final static float ASPECT_RATIO = (float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth();
    public final static float WIDTH = (float) Gdx.graphics.getWidth();
    public final static float HEIGHT = (float) Gdx.graphics.getHeight();
    public final static float DEFAULT_ACTOR_WIDTH = (WIDTH * 3) / 4;
    public final static float DEFAULT_ACTOR_HEIGHT = HEIGHT / 10;
    public final static float DEFAULT_ICON_SIZE = WIDTH / 5;
    public final static float LOGO_SIZE = WIDTH / 2;
    public final static float DEFAULT_POPUP_SIZE = (WIDTH * 4) / 5;
    public final static float PAD_VERTICAL_BIG = HEIGHT / 10;
    public final static float PAD_VERTICAL_SMALL = HEIGHT / 20;
    public final static float PAD_HORIZONTAL_BIG = WIDTH / 15;
    public final static float PAD_HORIZONTAL_SMALL = 40;
    public final static float DIALOG_VERTICAL_PAD = 80;
    private static I18NBundle strings;
    private static I18NBundle gameStrings;
    private static Label.LabelStyle blackStyle;
    private static Label.LabelStyle txtStyle;
    private final Game game;
    private final Skin skin;
    private final Label.LabelStyle titleStyle;
    private final TextButton.TextButtonStyle buttonStyle;
    private final Label.LabelStyle redStyle;
    private final TextField.TextFieldStyle textFieldStyle;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private boolean isTraceable = false;

    public BaseScreen(Game game) {
        this.game = game;

        UIUtil uiUtilInstance = UIUtil.getInstance();
        skin = uiUtilInstance.getSkin();

        titleStyle = uiUtilInstance.getTitleStyle();
        buttonStyle = uiUtilInstance.getTextButtonStyle();
        redStyle = uiUtilInstance.getRedStyle();
        blackStyle = uiUtilInstance.getBlackStyle();
        txtStyle = uiUtilInstance.getTxtStyle();
        textFieldStyle = uiUtilInstance.getTextFieldStyle();

        strings = uiUtilInstance.getStrings();
        gameStrings = uiUtilInstance.getGameStrings();
    }

    public static I18NBundle getStrings() {
        return strings;
    }

    public static I18NBundle getGameStrings() {
        return gameStrings;
    }

    public static Label.LabelStyle getBlackStyle() {
        return blackStyle;
    }

    public static Label.LabelStyle getTxtStyle() {
        return txtStyle;
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

    public void setTraceable() {
        Gdx.input.setCatchKey(Input.Keys.BACK, true);
        ScreenStack.getScreenStack().add(this);
        isTraceable = true;
    }

    public Label.LabelStyle getTitleStyle() {
        return titleStyle;
    }

    public TextButton.TextButtonStyle getButtonStyle() {
        return buttonStyle;
    }

    public Label.LabelStyle getRedStyle() {
        return redStyle;
    }

    public TextField.TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    @Override
    public void show() {
        SpriteBatch batch = new SpriteBatch();
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WIDTH, WIDTH * ASPECT_RATIO, camera);
        viewport.apply();

        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);

        stage = new Stage(viewport, batch);
        stage.getRoot().addCaptureListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (!(event.getTarget() instanceof TextField)) {
                    stage.setKeyboardFocus(null);
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return false;
            }
        });

        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.98f, 0.9f, 0.72f, 1);

        stage.act();
        stage.draw();

        if (isTraceable && Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            this.game.setScreen(ScreenStack.findPrevious());
            this.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
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

    }

    /**
     * Shows a "wait" message and moves to the target {@code Screen} after all players have confirmed.
     *
     * @param request a normal {@code ProceedRequest}
     * @param screen  the target {@code Screen}
     */
    protected void waitForOtherPlayers(ProceedRequest request, Screen screen) {
        Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("waitMessage"), getSkin(), getBlackStyle());
        dialog.show(getStage());
        MatchClient.getMatchClientInstance().getClient().sendTCP(request);
        new Thread(() -> {
            while (true) {
                try {
                    if (MatchClient.getMatchClientInstance().isPermitted()) {
                        Gdx.app.postRunnable(() -> {
                            dialog.hide();
                            getGame().setScreen(screen);
                        });
                        MatchClient.getMatchClientInstance().setPermitted(false);
                        break;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }).start();
    }

    /**
     * Shows a "wait" message and moves to the target {@code Screen} after all players have confirmed.
     * Used for a {@code ProceedRequest} with code != {@code ProccedType.NORMAL}
     *
     * @param request a proceed request
     * @param type    the request's {@code ProceedType}
     * @param screen  the target {@code Screen}
     */
    protected void waitForOtherPlayers(ProceedRequest request, ProceedType type, Screen screen) {
        request.type = type;
        waitForOtherPlayers(request, screen);
    }

    /**
     * Moves to the next {@code Screen} as soon as all the alive players have finished their actions.
     *
     * @param screen the target {@code Screen}
     */
    protected void waitForAlivePlayers(Screen screen) {
        new Thread(() -> {
            while (true) {
                try {
                    if (MatchClient.getMatchClientInstance().isPermitted()) {
                        Gdx.app.postRunnable(() -> {
                            getGame().setScreen(screen);
                        });
                        MatchClient.getMatchClientInstance().setPermitted(false);
                        break;
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }).start();
    }

    protected void showErrorDialog(String errorMessage) {
        CustomDialog dialog = new CustomDialog(getStrings().get("errorInfo"), errorMessage, getSkin(), getBlackStyle());
        dialog.isHideable();
        dialog.show(getStage());
    }

    protected void checkEndGame() {
        while (true) {
            try {
                if (MatchClient.getMatchClientInstance().isEndGame()) {
                    Gdx.app.postRunnable(
                            () -> getGame().setScreen(new EndGameScreen(getGame()))
                    );
                }
                MatchClient.getMatchClientInstance().setEndGame(null);
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }
}
