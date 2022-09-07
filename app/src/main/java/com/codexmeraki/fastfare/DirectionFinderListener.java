package com.codexmeraki.fastfare;

import java.util.List;

public interface DirectionFinderListener {

    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
