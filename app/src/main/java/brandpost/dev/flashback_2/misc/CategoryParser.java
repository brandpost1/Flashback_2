package brandpost.dev.flashback_2.misc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Viktor on 2014-12-10.
 */
public class CategoryParser extends BaseParser {

    /**
     * A category has a name and contains a list of forums
     *
     * Static so it can be serialized since it's an inner class
     * */
    public static class Category implements Serializable{
        public Category() {
            mForums = new ArrayList<Forum>();
        }
        public String mName;
        public String mUrl;
        public ArrayList<Forum> mForums;
    }

    /**
     * Forum datastructure as viewed from the 'outside'
     * Threads contained within the forum are not present here,
     * but subforums can be obtained without opening the forum itself.
     *
     * Static so it can be serialized since it's an inner class
     * */
    public static class Forum implements Serializable {
        public Forum() {
            mSubforums = new ArrayList<Forum>();
        }
        // Basic info
        public String mTitle;
        public String mUrl;
        public String mId;
        public String mInfo;

        // Not necessarily always present
        public ArrayList<Forum> mSubforums;

        // Extra
        public String mLastActiveThreadName;
        public String mLastActiveThreadUrl;
        public String mLastActiveUser;
        public String mLastActiveUserUrl;
    }

    /**
     * Method used to parse the contents of a Category
     * For example the 'Vetenskap & humanoira' category located at https://www.flashback.org/f102
     * @param document
     * @return Category object with all forums their subforums
     */
    @Override
    public Category parse(Document document) {
        // Get top level forums
        Elements forums = document.select("tr td.alt1Active");
        Category category = new Category();

        category.mName = document.select("table.list-forum-title td.tcat").text().trim();
        category.mUrl = document.baseUri();

        // Loop top level forums
        for(Element e : forums) {
            Forum forum = new Forum();
            forum.mTitle    = e.select("a").text();
            forum.mId       = e.attr("id");
            forum.mUrl      = e.select("a[href]").attr("abs:href");
            forum.mInfo     = e.select("div.forum-summary").text().trim();

            // If subforum
            if(e.hasClass("td_subforum")) {
                // Add to previous forum in the Category item
                category.mForums
                        .get(category.mForums.size()-1)
                        .mSubforums.add(forum);
            } else {
                category.mForums
                        .add(forum);
            }

        }
        return category;
    }
}
