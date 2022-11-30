package com.lonerae.nightsintheoutskirts.screens;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.I18NBundle;

public class UIUtil {

    private static UIUtil instance;
    private final Skin skin = new Skin(Gdx.files.internal("skin/plain-james-ui.json"));
    private final I18NBundle strings = I18NBundle.createBundle(Gdx.files.internal("strings/strings"));
    private final I18NBundle gameStrings = I18NBundle.createBundle(Gdx.files.internal("strings/gameStrings"));
    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle redStyle;
    private final Label.LabelStyle blackStyle;
    private final TextField.TextFieldStyle textFieldStyle;

    public UIUtil() {
        FreeTypeFontGenerator generatorTitle = new FreeTypeFontGenerator(Gdx.files.internal("skin/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterTitle.size = (int) (0.08 * WIDTH);
        BitmapFont fontTitle = generatorTitle.generateFont(parameterTitle);

        titleStyle = new Label.LabelStyle();
        titleStyle.font = fontTitle;
        titleStyle.fontColor = Color.BLACK;

        FreeTypeFontGenerator generatorNormal = new FreeTypeFontGenerator(Gdx.files.internal("skin/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterNormal = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterNormal.size = (int) (0.06 * WIDTH);
        BitmapFont fontNormal = generatorNormal.generateFont(parameterNormal);

        redStyle = new Label.LabelStyle();
        redStyle.font = fontNormal;
        redStyle.fontColor = Color.RED;

        blackStyle = new Label.LabelStyle();
        blackStyle.font = fontNormal;
        blackStyle.fontColor = Color.BLACK;

        textFieldStyle = new TextField.TextFieldStyle(fontNormal, Color.BLACK, skin.getDrawable("cursor"),
                skin.getDrawable("selection"), skin.getDrawable("textfield"));
    }

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

    public Label.LabelStyle getTitleStyle() {
        return titleStyle;
    }

    public Label.LabelStyle getRedStyle() {
        return redStyle;
    }

    public Label.LabelStyle getBlackStyle() {
        return blackStyle;
    }

    public TextField.TextFieldStyle getTextFieldStyle() {
        return textFieldStyle;
    }

    public static void center(Actor actor) {
        actor.setPosition(WIDTH/2 - (actor.getWidth()/2), BaseScreen.HEIGHT/2 - (actor.getHeight()/2));
    }
    public static Label title(Label label) {
        label.setAlignment(Align.center);
        return label;
    }
}
