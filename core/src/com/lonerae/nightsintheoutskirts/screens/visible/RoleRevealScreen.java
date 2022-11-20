package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

public class RoleRevealScreen extends BaseScreen {

    public RoleRevealScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label roleName = new CustomLabel(Player.getPlayer().getRole().getName().toString(), getSkin());
        UIUtil.title(roleName);
        Image roleIcon = new Image(new Texture(Player.getPlayer().getRole().getIconPath()));
        Label roleDescription = new CustomLabel(Player.getPlayer().getRole().getDescription(), getSkin());
        Button startButton = new TextButton(getStrings().get("startGameButton"), getSkin());

        mainTable.add(roleName).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(roleIcon).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(roleDescription).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(startButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
