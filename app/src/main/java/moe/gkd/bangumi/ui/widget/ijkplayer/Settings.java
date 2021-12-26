package moe.gkd.bangumi.ui.widget.ijkplayer;

import android.content.Context;

public class Settings {
    private Context mAppContext;

    public static final int PV_PLAYER__Auto = 0;
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;

    public Settings(Context context) {
        mAppContext = context.getApplicationContext();
    }

    public int getPlayer() {
        return PV_PLAYER__IjkMediaPlayer;
    }

    public boolean getUsingMediaCodec() {
        return true;
    }

    public boolean getUsingMediaCodecAutoRotate() {
        return false;
    }

    public boolean getMediaCodecHandleResolutionChange() {
        return false;
    }

    public boolean getUsingOpenSLES() {
        return false;
    }

    public String getPixelFormat() {
        return "";
    }

    public boolean getEnableNoView() {
        return false;
    }

    public boolean getEnableSurfaceView() {
        return false;
    }

    public boolean getEnableTextureView() {
        return true;
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        return true;
    }

    public boolean getUsingMediaDataSource() {
        return false;
    }

}