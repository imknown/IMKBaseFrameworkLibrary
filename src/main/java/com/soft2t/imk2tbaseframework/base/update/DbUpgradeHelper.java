package com.soft2t.imk2tbaseframework.base.update;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.soft2t.imk2tbaseframework.util.LogUtil;
import com.soft2t.imk2tbaseframework.util.file.DatabaseUtil;

/**
 * Created by imknown on 2016/3/9.
 */
public class DbUpgradeHelper {
    private DatabaseUtil.DbUpgradeCallback dbUpgradeCallback;

    public void upgrade(@NonNull DatabaseUtil.DbUpgradeCallback dbUpgradeCallback) {
        this.dbUpgradeCallback = dbUpgradeCallback;

        try {
            new DbUpgradeAsyncTask().execute();
        } catch (Exception e) {
            e.printStackTrace();
            dbUpgradeCallback.onUpgradeFail();
            LogUtil.i("onUpgradeFail");
        }
    }

    private class DbUpgradeAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            dbUpgradeCallback.onUpgradeBegin();
            LogUtil.i("onUpgradeBegin");
        }

        @Override
        protected Void doInBackground(Void... params) {
            dbUpgradeCallback.onUpgrading();
            LogUtil.i("onUpgrading");

            return null;
        }

        @Override
        protected void onPostExecute(Void arg) {
            dbUpgradeCallback.onUpgradeSuccess();
            LogUtil.i("onUpgradeSuccess");
        }
    }
}
