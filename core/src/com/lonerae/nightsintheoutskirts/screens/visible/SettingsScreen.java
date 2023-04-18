package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;

public class SettingsScreen extends BaseScreen {

    public SettingsScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.top().padTop(PAD_VERTICAL_SMALL).defaults().width(DEFAULT_POPUP_SIZE).height(DEFAULT_ACTOR_HEIGHT);

        Label title = new CustomLabel(getStrings().get("settings"), getTitleStyle());
        UIUtil.title(title);

        mainTable.add(title).padBottom(PAD_VERTICAL_BIG).padRight(10).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
