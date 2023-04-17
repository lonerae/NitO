package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
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
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.MatchServer;
import com.lonerae.nightsintheoutskirts.network.requests.AssignRoleRequest;
import com.lonerae.nightsintheoutskirts.network.requests.LobbyRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.ScreenStack;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;

public class GameLobbyScreen extends BaseScreen {

    private Client client;

    public GameLobbyScreen(Game game) {
        super(game);
        setTraceable();
    }

    @Override
    public void show() {
        client = MatchClient.getMatchClientInstance().getClient();
        client.sendTCP(new LobbyRequest());

        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel(getStrings().get("lobbyTitle"), getTitleStyle());
        UIUtil.title(title);
        TextField playerNameTextField = new TextField("", getTextFieldStyle());
        playerNameTextField.setMessageText(getStrings().get("playerNamePlaceholder"));
        Label rolesLabel = new CustomLabel(getStrings().get("rolesLobbyLabel"), getBlackStyle());
        rolesLabel.setAlignment(Align.center);

        Table rolesTable = new Table();
        fill(rolesTable);

        TextButton assignRoleButton = new CustomTextButton(getStrings().get("assignRoleButton"), getButtonStyle());
        assignRoleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                assignRoleOrError(playerNameTextField);
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

    private void assignRoleOrError(TextField playerNameTextField) {
        String playerName = playerNameTextField.getText();
        if (!playerName.trim().isEmpty()) {
            if (playerName.length() < 6) {
                assignRole(playerNameTextField);
            } else {
                showErrorDialog(getStrings().get("longNameError"));
            }
        } else {
            showErrorDialog(getStrings().get("emptyNameError"));
        }
    }

    private void assignRole(TextField playerNameTextField) {
        AssignRoleRequest assignRoleRequest = new AssignRoleRequest();
        assignRoleRequest.playerName = playerNameTextField.getText();
        client.sendTCP(assignRoleRequest);
        while (true) {
            try {
                Player.getPlayer().setRole(MatchClient.getMatchClientInstance().getAssignedRole());
                Player.getPlayer().setName(playerNameTextField.getText());
                ScreenStack.clearStack();
                getGame().setScreen(new RoleRevealScreen(getGame()));
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    private void fill(Table rolesTable) {
        int counter = 0;
        while (true) {
            try {
                for (RoleName roleName : MatchClient.getMatchClientInstance().getMatchRoleList()) {
                    counter++;
                    Image lobbyIcon = new Image(new Texture(Role.getRole(roleName).getIconPath()));
                    rolesTable.add(lobbyIcon).width(DEFAULT_ICON_SIZE).height(DEFAULT_ICON_SIZE).pad(PAD_HORIZONTAL_SMALL);
                    if (counter % 3 == 0) {
                        rolesTable.row();
                    }
                }
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Override
    public void dispose() {
//        try {
//            MatchClient.getMatchClientInstance().close();
//            MatchServer.getMatchServerInstance().close();
//        } catch (NullPointerException ignored) {
//
//        }
    }
}
