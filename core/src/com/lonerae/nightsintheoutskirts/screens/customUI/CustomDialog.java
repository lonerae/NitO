package com.lonerae.nightsintheoutskirts.screens.customUI;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DEFAULT_POPUP_SIZE;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.DIALOG_VERTICAL_PAD;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.PAD_HORIZONTAL_SMALL;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.PAD_VERTICAL_SMALL;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomDialog extends Dialog {

    public CustomDialog(String title, Skin skin) {
        this(title, skin, false);
    }

    public CustomDialog(String title, Skin skin, boolean canHide) {
        super(title, skin);
        this.getTitleTable().padTop(DIALOG_VERTICAL_PAD).padLeft(PAD_HORIZONTAL_SMALL);
        this.getContentTable().defaults().width(DEFAULT_POPUP_SIZE).padTop(DIALOG_VERTICAL_PAD).padLeft(PAD_HORIZONTAL_SMALL);
        this.getButtonTable().defaults().width(WIDTH / 4).pad(PAD_HORIZONTAL_SMALL).padBottom(PAD_VERTICAL_SMALL / 8);

        if (canHide) {
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
}
