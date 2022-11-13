package com.lonerae.nightsintheoutskirts.screens.customUI;

import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.PAD_VERTICAL_SMALL;
import static com.lonerae.nightsintheoutskirts.screens.BaseScreen.WIDTH;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class CustomTable extends Table {

    public CustomTable(boolean isMain) {
        super();
        if (isMain) {
            this.defaults().width(WIDTH);
            this.top().padTop(PAD_VERTICAL_SMALL);
        }
    }
}
