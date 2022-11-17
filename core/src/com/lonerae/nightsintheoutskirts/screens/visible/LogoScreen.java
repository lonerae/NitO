package com.lonerae.nightsintheoutskirts.screens.visible;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.TimeUtils;
import com.lonerae.nightsintheoutskirts.screens.BaseScreen;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;

public class LogoScreen extends BaseScreen {

    private final long startTime = TimeUtils.millis();

    public LogoScreen(Game game) {
        super(game);
    }

    @Override
    public void show () {
        super.show();

        Image logo = new Image(new Texture("data/placeholder.png"));
        UIUtil.center(logo);

        getStage().addActor(logo);
    }

    @Override
    public void render(float delta) {
        super.render(delta);

        if (TimeUtils.timeSinceMillis(startTime) > 2000) {
            getGame().setScreen(new MenuScreen(getGame()));
        }
    }
}
