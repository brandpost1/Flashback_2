package brandpost.dev.flashback_2.misc;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Viktor on 2015-06-26.
 */
public class CurrentPostsParser extends BaseParser<CurrentPostsParser.CurrentThreads> {

    public class CurrentThreads {
        public CurrentThreads() {
            categories = new ArrayList<>();
        }
        public ArrayList<Category> categories;
    }

    public class CurrentThreadsThread {
        public String threadLink = "";
        public String threadName = "";
        public String forumLink = "";
        public String forumName = "";
        public String readers = "";
        public String replies = "";
        public String views = "";
    }

    public class Category {
        public String name;
        public ArrayList<CurrentThreadsThread> threads;

        public Category() {
            threads = new ArrayList<>();
        }
    }

    @Override
    public CurrentThreads parse(Document document) {

        CurrentThreads currentThreads = new CurrentThreads();

        // Get categories
        Elements categories = document.select("table.tborder.threadslist tbody");

        for (int i = 0; i < categories.size(); i++) {
            Category category = new Category();
            category.name = categories.get(i).select("tr:eq(0) td:eq(1)").text();

            Elements threads = categories.get(i).select("tr:not(:first-child)");
            for(int j = 0; j < threads.size(); j++) {
                CurrentThreadsThread thread = new CurrentThreadsThread();

                thread.threadLink = categories.get(i).select("tr:not(:first-child) td:eq(1) a:first-child").get(j).attr("abs:href");
                thread.threadName = categories.get(i).select("tr:not(:first-child) td:eq(1) a:first-child").get(j).text();
                thread.forumName = categories.get(i).select("tr:not(:first-child) td:eq(1) a:eq(1)").get(j).text();
                thread.readers = categories.get(i).select("tr:not(:first-child) td:eq(3)").get(j).text();
                thread.views  = categories.get(i).select("tr:not(:first-child) td:eq(4)").get(j).text();
                thread.replies = categories.get(i).select("tr:not(:first-child) td:eq(5)").get(j).text();
                thread.forumLink = categories.get(i).select("tr:not(:first-child) td:eq(1) a:eq(1)").get(j).attr("abs:href");

                category.threads.add(thread);
            }
            currentThreads.categories.add(category);
        }

        return currentThreads;
    }
}
