package com.example.locker.util;

import androidx.annotation.IntDef;

@IntDef({LockerState.DRAW_FIRST, LockerState.DRAW_FIRST_DONE, LockerState.DRAW_LAST, LockerState.DRAW_LAST_DONE})
public @interface LockerState {
    int DRAW_FIRST = 1;
    int DRAW_FIRST_DONE = 2;
    int DRAW_LAST = 3;
    int DRAW_LAST_DONE = 4;

}
