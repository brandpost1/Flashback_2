package brandpost.dev.flashback_2.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import brandpost.dev.flashback_2.ForumsActivity;
import brandpost.dev.flashback_2.R;

/**
 * Created by Viktor on 2014-11-01.
 */
public class Fragment_Categories extends ListFragment {

    private MainAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
	    // Set title
	    ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");


        return view;
    }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("Categories", (Serializable)mAdapter.getData());
	}

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

	    Bundle args = new Bundle();
	    args.putString("Url", mAdapter.getData().get(position).categoryUrl);
	    args.putString("Name", mAdapter.getData().get(position).categoryName);
	    args.putString("Id", mAdapter.getData().get(position).categoryId);
	    args.putString("Color", mAdapter.getData().get(position).categoryColor);

	    Intent intent = new Intent(getActivity(), ForumsActivity.class);
	    intent.putExtras(args);
	    startActivity(intent);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    mAdapter = new MainAdapter(getActivity());
	    setListAdapter(mAdapter);

        if(savedInstanceState == null) {

            InputStream xmlIn;
            Document document = null;

            ArrayList<CategoryRow> xmlData = new ArrayList<CategoryRow>();
            try {
                xmlIn = getActivity().getAssets().open("categories_xml.xml");
                document = Jsoup.parse(xmlIn, "UTF-8", "");
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(document != null) {
                Elements categories = document.select("category");
                for(Element e : categories) {
                    CategoryRow row = new CategoryRow();
                    row.categoryColor   = e.select("color").text();
                    row.categoryName    = e.select("category_name").text();
                    row.categoryId      = e.select("category_id").text();
                    row.categoryUrl     = e.select("category_link").text();

                    xmlData.add(row);
                }

                mAdapter.setData(xmlData);
            }
        } else {
	        if(mAdapter != null)
		        mAdapter.setData((ArrayList<CategoryRow>) savedInstanceState.getSerializable("Categories"));
        }
    }

    private static class CategoryRow implements Serializable {
        public String categoryId;
        public String categoryName;
        public String categoryUrl;
        public String categoryColor;

        public CategoryRow() {

        }
    }

    private class MainAdapter extends BaseAdapter {

        private List<CategoryRow> mData;
        private Context mContext;

        public MainAdapter(Context context) {
            mContext = context;
            mData = new ArrayList<CategoryRow>();
        }

	    public List<CategoryRow> getData() {
		    return mData;
	    }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int i) {
            return mData.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            TextView categoryText;
            ImageView color;

            if(view == null) {
                view = ((Activity)mContext).getLayoutInflater().inflate(R.layout.category_item, null);
            }

            // Set row color
            String colorstring = mData.get(i).categoryColor;

            int clr = Color.parseColor(colorstring);
            color = (ImageView)view.findViewById(R.id.category_color);
            color.setBackgroundColor(clr);

            // Set row text
            categoryText = (TextView)view.findViewById(R.id.category_text);
            categoryText.setText(mData.get(i).categoryName);

            return view;
        }

        public void setData(ArrayList<CategoryRow> data) {
            mData = data;
            notifyDataSetChanged();
        }
    }
}
