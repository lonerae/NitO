package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
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

import java.util.HashMap;

public class DayScreen extends BaseScreen {

    private static final HashMap<String, TextField> votingMap = new HashMap<>();
    private static final HashMap<String, Dialog> votersMap = new HashMap<>();
    private static final HashMap<String, StringBuilder> votedByMessageMap = new HashMap<>();
    private final ButtonGroup<CheckBox> voteCheckGroup = new ButtonGroup<>();
    private ScrollPane scroll;

    public DayScreen(Game game) {
        super(game);
        voteCheckGroup.setMinCheckCount(0);
    }

    public static void updateVote(String voterName, String votedPlayerName, int votes) {
        if (votes > Integer.parseInt(votingMap.get(votedPlayerName).getText())) {
            addVoter(voterName, votedPlayerName);
        } else {
            removeVoter(voterName, votedPlayerName);
        }
        votingMap.get(votedPlayerName).setText(String.valueOf(votes));
    }

    private static void removeVoter(String voterName, String votedPlayerName) {
        int index = votedByMessageMap.get(votedPlayerName).indexOf(voterName);
        StringBuilder newVoters = votedByMessageMap.get(votedPlayerName).delete(index - 1, index + voterName.length());
        votersMap.get(votedPlayerName).getContentTable().clearChildren();
        votersMap.get(votedPlayerName).getContentTable().add(new CustomLabel(newVoters, getBlackStyle())).width(DEFAULT_POPUP_SIZE);
    }

    private static void addVoter(String voterName, String votedPlayerName) {
        if (votedByMessageMap.get(votedPlayerName).indexOf(voterName) == -1) {
            StringBuilder newVoters = votedByMessageMap.get(votedPlayerName).append("\n").append(voterName);
            votersMap.get(votedPlayerName).getContentTable().clearChildren();
            votersMap.get(votedPlayerName).getContentTable().add(new CustomLabel(newVoters, getBlackStyle())).width(DEFAULT_POPUP_SIZE);
        }
    }

    public ScrollPane getScroll() {
        return scroll;
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
        fillVotingTable(votingTable);

        mainTable.add(votingTable).padBottom(PAD_VERTICAL_BIG).row();

        if (Player.getPlayer().isAlive()) {
            addLockButton(mainTable);
        } else {
            waitForAlivePlayers(new DayResolutionScreen(getGame()));
        }

        scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);

        checkEndGame();
    }

    private void checkEndGame() {
        while (true) {
            try {
                if (MatchClient.isEndGame()) {
                    getGame().setScreen(new EndGameScreen(getGame()));
                }
                MatchClient.setEndGame(null);
                break;
            } catch (NullPointerException ignored) {
            }
        }
    }

    private void addLockButton(Table mainTable) {
        TextButton lockButton = new CustomTextButton(getStrings().get("lockChoice"), getSkin(), getBlackStyle());
        lockButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
//                if (voteCheckGroup.getAllChecked().size == 1) {
                continueToResolution();
//                } else {
//                    showErrorDialog(getStrings().get("noVoteError"));
//                }
            }
        });
        mainTable.add(lockButton).width(DEFAULT_ACTOR_WIDTH);
    }

    private void continueToResolution() {
        ProceedRequest request = new ProceedRequest();
        waitForOtherPlayers(request, ProceedType.VOTING, new DayResolutionScreen(getGame()));
    }

    private void fillVotingTable(Table votingTable) {
        int i = 0;
        Table voteTable;

        if (!MatchClient.getAlivePlayersMap().containsKey(Player.getPlayer().getName())) {
            Player.getPlayer().setAlive(false);
        }

        if (Player.getPlayer().isAlive()) {
            i++;
            voteTable = getTable(Player.getPlayer().getName());
            votingTable.add(voteTable).width(WIDTH / 5).pad(PAD_HORIZONTAL_BIG);
        }

        for (String player : MatchClient.getAlivePlayersMap().keySet()) {
            if (!player.equals(Player.getPlayer().getName())) {
                i++;
                voteTable = getTable(player);
                votingTable.add(voteTable).width(WIDTH / 5).pad(PAD_HORIZONTAL_BIG);

                if (i % 3 == 0) {
                    votingTable.row();
                }
            }
        }
    }

    private Table getTable(String player) {
        Table voteTable = new Table(getSkin());

        Label playerName = new CustomLabel(player, getBlackStyle());
        playerName.setAlignment(Align.center);
        voteTable.add(playerName).width(WIDTH / 5).row();

        Table voteInfo = new Table(getSkin());
        TextField voteCount = new CustomTextField("0", getTextFieldStyle());
        voteCount.setTouchable(Touchable.disabled);

        votingMap.put(player, voteCount);
        voteInfo.add(voteCount).width(WIDTH / 11).pad(20);

        if (Player.getPlayer().isAlive() && !player.equals(Player.getPlayer().getName())) {
            addVotingBox(player, voteInfo);
        }

        TextButton votedByButton = addVotedByButton(player, voteTable);

        voteTable.add(voteInfo).row();
        voteTable.add(votedByButton).width(WIDTH / 4);
        return voteTable;
    }

    private TextButton addVotedByButton(String player, Table voteTable) {
        voteTable.row();
        TextButton votedByButton = new CustomTextButton(getStrings().get("votersButtonTitle"), getSkin(), getBlackStyle());
        Dialog votedByDialog = new CustomDialog(getSkin(), getBlackStyle());
        votersMap.put(player, votedByDialog);
        votedByMessageMap.put(player, new StringBuilder());

        votedByButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                votedByDialog.show(getStage());
                getScroll().setFlickScroll(false);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                votedByDialog.hide();
                getScroll().setFlickScroll(true);
            }
        });
        return votedByButton;
    }

    private void addVotingBox(String player, Table voteInfo) {
        CheckBox voteCheck = new CheckBox(null, getSkin());
        voteCheck.getImage().setScaling(Scaling.fill);
        voteCheck.getImageCell().size(WIDTH / 15);
        voteCheck.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (voteCheck.isChecked()) {
                    sendVote(player, 1);
                } else {
                    sendVote(player, -1);
                }
            }
        });
        voteCheckGroup.add(voteCheck);
        voteInfo.add(voteCheck);
    }

    private void sendVote(String player, int vote) {
        VoteRequest request = new VoteRequest();
        request.voterName = Player.getPlayer().getName();
        request.votedPlayerName = player;
        request.vote = vote;
        MatchClient.getClient().sendTCP(request);
    }
}
