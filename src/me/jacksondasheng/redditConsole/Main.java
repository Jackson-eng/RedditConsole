package me.jacksondasheng.redditConsole;

import java.io.IOException;
import java.util.Scanner;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Main
{
    public static String lineOfPost = null, subreddit = "";

    public static void main(String[] args) throws IOException
    {
        String link = getLink();

        try
        {
            Document doc = Jsoup.connect(link).get();

            System.out.println("\n" + doc.title());

            for(String line : doc.getAllElements().toString().split("\n"))
            {
                if(line.contains("<script id=\"data\">"))
                {
                    lineOfPost = line;

                    break;
                }
            }

            JSONObject json = new JSONObject(lineOfPost.substring(34));
            json = json.getJSONObject("widgets").getJSONObject("models").getJSONObject(json.getJSONObject("widgets").getJSONObject("idCardIds").getString(json.getJSONObject("publicAccessNetwork").getJSONObject("api").getJSONObject("config").getJSONObject("subreddits").getJSONObject("r/" + subreddit).getString("id")));

            System.out.print("    About Community\n" + "        ");

            if(json.getString("subscribersText").equals(""))
            {
                System.out.print("Members: " + json.getInt("subscribersCount") + "\n        ");
            }
            else
            {
                System.out.print(json.getString("subscribersText") + ": " + json.getInt("subscribersCount") + "\n        ");
            }

            if(json.getString("currentlyViewingText").equals(""))
            {
                System.out.print("Online: " + json.getInt("currentlyViewingCount") + "\n");
            }
            else
            {
                System.out.print(json.getString("currentlyViewingText") + ": " + json.getInt("currentlyViewingCount") + "\n");
            }

            System.out.println();

            String[] post = lineOfPost.split(",\"permalink\"");

            for(int i = 0; i < post.length; i++)
            {
                post[i] = "\"permalink\"" + post[i];
            }

            post(post);
        }
        catch(Exception exc)
        {
            System.out.println("Sorry, page \"" + link + "\" not found, make sure you don't have a typo");
        }
    }

    public static String getLink()
    {
        String link = "https://www.reddit.com/r/";

        System.out.println("Format your input like this \"<subreddit> <sort (optional)>\"");

        while(true)
        {
            String[] input = new Scanner(System.in).nextLine().toLowerCase().split(" ");

            if(input[0].length() == 0)
            {
                System.out.println("You did not input a subreddit, please try again");
            }
            else
            {
                link += input[0] + "/";
                subreddit = input[0].toLowerCase();

                if(input.length > 1)
                {
                    link += input[1];
                }

                return link;
            }

            return link;
        }
    }

    public static void post(String[] post)
    {
        for(String line : post)
        {
            line = "{" + line + "}";

            for(int i = 0; i < line.length() - 4; i++)
            {
                if(line.substring(i, i + 5).equals("{\"id\""))
                {
                    line = line.substring(i);

                    break;
                }
            }

            line = line.replace(";</script>", "");

            JSONObject json = new JSONObject(line);

            try
            {
                if(!json.getBoolean("isSponsored") && !json.getBoolean("isBlank"))
                {
                    System.out.println("    " + json.getString("title") + "\n        Author: " + json.getString("author") + "\n        Score: " + json.getInt("score") + "\n        Comments: " + json.getInt("numComments"));

                    if(json.getJSONArray("flair").length() != 0)
                    {
                        System.out.println("        Flair: " + json.getJSONArray("flair").getJSONObject(0).getJSONArray("richtext").getJSONObject(0).getString("t"));
                    }

                    if(json.getBoolean("isArchived"))
                    {
                        System.out.println("        Archived");
                    }

                    System.out.println();
                }
            }
            catch(Exception exc)
            {}
        }
    }
}
