package com.lonerae.nightsintheoutskirts.screens.customUI;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DEFAULT_ACTOR_HEIGHT;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DEFAULT_POPUP_SIZE;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DIALOG_VERTICAL_PAD;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.HEIGHT;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.PAD_HORIZONTAL_SMALL;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.PAD_VERTICAL_SMALL;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;

public class CustomDialog extends Dialog {

    public CustomDialog(String title, String message, Skin skin, Label.LabelStyle style) {
        super(title, skin);
        this.pad(150, 25, 25, 25);
        this.getTitleLabel().setAlignment(Align.center);
        this.getTitleLabel().setStyle(style);
        this.getContentTable().defaults().width(DEFAULT_POPUP_SIZE);
        Label messageLabel = new CustomLabel(message, style);
        this.getContentTable().add(messageLabel).width(DEFAULT_POPUP_SIZE);
        this.getButtonTable().defaults().width(WIDTH / 3).pad(25);
    }

    public void isHideable() {
        this.addListener(new InputListener() {
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()){
                    hide();
                }
                return true;
            }
        });
    }
}
