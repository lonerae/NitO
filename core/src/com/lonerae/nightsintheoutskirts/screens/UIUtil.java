package com.lonerae.nightsintheoutskirts.screens;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
    private final TextButton.TextButtonStyle textButtonStyle;

    public UIUtil() {
        FreeTypeFontGenerator generatorTitle = new FreeTypeFontGenerator(Gdx.files.internal("skin/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterTitle = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterTitle.size = (int) (0.08 * WIDTH);
        BitmapFont fontTitle = generatorTitle.generateFont(parameterTitle);

        FreeTypeFontGenerator generatorNormal = new FreeTypeFontGenerator(Gdx.files.internal("skin/arial.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameterNormal = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameterNormal.size = (int) (0.06 * WIDTH);
        BitmapFont fontNormal = generatorNormal.generateFont(parameterNormal);

        titleStyle = new Label.LabelStyle();
        titleStyle.font = fontTitle;
        titleStyle.fontColor = Color.BLACK;

        redStyle = new Label.LabelStyle();
        redStyle.font = fontNormal;
        redStyle.fontColor = Color.RED;

        blackStyle = new Label.LabelStyle();
        blackStyle.font = fontNormal;
        blackStyle.fontColor = Color.BLACK;

        textFieldStyle = new TextField.TextFieldStyle(fontNormal, Color.BLACK, null,
                skin.getDrawable("selection"), skin.getDrawable("textfield"));

        textButtonStyle = new TextButton.TextButtonStyle(skin.getDrawable("round-gray"),
                skin.getDrawable("round-dark-gray"), skin.getDrawable("round-gray"),
                fontNormal);
    }

    public static UIUtil getInstance() {
        if (instance == null) {
            instance = new UIUtil();
        }
        return instance;
    }

    public static void center(Actor actor) {
        actor.setPosition(WIDTH / 2 - (actor.getWidth() / 2), BaseScreen.HEIGHT / 2 - (actor.getHeight() / 2));
    }

    public static Label title(Label label) {
        label.setAlignment(Align.center);
        return label;
    }

    public static String justifyText(String message) {
        String[] words = message.split(" ");
        String separator = " ";
        StringBuilder row = new StringBuilder();
        StringBuilder newText = new StringBuilder();

        int i = 0;
        int rowLength = 27;
        while (row.length() <= rowLength && i < words.length) {
            if (words[i].length() <= rowLength - row.length() && (row.length() == 0 || (row.charAt(row.length() - 1) == ' '))) {
                addWord(words, separator, row, i, rowLength);
            } else {
                addSpaces(separator, row, rowLength);
                newText.append(row).append("\n");
                row = new StringBuilder();
                addWord(words, separator, row, i, rowLength);
            }
            if (i == words.length - 1 && row.length() != 0) {
                newText.append(row);
            }
            i++;
        }
        return newText.toString();
    }

    private static void addSpaces(String separator, StringBuilder row, int rowLength) {
        int start = 0;
        while (row.length() < rowLength) {
            if (row.indexOf(separator, start) == -1) {
                start = 0;
            }
            String changed = row.insert(row.indexOf(separator, start), separator).substring(0, row.indexOf(separator, start));
            start = row.indexOf(changed) + changed.length();
            while (start < row.length() && row.charAt(start) == ' ') {
                start++;
            }
        }
    }

    private static void addWord(String[] words, String separator, StringBuilder row, int i, int rowLength) {
        row.append(words[i]);
        if (i + 1 < words.length && !(1 + words[i + 1].length() > rowLength - row.length())) {
            row.append(separator);
        }
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

    public TextButton.TextButtonStyle getTextButtonStyle() {
        return textButtonStyle;
    }
}
