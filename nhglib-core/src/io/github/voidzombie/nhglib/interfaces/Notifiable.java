package io.github.voidzombie.nhglib.interfaces;

import io.github.voidzombie.nhglib.utils.data.Bundle;

/**
 * Created by Fausto Napoli on 25/10/2016.
 */
public interface Notifiable {
    void onNotify(Bundle bundle);
}
