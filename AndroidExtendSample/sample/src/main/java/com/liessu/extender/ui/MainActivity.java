package com.liessu.extender.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.liessu.andex.sharedmulti.SharedPreferencesProvider;
import com.liessu.andex.span.ClickableSpanEx;
import com.liessu.extender.DemoApplication;
import com.liessu.extender.R;
import com.liessu.extender.StringUtil;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            final EditText editText = (EditText) rootView.findViewById(R.id.value_edit);
            Button btnGet = (Button) rootView.findViewById(R.id.get_value);
            Button btnSet = (Button) rootView.findViewById(R.id.set_value);
            btnGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //server端APP，建议直接使用SharedPreferences设置与调用即可
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                            SharedPreferencesProvider.SHARED_FILE_NAME, Context.MODE_PRIVATE);
                    String loglevel = sharedPreferences.getString("logLevel", "3");
                    Snackbar.make(view, "The local Loglevel is " + loglevel, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            btnSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = editText.getText().toString();
                    //server端APP，建议直接使用SharedPreferences设置与调用即可
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences(
                            SharedPreferencesProvider.SHARED_FILE_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putString("logLevel", value).apply();
                }
            });

            SpannableString styledString
                    = new SpannableString("Large\n\n"     // index 0 - 5
                    + "Bold\n\n"          // index 7 - 11
                    + "Underlined\n\n"    // index 13 - 23
                    + "Italic\n\n"        // index 25 - 31
                    + "Strikethrough\n\n" // index 33 - 46
                    + "Colored\n\n"       // index 48 - 55
                    + "Highlighted\n\n"   // index 57 - 68
                    + "K Superscript\n\n" // "Superscript" index 72 - 83
                    + "K Subscript\n\n"   // "Subscript" index 87 - 96
                    + "Url\n\n"           //  index 98 - 101
                    + "Clickable\n\n");   // index 103 - 112

            // make the text twice as large
            styledString.setSpan(new RelativeSizeSpan(2f), 0, 5, 0);
            // make text bold
            styledString.setSpan(new StyleSpan(Typeface.BOLD), 7, 11, 0);
            // underline text
            styledString.setSpan(new UnderlineSpan(), 13, 23, 0);
            // make text italic
            styledString.setSpan(new StyleSpan(Typeface.ITALIC), 25, 31, 0);
            styledString.setSpan(new StrikethroughSpan(), 33, 46, 0);
            // change text color
            styledString.setSpan(new ForegroundColorSpan(Color.GREEN), 48, 55, 0);
            // highlight text
            styledString.setSpan(new BackgroundColorSpan(Color.CYAN), 57, 68, 0);
            // superscript
            styledString.setSpan(new SuperscriptSpan(), 72, 83, 0);
            // make the superscript text smaller
            styledString.setSpan(new RelativeSizeSpan(0.5f), 72, 83, 0);
            // subscript
            styledString.setSpan(new SubscriptSpan(), 87, 96, 0);
            // make the subscript text smaller
            styledString.setSpan(new RelativeSizeSpan(0.5f), 87, 96, 0);
            // url
            styledString.setSpan(new URLSpan("http://www.google.com"), 98, 101, 0);
            // clickable text
            ClickableSpan clickableSpan = new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    // We display a Toast. You could do anything you want here.
                    Toast.makeText(getActivity(), "Clicked", Toast.LENGTH_SHORT).show();

                }
            };
            styledString.setSpan(clickableSpan, 103, 112, 0);

            // this step is mandated for the url and clickable styles.
            textView.setMovementMethod(LinkMovementMethod.getInstance());

            // make it neat
            textView.setGravity(Gravity.LEFT);
            textView.setTextSize(14);
//            textView.setBackgroundColor(Color.WHITE);

            textView.setOnTouchListener(new ClickableSpanEx.Selector(true));
            textView.setText(StringUtil.getExample());
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(DemoApplication.getContext(), "My time - OnClickListener", Toast.LENGTH_SHORT).show();
                }
            });

            return rootView;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
