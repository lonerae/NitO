package com.lonerae.nightsintheoutskirts.screens.customUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

public class CustomTextButton extends TextButton {

    public CustomTextButton(String text, TextButtonStyle style) {
        super(text, style);
        style.fontColor = Color.WHITE;
        this.setColor(0.92f, 0.53f, 0.33f, 1);
    }
}
