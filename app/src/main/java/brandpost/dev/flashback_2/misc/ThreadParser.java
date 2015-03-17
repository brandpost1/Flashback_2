package brandpost.dev.flashback_2.misc;

import android.content.Context;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeVisitor;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Viktor on 2014-07-03.
 */
public class ThreadParser extends BaseParser {

    /**
     * Data structure representing a ThreadPage
     */
    public class ThreadPage {

        public ThreadPage() {
            threadPosts = new ArrayList<Post>();
        }

        // General threaddata
        public int threadPageCount;
        public String threadId;
        public String threadTitle;
        public String threadPage;

        // The posts contained within the threadpage
        public List<Post> threadPosts = new ArrayList<Post>();
    }

    /**
     * Data structure representing a Post in a Thread
     */
    public class Post {

        // PostHeader variables
        public String postAuthor;
        public String postDateTime;
        public String postUserType;
        public String postUserPostCount;
        public String postUserRegistrationDate;
        public String postIndex;

    }


    // Private members
    private Context mContext;

    /**
     * Constructor
     */
    public ThreadParser() {}

    /**
     * Method used to parse a page in a thread
     *
     * @param threadDocument  The url in which the threadpage exists
     * @return threadpage if successful, else it returns null.
     */
    public ThreadPage parse(Document threadDocument) {
        if(threadDocument == null) return null;

        ThreadPage threadPage = new ThreadPage();

        /**
         * Clean the document a bit
         */
        threadDocument = new Cleaner(Whitelist.relaxed()
                .addAttributes("div", "class", "id", "style")
		        .addTags("div", "table", "tr", "td", "tbody", "a", "b", "strong", "br")
                .addAttributes("table", "class", "id", "style")
                .addAttributes("tr", "class", "id", "style")
                .addAttributes("td", "class", "id", "style"))
                .clean(threadDocument);
        threadDocument = Jsoup.parse(threadDocument.select("div[id=posts]").html());

		threadDocument.outputSettings(new Document.OutputSettings().prettyPrint(true).indentAmount(4));

        // Make sure the Document is not null
        if (threadDocument != null) {

            // Some initialization
            Post tempPost = null;

            // First get stuff we only need to get once. Like title, threadID, pagenumber, number of pages, etc..



            // Select all of the posts
            Elements posts = threadDocument.select("table[id^=post]");


            // Then we loop through all the found posts in the thread.
            for(Element post : posts) {
                tempPost = new Post();

	            tempPost.postAuthor = post.select("div[id^=postmenu] a").text();
	            //tempPost.postAuthorLink = post.select("div[id^=postmenu a").attr("abs:href");
	            tempPost.postUserType = post.select("table.post-user-title tbody tr td").first().text();
	            tempPost.postUserRegistrationDate = post.select("div.post-user-info div").first().text();
	            tempPost.postUserPostCount = post.select("div.post-user-info div").get(1).text();
	            tempPost.postIndex = post.select("td.thead.post-date strong").text();
	            tempPost.postDateTime = post.select("tbody tr td.thead.post-date").first().ownText();

	            Elements postMessage = post.select("div.post_message");

	            // Clean links
	            cleanLinks(postMessage);

	            // Surround quotes with {quote}{/quote}
	            postMessage.select("div.post-quote-holder").before("{quote}").after("{/quote}");

	            // Surround name of the quotee [newline]
	            postMessage.select("div.post-quote-holder table.p2-4 tbody tr td.alt2.post-quote strong").before("{quoter_name}").after("{/quoter_name}");

	            // Surround regular spoilers and spoilers within quotes
	            postMessage.select("div.post_message > div > div.alt2.post-bbcode-spoiler").before("{spoilertext}").after("{/spoilertext}");

	            // Remove quote header ("Citat:")
	            postMessage.select("div.post-quote-holder > div.smallfont.post-quote-title").remove();

	            // Remove spoiler header ("Spoiler:")
	            postMessage.select("div[style=margin:5px 20px 20px 20px] div.smallfont").remove();


	            // Replace text-styling tags
	            postMessage.select("b").before("[b]").after("[/b]");
	            postMessage.select("i").before("[i]").after("[/i]");
	            postMessage.select("u").before("[u]").after("[/u]");

				// String representation of Post
	            String postAsString = postMessage.toString();

	            // Custom newline tag
	            postAsString = postAsString.replaceAll("(?i)<br[^>]*> *", "[newline]");

	            Element postElement = Jsoup.parse(postAsString);

	            // Remove < and >. Also happens to remove their tags when done like this.
	            postAsString = postElement.text().replace("<", "&lt;").replace(">", "&gt;");

	            Document doc = Jsoup.parseBodyFragment(postAsString);
	            postAsString = doc.outerHtml();

	            postAsString = postAsString.replace("{post}", "<post>").replace("{/post}", "</post>");
	            postAsString = postAsString.replace("{anon_quote}", "<anon_quote>").replace("{/anon_quote}", "</anon_quote>");
	            postAsString = postAsString.replace("{quoter_name}", "<quoter_name>").replace("{/quoter_name}", "</quoter_name>");
	            postAsString = postAsString.replace("{quote}", "<quote>").replace("{/quote}", "</quote>");
	            postAsString = postAsString.replace("{quote_text}", "<quote_text>").replace("{/quote_text}", "</quote_text>");
	            postAsString = postAsString.replace("{spoilertext}", "<spoiler>").replace("{/spoilertext}", "</spoiler>");
	            postAsString = postAsString.replace("{quotespoilertext}", "<quotespoiler>").replace("{/quotespoilertext}", "</quotespoiler>");
	            postAsString = postAsString.replace("{phptag}", "<phptag>").replace("{/phptag}", "</phptag>");
	            postAsString = postAsString.replace("{codetag}", "<codetag>").replace("{/codetag}", "</codetag>");

	            // Re-parse the string representation of the post
	            Document cleanedPost = Jsoup.parseBodyFragment(postAsString);
	            cleanedPost.outputSettings(new Document.OutputSettings().indentAmount(4).prettyPrint(true));

	            cleanedPost.traverse(new NodeVisitor() {
		            @Override
		            public void head(Node node, int depth) {

		            }

		            @Override
		            public void tail(Node node, int depth) {

		            }
	            });

                threadPage.threadPosts.add(tempPost);
            }

        } else {
            return null;
        }

        return threadPage;
    }

	private void cleanLinks(Elements message) {
		for (int i = 0; i < message.select("td.alt1.post-right div.post_message a").size(); i++) {
			String text = message.select("td.alt1.post-right div.post_message a").get(i).attr("href");
			if (!text.contains("flashback.org") && text.startsWith("/leave.php") && !(text.length() < 14)) {
				try {
					text = text.substring(13);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					text = "[Trasig länk]";
				}
			}

			try {
				text = URLDecoder.decode(text, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new AssertionError("UTF-8 is unknown");
			}

			message.select("td.alt1.post-right div.post_message a").get(i).text(text);
		}
	}
}


