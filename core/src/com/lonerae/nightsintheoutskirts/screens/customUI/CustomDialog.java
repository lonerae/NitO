package com.lonerae.nightsintheoutskirts.screens.customUI;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DEFAULT_POPUP_SIZE;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

public class CustomDialog extends Dialog {

    public CustomDialog(String title, String message, Skin skin, Label.LabelStyle style) {
        this(title, message, skin, style, true);
    }

    public CustomDialog(String title, String message, Skin skin, Label.LabelStyle style, boolean justified) {
        super(title, skin);
        this.pad(150, 25, 25, 25);
        this.getTitleLabel().setAlignment(Align.center);
        this.getTitleLabel().setStyle(style);
        this.getContentTable().defaults().width(DEFAULT_POPUP_SIZE);
        Label messageLabel;
        if (justified) {
            messageLabel = new CustomLabel(message, style);
        } else {
            messageLabel = new Label(message, style);
            messageLabel.setAlignment(Align.center);
        }
        this.getContentTable().add(messageLabel).width(DEFAULT_POPUP_SIZE);
        this.getButtonTable().defaults().width(WIDTH / 3).pad(25);
    }

    public CustomDialog(Skin skin, Label.LabelStyle style) {
        super("", skin);
        this.getContentTable().defaults().width(DEFAULT_POPUP_SIZE);
        Label messageLabel = new CustomLabel("", style);
        this.getContentTable().add(messageLabel).width(DEFAULT_POPUP_SIZE);
    }

    public void isHideable() {
        this.addListener(new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (x < 0 || x > getWidth() || y < 0 || y > getHeight()) {
                    hide();
                }
                return true;
            }
        });
    }
}
