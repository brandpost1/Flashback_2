package brandpost.dev.flashback_2.misc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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

        public String postData;

        public Post() {}

        public void addPostAsString(String postAsString) {
            postData = postAsString;
            postData.replace("<body>", "<body author=\"" + postAuthor +"\" time=\"" + postDateTime + "\" usertype=\"" + postUserType + "\" postcount=\"" + postUserPostCount + "\" regdate=\"" + postUserRegistrationDate + "\" postindex=\"" + postIndex + "\" >");
        }
    }

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

                // Remove link in quote header
                postMessage.select("div.post-quote-holder table.p2-4 tbody tr td.alt2.post-quote a").remove();

	            // Surround regular spoilers and spoilers within quotes
	            postMessage.select("div.alt2.post-bbcode-spoiler").before("{spoilertext}").after("{/spoilertext}");

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

                postAsString = postAsString.substring(postAsString.indexOf("<body>"), postAsString.indexOf("</body>") + 7);
                postAsString = postAsString.replace("[newline]", "\n \n");
                postAsString = postAsString.replace("Ursprungligen postat av", "");
                postAsString = postAsString.replace("&nbsp;", " ");
                postAsString.trim();

	            // Re-parse the string representation of the post
	            //Document cleanedPost = Jsoup.parseBodyFragment(postAsString);

                tempPost.addPostAsString(postAsString);

                threadPage.threadPosts.add(tempPost);
            }

        } else {
            return null;
        }

        return threadPage;
    }

	private void cleanLinks(Elements message) {
        int numlinks = message.select("a").size();
		for (int i = 0; i < numlinks; i++) {
			String text = message.select("a").get(i).attr("href");
            // https://www.flashback.org/leave.php?u=http://www.idg.se/2.1085/1.114400/operatrerna-blockerar-pirate-bay
            // Rewrites to
            // http://www.idg.se/2.1085/1.114400/operatrerna-blockerar-pirate-bay

			if (text.startsWith("https://www.flashback.org/leave.php?u=")) {
				try {
					text = text.substring(38);
				} catch (IndexOutOfBoundsException e) {
					e.printStackTrace();
					text = "[[APP] - Fel vid länkomskrivning]";
				}
			}

			try {
				text = URLDecoder.decode(text, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new AssertionError("UTF-8 is unknown");
			}
            text = text.replace("&amp;", "{amp}");

			message.select("a").get(i).text(text);
		}
	}
}


