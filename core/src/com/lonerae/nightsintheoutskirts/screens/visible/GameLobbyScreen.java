package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchServer;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

public class GameLobbyScreen extends BaseScreen {

    public GameLobbyScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().format("lobbyTitle"), getSkin());
        UIUtil.title(title);
        TextField playerNameTextField = new TextField("",getSkin());
        playerNameTextField.setMessageText(getStrings().format("playerNamePlaceholder"));
        Label rolesLabel = new CustomLabel(getStrings().format("rolesLobbyLabel"), getSkin());
        rolesLabel.setAlignment(Align.center);

        Table rolesTable = new Table();
        fill(rolesTable);

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(playerNameTextField).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rolesLabel).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rolesTable).padBottom(PAD_VERTICAL_SMALL).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void fill(Table rolesTable) {
        int counter = 0;
        for (RoleName roleName : MatchServer.getMatchRoleList().keySet()) {
            counter++;
            rolesTable.add(new Image(Role.getRole(roleName).getIcon())).width(DEFAULT_ICON_SIZE).height(DEFAULT_ICON_SIZE).pad(PAD_HORIZONTAL_SMALL);

            if (counter % 3 == 0) {
                rolesTable.row();
            }
        }
    }

    @Override
    public void dispose() {
        MatchServer.getServer().close();
    }
}
