package com.lonerae.nightsintheoutskirts.screens.customUI;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.lonerae.nightsintheoutskirts.screens.UIUtil;

public class CustomLabel extends Label {

    public CustomLabel(CharSequence text, Skin skin) {
        super(UIUtil.justifyText(text.toString()), skin);
    }

    public CustomLabel(CharSequence text, LabelStyle style) {
        super(UIUtil.justifyText(text.toString()), style);
    }

}
