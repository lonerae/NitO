package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;

public class RulesScreen extends BaseScreen {

    public RulesScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new Table();
        mainTable.top().defaults().width(DEFAULT_POPUP_SIZE).height(DEFAULT_ACTOR_HEIGHT);

        Label title = new CustomLabel(getStrings().get("rules"), getTitleStyle());
        UIUtil.title(title);

        mainTable.add(title).padBottom(10).padRight(10).row();

        FileHandle rulesFile = Gdx.files.internal("strings/rules/main.txt");
        String rulesText = rulesFile.readString();
        String[] linesArray = rulesText.split("\\n");
        for(String line : linesArray) {
            Label rules = new Label(line, getTxtStyle());
            rules.setWrap(true);
            rules.pack();
            rules.setWidth(DEFAULT_ACTOR_WIDTH);
            // have to duplicate due to LibGDX bug
            rules.pack();
            rules.setWidth(DEFAULT_ACTOR_WIDTH);
            mainTable.add(rules).height(rules.getHeight()).row();
        }

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}