package brandpost.dev.flashback_2.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import brandpost.dev.flashback_2.R;
import brandpost.dev.flashback_2.ThreadActivity;
import brandpost.dev.flashback_2.ThreadsActivity;
import brandpost.dev.flashback_2.customviews.MyScrollView;
import brandpost.dev.flashback_2.misc.CurrentPostsParser;
import brandpost.dev.flashback_2.misc.DocumentFetcher;

/**
 * Created by Viktor on 2015-06-26.
 */
public class Fragment_CurrentThreads extends Fragment {

    private int mCurrentCategory = 0;
    private CurrentPostsParser.CurrentThreads mCurrentThreads;
    private LinearLayout mCurrentThreadsContainer;
    private MyScrollView mScrollView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_currentthreads, container, false);
        final TabLayout tabLayout = (TabLayout)v.findViewById(R.id.tab_layout);
        final ProgressBar progressBar = (ProgressBar)v.findViewById(R.id.progressbar);

        mCurrentThreadsContainer = (LinearLayout)v.findViewById(R.id.content);
        mScrollView = (MyScrollView)v.findViewById(R.id.scroller);

        DocumentFetcher.DocumentCallback callback = new DocumentFetcher.DocumentCallback<CurrentPostsParser.CurrentThreads>() {
            @Override
            public void onDocumentFetched(CurrentPostsParser.CurrentThreads document) {
                progressBar.setVisibility(View.GONE);
                mCurrentThreads = document;

                for (int i = 0; i < document.categories.size(); i++) {
                    String name = document.categories.get(i).name;
                    if(name.contains("vriga") || name.equals("Nyheter")) {
                        tabLayout.addTab(tabLayout.newTab().setText(name));
                    }
                }

                tabLayout.setVisibility(View.VISIBLE);

                for (int i = 0; i < document.categories.get(mCurrentCategory).threads.size(); i++) {
                    addThread(document.categories.get(mCurrentCategory).threads.get(i));
                }
            }
        };
        DocumentFetcher fetcher = new DocumentFetcher(callback, CurrentPostsParser.class);
        fetcher.execute("https://www.flashback.org/aktuella-amnen");

        final Handler tabActionHandler = new Handler();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mCurrentCategory = tab.getPosition();

                tabActionHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        clearThreads();
                        mScrollView.fullScroll(View.FOCUS_UP);

                        if(mCurrentThreads != null) {
                            for (int i = 0; i < mCurrentThreads.categories.get(mCurrentCategory).threads.size(); i++) {
                                addThread(mCurrentThreads.categories.get(mCurrentCategory).threads.get(i));
                            }
                        }
                    }
                }, 350);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return v;
    }

    public void clearThreads() {
        mCurrentThreadsContainer.removeAllViews();
    }

    public void addThread(final CurrentPostsParser.CurrentThreadsThread thread) {
        if (mCurrentThreadsContainer != null) {
            final View threadItem = getActivity().getLayoutInflater().inflate(R.layout.currentthread_container, mCurrentThreadsContainer, false);
            final Button forumButton = (Button)threadItem.findViewById(R.id.open_forum);
            final Button lastPageButton = (Button)threadItem.findViewById(R.id.thread_gotolast);

            ((TextView) threadItem.findViewById(R.id.threadtitle)).setText(thread.threadName);
            ((TextView)threadItem.findViewById(R.id.threadforum)).setText("Forum - " + thread.forumName);
            ((TextView)threadItem.findViewById(R.id.threadviews)).setText("Visningar: " + thread.views);
            ((TextView)threadItem.findViewById(R.id.threadreaders)).setText("L\u00E4sare: " + thread.readers);
            ((TextView)threadItem.findViewById(R.id.threadreplies)).setText("Svar: " + thread.replies);

            threadItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler delay = new Handler();

                    int replies_no_spaces = Integer.parseInt(thread.replies.replaceAll("\\s+", ""));
                    SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    int max_pages = Integer.parseInt(myPreferences.getString("MAX_POSTS_PER_PAGE", "12"));
                    double a = (double)replies_no_spaces / (double)max_pages;
                    final int pages = (int)Math.ceil(a);
                    final String threadId = thread.threadLink.substring(thread.threadLink.indexOf("/t") + 2);

                    /**
                     * Short 300ms delay so that the click-animation has time to finish
                     */
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle args = new Bundle();
                            args.putString("Name", thread.threadName);
                            args.putString("Id", threadId);
                            args.putString("Page", "0");
                            args.putInt("Num_Pages", Integer.valueOf(pages));

                            Intent intent = new Intent(getActivity(), ThreadActivity.class);
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    }, 300);
                }
            });

            forumButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler delay = new Handler();

                    /**
                     * Short 300ms delay so that the click-animation has time to finish
                     */
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle args = new Bundle();
                            args.putString("Name", thread.forumName);
                            args.putString("Url", thread.forumLink);

                            Intent intent = new Intent(getActivity(), ThreadsActivity.class);
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    }, 300);
                }
            });

            lastPageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Handler delay = new Handler();

                    int replies_no_spaces = Integer.parseInt(thread.replies.replaceAll("\\s+", ""));
                    SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
                    int max_pages = Integer.parseInt(myPreferences.getString("MAX_POSTS_PER_PAGE", "12"));
                    double a = (double)replies_no_spaces / (double)max_pages;
                    final int pages = (int)Math.ceil(a);
                    final String threadId = thread.threadLink.substring(thread.threadLink.indexOf("/t") + 2);

                    /**
                     * Short 300ms delay so that the click-animation has time to finish
                     */
                    delay.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Bundle args = new Bundle();
                            args.putString("Name", thread.threadName);
                            args.putString("Id", threadId);
                            args.putString("Page", String.valueOf(pages));
                            args.putInt("Num_Pages", Integer.valueOf(pages));

                            Intent intent = new Intent(getActivity(), ThreadActivity.class);
                            intent.putExtras(args);
                            startActivity(intent);
                        }
                    }, 300);
                }
            });

            mCurrentThreadsContainer.addView(threadItem);
        }
    }

}
