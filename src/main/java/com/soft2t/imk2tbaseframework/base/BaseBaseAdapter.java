package com.soft2t.imk2tbaseframework.base;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.lidroid.xutils.ViewUtils;

public abstract class BaseBaseAdapter<DataType, HolderType> extends BaseAdapter {

	// private final String TAG = BaseBaseAdapter.class.getSimpleName();

	private List<DataType> list;

	private LayoutInflater inflater;

	private Context context;

	private int layoutResId;

	public BaseBaseAdapter(Context context, List<DataType> list, int layoutResId) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.layoutResId = layoutResId;

		if (list == null) {
			list = new ArrayList<DataType>();
		}
		this.list = list;
	}

	public Context getContext() {
		return context;
	}

	public List<DataType> getList() {
		return this.list;
	}

	public void setItem(List<DataType> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}

	public void addItem(DataType dt) {
		if (this.list != null) {
			this.list.add(dt);
			this.notifyDataSetChanged();
		}
	}

	public void addItem(List<DataType> list) {
		if (this.list != null) {
			this.list.addAll(list);
			this.notifyDataSetChanged();
		}
	}

	public void addItemToPosition(int position, DataType dt) {
		if (this.list != null) {
			this.list.add(position, dt);
			this.notifyDataSetChanged();
		}
	}

	public void addItemToPosition(int position, List<DataType> list) {
		if (this.list != null) {
			this.list.addAll(position, list);
			this.notifyDataSetChanged();
		}
	}

	public void addItemToFirst(DataType dt) {
		// if (this.list != null) {
		// this.list.add(0, dt);
		// this.notifyDataSetChanged();
		// }

		addItemToPosition(0, dt);
	}

	public void addItemToFirst(List<DataType> list) {
		// if (this.list != null) {
		// this.list.addAll(0, list);
		// this.notifyDataSetChanged();
		// }

		addItemToPosition(0, list);
	}

	public void removeItem(int position) {
		if (position < list.size()) {
			DataType model = list.get(position);
			removeItem(model);
		}
	}

	public void removeItem(DataType model) {
		if (this.list.contains(model)) {
			this.list.remove(model);
			this.notifyDataSetChanged();
		}
	}

	public void removeAllItem() {
		if (this.list != null) {
			this.list.clear();
			this.notifyDataSetChanged();
		}
	}

	@Override
	public int getCount() {
		if (list != null) {
			return list.size();
		} else {
			return 0;
		}
	}

	@Override
	public DataType getItem(int position) {
		if (position < list.size()) {
			return list.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	protected DataType getCurrentData() {
		return currentData;
	}

	private DataType currentData = null;

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HolderType holder = null;

		currentData = getItem(position);

		if (convertView == null) {
			// convertView = inflater.inflate(viewCallback.getViewId(), null);
			convertView = inflater.inflate(layoutResId, null);

			holder = viewCallback.getViewHolder(convertView, position);

			ViewUtils.inject(holder, convertView);

			convertView.setTag(holder);
		} else {
			holder = (HolderType) convertView.getTag();
		}

		viewCallback.initView(holder, position);
		return convertView;
	}

	protected ViewCallback viewCallback;

	public abstract class ViewCallback {
		// protected abstract int getViewId();

		protected abstract HolderType getViewHolder(View view, int position);

		protected abstract void initView(HolderType holder, int position);
	}
}
