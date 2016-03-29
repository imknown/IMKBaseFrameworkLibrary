package com.soft2t.imk2tbaseframework.util.file;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;

import com.soft2t.imk2tbaseframework.base.Constant;
import com.soft2t.imk2tbaseframework.util.LogUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * // ① 准备 SQLDiff<br>
 * 假设 "X-2" 为 上一版数据库版本(当前非第一版, 才存在), "X-1" 为当前数据库版本, "X" 为 数据库下一版本, "X" 为 正整数 且 大于 "1".<br>
 * 数据库"DB(X-1)" 升级到数据库"DBX", 然后手动编写 差异语句 "SQLDiff".<br>
 * 建议复制 "DB(X-1)" 成 副本 "DB(X-1)copy", 然后用 "GUI Tool" 通过 "SQLDiff" 修改副本 "DB(X-1)copy" 成 "DBX" 的 样子.<br>
 * 看最终的 表结构 对不对.<br>
 * <br>
 * <br>
 * // ② 升级 DB(X-1) <br>
 * 使用 ① 中的 "SQLDiff" 拿到 "DB(X-1)" 中去执行, 即可得到 "DBX", 注意需要先 获取锁定.<br>
 * 用 "SQLDiff" 修改 "MyDbUpgradeHelper.doUpgrading(oldVersion, newVersion);" 中 对应的版本方法 "upgradeToVersionX();".<br>
 * 例如: db.execSQL("SQLDiff"); 注意, 代码中的 for 可以实线迭代升级, 避免 用户跨数据库版本升级 导致数据丢失, 例如 从 "DB(X-2)" 升级到 "DBX",<br>
 * 具体逻辑 参看 DatabaseUtil.DbUpgradeCallback 接口. 注意 线程的切换.<br>
 * <br>
 * 最后, 直接用修改表 "DB(X-1).ZZ_TEST_FOR_CHECK_ONLY", 修改DB版本号 "db_version" 为 "DB_VERSION_X",<br>
 * 同时 修改 代码 "MyBaseApplication.initConstant().Constant.Db.DB_VERSION = DB_VERSION_X"; // T.O.D.O 数据库有更改, 需要修改这里<br>
 * <br>
 * 保存数据库, 提交, 即可 完成数据库升级.<br>
 */
public class DatabaseUtil {

    private static final String FOO_COLUMN = "foo";
    private static final String FOO_COLUMN_CONTENT = "dummy";
    private static final String DB_VERSION_COLUMN = "db_version";
    /**
     * 数据库在手机里的路径
     */
    public static String DATABASE_PATH;
    /**
     * 数据库的名字
     */
    public static String DB_NAME;
    /**
     * 当前数据库版本
     */
    public static int DB_VERSION;

    /**
     * 判断数据库是否存在
     *
     * @return 存在=true, 不存在=false
     */
    public static boolean checkDatabase() {
        boolean exists = false;

        SQLiteDatabase db = null;
        Cursor cursorContent = null;

        try {
            String databaseFilename = DATABASE_PATH + DB_NAME;

            File dbFile = new File(databaseFilename);

            if (dbFile.exists()) {
                db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);

                cursorContent = db.rawQuery("select " + FOO_COLUMN + " from " + Constant.Db.EXISTS_FOR_CHECKING, null);

                // 只有一条, 注意 是一个 &, 为了保证 两个 判断 都进行
                if (cursorContent.getCount() == 1 & cursorContent.moveToFirst()) {
                    String temp = cursorContent.getString(cursorContent.getColumnIndex(FOO_COLUMN));

                    if (FOO_COLUMN_CONTENT.equals(temp)) {
                        exists = true;
                    }
                }
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursorContent != null && !cursorContent.isClosed()) {
                cursorContent.close();
            }

            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return exists;
    }

    public static void smartDo(Context context, @NonNull SmartDatabaseCallback smartDatabaseCallback) {
        if (checkDatabase()) {
            int oldVersion = DatabaseUtil.getDatabaseVersion();
            int newVersion = Constant.Db.DB_VERSION;

            if (oldVersion < newVersion) {
                smartDatabaseCallback.onUpgrade(oldVersion, newVersion);
                LogUtil.i("onUpgrade, from " + oldVersion + " to " + newVersion);
            } else if (oldVersion == newVersion) {
                smartDatabaseCallback.onGradeNoChange(oldVersion);
                LogUtil.i("onGradeNoChange, from " + oldVersion + " to " + newVersion);
            }
        } else {
            new CopyDbAsyncTask(context, smartDatabaseCallback).execute();
        }
    }

    /**
     * @return [-1] 数据库中没有找到版本号
     */
    public static int getDatabaseVersion() {
        int dbVersion = -1;

        SQLiteDatabase db = null;
        Cursor cursorContent = null;

        try {
            String databaseFilename = DATABASE_PATH + DB_NAME;

            db = SQLiteDatabase.openDatabase(databaseFilename, null, SQLiteDatabase.OPEN_READWRITE);

            cursorContent = db.rawQuery("select " + DB_VERSION_COLUMN + " from " + Constant.Db.EXISTS_FOR_CHECKING, null);

            // 只有一条, 注意 是一个 &, 为了保证 两个 判断 都进行
            if (cursorContent.getCount() == 1 & cursorContent.moveToFirst()) {
                dbVersion = cursorContent.getInt(cursorContent.getColumnIndex(DB_VERSION_COLUMN));
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursorContent != null && !cursorContent.isClosed()) {
                cursorContent.close();
            }

            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return dbVersion;
    }

    public interface SmartDatabaseCallback {
        @UiThread
        void onFirstCopyBegin();

        @UiThread
        void onFirstCopySuccess();

        @UiThread
        void onFirstCopyFail();

        @UiThread
        void onUpgrade(int oldVersion, int newVersion);

        @UiThread
        void onGradeNoChange(int oldVersion);
    }

    public interface DbUpgradeCallback {
        @UiThread
        void onUpgradeBegin();

        @WorkerThread
        void onUpgrading();

        @UiThread
        void onUpgradeSuccess();

        @UiThread
        void onUpgradeFail();
    }

    /**
     * 复制数据库到手机指定文件夹下
     */
    private static class CopyDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private SmartDatabaseCallback smartDatabaseCallback;
        private Context context;

        public CopyDbAsyncTask(Context context, SmartDatabaseCallback smartDatabaseCallback) {
            this.smartDatabaseCallback = smartDatabaseCallback;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            smartDatabaseCallback.onFirstCopyBegin();
            LogUtil.i("onFirstCopyBegin");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                File dir = new File(DATABASE_PATH);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                String databaseFilenames = DATABASE_PATH + DB_NAME;

                FileOutputStream os = new FileOutputStream(databaseFilenames);// 得到数据库文件的写入流
                InputStream is = FileUtil.getInputStream(context, "db" + File.separator + DB_NAME);// 得到数据库文件的数据流

                byte[] buffer = new byte[20 * 1024];

                int count;

                while ((count = is.read(buffer)) > 0) {
                    os.write(buffer, 0, count);
                    os.flush();
                }

                is.close();
                os.close();
            } catch (IOException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        smartDatabaseCallback.onFirstCopyFail();
                        LogUtil.e("onFirstCopyFail");
                    }
                });

                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            smartDatabaseCallback.onFirstCopySuccess();
            LogUtil.i("onFirstCopySuccess");
        }
    }
}
