package brandpost.dev.flashback_2.misc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Viktor on 2014-12-10.
 */
public class ForumParser extends BaseParser<ForumParser.ForumPage>{

	public static final int STATUS_OPEN = 0;
	public static final int STATUS_LOCKED = 1;
	public static final int STATUS_MOVED = 2;

	public static class ForumPage implements Serializable {
		public int mNumPages = 0;
        public ArrayList<Thread> mThreads = new ArrayList<>();
    }

    /**
     * A thread in the context of its forumparent.
     * Here it's just an item in a list, not a list in it self which contains posts.
     */
    public static class Thread implements Serializable {
        // Basic info
        public String mTitle;
        public String mAuthor;
	    public String mUrl;
        public String mNumPages;
        public String mNumReplies;
        public String mNumViews;
	    public boolean mSticky;

        // Activity
        public String mLastActivity;

        // Locked / Moved / Open
        public int mStatus;

	    // Other
	    public boolean mBoldTitle;
    }

    /**
     * Method used to parse a forum for its content.
     * @param document Document to parse.
     * @return A datastructure containing all the threads in the specified forum,
     * or null if parsing could not be completed.
     */
    @Override
    public ForumPage parse(Document document) {

	    ForumPage page = new ForumPage();
	    page.mNumPages = getForumNumPages(document);

	    Elements stickyThreads = document.select("table#threadslist tr.tr_sticky");
	    Elements forumThreads = document.select("table#threadslist tr:not(:has(td.alt1.threadslist-announcement)):has(td.alt1.td_status):not(.tr_sticky)");

		for(Element stickythread : stickyThreads) {
			Thread thread = new Thread();
			thread.mSticky = true;

			thread.mTitle = stickythread.select("td.alt1.td_title div strong a[id^=thread_title]").text();
			if(thread.mTitle.isEmpty()) {
				// Not bold
				thread.mTitle = stickythread.select("td.alt1.td_title div a[id^=thread_title]").text();
				thread.mBoldTitle = false;
			} else {
				thread.mBoldTitle = true;
			}
			thread.mTitle = "Viktigt: " + thread.mTitle;

					thread.mAuthor = stickythread.select("td.alt1.td_title span[onclick^=window.open('/u]").text();
			thread.mUrl = stickythread.select("td.alt1.td_title a[id^=thread_title]").attr("abs:href");
			thread.mNumViews = stickythread.select("td.alt2.td_views").text();
			thread.mNumReplies = stickythread.select("td.alt1.td_replies").text();
			thread.mNumPages = stickythread.select("td.alt1.td_title a.thread-pagenav-lastpage").text();
			thread.mLastActivity = stickythread.select("td.alt2.td_last_post").text();

			boolean isLocked = !stickythread.select("td.alt1.td_status a.clear.icon-thread-lock").isEmpty();
			boolean isMoved = !stickythread.select("td.alt1.td_status a.clear.icon-thread-moved").isEmpty();

			if(!isLocked && !isMoved) {
				thread.mStatus = STATUS_OPEN;
			} else {
				if(isLocked) {
					thread.mStatus = STATUS_LOCKED;
				}
				if(isMoved) {
					thread.mStatus = STATUS_MOVED;
				}
			}

			page.mThreads.add(thread);

		}

	    for(Element regularthread : forumThreads) {
		    Thread thread = new Thread();
		    thread.mSticky = true;

		    thread.mTitle = regularthread.select("td.alt1.td_title div strong a[id^=thread_title]").text();
		    if(thread.mTitle.isEmpty()) {
			    // Not bold
			    thread.mTitle = regularthread.select("td.alt1.td_title div a[id^=thread_title]").text();
			    thread.mBoldTitle = false;
		    } else {
			    thread.mBoldTitle = true;
		    }

		    thread.mAuthor = regularthread.select("td.alt1.td_title span[onclick^=window.open('/u]").text();
		    thread.mUrl = regularthread.select("td.alt1.td_title a[id^=thread_title]").attr("abs:href");
		    thread.mNumViews = regularthread.select("td.alt2.td_views").text();
		    thread.mNumReplies = regularthread.select("td.alt1.td_replies").text();
		    thread.mNumPages = regularthread.select("td.alt1.td_title a.thread-pagenav-lastpage").text();
		    thread.mLastActivity = regularthread.select("td.alt2.td_last_post").text();

		    boolean isLocked = !regularthread.select("td.alt1.td_status a.clear.icon-thread-lock").isEmpty();
		    boolean isMoved = !regularthread.select("td.alt1.td_status a.clear.icon-thread-moved").isEmpty();

		    if(!isLocked && !isMoved) {
			    thread.mStatus = STATUS_OPEN;
		    } else {
			    if(isLocked) {
				    thread.mStatus = STATUS_LOCKED;
			    }
			    if(isMoved) {
				    thread.mStatus = STATUS_MOVED;
			    }
		    }

		    page.mThreads.add(thread);

	    }

        return page;
    }

	/**
	 * Returns the number of pages you can scroll within a forum
	 * @param document the document in which to retrieve the pages
	 * @return The number of pages, or -1
	 */
	private int getForumNumPages(Document document) {

		// Default number of pages
		int numPages = -1;
		String pages = "";

		try {
			pages = document.select("td.vbmenu_control.smallfont2.delim").first().text();
			if(pages.length() > 0 && pages.contains(" ")) {
				numPages = Integer.parseInt(pages.split(" ")[3]);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		return numPages;
	}
}
