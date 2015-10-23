package brandpost.dev.flashback_2.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import brandpost.dev.flashback_2.R;

/**
 * Created by Viktor on 2015-10-07.
 */
public class HorizontalPagePicker extends LinearLayout {

    private Button mLeftButton;
    private Button mGotoButton;
    private Button mRightButton;

    private LinearLayout mTopLayout;
    private LinearLayout mBottomLayout;

    private int mCurrentPage;

    public HorizontalPagePicker(Context context) {
        super(context);
    }

    public HorizontalPagePicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        doStuff(context);
    }

    public HorizontalPagePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        doStuff(context);
    }

    float lefty;
    float midy;
    float righty;

    private void doStuff(final Context context) {

        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.pagepicker_layout, this, true);



        mLeftButton = (Button)findViewById(R.id.previous);
        mGotoButton = (Button)findViewById(R.id.gotobutton);
        mRightButton = (Button)findViewById(R.id.next);

        View topPage = (TextView)findViewById(R.id.left);
        View midPage = (TextView)findViewById(R.id.mid);
        View bottomPage = (TextView)findViewById(R.id.right);

        lefty = topPage.getY();
        midy = midPage.getY();
        righty = bottomPage.getY();

        mLeftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mGotoButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "Roboto-Light.ttf"));
        mGotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                findViewById(R.id.right).animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .y(lefty)
                        .setDuration(100);

                findViewById(R.id.mid).animate()
                        .scaleX(0.625f)
                        .scaleY(0.625f)
                        .y(righty)
                        .setDuration(100);

                findViewById(R.id.left).animate()
                        .scaleX(1.6f)
                        .scaleY(1.6f)
                        .y(midy)
                        .setDuration(100);
                 */
                Toast.makeText(context, "Fungerar inte än.", Toast.LENGTH_SHORT).show();
            }
        });
        mRightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }
}
