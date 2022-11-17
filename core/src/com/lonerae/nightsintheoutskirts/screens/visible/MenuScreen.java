package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;

public class MenuScreen extends BaseScreen {

    public MenuScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.defaults().width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT);
        mainTable.center();

        Label title = UIUtil.title(new CustomLabel(getStrings().get("gameTitle"), getSkin()));

        TextButton createButton = new TextButton(getStrings().get("create"), getSkin());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameCreateScreen(getGame()));
            }
        });

        TextButton joinButton = new TextButton(getStrings().get("join"), getSkin());
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameJoinScreen(getGame()));
            }
        });

        TextButton settingsButton = new TextButton(getStrings().get("settings"), getSkin());
        TextButton rulesButton = new TextButton(getStrings().get("rules"), getSkin());

        mainTable.add(title).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(createButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(joinButton).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(settingsButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rulesButton).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
