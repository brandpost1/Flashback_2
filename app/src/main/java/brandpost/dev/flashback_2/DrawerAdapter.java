package brandpost.dev.flashback_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import brandpost.dev.flashback_2.misc.FBHelper;

/**
 * Created by Viktor on 2014-10-30.
 */
public class DrawerAdapter extends BaseAdapter {


    class DrawerItem {
        public DrawerItem(String type, String text) {
            this.type = type;
            this.text = text;
            this.id = -1;
        }
        public DrawerItem(String type, String text, int image, int id) {
            this.type = type;
            this.text = text;
            this.image = image;
            this.id = id;
        }
        public String type = "";
        public String text = "";
        public int image;
        public int id;
    }

    static final int DRAWER_DIVIDER = 0;
    static final int DRAWER_ITEM = 1;

    private LayoutInflater mInflater;
    private ArrayList<DrawerItem> mList;

    public DrawerAdapter(Context context) {

        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList = new ArrayList<>();

        // Since refreshing refuses to work properly

        // Not logged in
        if(!FBHelper.isLoggedIn(context)) {
            mList.add(new DrawerItem("Divider", "NAVIGATION"));
            mList.add(new DrawerItem("Item", "Hem", R.drawable.navigationdrawer_icon_home, 0));
            mList.add(new DrawerItem("Item", "Aktuella ämnen", R.drawable.navigationdrawer_icon_aktuella, 1));
            mList.add(new DrawerItem("Item", "Nya ämnen", -1, 2));
            mList.add(new DrawerItem("Item", "Nya inlägg", -1, 3));
            mList.add(new DrawerItem("Item", "Sök", R.drawable.navigationdrawer_icon_search, 4));
            mList.add(new DrawerItem("Divider", "APP"));
            mList.add(new DrawerItem("Item", "Inställningar", R.drawable.navigationdrawer_icon_manage, 9));
            mList.add(new DrawerItem("Item", "Logga in", R.drawable.navigationdrawer_icon_login, 10));
        }
        // When logged in
        if(FBHelper.isLoggedIn(context)) {
            mList.add(new DrawerItem("Divider", "NAVIGATION"));
            mList.add(new DrawerItem("Item", "Hem", -1, 0));
            mList.add(new DrawerItem("Item", "Aktuella ämnen", -1, 1));
            mList.add(new DrawerItem("Item", "Nya ämnen", -1, 2));
            mList.add(new DrawerItem("Item", "Nya inlägg", -1, 3));
            mList.add(new DrawerItem("Item", "Sök", -1, 4));
            mList.add(new DrawerItem("Divider", "KONTO"));
            mList.add(new DrawerItem("Item", "Mina inlägg", -1, 5));
            mList.add(new DrawerItem("Item", "Mina Ämnen", -1, 6));
            mList.add(new DrawerItem("Item", "Citerade inlägg", -1, 7));
            mList.add(new DrawerItem("Item", "Prenumerationer", -1, 11));
            mList.add(new DrawerItem("Item", "PM", -1, 8));
            mList.add(new DrawerItem("Divider", "APP"));
            mList.add(new DrawerItem("Item", "Inställningar", -1, 9));
            mList.add(new DrawerItem("Item", "Logga ut", -1, 10));
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return 0;
    }

    @Override
    public long getItemId(int i) {
        return mList.get(i).id;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mList.get(position).type.equals("Divider");
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        int type = getItemViewType(i);
        TextView itemText;
        ImageView itemImage;
        TextView dividerText;

        if(view == null) {
            switch (type) {
                case DRAWER_ITEM:
                    view = mInflater.inflate(R.layout.navigationdrawer_list_item, null);
                    break;
                case DRAWER_DIVIDER:
                    view = mInflater.inflate(R.layout.navigationdrawer_list_divider, null);
                    break;
            }
        }

        switch (type) {
            case DRAWER_ITEM:
                itemText = (TextView)view.findViewById(R.id.navigationdrawer_item_text);
                itemImage = (ImageView)view.findViewById(R.id.navigationdrawer_item_image);
                itemText.setText(mList.get(i).text);
                if(mList.get(i).image != -1)
                    itemImage.setImageResource(mList.get(i).image);
                break;
            case DRAWER_DIVIDER:
                dividerText = (TextView)view.findViewById(R.id.navigationdrawer_divider_text);
                dividerText.setText(mList.get(i).text);
                break;
        }
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        DrawerItem item = mList.get(position);
        if(item.type.equals("Divider")) {
            return DRAWER_DIVIDER;
        } else {
            return DRAWER_ITEM;
        }
    }
}