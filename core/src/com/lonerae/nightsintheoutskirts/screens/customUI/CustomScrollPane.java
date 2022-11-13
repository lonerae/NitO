package com.lonerae.nightsintheoutskirts.screens.customUI;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

public class CustomScrollPane extends ScrollPane {

    public CustomScrollPane(Actor widget) {
        this(widget,false);
    }

    public CustomScrollPane(Actor widget, boolean isMain) {
        super(widget);
        if (isMain) {
            this.setFillParent(true);
        }
    }
}
