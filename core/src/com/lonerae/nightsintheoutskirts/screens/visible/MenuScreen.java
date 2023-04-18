package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

public class MenuScreen extends BaseScreen {

    public MenuScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.top().padTop(PAD_VERTICAL_SMALL).defaults().width(DEFAULT_POPUP_SIZE).height(DEFAULT_ACTOR_HEIGHT);

        Label title = new CustomLabel(getStrings().get("gameTitle"), getTitleStyle());
        UIUtil.title(title);

        TextButton createButton = new CustomTextButton(getStrings().get("create"), getButtonStyle());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameCreateScreen(getGame()));
            }
        });

        TextButton joinButton = new CustomTextButton(getStrings().get("join"), getButtonStyle());
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameJoinScreen(getGame()));
            }
        });

//        TextButton settingsButton = new CustomTextButton(getStrings().get("settings"), getButtonStyle());
//        settingsButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                getGame().setScreen(new SettingsScreen(getGame()));
//            }
//        });

        TextButton rulesButton = new CustomTextButton(getStrings().get("rules"), getButtonStyle());
        rulesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new RulesScreen(getGame()));
            }
        });

        mainTable.add(title).padBottom(PAD_VERTICAL_BIG).padRight(10).row();
        mainTable.add(createButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(joinButton).padBottom(PAD_VERTICAL_BIG).row();
//        mainTable.add(settingsButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rulesButton).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
