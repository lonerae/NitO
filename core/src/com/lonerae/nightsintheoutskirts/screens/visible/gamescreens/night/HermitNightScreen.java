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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextField;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.DayScreen;

import java.util.HashMap;
import java.util.Map;

public class HermitNightScreen extends NightScreen {

    private final ButtonGroup<CheckBox> voteCheckGroup = new ButtonGroup<>();
    private final Map<CheckBox, TextField> choiceMap = new HashMap<>();

    public HermitNightScreen(Game game) {
        super(game);
        voteCheckGroup.setMinCheckCount(0);

    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getBlackStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(GameData.getRoleStrings().get("hermitNight"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        Table alivePlayerTable = new Table(getSkin());
        for (String playerName : MatchClient.getAlivePlayersMap().keySet()) {
            CheckBox voteCheck = new CheckBox(playerName, getSkin());
            voteCheck.getLabel().setStyle(getBlackStyle());
            voteCheck.getImage().setScaling(Scaling.fill);
            voteCheck.getImageCell().size(WIDTH / 15);
            voteCheck.getLabelCell().padLeft(PAD_HORIZONTAL_SMALL);
            voteCheckGroup.add(voteCheck);

            TextField allianceTextField = new CustomTextField("", getTextFieldStyle());
            allianceTextField.setTouchable(Touchable.disabled);

            choiceMap.put(voteCheck, allianceTextField);

            alivePlayerTable.add(voteCheck).pad(PAD_HORIZONTAL_SMALL);
            alivePlayerTable.add(allianceTextField).width(WIDTH/3).pad(PAD_HORIZONTAL_SMALL).row();
        }

        mainTable.add(alivePlayerTable).row();

        TextButton activateButton = new CustomTextButton(getStrings().get("abilityUsed"), getSkin(), getBlackStyle());
        activateButton.setTouchable(Touchable.disabled);
        TextButton continueButton = new CustomTextButton(getStrings().get("endNight"), getSkin(), getBlackStyle());

        if (Player.getPlayer().isAbleToUseAbility()) {
            activateButton.setText(getStrings().get("abilityReady"));
            activateButton.setTouchable(Touchable.enabled);
            continueButton.setText(getStrings().get("skipChoise"));
            activateButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (voteCheckGroup.getAllChecked().size == 1) {
                        choiceMap.get(voteCheckGroup.getChecked())
                                .setText(
                                        Role.getRole(MatchClient.getAlivePlayersMap()
                                                        .get(voteCheckGroup.getChecked().getLabel().getText().toString()))
                                                .getAlliance()
                                                .toString()
                                );
                        activateButton.setTouchable(Touchable.disabled);
                        activateButton.setText(getStrings().get("abilityUsed"));
                        continueButton.setText(getStrings().get("endNight"));
                        Player.getPlayer().setAbleToUseAbility(false);
                    } else {
                        showErrorDialog(getStrings().get("noChoiceError"));
                    }
                }
            });
        }

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProceedRequest request = new ProceedRequest();
                waitForOtherPlayers(request, new DayScreen(getGame()));
            }
        });

        Table buttonTable = new Table(getSkin());
        buttonTable.add(activateButton).width(WIDTH / 3).pad(PAD_HORIZONTAL_SMALL);
        buttonTable.add(continueButton).width(WIDTH / 3).pad(PAD_HORIZONTAL_SMALL);

        mainTable.add(buttonTable);

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }
}
