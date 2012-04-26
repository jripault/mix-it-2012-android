package fr.mixit.android_2012.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import fr.mixit.android_2012.R;
import fr.mixit.android_2012.provider.MixItContract;

public class MembersAdapter extends CursorAdapter {

	ImageLoader mImageLoader;

	LayoutInflater inflater;
	
	DisplayImageOptions mOptions;

	class MemberHolder {
		ImageView image;
		TextView name;
		TextView company;
	}

	public MembersAdapter(Context ctx, ImageLoader imageLoader) {
		super(ctx, null, 0);

		inflater = LayoutInflater.from(ctx);
		mImageLoader = imageLoader;
		
		mOptions = new DisplayImageOptions.Builder()
			.showImageForEmptyUrl(R.drawable.speaker_thumbnail)
			.showStubImage(R.drawable.speaker_thumbnail)
			.cacheInMemory()
			.cacheOnDisc()
//			.decodingType(DecodingType.MEMORY_SAVING)
			.build();
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View v = inflater.inflate(R.layout.item_member, parent, false);
		MemberHolder holder = new MemberHolder();
		holder.image = (ImageView) v.findViewById(R.id.member_image);
		holder.name = (TextView) v.findViewById(R.id.member_name);
		holder.company = (TextView) v.findViewById(R.id.member_company);
		v.setTag(holder);
		return v;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		MemberHolder holder = (MemberHolder) view.getTag();
		
		// TODO : maybe harmonize syntax
		StringBuilder name = new StringBuilder();
		String firstName = cursor.getString(MembersQuery.FIRSTNAME);
		if (!TextUtils.isEmpty(firstName)) {
			name.append(firstName);
			name.append(' ');
		}
		name.append(cursor.getString(MembersQuery.LASTNAME));
		holder.name.setText(name);

		holder.company.setText(cursor.getString(MembersQuery.COMPANY));
		
		String url = cursor.getString(MembersQuery.IMAGE_URL);
		mImageLoader.displayImage(url, holder.image, mOptions);
	}

	public interface MembersQuery {
		String[] PROJECTION = {
				BaseColumns._ID,
				MixItContract.Members.MEMBER_ID,
				MixItContract.Members.FIRSTNAME,
				MixItContract.Members.LASTNAME,
				MixItContract.Members.COMPANY,
				MixItContract.Members.IMAGE_URL};

		int _ID = 0;
		int MEMBER_ID = 1;
		int FIRSTNAME = 2;
		int LASTNAME = 3;
		int COMPANY = 4;
		int IMAGE_URL = 5;
	}

}
