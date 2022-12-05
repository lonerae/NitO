package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.FirstNightScreen;

public class RoleRevealScreen extends BaseScreen {

    public RoleRevealScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label roleName = new CustomLabel(Player.getPlayer().getRole().getName().toString(), getTitleStyle());
        UIUtil.title(roleName);
        Image roleIcon = new Image(new Texture(Player.getPlayer().getRole().getIconPath()));
        Label roleDescription = new CustomLabel(Player.getPlayer().getRole().getDescription(), getBlackStyle());
        Button startButton = new CustomTextButton(getStrings().get("startGameButton"), getSkin(), getBlackStyle());
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("waitMessage"), getSkin(), getBlackStyle());
                dialog.show(getStage());
                ProceedRequest request = new ProceedRequest();
                request.type = ProceedType.FIRST;
                MatchClient.getClient().sendTCP(request);
                new Thread(() -> {
                    while (true) {
                        try {
                            if (MatchClient.isPermitted()) {
                                Gdx.app.postRunnable(() -> {
                                    dialog.hide();
                                    getGame().setScreen(new FirstNightScreen(getGame()));
                                });
                                MatchClient.setPermitted(false);
                                break;
                            }
                        } catch (NullPointerException ignored) {
                        }
                    }
                }).start();
            }
        });

        mainTable.add(roleName).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(roleIcon).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_SMALL).row();
        mainTable.add(roleDescription).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(startButton).width(DEFAULT_ACTOR_WIDTH).height(DEFAULT_ACTOR_HEIGHT).padBottom(PAD_VERTICAL_SMALL).row();

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
