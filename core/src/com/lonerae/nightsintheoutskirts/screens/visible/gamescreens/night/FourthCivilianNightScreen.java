package com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.night;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.lonerae.nightsintheoutskirts.game.GameData;
import com.lonerae.nightsintheoutskirts.game.Player;
import com.lonerae.nightsintheoutskirts.game.roles.Role;
import com.lonerae.nightsintheoutskirts.network.MatchClient;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedRequest;
import com.lonerae.nightsintheoutskirts.network.requests.ProceedType;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.FourthRequest;
import com.lonerae.nightsintheoutskirts.network.requests.abilities.SaveRequest;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomLabel;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomScrollPane;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTable;
import com.lonerae.nightsintheoutskirts.screens.customUI.CustomTextButton;
import com.lonerae.nightsintheoutskirts.screens.visible.gamescreens.NightResolutionScreen;

public class FourthCivilianNightScreen extends NightScreen {
    public FourthCivilianNightScreen(Game game) {
        super(game);
    }

    @Override
    public void show() {
        super.show();

        Table mainTable = new CustomTable(true);

        Label title = new CustomLabel("Night", getTitleStyle());
        UIUtil.title(title);
        mainTable.add(title).padBottom(PAD_VERTICAL_SMALL).row();

        Label description = new CustomLabel(GameData.getRoleStrings().get("fourthCivilianNight"), getBlackStyle());
        mainTable.add(description).width(DEFAULT_ACTOR_WIDTH).padBottom(PAD_VERTICAL_BIG).row();

        TextButton activateButton = new CustomTextButton(getStrings().get("abilityUsed"), getButtonStyle());
        activateButton.setTouchable(Touchable.disabled);
        TextButton continueButton = new CustomTextButton(getStrings().get("endNight"), getButtonStyle());

        if (Player.getPlayer().isAbleToUseAbility()) {
            updateButtons(activateButton, continueButton);
        }

        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ProceedRequest request = new ProceedRequest();
                waitForOtherPlayers(request, ProceedType.ABILITY, new NightResolutionScreen(getGame()));
            }
        });

        Table buttonTable = new Table(getSkin());
        buttonTable.add(activateButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL);
        buttonTable.add(continueButton).width(2 * WIDTH / 5).pad(PAD_HORIZONTAL_SMALL);

        mainTable.add(buttonTable);

        ScrollPane scroll = new CustomScrollPane(mainTable, true);
        getStage().addActor(scroll);
    }

    private void updateButtons(TextButton activateButton, TextButton continueButton) {
        activateButton.setText(getStrings().get("abilityReady"));
        activateButton.setTouchable(Touchable.enabled);
        continueButton.setText(getStrings().get("skipChoice"));
        activateButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                activateAbility(activateButton, continueButton);
            }
        });
    }

    private void activateAbility(TextButton activateButton, TextButton continueButton) {
        FourthRequest request = new FourthRequest();
        request.playerName = Player.getPlayer().getName();
        MatchClient.getMatchClientInstance().getClient().sendTCP(request);

        activateButton.setTouchable(Touchable.disabled);
        activateButton.setText(getStrings().get("abilityUsed"));
        continueButton.setText(getStrings().get("endNight"));
        Player.getPlayer().setAbleToUseAbility(false);
    }
}
