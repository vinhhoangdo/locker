package com.example.locker.util;

import androidx.annotation.IntDef;

@IntDef({DrawState.DRAW_FIRST, DrawState.DRAW_FIRST_DONE, DrawState.DRAW_LAST, DrawState.DRAW_LAST_DONE})
public @interface DrawState {
    int DRAW_FIRST = 1;
    int DRAW_FIRST_DONE = 2;
    int DRAW_LAST = 3;
    int DRAW_LAST_DONE = 4;

}
