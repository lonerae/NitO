package com.lonerae.nightsintheoutskirts.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

public class UIUtil {

    private static UIUtil instance;
    private final TextureAtlas atlas = new TextureAtlas("skin/plain-james-ui.atlas");
    private final Skin skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"), atlas);
    private final I18NBundle strings = I18NBundle.createBundle(Gdx.files.internal("strings/strings"));
    private final I18NBundle gameStrings = I18NBundle.createBundle(Gdx.files.internal("strings/gameStrings"));

    public static UIUtil getInstance() {
        if (instance == null) {
            instance = new UIUtil();
        }
        return instance;
    }

    public Skin getSkin() {
        return skin;
    }

    public I18NBundle getStrings() {
        return strings;
    }

    public I18NBundle getGameStrings() {
        return gameStrings;
    }

    public static void center(Actor actor) {
        actor.setPosition(BaseScreen.WIDTH/2 - (actor.getWidth()/2), BaseScreen.HEIGHT/2 - (actor.getHeight()/2));
    }
    public static Label title(Label label) {
        label.setFontScale(3);
        label.setAlignment(Align.center);
        return label;
    }
}
