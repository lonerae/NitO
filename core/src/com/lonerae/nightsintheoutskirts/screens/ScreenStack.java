package com.lonerae.nightsintheoutskirts.screens;

import com.lonerae.nightsintheoutskirts.screens.visible.MenuScreen;

import java.util.LinkedList;

public class ScreenStack {

    private static final LinkedList<BaseScreen> screenStack = new LinkedList<>();

    public static LinkedList<BaseScreen> getScreenStack() {
        return screenStack;
    }

    public static BaseScreen goToPrevious() {
        if (!(screenStack.peekLast().getClass().equals(MenuScreen.class))) {
            screenStack.removeLast();
            return screenStack.peekLast();
        }
        return null;
    }
}
