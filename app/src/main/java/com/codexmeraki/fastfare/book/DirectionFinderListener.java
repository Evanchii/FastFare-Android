package com.codexmeraki.fastfare.book;

import java.util.List;

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
