package com.lonerae.nightsintheoutskirts.screens.customUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class CustomLabel extends Label {

    public CustomLabel(CharSequence text, Skin skin) {
        super(text, skin);
        this.setWrap(true);
    }

    public CustomLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
        this.setWrap(true);
    }
}
