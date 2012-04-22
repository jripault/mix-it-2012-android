package fr.mixit.android.ui.adapters;

import android.accounts.Account;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import fr.mixit.android.R;

public class AccountAdapter extends BaseAdapter {

	LayoutInflater inflater;
	Account[] accounts;

	class AccountHolder {
		TextView name;
	}

	public AccountAdapter(Account[] a, Context ctx) {
		super();

		accounts = a;
		inflater = LayoutInflater.from(ctx);
	}

	@Override
	public int getCount() {
		return accounts != null ? accounts.length : 0;
	}

	@Override
	public Account getItem(int position) {
		return accounts != null ? accounts[position] : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AccountHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_account, parent, false);
			holder = new AccountHolder();
			holder.name = (TextView) convertView.findViewById(R.id.item_account_name);
			convertView.setTag(holder);
		} else {
			holder = (AccountHolder) convertView.getTag();
		}
		
		holder.name.setText(accounts[position].toString());
		
		return convertView;
	}
}
