/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.mixit.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import fr.mixit.android.util.ActivityHelper;

/**
 * A base activity that defers common functionality across app activities to an {@link ActionBarHelper}.
 *
 * NOTE: dynamically marking menu items as invisible/visible is not currently supported.
 */
public abstract class BaseActivity extends FragmentActivity {
	final ActivityHelper mActivityHelper = ActivityHelper.createInstance(this);

    /**
     * Returns the {@link ActionBarHelper} for this activity.
     */
    protected ActivityHelper getActionBarHelper() {
        return mActivityHelper;
    }

    /**{@inheritDoc}*/
    @Override
    public MenuInflater getMenuInflater() {
        return mActivityHelper.getMenuInflater(super.getMenuInflater());
    }

    /**{@inheritDoc}*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityHelper.onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActivityHelper.onPostCreate(savedInstanceState);
    }

    /**
     * Base action bar-aware implementation for
     * {@link Activity#onCreateOptionsMenu(android.view.Menu)}.
     *
     * Note: marking menu items as invisible/visible is not currently supported.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean retValue = false;
        retValue |= mActivityHelper.onCreateOptionsMenu(menu);
        retValue |= super.onCreateOptionsMenu(menu);
        return retValue;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        	mActivityHelper.goHome();
        }
        return super.onOptionsItemSelected(item);
    }
    /**{@inheritDoc}*/
    @Override
    protected void onTitleChanged(CharSequence title, int color) {
    	mActivityHelper.onTitleChanged(title, color);
        super.onTitleChanged(title, color);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyLongPress(keyCode, event) ||
                super.onKeyLongPress(keyCode, event);
    }

/*    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mActivityHelper.onKeyDown(keyCode, event) ||
                super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mActivityHelper.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    /**
     * Takes a given intent and either starts a new activity to handle it (the default behavior),
     * or creates/updates a fragment (in the case of a multi-pane activity) that can handle the
     * intent.
     *
     * Must be called from the main (UI) thread.
     */
    public void openActivityOrFragment(Intent intent) {
        // Default implementation simply calls startActivity
        startActivity(intent);
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }
}
