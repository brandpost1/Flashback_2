package brandpost.dev.flashback_2.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import brandpost.dev.flashback_2.R;
import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.misc.DocumentFetcher;
import brandpost.dev.flashback_2.misc.ThreadParser;

/**
 * Created by Viktor on 2014-12-12.
 */
public class Fragment_ThreadPage extends Fragment {

    private LinearLayout mCardContainer;
    private MyScrollView scroll;
	private HeaderFooterProvider mCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null) {

			DocumentFetcher.DocumentCallback<ThreadParser.ThreadPage> callback = new DocumentFetcher.DocumentCallback<ThreadParser.ThreadPage>() {
				@Override
				public void onDocumentFetched(ThreadParser.ThreadPage document) {

				}
			};

			DocumentFetcher fetcher = new DocumentFetcher(callback, ThreadParser.class);
			fetcher.execute("https://www.flashback.org/t341657p459");
		}
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_threadpage, container, false);
        mCardContainer = (LinearLayout)root.findViewById(R.id.card_container);

        return root;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if(scroll != null) {
	            scroll.showHeader(true);
                scroll.showFooter(true);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        scroll = (MyScrollView) view.findViewById(R.id.scroller);

    }

    private View addPost(ThreadParser.Post post) {
        View postContainer = getActivity().getLayoutInflater().inflate(R.layout.post_container, mCardContainer, false);

        mCardContainer.addView(postContainer);
        return postContainer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
	    try {
		    mCallback = (HeaderFooterProvider)activity;
	    } catch (ClassCastException e) {
		    System.out.println("Activity must implement the HeaderFooterProvider-interface.");
	    }
	}

	/**
	 * Interface to implement as to provide the Header and Footer view that this fragment
	 * will hide on scroll.
	 */
	public interface HeaderFooterProvider {
		public View getHeader();
		public ArrayList<View> getFooter();
	}
}
