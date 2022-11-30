package com.lonerae.nightsintheoutskirts.screens.customUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class CustomTextButton extends TextButton {

    public CustomTextButton(String text, Skin skin, Label.LabelStyle style) {
        super(text, skin);
        Label buttonLabel = new CustomLabel(text, style);
        buttonLabel.setAlignment(Align.center);
        this.setLabel(buttonLabel);
    }
}
