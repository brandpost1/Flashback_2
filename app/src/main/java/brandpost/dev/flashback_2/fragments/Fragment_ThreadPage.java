package brandpost.dev.flashback_2.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import brandpost.dev.flashback_2.R;
import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.misc.DocumentFetcher;
import brandpost.dev.flashback_2.misc.ThreadParser;

/**
 * Created by Viktor on 2014-12-12.
 */
public class Fragment_ThreadPage extends Fragment {

    private LinearLayout mCardContainer;
    private ProgressBar mProgressBar;
    private MyScrollView scroll;
	private MyScrollView.HeaderFooterProvider mCallback;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if(savedInstanceState == null) {

			DocumentFetcher.DocumentCallback<ThreadParser.ThreadPage> callback = new DocumentFetcher.DocumentCallback<ThreadParser.ThreadPage>() {
				@Override
				public void onDocumentFetched(ThreadParser.ThreadPage document) {
                    if(getActivity() != null) {
                        mProgressBar.setVisibility(View.GONE);
                        for (ThreadParser.Post post : document.threadPosts) {
                            try {
                                addPost(post);
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
				}
			};

            Bundle activityBundle = getArguments();

            String baseUrl = "https://www.flashback.org/showthread.php?";
            String threadId = "t=" + "1452366";
            String postsPerPage = "&pp=" + "12";
            String currentPage = "&page=" + "3";

			DocumentFetcher fetcher = new DocumentFetcher(callback, ThreadParser.class);
			fetcher.execute(baseUrl + threadId + postsPerPage + currentPage);
		}
	}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_threadpage, container, false);
        mCardContainer = (LinearLayout)root.findViewById(R.id.card_container);
        mProgressBar = (ProgressBar)root.findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.VISIBLE);
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

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private View addPost(final ThreadParser.Post post) throws ParserConfigurationException, SAXException, IOException {
        final View postContainer = getActivity().getLayoutInflater().inflate(R.layout.post_container, mCardContainer, false);
        final View postContent = postContainer.findViewById(R.id.post_content);

        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxParserFactory.newSAXParser();

        DefaultHandler handler = new DefaultHandler() {
            int depth = 0;
            View parent;
            Stack<View> mViews = new Stack<>();
            Stack<String> mTypes = new Stack<>();

            @Override
            public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

                switch (localName) {
                    case "quote":
                        parent = getActivity().getLayoutInflater().inflate(R.layout.post_root_quote, (LinearLayout)postContent, false);
                        mViews.push(parent);
                        mTypes.push("quote");
                        break;
                    case "quoter_name":
                        parent = getActivity().getLayoutInflater().inflate(R.layout.post_quote_name, (LinearLayout)postContent, false);
                        mViews.push(parent);
                        mTypes.push("quoter_name");
                        break;
                    case "spoiler":
                        if(depth % 2 == 0) {
                            parent = getActivity().getLayoutInflater().inflate(R.layout.post_root_spoiler_even, (LinearLayout) postContent, false);
                        } else {
                            parent = getActivity().getLayoutInflater().inflate(R.layout.post_root_spoiler_odd, (LinearLayout) postContent, false);
                        }
                        mViews.push(parent);
                        mTypes.push("spoiler");
                        break;
                }
                depth++;
            }

            @Override
            public void endElement(String uri, String localName, String qName) throws SAXException {

                if(depth > 1) {

                    final View v = mViews.pop();
                    mTypes.pop();

                    if(localName.equals("spoiler")) {
                        final int childCount = ((LinearLayout) v.findViewById(R.id.container)).getChildCount();
                        View spoilerButtonContainer = null;
                        for (int i = 0; i < ((LinearLayout)v).getChildCount(); i++) {
                            if(((LinearLayout) v).getChildAt(i).getId() == R.id.spoilerButton_container) {
                              spoilerButtonContainer = ((LinearLayout) v).getChildAt(i);
                            }
                        }

                        View spoilerButton = spoilerButtonContainer.findViewById(R.id.spoilerButton);
                        spoilerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                View container = null;
                                // Findviewbyid did not get the correct view due to the nesting, so had to locate it manually instead.
                                // Refer to view hierarchy of the spoiler layout for details
                                for (int i = 0; i < ((LinearLayout) v).getChildCount(); i++) {
                                    if(((LinearLayout) v).getChildAt(i).getId() == R.id.spoiler_outer) {
                                        container = ((LinearLayout)((LinearLayout) v).getChildAt(i)).getChildAt(1);
                                    }
                                }
                                for (int i = 0; i < ((LinearLayout) container).getChildCount(); i++) {
                                    if(((Button)view).getText().equals("Visa")) {
                                        ((LinearLayout) container).getChildAt(i).setVisibility(View.VISIBLE);
                                    } else if (((Button)view).getText().equals("Dölj")){
                                        ((LinearLayout) container).getChildAt(i).setVisibility(View.GONE);
                                    }
                                }
                                if(((Button)view).getText().equals("Visa")) {
                                    ((Button) view).setText("Dölj");
                                } else if (((Button)view).getText().equals("Dölj")){
                                    ((Button) view).setText("Visa");
                                }

                            }
                        });

                        for (int i = 0; i < childCount; i++) {
                            ((LinearLayout) v.findViewById(R.id.container)).getChildAt(i).setVisibility(View.GONE);
                        }
                    }

                    if(mViews.empty()) {
                        ((ViewGroup)postContent).addView(v);
                    } else {
                        ((ViewGroup) mViews.peek().findViewById(R.id.container)).addView(v);
                    }
                }

                depth--;
            }

            @Override
            public void characters(char[] ch, int start, int length) throws SAXException {
                // Textnode
                String s = String.copyValueOf(ch, start, length).trim();

                if(length > 1 && !s.isEmpty()) {
                    int layout = R.layout.post_root_textnode;

                    if(depth > 1) {
                        switch (mTypes.peek()) {
                            case "quoter_name":
                                layout = R.layout.post_root_quotername;
                                break;
                            case "spoiler":
                                layout = R.layout.post_root_textnode_nomargin;
                        }
                    }
                    // Replace {amp} with & in links since the xml parser broke the links into separate chunks at those signs
                    s = s.replace("{amp}", "&");

                    TextView v = (TextView)getActivity().getLayoutInflater().inflate(layout, (LinearLayout)postContent, false);
                    v.setText(s);

                    if(depth == 1) {
                        // Add to postcontainer
                        ((LinearLayout) postContent).addView(v);
                    } else if(depth > 1) {
                        // Add to parent
                        ((ViewGroup)mViews.peek().findViewById(R.id.container)).addView(v);
                    }
                }
            }
        };

        saxParser.parse(new ByteArrayInputStream(post.postData.getBytes(StandardCharsets.UTF_8)), handler);

        ((TextView) postContainer.findViewById(R.id.post_author)).setText(post.postAuthor);
        ((TextView)postContainer.findViewById(R.id.post_membertype)).setText(post.postUserType);
        ((TextView)postContainer.findViewById(R.id.post_date_time)).setText(post.postDateTime);



        mCardContainer.addView(postContainer);
        return postContainer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
	    try {
		    mCallback = (MyScrollView.HeaderFooterProvider)activity;
	    } catch (ClassCastException e) {
		    System.out.println("Activity must implement the HeaderFooterProvider-interface.");
	    }
	}
}
