package io.dev.tanners.bakerhelper.recipe;

public interface MediaCallBack {
    // TODO set callback to release all players on activity onDestory
    // may need to look int omedia controlelr first
    public void releasePlayers();
}
