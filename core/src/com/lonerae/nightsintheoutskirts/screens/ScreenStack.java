package com.lonerae.nightsintheoutskirts.screens;

import com.badlogic.gdx.Gdx;
import com.lonerae.nightsintheoutskirts.screens.visible.MenuScreen;

import java.util.LinkedList;

public class ScreenStack {

    private static final LinkedList<BaseScreen> screenStack = new LinkedList<>();

    public static LinkedList<BaseScreen> getScreenStack() {
        return screenStack;
    }

    public static BaseScreen findPrevious() {
        if (!(screenStack.peekLast() instanceof MenuScreen)) {
            Gdx.app.log("STACK: ", screenStack.toString());
            screenStack.removeLast();
            Gdx.app.log("RETURN: ", screenStack.peekLast().toString());
            return screenStack.peekLast();
        }
        return null;
    }
}
