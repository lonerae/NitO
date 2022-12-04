package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.network.requests.VoteRequest;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomDialog;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextField;

public class DayScreen extends BaseScreen {

    public DayScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Day", getBlackStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(getGameStrings().get("day"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table votingTable = new Table(getSkin());
        if (Player.getPlayer().isAlive()) {
            int i = 0;
            for (String player : MatchClient.getAlivePlayersMap().keySet()) {
//                if (!player.equals(Player.getPlayer().getName())) {
                    i++;
                    Table voteTable = getTable(player);

                    votingTable.add(voteTable).width(WIDTH / 5).pad(PAD_HORIZONTAL_BIG);

                    if (i % 3 == 0) {
                        votingTable.row();
                    }
//                }
            }
        }

        TextButton lockButton = new CustomTextButton(getStrings().get("lockChoice"), getSkin(), getBlackStyle());
        lockButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new CustomDialog(getStrings().get("messageInfo"), getStrings().get("waitMessage"), getSkin(), getBlackStyle());
                dialog.show(getStage());
                ProceedRequest request = new ProceedRequest();
                request.type = ProceedType.VOTING;
                MatchClient.getClient().sendTCP(request);
                new Thread(() -> {
                    while (true) {
                        try {
                            if (MatchClient.isPermitted()) {
                                Gdx.app.postRunnable(() -> {
                                    dialog.hide();
                                    getGame().setScreen(new DayResolutionScreen(getGame()));
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

        mainTable.add(votingTable).padBottom(PAD_VERTICAL_BIG).row();
        mainTable.add(lockButton);

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private Table getTable(String player) {
        Table voteTable = new Table(getSkin());

        Label playerName = new CustomLabel(player, getBlackStyle());
        playerName.setAlignment(Align.center);
        voteTable.add(playerName).width(WIDTH/5).row();

        Table voteInfo = new Table(getSkin());
        TextField voteCount = new CustomTextField("", getTextFieldStyle());
        voteCount.setTouchable(Touchable.disabled);
        voteCount.setMessageText("0");
        CheckBox voteCheck = new CheckBox(null, getSkin());
        voteCheck.getImage().setScaling(Scaling.fill);
        voteCheck.getImageCell().size(WIDTH/15);
        voteCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (voteCheck.isChecked()) {
                    VoteRequest request = new VoteRequest();
                    request.votedPlayerName = player;
                    request.vote = 1;
                    MatchClient.getClient().sendTCP(request);
                } else {
                    VoteRequest request = new VoteRequest();
                    request.votedPlayerName = player;
                    request.vote = -1;
                    MatchClient.getClient().sendTCP(request);
                }
            }
        });
        voteInfo.add(voteCount).width(WIDTH/11).pad(20);
        voteInfo.add(voteCheck);

        voteTable.add(voteInfo);
        return voteTable;
    }
}
