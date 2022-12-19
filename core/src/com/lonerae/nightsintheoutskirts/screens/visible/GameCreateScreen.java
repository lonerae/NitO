package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.game.roles.RoleName;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.MatchServer;
import com.lonerae.nightsintheoutskirts.network.requests.GreetingRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextField;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameCreateScreen extends BaseScreen {

    private final Map<RoleName, TextField> roleCounterMap = new LinkedHashMap<>();
    private ScrollPane scroll;

    public GameCreateScreen(Game game) {
        super(game);
        setTraceable();
    }

    public ScrollPane getScroll() {
        return scroll;
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);
        Label title = UIUtil.title(new CustomLabel(getStrings().get("create"), getTitleStyle()));
        mainTable.add(title).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.center();

        Table menuTable = new Table();
        menuTable.defaults().width(DEFAULT_ACTOR_WIDTH);
        Label townNameLabel = new CustomLabel(getStrings().get("townNameLabel"), getBlackStyle());
        townNameLabel.setAlignment(Align.center);
        TextField townNameTextField = new CustomTextField("", getTextFieldStyle());
        Label numberOfPlayersLabel = new CustomLabel(getStrings().get("numberOfPlayersLabel"), getBlackStyle());
        numberOfPlayersLabel.setAlignment(Align.center);
        TextField numberOfPlayersTextField = new CustomTextField("", getTextFieldStyle());
        numberOfPlayersTextField.setMessageText("0");
        numberOfPlayersTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        Label rolesLabel = new CustomLabel(getStrings().get("rolesLabel"), getBlackStyle());
        menuTable.add(townNameLabel).padBottom(PAD_VERTICAL_SMALL).row();
        menuTable.add(townNameTextField).padBottom(PAD_VERTICAL_SMALL).row();
        menuTable.add(numberOfPlayersLabel).padBottom(PAD_VERTICAL_SMALL).row();
        menuTable.add(numberOfPlayersTextField).padBottom(PAD_VERTICAL_SMALL).row();
        menuTable.add(rolesLabel).padBottom(PAD_VERTICAL_SMALL).row();

        mainTable.add(menuTable).row();

        Table rolesTable = new Table();
        fill(rolesTable);

        mainTable.add(rolesTable).padBottom(PAD_VERTICAL_SMALL).row();

        TextButton createButton = new CustomTextButton(getStrings().get("create"), getButtonStyle());
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                checkGameConditions(townNameTextField, numberOfPlayersTextField);
            }
        });

        mainTable.add(createButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL);
        scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private Map<RoleName, Integer> createMatchRoleList() {
        Map<RoleName, Integer> matchRoleList = new LinkedHashMap<>();
        for (Map.Entry<RoleName, TextField> entry : roleCounterMap.entrySet()) {
            int roleCounter = getTextFieldNumber(entry.getValue());
            if (roleCounter > 0) {
                matchRoleList.put(entry.getKey(), roleCounter);
            }
        }
        return matchRoleList;
    }

    private int getTextFieldNumber(TextField textField) {
        try {
            return Integer.parseInt(textField.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void fill(Table rolesTable) {
        int iconCounter = 0;
        for (Role role : GameData.getRoleList().values()) {
            iconCounter++;
            Table roleIconAndCounter = createRoleIconAndCounter(role);
            rolesTable.add(roleIconAndCounter).pad(PAD_HORIZONTAL_BIG);
            if (iconCounter % 3 == 0) {
                rolesTable.row();
            }
        }
    }

    private Table createRoleIconAndCounter(Role role) {
        TextField numberOfRoleTextField = new CustomTextField("", getTextFieldStyle());
        numberOfRoleTextField.setMessageText("0");

        roleCounterMap.put(role.getName(), numberOfRoleTextField);
        numberOfRoleTextField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());

        Table roleIconAndCounter = new Table();
        CustomDialog dialog = new CustomDialog(role.getName().toString(), role.getDescription(), getSkin(), getBlackStyle());
        dialog.isHideable();

        Image icon = new Image(new Texture(role.getIconPath()));
        addResponsiveTextToIcon(dialog, icon);

        roleIconAndCounter.add(icon).width(DEFAULT_ICON_SIZE).height(DEFAULT_ICON_SIZE).padBottom(PAD_VERTICAL_SMALL).row();
        roleIconAndCounter.add(numberOfRoleTextField).width(DEFAULT_ICON_SIZE);
        return roleIconAndCounter;
    }

    private void addResponsiveTextToIcon(CustomDialog dialog, Image icon) {
        icon.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialog.show(getStage());
                getScroll().setFlickScroll(false);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                dialog.hide();
                getScroll().setFlickScroll(true);
            }
        });
    }

    private void checkGameConditions(TextField townNameTextField, TextField numberOfPlayersTextField) {
        int initialCounter = getTextFieldNumber(numberOfPlayersTextField);
        int counter = getTextFieldNumber(numberOfPlayersTextField);
        for (TextField roleCounter : roleCounterMap.values()) {
            counter -= getTextFieldNumber(roleCounter);
        }

        if (!townNameTextField.getText().isEmpty() && initialCounter > 0 && counter == 0) {
            createSuccessDialog(townNameTextField, numberOfPlayersTextField).show(getStage());
        } else {
            showErrorDialog(getStrings().get("createGameError"));
        }
    }

    private Dialog createSuccessDialog(TextField townNameTextField, TextField numberOfPlayersTextField) {
        Dialog successDialog = new CustomDialog(getStrings().get("gameInfo"), createSuccessMessage(townNameTextField, numberOfPlayersTextField), getSkin(), getBlackStyle(), false) {
            public void result(Object obj) {
                if ((boolean) obj) {
                    GameData match = new GameData(townNameTextField.getText(), getTextFieldNumber(numberOfPlayersTextField), createMatchRoleList());
                    try {
                        MatchServer.getMatchServerInstance().createServer(match);
                        MatchClient.getMatchClientInstance().getClient().connect(5000, InetAddress.getLocalHost().getHostAddress(), 54555, 54777);
                        GreetingRequest request = new GreetingRequest();
                        MatchClient.getMatchClientInstance().getClient().sendTCP(request);
                    } catch (UnknownHostException e) {
                        Gdx.app.log("SERVER CREATION ERROR: ", e.getMessage());
                    } catch (IOException e) {
                        Gdx.app.log("CONNECTION ERROR: ", e.getMessage());
                    }
                    getGame().setScreen(new GameLobbyScreen(getGame()));
                } else {
                    this.hide();
                }
            }
        };
        successDialog.button(new CustomTextButton("CANCEL", getButtonStyle()), false);
        successDialog.button(new CustomTextButton("OKAY", getButtonStyle()), true);
        return successDialog;
    }

    private String createSuccessMessage(TextField townNameTextField, TextField numberOfPlayersTextField) {
        StringBuilder gameInfo = new StringBuilder();
        Formatter format = new Formatter(gameInfo);
        format.format(getStrings().get("createGameMessage"), townNameTextField.getText(), getTextFieldNumber(numberOfPlayersTextField));
        for (Map.Entry<RoleName, TextField> roleCounterMapEntry : roleCounterMap.entrySet()) {
            if (getTextFieldNumber(roleCounterMapEntry.getValue()) > 0) {
                gameInfo.append("\n").append(roleCounterMapEntry.getKey()).append(" : ").append(roleCounterMapEntry.getValue().getText());
            }
        }
        gameInfo.append("\n");
        return gameInfo.toString();
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            MatchClient.getMatchClientInstance().getClient().stop();
            MatchServer.getMatchServerInstance().getServer().stop();
        } catch (NullPointerException ignored) {
        }
    }
}
