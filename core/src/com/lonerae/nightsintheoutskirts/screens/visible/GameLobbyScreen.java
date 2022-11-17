package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.esotericsoftware.kryonet.Client;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.MatchServer;
import com.lonerae.nightsintheoutskirts.network.requests.AssignRoleRequest;
import com.lonerae.nightsintheoutskirts.network.requests.LobbyRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;

public class GameLobbyScreen extends BaseScreen {

    private Client client;
    public GameLobbyScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        client = MatchClient.getClient();
        client.sendTCP(new LobbyRequest());

        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().get("lobbyTitle"), getSkin());
        UIUtil.title(title);
        TextField playerNameTextField = new TextField("",getSkin());
        playerNameTextField.setMessageText(getStrings().get("playerNamePlaceholder"));
        Label rolesLabel = new CustomLabel(getStrings().get("rolesLobbyLabel"), getSkin());
        rolesLabel.setAlignment(Align.center);

        Table rolesTable = new Table();
        fill(rolesTable);

        TextButton assignRoleButton = new TextButton(getStrings().get("assignRoleButton"), getSkin());
        assignRoleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!playerNameTextField.getText().trim().isEmpty()) {
                    AssignRoleRequest assignRoleRequest = new AssignRoleRequest();
                    assignRoleRequest.playerName = playerNameTextField.getText();
                    client.sendTCP(assignRoleRequest);
                    boolean flag = true;
                    while (flag) {
                        try {
                            Gdx.app.log("ROLE:", MatchClient.getAssignedRole().getName().toString());
                            flag = false;
                        } catch (NullPointerException ignored) {}
                    }
                } else {
                    //error dialog
                }
            }
        });

        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(playerNameTextField).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rolesLabel).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(rolesTable).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(assignRoleButton).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

    }

    private void fill(Table rolesTable) {
        int counter = 0;
        try {
            for (RoleName roleName : MatchClient.getMatchRoleList()) {
                counter++;
                rolesTable.add(new Image(Role.getRole(roleName).getIcon())).width(DEFAULT_ICON_SIZE).height(DEFAULT_ICON_SIZE).pad(PAD_HORIZONTAL_SMALL);

                if (counter % 3 == 0) {
                    rolesTable.row();
                }
            }
        } catch (NullPointerException e) {
            fill(rolesTable);
        }
    }

    @Override
    public void dispose() {
        MatchServer.getServer().close();
    }
}
