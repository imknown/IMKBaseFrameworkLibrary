package com.soft2t.imk2tbaseframework.base.debug;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.soft2t.imk2tbaseframework.base.BaseApplication;

public class FloatInject {

    private static WindowManager windowManager;
    private static FloatInject fi;

    private float mTouchX;
    private float mTouchY;

    private float x;
    private float y;

    private float mStartX;
    private float mStartY;

    // private OnClickListener mClickListener;

    // 此windowManagerParams变量为获取的全局变量，用以保存悬浮窗口的属性
    private WindowManager.LayoutParams windowManagerParams;

    private FloatInject() {
    }

    public static FloatInject getInstance() {
        if (fi == null) {
            fi = new FloatInject();
        }

        if (windowManager == null) {
            windowManager = (WindowManager) BaseApplication.mApplicationContext.getSystemService(Context.WINDOW_SERVICE);
        }

        return fi;
    }

    public void bindViews(View floatView) {
        floatView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {
                view.performClick();

                // 获取到状态栏的高度
                Rect frame = new Rect();
                view.getWindowVisibleDisplayFrame(frame);
                int statusBarHeight = frame.top; // statusBarHeight是系统状态栏的高度
                // Log.i("tag", "statusBarHeight:" + statusBarHeight);

                // 获取相对屏幕的坐标，即以屏幕左上角为原点
                x = event.getRawX();
                y = event.getRawY() - statusBarHeight;

                // Log.i("tag", "currX" + x);
                // Log.i("tag", "currY" + y);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // 捕获手指触摸按下动作
                        // 获取相对View的坐标，即以此View左上角为原点
                        mTouchX = event.getX();
                        mTouchY = event.getY();
                        mStartX = x;
                        mStartY = y;
                        // Log.i("tag", "startX" + mTouchX);
                        // Log.i("tag", "startY" + mTouchY);
                        break;

                    case MotionEvent.ACTION_MOVE: // 捕获手指触摸移动动作
                        updateViewPosition(view);
                        break;

                    case MotionEvent.ACTION_UP: // 捕获手指触摸离开动作
                        updateViewPosition(view);

                        mTouchX = mTouchY = 0;

                        if ((x - mStartX) < 5 && (y - mStartY) < 5) {
                            // if (mClickListener != null) {
                            // mClickListener.onClick(view);
                            // }
                        }
                }

                return true;
            }
        });

        windowManagerParams = new WindowManager.LayoutParams();

        // 设置window type
        // windowManagerParams.type = LayoutParams.TYPE_PHONE;
        windowManagerParams.type = LayoutParams.TYPE_SYSTEM_ERROR;

        // 设置图片格式，效果为背景透明
        // windowManagerParams.format = PixelFormat.RGBA_8888;
        windowManagerParams.format = PixelFormat.TRANSLUCENT;

        // 设置Window flag
        // 注意，flag的值可以为： 下面的flags属性的效果形同“锁定”。
        // 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
        // LayoutParams.FLAG_NOT_TOUCH_MODAL 不影响后面的事件
        // LayoutParams.FLAG_NOT_FOCUSABLE 不可聚焦
        // LayoutParams.FLAG_NOT_TOUCHABLE 不可触摸
        windowManagerParams.flags = //
                // Keeps the button presses from going to the background window
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        // Enables the notification to recieve touch events
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        // Draws over status bar
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        // 调整悬浮窗口至左上角，便于调整坐标
        windowManagerParams.gravity = Gravity.LEFT | Gravity.TOP;

        // 以屏幕左上角为原点，设置x、y初始值
        windowManagerParams.x = 0;
        windowManagerParams.y = 0;

        // 设置悬浮窗口长宽数据
        windowManagerParams.width = LayoutParams.WRAP_CONTENT;
        windowManagerParams.height = LayoutParams.WRAP_CONTENT;

        windowManagerParams.windowAnimations = android.R.style.Animation_Toast;
        // windowManagerParams.windowAnimations = android.R.style.Animation_Dialog;

        // 显示myFloatView图像
        windowManager.addView(floatView, windowManagerParams);
    }

    private void updateViewPosition(View view) {
        // 更新浮动窗口位置参数
        windowManagerParams.x = (int) (x - mTouchX);
        windowManagerParams.y = (int) (y - mTouchY);
        windowManager.updateViewLayout(view, windowManagerParams); // 刷新显示
    }

    public void unbindViews(View floatView) {
        try {
            windowManager.removeView(floatView);
        } catch (Exception e) {
            Log.i("tag", "调试窗口已经被关闭了? " + e.getMessage());
        }
    }
}
