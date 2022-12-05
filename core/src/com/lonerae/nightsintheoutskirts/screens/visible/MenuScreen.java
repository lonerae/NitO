package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
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
        mainTable.defaults().width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT);
        mainTable.center();

        Label title = UIUtil.title(new CustomLabel(getStrings().get("gameTitle"), getTitleStyle()));

        TextButton createButton = new CustomTextButton(getStrings().get("create"), getSkin(), getBlackStyle());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameCreateScreen(getGame()));
            }
        });

        TextButton joinButton = new CustomTextButton(getStrings().get("join"), getSkin(), getBlackStyle());
        joinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                getGame().setScreen(new GameJoinScreen(getGame()));
            }
        });

        TextButton settingsButton = new CustomTextButton(getStrings().get("settings"), getSkin(), getBlackStyle());
        TextButton rulesButton = new CustomTextButton(getStrings().get("rules"), getSkin(), getBlackStyle());

        mainTable.add(title).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(createButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(joinButton).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(settingsButton).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rulesButton).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
