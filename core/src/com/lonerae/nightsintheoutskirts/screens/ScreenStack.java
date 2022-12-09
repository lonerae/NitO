package com.lonerae.nightsintheoutskirts.screens;

import com.badlogic.gdx.Gdx;
import com.lonerae.nightsintheoutskirts.screens.visible.MenuScreen;

import java.util.LinkedList;

public class ScreenStack {

    private static final LinkedList<BaseScreen> screenStack = new LinkedList<>();

    public static LinkedList<BaseScreen> getScreenStack() {
        return screenStack;
    }

    public static void clearStack() {
        screenStack.clear();
    }

    public static BaseScreen findPrevious() {
        if (!(screenStack.peekLast() instanceof MenuScreen)) {
            screenStack.removeLast();
            return screenStack.peekLast();
        }
        return null;
    }
}
