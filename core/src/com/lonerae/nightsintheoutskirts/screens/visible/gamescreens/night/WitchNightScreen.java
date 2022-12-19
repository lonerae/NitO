package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.KillRequest;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.SaveRequest;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.NightResolutionScreen;

public class WitchNightScreen extends NightScreen {

    private final ButtonGroup<CheckBox> voteCheckGroup = new ButtonGroup<>();

    public WitchNightScreen(Game game) {
        super(game);
        voteCheckGroup.setMinCheckCount(0);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getTitleStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(GameData.getRoleStrings().get("witchNight"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table alivePlayerTable = new Table(getSkin());
        fillAlivePlayerTable(alivePlayerTable);

        mainTable.add(alivePlayerTable).padBottom(PAD_VERTICAL_BIG).row();

        TextButton saveButton = new CustomTextButton(getStrings().get("abilityUsed"), getButtonStyle());
        saveButton.setTouchable(Touchable.disabled);
        TextButton killButton = new CustomTextButton(getStrings().get("abilityUsed"), getButtonStyle());
        killButton.setTouchable(Touchable.disabled);
        TextButton continueButton = new CustomTextButton(getStrings().get("endNight"), getButtonStyle());

        if (Player.getPlayer().isAbleToUseAbility()) {
            updateButtons(saveButton, killButton, continueButton);
        }

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProceedRequest request = new ProceedRequest();
                waitForOtherPlayers(request, ProceedType.ABILITY, new NightResolutionScreen(getGame()));
            }
        });

        Table buttonTable = new Table(getSkin());
        buttonTable.add(saveButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL);
        buttonTable.add(killButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL).row();
        buttonTable.add(continueButton).width(DEFAULT_ACTOR_WIDTH).colspan(2).pad(PAD_HORIZONTAL_SMALL);

        mainTable.add(buttonTable);

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void updateButtons(TextButton saveButton, TextButton killButton, TextButton continueButton) {
        saveButton.setText(getStrings().get("witchSaveReady"));
        saveButton.setTouchable(Touchable.enabled);
        killButton.setText(getStrings().get("witchKillReady"));
        killButton.setTouchable(Touchable.enabled);
        continueButton.setText(getStrings().get("skipChoice"));
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveOrError(saveButton, killButton, continueButton);
            }
        });
        killButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                killOrError(killButton, saveButton, continueButton);
            }
        });
    }

    private void saveOrError(TextButton saveButton, TextButton killButton, TextButton continueButton) {
        if (voteCheckGroup.getAllChecked().size == 1) {
            save(saveButton, killButton, continueButton);
        } else {
            showErrorDialog(getStrings().get("noChoiceError"));
        }
    }

    private void save(TextButton saveButton, TextButton killButton, TextButton continueButton) {
        SaveRequest request = new SaveRequest();
        request.playerName = voteCheckGroup.getChecked().getLabel().getText().toString();
        MatchClient.getMatchClientInstance().getClient().sendTCP(request);

        saveButton.setTouchable(Touchable.disabled);
        saveButton.setText(getStrings().get("abilityUsed"));
        killButton.setTouchable(Touchable.disabled);
        killButton.setText(getStrings().get("abilityUsed"));
        continueButton.setText(getStrings().get("endNight"));
        Player.getPlayer().setAbleToUseAbility(false);
    }

    private void killOrError(TextButton killButton, TextButton saveButton, TextButton continueButton) {
        if (voteCheckGroup.getAllChecked().size == 1) {
            kill(saveButton, killButton, continueButton);
        } else {
            showErrorDialog(getStrings().get("noChoiceError"));
        }
    }

    private void kill(TextButton saveButton, TextButton killButton, TextButton continueButton) {
        KillRequest request = new KillRequest();
        request.playerName = voteCheckGroup.getChecked().getLabel().getText().toString();
        MatchClient.getMatchClientInstance().getClient().sendTCP(request);

        killButton.setTouchable(Touchable.disabled);
        killButton.setText(getStrings().get("abilityUsed"));
        saveButton.setTouchable(Touchable.disabled);
        saveButton.setText(getStrings().get("abilityUsed"));
        continueButton.setText(getStrings().get("endNight"));
        Player.getPlayer().setAbleToUseAbility(false);
    }

    private void fillAlivePlayerTable(Table alivePlayerTable) {
        int count = 0;
        for (String playerName : MatchClient.getMatchClientInstance().getAlivePlayersMap().keySet()) {
            CheckBox voteCheck = createCheckBox(playerName);
            alivePlayerTable.add(voteCheck).pad(PAD_HORIZONTAL_SMALL);
            count++;
            if (count % 3 == 0) {
                alivePlayerTable.row();
            }
        }
    }

    private CheckBox createCheckBox(String playerName) {
        CheckBox voteCheck = new CheckBox(playerName, getSkin());
        voteCheck.getLabel().setStyle(getBlackStyle());
        voteCheck.getImage().setScaling(Scaling.fill);
        voteCheck.getImageCell().size(WIDTH / 15);
        voteCheck.getLabelCell().padLeft(PAD_HORIZONTAL_SMALL);
        voteCheckGroup.add(voteCheck);
        return voteCheck;
    }
}
